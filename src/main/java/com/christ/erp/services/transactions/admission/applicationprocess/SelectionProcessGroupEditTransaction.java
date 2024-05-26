package com.christ.erp.services.transactions.admission.applicationprocess;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Tuple;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmOfflineApplicationGenerationDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessGroupDetailDBO;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessGroupDBO;
import com.christ.erp.services.dto.admission.applicationprocess.SelectionProcessGroupEditDTO;
import reactor.core.publisher.Mono;

@Repository
public class SelectionProcessGroupEditTransaction {

    @Autowired
    private Mutiny.SessionFactory sessionFactory;

    public Mono<List<Object>> getSelectionDate(int year) {
        String queryString = " select  distinct adm_selection_process_plan_detail.selection_process_date as processDate , adm_selection_process_plan_detail.adm_selection_process_plan_detail_id as planDetailId" +
                " from adm_selection_process_group" +
                " inner join adm_selection_process_plan_detail_allotment On  adm_selection_process_group.adm_selection_process_plan_detail_allotment_id = adm_selection_process_plan_detail_allotment.adm_selection_process_plan_detail_allotment_id" +
                " inner join adm_selection_process_plan_detail on adm_selection_process_plan_detail.adm_selection_process_plan_detail_id = adm_selection_process_plan_detail_allotment.adm_selection_process_plan_detail_id" +
                " inner join adm_selection_process_plan ON adm_selection_process_plan.adm_selection_process_plan_id = adm_selection_process_plan_detail.adm_selection_process_plan_id" +
                " inner join adm_selection_process_type ON adm_selection_process_type.adm_selection_process_type_id = adm_selection_process_plan_detail.adm_selection_process_type_id" +
                " where adm_selection_process_group.record_status = 'A' and adm_selection_process_plan_detail_allotment.record_status = 'A'" +
                " and adm_selection_process_plan_detail.record_status = 'A' and adm_selection_process_plan.record_status = 'A'" +
                " and adm_selection_process_type.record_status = 'A' and adm_selection_process_type.mode='Group Process' " +
                " and adm_selection_process_plan.erp_academic_year_id=:year";
        return Mono.fromFuture(sessionFactory.withSession(s -> {
            Mutiny.Query<Object> query = s.createNativeQuery(queryString, Object.class).setParameter("year", year);
            return query.getResultList();
        }).subscribeAsCompletionStage());
    }

