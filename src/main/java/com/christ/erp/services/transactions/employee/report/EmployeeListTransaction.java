package com.christ.erp.services.transactions.employee.report;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Tuple;
import org.hibernate.reactive.mutiny.Mutiny;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpQualificationLevelDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpEligiblityTestDTO;
import com.christ.erp.services.dto.common.ErpCampusDepartmentMappingDTO;
import com.christ.erp.services.dto.employee.common.EmpDTO;
import com.christ.erp.services.dto.employee.common.EmpGuestContractDetailsDTO;
import com.christ.erp.services.dto.employee.common.EmpMajorAchievementsDTO;
import com.christ.erp.services.dto.employee.common.EmpResignationDTO;
import com.christ.erp.services.dto.employee.onboarding.EmpApplnEntriesDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpAddtnlPersonalDataDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpEducationalDetailsDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpEmployeeLetterDetailsDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpFamilyDetailsAddtnlDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpJobDetailsDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpPersonalDataDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpPfGratuityNomineesDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpWorkExperienceDTO;
import com.christ.erp.services.dto.employee.report.EmployeeListDTO;
import com.christ.erp.services.dto.employee.salary.EmpPayScaleDetailsComponentsDTO;
import com.christ.erp.services.dto.employee.salary.EmpPayScaleDetailsDTO;
import com.christ.erp.services.dto.employee.settings.EmpApproversDetailsDTO;

@Repository
public class EmployeeListTransaction {

	@Autowired
	SessionFactory sessionFactory;
	
	public  List<Tuple> getEmployeesData1(EmployeeListDTO dto) {
		String query =" select emp.emp_id as empId, emp.emp_no as empNo, emp.emp_name as name ,erp_gender.gender_name as gender,emp.dob as dob from emp"
				+ " inner join  erp_gender ON erp_gender.erp_gender_id = emp.erp_gender_id";
		if(!Utils.isNullOrEmpty(dto.getSchool()) || !Utils.isNullOrEmpty(dto.getDepartment()) || !Utils.isNullOrEmpty(dto.getCampus()) ) {
			query +=" inner join  erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id";
			if(!Utils.isNullOrEmpty(dto.getSchool()) || !Utils.isNullOrEmpty(dto.getDepartment())) {
				query +=" inner join  erp_department ON erp_department.erp_department_id = erp_campus_department_mapping.erp_department_id\r\n"
					  + " inner join  erp_deanery ON erp_deanery.erp_deanery_id = erp_department.erp_deanery_id";
			} 
			if(!Utils.isNullOrEmpty(dto.getCampus())) {
				query +=" inner join  erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id ";
			}
		}
		if(!Utils.isNullOrEmpty(dto.getAsOnDate()) || !Utils.isNullOrEmpty(dto.getEmpFromDate()) || !Utils.isNullOrEmpty(dto.getEmpToDate())) {
			query +=" inner join  emp_resignation on emp.emp_id = emp_resignation.emp_id";
		}
		if(Utils.isNullOrEmpty(dto.getStatus()) || dto.getStatus().equalsIgnoreCase("Active")) {
			query += " where  emp.record_status = 'A' ";
		} else if (dto.getStatus().equalsIgnoreCase("Inactive")) {
			query += " where emp.record_status =  'I'";
		} else {
			query += " where emp.record_status in ('A','I')";
		}
		if(!Utils.isNullOrEmpty(dto.getEmployeeCategory())) {
			query += " and emp.emp_employee_category_id = :employeeCategoryId";
		}
		if(!Utils.isNullOrEmpty(dto.getJobcategory())) {
			query += " and emp.emp_employee_job_category_id = :jobCategoryId";
		}
		if(!Utils.isNullOrEmpty(dto.getSchool())) {
			query += " and erp_deanery.erp_deanery_id = :schoolId";
		}
		if(!Utils.isNullOrEmpty(dto.getDepartment())) {
			query += " and  erp_department.erp_department_id =  :deptId";
		}
		if(!Utils.isNullOrEmpty(dto.getCampus())) {
			query += " and erp_campus.erp_campus_id in (:campusIds)";
		}
		if(!Utils.isNullOrEmpty(dto.getDesignation())) {
			query += " and emp.emp_designation_id = :designationId";
		}
		if(!Utils.isNullOrEmpty(dto.getAsOnDate())) {
			query += " and (emp.doj  <= :asOnDate) or (emp_resignation.date_of_leaving > :asOnDate or emp_resignation.date_of_leaving is null)";
		}
		if(!Utils.isNullOrEmpty(dto.getEmpFromDate())  && !Utils.isNullOrEmpty(dto.getEmpToDate())) {
			query += " and (emp.doj >= :fromDate and emp.doj <= :toDate) or (emp_resignation.date_of_leaving >= :fromDate and emp_resignation.date_of_leaving <= :toDate)";
		}
		query +=" order by name asc";
		String finalquery = query;
		List<Tuple> list = sessionFactory.withSession(s -> { Mutiny.Query<Tuple> query1 = s.createNativeQuery(finalquery, Tuple.class);	
		if(!Utils.isNullOrEmpty(dto.getEmployeeCategory())) {
			query1.setParameter("employeeCategoryId", Integer.parseInt(dto.getEmployeeCategory().getValue()));
		}
		if(!Utils.isNullOrEmpty(dto.getJobcategory())) {
			query1.setParameter("jobCategoryId", Integer.parseInt(dto.getJobcategory().getValue()));
		}
		if(!Utils.isNullOrEmpty(dto.getSchool())) {
			query1.setParameter("schoolId", Integer.parseInt(dto.getSchool().getValue()));
		}
		if(!Utils.isNullOrEmpty(dto.getDepartment())) {
			query1.setParameter("deptId", Integer.parseInt(dto.getDepartment().getValue()));
		}
		if(!Utils.isNullOrEmpty(dto.getCampus())) {
			List<Integer> campIds = new ArrayList<Integer>();
			dto.getCampus().forEach( camp -> {
				campIds.add(Integer.parseInt(camp.getValue()));
			});
			query1.setParameter("campusIds", campIds);
		}
		if(!Utils.isNullOrEmpty(dto.getDesignation())) {
			query1.setParameter("designationId", Integer.parseInt(dto.getDesignation().getValue()));
		}
		if(!Utils.isNullOrEmpty(dto.getAsOnDate())) {
			query1.setParameter("asOnDate",dto.getAsOnDate());
		}
		if(!Utils.isNullOrEmpty(dto.getEmpFromDate())  && !Utils.isNullOrEmpty(dto.getEmpToDate())) {
			query1.setParameter("fromDate",dto.getEmpFromDate());
			query1.setParameter("toDate",dto.getEmpToDate());
		}
		return  query1.getResultList();
		}).await().indefinitely();
		return list;
	}
	
