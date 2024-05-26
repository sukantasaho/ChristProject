package com.christ.erp.services.transactions.admission.applicationprocess;


import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import javax.persistence.Tuple;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;

@Repository
public class GenerateAdmitCardTransaction {

    @Autowired
    private Mutiny.SessionFactory sessionFactory;

//    public StudentApplnEntriesDBO getStudentApplnEntriesDBOByApplnNo(Integer applnNo) {
//        return sessionFactory.withSession(session->session.createQuery("select dbo from StudentApplnEntriesDBO dbo " +
//                "inner join fetch dbo.studentApplnSelectionProcessDatesDBOS processDatesDBO"+
//                " where dbo.recordStatus='A' and processDatesDBO.recordStatus='A'" +
//                " and dbo.applicationNo=:applnNo", StudentApplnEntriesDBO.class)
//                .setParameter("applnNo", applnNo).getSingleResultOrNull()).await().indefinitely();
//    }

    public AdmSelectionProcessPlanDBO getSelectionProcessPlanByProcessType(Integer sessionId, Integer processTypeId) {
        String str = "select distinct dbo from AdmSelectionProcessPlanDBO dbo "
                + " inner join fetch dbo.admSelectionProcessPlanDetailDBO detailDBO "
                + " left join fetch detailDBO.admSelectionProcessPlanDetailProgDBOs detailProgDBOs "
                + " left join fetch detailDBO.studentApplnSelectionProcessDatesDBOS processDatesDBOS "
                + " left join fetch detailDBO.admSelectionProcessPlanCenterBasedDBOs planCenterBasedDBOs "
                + " left join fetch planCenterBasedDBOs.admSelectionProcessVenueCityDBO venueCityDBO "
                + " left join fetch venueCityDBO.centerDetailsDBOs centerDetailsDBOs "
                + " where dbo.recordStatus = 'A' and detailDBO.recordStatus = 'A'  "
                + " and dbo.id=:sessionId and detailDBO.admSelectionProcessTypeDBO.id=:processTypeId";
        return sessionFactory.withSession(session->session.createQuery(str, AdmSelectionProcessPlanDBO.class)
            .setParameter("sessionId", sessionId).setParameter("processTypeId",processTypeId).getSingleResultOrNull()).await().indefinitely();
    }

    /*public List<AdmSelectionProcessDBO> getExistingGeneratedAdmitCard(int sessionId, int processTypeId) {
        String str = "from AdmSelectionProcessDBO dbo "
                + " inner join dbo.admSelectionProcessPlanDetailDBO detailDBO "
                + " where dbo.recordStatus = 'A' and detailDBO.recordStatus = 'A'  "
                + " and detailDBO.admSelectionProcessPlanDBO.id=:sessionId and detailDBO.admSelectionProcessTypeDBO.id=:processTypeId";
        return sessionFactory.withSession(session->session.createQuery(str, AdmSelectionProcessDBO.class)
                .setParameter("sessionId", sessionId).setParameter("processTypeId",processTypeId).getResultList()).await().indefinitely();
    }*/

    public List<AdmSelectionProcessDBO> getAdmSelectionProcessPlanDetailDBOSBySessionAndProcessType(int sessionId, int processTypeId) {
        String str = "select dbo from AdmSelectionProcessDBO dbo "
                + " where dbo.recordStatus = 'A' and dbo.admSelectionProcessPlanDetailDBO.recordStatus = 'A' and dbo.admSelectionProcessPlanDetailDBO.admSelectionProcessPlanDBO.recordStatus = 'A' "
                + " and dbo.admSelectionProcessPlanDetailDBO.admSelectionProcessTypeDBO.recordStatus = 'A' "
                + " and dbo.admSelectionProcessPlanDetailDBO.admSelectionProcessPlanDBO.id=:sessionId and dbo.admSelectionProcessPlanDetailDBO.admSelectionProcessTypeDBO.id=:processTypeId";
        return sessionFactory.withSession(session->session.createQuery(str, AdmSelectionProcessDBO.class)
                .setParameter("sessionId", sessionId).setParameter("processTypeId",processTypeId).getResultList()).await().indefinitely();
    }

    public List<StudentApplnEntriesDBO> getApplicantsDetails(List<Integer> applicationNos) {
        String query = " select distinct dbo from StudentApplnEntriesDBO dbo " +
            " left join fetch dbo.admSelectionProcessDBOS processDBOS "+
            " where dbo.recordStatus = 'A' and dbo.applicationNo in( :applicationNos )";
        return  sessionFactory.withSession(s->s.createQuery(query,StudentApplnEntriesDBO.class).setParameter("applicationNos", applicationNos).getResultList()).await().indefinitely();
    }

