package com.christ.erp.services.transactions.hostel.application;

import java.util.List;
import javax.persistence.Tuple;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelApplicationDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelDisciplinaryActionsTypeDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelRoomTypeDBO;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;
import com.christ.erp.services.transactions.common.CommonApiTransaction;

@Repository

public class SelectionProcessTransaction {
	@Autowired
	private Mutiny.SessionFactory sessionFactory;
	
	@Autowired
	private CommonApiTransaction commonApiTransaction;

	public List<HostelApplicationDBO> getGridData(String yearId, String hostelId) {
		ErpAcademicYearDBO currYear = commonApiTransaction.getCurrentAdmissionYear(); 
		String str =  " select distinct dbo from HostelApplicationDBO dbo"
				+" inner join fetch dbo.hostelApplicationRoomTypePreferenceDBO dbos "
				+" left join HostelAdmissionsDBO hdbo on hdbo.hostelApplicationDBO.id = dbo.id"
				+" where (dbo.hostelApplicationCurrentProcessStatus.processCode = 'HOSTEL_APPLICATION_NEW_APPLIED' or dbo.hostelApplicationCurrentProcessStatus.processCode = 'HOSTEL_APPLICATION_RENEWAL_APPLIED'"
				+" or dbo.hostelApplicationCurrentProcessStatus.processCode = 'HOSTEL_APPLICATION_REVIEWED' or dbo.hostelApplicationCurrentProcessStatus.processCode ='HOSTEL_APPLICATION_NOT_SELECTED_UPLOADED' or dbo.hostelApplicationCurrentProcessStatus.processCode ='HOSTEL_APPLICATION_SELECTED_UPLOADED')"
				+" and dbo.erpAcademicYearDBO.id =:yearId and dbo.hostelDBO.id =:hostelId and dbo.recordStatus ='A' and dbos.recordStatus ='A' order by dbo.applicationNo ASC ";
		String finalStr = str;
		List<HostelApplicationDBO> list = sessionFactory.withSession(s-> {
			Mutiny.Query<HostelApplicationDBO> query = s.createQuery(finalStr, HostelApplicationDBO.class);
			if(!Utils.isNullOrEmpty(yearId)) {
				query.setParameter("yearId", Integer.parseInt(yearId));
			} else {
				query.setParameter("yearId",currYear.getId());
			}
			query.setParameter("hostelId",Integer.parseInt(hostelId));
			return query.getResultList();
		}).await().indefinitely();
		return list;	
	}

	public void merge(List<Object> dbo) {
		sessionFactory.withTransaction((session, tx) -> session.mergeAll(dbo.toArray())).await().indefinitely();
	}

	public List<HostelApplicationDBO> getData(List<Integer> applicationIds) {
		String str = " select dbo from HostelApplicationDBO dbo where dbo.recordStatus ='A'and dbo.id IN(:applicationIds) ";
		String finalStr = str;
		List<HostelApplicationDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<HostelApplicationDBO> query = s.createQuery(finalStr, HostelApplicationDBO.class);
			query.setParameter("applicationIds", applicationIds);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public void update(List<Object> dbo) {
		sessionFactory.withTransaction((session, tx) -> session.mergeAll(dbo.toArray())).await().indefinitely();
	}

	public List<Integer> checkIsStudents(List<Integer> applicationNo) {
		String query = " select dbo.id from StudentApplnEntriesDBO dbo "
				+" where dbo.recordStatus = 'A' and dbo.applicationNo in (:applicationNo) ";
		return  sessionFactory.withSession(s->s.createQuery(query,Integer.class).setParameter("applicationNo", applicationNo).getResultList()).await().indefinitely();
	}

	public List<HostelRoomTypeDBO> getRoomTypeForStudent(String hostelId) {
		String str = " from HostelRoomTypeDBO dbo where dbo.recordStatus ='A' and dbo.hostelDBO.id = :hostelId and dbo.roomTypeCategory = 'Student' order by dbo.id";
		return sessionFactory.withSession(s -> s.createQuery(str, HostelRoomTypeDBO.class).setParameter("hostelId", Integer.parseInt(hostelId)).getResultList()).await().indefinitely();
	}

	public Integer getAvailableSeats(String hostelId, String yearId, int roomTypeId) {
		String str = "select hostel_seat_availability_details.available_seats from hostel_seat_availability "
				+" inner join hostel_seat_availability_details on hostel_seat_availability_details.hostel_seat_availability_id = hostel_seat_availability.hostel_seat_availability_id and hostel_seat_availability_details.record_status ='A'"
				+" where hostel_seat_availability.hostel_id =:hostelId and hostel_seat_availability_details.hostel_room_type_id =:roomTypeId and  hostel_seat_availability.erp_academic_year_id =:yearId"
				+" and hostel_seat_availability_details.record_status= 'A'";
		return sessionFactory.withSession(s -> s.createNativeQuery(str, Integer.class).setParameter("hostelId", Integer.parseInt(hostelId)).setParameter("yearId", Integer.parseInt(yearId)).setParameter("roomTypeId", roomTypeId).getSingleResultOrNull()).await().indefinitely();			
	}

	public void update(HostelApplicationDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.merge(dbo)).await().indefinitely();
	}