	public  List<EmpDBO> getEmployeesData(List<Integer> empIds, EmployeeListDTO dto) {
		String query ="  from EmpDBO dbo";
//					+ " left join fetch dbo.empApplnEntriesDBO"
//				    + " left join fetch dbo.empJobDetailsDBO ejd"
//				    + " inner join fetch dbo.empPersonalDataDBO epd";	
//		if( !Utils.isNullOrEmpty(dto.getFathersName()) || !Utils.isNullOrEmpty(dto.getMothersName()) || !Utils.isNullOrEmpty(dto.getFamilyDependentDetails())) {
//			query += " left join fetch epd.empFamilyDetailsAddtnlDBOS efd";
//		}
//		if(!Utils.isNullOrEmpty(dto.getDeputedDepartmentAndCampus())) {
//			query += " left join fetch dbo.deputationErpCampusDepartmentMappingDBO dcd";
//		}
		
//		if(!Utils.isNullOrEmpty(dto.getLetterDetails())) {
//			query += " left join  fetch dbo.empEmployeeLetterDetailsDBOSet eld";
//		}
//		if(!Utils.isNullOrEmpty(dto.getPfNomineeDetails()) || !Utils.isNullOrEmpty(dto.getGratuityNomineeDetails())) {
//			query += " left join fetch ejd.empPfGratuityNomineesDBOS pfgn";
//		}
//		if(!Utils.isNullOrEmpty(dto.getPayScaleDetails())) {
//			query += " left join fetch ejd.empPayScaleDetailsId epsd"
//			      + " left join fetch epsd.empPayScaleDetailsComponentsDBOs epsdc";
//		}
//		if(!Utils.isNullOrEmpty(dto.getApproverDetails())) {
//			query += " left join fetch dbo.empApproversDBO ea";
//		}
//		if(!Utils.isNullOrEmpty(dto.getGuestAndContractDetails())) {
//			query += " left join fetch dbo.empGuestContractDetailsDBOSet egcd";
//		}
//		if(!Utils.isNullOrEmpty(dto.getWorkExperience())) {
//			query += " left join fetch dbo.empWorkExperienceDBOSet ewe";
//		}
//		if(!Utils.isNullOrEmpty(dto.getQualificationDetails())) {
//			query += " left join fetch dbo.empEducationalDetailsDBOSet eed";
//		}
//		if(!Utils.isNullOrEmpty(dto.getEligibilityTest())) {
//			query += " left join fetch dbo.empEligibilityTestDBOSet et";
//		}
//		if(!Utils.isNullOrEmpty(dto.getMajorAchievements())) {
//			query += " left join fetch dbo.empMajorAchievementsDBOSet ema";
//		}
//		if(!Utils.isNullOrEmpty(dto.getResignationDetails())) {
//			query += " left join fetch dbo.empresignationDBO er";
//		}	

		query	+= " where dbo.id in (:empIds) and dbo.recordStatus ='A'";
		String finalquery = query;
		return sessionFactory.withSession(s->s.createQuery(finalquery,EmpDBO.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}
	
	public List<Tuple> getpayComponents(List<Integer> empIds) {
//		String str = "select distinct emp_pay_scale_components.salary_component_name as name ,emp_pay_scale_components.salary_component_display_order as displayOrder from emp"
//				+ " inner join emp_job_details ON emp_job_details.emp_job_details_id = emp.emp_job_details_id"
//				+ " inner join emp_pay_scale_details on emp_pay_scale_details.emp_pay_scale_details_id = emp_job_details.emp_pay_scale_details_id"
//				+ " inner join emp_pay_scale_details_components on emp_pay_scale_details_components.emp_pay_scale_details_id = emp_pay_scale_details.emp_pay_scale_details_id"
//				+ " inner join emp_pay_scale_components ON emp_pay_scale_components.emp_pay_scale_components_id = emp_pay_scale_details_components.emp_pay_scale_components_id"
//				+ " where emp.record_status = 'A' and emp_job_details.record_status = 'A' and emp_pay_scale_details.record_status = 'A' and emp_pay_scale_details_components.record_status = 'A' "
//				+ " and emp_pay_scale_components.record_status = 'A' and  emp.emp_id in (:empIds)  order by displayOrder asc";
		
		String str ="select distinct emp_pay_scale_components.salary_component_name as name ,emp_pay_scale_components.salary_component_display_order as displayOrder"
				+ " from emp_pay_scale_details"
				+ " inner join emp_pay_scale_details_components on emp_pay_scale_details_components.emp_pay_scale_details_id = emp_pay_scale_details.emp_pay_scale_details_id"
				+ "	inner join emp_pay_scale_components ON emp_pay_scale_components.emp_pay_scale_components_id = emp_pay_scale_details_components.emp_pay_scale_components_id"
				+ " where emp_pay_scale_details.record_status = 'A' and emp_pay_scale_details_components.record_status = 'A'"
				+ "	and emp_pay_scale_components.record_status = 'A' and emp_pay_scale_details.emp_id in (:empIds)"
				+ " and emp_pay_scale_details.is_current = true  order by displayOrder asc";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}
	
	public List<ErpQualificationLevelDBO> getQualificationDetails() {
		String query = " select  dbo FROM ErpQualificationLevelDBO dbo where  dbo.recordStatus  = 'A' ORDER BY dbo.qualificationLevelDegreeOrder";
		return  sessionFactory.withSession(s->s.createQuery(query,ErpQualificationLevelDBO.class).getResultList()).await().indefinitely();
	}
	
	public Integer getEmpMaxQualification(List<Integer> empIds) {
		String str = "select Max(erp_qualification_level.qualification_level_degree_order) from emp_educational_details"
				+ " inner join erp_qualification_level ON erp_qualification_level.erp_qualification_level_id = emp_educational_details.erp_qualification_level_id"
				+ " where emp_educational_details.record_status = 'A' and erp_qualification_level.record_status = 'A'"
				+ " and emp_educational_details.emp_id in (:empIds)";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Integer.class).setParameter("empIds", empIds).getSingleResultOrNull()).await().indefinitely();
	}

