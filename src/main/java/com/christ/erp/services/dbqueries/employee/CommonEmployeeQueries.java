package com.christ.erp.services.dbqueries.employee;

public class CommonEmployeeQueries {
	
	public static final String SELECT_EMPLOYEE_CATEGORY = "SELECT  MST.emp_employee_category_id AS 'ID', MST.employee_category_name AS 'Text', MST.is_employee_category_academic AS 'isEmployeeCategoryAcademic', MST.is_show_in_appln AS 'isShowInAppln' FROM emp_employee_category AS MST WHERE MST.record_status='A' ORDER BY MST.employee_category_name ASC";
	public static final String SELECT_PAY_SCALE_GRADE = "SELECT MST.emp_pay_scale_grade_id AS 'ID', MST.grade_name AS 'Text' FROM emp_pay_scale_grade AS MST WHERE MST.emp_employee_category_id=:employeeCategoryID ORDER BY MST.grade_name ASC";
	public static final String SELECT_APPLN_SUBJECT_CATEGORY = "select e.emp_appln_subject_category_id as ID, e.subject_category_name as 'Text' from emp_appln_subject_category e where e.record_status='A' and e.is_academic=:IsAcademic order by e.subject_category_name asc";
	public static final String SELECT_APPLN_SUBJECT_CATEGORY_SPECIALIZATION = "select e.emp_appln_subject_category_specialization_id as ID, e.subject_category_specialization_name as 'Text', e.emp_appln_subject_category_id as subjectCategoryId from emp_appln_subject_category_specialization e where e.record_status='A' and e.emp_appln_subject_category_id in (:categoryIds) order by e.subject_category_specialization_name asc";
	public static final String SELECT_TITLE = "select e.emp_title_id as ID, e.title_name as 'Text' from emp_title e where e.record_status='A' order by e.title_name asc";
    public static final String SELECT_LEAVE_TYPE_LIST = "select ltype.emp_leave_type_id  AS 'ID', ltype.leave_type_name  AS 'Text' from emp_leave_type ltype where ltype.record_status = 'A' and ltype.is_apply_online=1";//and ltype.is_leave=1 
    public static final String SELECT_EMPLOYEE_JOB_CATEGORY = "SELECT JCL.emp_employee_job_category_id AS 'ID', JCL.employee_job_name AS 'Text', is_show_in_appln as showInAppln FROM emp_employee_job_category AS JCL WHERE JCL.emp_employee_category_id=:employeeCategoryID ORDER BY JCL.employee_job_name ASC";
	public static final String SELECT_CATEGORY_LIST="select  emp_appln_subject_category_id AS ID, subject_category_name AS 'Text' from emp_appln_subject_category where record_status= 'A'";
	public static final String SELECT_EMPLOYEE_TITLE_LIST = " select mst.emp_title_id as 'ID', mst.title_name as 'Text' from emp_title as mst where record_status= 'A' order by mst.title_name asc;";
	public static final String SELECT_EMPLOYEE_LIST = "select mst.emp_id AS 'ID', mst.emp_name as 'Text' from emp as mst where record_status= 'A' order BY mst.emp_name asc;";
	public static final String SELECT_EMPLOYEE_LIST1 = "select distinct emp.emp_id AS 'ID', emp.emp_name as 'Text' , d.emp_designation_name as 'designation', dep.department_name as 'department', c.campus_name as 'campus',c.erp_campus_id as 'campusId' "
			+ " from emp as emp "
			+ " left join emp_designation d on d.emp_designation_id=emp.emp_designation_id  AND d.record_status = 'A' "
			+ " inner join erp_campus_department_mapping cd on cd.erp_campus_department_mapping_id=emp.erp_campus_department_mapping_id  AND cd.record_status = 'A' "
			+ " inner join erp_department dep on dep.erp_department_id=cd.erp_campus_department_mapping_id  AND dep.record_status = 'A' "
			+ " inner join erp_campus c on c.erp_campus_id=cd.erp_campus_id  AND c.record_status = 'A' "
			+ " and emp.record_status= 'A' order BY emp.emp_name asc ";
	public static final String SELECT_CAMPUS_DEANERY_DEPARTMENT = " FROM ErpCampusDepartmentMappingDBO E WHERE E.recordStatus='A' ORDER BY E.erpCampusDBO.id ASC ";	
	/*--------- DocumentVerification---------*/
	public static final String SELECT_APPLICANT_DETAILS ="select DISTINCT e.emp_appln_entries_id as ApplicantId, e.applicant_name as ApplicantName , ecat.employee_category_name as PostApplied, e1.campus_name as Campus,e2.country_name as Country,ecat.emp_employee_category_id as CategoryId from emp_appln_entries e " + 
			"	inner join emp_employee_category ecat on ecat.emp_employee_category_id=e.emp_employee_category_id" + 
			"	left join erp_campus e1 on e1.erp_campus_id=e.erp_campus_id" + 
			"	inner join emp_appln_personal_data per on per.emp_appln_entries_id=e.emp_appln_entries_id" + 
			"	inner join erp_country e2 on e2.erp_country_id=per.erp_country_id where e.application_no=:applicantNumber and e.record_status='A'";
	public static final String SELECT_DOCUMENT_CHECK_LIST_FOR_INDIAN_APPLICANTS ="from EmpDocumentChecklistMainDBO dbo where dbo.recordStatus='A' and dbo.isForeignNationalDocumentChecklist=false";
	public static final String SELECT_DOCUMENT_CHECK_LIST_FOR_FOREIGN_APPLICANTS ="from EmpDocumentChecklistMainDBO dbo where dbo.recordStatus='A' and dbo.isForeignNationalDocumentChecklist=true";
	public static final String SELECT_APPLICATION_NUMBERS="select e.emp_appln_entries_id as ID , e.application_no as 'Text' from emp_appln_entries e where e.application_no like :applicationId  and e.record_status='A'";
	public static final String SELECT_APPLICATION_NUMBER_LENGTH="select e.emp_appln_number_generation_id as ID , e.appln_number_from as 'Text' from emp_appln_number_generation e where e.is_current_range=true and e.record_status='A'";
	public static final String GET_EMP_ELIGIBILITY_EXAM_LIST = "select emp_eligibility_exam_list_id as 'ID', eligibility_exam_name as 'Text' from emp_eligibility_exam_list where record_status='A' order by eligibility_exam_name asc";
	public static final String SELECT_DESIGNATION_LIST="select  emp_designation_id AS ID, emp_designation_name AS 'Text' from emp_designation where record_status= 'A' and emp_employee_category_id=:employeeCategoryId order by  emp_designation_name asc";
	public static final String SELECT_GRADES_AND_REVISED_YEAR_BY_CATAGORY_ID="select e1.emp_pay_scale_grade_id as ID, e1.grade_name as Text, max(e.pay_scale_revised_year) as RevisedYear " + 
			" from emp_pay_scale_grade_mapping e " + 
			" inner join emp_pay_scale_grade e1 on e.emp_pay_scale_grade_id=e1.emp_pay_scale_grade_id " + 
			" where e1.emp_employee_category_id=:employeeCategoryID " + 
			" AND e.record_status='A' AND e1.record_status='A' "+
			" group by e1.emp_pay_scale_grade_id, e1.grade_name ";
	public static final String SELECT_LEVEL_BY_GRADE_ID_AND_REVISED_YEAR="SELECT e.emp_pay_scale_grade_mapping_detail_id AS ID, e.pay_scale as PayScale, " + 
			" epl.emp_pay_scale_level_id as LevelId, epl.emp_pay_scale_level as ScaleLevel " + // query changed
			" FROM  emp_pay_scale_grade_mapping_detail e " + 
			" inner join emp_pay_scale_grade_mapping e1" +
			" on e.emp_pay_scale_grade_mapping_id = e1.emp_pay_scale_grade_mapping_id " +
			" inner join emp_pay_scale_level epl on e.emp_pay_scale_level_id = epl.emp_pay_scale_level_id" +  // query changed
			" where e1.emp_pay_scale_grade_id =:GradeID AND e1.pay_scale_revised_year =:RevisedYear AND e.record_status='A' and e1.record_status='A' ";
	public static final String SELECT_CELLS_AND_PAY_SCALE_BY_LEVEL_ID="SELECT e.emp_pay_scale_matrix_detail_id as ID, e.level_cell_no as Text, e.level_cell_value as CellValue" + 
			" from emp_pay_scale_matrix_detail e " + 
			" INNER JOIN emp_pay_scale_grade_mapping_detail  e1 " + 
			" on e1.emp_pay_scale_grade_mapping_detail_id = e.emp_pay_scale_grade_mapping_detail_id " + 
			" WHERE e.emp_pay_scale_grade_mapping_detail_id =:LevelId " + 
			" and e.record_status='A' ";
	public static final String SELECT_EMPLOYEE_GROUP_LIST="SELECT  gru.emp_employee_group_id AS ID, gru.employee_group_name AS Text FROM emp_employee_group gru where gru.emp_employee_category_id=:employeeCategoryID and gru.record_status='A'"; 
	public static final String SELECT_DESIGNATION_BY_CATAGORY_ID="SELECT e.emp_designation_id AS ID, e.emp_designation_name AS Text FROM  emp_designation e where e.emp_employee_category_id=:EmployeeCategoryID AND e.record_status='A' order by e.emp_designation_name";
	public static final String SELECT_APPOINTMENT_LATTER_PENDING_EMPLOYEES="select e.emp_name as EmployeeName,  e.emp_no as EmployeeID, e.doj as JoiningDate, e1.wait_for_document as DocumentSubmissionStatus from emp e \r\n" + 
			"inner join emp_document_verification e1 on e1.emp_id=e.emp_id\r\n" + 
			"where e.doj>=03/12/2020  and e1.is_in_draft_mode=false and  e.record_status='A'";
	public static final String SELECT_CAMPUS_BY_LOC = "select camp.erp_campus_id AS ID, camp.campus_name AS Text from erp_campus camp  where camp.record_status='A'  and camp.erp_location_id=:locId ORDER BY camp.campus_name ";
	public static final String GET_EMPLOYEE_LIST_BY_DEAN_CAMP_DEPT = " select emp.emp_id as ID, emp.emp_name as Text from emp " + 
			" inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id " + 
			" inner join erp_department ON erp_department.erp_department_id = erp_campus_department_mapping.erp_department_id " + 
			" inner join erp_deanery ON erp_deanery.erp_deanery_id =  erp_department.erp_deanery_id " + 
			" inner join  erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id " + 
			" where erp_deanery.erp_deanery_id in(:ids) ORDER BY emp.emp_name ";
	public static final String GET_EMPLOYEE_LIST_BY_DEAN = " select emp.emp_id as ID,emp.emp_name as TEXT from emp " + 
			" inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id " + 
			" inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id " + 
			" inner join erp_location on erp_location.erp_location_id = erp_campus.erp_location_id " + 
			" where emp.record_status='A' and erp_campus_department_mapping.record_status='A' and erp_campus.record_status='A' and erp_location.record_status='A' " + 
			" and erp_campus_department_mapping.erp_campus_department_mapping_id in(:ids) or erp_location.erp_location_id in(:locIds) ORDER BY emp.emp_name ";
	public static final String GET_EMPLOYEE_LIST_BY_LOCATION = "select emp.emp_id as ID,emp.emp_name as TEXT from emp " + 
			" inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id " + 
			" inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id " + 
			" inner join erp_location on erp_location.erp_location_id = erp_campus.erp_location_id " + 
			" where emp.record_status='A' and erp_campus_department_mapping.record_status='A' and erp_campus.record_status='A' and erp_location.record_status='A' " + 
			" and erp_location.erp_location_id=1 " ;
	public static final String SELECT_CAMPUS_DEANERY_DEPARTMENT_BY_LOC = " FROM ErpCampusDepartmentMappingDBO E WHERE E.recordStatus='A' and E.erpCampusDBO.erpLocationDBO.id=:locId  ORDER BY E.erpCampusDBO.campusName ASC ";

