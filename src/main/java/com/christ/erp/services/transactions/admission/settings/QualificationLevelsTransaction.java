package com.christ.erp.services.transactions.admission.settings;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.settings.AdmQualificationListDBO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmQualificationListDTO;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import javax.persistence.Tuple;
import java.util.List;

@Repository
public class QualificationLevelsTransaction {
    @Autowired
    private Mutiny.SessionFactory sessionFactory;


    public Mono<List<AdmQualificationListDBO>> getGridData() {
        String str = "select data from AdmQualificationListDBO data where data.recordStatus = 'A' order by qualification_order";
        return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(str, AdmQualificationListDBO.class).getResultList()).subscribeAsCompletionStage());
    }


    public void update(AdmQualificationListDBO dbo) {
        sessionFactory.withTransaction((session, tx) -> session.find(AdmQualificationListDBO.class, dbo.getId()).call(() -> session.merge(dbo))).await().indefinitely();
    }


    public void save(AdmQualificationListDBO dbo) {
        sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
    }

    public AdmQualificationListDBO getAdmQualificationDetails(int id) {
        return sessionFactory.withSession(s->s.find(AdmQualificationListDBO.class, id)).await().indefinitely();
    }

    public Mono<Boolean> delete(int id, Integer userId) {
        sessionFactory.withTransaction((session, tx) -> session.find(AdmQualificationListDBO.class, id).invoke(dbo -> {
            dbo.setModifiedUsersId(userId);
            dbo.setRecordStatus('D');
        })).await().indefinitely();
        return Mono.just(Boolean.TRUE);
    }

   public boolean duplicateCheck(AdmQualificationListDTO dto) {
        String str1 = " from AdmQualificationListDBO dbo where dbo.recordStatus = 'A'"+
                " and (dbo.qualificationName = :qualification_name or dbo.shortName= :short_name) ";
        if (!Utils.isNullOrEmpty(dto.getId())) {
            str1 += "and id != :id";
        }
        String finalStr = str1;
        List<AdmQualificationListDBO> list = sessionFactory.withSession(s -> {
            Mutiny.Query<AdmQualificationListDBO> query = s.createQuery(finalStr, AdmQualificationListDBO.class);
            if (!Utils.isNullOrEmpty(dto.getId())) {
                query.setParameter("id", dto.getId());
            }
            query.setParameter("qualification_name", dto.getQualificationName().trim());
            query.setParameter("short_name", dto.getShortName().trim());
            return query.getResultList();
        }).await().indefinitely();
        return Utils.isNullOrEmpty(list) ? false : true;
    }
    public boolean duplicateOrderCheck(AdmQualificationListDTO dto) {
        String str1 = " Select distinct adm_qualification_list.adm_qualification_list_id from adm_qualification_list" +
                " where  adm_qualification_list.record_status = 'A' and  adm_qualification_list.qualification_order = :order" +
                " and adm_qualification_list.is_additional_document = :addDoc ";
        if (!Utils.isNullOrEmpty(dto.getId())) {
            str1 += "and adm_qualification_list.adm_qualification_list_id != :id";
        }
        String finalStr = str1;
        List<Tuple> list = sessionFactory.withSession(s -> {
            Mutiny.Query<Tuple> query = s.createNativeQuery(finalStr, Tuple.class);
            if (!Utils.isNullOrEmpty(dto.getId())) {
                query.setParameter("id", dto.getId());
            }
            query.setParameter("order", dto.getQualificationOrder());
            query.setParameter("addDoc", dto.getIsAdditionalDocument());
            return query.getResultList();
        }).await().indefinitely();
        return Utils.isNullOrEmpty(list) ? false : true;
    }

}








