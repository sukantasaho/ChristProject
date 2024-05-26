package com.christ.erp.services.transactions.common;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpDataCollectionTemplateDBO;
import com.christ.erp.services.dbobjects.common.ErpDataCollectionTemplateQuestionsDBO;
import com.christ.erp.services.dbobjects.common.ErpDataCollectionTemplateQuestionsOptionsDBO;
import com.christ.erp.services.dto.common.ErpDataCollectionTemplateDTO;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class DataCollectionTransaction {

    @Autowired
    private Mutiny.SessionFactory sessionFactory;

    public Mono<List<ErpDataCollectionTemplateDBO>> getGridData() {
        Mono<List<ErpDataCollectionTemplateDBO>> list = Mono.fromFuture(sessionFactory.withSession(s -> {
            Mutiny.Query<ErpDataCollectionTemplateDBO> query = s.createQuery("select dbo from ErpDataCollectionTemplateDBO dbo where dbo.recordStatus ='A' ", ErpDataCollectionTemplateDBO.class);
            return  query.getResultList();
        }).subscribeAsCompletionStage());
        return list;
    }

    public boolean duplicateCheck(ErpDataCollectionTemplateDTO erpDataCollectionTemplateDTO) {
        List<ErpDataCollectionTemplateDBO> list = sessionFactory.withSession(s -> {
            String str = " from ErpDataCollectionTemplateDBO dbo where dbo.recordStatus='A' and dbo.templateName=:templateName";
            if(!Utils.isNullOrEmpty(erpDataCollectionTemplateDTO.getId())){
                str += " and dbo.id not in (:id)";
            }
            Mutiny.Query<ErpDataCollectionTemplateDBO> query = s.createQuery(str, ErpDataCollectionTemplateDBO.class);
            if(!Utils.isNullOrEmpty(erpDataCollectionTemplateDTO.getId())){
                query.setParameter("id", erpDataCollectionTemplateDTO.getId());
            }
            query.setParameter("templateName", erpDataCollectionTemplateDTO.getTemplateName());
            return  query.getResultList();
        }).await().indefinitely();
        return Utils.isNullOrEmpty(list) ? false : true;
    }

    public void update(ErpDataCollectionTemplateDBO dbo) {
        sessionFactory.withTransaction((session, tx) -> session.find(ErpDataCollectionTemplateDBO.class, dbo.getId()).call(() -> session.merge(dbo))).await().indefinitely();
    }

    public void save(ErpDataCollectionTemplateDBO dbo) {
        sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
    }

    public Mono<ErpDataCollectionTemplateDBO> edit(int id) {
        String str = "select dbo from ErpDataCollectionTemplateDBO dbo " +
                " left join fetch dbo.erpDataCollectionTemplateSectionDBOS as templateSectionDBOS " +
                " left join fetch templateSectionDBOS.erpDataCollectionTemplateQuestionsDBOS as templateSectionQuestionDBOS " +
                " left join fetch templateSectionQuestionDBOS.erpDataCollectionTemplateQuestionsOptionsDBOS as templateSectionQuestionOptionsDBOS " +
                " where dbo.recordStatus = 'A' and dbo.id= :id";
        return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, ErpDataCollectionTemplateDBO.class).setParameter("id", id).getSingleResultOrNull()).subscribeAsCompletionStage());
    }

    public ErpDataCollectionTemplateDBO getTemplateDBO(int id) {
        String str = "select dbo from ErpDataCollectionTemplateDBO dbo " +
                " left join fetch dbo.erpDataCollectionTemplateSectionDBOS as templateSectionDBOS " +
                " left join fetch templateSectionDBOS.erpDataCollectionTemplateQuestionsDBOS as templateSectionQuestionDBOS " +
                " left join fetch templateSectionQuestionDBOS.erpDataCollectionTemplateQuestionsOptionsDBOS as templateSectionQuestionOptionsDBOS " +
                " where dbo.recordStatus = 'A' and dbo.id= :id";
        return sessionFactory.withSession(s->s.createQuery(str, ErpDataCollectionTemplateDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
    }

    public Mono<Boolean> delete(int id, Integer userId) {
        sessionFactory.withTransaction((session, tx) -> session.createQuery( "select dbo from ErpDataCollectionTemplateDBO dbo " +
            " left join fetch dbo.erpDataCollectionTemplateSectionDBOS as templateSectionDBOS " +
            " left join fetch templateSectionDBOS.erpDataCollectionTemplateQuestionsDBOS as templateSectionQuestionDBOS " +
            " left join fetch templateSectionQuestionDBOS.erpDataCollectionTemplateQuestionsOptionsDBOS as templateSectionQuestionOptionsDBOS " +
            " where dbo.id=:id", ErpDataCollectionTemplateDBO.class).setParameter("id", id).getSingleResultOrNull()
            .chain(dbo -> session.fetch(dbo.getErpDataCollectionTemplateSectionDBOS())
                .invoke(erpDataCollectionTemplateSectionDBOS -> {
                    erpDataCollectionTemplateSectionDBOS.forEach(erpDataCollectionTemplateSectionDBO->{
                        erpDataCollectionTemplateSectionDBO.setRecordStatus('D');
                        erpDataCollectionTemplateSectionDBO.setModifiedUsersId(userId);
                        Set<ErpDataCollectionTemplateQuestionsDBO> erpDataCollectionTemplateQuestionsDBOS = erpDataCollectionTemplateSectionDBO.getErpDataCollectionTemplateQuestionsDBOS().stream().map(erpDataCollectionTemplateQuestionsDBO->{
                            erpDataCollectionTemplateQuestionsDBO.setRecordStatus('D');
                            erpDataCollectionTemplateQuestionsDBO.setModifiedUsersId(userId);
                            Set<ErpDataCollectionTemplateQuestionsOptionsDBO> erpDataCollectionTemplateQuestionsOptionsDBOS = erpDataCollectionTemplateQuestionsDBO.getErpDataCollectionTemplateQuestionsOptionsDBOS().stream().map(erpDataCollectionTemplateQuestionsOptionsDBO->{
                                erpDataCollectionTemplateQuestionsOptionsDBO.setRecordStatus('D');
                                erpDataCollectionTemplateQuestionsOptionsDBO.setModifiedUsersId(userId);
                                return erpDataCollectionTemplateQuestionsOptionsDBO;
                            }).collect(Collectors.toSet());
                            erpDataCollectionTemplateQuestionsDBO.setErpDataCollectionTemplateQuestionsOptionsDBOS(erpDataCollectionTemplateQuestionsOptionsDBOS);
                            return erpDataCollectionTemplateQuestionsDBO;
                        }).collect(Collectors.toSet());
                        erpDataCollectionTemplateSectionDBO.setErpDataCollectionTemplateQuestionsDBOS(erpDataCollectionTemplateQuestionsDBOS);
                    });
                    dbo.setRecordStatus('D');
                    dbo.setModifiedUsersId(userId);
                }))).await().indefinitely();
        return Mono.just(Boolean.TRUE);
    }
}