	public HostelApplicationDBO getStudentReviewedData(int id) {
		String str = " select dbo from HostelApplicationDBO dbo "
				+" where dbo.id =:id and dbo.recordStatus = 'A'";
		return sessionFactory.withSession(s-> s.createQuery(str, HostelApplicationDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();	
	}

	public List<HostelDisciplinaryActionsTypeDBO> getDisciplpinaryCount(int yearId, int admissionId) {
		String str = " select dbo from HostelDisciplinaryActionsTypeDBO dbo "
				+" left join fetch dbo.hostelDisciplinaryActionsDBOSet as dbos "
				+" where dbos.academicYearDBO.id =:yearId and dbos.hostelAdmissionsDBO.id =:admissionId and dbo.recordStatus ='A'";
		return sessionFactory.withSession(s -> s.createQuery(str, HostelDisciplinaryActionsTypeDBO.class).setParameter("yearId", yearId).setParameter("admissionId", admissionId).getResultList()).await().indefinitely();		
	}

	public boolean checkIsStudent(String applnId) {
		String str = " select dbo from StudentDBO dbo"
				+" where dbo.recordStatus ='A' and dbo.studentApplnEntriesId =:applnId";
		List<StudentDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<StudentDBO> query = s.createQuery(str, StudentDBO.class);
			query.setParameter("applnId", Integer.parseInt(applnId));
			return query.getResultList();
		}).await().indefinitely();
		return Utils.isNullOrEmpty(list) ? false : true;
	}

	public HostelAdmissionsDBO getAdmissionId(String applicationId) {
		String str = " select dbo from HostelAdmissionsDBO dbo where dbo.hostelApplicationDBO.id =:applicationId and dbo.recordStatus ='A'";
		return sessionFactory.withSession(s -> s.createQuery(str, HostelAdmissionsDBO.class).setParameter("applicationId", Integer.parseInt(applicationId)).getSingleResultOrNull()).await().indefinitely();	
	}

	public List<HostelApplicationDBO> getHostelApplicantDetails(List<Integer> applicationNo) {
		String str = " select dbo from HostelApplicationDBO dbo "
				+" where dbo.studentApplnEntriesDBO.applicationNo in (:applicationNo) and dbo.recordStatus ='A' ";
		return sessionFactory.withSession(s->s.createQuery(str,HostelApplicationDBO.class).setParameter("applicationNo", applicationNo).getResultList()).await().indefinitely();
	}

	public List<StudentDBO> checkEntriesId(List<Integer> entriesId) {
		String str = " select dbo from StudentDBO dbo "
				+" where dbo.recordStatus ='A' and dbo.studentApplnEntriesDBO.id IN (:entriesId)"
				+" and dbo.recordStatus ='A' ";
		return sessionFactory.withSession(s->s.createQuery(str,StudentDBO.class).setParameter("entriesId", entriesId).getResultList()).await().indefinitely();
	}