    public Mono<List<Object>> getSelectionGroup(String date, String timeId) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String queryString = "select  distinct adm_selection_process_group.adm_selection_process_group_id as groupId,adm_selection_process_group.selection_process_group_name as groupName ,adm_selection_process_group.selection_process_group_no as groupNo " +
                "  from adm_selection_process_plan_detail " +
                "  inner join adm_selection_process_plan ON adm_selection_process_plan.adm_selection_process_plan_id = adm_selection_process_plan_detail.adm_selection_process_plan_id " +
                "  inner join adm_selection_process_plan_detail_allotment On  adm_selection_process_plan_detail.adm_selection_process_plan_detail_id = adm_selection_process_plan_detail_allotment.adm_selection_process_plan_detail_id  " +
                "  inner join adm_selection_process_group on adm_selection_process_group.adm_selection_process_plan_detail_allotment_id = adm_selection_process_plan_detail_allotment.adm_selection_process_plan_detail_allotment_id " +
                "  where adm_selection_process_plan_detail.selection_process_date = :date " +
                "  and adm_selection_process_plan_detail.record_status = 'A' and adm_selection_process_plan.record_status = 'A' " +
                "  and adm_selection_process_plan_detail_allotment.record_status = 'A' and adm_selection_process_group.record_status ='A' " +
                "  and adm_selection_process_plan_detail_allotment.adm_selection_process_plan_detail_allotment_id = :timeId and adm_selection_process_plan_detail_allotment.record_status = 'A' " +
                "  order by   adm_selection_process_group.selection_process_group_no";
        return Mono.fromFuture(sessionFactory.withSession(s -> {
            Mutiny.Query<Object> query = s.createNativeQuery(queryString, Object.class).setParameter("date", localDate).setParameter("timeId", timeId);
            return query.getResultList();
        }).subscribeAsCompletionStage());
    }

    public Mono<List<Tuple>> getGroupApplicantsData(String groupId) {
        String queryString = " select student_appln_entries.student_appln_entries_id as studentId ,student_appln_entries.application_no as applicationNo ,student_appln_entries.applicant_name as applicantName,"
                + "  concat(erp_programme.programme_name, ' (', ifnull(erp_campus.campus_name, erp_location.location_name), ')') as campusProgramName,"
                + "  student_personal_data_addtnl.profile_photo_url as photoUrl,"
                + "  adm_selection_process.adm_selection_process_id as processId"
                + "  from adm_selection_process_group"
                + "  inner join adm_selection_process_group_detail on  adm_selection_process_group_detail.adm_selection_process_group_id = adm_selection_process_group.adm_selection_process_group_id"
                + "  inner join student_appln_entries on adm_selection_process_group_detail.student_appln_entries_id = student_appln_entries.student_appln_entries_id"
                + "  inner join adm_selection_process  on adm_selection_process.student_appln_entries_id = student_appln_entries.student_appln_entries_id"
                + "  inner join erp_campus_programme_mapping on erp_campus_programme_mapping.erp_campus_programme_mapping_id = student_appln_entries.erp_campus_programme_mapping_id"
                + "  inner join erp_programme ON erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id"
                + "  left join erp_campus ON erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id"
                + "  left join erp_location ON erp_location.erp_location_id = erp_campus_programme_mapping.erp_location_id"
                + "  left join student_personal_data on student_appln_entries.student_personal_data_id = student_personal_data.student_personal_data_id"
                + "  left join student_personal_data_addtnl on  student_personal_data_addtnl.student_personal_data_addtnl_id = student_personal_data.student_personal_data_addtnl_id"
                + "  where adm_selection_process_group_detail.adm_selection_process_group_id = :id and adm_selection_process_group.record_status = 'A'"
                + "  and  adm_selection_process_group_detail.record_status = 'A' and student_appln_entries.record_status = 'A'"
                + "  and erp_campus_programme_mapping.record_status = 'A' and erp_programme.record_status = 'A'"
                + "  and adm_selection_process.record_status= 'A'";
        return Mono.fromFuture(sessionFactory.withSession(s -> {
            Mutiny.Query<Tuple> query = s.createNativeQuery(queryString, Tuple.class).setParameter("id", groupId);
            return query.getResultList();
        }).subscribeAsCompletionStage());
    }

    public List<Tuple> getGroupApplicantsData1(List<Integer> groupId) {
        String queryString = " select student_appln_entries.student_appln_entries_id as studentId ,student_appln_entries.application_no as applicationNo ,student_appln_entries.applicant_name as applicantName ,"
                + "  concat(erp_programme.programme_name, ' (', ifnull(erp_campus.campus_name, erp_location.location_name), ')') as campusProgramName,"
                + "  student_personal_data_addtnl.profile_photo_url as photoUrl,"
                + "  adm_selection_process.adm_selection_process_id as processId"
                + "  from adm_selection_process_group"
                + "  inner join adm_selection_process_group_detail on  adm_selection_process_group_detail.adm_selection_process_group_id = adm_selection_process_group.adm_selection_process_group_id"
                + "  inner join student_appln_entries on adm_selection_process_group_detail.student_appln_entries_id = student_appln_entries.student_appln_entries_id"
                + "  inner join adm_selection_process  on adm_selection_process.student_appln_entries_id = student_appln_entries.student_appln_entries_id"
                + "  inner join erp_campus_programme_mapping on erp_campus_programme_mapping.erp_campus_programme_mapping_id = student_appln_entries.erp_campus_programme_mapping_id"
                + "  inner join erp_programme ON erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id"
                + "  left join erp_campus ON erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id"
                + "  left join erp_location ON erp_location.erp_location_id = erp_campus_programme_mapping.erp_location_id"
                + "  left join student_personal_data on student_appln_entries.student_personal_data_id = student_personal_data.student_personal_data_id"
                + "  left join student_personal_data_addtnl on  student_personal_data_addtnl.student_personal_data_addtnl_id = student_personal_data.student_personal_data_addtnl_id"
                + "  where adm_selection_process_group_detail.adm_selection_process_group_id in (:id) and adm_selection_process_group.record_status = 'A'"
                + "  and  adm_selection_process_group_detail.record_status = 'A' and student_appln_entries.record_status = 'A'"
                + "  and erp_campus_programme_mapping.record_status = 'A' and erp_programme.record_status = 'A'"
                + "  and adm_selection_process.record_status= 'A'";
        return sessionFactory.withSession(s -> {
            Mutiny.Query<Tuple> query = s.createNativeQuery(queryString, Tuple.class).setParameter("id", groupId);
            return query.getResultList();
        }).await().indefinitely();
    }


    public Mono<List<Object>> getApplicantData(String applicationNo, String id, String venueCityId) {
        String queryString = " select student_appln_entries.student_appln_entries_id as studentId,student_appln_entries.application_no as applicationNo,student_appln_entries.applicant_name as applicantName ," +
                "    concat(erp_programme.programme_name, ' (', ifnull(erp_campus.campus_name, erp_location.location_name), ')') as campusProgramName from student_appln_entries" +
                "    inner join erp_campus_programme_mapping on erp_campus_programme_mapping.erp_campus_programme_mapping_id = student_appln_entries.erp_campus_programme_mapping_id" +
                "    inner join erp_programme ON erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id" +
                "    inner join adm_selection_process  on adm_selection_process.student_appln_entries_id = student_appln_entries.student_appln_entries_id" +
                "    inner join adm_selection_process_plan_detail on adm_selection_process_plan_detail.adm_selection_process_plan_detail_id = adm_selection_process.adm_selection_process_plan_detail_id" +
                "    left join erp_campus ON erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id" +
                "    left join erp_location ON erp_location.erp_location_id = erp_campus_programme_mapping.erp_location_id" +
                "    where student_appln_entries.application_no = :applicationNo " +
                "    and student_appln_entries.record_status = 'A' and erp_campus_programme_mapping.record_status = 'A'" +
                "    and  erp_programme.record_status = 'A'" +
                "    and adm_selection_process.record_status = 'A' " +
                "    and adm_selection_process_plan_detail.record_status = 'A' " +
                "    and student_appln_entries.record_status = 'A'";
        if (!Utils.isNullOrEmpty(id)) {
            queryString += " and adm_selection_process_plan_detail.adm_selection_process_plan_detail_id = :id";
        }
        if (!Utils.isNullOrEmpty(venueCityId)) {
            queryString += " and adm_selection_process_plan_detail.adm_selection_process_venue_city_id = :venueCityId";
        }
        String finalStr = queryString;
        Mono<List<Object>> list = Mono.fromFuture(sessionFactory.withSession(s -> {
            Mutiny.Query<Object> query = s.createNativeQuery(finalStr, Object.class).setParameter("applicationNo", applicationNo);
            if (!Utils.isNullOrEmpty(id)) {
                query.setParameter("id", id);
            }
            if (!Utils.isNullOrEmpty(venueCityId)) {
                query.setParameter("venueCityId", venueCityId);
            }
            return query.getResultList();
        }).subscribeAsCompletionStage());
        return list;
    }

    public AdmSelectionProcessGroupDBO edit(String id) {
        String queryString = "select bo from AdmSelectionProcessGroupDBO bo left join fetch bo.admSelectionProcessGroupDetailDBOsSet cbo where bo.id = :id and bo.recordStatus = 'A' and cbo.recordStatus = 'A'";
        return sessionFactory.withSession(s -> s.createQuery(queryString, AdmSelectionProcessGroupDBO.class).setParameter("id", Integer.parseInt(id)).getSingleResultOrNull()).await().indefinitely();
    }

    public void save(AdmSelectionProcessGroupDBO dbo) {
        sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).subscribeAsCompletionStage();
    }

    public void update(AdmSelectionProcessGroupDBO dbo) {
        sessionFactory.withTransaction((session, tx) -> session.find(AdmSelectionProcessGroupDBO.class, dbo.getId()).call(() -> session.merge(dbo))).await().indefinitely();
    }

    public List<Tuple> duplicateCheck(String id, String sid) {
        String queryString = " select distinct  adm_selection_process_group.adm_selection_process_group_id as groupId ,student_appln_entries.application_no as applicationNo ," +
                " adm_selection_process_group.selection_process_group_name as groupName ,adm_selection_process_group_detail.student_appln_entries_id as entriesId, " +
                " adm_selection_process_plan_detail_allotment.selection_process_time as processTime from adm_selection_process_group "
                + " inner join adm_selection_process_group_detail on adm_selection_process_group.adm_selection_process_group_id = adm_selection_process_group_detail.adm_selection_process_group_id"
                + " inner join adm_selection_process_plan_detail_allotment  on adm_selection_process_group.adm_selection_process_plan_detail_allotment_id = adm_selection_process_plan_detail_allotment.adm_selection_process_plan_detail_allotment_id"
                + " inner join student_appln_entries on adm_selection_process_group_detail.student_appln_entries_id = student_appln_entries.student_appln_entries_id"
                + " where adm_selection_process_group_detail.record_status = 'A' and  adm_selection_process_group.adm_selection_process_group_id != :id and  adm_selection_process_group_detail.student_appln_entries_id = :sid";
        return sessionFactory.withSession(s -> s.createNativeQuery(queryString, Tuple.class).setParameter("id", id).setParameter("sid", sid).getResultList()).await().indefinitely();
    }

    public List<Object> duplicateCheckGroup(SelectionProcessGroupEditDTO dto) {
        LocalDate localDate = LocalDate.parse(dto.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String queryString = " select distinct  adm_selection_process_group.selection_process_group_name from adm_selection_process_group"
                + " inner join adm_selection_process_group_detail on adm_selection_process_group.adm_selection_process_group_id = adm_selection_process_group_detail.adm_selection_process_group_id"
                + " inner join adm_selection_process_plan_detail_allotment on adm_selection_process_group.adm_selection_process_plan_detail_allotment_id = adm_selection_process_plan_detail_allotment.adm_selection_process_plan_detail_allotment_id"
                + " inner join  adm_selection_process_plan_detail ON adm_selection_process_plan_detail.adm_selection_process_plan_detail_id = adm_selection_process_plan_detail_allotment.adm_selection_process_plan_detail_id"
                + " inner join adm_selection_process_venue_city ON adm_selection_process_venue_city.adm_selection_process_venue_city_id = adm_selection_process_plan_detail.adm_selection_process_venue_city_id"
                + " where adm_selection_process_group.adm_selection_process_plan_detail_allotment_id = :allotmentId"
                + " and adm_selection_process_plan_detail.selection_process_date = :date \r\n"
                + " and adm_selection_process_venue_city.adm_selection_process_venue_city_id = :cityId and adm_selection_process_group.record_status = 'A'"
                + " and adm_selection_process_group.selection_process_group_name = :name ";
        return sessionFactory.withSession(s -> s.createNativeQuery(queryString, Object.class).setParameter("allotmentId", dto.getTimeAllotmentId()).setParameter("date", localDate)
                .setParameter("cityId", dto.getCenterId()).setParameter("name", dto.getGroupName()).getResultList()).await().indefinitely();
    }

    public AdmSelectionProcessGroupDBO move(SelectionProcessGroupEditDTO dto) {
        String queryString = "select bo from AdmSelectionProcessGroupDBO bo left join fetch bo.admSelectionProcessGroupDetailDBOsSet cbo where bo.id = :id and cbo.studentApplnEntriesDBO.id = :stud and bo.recordStatus = 'A' and cbo.recordStatus = 'A'";
        return sessionFactory.withSession(s -> s.createQuery(queryString, AdmSelectionProcessGroupDBO.class).setParameter("id", Integer.parseInt(dto.getId())).setParameter("stud", Integer.parseInt(dto.getLevels().get(0).getStudentEntrieId())).getSingleResultOrNull()).await().indefinitely();
    }

    public List<Object> groupNo(SelectionProcessGroupEditDTO dto) {
        LocalDate localDate = LocalDate.parse(dto.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String queryString = " select  selection_process_group_no from adm_selection_process_group "
                + " inner join adm_selection_process_plan_detail_allotment on adm_selection_process_group.adm_selection_process_plan_detail_allotment_id = adm_selection_process_plan_detail_allotment.adm_selection_process_plan_detail_allotment_id"
                + " inner join  adm_selection_process_plan_detail ON adm_selection_process_plan_detail.adm_selection_process_plan_detail_id = adm_selection_process_plan_detail_allotment.adm_selection_process_plan_detail_id"
                + " inner join adm_selection_process_venue_city ON adm_selection_process_venue_city.adm_selection_process_venue_city_id = adm_selection_process_plan_detail.adm_selection_process_venue_city_id"
                + " where selection_process_group_no =( select max(selection_process_group_no) from adm_selection_process_group where adm_selection_process_plan_detail_allotment_id = :allotmentId)"
                + " and adm_selection_process_group.adm_selection_process_plan_detail_allotment_id = :allotmentId"
                + " and adm_selection_process_plan_detail.selection_process_date = :date "
                + " and adm_selection_process_venue_city.adm_selection_process_venue_city_id = :cityId"
                + " and adm_selection_process_group.record_status = 'A'";
        return sessionFactory.withSession(s -> s.createNativeQuery(queryString, Object.class).setParameter("allotmentId", dto.getTimeAllotmentId()).setParameter("date", localDate)
                .setParameter("cityId", dto.getCenterId()).getResultList()).await().indefinitely();
    }

    public void moveUpdate(List<AdmSelectionProcessGroupDBO> dbo) {
        sessionFactory.withTransaction((session, tx) -> session.mergeAll(dbo.toArray())).await().indefinitely();
    }

    public Mono<Boolean> delete(List<Integer> studentEntrieId, List<Integer> groupId, String userId) {
        sessionFactory.withTransaction((session, tx) -> session.createQuery("select dbo from AdmSelectionProcessGroupDetailDBO dbo " +
                        " left join fetch dbo.studentApplnEntriesDBO" +
                        " where dbo.studentApplnEntriesDBO.id IN (:studentEntrieId) and dbo.admSelectionProcessGroupDBO.id IN (:groupId) and dbo.recordStatus ='A'", AdmSelectionProcessGroupDetailDBO.class).setParameter("studentEntrieId", studentEntrieId).setParameter("groupId", groupId).getSingleResultOrNull()
                .chain(dbo -> session.fetch(dbo)).invoke(dbo -> {
                    if (!Utils.isNullOrEmpty(dbo)) {
                        dbo.setModifiedUsersId(Integer.valueOf(userId));
                        dbo.setRecordStatus('D');
                    }
                })).await().indefinitely();
        return Mono.just(Boolean.TRUE);
    }

    public Mono<Boolean> deleteGroup(Integer id, String userId) {
        sessionFactory.withTransaction((session, tx) -> session.find(AdmSelectionProcessGroupDBO.class, id).invoke(dbo -> {
            if (!Utils.isNullOrEmpty(dbo)) {
                dbo.setModifiedUsersId(Integer.valueOf(userId));
                dbo.setRecordStatus('D');
            }
        })).await().indefinitely();
        return Mono.just(Boolean.TRUE);
    }

    public Mono<Boolean> deleteGroup1(List<Integer> groupIds, String userId) {
        Set<Integer> groupDetailsIds = new HashSet<>();
        sessionFactory.withTransaction((session, tx) -> session.createQuery("select dbo from AdmSelectionProcessGroupDBO dbo " +
                        " left join fetch dbo.admSelectionProcessGroupDetailDBOsSet" +
                        " where dbo.id IN (:groupIds) and dbo.recordStatus ='A'", AdmSelectionProcessGroupDBO.class).setParameter("groupIds", groupIds).getResultList()
                .chain(dbo -> session.fetch(dbo)).invoke(dbos -> {
                    if (!Utils.isNullOrEmpty(dbos)) {
                        dbos.forEach(groupDbo -> {
                            groupDetailsIds.clear();
                            if (!Utils.isNullOrEmpty(groupDbo.getAdmSelectionProcessGroupDetailDBOsSet())) {
                                for (AdmSelectionProcessGroupDetailDBO admSelectionProcessGroupDetailDBO : groupDbo.getAdmSelectionProcessGroupDetailDBOsSet()) {
                                    if (admSelectionProcessGroupDetailDBO.getRecordStatus() == 'A') {
                                        groupDetailsIds.add(admSelectionProcessGroupDetailDBO.getId());
                                    }
                                }
                            }
                            if (Utils.isNullOrEmpty(groupDetailsIds)) {
                                groupDbo.setModifiedUsersId(Integer.valueOf(userId));
                                groupDbo.setRecordStatus('D');
                            }
                        });
                    }
                })).await().indefinitely();
        return Mono.just(Boolean.TRUE);
    }


}