	 public static final String SELECT_LEAVE_TYPE_LIST1 = "select ltype.emp_leave_type_id  AS 'ID', ltype.leave_type_name  AS 'Text', ltype.is_leave_type_document_required As 'DOCUMENTREQUIRED' from emp_leave_type ltype where ltype.record_status = 'A' and ltype.is_leave_exemption=0 ";
	 public static final String SELECT_SUBJ_SPECIALIZATION_PREF_SUBJ_CATEGORY ="select Distinct subjectCategory.emp_appln_subject_category_id AS 'ID' ,subjectCategory.subject_category_name AS 'Text'  " +
				" from emp_appln_subj_specialization_pref subjectPref "+
				" inner join emp_appln_entries e ON e.emp_appln_entries_id = subjectPref.emp_appln_entries_id "+
				" inner join emp_appln_subject_category subjectCategory ON subjectCategory.emp_appln_subject_category_id = subjectPref.emp_appln_subject_category_id "+
				" where subjectCategory.record_status='A' and subjectPref.record_status='A' "+
				" and e.application_no=:applicationNumber and e.record_status='A'";	
	 public static final String SELECT_APPLICANT_NAME="select e.emp_appln_entries_id as ID , e.applicant_name as 'Text',e.application_no as application_no from emp_appln_entries e where e.record_status='A' and e.applicant_name like :applicantName order by e.applicant_name";
	 public static final String SELECT_APPLICANT_DETAILS_BY_NAME ="select DISTINCT e.emp_appln_entries_id as ApplicantId, e.applicant_name as ApplicantName , ecat.employee_category_name as PostApplied, e1.campus_name as Campus,e2.country_name as Country,ecat.emp_employee_category_id as CategoryId ,"+
				 	"   e.application_no as application_no from emp_appln_entries e " + 
					"	inner join emp_employee_category ecat on ecat.emp_employee_category_id=e.emp_employee_category_id" + 
					"	left join erp_campus e1 on e1.erp_campus_id=e.erp_campus_id" + 
					"	inner join emp_appln_personal_data per on per.emp_appln_entries_id=e.emp_appln_entries_id" + 
					"	inner join erp_country e2 on e2.erp_country_id=per.erp_country_id where e.applicant_name=:applicant_name and e.record_status='A'";
	 public static final String SELECT_DEPARTMENT_ON_CAMPUS = " select dpt.erp_department_id as ID , dpt.department_name as Text from erp_department dpt " +
			 " inner join erp_department_category dptCtr on dptCtr.erp_department_category_id = dpt.erp_department_category_id " +
			 " where dptCtr.erp_campus_id =:campusId and dpt.record_status = 'A' and dptCtr.record_status = 'A' ";
	 public static final String SELECT_APPLICATION_NAMES = "select e.emp_appln_entries_id AS 'ID', e.applicant_name as 'Text',e.application_no as 'APPLICATION_NO' from emp_appln_entries as e "+
			 " where e.applicant_name like (:ApplicantName) and e.record_status= 'A'  order BY e.applicant_name asc; ";
	 public static final String SELECT_LEAVE_TYPE_LIST2 = "select ltype.emp_leave_type_id  AS 'ID', ltype.leave_type_name  AS 'Text', ltype.is_leave_type_document_required As 'DOCUMENTREQUIRED' from emp_leave_type ltype where ltype.record_status = 'A' and ltype.is_leave_exemption=1 ";
	 public static final String SELECT_LEAVE_TYPE_FOR_ONLINE = "select ltype.emp_leave_type_id  AS 'ID', ltype.leave_type_name  AS 'Text', ltype.is_leave_type_document_required As 'DOCUMENTREQUIRED' from emp_leave_type ltype where ltype.record_status = 'A' and ltype.is_apply_online=1 ";
	 public static final String SELECT_TEMPLATE_LIST = "select temp.erp_template_id as ID, temp.template_name as TEXT  from erp_template temp \r\n" + 
		 		"	 		inner join erp_template_group grp on temp.erp_template_group_id = grp.erp_template_group_id\r\n" + 
		 		"	 		where grp.record_status='A' and temp.record_status='A'\r\n" + 
		 		"	 		and grp.template_group_name <> 'Appointment Letter'\r\n" + 
		 		"	 		and grp.template_group_name <> 'Offer Letter'";
}