    public Mono<List<Tuple>> getGridData(Integer admissionYearId) {
        String str1 = "SELECT     " +
                "adm_selection_process_plan.adm_selection_process_plan_id    " +
                ",adm_selection_process_plan.selection_process_session as selectionProcessSession    " +
                ",adm_selection_process_plan.selection_process_start_date,adm_selection_process_plan.selection_process_end_date   " +
                ",count( student_appln_entries_id) as applicationReceived    " +
                ",admitCardGenerated    " +
                ",admitCardPublished    " +
                "FROM student_appln_selection_process_dates    " +
                "LEFT JOIN adm_selection_process_plan_center_based ON adm_selection_process_plan_center_based.adm_selection_process_plan_center_based_id = student_appln_selection_process_dates.adm_selection_process_plan_center_based_id    " +
                " AND adm_selection_process_plan_center_based.record_status='A'    " +
                "inner JOIN adm_selection_process_plan_detail ON   " +
                "adm_selection_process_plan_detail.adm_selection_process_plan_detail_id =   " +
                "ifnull(adm_selection_process_plan_center_based.adm_selection_process_plan_detail_id,  student_appln_selection_process_dates.adm_selection_process_plan_detail_id)  " +
                "AND adm_selection_process_plan_detail.record_status='A'  and adm_selection_process_plan_detail.process_order=1  " +
                "INNER JOIN adm_selection_process_plan ON adm_selection_process_plan.adm_selection_process_plan_id = adm_selection_process_plan_detail.adm_selection_process_plan_id    " +
                "AND adm_selection_process_plan.record_status='A'    " +
                "LEFT JOIN (  " +
                "select adm_selection_process_plan.adm_selection_process_plan_id    " +
                ",COUNT(adm_selection_process.adm_selection_process_id)as admitCardGenerated      " +
                ",sum(ifnull(is_admit_card_published,0)) as admitCardPublished    " +
                "from adm_selection_process    " +
                "inner join adm_selection_process_plan_detail ON adm_selection_process_plan_detail.adm_selection_process_plan_detail_id = adm_selection_process.adm_selection_process_plan_detail_id     " +
                "and adm_selection_process_plan_detail.record_status ='A'    " +
                "inner join adm_selection_process_plan ON adm_selection_process_plan.adm_selection_process_plan_id = adm_selection_process_plan_detail.adm_selection_process_plan_id    " +
                "and adm_selection_process_plan.record_status ='A'    " +
                "where adm_selection_process.record_status='A'  and (adm_selection_process.is_admit_card_nill is null or adm_selection_process.is_admit_card_nill=0)  " +
                "and adm_selection_process_plan_detail.process_order=1  " +
                "AND adm_selection_process_plan.erp_academic_year_id=:admissionYearId    " +
                "group by adm_selection_process_plan.adm_selection_process_plan_id  " +
                ") as admitCardSummary ON admitCardSummary.adm_selection_process_plan_id = adm_selection_process_plan.adm_selection_process_plan_id    " +
                "WHERE adm_selection_process_plan.erp_academic_year_id =:admissionYearId    " +
                "group by adm_selection_process_plan.adm_selection_process_plan_id";
         return Mono.fromFuture(sessionFactory.withSession(s->s.createNativeQuery(str1, Tuple.class).setParameter("admissionYearId", admissionYearId).getResultList()).subscribeAsCompletionStage());
    }

