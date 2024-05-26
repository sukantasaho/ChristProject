package com.christ.erp.services.transactions.admission.applicationprocess;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.Tuple;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDetailDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnSelectionProcessRescheduleDBO;
import com.christ.erp.services.dbobjects.student.recruitment.StudentApplnSelectionProcessDatesDBO;
import com.christ.erp.services.dto.admission.admissionprocess.SelectionProcessRescheduleRequestDTO;
import reactor.core.publisher.Mono;

@Repository
public class RescheduleRequestsAndApprovalTransaction {
	
	@Autowired
	private Mutiny.SessionFactory sessionFactory;
	
	public Mono<List<Tuple>> getApplicantNoList(int applicationNo, Integer yearId) {
		String queryString = "select distinct  student_appln_entries.student_appln_entries_id,"
				+ "	concat( student_appln_entries.application_no, '(', (student_appln_entries.applicant_name), ')')  as student"
				+ "	from  student_appln_entries where  record_status = 'A' and applied_academic_year_id = :yearId and application_no like   :applicationNo ";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(queryString, Tuple.class)
				.setParameter("applicationNo",String.valueOf(applicationNo)+"%").setParameter("yearId", yearId).getResultList()).subscribeAsCompletionStage());
	}
	
	public Mono<List<Tuple>> getApplicantNameList(String applicantName,Integer yearId) {
		String queryString = "  select distinct  student_appln_entries.student_appln_entries_id,"
				+ "	concat( student_appln_entries.applicant_name, '(', (student_appln_entries.application_no),',',(erp_programme.programme_name), ')')  as student"
				+ "	from  student_appln_entries"
				+ " inner join erp_campus_programme_mapping ON erp_campus_programme_mapping.erp_campus_programme_mapping_id = student_appln_entries.erp_campus_programme_mapping_id"
				+ " inner join erp_programme on erp_campus_programme_mapping.erp_programme_id = erp_programme.erp_programme_id"
				+ "	where  student_appln_entries.record_status = 'A' and applied_academic_year_id = :yearId and student_appln_entries.applicant_name like  :applicantName "
				+ " and student_appln_entries.applicant_name is not null"
				+ " and student_appln_entries.application_no is not null"
				+ " and student_appln_entries.erp_campus_programme_mapping_id is not null";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(queryString, Tuple.class)
				.setParameter("applicantName",applicantName+"%").setParameter("yearId", yearId).getResultList()).subscribeAsCompletionStage());
	}

	public StudentApplnEntriesDBO getApplicantDetails(String studentApplnEntriesId) {
		 String queryString = "select dbo from StudentApplnEntriesDBO dbo "
		 	//	+ "	 inner join fetch dbo.admSelectionProcessDBOS asp"
		 		+ "  inner join fetch dbo.studentApplnSelectionProcessDatesDBOS sspd"
		 		+ "  inner join fetch sspd.admSelectionProcessPlanDetailDBO aspd"
		 		+ "  inner join fetch aspd.admSelectionProcessPlanDetailProgDBOs adpdp"
		 		+ "  where dbo.recordStatus='A'and sspd.recordStatus = 'A' and aspd.recordStatus = 'A' and adpdp.recordStatus = 'A'"
		 		+ "  and dbo.id=:studentApplnEntriesId and dbo.erpCampusProgrammeMappingDBO.id = adpdp.erpCampusProgrammeMappingDBO.id ";
		return sessionFactory.withSession(session->session.createQuery(queryString, StudentApplnEntriesDBO.class)
		            .setParameter("studentApplnEntriesId", Integer.parseInt(studentApplnEntriesId)).getSingleResultOrNull()).await().indefinitely();
	}
	
	public List<StudentApplnSelectionProcessRescheduleDBO> getRescheduleCount(String studentApplnEntriesId) {
		 String queryString = "select dbo from StudentApplnSelectionProcessRescheduleDBO dbo"
		 		+ " left join fetch dbo.admSelectionProcessPlanDetailDBO aspd"
		 		+ " where  dbo.studentApplnEntriesDBO.id = :studentApplnEntriesId and dbo.recordStatus = 'A'";
		return sessionFactory.withSession(session->session.createQuery(queryString, StudentApplnSelectionProcessRescheduleDBO.class)
		            .setParameter("studentApplnEntriesId", Integer.parseInt(studentApplnEntriesId)).getResultList()).await().indefinitely();
	}
	
	public ErpAcademicYearDBO getCurrentYear() {
		String queryString = " from ErpAcademicYearDBO bo where bo.recordStatus='A' and  bo.isCurrentAcademicYear = 1";
		return  sessionFactory.withSession(session->session.createQuery(queryString, ErpAcademicYearDBO.class).getSingleResultOrNull()).await().indefinitely();
	}
	
	public List<Tuple> getFilledCount(int selectionProcessDetailsId) {
		 String queryString = " select student_appln_selection_process_dates.student_appln_selection_process_dates_id from student_appln_selection_process_dates"
		 		+ " where record_status = 'A' and adm_selection_process_plan_detail_id = :selectionProcessDetailsId ";
		return sessionFactory.withSession(session->session.createNativeQuery(queryString, Tuple.class)
		       .setParameter("selectionProcessDetailsId", selectionProcessDetailsId).getResultList()).await().indefinitely();
	}
	
	public List<Tuple> getFilledCountCenterBased(int selectionProcessDetailsId, int selectionProcessCenterBasedId) {
		 String queryString = " select student_appln_selection_process_dates.student_appln_selection_process_dates_id from student_appln_selection_process_dates"
		 		+ " where record_status = 'A' and adm_selection_process_plan_detail_id = :selectionProcessDetailsId and adm_selection_process_plan_center_based_id = :selectionProcessCenterBasedId ";
		return sessionFactory.withSession(session->session.createNativeQuery(queryString, Tuple.class)
				.setParameter("selectionProcessDetailsId", selectionProcessDetailsId)
		       .setParameter("selectionProcessCenterBasedId", selectionProcessCenterBasedId).getResultList()).await().indefinitely();
	}

	public List<AdmSelectionProcessPlanDetailDBO> getSelectionProcessDates(String erpCampusProgrammeMappingId,String selectionProcessType) {
		String queryString = " select distinct dbo from AdmSelectionProcessPlanDetailDBO dbo "
				+ " left join fetch dbo.admSelectionProcessPlanCenterBasedDBOs aspcd"
				+ " inner join fetch dbo.admSelectionProcessPlanDetailAllotmentDBOs asppda"
				+ " inner join fetch  dbo.admSelectionProcessPlanDBO aspp"
//				+ " inner join fetch dbo.studentApplnSelectionProcessDatesDBOS saspd"
				+ " inner join fetch  dbo.admSelectionProcessPlanDetailProgDBOs aspdp"
				+ " inner join fetch  aspdp.erpCampusProgrammeMappingDBO ecpm "
				+ " where dbo.recordStatus='A' and aspp.recordStatus='A' and aspdp.recordStatus='A' and ecpm.recordStatus='A' and asppda.recordStatus='A' "
				+ " and aspdp.erpCampusProgrammeMappingDBO.id = :erpCampusProgrammeMappingId"
				+ " and dbo.processOrder = 1 and dbo.admSelectionProcessTypeDBO.id = :selectionProcessType"
				+ " and dbo.selectionProcessDate > :localdate "
				+ " and :localdate <= aspp.applicationOpenTill ";
		return sessionFactory.withSession(s -> s.createQuery(queryString, AdmSelectionProcessPlanDetailDBO.class)
				.setParameter("erpCampusProgrammeMappingId", Integer.parseInt(erpCampusProgrammeMappingId))
				.setParameter("selectionProcessType", Integer.parseInt(selectionProcessType))
				.setParameter("localdate", LocalDate.now())
				.getResultList()).await().indefinitely();
	}
	
	public AdmSelectionProcessPlanDetailDBO getSelectionProcessPlanId(String selectionProcessPlanDetailsId, String selectedVenueId) {
		String queryString =" select distinct dbo from AdmSelectionProcessPlanDetailDBO dbo"
				+ " where dbo.recordStatus='A'   and dbo.id = :selectionProcessPlanDetailsId and dbo.admSelectionProcessVenueCityDBO.id = :selectedVenueId  ";
		return sessionFactory.withSession(s -> s.createQuery(queryString, AdmSelectionProcessPlanDetailDBO.class)
				.setParameter("selectionProcessPlanDetailsId", Integer.parseInt(selectionProcessPlanDetailsId))
				.setParameter("selectedVenueId", Integer.parseInt(selectedVenueId))
				.getSingleResultOrNull()).await().indefinitely();
	}

	public List<AdmSelectionProcessPlanDetailDBO> getSelectionProcessDatesBySelectionProcessPlanDetailsId(int selectionProcessPlanId) {
		String queryString =" select distinct dbo from AdmSelectionProcessPlanDetailDBO dbo"
				+ " left join fetch dbo.admSelectionProcessPlanCenterBasedDBOs aspcd"
				+ " inner join fetch dbo.admSelectionProcessPlanDetailAllotmentDBOs asppda"
				+ " inner join fetch  dbo.admSelectionProcessPlanDBO aspp"
				+ " inner join fetch dbo.studentApplnSelectionProcessDatesDBOS saspd"
				+ " where dbo.recordStatus='A' and aspp.recordStatus='A' and saspd.recordStatus='A'  and asppda.recordStatus='A'"
				+ " and dbo.processOrder = 2 and aspp.id = :selectionProcessPlanId ";
		return sessionFactory.withSession(s -> s.createQuery(queryString, AdmSelectionProcessPlanDetailDBO.class)
				.setParameter("selectionProcessPlanId", selectionProcessPlanId)
				//.setParameter("selectedVenueId", Integer.parseInt(selectedVenueId))
				.getResultList()).await().indefinitely();
	}
	
	public List<Tuple> checkAdmitCard(SelectionProcessRescheduleRequestDTO dto) {
		 String queryString = " select adm_selection_process.adm_selection_process_id from adm_selection_process"
		 		+ " inner join adm_selection_process_plan_detail on adm_selection_process.adm_selection_process_plan_detail_id = adm_selection_process_plan_detail.adm_selection_process_plan_detail_id"
		 		+ " where adm_selection_process.record_status = 'A' and adm_selection_process_plan_detail.record_status = 'A' "
		 		+ " and adm_selection_process.student_appln_entries_id = :id";
		return sessionFactory.withSession(session->session.createNativeQuery(queryString, Tuple.class)
		      .setParameter("id", dto.getStudentEnteriesId()).getResultList()).await().indefinitely();
	}
	
	public void save(List<StudentApplnSelectionProcessRescheduleDBO> dbos) {
		sessionFactory.withTransaction((session, tx) -> session.persistAll(dbos.toArray())).subscribeAsCompletionStage();
	}
	
	public void save1(List<StudentApplnEntriesDBO> dbos) {
		sessionFactory.withTransaction((session, txt) -> session.mergeAll(dbos.toArray())).subscribeAsCompletionStage();
	}
	
	public AdmSelectionProcessPlanDetailDBO getAdmSelectionProcessCenterBasedId(String selectionProcessPlanDetailsId) {
		String query = " select distinct dbo from AdmSelectionProcessPlanDetailDBO dbo"
				+ " left join fetch dbo.admSelectionProcessPlanCenterBasedDBOs aspcd  where dbo.recordStatus='A' and dbo.id = : id";
		return  sessionFactory.withSession(s -> s.createQuery(query, AdmSelectionProcessPlanDetailDBO.class).setParameter("id",Integer.parseInt(selectionProcessPlanDetailsId))
				.getSingleResultOrNull()).await().indefinitely();
	}

	public List<StudentApplnSelectionProcessRescheduleDBO> approvalRescheduleDetails() {
		String query = " select dbo from StudentApplnSelectionProcessRescheduleDBO dbo where   dbo.isRequestAuthorized  = 0 or dbo.isRequestAuthorized is null"
				+ " and dbo.recordStatus = 'A' and dbo.isRequestRejected is null or dbo.isRequestRejected != 1";
		return sessionFactory.withSession(s -> s.createQuery(query,StudentApplnSelectionProcessRescheduleDBO.class).getResultList()).await().indefinitely();
	}
	
	public List<StudentApplnEntriesDBO> getRescheduleData(List<Integer> studentIds) {
		 String queryString = "select dbo1 from StudentApplnEntriesDBO dbo1 "
		 		+ "  left join fetch dbo1.studentApplnSelectionProcessRescheduleDBOs dbo"
		 		+ "  left join fetch dbo1.admSelectionProcessDBOS dbo2"
		 		+ "  left join fetch dbo1.studentApplnSelectionProcessDatesDBOS dbo3"
		 		+ " where  dbo.studentApplnEntriesDBO.id in( :studentApplnEntriesId ) and dbo.isRequestAuthorized  = 0 or dbo.isRequestAuthorized is null"
		 		+ " and dbo.recordStatus = 'A' and dbo.isRequestRejected is null or dbo.isRequestRejected != 1 ";
		return sessionFactory.withSession(session->session.createQuery(queryString, StudentApplnEntriesDBO.class)
		            .setParameter("studentApplnEntriesId", studentIds).getResultList()).await().indefinitely();
	}
	
	public List<StudentApplnSelectionProcessDatesDBO> getStudentSelectionProcessDatas(List<Integer> studentIds) {
		 String queryString = "select dbo from StudentApplnSelectionProcessDatesDBO dbo"
		 		+ " where  dbo.recordStatus = 'A' and  dbo.studentApplnEntriesDBO.id in( :studentApplnEntriesId )  ";
		return sessionFactory.withSession(session->session.createQuery(queryString, StudentApplnSelectionProcessDatesDBO.class)
		            .setParameter("studentApplnEntriesId", studentIds).getResultList()).await().indefinitely();
	}
	
	public List<AdmSelectionProcessDBO> getSelectionProcess(Integer studentId,Integer planDetailsId) {
		String query = " select distinct dbo from AdmSelectionProcessDBO dbo"
				+ " where dbo.recordStatus='A' and dbo.studentApplnEntriesDBO.id = :studentId  ";
//				+ " and dbo.admSelectionProcessPlanDetailDBO.id = :planDetailId and dbo.spAdmitCardUrl is not null ";
		return  sessionFactory.withSession(s -> s.createQuery(query, AdmSelectionProcessDBO.class)
				.setParameter("studentId",studentId)
//				.setParameter("planDetailId",planDetailsId)
				.getResultList()).await().indefinitely();
	}

}
