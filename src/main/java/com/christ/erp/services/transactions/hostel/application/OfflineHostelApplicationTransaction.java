package com.christ.erp.services.transactions.hostel.application;

import java.util.List;
import javax.persistence.Tuple;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelApplicationDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;
import com.christ.erp.services.dto.hostel.settings.HostelApplicationDTO;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import reactor.core.publisher.Mono;

@Repository
public class OfflineHostelApplicationTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	@Autowired
	CommonApiTransaction commonApiTransaction;

	public Mono<List<HostelApplicationDBO>> getGridData(String yearId) {
		ErpAcademicYearDBO currYear = commonApiTransaction.getCurrentAdmissionYear();
		String str = " select distinct dbo from HostelApplicationDBO dbo"
				+" left join fetch dbo.hostelApplicationRoomTypePreferenceDBO dbos"
				+" where dbo.recordStatus ='A' and dbo.isOffline = 1 and dbo.erpAcademicYearDBO.id =:yearId and dbos.recordStatus ='A'";
		String finalStr = str;
		Mono<List<HostelApplicationDBO>> list = Mono.fromFuture(sessionFactory.withSession(s-> {
			Mutiny.Query<HostelApplicationDBO> query = s.createQuery(finalStr,HostelApplicationDBO.class);
			if(!Utils.isNullOrEmpty(yearId)) {
				String year = yearId.trim();
				query.setParameter("yearId", Integer.parseInt(year));
			} else {
				query.setParameter("yearId",currYear.getId());
			}
			return query.getResultList();
		}).subscribeAsCompletionStage());
		return list;	
	}		

	public void save(HostelApplicationDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo).chain(session::flush).map(s -> {
			return convertDbo(dbo);
		}).flatMap(s -> session.persist(s))).await().indefinitely();
	}

	public ErpWorkFlowProcessStatusLogDBO convertDbo(HostelApplicationDBO dbo) {
		ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
		erpWorkFlowProcessStatusLogDBO.setEntryId(dbo.getId());
		erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(new ErpWorkFlowProcessDBO());
		erpWorkFlowProcessStatusLogDBO.getErpWorkFlowProcessDBO().setId(dbo.getHostelApplicationCurrentProcessStatus().getId());
		erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
		erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(dbo.getCreatedUsersId());
		return erpWorkFlowProcessStatusLogDBO;
	}

	public void update(HostelApplicationDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.find(HostelApplicationDBO.class, dbo.getId()).call(() -> session.merge(dbo))).await().indefinitely();
	}

	public boolean checkIsStudent(String registerNo) {
		String str = "select distinct dbo from StudentDBO dbo where dbo.registerNo =:registerNo and dbo.recordStatus ='A'";
		List<StudentDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<StudentDBO> query = s.createQuery(str, StudentDBO.class);
			query.setParameter("registerNo", registerNo);
			return query.getResultList();
		}).await().indefinitely();
		return Utils.isNullOrEmpty(list) ? false : true;
	}

	public Mono<HostelApplicationDBO> edit(int id) {
		String str = " select dbo from HostelApplicationDBO dbo left join fetch dbo.hostelApplicationRoomTypePreferenceDBO as dbos"
				+" where dbo.recordStatus ='A' and dbos.recordStatus = 'A' and dbo.id =:id";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, HostelApplicationDBO.class).setParameter("id", id).getSingleResultOrNull()).subscribeAsCompletionStage());
	}	

	public boolean duplicateCheck(HostelApplicationDTO dto) {
		String str = " select dbo from HostelApplicationDBO dbo"
				+" where dbo.recordStatus ='A' and ((dbo.erpAcademicYearDBO.id =:yearId and dbo.studentApplnEntriesDBO.applicationNo =:applnNo) or"
				+" (dbo.erpAcademicYearDBO.id =:yearId and dbo.studentDBO.registerNo =:registerNo))";
		if(!Utils.isNullOrEmpty(dto.getId())){
			str += " and dbo.id != :id";	
		}
		String finalStr = str;
		List<HostelApplicationDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<HostelApplicationDBO> query = s.createQuery(finalStr, HostelApplicationDBO.class);
			if(!Utils.isNullOrEmpty(dto.getId()))	{
				query.setParameter("id", Integer.parseInt(dto.getId()));	
			}
			query.setParameter("yearId", Integer.parseInt(dto.getAcademicYear().getValue()));
			if(!Utils.isNullOrEmpty(dto.getStudent())) {
				query.setParameter("registerNo", dto.getStudent().getRegisterNo());
			}
			if(!Utils.isNullOrEmpty(dto.getStudentApplnEntriesDTO())) {
				query.setParameter("applnNo", Integer.parseInt(dto.getStudentApplnEntriesDTO().getApplicationNumber()));
			}
			return query.getResultList();
		}).await().indefinitely();
		return Utils.isNullOrEmpty(list) ? false : true;
	}

	public Mono<Boolean> delete(int id, Integer userId) {
		sessionFactory.withTransaction((session, tx) -> session.find(HostelApplicationDBO.class, id)
				.chain(bo -> session.fetch(bo.getHostelApplicationRoomTypePreferenceDBO())
						.invoke(subDbo -> {
							subDbo.forEach(dbos -> {
								dbos.setRecordStatus('D');
								dbos.setModifiedUsersId(userId);
							});
							bo.setRecordStatus('D');
							bo.setModifiedUsersId(userId);						
						}))).await().indefinitely();
		return Mono.just(Boolean.TRUE);
	}

	public boolean checkIsStudentPresent(Integer studentId, String yearId, String hostelId) {
		String str = " select dbo from HostelAdmissionsDBO dbo"
				+" where dbo.studentDBO.id =:studentId and dbo.erpAcademicYearDBO.id =:yearId-1 and"
				+" dbo.hostelDBO.id =:hostelId and dbo.erpStatusDBO.statusCode ='HOSTEL_CHECK_IN' and dbo.recordStatus ='A'";
		List<HostelAdmissionsDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<HostelAdmissionsDBO> query = s.createQuery(str, HostelAdmissionsDBO.class);
			query.setParameter("studentId", studentId);
			query.setParameter("yearId", Integer.parseInt(yearId));
			query.setParameter("hostelId", Integer.parseInt(hostelId));
			return query.getResultList();
		}).await().indefinitely();
		return Utils.isNullOrEmpty(list) ? false : true;
	}

	public boolean getStatusByHostel(String registerNo, String yearId, String hostelId, String applicationNo) {
		String str = " select dbo from HostelAdmissionsDBO dbo"
				+" where dbo.erpAcademicYearDBO.id =:yearId-1 and"
				+" dbo.hostelDBO.id =:hostelId and dbo.erpStatusDBO.statusCode ='HOSTEL_CHECK_IN' and dbo.recordStatus ='A'"; 
		if(!Utils.isNullOrEmpty(registerNo)) { 
			str+= " and dbo.studentDBO.registerNo =:registerNo";
		}
		if(!Utils.isNullOrEmpty(applicationNo)) {
			str+= " and dbo.studentApplnEntriesDBO.applicationNo =:applicationNo";
		}
		String query1 = str;
		List<HostelAdmissionsDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<HostelAdmissionsDBO> query = s.createQuery(query1, HostelAdmissionsDBO.class);
			if(!Utils.isNullOrEmpty(registerNo)) {
				query.setParameter("registerNo", registerNo);
			}
			if(!Utils.isNullOrEmpty(applicationNo)) {
				query.setParameter("applicationNo", Integer.parseInt(applicationNo));	
			}
			query.setParameter("yearId", Integer.parseInt(yearId));
			query.setParameter("hostelId", Integer.parseInt(hostelId));
			return query.getResultList();
		}).await().indefinitely();
		return Utils.isNullOrEmpty(list) ? false : true;
	}

	public Mono<List<Tuple>> getHostelDataByRegNo(String yearId, String registerNo) {
		String str = " select distinct student.student_id as student_id, student.student_name as student_name, student.register_no as register_no,"
				+" erp_campus_programme_mapping.erp_campus_id as erp_campus_id, erp_campus_programme_mapping.erp_programme_id as erp_programme_id,"
				+" erp_campus.campus_name as campus_name, erp_programme.programme_name as programme_name, student.erp_gender_id as erp_gender_id,"
				+" student_appln_entries.application_no as application_no, student_appln_entries.applicant_name as applicant_name, student_appln_entries.student_appln_entries_id as student_appln_entries_id, "
				+" erp_campus_programme_mapping.erp_campus_programme_mapping_id as erp_campus_programme_mapping_id, aca_class.class_name as class_name,"
				+" hostel_admissions.hostel_id as hostel_id, hostel.hostel_name as hostel_name, student.admitted_year_id as admitted_year_id from student"
				+" inner join student_appln_entries ON student_appln_entries.student_appln_entries_id = student.student_appln_entries_id and student_appln_entries.record_status ='A'"
				+" inner join erp_campus_programme_mapping ON erp_campus_programme_mapping.erp_campus_programme_mapping_id = student.erp_campus_programme_mapping_id"
				+" inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id and erp_campus.record_status ='A'"
				+" inner join erp_programme ON erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id and erp_programme.record_status ='A'"
				+" inner join aca_class ON aca_class.aca_class_id = student.aca_class_id and aca_class.record_status ='A'"
				+" left join hostel_admissions on hostel_admissions.student_id = student.student_id"
				+" left join hostel on hostel.hostel_id = hostel_admissions.hostel_id and hostel.record_status ='A'"
				+" where student.register_no =:registerNo and student.admitted_year_id =:yearId";
		return Mono.just(sessionFactory.withSession(q -> q.createNativeQuery(str, Tuple.class)
				.setParameter("yearId", Integer.parseInt(yearId)).setParameter("registerNo", registerNo)
				.getResultList()).await().indefinitely());
	}

	public Mono<List<Tuple>> getHostelDataByApplnNo(String yearId, String applicationNo) {
		String str = " select distinct student_appln_entries.student_appln_entries_id as student_appln_entries_id, student_appln_entries.application_no as application_no,"
				+" student_appln_entries.applicant_name as applicant_name, student_appln_entries.erp_campus_programme_mapping_id as erp_campus_programme_mapping_id,"
				+" erp_campus.campus_name as campus_name, erp_programme.programme_name as programme_name, student_appln_entries.applied_academic_year_id as admitted_year_id,"
				+" student.student_id as student_id, student.student_name as student_name, student.register_no as register_no, aca_class.class_name as class_name,"
				+" student_appln_entries.erp_gender_id as erp_gender_id, hostel_admissions.hostel_id as hostel_id, hostel.hostel_name as hostel_name from student_appln_entries"
				+" left join student on student.student_appln_entries_id = student_appln_entries.student_appln_entries_id and student.record_status ='A'"
				+" inner join erp_campus_programme_mapping ON erp_campus_programme_mapping.erp_campus_programme_mapping_id = student_appln_entries.erp_campus_programme_mapping_id"
				+" and erp_campus_programme_mapping.record_status ='A'"
				+" inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id and erp_campus.record_status ='A'"
				+" inner join erp_programme ON erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id and erp_programme.record_status ='A'"
				+" left join aca_class ON aca_class.aca_class_id = student.aca_class_id and aca_class.record_status ='A'"
				+" left join hostel_admissions on hostel_admissions.student_id = student.student_id"
				+" left join hostel on hostel.hostel_id = hostel_admissions.hostel_id and hostel.record_status ='A'"
				+" where student_appln_entries.application_no =:applicationNo and student_appln_entries.applied_academic_year_id =:yearId";
		return Mono.just(sessionFactory.withSession(s -> s.createNativeQuery(str, Tuple.class).setParameter("yearId", Integer.parseInt(yearId)).setParameter("applicationNo", applicationNo)
				.getResultList()).await().indefinitely());
	}

	public Mono<StudentApplnEntriesDBO> getHostelDataByRegNoOrApplnNo(String yearId, String registerNo, String applicationNo) {
		String str = "select dbo from StudentApplnEntriesDBO dbo "
				+ " left join StudentDBO bo on dbo.id = bo.studentApplnEntriesDBO.id and bo.recordStatus = 'A'"
				+ " where dbo.recordStatus = 'A'";
		if(!Utils.isNullOrEmpty(applicationNo)) { 
			str+= " and dbo.applicationNo =:applicationNo and dbo.appliedAcademicYear.id =:yearId";
		}
		if(!Utils.isNullOrEmpty(registerNo)) { 
			str+= " and bo.registerNo =:registerNo and bo.admittedYearId.id =:yearId";
		}
		String query1 = str;
		Mono<StudentApplnEntriesDBO> result =  Mono.fromFuture(sessionFactory.withSession(s -> {
			Mutiny.Query<StudentApplnEntriesDBO> query = s.createQuery(query1, StudentApplnEntriesDBO.class);
			if(!Utils.isNullOrEmpty(applicationNo)) {
				query.setParameter("applicationNo", Integer.parseInt(applicationNo));
			}
			if(!Utils.isNullOrEmpty(registerNo)) {
				query.setParameter("registerNo", Integer.parseInt(registerNo));	
			}
			query.setParameter("yearId", Integer.parseInt(yearId));
			return query.getSingleResultOrNull();
		}).subscribeAsCompletionStage());
		return result;
	}

	public Boolean duplicateCheck(String yearId, String registerNo, String applnNo) {
		String str = " select dbo from HostelApplicationDBO dbo"
				+" where dbo.recordStatus ='A'";
		if(!Utils.isNullOrEmpty(applnNo)) {
			str+= " and dbo.erpAcademicYearDBO.id =:yearId and dbo.studentApplnEntriesDBO.applicationNo =:applnNo";
		}
		if(!Utils.isNullOrEmpty(registerNo)) {
			str+= " and dbo.erpAcademicYearDBO.id =:yearId and dbo.studentDBO.registerNo =:registerNo";
		}
		String finalStr = str;
		List<HostelApplicationDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<HostelApplicationDBO> query = s.createQuery(finalStr, HostelApplicationDBO.class);
			query.setParameter("yearId", Integer.parseInt(yearId));
			if(!Utils.isNullOrEmpty(registerNo)) {
				query.setParameter("registerNo", registerNo);
			}
			if(!Utils.isNullOrEmpty(applnNo)) {
				query.setParameter("applnNo", Integer.parseInt(applnNo));
			}
			return query.getResultList();
		}).await().indefinitely();
		return Utils.isNullOrEmpty(list) ? false : true;
     }
}



