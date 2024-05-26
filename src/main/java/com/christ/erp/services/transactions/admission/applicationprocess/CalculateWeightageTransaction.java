package com.christ.erp.services.transactions.admission.applicationprocess;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmWeightageDefinitionDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public class CalculateWeightageTransaction {

    @Autowired
    private Mutiny.SessionFactory sessionFactory;

    public List<StudentApplnEntriesDBO> getStudentApplnEntries(int sessionId, List<Integer> erpCampusProgrammeMappingIds) {
        String str = "select dbo from StudentApplnEntriesDBO dbo "
                + " inner join fetch dbo.studentApplnSelectionProcessDatesDBOS processDatesDBOS"
                + " inner join fetch dbo.studentEducationalDetailsDBOS educationalDetailsDBOS"
                + " inner join fetch dbo.admSelectionProcessDBOS selectionProcessDBOS"
//                + " inner join fetch dbo.studentApplnPrerequisiteDBO prerequisiteDBO"
                + " where dbo.recordStatus = 'A' and processDatesDBOS.recordStatus = 'A' "
                + " and processDatesDBOS.admSelectionProcessPlanDetailDBO.recordStatus = 'A' and processDatesDBOS.admSelectionProcessPlanDetailDBO.admSelectionProcessPlanDBO.recordStatus = 'A' "
                + " and dbo.erpCampusProgrammeMappingDBO.recordStatus = 'A' "
                + " and processDatesDBOS.admSelectionProcessPlanDetailDBO.admSelectionProcessPlanDBO.id=:sessionId";
        if(!Utils.isNullOrEmpty(erpCampusProgrammeMappingIds))
            str += " and dbo.erpCampusProgrammeMappingDBO.id in (:erpCampusProgrammeMappingIds)";
        String qry = str;
        if(!Utils.isNullOrEmpty(erpCampusProgrammeMappingIds))
            return sessionFactory.withSession(session->session.createQuery(qry, StudentApplnEntriesDBO.class)
                .setParameter("sessionId", sessionId).setParameter("erpCampusProgrammeMappingIds",erpCampusProgrammeMappingIds).getResultList()).await().indefinitely();
        else
            return sessionFactory.withSession(session->session.createQuery(qry, StudentApplnEntriesDBO.class)
                .setParameter("sessionId", sessionId).getResultList()).await().indefinitely();
    }

    public List<Integer> getErpCampusProgrammeMappingIds(int sessionId) {
        String  str = "select mappingDBO.id from AdmSelectionProcessPlanDBO dbo "
                + " inner join dbo.admSelectionProcessPlanProgrammeDBOs processPlanProgrammeDBOs"
                + " inner join processPlanProgrammeDBOs.erpCampusProgrammeMappingDBO mappingDBO"
                + " where dbo.recordStatus = 'A' and processPlanProgrammeDBOs.recordStatus = 'A' "
                + " and mappingDBO.recordStatus = 'A' and dbo.id=:sessionId";
        return sessionFactory.withSession(session->session.createQuery(str, Integer.class)
                .setParameter("sessionId",sessionId).getResultList()).await().indefinitely();
    }

    public List<AdmWeightageDefinitionDBO> getAdmWeightageDefinitionDBOS(List<Integer> erpCampusProgrammeMappingIds, int academicYearId) {
        String  str = "select definitionDBO from AdmWeightageDefinitionDBO definitionDBO "
                + " inner join fetch definitionDBO.admWeightageDefinitionDetailDBOsSet definitionDetailDBOs"
                + " inner join fetch definitionDBO.admWeightageDefinitionGeneralDBOsSet definitionGeneralDBOs"
                + " inner join fetch definitionDBO.admWeightageDefinitionLocationCampusDBOsSet locationCampusDBOs"
                + " where definitionDBO.recordStatus = 'A' and locationCampusDBOs.recordStatus = 'A' "
                + " and definitionDetailDBOs.recordStatus = 'A' and definitionGeneralDBOs.recordStatus = 'A' and locationCampusDBOs.erpCampusProgrammeMappingDBO.recordStatus = 'A' "
                + " and definitionDBO.erpAcademicYearDBO.id=:academicYearId and locationCampusDBOs.erpCampusProgrammeMappingDBO.id in (:erpCampusProgrammeMappingIds)";
        return sessionFactory.withSession(session->session.createQuery(str, AdmWeightageDefinitionDBO.class)
            .setParameter("erpCampusProgrammeMappingIds",erpCampusProgrammeMappingIds).setParameter("academicYearId", academicYearId).getResultList()).await().indefinitely();
    }

    public void saveWeightageScore(StudentApplnEntriesDBO studentApplnEntriesDBO) {
        sessionFactory.withTransaction((session, tx) ->  session.merge(studentApplnEntriesDBO)).subscribeAsCompletionStage();
    }

    public void saveWeightageScore(Set<StudentApplnEntriesDBO> studentApplnEntriesDBOSet) {
        sessionFactory.withTransaction((session, tx) ->  session.mergeAll(studentApplnEntriesDBOSet.toArray())).subscribeAsCompletionStage();
    }
}
