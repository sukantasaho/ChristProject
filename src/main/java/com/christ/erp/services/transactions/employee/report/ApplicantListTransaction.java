package com.christ.erp.services.transactions.employee.report;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Tuple;
import org.hibernate.reactive.mutiny.Mutiny;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpQualificationLevelDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnAddtnlInfoHeadingDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpEligiblityTestDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.onboarding.EmpApplnEntriesDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnAddtnlInfoEntriesDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnEducationalDetailsDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnLocationPrefDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnPersonalDataDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnWorkExperienceDTO;
import com.christ.erp.services.dto.employee.report.ApplicantListDTO;

@Repository
public class ApplicantListTransaction {
	
	@Autowired
	SessionFactory sessionFactory;

	public  List<Tuple> getApplicantData(ApplicantListDTO dto) {
		String query = "select distinct emp_appln_entries.emp_appln_entries_id as id, emp_appln_entries.application_no as applicationNo,emp_appln_entries.applicant_name as name,"
				+ " erp_gender.gender_name as gender ,emp_appln_entries.personal_email_id as mail,emp_appln_entries.mobile_no_country_code as code,emp_appln_entries.mobile_no as mobile,"
				+ " erp_work_flow_process.applicant_status_display_text as status,"
				+ " url.file_name_unique, url.file_name_original, folder.upload_process_code "
				+ " from emp_appln_entries"
				+ " left join  emp_appln_personal_data on emp_appln_entries.emp_appln_entries_id = emp_appln_personal_data.emp_appln_entries_id and emp_appln_personal_data.record_status = 'A'"
				+ " left join url_access_link url ON emp_appln_personal_data.profile_photo_url_id = url.url_access_link_id"
				+ "	left join url_folder_list folder on url.url_folder_list_id = folder.url_folder_list_id "
				+ " inner join erp_gender ON erp_gender.erp_gender_id = emp_appln_entries.erp_gender_id and erp_gender.record_status = 'A'"
				+ " inner join erp_work_flow_process ON erp_work_flow_process.erp_work_flow_process_id = emp_appln_entries.application_current_process_status and erp_work_flow_process.record_status = 'A'";
		if(!Utils.isNullOrEmpty(dto.getSelectedLocation())) {
			query += " inner join emp_appln_location_pref on emp_appln_location_pref.emp_appln_entries_id = emp_appln_entries.emp_appln_entries_id and emp_appln_location_pref.record_status = 'A'";
			query += " inner join erp_location ON erp_location.erp_location_id = emp_appln_location_pref.erp_location_id and erp_location.record_status = 'A'";
		}
		if(!Utils.isNullOrEmpty(dto.getSelectedSubjectOrCategory())) {
			query += " inner join emp_appln_subj_specialization_pref on emp_appln_subj_specialization_pref.emp_appln_entries_id = emp_appln_entries.emp_appln_entries_id and emp_appln_subj_specialization_pref.record_status = 'A'";
		}
		query += " where emp_appln_entries.record_status = 'A' ";
		if(!Utils.isNullOrEmpty(dto.getSelectedLocation())) {
			query += " and erp_location.erp_location_id in (:location)";
		}
		if(!Utils.isNullOrEmpty(dto.getSelectedCampus())) {
			query += " and emp_appln_entries.erp_campus_id in (:Campus)";
		}
		if(!Utils.isNullOrEmpty(dto.getEmployeeCategory())) {
			query += "  and emp_appln_entries.emp_employee_category_id = :categoryId";
		}
		if(!Utils.isNullOrEmpty(dto.getSelectedSubjectOrCategory())) {
			query += " and emp_appln_subj_specialization_pref.emp_appln_subject_category_id in (:subjectCategory)";
		}
		if(!Utils.isNullOrEmpty(dto.getSelectedSpecialization())) {
			query += " and emp_appln_subj_specialization_pref.emp_appln_subject_category_specialization_id in (:specialization)";
		}
		if(!Utils.isNullOrEmpty(dto.getDesignation())) {
			query += "and emp_appln_entries.emp_designation_id in (:designation)";
		}
		if(!Utils.isNullOrEmpty(dto.getDepartment())) {
			query += "  and emp_appln_entries.shortlisted_department_id in (:department)";
		}
		if(!Utils.isNullOrEmpty(dto.getEmpFromDate())  && !Utils.isNullOrEmpty(dto.getEmpToDate())) {
			query += " and ( date(emp_appln_entries.submission_date_time) >= :fromDate and date(emp_appln_entries.submission_date_time) <= :toDate) ";
		}

		String finalquery = query;
		List<Tuple> list = sessionFactory.withSession(s -> { Mutiny.Query<Tuple> query1 = s.createNativeQuery(finalquery, Tuple.class);	
		if(!Utils.isNullOrEmpty(dto.getSelectedLocation())) {
			Set<Integer> locationIds = new HashSet<Integer>();
			dto.getSelectedLocation().forEach(loc ->{
				locationIds.add(Integer.parseInt(loc.getValue()));
			});
			query1.setParameter("location",locationIds);
		}
		if(!Utils.isNullOrEmpty(dto.getSelectedCampus())) {
			Set<Integer> campusId = new HashSet<Integer>();
			dto.getSelectedCampus().forEach(loc ->{
				campusId.add(Integer.parseInt(loc.getValue()));
			});
			query1.setParameter("Campus",campusId);
		}
		if(!Utils.isNullOrEmpty(dto.getEmployeeCategory())) {
			query1.setParameter("categoryId", Integer.parseInt(dto.getEmployeeCategory().getValue()));
		}
		if(!Utils.isNullOrEmpty(dto.getSelectedSubjectOrCategory())) {
			Set<Integer> subcatIds = new HashSet<Integer>();
			dto.getSelectedSubjectOrCategory().forEach( sub -> {
				subcatIds.add(Integer.parseInt(sub.getValue()));
			});
			query1.setParameter("subjectCategory", subcatIds);
		}
		if(!Utils.isNullOrEmpty(dto.getSelectedSpecialization())) {
			Set<Integer> specialIds = new HashSet<Integer>();
			dto.getSelectedSpecialization().forEach( special -> {
				specialIds.add(Integer.parseInt(special.getValue()));
			});
			query1.setParameter("specialization", specialIds);
		}
		if(!Utils.isNullOrEmpty(dto.getDepartment())) {
			Set<Integer> departmentIds = new HashSet<Integer>();
			dto.getDepartment().forEach(des ->{
				departmentIds.add(Integer.parseInt(des.getValue()));
			});
			query1.setParameter("department",departmentIds);
		}
		if(!Utils.isNullOrEmpty(dto.getDesignation())) {
			Set<Integer> designationIds = new HashSet<Integer>();
			dto.getDesignation().forEach(des ->{
				designationIds.add(Integer.parseInt(des.getValue()));
			});
			query1.setParameter("designation",designationIds);
		}
		if(!Utils.isNullOrEmpty(dto.getEmpFromDate())  && !Utils.isNullOrEmpty(dto.getEmpToDate())) {
			query1.setParameter("fromDate",dto.getEmpFromDate());
			query1.setParameter("toDate",dto.getEmpToDate());
		}

		return  query1.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<EmpApplnEntriesDTO> empDBO(List<Integer> empIds) {
		String query =" select new com.christ.erp.services.dto.employee.onboarding.EmpApplnEntriesDTO"
				+ "(dbo.id,dbo.applicationNo,dbo.applicantName,dbo.personalEmailId,dbo.erpGenderDBO.genderName,dbo.mobileNoCountryCode,dbo.mobileNo,dbo.dob,"
				+ " dbo.empApplnVacancyInformationDBO.vacancyInformationName,dbo.aboutVacancyOthers,dbo.empEmployeeJobCategoryDBO.employeeJobName,dbo.empApplnSubjectCategoryDBO.subjectCategory,"
				+ " dbo.empApplnSubjectCategorySpecializationDBO.subjectCategorySpecializationName,dbo.majorAchievements,dbo.expectedSalary,dbo.isInterviewedBefore,dbo.interviewedBeforeDepartment,"
				+ " dbo.interviewedBeforeYear,dbo.interviewedBeforeApplicationNo,dbo.interviewedBeforeSubject,dbo.isResearchExperiencePresent"
				+ ")"
				+ " from EmpApplnEntriesDBO dbo"
				+ " left join dbo.erpGenderDBO"
				+ " left join dbo.empApplnVacancyInformationDBO eavi "
				+ " left join dbo.empEmployeeJobCategoryDBO ejc"
				+ " left join dbo.empApplnSubjectCategoryDBO easc"
				+ " left join dbo.empApplnSubjectCategorySpecializationDBO escs"
				+ " where dbo.recordStatus  = 'A' and dbo.id in (:empIds)";
		return sessionFactory.withSession(s->s.createQuery(query,EmpApplnEntriesDTO.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}
	
	public List<EmpApplnPersonalDataDTO> getPersonalData(List<Integer> empIds) {
		String query ="select new com.christ.erp.services.dto.employee.recruitment.EmpApplnPersonalDataDTO"
				+ " (dbo.id,dbo.empApplnEntriesDBO.id,dbo.alternateNo,dbo.erpBloodGroupDBO.bloodGroupName,dbo.isDifferentlyAbled,"
				+ " dbo.erpDifferentlyAbledDBO.differentlyAbledName,dbo.erpMaritalStatusDBO.maritalStatusName,"
				+ " dbo.erpReligionDBO.religionName,dbo.erpReservationCategoryDBO.reservationCategoryName,dbo.erpCountryDBO.countryName,dbo.aadharNo,dbo.passportNo,dbo.currentAddressLine1,dbo.currentAddressLine2,"
				+ " dbo.currentCountry.countryName,dbo.currentState.stateName,dbo.currentStateOthers,dbo.currentCity.cityName,dbo.currentCityOthers,dbo.currentPincode,dbo.permanentAddressLine1,dbo.permanentAddressLine2,"
				+ " dbo.permanentCountry.countryName,dbo.permanentState.stateName,dbo.permanentStateOthers,dbo.permanentCity.cityName,dbo.permanentCityOthers,dbo.permanentPincode,"
				+ " dbo.erpQualificationLevelDBO.qualificationLevelName,dbo.orcidNo,dbo.vidwanNo,dbo.scopusNo,dbo.hIndexNo"
				+ ")"
				+ " from EmpApplnPersonalDataDBO dbo"
				+ " left join dbo.erpBloodGroupDBO"
				+ " left join dbo.erpDifferentlyAbledDBO"
				+ " left join dbo.erpMaritalStatusDBO"
				+ " left join dbo.erpReligionDBO"
				+ " left join dbo.erpReservationCategoryDBO"
				+ " left join dbo.currentCountry"
				+ " left join dbo.currentState"
				+ " left join dbo.currentCity"
				+ " left join dbo.permanentCountry"
				+ " left join dbo.permanentState"
				+ " left join dbo.permanentCity"
				+ " left join dbo.erpQualificationLevelDBO"
				+ " where  dbo.recordStatus  = 'A' and dbo.empApplnEntriesDBO.id in (:empIds)";
		return sessionFactory.withSession(s->s.createQuery(query,EmpApplnPersonalDataDTO.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}
	
	public List<EmpApplnLocationPrefDTO> getApplnLocationPrefDBO(List<Integer> empIds) {
		String query ="select new com.christ.erp.services.dto.employee.recruitment.EmpApplnLocationPrefDTO"
				+ " (dbo.empApplnLocationPrefId,dbo.empApplnEntriesDBO.id,dbo.erpLocationDBO.locationName"
				+ ")"
				+ " from EmpApplnLocationPrefDBO dbo"
				+ " where  dbo.recordStatus  = 'A' and dbo.empApplnEntriesDBO.id in (:empIds)";
		return sessionFactory.withSession(s->s.createQuery(query,EmpApplnLocationPrefDTO.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}
	
	public List<EmpApplnEducationalDetailsDTO> getEmpEducationalDetailsDBOSet(List<Integer> empIds) {
		String query = " select new com.christ.erp.services.dto.employee.recruitment.EmpApplnEducationalDetailsDTO"
				+ " (dbo.empApplnEntriesDBO.id,dbo.course,dbo.specialization,dbo.yearOfCompletion,dbo.gradeOrPercentage,dbo.erpCountryDBO.countryName,dbo.erpStateDBO.stateName,dbo.stateOthers,"
				+ " dbo.erpUniversityBoardDBO.universityBoardName,dbo.boardOrUniversity,dbo.erpInstitutionDBO.institutionName,dbo.institute,dbo.erpQualificationLevelDBO.id"
				+ ")"
				+ " from EmpApplnEducationalDetailsDBO dbo"
				+ " left join dbo.erpCountryDBO"
				+ " left join dbo.erpStateDBO"
				+ " left join dbo.erpUniversityBoardDBO"
				+ " left join dbo.erpInstitutionDBO"
				+ " left join dbo.erpQualificationLevelDBO"
				+ " where dbo.recordStatus  = 'A' and dbo.empApplnEntriesDBO.id in (:empIds) ";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpApplnEducationalDetailsDTO.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}
	
	public Integer getEmpMaxQualification(List<Integer> empIds) {
		String str = "select Max(erp_qualification_level.qualification_level_degree_order) from emp_appln_educational_details"
				+ "	inner join erp_qualification_level ON erp_qualification_level.erp_qualification_level_id = emp_appln_educational_details.erp_qualification_level_id"
				+ "	where emp_appln_educational_details.record_status = 'A' and erp_qualification_level.record_status = 'A'"
				+ "	and emp_appln_educational_details.emp_appln_entries_id in (:empIds)";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Integer.class).setParameter("empIds", empIds).getSingleResultOrNull()).await().indefinitely();
	}
	
	public List<ErpQualificationLevelDBO> getQualificationDetails() {
		String query = " select  dbo FROM ErpQualificationLevelDBO dbo where  dbo.recordStatus  = 'A' ORDER BY dbo.qualificationLevelDegreeOrder";
		return  sessionFactory.withSession(s->s.createQuery(query,ErpQualificationLevelDBO.class).getResultList()).await().indefinitely();
	}
	
	public List<Tuple> getEduCounts(List<Integer> empIds) {
		String str = "select edu_level as level ,max(count_level) as maxCount,displayOrder "
				+ "	from("
				+ "	SELECT emp_appln_entries_id, emp_appln_educational_details.erp_qualification_level_id as edu_level, COUNT(*) AS count_level,"
				+ " erp_qualification_level.qualification_level_degree_order as displayOrder"
				+ "	FROM emp_appln_educational_details"
				+ "	left join  erp_qualification_level ON erp_qualification_level.erp_qualification_level_id = emp_appln_educational_details.erp_qualification_level_id"
				+ "	WHERE emp_appln_educational_details.emp_appln_entries_id in (:empIds) and emp_appln_educational_details.record_status = 'A'"
				+ "	GROUP BY emp_appln_entries_id,emp_appln_educational_details.erp_qualification_level_id"
				+ "	HAVING COUNT(*) >= 1) as a"
				+ "	group by edu_level order by  edu_level asc";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}
	
	public List<EmpEligiblityTestDTO> empEligibilityTestDBOSet(List<Integer> empIds) {
		String query =" select new com.christ.erp.services.dbobjects.employee.recruitment.EmpEligiblityTestDTO"
				+ " (dbo.empApplnEntriesDBO.id, dbo.empEligibilityExamListDBO.eligibilityExamName, dbo.testYear)"
				+ " from EmpEligibilityTestDBO dbo where  dbo.recordStatus  = 'A' and dbo.empApplnEntriesDBO.id in (:empIds)";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpEligiblityTestDTO.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}
	
	public List<Integer> getWorkExp(List<Integer> empIds) {
		String str = "select count(emp_appln_work_experience.emp_appln_entries_id) as countname from emp_appln_work_experience"
				+ "	where emp_appln_work_experience.emp_appln_entries_id in (:empIds)"
				+ "	and emp_appln_work_experience.record_status = 'A' and  (emp_appln_work_experience.is_current_experience = false or "
				+ " emp_appln_work_experience.is_current_experience is null) group by emp_appln_work_experience.emp_appln_entries_id";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Integer.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}
	
	public List<EmpApplnWorkExperienceDTO> getEmpWorkExperienceDBOSet(List<Integer> empIds, boolean b) {
		String query = " select new com.christ.erp.services.dto.employee.recruitment.EmpApplnWorkExperienceDTO"
				+ " (dbo.empApplnEntriesDBO.id, dbo.workExperienceYears, dbo.workExperienceMonth, dbo.isPartTime, dbo.empApplnWorkExperienceTypeDBO.workExperienceTypeName, dbo.empApplnSubjectCategoryDBO.subjectCategory,"
				+ " dbo.workExperienceFromDate, dbo.workExperienceToDate, dbo.empDesignation, dbo.institution,dbo.empApplnEntriesDBO.noticePeriod,dbo.empApplnEntriesDBO.currentMonthlySalary,dbo.functionalAreaOthers,"
				+ " dbo.isCurrentExperience)"
				+ " from EmpApplnWorkExperienceDBO dbo "
				+ " left join dbo.empApplnWorkExperienceTypeDBO"
				+ " left join dbo.empApplnSubjectCategoryDBO "
				+ " left join dbo.empApplnEntriesDBO"
				+ " where  dbo.recordStatus  = 'A' and dbo.empApplnEntriesDBO.id in (:empIds) ";
		if(b) {
			query +=" and dbo.isCurrentExperience = true";
		} 
		String finalquery = query;
		return  sessionFactory.withSession(s->s.createQuery(finalquery,EmpApplnWorkExperienceDTO.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}
	
	public List<EmpApplnAddtnlInfoHeadingDBO> getApplnAddInfoHeading(SelectDTO category) {
		String query = " select distinct dbo FROM EmpApplnAddtnlInfoHeadingDBO dbo"
				+ " inner join fetch dbo.empApplnAddtnlInfoParameterMap eaip"
				+ " where  dbo.recordStatus  = 'A'";
		if(!Utils.isNullOrEmpty(category)) {
			query += " and dbo.empEmployeeCategoryId.id = :categoryId";
		}
		query += " Order by dbo.headingDisplayOrder asc";
		String finalquery = query;
		List<EmpApplnAddtnlInfoHeadingDBO> list = sessionFactory.withSession(s -> { Mutiny.Query<EmpApplnAddtnlInfoHeadingDBO> query1 = s.createQuery(finalquery, EmpApplnAddtnlInfoHeadingDBO.class);	
		if(!Utils.isNullOrEmpty(category)) {
			query1.setParameter("categoryId",Integer.parseInt(category.getValue()));
		}
		return  query1.getResultList();
		}).await().indefinitely();
		return list;
	}
	
	public List<EmpApplnAddtnlInfoEntriesDTO> empApplnAddtnlInfoEntries(List<Integer> empIds) {
		String query =" select new com.christ.erp.services.dto.employee.recruitment.EmpApplnAddtnlInfoEntriesDTO"
				+ " (dbo.empApplnEntriesDBO.id,dbo.empApplnAddtnlInfoParameterDBO.id,dbo.addtnlInfoValue)"
				+ " from EmpApplnAddtnlInfoEntriesDBO dbo where  dbo.recordStatus  = 'A' and dbo.empApplnEntriesDBO.id in (:empIds)";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpApplnAddtnlInfoEntriesDTO.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}
	
//	public List<EmpApplnSubjSpecializationPrefDTO> getEmpApplnSubjSpecializationPrefDBO(List<Integer> empIds) {
//		String query ="select new com.christ.erp.services.dto.employee.recruitment.EmpApplnSubjSpecializationPrefDTO"
//				+ " (dbo.empApplnSubjSpecializationPrefId,dbo.empApplnEntriesDBO.id,dbo.empApplnSubjectCategoryDBO.id,dbo.empApplnSubjectCategoryDBO.subjectCategory,"
//				+ " dbo.empApplnSubjectCategorySpecializationDBO.empApplnSubjectCategorySpecializationId,dbo.empApplnSubjectCategorySpecializationDBO.subjectCategorySpecializationName"
//				+ ")"
//				+ " from EmpApplnSubjSpecializationPrefDBO dbo"
//				+ " left join dbo.empApplnSubjectCategoryDBO"
//				+ " left join dbo.empApplnSubjectCategorySpecializationDBO"
//				+ " where  dbo.recordStatus  = 'A' and dbo.empApplnEntriesDBO.id in (:empIds)";
//		return sessionFactory.withSession(s->s.createQuery(query,EmpApplnSubjSpecializationPrefDTO.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
//	}
	
	public List<Tuple> getEmpApplnSubjSpecializationPrefDBO(List<Integer> empIds) {
		String query ="select distinct emp_appln_subj_specialization_pref.emp_appln_subj_specialization_pref_id as id,emp_appln_subj_specialization_pref.emp_appln_entries_id as entriesId,\r\n"
				+ "emp_appln_subject_category.emp_appln_subject_category_id as categoryId , emp_appln_subject_category.subject_category_name as categoryName,\r\n"
				+ "emp_appln_subject_category_specialization.emp_appln_subject_category_specialization_id as specialId , emp_appln_subject_category_specialization.subject_category_specialization_name as specialName\r\n"
				+ "from emp_appln_subj_specialization_pref\r\n"
				+ "left join emp_appln_subject_category ON emp_appln_subject_category.emp_appln_subject_category_id = emp_appln_subj_specialization_pref.emp_appln_subject_category_id and emp_appln_subject_category.record_status = 'A'\r\n"
				+ "left join emp_appln_subject_category_specialization ON emp_appln_subject_category_specialization.emp_appln_subject_category_specialization_id = emp_appln_subj_specialization_pref.emp_appln_subject_category_specialization_id and emp_appln_subject_category_specialization.record_status = 'A'\r\n"
				+ "where emp_appln_subj_specialization_pref.record_status = 'A' and emp_appln_subj_specialization_pref.emp_appln_entries_id in (:empIds)";
		return sessionFactory.withSession(s->s.createNativeQuery(query,Tuple.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}
	
	
}