	public List<Integer> getWorkExp(List<Integer> empIds) {
		String str = "select count(emp_work_experience.emp_id) as countname from emp_work_experience"
				+ "	where emp_work_experience.emp_id in (:empIds)"
				+ " and emp_work_experience.record_status = 'A' group by emp_work_experience.emp_id";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Integer.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}
	
	public List<Integer> getFamilyCount(List<Integer> empIds) {
		String str = " select count(emp_family_details_addtnl.emp_personal_data_id) as countname from emp"
				+ " inner join emp_personal_data ON emp_personal_data.emp_personal_data_id = emp.emp_personal_data_id"
				+ " inner join emp_family_details_addtnl on  emp_personal_data.emp_personal_data_id = emp_family_details_addtnl.emp_personal_data_id"
				+ " where emp.emp_id in (:empIds)and emp_family_details_addtnl.relationship not in('MOTHER','FATHER')"
				+ "	and emp.record_status = 'A' and emp_personal_data.record_status = 'A' and emp_family_details_addtnl.record_status = 'A'"
				+ " group by emp_family_details_addtnl.emp_personal_data_id";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Integer.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}
	
	public List<Tuple> getEduCounts(List<Integer> empIds) {
		String str = "select edu_level as level ,max(count_level) as maxCount"
				+ "	from("
				+ "	SELECT emp_id,erp_qualification_level.qualification_level_degree_order as edu_level, COUNT(*) AS count_level"
				+ "	FROM emp_educational_details"
				+ " inner join  erp_qualification_level ON erp_qualification_level.erp_qualification_level_id = emp_educational_details.erp_qualification_level_id and erp_qualification_level.record_status = 'A'"
				+ "	WHERE emp_id in (:empIds) and emp_educational_details.record_status = 'A'"
				+ "	GROUP BY emp_id, erp_qualification_level.qualification_level_degree_order"
				+ "	HAVING COUNT(*) >= 1) as a"
				+ "	group by edu_level order by  edu_level asc";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}
	
