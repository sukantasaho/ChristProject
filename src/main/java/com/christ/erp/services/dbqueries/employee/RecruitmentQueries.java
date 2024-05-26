package com.christ.erp.services.dbqueries.employee;

public class RecruitmentQueries {
	public static final String RESEARCH_DETAILS_SEARCH_ALL ="SELECT  "+
			"       HDR.emp_appln_addtnl_info_heading_id AS 'ID',"+
			"       CTG.emp_employee_category_id AS 'CategoryID', "+
			"       CTG.employee_category_name AS 'CategoryText', " + 
    		"		HDR.addtnl_info_heading_name AS 'Group Heading', " + 
    		"		HDR.heading_display_order AS 'Display order', " + 
    		"		HDR.is_type_research AS 'Research' "+
    		"   	FROM emp_appln_addtnl_info_heading AS HDR " + 
    		"   	LEFT JOIN emp_employee_category AS CTG " + 
    		"   	ON CTG.emp_employee_category_id = HDR.emp_employee_category_id "+
			"   	WHERE HDR.record_status = 'A' ORDER BY HDR.is_type_research,CTG.employee_category_name,HDR.heading_display_order ASC";
	public static final String SELECT_RESEARCH_DETAILS_ALL = " select head from EmpApplnAddtnlInfoHeadingDBO head "
			+ " where head.recordStatus='A' order by head.headingDisplayOrder asc" ;
	public static final String SELECT_SUBJECT_LIST="select DISTINCT e1.subject_category_name as 'CATEGORY'  from emp_appln_subject_category_department e ,emp_appln_subject_category e1 where e.emp_appln_subject_category_id=e1.emp_appln_subject_category_id and  e.record_status='A'";
	public static final String SUBJECT_CATEGORY_DEPARTMENT_EDIT = "from EmpApplnSubjectCategoryDepartmentDBO scd where scd.subject.subjectCategory=:subjectCategory and scd.recordStatus='A'";
	public static final String SUBJECT_CATEGORY_DEPARTMENT_SAVE = "from EmpApplnSubjectCategoryDepartmentDBO scd where scd.subject.id=:ID and scd.recordStatus='A' ";
	public static final String SUBJECT_CATEGORY_DEPARTMENT_DELETE = "from EmpApplnSubjectCategoryDepartmentDBO scd where scd.subject.subjectCategory=:subjectCategory and scd.recordStatus='A'";
	public static final String SUBJECT_CATEGORY_DEPARTMENT_DUPLICATE = "from EmpApplnSubjectCategoryDepartmentDBO  where  subject.id=:category and recordStatus='A' and id not in (:recordIds)";
	public static final String SELECT_JOB_ADVERTISEMENT_LIST="select e1.emp_appln_advertisement_id as ID, e1.advertisement_no as advertisementNo,e1.advertisement_start_date as startDate, e1.advertisement_end_date as endDate,e2.academic_year_name as academicYear " + 
			" from emp_appln_advertisement  e1 " + 
			" inner join erp_academic_year e2 on e2.erp_academic_year_id = e1.erp_academic_year_id" + 
			" where e1.record_status='A'";
	public static final String SELECT_JOB_ADVERTISMENT = "from EmpApplnAdvertisementDBO where recordStatus='A' and advertisementNo=:advertisementNo";
	public static final String GET_EMP_APPLN_ADVERTISEMENT = "select empApplnAdvertisementDBO from EmpApplnAdvertisementDBO empApplnAdvertisementDBO  join empApplnAdvertisementDBO.empApplnAdvertisementImagesSet empApplnAdvertisementImages where empApplnAdvertisementDBO.recordStatus='A' and empApplnAdvertisementImages.recordStatus='A' and empApplnAdvertisementDBO.id=:id";
	
}
