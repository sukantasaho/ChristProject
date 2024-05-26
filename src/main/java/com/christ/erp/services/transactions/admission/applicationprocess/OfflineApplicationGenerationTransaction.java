package com.christ.erp.services.transactions.admission.applicationprocess;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmOfflineApplicationGenerationDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmAdmissionTypeDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmIntakeBatchDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmPrerequisiteSettingsDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmProgrammeSettingsDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpCommitteeDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dto.admission.applicationprocess.OfflineApplicationGenerationDTO;
import com.christ.erp.services.dto.admission.settings.AdmProgrammeSettingsDTO;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.christ.erp.services.common.Utils.convertLocalDateTimeToStringDate;

@Repository
public class OfflineApplicationGenerationTransaction {

    @Autowired
    private Mutiny.SessionFactory sessionFactory;

//    public Mono<List<AdmOfflineApplicationGenerationDBO>> getGridData(Integer yearId) {
//        String str = "select db from AdmOfflineApplicationGenerationDBO db where db.erpAcademicYearDBO.id = :yearId  and db.recordStatus = 'A' ";
//        return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, AdmOfflineApplicationGenerationDBO.class).setParameter("yearId", yearId).getResultList()).subscribeAsCompletionStage());
//    }

    public Mono<List<AdmOfflineApplicationGenerationDBO>> getGridData(Integer yearId) {
        String str = "select db from AdmOfflineApplicationGenerationDBO db " +
                " left join fetch db.erpAcademicYearDBO ey " +
                " left join fetch db.admProgrammeBatchDBO apb" +
                " left join fetch apb.admProgrammeSettingsDBO aps" +
                " left join fetch aps.admAdmissionTypeDBO at" +
                " left join fetch apb.erpCampusProgrammeMappingDBO ecpm" +
                " left join fetch aps.admIntakeBatchDBO" +
                " where ey.id = :yearId  and db.recordStatus = 'A' ";
        return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, AdmOfflineApplicationGenerationDBO.class).setParameter("yearId", yearId).getResultList()).subscribeAsCompletionStage());
    }

    public void update(AdmOfflineApplicationGenerationDBO dbo) {
        sessionFactory.withTransaction((session, tx) -> session.find(AdmOfflineApplicationGenerationDBO.class, dbo.getId()).call(() -> session.merge(dbo))).await().indefinitely();
    }

    public void save(AdmOfflineApplicationGenerationDBO dbo) {
        sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
    }

    public AdmOfflineApplicationGenerationDBO getOfflineApplicationGenerationDetail(int id) {
//        return sessionFactory.withSession(s -> s.find(AdmOfflineApplicationGenerationDBO.class, id)).await().indefinitely();
        String str = " select dbo from AdmOfflineApplicationGenerationDBO dbo " +
                " left join fetch dbo.admProgrammeBatchDBO apb" +
                " left join fetch apb.admProgrammeSettingsDBO aps" +
                " left join fetch aps.admAdmissionTypeDBO at" +
                " left join fetch apb.erpCampusProgrammeMappingDBO ecpm" +
                " left join fetch aps.admIntakeBatchDBO" +
                " where dbo.recordStatus ='A' and dbo.id = :id ";
        return sessionFactory.withSession(s -> s.createQuery(str, AdmOfflineApplicationGenerationDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();


    }

    public boolean duplicateCheck(OfflineApplicationGenerationDTO dto) {
        String query = "select db from AdmOfflineApplicationGenerationDBO db" +
                " join AdmProgrammeBatchDBO pb on db.admProgrammeBatchDBO.id = pb.id" +
                " join AdmProgrammeSettingsDBO ps on pb.admProgrammeSettingsDBO.id = ps.id" +
                " where db.emailToSendApplicationLink = :email and db.mobileNoToSendApplicationLink = :mobile" +
                " and db.erpAcademicYearDBO.id = :aId and ps.erpProgrammeDBO.id = :pId and ps.admIntakeBatchDBO.id = :batchId" +
                " and db.recordStatus = 'A'";

        if (!Utils.isNullOrEmpty(dto.getId())) {
            query += " and db.id != :id";
        }

        String finalQuery = query;

        List<AdmOfflineApplicationGenerationDBO> list = sessionFactory.withSession(s -> {
            Mutiny.Query<AdmOfflineApplicationGenerationDBO> str = s.createQuery(finalQuery, AdmOfflineApplicationGenerationDBO.class);
            str.setParameter("email", dto.getEmailToSendApplicationLink());
            str.setParameter("mobile", dto.getMobileNoToSendApplicationLink());
            str.setParameter("aId", Integer.parseInt(String.valueOf(dto.getErpAcademicYearDTO().getId())));
            str.setParameter("pId", Integer.parseInt(String.valueOf(dto.getErpProgrammeDTO().getValue())));
            str.setParameter("batchId", Integer.parseInt(String.valueOf(dto.getAdmIntakeBatchDTO().getValue())));

            if (!Utils.isNullOrEmpty(dto.getId())) {
                str.setParameter("id", dto.getId());
            }

            return str.getResultList();
        }).await().indefinitely();
        return !Utils.isNullOrEmpty(list);
    }

//    public boolean duplicateCheck(OfflineApplicationGenerationDTO dto) {
//        String query = "select db.adm_offline_application_generation_id, db.applicant_name, db.email_to_send_application_link, db.mobile_no_to_send_application_link, db.adm_programme_batch_id from adm_offline_application_generation as db" +
//                " inner join adm_programme_batch on db.adm_programme_batch_id = adm_programme_batch.adm_programme_batch_id" +
//                " inner join adm_programme_settings on adm_programme_batch.adm_programme_settings_id = adm_programme_settings.adm_programme_settings_id" +
//                " where db.email_to_send_application_link = :email and db.mobile_no_to_send_application_link = :mobile and db.erp_admission_year_id = :aId" +
//                " and adm_programme_settings.erp_programme_id = :pId and adm_programme_settings.adm_intake_batch_id = :batchId" +
//                " and db.record_status = 'A'";
//        if (!Utils.isNullOrEmpty(dto.getId())) {
//            query += " and db.id = :id";
//        }
//        String finalStr = query;
//        Mono<List<Tuple>> list = Mono.fromFuture(sessionFactory.withSession(s -> { Mutiny.Query<Tuple> query1 = s.createNativeQuery(finalStr, Tuple.class);
//            query1.setParameter("email", dto.getEmailToSendApplicationLink());
//            query1.setParameter("mobile", dto.getMobileNoToSendApplicationLink());
//            query1.setParameter("aId", Integer.parseInt(String.valueOf(dto.getErpAcademicYearDBO().getId())));
//            query1.setParameter("pId", Integer.parseInt(String.valueOf(dto.getErpProgrammeDBO().getId())));
//            query1.setParameter("batchId", Integer.parseInt(String.valueOf(dto.getAdmIntakeBatchDBO().getId())));
//            if (!Utils.isNullOrEmpty(dto.getId())) {
//                query1.setParameter("id", dto.getId());
//            }
//            return  query1.getResultList();
//        }).subscribeAsCompletionStage());
////        return Utils.isNullOrEmpty(list) ? true : false;
//        if(Utils.isNullOrEmpty(list)) {
//            return false;
//        } else {
//            return true;
//        }
//    }

    public ErpTemplateDBO getTemplate(String templateCode) throws Exception {
        return DBGateway.runJPA(new ISelectGenericTransactional<ErpTemplateDBO>() {
            @SuppressWarnings("unchecked")
            @Override
            public ErpTemplateDBO onRun(EntityManager context) throws Exception {
                Query query = context.createQuery("from ErpTemplateDBO where templateCode=:templateCode and recordStatus='A'");
                query.setParameter("templateCode", templateCode);
                return (ErpTemplateDBO) Utils.getUniqueResult(query.getResultList());
            }

            @Override
            public void onError(Exception error) throws Exception {
                throw error;
            }
        });
    }

    public Mono<Boolean> delete(int id, Integer userId) {
        sessionFactory.withTransaction((session, tx) -> session.find(AdmOfflineApplicationGenerationDBO.class, id).invoke(dbo -> {
            dbo.setModifiedUsersId(userId);
            dbo.setRecordStatus('D');
        })).await().indefinitely();
        return Mono.just(Boolean.TRUE);
    }

    public Mono<List<Tuple>> getProgrammeByDate(LocalDate date) {
        String query = "select distinct erp_programme.erp_programme_id as id, erp_programme.programme_name as name from erp_programme" +
                " inner join adm_programme_settings on  erp_programme.erp_programme_id = adm_programme_settings.erp_programme_id" +
                " inner join adm_programme_batch on adm_programme_settings.adm_programme_settings_id = adm_programme_batch.adm_programme_settings_id" +
                " inner join adm_selection_process_plan_programme on adm_programme_batch.adm_programme_batch_id = adm_selection_process_plan_programme.adm_programme_batch_id" +
                " inner join adm_selection_process_plan on adm_selection_process_plan_programme.adm_selection_process_plan_id = adm_selection_process_plan.adm_selection_process_plan_id" +
                " where adm_selection_process_plan.record_status = 'A' and adm_selection_process_plan_programme.record_status = 'A' and adm_programme_batch.record_status = 'A'" +
                " and adm_programme_settings.record_status = 'A' and erp_programme.record_status = 'A'";
        if (!Utils.isNullOrEmpty(date)) {
            query += " and :date between adm_selection_process_plan.application_open_from and adm_selection_process_plan.application_open_till";
        }
        String finalStr = query;
        Mono<List<Tuple>> list = Mono.fromFuture(sessionFactory.withSession(s -> {
            Mutiny.Query<Tuple> query1 = s.createNativeQuery(finalStr, Tuple.class);
            if (!Utils.isNullOrEmpty(date)) {
                query1.setParameter("date", date);
            }
            return query1.getResultList();
        }).subscribeAsCompletionStage());
        return list;
//        return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(queryString, Tuple.class).setParameter("date", date).getResultList()).subscribeAsCompletionStage());
    }

    public Mono<List<Tuple>> getIntakeBatchByProgramme(Integer programmeId) {
        String queryString = "select distinct adm_intake_batch.adm_intake_batch_id as id, adm_intake_batch.adm_intake_batch_name as name from adm_intake_batch" +
                " inner join adm_programme_settings on adm_intake_batch.adm_intake_batch_id = adm_programme_settings.adm_intake_batch_id" +
                " where adm_programme_settings.erp_programme_id = :programmeId" +
                " and adm_programme_settings.record_status = 'A' and adm_intake_batch.record_status = 'A'";
        return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(queryString, Tuple.class).setParameter("programmeId", programmeId).getResultList()).subscribeAsCompletionStage());
    }

    public Mono<List<Tuple>> getAdmissionTypeByBatch(Integer programmeId, Integer intakeBatchId) {
        String queryString = "select distinct adm_admission_type.adm_admission_type_id as id, adm_admission_type.admission_type as name from adm_admission_type" +
                " inner join adm_programme_settings on adm_admission_type.adm_admission_type_id = adm_programme_settings.adm_admission_type_id" +
                " where adm_programme_settings.erp_programme_id = :programmeId and adm_programme_settings.adm_intake_batch_id = :intakeBatchId" +
                " and adm_programme_settings.record_status = 'A' and adm_admission_type.record_status = 'A'";
        return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(queryString, Tuple.class).setParameter("programmeId", programmeId).setParameter("intakeBatchId", intakeBatchId).getResultList()).subscribeAsCompletionStage());
    }

    public Mono<List<Tuple>> getCampusOrLocation(Integer programmeId, Integer intakeBatchId, Integer admissionTypeId) {
        String queryString = "select distinct erp_campus.erp_campus_id as id, erp_campus.campus_name as name from erp_campus" +
                " inner join erp_campus_programme_mapping on erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id" +
                " inner join adm_programme_settings on erp_campus_programme_mapping.erp_programme_id = adm_programme_settings.erp_programme_id" +
                " and adm_programme_settings.erp_programme_id = :programmeId and adm_programme_settings.adm_intake_batch_id = :intakeBatchId and adm_programme_settings.adm_admission_type_id = :admissionTypeId" +
                " and adm_programme_settings.record_status = 'A' and erp_campus_programme_mapping.record_status = 'A' and erp_campus.record_status = 'A'";
        return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(queryString, Tuple.class).setParameter("programmeId", programmeId).setParameter("intakeBatchId", intakeBatchId).setParameter("admissionTypeId", admissionTypeId).getResultList()).subscribeAsCompletionStage());
    }

    //    public Mono<List<Tuple>> getAdmProgrammeBatchId(Integer intakeBatchId) {
    public List<Integer> getAdmProgrammeBatchId(Integer intakeBatchId) {
        String queryString = "select distinct adm_programme_batch.adm_programme_batch_id from adm_programme_batch" +
                " inner join adm_programme_settings on adm_programme_batch.adm_programme_settings_id = adm_programme_settings.adm_programme_settings_id" +
                " inner join adm_intake_batch on adm_programme_settings.adm_intake_batch_id = adm_intake_batch.adm_intake_batch_id" +
                " where adm_intake_batch.adm_intake_batch_id = :intakeBatchId" +
                " and adm_intake_batch.record_status = 'A' and adm_programme_settings.record_status = 'A' and adm_programme_batch.record_status = 'A'";
//        return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(queryString, Tuple.class).setParameter("intakeBatchId",intakeBatchId).getResultList()).subscribeAsCompletionStage());
//        return sessionFactory.withSession(s -> s.createNativeQuery(queryString, Object.class).setParameter("intakeBatchId",intakeBatchId).getResultList()).await().indefinitely();
        List<Object> resultList = sessionFactory.withSession(s ->
                        s.createNativeQuery(queryString)
                                .setParameter("intakeBatchId", intakeBatchId)
                                .getResultList())
                .await().indefinitely();

        List<Integer> integerList = new ArrayList<>();
        for (Object obj : resultList) {
            if (obj instanceof Integer) {
                integerList.add((Integer) obj);
            }
        }
        return integerList;
    }


}