	public List<Tuple> getGuestCounts(List<Integer> empIds) {	
		String str ="select emp_guest_contract_details.emp_id,COUNT(*) AS countLevel from emp_guest_contract_details"
				+ " where emp_guest_contract_details.emp_id in (:empIds) and emp_guest_contract_details.is_current = true"
				+ " GROUP BY emp_id"
				+ " HAVING COUNT(*) >= 1 ";
				return sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}
	////
	
	public List<EmpDTO> empDBO(List<Integer> empIds) {
//		String query = " select  dbo FROM EmpEmployeeLetterDetailsDBO dbo where  dbo.recordStatus  = 'A' and dbo.empDBO.id in (:empIds)";
		String query = " select new com.christ.erp.services.dto.employee.common.EmpDTO"
				+ " (dbo.id, dbo.empPersonalDataDBO.empPersonalDataId,"
				+ " dbo.deputationErpCampusDepartmentMappingDBO.id,"
				+ "  dbo.empNumber, dbo.empName, dbo.erpGenderDBO.genderName, dbo.empDOB, dbo.countryCode, dbo.empMobile, dbo.empDOJ,"
				+ "  dbo.recordStatus, dbo.empPersonalEmail, dbo.empUniversityEmail,"
				+ " dbo.empEmployeeCategoryDBO.employeeCategoryName, dbo.empEmployeeJobCategoryDBO.employeeJobName, dbo.empApplnSubjectCategoryDBO.subjectCategory, dbo.empApplnSubjectCategorySpecializationDBO.subjectCategorySpecializationName,"
				+ " dbo.empEmployeeGroupDBO.employeeGroupName, dbo.empDesignationDBO.empDesignationName, dbo.empAlbumDesignationDBO.empDesignationName, dbo.erpEmployeeTitleDBO.titleName, dbo.erpCampusDepartmentMappingDBO.erpDepartmentDBO.departmentName, dbo.erpCampusDepartmentMappingDBO.erpCampusDBO.campusName,"
				+ " dbo.deputationDepartmentTitleDBO.titleName,"
				+ " eb.blockName,"
				+ " ef.floorName,"
				+ " er.roomName, "
				+ " dbo.erpRoomEmpMappingDBO.cabinNo,dbo.erpRoomEmpMappingDBO.telephoneExtension, dbo.erpRoomEmpMappingDBO.telephoneNumber,"
				+ " dbo.depautationStartDate,"
				+ " dbo.empApplnEntriesDBO.id,"
				+ " dbo.empTimeZoneDBO.timeZoneName)"
				+ " from EmpDBO dbo"
				+ " left join dbo.empPersonalDataDBO"
				+ " left join dbo.deputationErpCampusDepartmentMappingDBO"
				+ " left join dbo.erpGenderDBO"
				+ " left join dbo.empEmployeeCategoryDBO"
				+ " left join dbo.empEmployeeJobCategoryDBO"
				+ " left join dbo.empApplnSubjectCategoryDBO"
				+ " left join dbo.empApplnSubjectCategorySpecializationDBO"
				+ " left join dbo.empEmployeeGroupDBO"
				+ " left join dbo.empDesignationDBO"
				+ " left join dbo.empAlbumDesignationDBO"
				+ " left join dbo.erpEmployeeTitleDBO"
				+ " left join dbo.erpCampusDepartmentMappingDBO ecdm"
				+ " left join ecdm.erpDepartmentDBO"
				+ " left join ecdm.erpCampusDBO"
				+ " left join dbo.deputationDepartmentTitleDBO"
				+ " left join dbo.erpRoomEmpMappingDBO erm"
				+ " left join erm.erpRoomsDBO er"
				+ " left join er.erpBlockDBO eb"
				+ " left join er.erpFloorsDBO ef"
				+ " left join dbo.empApplnEntriesDBO"
				+ " left join dbo.empTimeZoneDBO"
				+ " where dbo.recordStatus  = 'A' and dbo.id in (:empIds)";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpDTO.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}
	
