package com.christ.erp.services.transactions.admission.applicationprocess;

import java.util.List;
import javax.persistence.Tuple;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dto.admission.applicationprocess.FinalResultEntryDTO;

@Repository
public class FinalResultEntryTransaction {
	
	@Autowired
	private Mutiny.SessionFactory sessionFactory;

//	public Mono<List<Tuple>> getActiveProgrammeByYearValue(String yearValue) {
//		String queryString =" select distinct erp_campus_programme_mapping.erp_programme_id ,erp_programme.programme_name from erp_campus_programme_mapping"
//				+ " inner join erp_programme on erp_campus_programme_mapping.erp_programme_id = erp_programme.erp_programme_id"
//				+ " where  erp_campus_programme_mapping.record_status = 'A' and erp_programme.record_status ='A' and :yearValue >= programme_commence_year and :yearValue < programme_inactivated_year or programme_inactivated_year = 0 or programme_inactivated_year is null order by erp_programme.programme_name ";
//		return  Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(queryString, Tuple.class).setParameter("yearValue",Integer.parseInt(yearValue)).getResultList()).subscribeAsCompletionStage());
//	}
	
	public List<ErpCampusProgrammeMappingDBO> getActiveProgrammeByYearValue(String yearValue) {
		String queryString = " select distinct dbo from ErpCampusProgrammeMappingDBO dbo"
				+ " where  dbo.recordStatus = 'A' and dbo.erpProgrammeDBO.recordStatus = 'A' "
				+ " and :yearValue >= dbo.programmeCommenceYear and :yearValue < dbo.programmeInactivatedYear or (dbo.programmeInactivatedYear = 0 or dbo.programmeInactivatedYear is null) ";
		return  sessionFactory.withSession(s -> s.createQuery(queryString, ErpCampusProgrammeMappingDBO.class)
				.setParameter("yearValue", Integer.parseInt(yearValue)).getResultList()).await().indefinitely();
	}
	
	
	public StudentApplnEntriesDBO applicationCheck(FinalResultEntryDTO dto) {
		String queryString = " select dbo from StudentApplnEntriesDBO dbo"
				+ " where  dbo.recordStatus = 'A' and"
				+ " dbo.appliedAcademicYear.id = :yearId  and dbo.applicationNo = :application"
				+ " and (applicationCurrentProcessStatus.processOrder >= 19 or applicationCurrentProcessStatus.processCode in ( 'ADM_APPLN_SELECTED','ADM_APPLN_SELECTED_UPLOADED'))";
		return  sessionFactory.withSession(s -> s.createQuery(queryString, StudentApplnEntriesDBO.class).setParameter("yearId",Integer.parseInt(dto.getAcademicYear().getValue()))
				.setParameter("application", dto.getApplicationNo()).getSingleResultOrNull()).await().indefinitely();
	}
	
	public List<Tuple> getGridData(FinalResultEntryDTO dto) {
		String queryString = " select distinct  student_appln_entries.application_no,student_appln_entries.applicant_name,"
				+ " erp_gender.gender_name, student_appln_entries.total_weightage,erp_resident_category.resident_category_name,"
				+ " concat(erp_programme.programme_name, '(', ifnull(erp_campus.campus_name, erp_location.location_name), ')')  as appliedProgramme,"
				+ " erp_work_flow_process.application_status_display_text as displayText , erp_work_flow_process.process_code as processCode "
				+ " from  student_appln_entries"
				+ " inner join  erp_gender on student_appln_entries.erp_gender_id = erp_gender.erp_gender_id "
				+ " inner join erp_resident_category on student_appln_entries.erp_resident_category_id = erp_resident_category.erp_resident_category_id "
				+ " inner join erp_campus_programme_mapping on erp_campus_programme_mapping.erp_campus_programme_mapping_id = student_appln_entries.erp_campus_programme_mapping_id"
				+ " inner join erp_programme ON erp_programme.erp_programme_id = erp_campus_programme_mapping.erp_programme_id "
				+ " left join erp_location ON erp_location.erp_location_id = erp_campus_programme_mapping.erp_location_id "
				+ " left join erp_campus ON erp_campus.erp_campus_id = erp_campus_programme_mapping.erp_campus_id "
				+ " inner join erp_work_flow_process on erp_work_flow_process.erp_work_flow_process_id = student_appln_entries.application_current_process_status "
				+ " where student_appln_entries.record_status = 'A' and  erp_gender.record_status = 'A'"
				+ " and erp_resident_category.record_status ='A' and erp_campus_programme_mapping.record_status ='A' "
				+ " and  erp_programme.record_status = 'A'and student_appln_entries.applied_academic_year_id = :yearId" ;
		if(!Utils.isNullOrEmpty(dto.getApplicationNo())) {
			queryString += " and student_appln_entries.application_no = :application_no";
		} else if(!Utils.isNullOrEmpty(dto.getApplicantName())) {
			queryString += " and student_appln_entries.applicant_name = :applicant_name"
					+ " and erp_work_flow_process.process_order < 19 and erp_work_flow_process.process_code not in ( 'ADM_APPLN_SELECTED','ADM_APPLN_SELECTED_UPLOADED')";
		} else {
			queryString += "  and  student_appln_entries.erp_campus_programme_mapping_id = :cpmId"
					+ " and erp_work_flow_process.process_order < 19 and erp_work_flow_process.process_code not in ('ADM_APPLN_SELECTED','ADM_APPLN_SELECTED_UPLOADED') "
					+ " order by student_appln_entries.application_no";
		}
		String finalquery = queryString;
		List<Tuple> list = sessionFactory.withSession(s -> { Mutiny.Query<Tuple> query = s.createNativeQuery(finalquery, Tuple.class);
		if(!Utils.isNullOrEmpty(dto.getApplicationNo())) {
			query.setParameter("yearId", Integer.parseInt(dto.getAcademicYear().getValue()));
			query.setParameter("application_no",dto.getApplicationNo());
		} else if(!Utils.isNullOrEmpty(dto.getApplicantName())) {
			query.setParameter("yearId", Integer.parseInt(dto.getAcademicYear().getValue()));
			query.setParameter("applicant_name", dto.getApplicantName());
		} else {
			query.setParameter("yearId", Integer.parseInt(dto.getAcademicYear().value));
			query.setParameter("cpmId",Integer.parseInt(dto.getProgramme().getValue()));
		}
		return  query.getResultList();
		}).await().indefinitely();
		return list;
	}
	
}
