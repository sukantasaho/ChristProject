package com.christ.erp.services.dbqueries.employee;

public class LeaveQueries {
	public static final String LEAVE_TYPE_MAPPING_SEARCH_ALL ="select LT.emp_leave_type_id AS `ID`, LT.leave_type_name AS `leaveTypeName` , LT.leave_type_code AS `leaveTypeCode`, LT.is_apply_online  AS `isApplyOnline`, LT.is_leave_exemption AS `Exemption`,LT.is_leave AS `CanAllotLeave` FROM emp_leave_type AS LT where LT.record_status='A'";
//	public static final String LEAVE_ALLOTMENT_MAPPING_SEARCH_ALL =" select distinct allot.emp_leave_category_name AS 'LeaveCategoryName', " + 
//             " 	allot.leave_initialize_month AS 'Month' from emp_leave_allotment allot " + 
//             " 	left join emp_leave_type lType on lType.emp_leave_type_id = allot.emp_leave_type_id " + 
//             " 	where (lType. record_status='A' and allot.record_status='A') or  allot.record_status='A' ";
	public static final String LEAVE_ALLOTMENT_MAPPING_SEARCH_ALL ="  select distinct CA.emp_leave_category_allotment_id as 'id' ,CA.emp_leave_category_allotment_name AS 'LeaveCategoryName',   " + 
			"   CA.leave_initialize_month AS 'Month' from emp_leave_category_allotment CA   " + 
			"   inner join emp_leave_category_allotment_details CAD on " + 
			"   CA.emp_leave_category_allotment_id = CAD.emp_leave_category_allotment_id and CAD.record_status='A'" + 
			"   left join emp_leave_type lType on lType.emp_leave_type_id = CAD.emp_leave_type_id and lType. record_status='A'   " + 
			"   where CA.record_status='A' ";
	public static final String LEAVE_EMPLOYEE_DETAILS ="select distinct emp.emp_id AS 'id', emp.emp_name as 'name' , d.emp_designation_name as 'designation', dep.department_name as 'department', c.campus_name as 'campus' ,emp.emp_no as 'empNo'  " + 
			"	from emp as emp " + 
			"	left join emp_designation d on d.emp_designation_id=emp.emp_designation_id  AND d.record_status = 'A' " + 
			"	inner join erp_campus_department_mapping cd on cd.erp_campus_department_mapping_id=emp.erp_campus_department_mapping_id  AND cd.record_status = 'A' " + 
			"	inner join erp_department dep on dep.erp_department_id=cd.erp_department_id  AND dep.record_status = 'A' " + 
			"	inner join erp_campus c on c.erp_campus_id=cd.erp_campus_id  AND c.record_status = 'A' " + 
			"	where emp.emp_id =:empId and emp.record_status= 'A' order BY emp.emp_name asc ";
	public static final String LEAVE_EMPLOYEE_DETAILS1 ="select distinct emp.emp_id AS 'id', emp.emp_name as 'name' , d.emp_designation_name as 'designation', dep.department_name as 'department', c.campus_name as 'campus',emp.emp_no as 'empNo' " + 
			"	from emp as emp " + 
			"	left join emp_designation d on d.emp_designation_id=emp.emp_designation_id  AND d.record_status = 'A' " + 
			"	inner join erp_campus_department_mapping cd on cd.erp_campus_department_mapping_id=emp.erp_campus_department_mapping_id  AND cd.record_status = 'A' " + 
			"	inner join erp_department dep on dep.erp_department_id=cd.erp_department_id  AND dep.record_status = 'A' " + 
			"	inner join erp_campus c on c.erp_campus_id=cd.erp_campus_id  AND c.record_status = 'A' " + 
			"	where emp.emp_name =:empName and emp.record_status= 'A' order BY emp.emp_name asc ";
	public static final String LEAVE_EMPLOYEE_LEAVE_DETAILS ="select e1.leave_type_code as 'LeaveTypeCode',e1.leave_type_name as 'LeaveTypeName', e.leaves_remaining as 'LeaveRemaining' "+ 
			"	from emp_leave_allocation e " + 
			"	inner join emp_leave_type e1 on e1.emp_leave_type_id=e.emp_leave_allocation_id and e1.record_status='A' " + 
			"	WHERE e.emp_id =:empId  and e.record_status='A' ";
	public static final String LEAVE_EMPLOYEE_LEAVE_DETAILS1 ="select e1.leave_type_code as 'LeaveTypeCode',e1.leave_type_name as 'LeaveTypeName', e.leaves_remaining as 'LeaveRemaining' "+ 
			"	from emp_leave_allocation e " + 
			"	inner join emp_leave_type e1 on e1.emp_leave_type_id=e.emp_leave_allocation_id and e1.record_status='A' " + 
			"	WHERE e.emp_name =:empName and e.record_status='A' ";
	public static  String  LEAVE_EMPLOYEE_LEAVE_DETAILS_NEW = "select distinct emp.emp_id AS 'id', emp.emp_name as 'name' , d.emp_designation_name as 'designation', "
			+ "dep.department_name as 'department',c.campus_name as 'campus' ,emp.emp_no as 'empNo', "
			+ "emp_job_details.is_sunday_working as 'isSundayWorking',emp_job_details.is_holiday_working as 'isHolidayWorking' from emp "
			+ "inner join emp_job_details on emp_job_details.emp_id = emp.emp_id "
			+ "inner join erp_users on erp_users.emp_id = emp.emp_id "
			+ "left join emp_designation d on d.emp_designation_id=emp.emp_designation_id  AND d.record_status = 'A'    "
			+ "inner join erp_campus_department_mapping cd on cd.erp_campus_department_mapping_id=emp.erp_campus_department_mapping_id  AND cd.record_status = 'A'    "
			+ "inner join erp_department dep on dep.erp_department_id=cd.erp_campus_department_mapping_id  AND dep.record_status = 'A'    "
			+ "inner join erp_campus c on c.erp_campus_id=cd.erp_campus_id  AND c.record_status = 'A'    "
			+ "where erp_users.erp_users_id =:user_id and emp.record_status= 'A' order BY emp.emp_name asc ";
}