	public List<EmpEmployeeLetterDetailsDTO> getEmployeeLetterDetailsDBOSet(List<Integer> empIds) {
//		String query = " select  dbo FROM EmpEmployeeLetterDetailsDBO dbo where  dbo.recordStatus  = 'A' and dbo.empDBO.id in (:empIds)";
		String query = " select new com.christ.erp.services.dto.employee.recruitment.EmpEmployeeLetterDetailsDTO"
				+ " (dbo.empDBO.id, dbo.letterType, dbo.letterRefNo, dbo.letterDate)"
				+ " from EmpEmployeeLetterDetailsDBO dbo where dbo.recordStatus  = 'A' and dbo.empDBO.id in (:empIds) and dbo.letterType in ('APPOINTMENT_LETTER','APPOINTMENT_LETTER_EXTENDED','REGULAR_APPOINTMENT_LETTER','CONFIRMATION_LETTER')";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpEmployeeLetterDetailsDTO.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}

	public List<EmpPfGratuityNomineesDTO> getEmpPfGratuityNomineesDBOS(List<Integer> empIds) {
//		String query = " select  dbo FROM EmpPfGratuityNomineesDBO dbo where  dbo.recordStatus  = 'A' and dbo.empJobDetailsDBO.empDBO.id in (:empIds)";
		String query ="select new com.christ.erp.services.dto.employee.recruitment.EmpPfGratuityNomineesDTO"
				+ " (dbo.empJobDetailsDBO.empDBO.id, dbo.nominee, dbo.nomineeAddress, dbo.nomineeRelationship, dbo.nomineeDob, dbo.sharePercentage, dbo.under18GuardName,"
				+ " dbo.under18GuardianAddress, dbo.isPf, dbo.isGratuity)"
				+ " from EmpPfGratuityNomineesDBO dbo where dbo.recordStatus  = 'A' and dbo.empJobDetailsDBO.empDBO.id in (:empIds) ";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpPfGratuityNomineesDTO.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}
	
	public List<EmpApproversDetailsDTO> getEmpApproversDBO(List<Integer> empIds) {
		//String query = " select  dbo FROM EmpApproversDBO dbo where  dbo.recordStatus  = 'A' and dbo.empDBO.id in (:empIds)";
		String query = "select new com.christ.erp.services.dto.employee.settings.EmpApproversDetailsDTO"
				+ " (dbo.empDBO.id, dbo.leaveApproverId.empName, dbo.leaveAuthorizerId.empName, dbo.levelOneAppraiserId.empName, dbo.levelTwoAppraiserId.empName,dbo.workDairyApproverId.empName)"
				+ " from EmpApproversDBO dbo where dbo.recordStatus  = 'A'"
				+ " and dbo.empDBO.id in (:empIds)";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpApproversDetailsDTO.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}
	
