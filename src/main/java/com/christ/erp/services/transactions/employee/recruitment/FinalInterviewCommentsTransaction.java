package com.christ.erp.services.transactions.employee.recruitment;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessNotificationsDBO;
import com.christ.erp.services.dbobjects.employee.EmpPositionRoleDBO;
import com.christ.erp.services.dbobjects.employee.EmpPositionRoleSubDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpJobDetailsDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnDignitariesFeedbackDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnWorkExperienceDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleDetailsComponentsDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleDetailsDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;

@SuppressWarnings("unchecked")
@Repository
public class FinalInterviewCommentsTransaction {
	private static volatile FinalInterviewCommentsTransaction finalInterviewCommentsTransaction = null;

	public static FinalInterviewCommentsTransaction getInstance() {
		if(finalInterviewCommentsTransaction==null) {
			finalInterviewCommentsTransaction = new FinalInterviewCommentsTransaction();
		}
		return  finalInterviewCommentsTransaction;
	}

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public EmpApplnEntriesDBO getEmpApplnEntries(Integer applicationNumber) {
		EmpApplnEntriesDBO empApplnEntriesDBO = sessionFactory.withSession(s -> s.createQuery("FROM EmpApplnEntriesDBO bo where bo.recordStatus='A' and bo.applicationNo=:applicationNumber ",EmpApplnEntriesDBO.class)
				.setParameter("applicationNumber", applicationNumber).getSingleResultOrNull()).await().indefinitely();
		return empApplnEntriesDBO;
	}

	public List<EmpApplnDignitariesFeedbackDBO> getApplnDignitariesFeedbackByApplnId(Integer id){
		String str = " FROM EmpApplnDignitariesFeedbackDBO bo WHERE bo.recordStatus='A' and bo.empApplnEntriesDBO.id=:id ";
		return sessionFactory.withSession(session->session.createQuery(str,EmpApplnDignitariesFeedbackDBO.class).setParameter("id", id).getResultList()).await().indefinitely();
	}

	public boolean saveOrUpdateEmpApplnEntries(EmpApplnEntriesDBO applnEntriesDBO) {
		sessionFactory.withTransaction((session, tx) -> session.merge(applnEntriesDBO)).await().indefinitely();
		return true;
	}

	public boolean saveOrUpdate(List<Object> dboList) {
		sessionFactory.withTransaction((session, tx) -> session.mergeAll(dboList.toArray())).await().indefinitely();
		return true;
	}

	public EmpJobDetailsDBO getEmpJobDetails(Integer empApplnEntriesId) {
		EmpJobDetailsDBO empJobDetailsDBO = sessionFactory.withSession(s -> s.createQuery("FROM EmpJobDetailsDBO bo where bo.empApplnEntriesId.id=:empApplnEntriesId",EmpJobDetailsDBO.class)
				.setParameter("empApplnEntriesId", empApplnEntriesId).getSingleResultOrNull()).await().indefinitely();
		return empJobDetailsDBO;
	}

	public boolean saveOrUpdateEmpJobDetails(EmpJobDetailsDBO jobDetailsDBO) {
		sessionFactory.withTransaction((session, tx) -> session.merge(jobDetailsDBO)).await().indefinitely();
		return true;
	}

	public EmpPayScaleDetailsDBO getEmpPayScaleDetails(Integer empApplnEntriesId) {
		EmpPayScaleDetailsDBO empPayScaleDetailsDBO = sessionFactory.withSession(s -> s.createQuery("FROM EmpPayScaleDetailsDBO bo "
				+ " left join fetch bo.empPayScaleDetailsComponentsDBOs"
				+ " where bo.recordStatus='A' and bo.empApplnEntriesDBO.id=:empApplnEntriesId and bo.current=1",EmpPayScaleDetailsDBO.class)
				.setParameter("empApplnEntriesId", empApplnEntriesId).getSingleResultOrNull()).await().indefinitely();
		return empPayScaleDetailsDBO;
	}

	public List<EmpPayScaleDetailsComponentsDBO> getPayScaleDetailComponent(Integer id){
		String str = "FROM EmpPayScaleDetailsComponentsDBO bo WHERE bo.recordStatus='A' and bo.empPayScaleDetailsDBO.id=:id ";
		return sessionFactory.withSession(session->session.createQuery(str,EmpPayScaleDetailsComponentsDBO.class).setParameter("id", id).getResultList()).await().indefinitely();
	}

	public boolean saveOrUpdateEmpPayScaleDetails(EmpPayScaleDetailsDBO empPayScaleDetailsDBO) {
		sessionFactory.withTransaction((session, tx) -> session.find(EmpPayScaleDetailsDBO.class, empPayScaleDetailsDBO.getId()).call(() -> session.mergeAll(empPayScaleDetailsDBO))).await().indefinitely();
		return true;
	}

