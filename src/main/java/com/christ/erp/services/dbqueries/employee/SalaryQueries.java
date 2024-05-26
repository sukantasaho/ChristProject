package com.christ.erp.services.dbqueries.employee;

public class SalaryQueries {
    public static final String PAY_SCALE_MAPPING_SAVE_OR_UPDATE = "CALL payscale_sp_save_mapping_data(?, ?, ?, ?, ?);";
    public static final String PAY_SCALE_MAPPING_SELECT_HEADER = "SELECT \n" +
        "   HDR.emp_pay_scale_grade_mapping_id AS 'ID', \n" +
        "   HDR.emp_employee_category_id AS 'Category', \n" +
        "   HDR.emp_pay_scale_grade_id AS 'Grade', \n" +
        "   HDR.pay_scale_revised_year AS 'RevisedYear' \n" +
        "FROM emp_pay_scale_grade_mapping AS HDR \n" +
        "WHERE HDR.emp_pay_scale_grade_mapping_id=?;";
    public static final String PAY_SCALE_MAPPING_SELECT_DETAIL = "SELECT \n" +
        "   DTL.emp_pay_scale_grade_mapping_detail_id AS 'ID', \n" +
        "   DTL.pay_scale_level AS 'Level', \n" +
        "   DTL.pay_scale AS 'Scale', \n" +
        "   DTL.pay_scale_display_order AS 'Order' \n" +
        "FROM emp_pay_scale_grade_mapping_detail AS DTL \n" +
        "WHERE DTL.emp_pay_scale_grade_mapping_id=?;";
    public static final String PAY_SCALE_MAPPING_SEARCH_ALL = "SELECT\n"+  
    		" PSGM.emp_pay_scale_grade_mapping_id AS 'ID', \n"+ 
    		" PSG.emp_employee_category_id AS 'Category',\n"+  
    		" EC.employee_category_name as 'CategoryName',\n"+
    		" PSGM.emp_pay_scale_grade_id AS 'Grade',\n"+
    		" PSG.grade_name as 'GradeName',\n"+
    		" PSGM.pay_scale_revised_year AS 'RevisedYear'  \n"+
    		"FROM emp_pay_scale_grade_mapping AS PSGM  \n"+
    		"inner join emp_pay_scale_grade PSG ON PSG.emp_pay_scale_grade_id = PSGM.emp_pay_scale_grade_id \n"+
    		"inner join emp_employee_category as EC on EC.emp_employee_category_id = PSG.emp_employee_category_id \n"+
    		"WHERE  PSGM.record_status='A' order by EC.employee_category_name";
    public static final String PAY_SCALE_MAPPING_DELETE_DETAIL = "DELETE FROM emp_pay_scale_grade_mapping_detail \n" +
		" WHERE emp_pay_scale_grade_mapping_id = :header_id \n" +
		" AND emp_pay_scale_grade_mapping_detail_id NOT IN (:detail_ids)";
    public static final String SALARY_COMPONENT_SELECT_DETAIL ="select SC.emp_pay_scale_components_id AS ID, SC.salary_component_name AS salaryComponentName , SC.salary_component_short_name AS salaryComponentShortName,\r\n" + 
    		" SC.salary_component_display_order  AS salaryComponentDisplayOrder,is_component_basic AS isComponentBasic,SC.percentage  AS percentage,SC.is_caculation_type_percentage AS isCaculationTypePercentage,SC.pay_scale_type AS payScaleType FROM emp_pay_scale_components AS SC where SC.record_status='A' order by salaryComponentDisplayorder;  ";
    public static final String SALARY_COMPONENT_DUPLICATE_ALLOWANCETYPE = " from EmpPayScaleComponentsDBO where salaryComponentName=:allowanceType and payScaleType=:payScaleType and recordStatus='A' and id not in (:id)";
    public static final String SALARY_COMPONENT_DUPLICATE_DISPLAYORDER = "from EmpPayScaleComponentsDBO where  recordStatus='A' and salaryComponentDisplayOrder=:salaryComponentDisplayOrder and id not in (:id)";
    public static final String SALARY_COMPONENT_DUPLICATE_ISBASICSAVE = "from EmpPayScaleComponentsDBO where isComponentBasic=true and recordStatus='A' and payScaleType=:payScaleType ";
    public static final String SALARY_COMPONENT_DUPLICATE_ISBASICEDIT = "from EmpPayScaleComponentsDBO where isComponentBasic=true and recordStatus='A'and payScaleType=:payScaleType and id!=:id";
    public static final String PAYSCALE_DUPLICATE_CHECK = "SELECT HDR.emp_pay_scale_grade_mapping_id " + 
    		"FROM emp_pay_scale_grade_mapping AS HDR " + 
    		"INNER JOIN  emp_pay_scale_grade AS CTG " + 
    		"ON CTG.emp_pay_scale_grade_id = HDR.emp_pay_scale_grade_id " + 
    		"INNER JOIN emp_employee_category AS EC ON  EC.emp_employee_category_id = CTG.emp_employee_category_id " + 
    		"WHERE HDR.record_status = 'A' AND HDR.emp_pay_scale_grade_id=:gradeId AND HDR.pay_scale_revised_year=:revisedYear and  CTG.emp_employee_category_id=:categoryId and HDR.emp_pay_scale_grade_mapping_id not in (:id)";

}