	public List<EmpEducationalDetailsDTO> getEmpEducationalDetailsDBOSet(List<Integer> empIds) {
//	String query = " select  dbo FROM EmpEducationalDetailsDBO dbo where  dbo.recordStatus  = 'A' and dbo.empDBO.id in (:empIds)";
	String query = " select new com.christ.erp.services.dto.employee.recruitment.EmpEducationalDetailsDTO"
			+ " (dbo.empDBO.id, dbo.erpQualificationLevelDBO.id, dbo.erpQualificationLevelDBO.qualificationLevelName, dbo.course, dbo.specialization, dbo.yearOfCompletion, dbo.gradeOrPercentage,"
			+ " dbo.erpInstitutionDBO.institutionName"
			+ " , dbo.institute, dbo.boardOrUniversity, dbo.erpUniversityBoardDBO.universityBoardName, dbo.erpCountryDBO.countryName,"
			+ " dbo.stateOthers, dbo.erpStateDBO.stateName, dbo.currentStatus)"
			+ " from EmpEducationalDetailsDBO dbo"
			+ " left join dbo.erpInstitutionDBO"
			+ " left join dbo.erpUniversityBoardDBO"
			+ " left join dbo.erpCountryDBO"
			+ " left join dbo.erpStateDBO"
			+ " where dbo.recordStatus  = 'A' and dbo.empDBO.id in (:empIds) ";
	return  sessionFactory.withSession(s->s.createQuery(query,EmpEducationalDetailsDTO.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
}
	
	public List<EmpGuestContractDetailsDTO> getEmpGuestContractDetailsDBOSet(List<Integer> empIds) {
//		String query = " select  dbo FROM EmpGuestContractDetailsDBO dbo where  dbo.recordStatus  = 'A' and dbo.isCurrent = true  and dbo.empDBO.id in (:empIds)";
		String query = " select new com.christ.erp.services.dto.employee.common.EmpGuestContractDetailsDTO"
				+ " (dbo.empDBO.id, dbo.erpCampusDepartmentMappingDBO.erpDepartmentDBO.departmentName, dbo.erpCampusDepartmentMappingDBO.erpCampusDBO.campusName, dbo.guestTutoringSemester,"
				+ " dbo.guestWorkingHoursWeek, dbo.guestReferredBy, dbo.contractEmpStartDate, dbo.contractEmpEndDate, dbo.contractEmpLetterNo, dbo.guestContractRemarks)"
				+ " from EmpGuestContractDetailsDBO dbo where dbo.recordStatus  = 'A' and dbo.isCurrent = true  and dbo.empDBO.id in (:empIds)";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpGuestContractDetailsDTO.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}
	
	public List<EmpWorkExperienceDTO> getEmpWorkExperienceDBOSet(List<Integer> empIds) {
//		String query = " select  dbo FROM EmpWorkExperienceDBO dbo where  dbo.recordStatus  = 'A' and dbo.empDBO.id in (:empIds)";
		String query = " select new com.christ.erp.services.dto.employee.recruitment.EmpWorkExperienceDTO"
				+ " (dbo.empDBO.id, dbo.workExperienceYears, dbo.workExperienceMonth, dbo.isPartTime, dbo.empApplnWorkExperienceTypeDBO.workExperienceTypeName, dbo.empApplnSubjectCategoryDBO.subjectCategory,"
				+ " dbo.workExperienceFromDate, dbo.workExperienceToDate, dbo.empDesignation, dbo.institution, dbo.isRecognized)"
				+ " from EmpWorkExperienceDBO dbo "
				+ " left join dbo.empApplnWorkExperienceTypeDBO"
				+ " left join dbo.empApplnSubjectCategoryDBO "
				+ "where  dbo.recordStatus  = 'A' and dbo.empDBO.id in (:empIds)";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpWorkExperienceDTO.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}
	
	public List<EmpEligiblityTestDTO> empEligibilityTestDBOSet(List<Integer> empIds) {
//		String query = " select  dbo FROM EmpEligibilityTestDBO dbo where  dbo.recordStatus  = 'A' and dbo.empDBO.id in (:empIds)";
		String query =" select new com.christ.erp.services.dbobjects.employee.recruitment.EmpEligiblityTestDTO"
				+ " (dbo.empDBO.id, dbo.empEligibilityExamListDBO.eligibilityExamName, dbo.testYear)"
				+ " from EmpEligibilityTestDBO dbo where  dbo.recordStatus  = 'A' and dbo.empDBO.id in (:empIds)";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpEligiblityTestDTO.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}
	
	public List<EmpMajorAchievementsDTO> empMajorAchievementsDBOSet(List<Integer> empIds) {
//		String query = " select  dbo FROM EmpMajorAchievementsDBO dbo where  dbo.recordStatus  = 'A' and dbo.empDBO.id in (:empIds)";
		String query =" select new com.christ.erp.services.dto.employee.common.EmpMajorAchievementsDTO"
				+ " (dbo.empDBO.id, dbo.achievements)"
				+ " from EmpMajorAchievementsDBO dbo where  dbo.recordStatus  = 'A' and dbo.empDBO.id in (:empIds)";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpMajorAchievementsDTO.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}
	
	public List<EmpResignationDTO> empresignationDBO(List<Integer> empIds) {
//		String query = " select  dbo FROM EmpResignationDBO dbo where  dbo.recordStatus  = 'A' and dbo.empDBO.id in (:empIds)";
		String query =" select new com.christ.erp.services.dto.employee.common.EmpResignationDTO"
				+ "(dbo.empDBO.id, dbo.submissionDate, dbo.dateOfLeaving, dbo.hodRecomendedRelievingDate, dbo.relievingDate, dbo.empResignationReasonDBO.resignationName, dbo.reasonOther, dbo.poRemarks)"
				+ " from EmpResignationDBO dbo where  dbo.recordStatus  = 'A' and dbo.empDBO.id in (:empIds)";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpResignationDTO.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}
	public List<EmpPayScaleDetailsDTO> empPayScaleDetails(List<Integer> empIds) {
		String query =" select new com.christ.erp.services.dto.employee.salary.EmpPayScaleDetailsDTO"
				+ "(dbo.id,dbo.empDBO.id, dbo.payScaleEffectiveDate, dbo.payScaleComments, dbo.payScaleType, dbo.empPayScaleMatrixDetailDBO.empPayScaleGradeMappingDetailDBO.payScale"
				+ " ,dbo.wageRatePerType, dbo.grossPay)"
				+ " from EmpPayScaleDetailsDBO dbo where  dbo.recordStatus  = 'A' and dbo.empDBO.id in (:empIds) and dbo.current = true";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpPayScaleDetailsDTO.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}
	
	public List<EmpPayScaleDetailsComponentsDTO> empPayScaleDetailsComponentsDBO(List<Integer> empPayScaleDetailsIds) {
		String query =" select new com.christ.erp.services.dto.employee.salary.EmpPayScaleDetailsComponentsDTO"
				+ "(dbo.empPayScaleDetailsDBO.id,dbo.empPayScaleComponentsDBO.salaryComponentDisplayOrder, dbo.empSalaryComponentValue)"
				+ " from EmpPayScaleDetailsComponentsDBO dbo where  dbo.recordStatus  = 'A' and dbo.empPayScaleDetailsDBO.id in (:empPayScaleDetailsIds)";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpPayScaleDetailsComponentsDTO.class).setParameter("empPayScaleDetailsIds", empPayScaleDetailsIds).getResultList()).await().indefinitely();
	}
	