	public List<Tuple> getProgrammeLevel(String yearId, String hostelId) {
		String str = "select erp_programme_level.erp_programme_level_id, erp_programme_level.programme_level, "
				+" count(student_appln_entries.student_appln_entries_id) as student_count from student_appln_entries "
				+" inner join erp_work_flow_process ON erp_work_flow_process.erp_work_flow_process_id = student_appln_entries.application_current_process_status"
				+" and erp_work_flow_process.record_status ='A'"
				+" inner join erp_resident_category ON erp_resident_category.erp_resident_category_id = student_appln_entries.erp_resident_category_id"
				+" and erp_resident_category.record_status ='A'"
				+" inner join hostel_programme_details on hostel_programme_details.erp_campus_programme_mapping_id = student_appln_entries.erp_campus_programme_mapping_id"
				+" and hostel_programme_details.record_status='A'"
				+" inner join hostel on hostel.hostel_id = hostel_programme_details.hostel_id and hostel.record_status ='A'"
				+" and hostel.record_status ='A'"
				+" inner join erp_campus_programme_mapping ON erp_campus_programme_mapping.erp_campus_programme_mapping_id = hostel_programme_details.erp_campus_programme_mapping_id"
				+" and erp_campus_programme_mapping.record_status ='A'"
				+" inner join erp_programme ON erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id "
				+" and erp_programme.record_status ='A'"
				+" inner join erp_programme_degree ON erp_programme_degree.erp_programme_degree_id = erp_programme.erp_programme_degree_id "
				+" and erp_programme_degree.record_status ='A'"
				+" inner join erp_programme_level ON erp_programme_level.erp_programme_level_id = erp_programme_degree.erp_programme_level_id "
				+" and erp_programme_level.record_status ='A'"
				+" where erp_work_flow_process.process_code = 'ADM_APPLN_ADMITTED' "
				+" and hostel.hostel_id =:hostelId"
				+" and student_appln_entries.applied_academic_year_id =:yearId "
				+" group by erp_programme_degree.erp_programme_level_id ";
		return sessionFactory.withSession(s -> s.createNativeQuery(str, Tuple.class).setParameter("yearId", Integer.parseInt(yearId)).setParameter("hostelId", Integer.parseInt(hostelId)).getResultList()).await().indefinitely(); 			
	}

	public List<Tuple> getProgrammeCategory(String yearId, String hostelId, String levelId) {
		String str = " select erp_resident_category.erp_resident_category_id, erp_resident_category.resident_category_name as resident_category_name, "
				+" count(student_appln_entries.student_appln_entries_id) as student_count, erp_programme.erp_programme_id as erp_programme_id"
				+" erp_programme.programme_name as programme_name"
				+" from student_appln_entries"
				+" inner join erp_work_flow_process ON erp_work_flow_process.erp_work_flow_process_id = student_appln_entries.application_current_process_status"
				+" and erp_work_flow_process.record_status ='A'"
				+" inner join erp_resident_category ON erp_resident_category.erp_resident_category_id = student_appln_entries.erp_resident_category_id"
				+" and erp_resident_category.record_status ='A'"
				+" inner join hostel_programme_details on hostel_programme_details.erp_campus_programme_mapping_id = student_appln_entries.erp_campus_programme_mapping_id"
				+" and hostel_programme_details.record_status='A'"
				+" inner join erp_campus_programme_mapping ON erp_campus_programme_mapping.erp_campus_programme_mapping_id = hostel_programme_details.erp_campus_programme_mapping_id"
				+" and erp_campus_programme_mapping.record_status ='A'"
				+" inner join erp_programme ON erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id "
				+" and erp_programme.record_status ='A'"
				+" inner join erp_programme_degree ON erp_programme_degree.erp_programme_degree_id = erp_programme.erp_programme_degree_id "
				+" and erp_programme_degree.record_status ='A'"
				+" inner join erp_programme_level ON erp_programme_level.erp_programme_level_id = erp_programme_degree.erp_programme_level_id "
				+" and erp_programme_level.record_status ='A'"
				+" inner join hostel on hostel.hostel_id = hostel_programme_details.hostel_id "
				+" and hostel.record_status ='A'"
				+" where erp_work_flow_process.process_code = 'ADM_APPLN_ADMITTED'"
				+" and hostel.hostel_id =:hostelId and erp_programme_level.erp_programme_level_id =:levelId "
				+" and student_appln_entries.applied_academic_year_id =:yearId "
				+" group by erp_resident_category.erp_resident_category_id, erp_programme.erp_programme_id ";
		return sessionFactory.withSession(s -> s.createNativeQuery(str, Tuple.class).setParameter("yearId", Integer.parseInt(yearId)).setParameter("hostelId", Integer.parseInt(hostelId)).setParameter("levelId", Integer.parseInt(levelId)).getResultList()).await().indefinitely(); 			
	}
}