	public List<Tuple> getEmployeeDlyWageDetails(String empCatId, String empJobCatId){
		String str = " select emp_daily_wage_slab.emp_daily_wage_slab_id as dly_wg_slab_Id , emp_daily_wage_slab.emp_employee_category_id as emp_cat_id , "
				+ "  emp_employee_category.employee_category_name as emp_cat_name , emp_daily_wage_slab.emp_employee_job_category_id as emp_job_cat_id , "
				+ "  emp_employee_job_category.employee_job_name as emp_job_cat_name ,emp_daily_wage_slab.daily_wage_slab_from as dly_wge_from , "
				+ "  emp_daily_wage_slab.daily_wage_slab_to as dly_wge_to , emp_daily_wage_slab.daily_wage_basic as dly_wge_bsc  from emp_daily_wage_slab "
				+ "  inner join emp_employee_category on emp_daily_wage_slab.emp_employee_category_id = emp_employee_category.emp_employee_category_id "
				+ "  inner join emp_employee_job_category on emp_daily_wage_slab.emp_employee_job_category_id = emp_employee_job_category.emp_employee_job_category_id "
				+ "  where emp_daily_wage_slab.record_status = 'A' and emp_daily_wage_slab.emp_employee_category_id =:empCatId and emp_daily_wage_slab.emp_employee_job_category_id =:empJobCatId ";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str,Tuple.class);
			query.setParameter("empCatId", empCatId);
			query.setParameter("empJobCatId", empJobCatId);
			return query.getResultList();
		}).await().indefinitely();
		return list;  
	}

	public EmpPositionRoleDBO getEmpPositionRoleSub(String campusId) {
		String str = " select bo from EmpPositionRoleDBO bo where bo.recordStatus='A' and bo.erpCampusId.id=:campusId and bo.processType=:processType ";
		return sessionFactory.withSession(session->session.createQuery(str,EmpPositionRoleDBO.class).setParameter("campusId", Integer.parseInt(campusId)).setParameter("processType", "Employee final interview comments").getSingleResultOrNull()).await().indefinitely();
	}

	public List<EmpPositionRoleSubDBO> getEmpPositionRoleSubById(Integer id) {
		String str = "select bo from EmpPositionRoleSubDBO bo WHERE bo.recordStatus='A' and bo.empPositionRoleId.id=:id";
		return sessionFactory.withSession(session->session.createQuery(str,EmpPositionRoleSubDBO.class).setParameter("id", id).getResultList()).await().indefinitely();
	}

	public Tuple getFinalInterviewEmpApplnEntries(Integer applicationNumber) {
		String str = "select applnEntries.emp_appln_entries_id as 'applnId',  "
				+ "	  applnEntries.application_no as 'applnNo',  "
				+ "	  applnEntries.applicant_name as 'applnName',  "
				+ "   applnEntries.personal_email_id as email, "
				+ "   applnEntries.mobile_no as mobile, "
				//				+ "   applnEntries.regret_letter_template_id as regretId, "
				//				+ "   etr.template_name as regreteLetterName,"
				//				+ "   applnEntries.offer_letter_template_id as offerId,"
				//				+ "   et.template_name as offerletterName,"
				//				+ "   applnEntries.offer_letter_url as offerletterUrl , "
				+ "	  employeeCategory.emp_employee_category_id as 'employeeCategoryId',  "
				+ "	  employeeCategory.employee_category_name as 'employeeCategoryName',  "
				+ "	  employeeCategory.is_employee_category_academic as 'isEmployeeCategoryAcademic',  "
				+ "	  employeeCategory.record_status as 'empCategoryRecordStatus',  "
				+ "	  erpCampus.erp_campus_id as 'erpCampusId',  "
				+ "	  erpCampus.campus_name as 'erpCampusName',  "
				+ "	  erpCampus.record_status as 'erpCampusRecordStatus',  "
				+ "	  employeeJobCategory.emp_employee_job_category_id as 'employeeJobCategoryId',  "
				+ "	  employeeJobCategory.employee_job_name as 'employeeJobCategoryName',  "
				+ "	  employeeJobCategory.record_status as 'empJobCategoryRecordStatus',  "
				+ "	  empDesignation.emp_designation_id as 'empDesignationId',  "
				+ "	  empDesignation.emp_designation_name as 'empDesignationName',  "
				+ "	  empDesignation.record_status as 'empDesignationRecordStatus',  "
				+ "	  empTitle.emp_title_id as 'empTitleId',  "
				+ "	  empTitle.title_name as 'empTitleName',  "
				+ "	  empTitle.record_status as 'empTitleRecordStatus',  "
				+ "	  empApplnSubjectCategory.emp_appln_subject_category_id as 'empApplnSubjectCategoryId',  "
				+ "	  empApplnSubjectCategory.subject_category_name as 'empApplnSubjectCategoryName',  "
				+ "	  empApplnSubjectCategory.record_status as 'empApplnSubjectCategoryRecordStatus',  "
				+ "	  empApplnSubjectCategorySpecialization.emp_appln_subject_category_specialization_id as 'empApplnSubjectCategorySpecializationId',  "
				+ "	  empApplnSubjectCategorySpecialization.subject_category_specialization_name as 'empApplnSubjectCategorySpecializationName',  "
				+ "	  empApplnSubjectCategorySpecialization.record_status as 'empApplnSubjectCategorySpecializationRecordStatus',  "
				//				+ "	  applnEntries.is_selected as 'isSelected',  "
				//				+ "	  applnEntries.offer_letter_url as 'offerLetterUrl',  "
				//				+ "	  applnEntries.offer_letter_generated_date as 'offerLetterGeneratedDate',  "
				//				+ "	  applnEntries.job_rejection_reason as 'jobRejectionReason',  "
				//				+ "	  empApplnNonAvailability.emp_appln_non_availability_id as 'empApplnNonAvailabilityId',  "
				//				+ "	  empApplnNonAvailability.non_availability_name as 'empApplnNonAvailabilityName',  "
				+ "	  applicantErpWorkFlowProcess.erp_work_flow_process_id as 'erpWorkFlowProcessApplicantId',  "
				+ "	  applicantErpWorkFlowProcess.process_code as 'erpWorkFlowProcessApplicantCode',  "
				+ "	  applicantErpWorkFlowProcess.applicant_status_display_text as 'erpWorkFlowProcessApplicantText', "
				+ "	  applicationErpWorkFlowProcess.erp_work_flow_process_id as 'erpWorkFlowProcessApplicationId',  "
				+ "	  applicationErpWorkFlowProcess.process_code as 'erpWorkFlowProcessApplicationCode',  "
				+ "	  applicationErpWorkFlowProcess.application_status_display_text as 'erpWorkFlowProcessApplicationText',  "
				+ "	  ecdm.erp_campus_department_mapping_id	as 'camDeptId',"
				+ "	  dept.department_name as 'deptName',"
				+ " 	  cast(applnEntries.stage2_onhold_rejected_comments as char) as 'satge2Comments' "
				+ "	  from emp_appln_entries as applnEntries  "
				+ "	  inner join emp_employee_category as employeeCategory  "
				+ "	  ON employeeCategory.emp_employee_category_id = applnEntries.emp_employee_category_id  "
				+ "	  left join erp_campus as erpCampus  "
				+ "	  ON erpCampus.erp_campus_id = applnEntries.erp_campus_id  "
				+ "	  left join emp_employee_job_category as employeeJobCategory  "
				+ "	  ON employeeJobCategory.emp_employee_job_category_id = applnEntries.emp_employee_job_category_id  "
				+ "	  left join emp_designation as empDesignation  "
				+ "	  ON empDesignation.emp_designation_id = applnEntries.emp_designation_id  "
				+ "	  left join emp_title as empTitle  "
				+ "	  ON empTitle.emp_title_id = applnEntries.title_id  "
				+ "	  left join emp_appln_subject_category as empApplnSubjectCategory  "
				+ "	  ON empApplnSubjectCategory.emp_appln_subject_category_id = applnEntries.emp_appln_subject_category_id  "
				+ "	  left join emp_appln_subject_category_specialization as empApplnSubjectCategorySpecialization  "
				+ "	  ON empApplnSubjectCategorySpecialization.emp_appln_subject_category_specialization_id = applnEntries.emp_appln_subject_category_specialization_id  "
				//				+ "	  left join emp_appln_non_availability as empApplnNonAvailability  "
				//				+ "	  ON empApplnNonAvailability.emp_appln_non_availability_id = applnEntries.emp_appln_non_availability_id  "
				+ "	  left join erp_work_flow_process as applicantErpWorkFlowProcess  "
				+ "	  ON applicantErpWorkFlowProcess.erp_work_flow_process_id = applnEntries.applicant_current_process_status  "
				+ "	  left join erp_work_flow_process as applicationErpWorkFlowProcess  "
				+ "	  ON applicationErpWorkFlowProcess.erp_work_flow_process_id = applnEntries.application_current_process_status "
				//				+ "   left join erp_template as et on et.erp_template_id = applnEntries.offer_letter_template_id and et.record_status = 'A' "
				//				+ "   left join erp_template as etr on etr.erp_template_id = applnEntries.regret_letter_template_id and et.record_status = 'A'"
				+ "	  left join erp_campus_department_mapping as ecdm ON applnEntries.erp_campus_department_mapping_id = ecdm.erp_campus_department_mapping_id and ecdm.record_status = 'A'"				
				+ "	  left join erp_department as dept ON ecdm.erp_department_id = dept.erp_department_id and dept.record_status ='A'"
				+ "	  where applnEntries.application_no =:applicationNumber  "
				+ "	  and applnEntries.record_status = 'A' and applicationErpWorkFlowProcess.process_code not in "
				/* After R10 release, modified query to give provision to access stage 3*/
//				+ " ('EMP_STAGE3_PO_SCHEDULED', 'EMP_STAGE3_PO_RESCHEDULED', 'EMP_STAGE3_SCHEDULE_APPLICANT_ACCEPTED', 'EMP_STAGE3_SCHEDULE_APPLICANT_DECLINED', 'EMP_STAGE3_SELECTED',"
//				+ " 'EMP_STAGE3_REJECTED', 'EMP_STAGE3_ONHOLD', 'EMP_OFFER_LETTER_REGENERATE', 'EMP_OFFER_ACCEPTED', 'EMP_REGRET_LETTER_GENERATED',"
				+ " ( 'EMP_CREATED')";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).setParameter("applicationNumber", applicationNumber).getSingleResultOrNull()).await().indefinitely();	
	}

	public List<Tuple> getFinalInterviewApplnDignitariesFeedback(Integer applnEntrieId){
		String str = "select applnDignitariesFeedback.emp_appln_dignitaries_feedback_id as 'ID',  "
				+ "	emp.emp_id as 'empId',  "
				+ "	emp.emp_name as 'empName',  "
				+ "	empTitle.emp_title_id as 'empTitleId',  "
				+ "	empTitle.title_name as 'empTitleName',  "
				+ "	applnDignitariesFeedback.dignitaries_feedback as 'Comment' "
				+ "	from emp_appln_dignitaries_feedback as applnDignitariesFeedback "
				+ "	inner join emp ON emp.emp_id = applnDignitariesFeedback.emp_id "
				+ "	inner join emp_title as empTitle ON empTitle.emp_title_id = applnDignitariesFeedback.emp_title_id "
				+ "	where applnDignitariesFeedback.emp_appln_entries_id = :applnEntrieId and applnDignitariesFeedback.record_status = 'A' "
				+ "	and emp.record_status = 'A' and empTitle.record_status = 'A' order by empTitle.title_name asc";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str,Tuple.class);
			query.setParameter("applnEntrieId", applnEntrieId);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public Tuple getFinalInterviewEmpJobDetails(Integer applnEntrieId) {
		String str="select empJobDetails.is_with_pf as 'isWithPf', "
				+ " empJobDetails.is_with_gratuity as 'isWithGratuity', "
				+ " empJobDetails.is_esi_applicable as 'isEsiApplicable', "
				+ " empJobDetails.reporting_date as 'reportingDate', "
				+ " empJobDetails.joining_date as 'joiningDate', "
				+ " empJobDetails.recognised_exp_years as 'recognisedExpYears', "
				+ " empJobDetails.recognised_exp_months as 'recognisedExpMonths' "
				+ " from emp_job_details as empJobDetails " 
				+ " where empJobDetails.emp_appln_entries_id =:applnEntrieId "
				+ " and empJobDetails.record_status='A' ";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).setParameter("applnEntrieId", applnEntrieId).getSingleResultOrNull()).await().indefinitely();
	}

	public Tuple getFinalInterviewEmpPayScaleDetails(Integer applnEntrieId) {
		String str="select DISTINCT empPayScaleDetails.emp_pay_scale_details_id as 'empPayScaleDetailsId',empPayScaleDetails.pay_scale_type as 'payScaleType', "
				+ " empPayScaleDetails.emp_pay_scale_matrix_detail_id as 'empPayScaleMatrixDetailId', "
				+ " empPayScaleDetails.emp_daily_wage_slab_id as 'empDailyWageSlabId', "
				+ " empPayScaleDetails.gross_pay as 'grossPay', "
				+ " empPayScaleDetails.wage_rate_per_type as 'wageRatePerType' "
				+ " from emp_pay_scale_details as empPayScaleDetails "
				+ " where empPayScaleDetails.emp_appln_entries_id =:applnEntrieId "
				+ " and empPayScaleDetails.record_status = 'A'  and empPayScaleDetails.is_current =1 ";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).setParameter("applnEntrieId", applnEntrieId).getSingleResultOrNull()).await().indefinitely();
	}

	public Tuple getFinalInterviewEmpPayScaleMatrixDetail(Integer empPayScaleMatrixDetailId) {
		String str="select empPayScaleGrade.emp_pay_scale_grade_id as 'gradeId', "
				+ " empPayScaleGrade.grade_name as 'gradeName', "
				+ " empPayScaleGradeMapping.pay_scale_revised_year as 'revisedYear', "
				+ " empPayScaleGradeMappingDetail.emp_pay_scale_grade_mapping_detail_id as 'levelId', "
				//	+ " empPayScaleGradeMappingDetail.pay_scale_level as 'levelName', " // query changed
				+ " empPayScaleGradeMappingDetail.pay_scale as 'payScale', "
				+ " empPayScaleLevel.emp_pay_scale_level_id as scaleLevelId," // query changed
				+ " empPayScaleLevel.emp_pay_scale_level as scaleLevel," // query changed
				+ " empPayScaleMatrixDetail.emp_pay_scale_matrix_detail_id as 'cellId', "
				+ " empPayScaleMatrixDetail.level_cell_no as 'cellName', "
				+ " empPayScaleMatrixDetail.level_cell_value as 'cellValue' "
				+ " from emp_pay_scale_matrix_detail as empPayScaleMatrixDetail "
				+ " inner join emp_pay_scale_grade_mapping_detail  as empPayScaleGradeMappingDetail "
				+ " ON empPayScaleGradeMappingDetail.emp_pay_scale_grade_mapping_detail_id	= empPayScaleMatrixDetail.emp_pay_scale_grade_mapping_detail_id "
				+ " inner join emp_pay_scale_level as empPayScaleLevel"  // query changed
				+ " ON empPayScaleLevel.emp_pay_scale_level_id = empPayScaleGradeMappingDetail.emp_pay_scale_level_id" // query changed
				+ " inner join emp_pay_scale_grade_mapping as empPayScaleGradeMapping "
				+ " ON empPayScaleGradeMapping.emp_pay_scale_grade_mapping_id= empPayScaleGradeMappingDetail.emp_pay_scale_grade_mapping_id  "
				+ " inner join emp_pay_scale_grade as empPayScaleGrade "
				+ " ON empPayScaleGrade.emp_pay_scale_grade_id = empPayScaleGradeMapping.emp_pay_scale_grade_id "
				+ " where empPayScaleMatrixDetail.emp_pay_scale_matrix_detail_id =:empPayScaleMatrixDetailId  "
				+ " and empPayScaleMatrixDetail.record_status = 'A' "
				+ " and empPayScaleGradeMappingDetail.record_status = 'A' "
				+ " and empPayScaleGradeMapping.record_status = 'A' "
				+ " and empPayScaleGrade.record_status = 'A' ";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).setParameter("empPayScaleMatrixDetailId", empPayScaleMatrixDetailId).getSingleResultOrNull()).await().indefinitely();
	}

	public List<Tuple> getFinalInterviewPayScaleDetailComponent(Integer empPayScaleDetailsId){
		String str = "select SC.emp_pay_scale_components_id as 'empPayScaleComponentsId',  "
				+ " SC.salary_component_name AS 'salaryComponentName' ,  "
				+ " SC.salary_component_short_name AS 'salaryComponentShortName' ,  "
				+ " SC.is_component_basic AS 'isComponentBasic',  "
				+ " SC.is_caculation_type_percentage AS 'isCaculationTypePercentage',  "
				+ " SC.percentage AS 'percentage', "
				+ " SC.pay_scale_type AS 'payScaleType',"
				+ " SC.salary_component_display_order AS 'displayOrder',"
				+ " empPayScaleDetailsComponents.emp_salary_component_value as 'amount',"
				+ " empPayScaleDetailsComponents.emp_pay_scale_details_components_id as 'empPayScaleDetailsComponentsId'"
				+ " from emp_pay_scale_details_components as empPayScaleDetailsComponents "
				+ " inner join emp_pay_scale_components as SC ON SC.emp_pay_scale_components_id = empPayScaleDetailsComponents.emp_pay_scale_components_id "
				+ " where empPayScaleDetailsComponents.emp_pay_scale_details_id=:empPayScaleDetailsId "
				+ " and empPayScaleDetailsComponents.record_status = 'A' and SC.record_status = 'A' ";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str,Tuple.class);
			query.setParameter("empPayScaleDetailsId", empPayScaleDetailsId);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public ErpTemplateDBO getOfferLetterTemplateData(String id) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<ErpTemplateDBO>() {
			@Override
			public ErpTemplateDBO onRun(EntityManager context) throws Exception {
				ErpTemplateDBO dberpTemplateDBO = context.find(ErpTemplateDBO.class, Integer.parseInt(id));
				return dberpTemplateDBO;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public Tuple printEmpApplnEntriesDetails(Integer applicationNumber) {
		String str=" select  applicant_name, "
				+ " erp_gender.gender_name , "
				+ " (year(now())- year(emp_appln_entries.dob)) as age, "
				+ " date(emp_appln_entries.dob) as dob, "
				+ " emp_appln_entries.application_no, "
				+ " date(emp_appln_entries.submission_date_time) as applied_date, "
				+ " emp_appln_subject_category.subject_category_name, "
				+ " emp_appln_subject_category.is_academic, "
				+ " emp_appln_entries.expected_salary, "
				+ " emp_designation.emp_designation_name, "
				+ " emp_job_details.recognised_exp_years, "
				+ " emp_job_details.recognised_exp_months, "
				+ " emp_job_details.joining_date, "
				+ " emp_appln_entries.is_selected,"
				+ " emp_title.title_name "
				+ " from emp_appln_entries "
				+ " inner join erp_gender on emp_appln_entries.erp_gender_id = erp_gender.erp_gender_id and erp_gender.record_status like 'A' "
				+ " inner join emp_appln_subject_category ON emp_appln_subject_category.emp_appln_subject_category_id = emp_appln_entries.emp_appln_subject_category_id "
				+ " left join emp_designation ON emp_designation.emp_designation_id = emp_appln_entries.emp_designation_id "
				+ " left join emp_title ON emp_title.emp_title_id = emp_appln_entries.title_id and emp_title.record_status ='A' "
				+ " inner join emp_job_details on emp_appln_entries.emp_appln_entries_id = emp_job_details.emp_appln_entries_id and emp_job_details.record_status = 'A'"
				+ " where emp_appln_entries.record_status = 'A' "
				+ " and emp_appln_entries.application_no =:applicationNumber ";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).setParameter("applicationNumber", applicationNumber).getSingleResultOrNull()).await().indefinitely();
	}

	public List<Tuple> printQualificationLevelsDetails(Integer applicationNumber){
		String str = "select  emp_appln_educational_details.year_of_completion,  "
				+ " erp_qualification_level.qualification_level_name,  "
				+ " emp_appln_educational_details.grade_or_percentage,  "
				+ " emp_appln_educational_details.board_or_university, "
				+ " emp_appln_educational_details.qualification_others,"
				+ " emp_appln_educational_details.current_status "
				+ " from  emp_appln_educational_details  "
				+ " inner join emp_appln_entries ON emp_appln_entries.emp_appln_entries_id = emp_appln_educational_details.emp_appln_entries_id "
				+ " left join erp_qualification_level ON erp_qualification_level.erp_qualification_level_id = emp_appln_educational_details.erp_qualification_level_id "
				+ " where emp_appln_entries.application_no =:applicationNumber  "
				+ " and emp_appln_educational_details.record_status = 'A' "
				+ " order by erp_qualification_level.qualification_level_degree_order ";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str,Tuple.class);
			query.setParameter("applicationNumber", applicationNumber);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public Tuple printSalaryDetail(Integer applicationNumber) {
		String str="select  emp_appln_entries.application_no,emp_designation.emp_designation_name ,emp_pay_scale_details.gross_pay,emp_pay_scale_details.pay_scale_type,emp_pay_scale_details.wage_rate_per_type, "
				+ " emp_pay_scale_components.salary_component_name, "
				+ " emp_pay_scale_details_components.emp_salary_component_value as basic_value, "
				+ " emp_pay_scale_grade_mapping_detail.pay_scale,"
				+ "  emp_pay_scale_level.emp_pay_scale_level,"
				+ " emp_pay_scale_matrix_detail.level_cell_no"
				+ " from emp_pay_scale_details "
				+ " inner join emp_appln_entries ON emp_appln_entries.emp_appln_entries_id = emp_pay_scale_details.emp_appln_entries_id "
				+ " left join emp_designation ON emp_designation.emp_designation_id = emp_appln_entries.emp_designation_id "
				+ " inner join emp_pay_scale_details_components on emp_pay_scale_details_components.emp_pay_scale_details_id = emp_pay_scale_details.emp_pay_scale_details_id "
				+ " and emp_pay_scale_details_components.record_status = 'A'  "
				+ " inner join emp_pay_scale_components ON emp_pay_scale_components.emp_pay_scale_components_id = emp_pay_scale_details_components.emp_pay_scale_components_id "
				+ " and emp_pay_scale_components.is_component_basic=1 and emp_pay_scale_components.record_status like 'A' and emp_pay_scale_details.pay_scale_type =  emp_pay_scale_components.pay_scale_type  "
				+ " left join emp_pay_scale_matrix_detail ON emp_pay_scale_matrix_detail.emp_pay_scale_matrix_detail_id = emp_pay_scale_details.emp_pay_scale_matrix_detail_id "
				+ " left join emp_pay_scale_grade_mapping_detail ON emp_pay_scale_grade_mapping_detail.emp_pay_scale_grade_mapping_detail_id = emp_pay_scale_matrix_detail.emp_pay_scale_grade_mapping_detail_id "
				+ " left join emp_pay_scale_level ON emp_pay_scale_grade_mapping_detail.emp_pay_scale_level_id = emp_pay_scale_level.emp_pay_scale_level_id "
				+ " where emp_pay_scale_details.record_status = 'A' "
				+ " and emp_appln_entries.application_no =:applicationNumber and emp_pay_scale_details.is_current =1";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).setParameter("applicationNumber", applicationNumber).getSingleResultOrNull()).await().indefinitely();
	}

	public Tuple printSalaryDetailsByPayScaleType(Integer applicationNumber) {
		String str="select  emp_appln_entries.application_no,emp_pay_scale_details.pay_scale_type,emp_pay_scale_details.wage_rate_per_type "
				+ " from emp_pay_scale_details "
				+ " inner join emp_appln_entries ON emp_appln_entries.emp_appln_entries_id = emp_pay_scale_details.emp_appln_entries_id "
				+ " where emp_pay_scale_details.record_status like 'A' "
				+ " and emp_appln_entries.application_no =:applicationNumber and emp_pay_scale_details.is_current =1";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).setParameter("applicationNumber", applicationNumber).getSingleResultOrNull()).await().indefinitely();
	}

	public List<Tuple> printEmpApplnDignitariesFeedback(Integer applicationNumber) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select emp_title.title_name,emp.emp_name, dignitaries_feedback  "
						+ " from emp_appln_dignitaries_feedback  "
						+ " inner join emp ON emp.emp_id = emp_appln_dignitaries_feedback.emp_id  "
						+ " inner join emp_title ON emp_title.emp_title_id = emp_appln_dignitaries_feedback.emp_title_id  "
						+ " inner join emp_appln_entries ON emp_appln_entries.emp_appln_entries_id = emp_appln_dignitaries_feedback.emp_appln_entries_id  "
						+ " where emp_appln_dignitaries_feedback.record_status like 'A' "
						+ " and emp_appln_entries.application_no =:applicantNumber ";
				Query query = context.createNativeQuery(str, Tuple.class);
				query.setParameter("applicantNumber", applicationNumber);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> printInterviewScoreDetails(Integer applicationNumber){
		String str = " select  distinct"
				+ " emp.emp_name,"
				+ " emp_interview_university_externals.panelist_name,"
				+ " erp_users.erp_users_id,"
				+ " emp_interview_university_externals.emp_interview_university_externals_id,"
				+ " emp_appln_interview_template.emp_appln_interview_template_id as 'template_id',"
				+ " emp_appln_interview_template_group.emp_appln_interview_template_group_id as 'group_id',"
				+ " emp_appln_interview_template_group.template_group_heading as 'group_heading',"
				+ " emp_appln_interview_template_group.heading_order_no as 'group_order',"
				+ " emp_appln_interview_template_group_details.emp_appln_interview_template_group_details_id as 'detail_id',"
				+ " emp_appln_interview_template_group_details.parameter_name as 'detail_parameter',"
				+ " emp_appln_interview_template_group_details.parameter_order_no as 'detail_order',"
				+ " emp_appln_interview_template_group_details.parameter_max_score as 'detail_max_score',"
				+ " emp_appln_interview_score_details.score_entered as 'obtained_score',"
				+ " emp_appln_interview_score_details.emp_appln_interview_score_details_id as 'details_id',"
				+ " emp_appln_interview_score.emp_appln_interview_score_id as 'score_entry_id',"
				+ " emp_appln_entries.application_no,"
				+ " emp_appln_interview_schedules.interview_round,"
				+ " if(emp_appln_interview_panel.is_internal_panel = 0 ,'External','Internal') as type"
				+ " from emp_appln_interview_score_details"
				+ " inner join emp_appln_interview_score ON emp_appln_interview_score.emp_appln_interview_score_id = emp_appln_interview_score_details.emp_appln_interview_score_id"
				+ " and emp_appln_interview_score.record_status = 'A'"
				+ " inner join emp_appln_entries ON emp_appln_entries.emp_appln_entries_id = emp_appln_interview_score.emp_appln_entries_id and emp_appln_entries.record_status = 'A'"
				+ " inner join emp_appln_interview_template_group_details ON emp_appln_interview_template_group_details.emp_appln_interview_template_group_details_id = emp_appln_interview_score_details.emp_appln_interview_template_group_details_id"
				+ " inner join emp_appln_interview_template_group ON emp_appln_interview_template_group.emp_appln_interview_template_group_id = emp_appln_interview_template_group_details.emp_appln_interview_template_group_id"
				+ " inner join emp_appln_interview_template ON emp_appln_interview_template.emp_appln_interview_template_id = emp_appln_interview_template_group.emp_appln_interview_template_id"
				+ " inner join emp_appln_interview_schedules ON emp_appln_interview_schedules.emp_appln_interview_schedules_id = emp_appln_interview_score.emp_appln_interview_schedules_id"
				+ " inner join emp_appln_interview_panel ON emp_appln_interview_panel.emp_appln_interview_schedules_id = emp_appln_interview_schedules.emp_appln_interview_schedules_id"
				+ " and emp_appln_interview_panel.record_status='A'"
				+ " and (emp_appln_interview_panel.erp_users_id=emp_appln_interview_score.erp_users_id or emp_appln_interview_panel.emp_interview_university_externals_id=emp_appln_interview_score.emp_interview_university_externals_id)"
				+ " left join  erp_users ON erp_users.erp_users_id = emp_appln_interview_score.erp_users_id"
				+ " left join emp ON emp.emp_id = erp_users.emp_id"
				+ " left join emp_interview_university_externals ON emp_interview_university_externals.emp_interview_university_externals_id = emp_appln_interview_score.emp_interview_university_externals_id"
				+ " where emp_appln_interview_score_details.record_status = 'A'"
				+ " and emp_appln_entries.application_no= :applicantNumber"
				+ " order by heading_order_no,parameter_order_no  ";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str,Tuple.class);
			query.setParameter("applicantNumber", applicationNumber);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public Tuple printTotalExperience(Integer applicationNumber) {
		String str = "select  (sum( work_experience_years)+(floor(sum(work_experience_month )/12))) as experience_yearss, "
				+ " mod(sum(work_experience_month ),12) as experience_monthss  "
				+ " FROM emp_appln_work_experience  "
				+ " inner join emp_appln_work_experience_type ON emp_appln_work_experience_type.emp_appln_work_experience_type_id = emp_appln_work_experience.emp_appln_work_experience_type_id  "
				+ " inner join emp_appln_entries ON emp_appln_entries.emp_appln_entries_id = emp_appln_work_experience.emp_appln_entries_id "
				+ " where emp_appln_work_experience.record_status = 'A' "
				+ " and emp_appln_entries.application_no =:applicationNumber ";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).setParameter("applicationNumber", applicationNumber).getSingleResultOrNull()).await().indefinitely();
	}

	public Tuple empApplnEntries(Integer applicationNumber) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<Tuple>() {
			@Override
			public Tuple onRun(EntityManager context) throws Exception {
				String str="select applnEntries.emp_appln_entries_id as 'applnId', "
						+ " applnEntries.application_no as 'applnNo', "
						+ " applnEntries.applicant_name as 'applnName', "
						//						+ " applnEntries.is_selected as 'isSelected', "
						+ " employeeCategory.emp_employee_category_id as 'employeeCategoryId', "
						+ " employeeCategory.employee_category_name as 'employeeCategoryName', "
						+ " employeeCategory.record_status as 'empCategoryRecordStatus', "
						+ " erpCampus.erp_campus_id as 'erpCampusId', "
						+ " erpCampus.campus_name as 'erpCampusName', "
						+ " erpCampus.record_status as 'erpCampusRecordStatus', "
						+ " employeeJobCategory.emp_employee_job_category_id as 'employeeJobCategoryId', "
						+ " employeeJobCategory.employee_job_name as 'employeeJobCategoryName', "
						+ " employeeJobCategory.record_status as 'empJobCategoryRecordStatus', "
						+ " empDesignation.emp_designation_id as 'empDesignationId', "
						+ " empDesignation.emp_designation_name as 'empDesignationName', "
						+ " empDesignation.record_status as 'empDesignationRecordStatus', "
						+ " empTitle.emp_title_id as 'empTitleId', "
						+ " empTitle.title_name as 'empTitleName', "
						+ " empTitle.record_status as 'empTitleRecordStatus', "
						+ " empApplnSubjectCategory.emp_appln_subject_category_id as 'empApplnSubjectCategoryId', "
						+ " empApplnSubjectCategory.subject_category_name as 'empApplnSubjectCategoryName', "
						+ " empApplnSubjectCategory.record_status as 'empApplnSubjectCategoryRecordStatus', "
						+ " empApplnSubjectCategorySpecialization.emp_appln_subject_category_specialization_id as 'empApplnSubjectCategorySpecializationId', "
						+ " empApplnSubjectCategorySpecialization.subject_category_specialization_name as 'empApplnSubjectCategorySpecializationName', "
						+ " empApplnSubjectCategorySpecialization.record_status as 'empApplnSubjectCategorySpecializationRecordStatus', "
						+ " applnEntries.offer_letter_url as 'offerLetterUrl' "
						+ " from emp_appln_entries as applnEntries "
						+ " inner join emp_employee_category as employeeCategory "
						+ " ON employeeCategory.emp_employee_category_id = applnEntries.emp_employee_category_id "
						+ " left join erp_campus as erpCampus "
						+ " ON erpCampus.erp_campus_id = applnEntries.erp_campus_id "
						+ " left join emp_employee_job_category as employeeJobCategory  "
						+ " ON employeeJobCategory.emp_employee_job_category_id = applnEntries.emp_employee_job_category_id  "
						+ " left join emp_designation as empDesignation "
						+ " ON empDesignation.emp_designation_id = applnEntries.emp_designation_id  "
						+ " left join emp_title as empTitle "
						+ " ON empTitle.emp_title_id = applnEntries.title_id  "
						+ " left join emp_appln_subject_category as empApplnSubjectCategory  "
						+ " ON empApplnSubjectCategory.emp_appln_subject_category_id = applnEntries.emp_appln_subject_category_id  "
						+ " left join emp_appln_subject_category_specialization as empApplnSubjectCategorySpecialization  "
						+ " ON empApplnSubjectCategorySpecialization.emp_appln_subject_category_specialization_id = applnEntries.emp_appln_subject_category_specialization_id  "
						+ " where applnEntries.application_no =:applicantNumber "
						+ " and applnEntries.record_status = 'A' ";
				Query query = context.createNativeQuery(str.toString(), Tuple.class);
				query.setParameter("applicantNumber", applicationNumber);
				Tuple mappings=null;
				try {
					mappings = (Tuple)  Utils.getUniqueResult(query.getResultList());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return mappings;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public ErpTemplateDBO getTemplateContent(Integer id) {
		ErpTemplateDBO erpTemplateDBO = sessionFactory.withSession(s -> s.createQuery("FROM ErpTemplateDBO bo where bo.recordStatus='A' and bo.id=:id",ErpTemplateDBO.class)
				.setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
		return erpTemplateDBO;
	}

	public List<Tuple> getTempalteForSelectNo(boolean value) {
		String str = null;
		if(!Utils.isNullOrEmpty(value)) {
			if(value) {
				str = "   select erp_sms_template.erp_template_id as smsTemplateID,     "
						+ "erp_sms_template.template_name as smsTemplateName,     "
						+ "erp_sms_template.template_content as smsTemplateContent,    "
						+ "erp_email_template.erp_template_id as emailTemplateId,    "
						+ "erp_email_template.template_name as emailTemplateName ,    "
						+ "erp_email_template.template_code as emailTemplateCode,    "
						+ "erp_email_template.template_content as emailTemplateContent,   "
						+ "erp_email_template.mail_subject as mailSubject, erp_email_template.mail_from_name as mailFromName "
						+ "FROM erp_work_flow_process    "
						+ "LEFT JOIN erp_work_flow_process_notifications     "
						+ "ON erp_work_flow_process_notifications.erp_work_flow_process_id = erp_work_flow_process.erp_work_flow_process_id     "
						+ "AND erp_work_flow_process_notifications.record_status = 'A'     "
						+ "left join erp_template as erp_sms_template     "
						+ "ON if(erp_work_flow_process_notifications.is_sms_activated=1,erp_work_flow_process_notifications.sms_template_id,null)=erp_sms_template.erp_template_id    "
						+ "and erp_sms_template.record_status='A'    "
						+ "left join erp_template as erp_email_template     "
						+ "ON if(erp_work_flow_process_notifications.is_email_activated=1,erp_work_flow_process_notifications.email_template_id,null)=erp_email_template.erp_template_id    "
						+ "and erp_email_template.record_status='A'    "
						+ "WHERE     "
						+ "erp_work_flow_process.process_code = 'EMP_SELECTED'     "
						+ "AND erp_work_flow_process.record_status = 'A'     "
						+ "AND erp_work_flow_process_notifications.notification_code = 'EMP_SELECTED' ";
			}
			if(!value) {
				str = " select erp_sms_template.erp_template_id as smsTemplateID,     "
						+ "erp_sms_template.template_name as smsTemplateName,     "
						+ "erp_sms_template.template_content as smsTemplateContent,    "
						+ "erp_email_template.erp_template_id as emailTemplateId,    "
						+ "erp_email_template.template_name as emailTemplateName ,    "
						+ "erp_email_template.template_code as emailTemplateCode,    "
						+ "erp_email_template.template_content as emailTemplateContent   "
						+ "FROM erp_work_flow_process    "
						+ "LEFT JOIN erp_work_flow_process_notifications     "
						+ "ON erp_work_flow_process_notifications.erp_work_flow_process_id = erp_work_flow_process.erp_work_flow_process_id     "
						+ "AND erp_work_flow_process_notifications.record_status = 'A'     "
						+ "left join erp_template as erp_sms_template     "
						+ "ON if(erp_work_flow_process_notifications.is_sms_activated=1,erp_work_flow_process_notifications.sms_template_id,null)=erp_sms_template.erp_template_id    "
						+ "and erp_sms_template.record_status='A'    "
						+ "left join erp_template as erp_email_template     "
						+ "ON if(erp_work_flow_process_notifications.is_email_activated=1,erp_work_flow_process_notifications.email_template_id,null)=erp_email_template.erp_template_id    "
						+ "and erp_email_template.record_status='A'    "
						+ "WHERE     "
						+ "erp_work_flow_process.process_code = 'R2_NOT_SELECTED'     "
						+ "AND erp_work_flow_process.record_status = 'A'     "
						+ "AND erp_work_flow_process_notifications.notification_code = 'EMP_APPLN_R2_NOT_SELECTED'  ";
			}
		}
		String finalStr = str;
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(finalStr,Tuple.class);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public Tuple getErpWorkFlowProcessIdbyProcessCode(String processCode) {
		String queryString = " select w.erp_work_flow_process_id as erp_work_flow_process_id, w.applicant_status_display_text as applicant_status_display_text, w.application_status_display_text as application_status_display_text "
				+ " from erp_work_flow_process w where w.process_code= :processCode and w.record_status='A' ";
		return sessionFactory.withSession(s -> s.createNativeQuery(queryString,Tuple.class).setParameter("processCode",processCode).getSingleResultOrNull()).await().indefinitely();
	}

	public List<Tuple> getR2SelectTemplate() {
		String	str = "select erp_sms_template.erp_template_id as smsTemplateID,     "
				+ "erp_sms_template.template_name as smsTemplateName,     "
				+ "erp_sms_template.template_content as smsTemplateContent,    "
				+ "erp_email_template.erp_template_id as emailTemplateId,    "
				+ "erp_email_template.template_name as emailTemplateName ,    "
				+ "erp_email_template.template_code as emailTemplateCode,    "
				+ "erp_email_template.template_content as emailTemplateContent,   "
				+ "erp_email_template.mail_subject as mailSubject, erp_email_template.mail_from_name as mailFromName "
				+ "FROM erp_work_flow_process    "
				+ "LEFT JOIN erp_work_flow_process_notifications     "
				+ "ON erp_work_flow_process_notifications.erp_work_flow_process_id = erp_work_flow_process.erp_work_flow_process_id "
				+ "AND erp_work_flow_process_notifications.record_status = 'A'     "
				+ "left join erp_template as erp_sms_template     "
				+ "ON if(erp_work_flow_process_notifications.is_sms_activated=1,erp_work_flow_process_notifications.sms_template_id,null)=erp_sms_template.erp_template_id    "
				+ "and erp_sms_template.record_status='A'    "
				+ "left join erp_template as erp_email_template     "
				+ "ON if(erp_work_flow_process_notifications.is_email_activated=1,erp_work_flow_process_notifications.email_template_id,null)=erp_email_template.erp_template_id    "
				+ "and erp_email_template.record_status='A'    "
				+ "WHERE     "
				+ "erp_work_flow_process.process_code = 'R2_SELECTED'     "
				+ "AND erp_work_flow_process.record_status = 'A'     "
				+ "AND erp_work_flow_process_notifications.notification_code = 'EMP_APPLN_R2_SELECTED' ";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str,Tuple.class);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<Tuple> getLetterTemplate(Integer category) {
		String	str = "  select e.erp_template_id as ID, e.template_name as Text,e.emp_employee_category_id as categoryId, "
				+" e2.template_group_code as templateGroupCode,e.template_content as templateContent "
				+" from erp_template e "
				+" inner join erp_template_group e2 on e.erp_template_group_id = e2.erp_template_group_id "
				+" where  e.record_status='A' and e2.record_status='A' and e.emp_employee_category_id=:category "
				+" and (e2.template_group_code = 'EMP_OFFER_LETTER' OR e2.template_group_code = 'EMP_OFFER_REGRET_LETTER') "
				+" and e.template_type = 'LETTER'";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str,Tuple.class);
			query.setParameter("category", category);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public List<EmpApplnWorkExperienceDBO> getWorkExperienceView(String applicationId) {
		String str = " select dbo from EmpApplnWorkExperienceDBO dbo where dbo.recordStatus = 'A' and dbo.empApplnEntriesDBO.id =: applicationId ";
		List<EmpApplnWorkExperienceDBO> list = sessionFactory.withSession(s -> {
			Mutiny.Query<EmpApplnWorkExperienceDBO> query = s.createQuery(str,EmpApplnWorkExperienceDBO.class);
			query.setParameter("applicationId", Integer.parseInt(applicationId));
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

	public boolean saveOrUpdateEmpPay(EmpPayScaleDetailsDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.merge(dbo)).await().indefinitely();
		return true;
	}

	public List<ErpWorkFlowProcessDBO> getWorkFlowProcessAll() {
		String str = " select dbo from ErpWorkFlowProcessDBO dbo "
				+ " where dbo.recordStatus ='A' and dbo.processCode IN ('EMP_STAGE3_SELECTED','EMP_STAGE3_REJECTED','EMP_STAGE3_ONHOLD','EMP_OFFER_LETTER_GENERATED','EMP_OFFER_LETTER_REGENERATE','EMP_REGRET_LETTER_GENERATED')";
		return sessionFactory.withSession(s -> s.createQuery(str,ErpWorkFlowProcessDBO.class).getResultList()).await().indefinitely();	
	}

	public List<ErpWorkFlowProcessNotificationsDBO> getErpNotifications(List<Integer> processCodeList) {
		String query = " select distinct dbo from ErpWorkFlowProcessNotificationsDBO dbo where dbo.recordStatus = 'A' and dbo.erpWorkFlowProcessDBO.id in (:code)";
		return sessionFactory.withSession(s->s.createQuery(query,ErpWorkFlowProcessNotificationsDBO.class).setParameter("code", processCodeList).getResultList()).await().indefinitely();
	}

	public ErpTemplateDBO getOfferLetterTemplateById(Integer id) {
		String str = "select bo from ErpTemplateDBO bo where bo.recordStatus ='A' and bo.id=:id ";
		return sessionFactory.withSession(s->s.createQuery(str, ErpTemplateDBO.class).setParameter("id", id).getSingleResultOrNull()).await().indefinitely();
	}

	public String getHODComments(Integer applicationNumber) {
		String str = " Select emp_appln_interview_schedules.comments as  comments"
				+ " from emp_appln_entries "
				+ " inner join emp_appln_interview_schedules on emp_appln_interview_schedules.emp_appln_entries_id = emp_appln_entries.emp_appln_entries_id and emp_appln_interview_schedules.record_status = 'A' and emp_appln_interview_schedules.interview_round = 1"
				+ " where emp_appln_entries.application_no =:applicationNumber and  emp_appln_entries.record_status = 'A'";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,String.class).setParameter("applicationNumber", applicationNumber).getSingleResultOrNull()).await().indefinitely();	
	}
	
	public Tuple getLocationDept(Integer campDeptId) {
		String str = "select erp_location.erp_location_id,erp_department.erp_department_id  from erp_campus_department_mapping "
				+ " inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id"
				+ " inner join erp_location ON erp_location.erp_location_id = erp_campus.erp_location_id"
				+ " inner join erp_department ON erp_department.erp_department_id = erp_campus_department_mapping.erp_department_id"
				+ " where erp_campus_department_mapping.erp_campus_department_mapping_id = :campDeptId";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).setParameter("campDeptId", campDeptId).getSingleResultOrNull()).await().indefinitely();	
	}
}