	public List<EmpApplnEntriesDTO> empApplnEntriesDBOs(List<Integer> empAppNoIds) {
		String query =" select new com.christ.erp.services.dto.employee.onboarding.EmpApplnEntriesDTO"
				+ "(dbo.id, dbo.applicationNo)"
				+ " from EmpApplnEntriesDBO dbo where  dbo.recordStatus  = 'A' and dbo.id in (:empAppNoIds)";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpApplnEntriesDTO.class).setParameter("empAppNoIds", empAppNoIds).getResultList()).await().indefinitely();
	}
	public List<EmpJobDetailsDTO> empJobDetailsDBO(List<Integer> empIds) {
		String query =" select new com.christ.erp.services.dto.employee.recruitment.EmpJobDetailsDTO"
				+ " (dbo.empDBO.id, dbo.retirementDate, dbo.contractStartDate, dbo.contractEndDate, dbo.contractRemarks, dbo.pfAccountNo, dbo.pfDate, dbo.uanNo,"
				+ " dbo.gratuityNo, dbo.gratuityDate, dbo.sibAccountBank, dbo.branchIfscCode, dbo.smartCardNo, dbo.isDisplayWebsite, dbo.isVacationApplicable,"
				+ " dbo.isVacationTimeZoneApplicable, dbo.vacationTimeZoneDBO.timeZoneName, dbo.isHolidayTimeZoneApplicable, dbo.holidayTimeZoneDBO.timeZoneName, dbo.isDutyRosterApplicable,"
				+ " dbo.empLeaveCategoryAllotmentId.empLeaveCategoryAllotmentName, dbo.recognisedExpYears, dbo.recognisedExpMonths)"
				+ " from EmpJobDetailsDBO dbo where  dbo.recordStatus  = 'A' and dbo.empDBO.id in (:empIds)";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpJobDetailsDTO.class).setParameter("empIds", empIds).getResultList()).await().indefinitely();
	}
	public List<EmpFamilyDetailsAddtnlDTO> empFamilyDetailsAddtnlDBOS(List<Integer> empPersonalDataIds) {
		String query =" select new com.christ.erp.services.dto.employee.recruitment.EmpFamilyDetailsAddtnlDTO"
				+ "(dbo.empPersonalDataDBO.id, dbo.relationship, dbo.dependentName, dbo.dependentDob, dbo.dependentQualification, dbo.dependentProfession)"
				+ " from EmpFamilyDetailsAddtnlDBO dbo "
				+ " left join dbo.empPersonalDataDBO"
				+ " where  dbo.recordStatus  = 'A' and dbo.empPersonalDataDBO.id in (:empPersonalDataIds)";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpFamilyDetailsAddtnlDTO.class).setParameter("empPersonalDataIds", empPersonalDataIds).getResultList()).await().indefinitely();
	}
	public List<ErpCampusDepartmentMappingDTO> deputationErpCampusDepartmentMappingDBO(List<Integer> empPersonalDataIds) {
		String query =" select new com.christ.erp.services.dto.common.ErpCampusDepartmentMappingDTO"
				+ "(dbo.id, dbo.erpDepartmentDBO.departmentName, dbo.erpCampusDBO.campusName)"
				+ " from ErpCampusDepartmentMappingDBO dbo "
				+ " left join dbo.erpDepartmentDBO"
				+ " left join dbo.erpCampusDBO"
				+ " where  dbo.recordStatus  = 'A' and dbo.id in (:empPersonalDataIds)";
		return  sessionFactory.withSession(s->s.createQuery(query,ErpCampusDepartmentMappingDTO.class).setParameter("empPersonalDataIds", empPersonalDataIds).getResultList()).await().indefinitely();
	}
	
	public List<EmpPersonalDataDTO> empPersonalDataDBO(List<Integer> empPersonalDataIds) {
//		String query = " select  dbo FROM EmpMajorAchievementsDBO dbo where  dbo.recordStatus  = 'A' and dbo.empDBO.id in (:empIds)";
		String query =" select new com.christ.erp.services.dto.employee.recruitment.EmpPersonalDataDTO"
				+ " (dbo.id, dbo.erpCountryDBO.countryName, dbo.erpMaritalStatusDBO.maritalStatusName, dbo.erpReligionDBO.religionName, dbo.erpReservationCategoryDBO.reservationCategoryName, dbo.erpBloodGroupDBO.bloodGroupName, dbo.isDifferentlyAbled,"
				+ "  dbo.erpDifferentlyAbledDBO.differentlyAbledName, dbo.currentAddressLine1, dbo.currentAddressLine2, dbo.currentCountry.countryName, dbo.currentState.stateName, dbo.currentStateOthers,"
				+ "  dbo.currentCity.cityName, dbo.currentCityOthers, dbo.currentPincode, dbo.permanentAddressLine1, dbo.permanentAddressLine2, dbo.permanentCountry.countryName,"
				+ "  dbo.permanentState.stateName, dbo.permanentStateOthers, dbo.permanentCity.cityName, dbo.permanentCityOthers, dbo.permanentPincode,"
				+ "  dbo.erpQualificationLevelDBO.qualificationLevelName, dbo.highestQualificationAlbum, dbo.orcidNo, dbo.scopusNo)"
				+ " from EmpPersonalDataDBO dbo "
				+ " left join dbo.erpCountryDBO"
				+ " left join dbo.erpMaritalStatusDBO"
				+ " left join dbo.erpReligionDBO"
				+ " left join dbo.erpReservationCategoryDBO"
				+ " left join dbo.erpBloodGroupDBO"
				+ " left join dbo.erpDifferentlyAbledDBO"
				+ " left join dbo.currentCountry"
				+ " left join dbo.currentState"
				+ " left join dbo.currentCity"
				+ " left join dbo.permanentCountry"
				+ " left join dbo.permanentState"
				+ " left join dbo.permanentCity"
				+ " left join dbo.erpQualificationLevelDBO"
				+ " where  dbo.recordStatus  = 'A' and dbo.id in (:empPersonalDataIds)";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpPersonalDataDTO.class).setParameter("empPersonalDataIds", empPersonalDataIds).getResultList()).await().indefinitely();
	}
	
	public List<EmpAddtnlPersonalDataDTO> empAddtnlPersonalDataDBO(List<Integer> empPersonalDataIds) {
		String query =" select new com.christ.erp.services.dto.employee.recruitment.EmpAddtnlPersonalDataDTO"
				+ "(dbo.empPersonalDataDBO.id, dbo.panNo, dbo.aadharNo, dbo.emergencyContactName, dbo.emergencyContactAddress, dbo.emergencyContactRelationship, dbo.emergencyMobileNo,"
				+ " dbo.emergencyContactHome, dbo.emergencyContactWork, dbo.passportNo, dbo.passportIssuedDate, dbo.passportStatus, dbo.passportDateOfExpiry, dbo.passportIssuedPlace, dbo.passportComments,"
				+ " dbo.visaNo, dbo.visaIssuedDate, dbo.visaStatus, dbo.visaDateOfExpiry, dbo.visaComments, dbo.frroNo, dbo.frroIssuedDate, dbo.frroStatus, dbo.frroDateOfExpiry, dbo.frroComments,"
				+ " dbo.fourWheelerNo, dbo.twoWheelerNo, dbo.familyBackgroundBrief)"
				+ " from EmpAddtnlPersonalDataDBO dbo "
				+ " where  dbo.recordStatus  = 'A' and dbo.empPersonalDataDBO.id in (:empPersonalDataIds)";
		return  sessionFactory.withSession(s->s.createQuery(query,EmpAddtnlPersonalDataDTO.class).setParameter("empPersonalDataIds", empPersonalDataIds).getResultList()).await().indefinitely();
	}
	
}
