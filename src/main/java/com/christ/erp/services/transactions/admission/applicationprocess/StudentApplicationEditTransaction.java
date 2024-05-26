package com.christ.erp.services.transactions.admission.applicationprocess;

import java.util.List;
import javax.persistence.Tuple;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import reactor.core.publisher.Mono;

@Repository
public class StudentApplicationEditTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public Mono<List<Tuple>> getApplicantListCard(String yearId, String applicationNoOrName, String programmeId, Boolean isNumeric) {
		String query = "Select student_appln_entries.student_appln_entries_id as studentId ,student_appln_entries.application_no as applicationNumber, student_appln_entries.applicant_name as name,erp_programme.erp_programme_id as programmeId,"
				+ " erp_programme.programme_name as programmeName ,student_personal_data_addtnl.profile_photo_url as photoUrl"
				+ " from student_appln_entries"
				+ " inner join student_personal_data ON student_personal_data.student_personal_data_id = student_appln_entries.student_personal_data_id"
				+ " inner join student_personal_data_addtnl ON student_personal_data_addtnl.student_personal_data_addtnl_id = student_personal_data.student_personal_data_addtnl_id"
				+ " inner join erp_campus_programme_mapping ON erp_campus_programme_mapping.erp_campus_programme_mapping_id = student_appln_entries.erp_campus_programme_mapping_id"
				+ " inner join erp_programme on erp_campus_programme_mapping.erp_programme_id = erp_programme.erp_programme_id"
				+ " where student_appln_entries.record_status = 'A' and student_personal_data.record_status = 'A' and student_personal_data_addtnl.record_status = 'A'"
				+ " and erp_campus_programme_mapping.record_status = 'A' and erp_programme.record_status = 'A' and"
				+ " student_appln_entries.applied_academic_year_id = :yearId";
		if(!Utils.isNullOrEmpty(applicationNoOrName)) {
			if(isNumeric) {
				query += " and student_appln_entries.application_no = :application_no";
			} else {
				query += " and student_appln_entries.applicant_name = :name";
			}
		}
		if(!Utils.isNullOrEmpty(programmeId)) {
			query += " and erp_campus_programme_mapping.erp_programme_id = :programmeId";
		}
		String finalStr = query;
		Mono<List<Tuple>> list = Mono.fromFuture(sessionFactory.withSession(s -> { Mutiny.Query<Tuple> query1 = s.createNativeQuery(finalStr, Tuple.class);
		query1.setParameter("yearId", Integer.parseInt(yearId));
		if(!Utils.isNullOrEmpty(applicationNoOrName)) {
			if(isNumeric) {
				query1.setParameter("application_no", Integer.parseInt(applicationNoOrName));
			} else {
				query1.setParameter("name",applicationNoOrName);
			}
		}
		if(!Utils.isNullOrEmpty(programmeId)) {
			query1.setParameter("programmeId", Integer.parseInt(programmeId));
		}
		return  query1.getResultList();
		}).subscribeAsCompletionStage());
		return list;

	}

	public StudentApplnEntriesDBO getStudentDetails(Integer studentId) {
		String query =" select distinct dbo from StudentApplnEntriesDBO dbo"
				+ " left join fetch  dbo.studentApplnPreferenceDBOS sapd"
				+ " left join fetch dbo.studentApplnSelectionProcessDatesDBOS saspd"
				+ " left join fetch dbo.accFeeDemandDBOSet afd"
				+ " inner join fetch dbo.studentEducationalDetailsDBOS sed"
				+ " left join fetch sed.studentEducationalMarkDetailsDBOSet semd"
				+ " left join fetch sed.studentEducationalDetailsDocumentsDBOSet sedd"
				+ " left join fetch dbo.studentWorkExperienceDBOS swe"
				+ " left join fetch swe.studentWorkExperienceDocumentDBOSet swed"
				+ " left join fetch dbo.studentExtraCurricularDetailsDBOS secd"
				+ " where dbo.recordStatus = 'A' and dbo.id = :studentId and sed.recordStatus = 'A'"
//				+ " or (sapd is null or sapd.recordStatus = 'A')"
//				+ " or (saspd is null or saspd.recordStatus = 'A')"
//				+ " or (afd is null or afd.recordStatus = 'A')"
//				+ " or (semd is null or semd.recordStatus = 'A')"
//				+ " or (sedd is null or sedd.recordStatus = 'A')"
//				+ " or (swe is null or swed.recordStatus = 'A')"
//				+ " or (swed is null or swed.recordStatus = 'A')"
//				+ " or (secd is null or secd.recordStatus = 'A')";
				+ " and (sapd.recordStatus = 'A' or saspd.recordStatus = 'A' or afd.recordStatus = 'A' or semd.recordStatus = 'A'"
				+ " or sedd.recordStatus = 'A' or swed.recordStatus = 'A' or swed.recordStatus = 'A')";
		return sessionFactory.withSession(s->s.createQuery(query, StudentApplnEntriesDBO.class).setParameter("studentId", studentId).getSingleResultOrNull()).await().indefinitely();
	}

	public void update(StudentApplnEntriesDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.merge(dbo)).subscribeAsCompletionStage();
	}

}
