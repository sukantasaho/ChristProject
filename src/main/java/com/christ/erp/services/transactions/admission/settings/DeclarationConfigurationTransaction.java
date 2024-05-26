package com.christ.erp.services.transactions.admission.settings;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.settings.StudentApplnDeclarationsDBO;
import com.christ.erp.services.dto.admission.settings.StudentApplnDeclarationsDTO;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DeclarationConfigurationTransaction {

    @Autowired
    private Mutiny.SessionFactory sessionFactory;

    public Mono<List<StudentApplnDeclarationsDBO>> getGridData(Integer yearId) {
        String str = "select distinct dbo from StudentApplnDeclarationsDBO dbo " +
                " left join fetch dbo.erpAcademicYearDBO as yearId" +
                " left join fetch dbo.erpCampusProgrammeMappingDBO" +
                " left join fetch dbo.studentApplnDeclarationsDetailsDBOSet as detail" +
                " left join fetch detail.studentApplnDeclarationsTemplateDBO " +
                " where dbo.recordStatus = 'A' and yearId.id = :yearId";
        return Mono.fromFuture(sessionFactory.withSession(s-> s.createQuery(str, StudentApplnDeclarationsDBO.class).setParameter("yearId",yearId).getResultList()).subscribeAsCompletionStage());
    }

    public StudentApplnDeclarationsDBO edit(Integer id) {
        String str = "select distinct dbo from StudentApplnDeclarationsDBO dbo " +
                " left join fetch dbo.erpAcademicYearDBO as yearId" +
                " left join fetch dbo.erpCampusProgrammeMappingDBO" +
                " left join fetch dbo.studentApplnDeclarationsDetailsDBOSet as detail" +
                " left join fetch detail.studentApplnDeclarationsTemplateDBO " +
                " where dbo.recordStatus = 'A' and dbo.id =:id";
        return sessionFactory.withSession(s-> s.createQuery(str, StudentApplnDeclarationsDBO.class).setParameter("id",id).getSingleResultOrNull()).await().indefinitely();
    }

    public Mono<Boolean> delete(Integer id, Integer userId) {
        sessionFactory.withTransaction((session, tx) -> session.find(StudentApplnDeclarationsDBO.class, id)
                .chain(dbo -> session.fetch(dbo.getStudentApplnDeclarationsDetailsDBOSet())
                .invoke(subSet -> {
                    subSet.forEach(subDbo -> {
                        subDbo.setRecordStatus('D');
                        subDbo.setModifiedUsersId(userId);
                    });
                    dbo.setRecordStatus('D');
                    dbo.setModifiedUsersId(userId);
                }))).await().indefinitely();
        return Mono.just(Boolean.TRUE);
    }

    public List<StudentApplnDeclarationsDBO> duplicateCheck(StudentApplnDeclarationsDTO data){
//        String str= "select concat(dbo.erpCampusProgrammeMappingDBO.erpProgrammeDBO.programmeName,'(',dbo.erpCampusProgrammeMappingDBO.erpCampusDBO.campusName,')') from StudentApplnDeclarationsDBO dbo " +
//                " where dbo.recordStatus ='A' and dbo.erpAcademicYearDBO.id =: yearId and dbo.erpCampusProgrammeMappingDBO.id in (:campusProgrammeMappingList)";
        String str= "select dbo from StudentApplnDeclarationsDBO dbo " +
                " left join fetch dbo.erpCampusProgrammeMappingDBO ecpm" +
                " where dbo.recordStatus ='A' and dbo.erpAcademicYearDBO.id =: yearId and dbo.erpCampusProgrammeMappingDBO.id in (:campusProgrammeMappingList)";
       var programmeCampusName = sessionFactory.withSession(s -> {
           Mutiny.Query<StudentApplnDeclarationsDBO> query = s.createQuery(str, StudentApplnDeclarationsDBO.class);
            if(!Utils.isNullOrEmpty(data.getErpAcademicYearDTO()) && !Utils.isNullOrEmpty(data.getErpAcademicYearDTO().getValue())) {
                query.setParameter("yearId", Integer.valueOf(data.getErpAcademicYearDTO().getValue()));
            }
            if(!Utils.isNullOrEmpty(data.getCampusProgrammeList())){
                query.setParameter("campusProgrammeMappingList", data.getCampusProgrammeList().stream().map(p->Integer.parseInt(p.getValue())).collect(Collectors.toList()));
            }
            return  query.getResultList();
       }).await().indefinitely();
       return programmeCampusName;
    }

    public void update(List<StudentApplnDeclarationsDBO> dboList) {
        sessionFactory.withTransaction((session, txt) -> session.mergeAll(dboList.toArray())).subscribeAsCompletionStage();
    }
}