    public Mono<List<Tuple>> getAdmitCardDetailsBySession(Integer sessionPlanId) {
        String str = "SELECT        " +
                "  erp_programme.erp_programme_id,     " +
                "  erp_programme.programme_name_for_application     " +
                "  ,COUNT( student_appln_selection_process_dates.student_appln_entries_id) as applicationReceived       " +
                "  ,admitCardGenerated       " +
                "  ,admitCardPublished       " +
                "  ,DATE_FORMAT(planCenterAdmSelectionProcessPlanDetail.selection_process_date, '%d/%b/%Y %H:%i') as selectionDate     " +
                "FROM student_appln_selection_process_dates       " +
                "LEFT JOIN adm_selection_process_plan_detail ON adm_selection_process_plan_detail.adm_selection_process_plan_detail_id = student_appln_selection_process_dates.adm_selection_process_plan_detail_id       " +
                "AND adm_selection_process_plan_detail.record_status='A' AND adm_selection_process_plan_detail.process_order=1       " +
                "LEFT JOIN adm_selection_process_plan_center_based ON adm_selection_process_plan_center_based.adm_selection_process_plan_center_based_id = student_appln_selection_process_dates.adm_selection_process_plan_center_based_id       " +
                "AND adm_selection_process_plan_center_based.record_status='A'       " +
                "LEFT JOIN adm_selection_process_plan_detail as planCenterAdmSelectionProcessPlanDetail ON planCenterAdmSelectionProcessPlanDetail.adm_selection_process_plan_detail_id = adm_selection_process_plan_center_based.adm_selection_process_plan_detail_id       " +
                "AND planCenterAdmSelectionProcessPlanDetail.record_status='A' AND planCenterAdmSelectionProcessPlanDetail.process_order=1       " +
                "INNER JOIN  adm_selection_process_plan ON adm_selection_process_plan.adm_selection_process_plan_id = IFNULL(adm_selection_process_plan_detail.adm_selection_process_plan_id,planCenterAdmSelectionProcessPlanDetail.adm_selection_process_plan_id)       " +
                "AND adm_selection_process_plan.record_status='A'       " +
                "INNER JOIN  student_appln_entries ON student_appln_entries.student_appln_entries_id = student_appln_selection_process_dates.student_appln_entries_id     " +
                "INNER JOIN  adm_programme_batch ON adm_programme_batch.adm_programme_batch_id = student_appln_entries.adm_programme_batch_id     " +
                "INNER JOIN  erp_campus_programme_mapping ON erp_campus_programme_mapping.erp_campus_programme_mapping_id = adm_programme_batch.erp_campus_programme_mapping_id     " +
                "INNER JOIN  erp_programme ON erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id     " +
                "LEFT JOIN (SELECT   " +
                "  adm_selection_process_plan.adm_selection_process_plan_id,       " +
                "  erp_programme.erp_programme_id,     " +
                "  erp_programme.programme_name_for_application     " +
                "  ,COUNT(adm_selection_process.adm_selection_process_id)AS admitCardGenerated         " +
                "  ,SUM(IFNULL(is_admit_card_published,0)) AS admitCardPublished       " +
                "FROM adm_selection_process       " +
                "INNER JOIN  adm_selection_process_plan_detail ON adm_selection_process_plan_detail.adm_selection_process_plan_detail_id = adm_selection_process.adm_selection_process_plan_detail_id        " +
                "AND adm_selection_process_plan_detail.record_status ='A'       " +
                "INNER JOIN  adm_selection_process_plan ON adm_selection_process_plan.adm_selection_process_plan_id = adm_selection_process_plan_detail.adm_selection_process_plan_id       " +
                "AND adm_selection_process_plan.record_status ='A'       " +
                "INNER JOIN  student_appln_entries ON student_appln_entries.student_appln_entries_id = adm_selection_process.student_appln_entries_id     " +
                "INNER JOIN  adm_programme_batch ON adm_programme_batch.adm_programme_batch_id = student_appln_entries.adm_programme_batch_id     " +
                "INNER JOIN  erp_campus_programme_mapping ON erp_campus_programme_mapping.erp_campus_programme_mapping_id = adm_programme_batch.erp_campus_programme_mapping_id     " +
                "INNER JOIN  erp_programme ON erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id     " +
                "WHERE adm_selection_process.record_status='A'  AND (adm_selection_process.is_admit_card_nill IS NULL OR adm_selection_process.is_admit_card_nill=0)     " +
                "AND adm_selection_process_plan_detail.process_order=1      " +
                "AND adm_selection_process_plan.adm_selection_process_plan_id= :sessionPlanId     " +
                "GROUP BY adm_selection_process_plan.adm_selection_process_plan_id,erp_programme.erp_programme_id     " +
                ") AS admitCardSummary ON admitCardSummary.adm_selection_process_plan_id = adm_selection_process_plan.adm_selection_process_plan_id       " +
                "AND admitCardSummary.erp_programme_id=erp_programme.erp_programme_id      " +
                "WHERE adm_selection_process_plan.adm_selection_process_plan_id= :sessionPlanId     " +
                "GROUP BY erp_programme.erp_programme_id,planCenterAdmSelectionProcessPlanDetail.selection_process_date,admitCardGenerated,admitCardPublished";
         return Mono.fromFuture(sessionFactory.withSession(s->s.createNativeQuery(str, Tuple.class).setParameter("sessionPlanId", sessionPlanId).getResultList()).subscribeAsCompletionStage());
    }
}
