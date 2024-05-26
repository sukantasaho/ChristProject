package com.christ.erp.services.transactions.admission.applicationprocess;

import java.util.List;
import javax.persistence.Tuple;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnVerificationRemarksDBO;
import reactor.core.publisher.Mono;

@Repository
public class MarksVerificationTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public Tuple getStudentDetails(int applicationNumber) {
		String queryString = " select student_appln_entries.applicant_name as applicant_name, erp_programme.erp_programme_id as erp_programme_id,"
			    + " erp_programme.programme_name as programme_name, erp_location.location_name as location_name, "
				+ " erp_campus.campus_name as campus_name, student_appln_entries.application_verification_status as application_verification_status from student_appln_entries"
				+ " inner join erp_campus_programme_mapping ON erp_campus_programme_mapping.erp_campus_programme_mapping_id = student_appln_entries.erp_campus_programme_mapping_id"
				+ " left join erp_programme  ON erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id and erp_programme.record_status='A'"
				+ " left join erp_campus  ON erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id and erp_campus.record_status = 'A'"
				+ " left join erp_location  ON erp_location.erp_location_id = erp_campus_programme_mapping.erp_location_id and erp_location.record_status ='A'"
				+ " where student_appln_entries.application_no =:applicationNumber and student_appln_entries.record_status = 'A'";
		return sessionFactory.withSession(s -> s.createNativeQuery(queryString,Tuple.class).setParameter("applicationNumber", applicationNumber).getSingleResultOrNull()).await().indefinitely();
	}

	public Mono<List<StudentApplnVerificationRemarksDBO>> getRemarkDetails() {
		String str = " select dbo from StudentApplnVerificationRemarksDBO dbo where dbo.recordStatus ='A'";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createQuery(str, StudentApplnVerificationRemarksDBO.class).getResultList()).subscribeAsCompletionStage());
	}

	public StudentApplnEntriesDBO getEducationalDetails(int applicationNumber)  {
		String str = "select dbo from StudentApplnEntriesDBO dbo"
				+" left join fetch dbo.studentApplnPrerequisiteDBO prerequisiteDBO"
				+" left join fetch dbo.studentEducationalDetailsDBOS as e"
				+" left join fetch e.studentEducationalMarkDetailsDBOSet as ecm"
				+" left join fetch e.studentEducationalDetailsDocumentsDBOSet as edc"
				+" where dbo.applicationNo =:applicationNumber and dbo.recordStatus = 'A'";
		return sessionFactory.withSession(s-> s.createQuery(str, StudentApplnEntriesDBO.class).setParameter("applicationNumber", applicationNumber).getSingleResultOrNull()).await().indefinitely();	
	}

	public void merge(List<Object> dbo) {
		sessionFactory.withTransaction((session, txt) -> session.mergeAll(dbo.toArray())).subscribeAsCompletionStage();
	}

	public StudentApplnEntriesDBO getEmployeeDetailsForUpdate(int id) {
		String str = " select distinct dbo from StudentApplnEntriesDBO dbo"
				+" left join fetch dbo.studentApplnPrerequisiteDBO prerequisiteDBO"
				+" left join fetch dbo.studentEducationalDetailsDBOS as e"
				+" left join fetch e.studentEducationalMarkDetailsDBOSet as ecm"
				+" left join fetch e.studentEducationalDetailsDocumentsDBOSet as edc"
				+" where dbo.id =:id and dbo.recordStatus='A'";
		return sessionFactory.withSession(s-> s.createQuery(str, StudentApplnEntriesDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();	
	}

	public Integer getPrerequisiteId(String year, String month, String id) {
		String str = " select adm_prerequisite_settings_period.adm_prerequisite_settings_period_id from adm_prerequisite_settings_period"
				+" inner join adm_prerequisite_settings ON adm_prerequisite_settings.adm_prerequisite_settings_id = adm_prerequisite_settings_period.adm_prerequisite_settings_id and adm_prerequisite_settings.record_status ='A'"
				+" inner join adm_prerequisite_exam ON adm_prerequisite_exam.adm_prerequisite_exam_id = adm_prerequisite_settings.adm_prerequisite_exam_id and adm_prerequisite_exam.record_status ='A'"
				+" where adm_prerequisite_settings_period.exam_year =:year and adm_prerequisite_settings_period.exam_month =:month and adm_prerequisite_settings.adm_prerequisite_exam_id=:id and adm_prerequisite_settings_period.record_status ='A'";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Integer.class).setParameter("year", year).setParameter("month", month).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();		
	}
}
