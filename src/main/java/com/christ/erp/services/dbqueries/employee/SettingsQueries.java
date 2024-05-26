package com.christ.erp.services.dbqueries.employee;

public class SettingsQueries {
	 public static final String PROCESS_AND_POSITION_MAPPING_GET_GRID_DATA = "select epr.emp_position_role_id as 'ID',epr.process_type as Process," + 
			 "  camp.campus_name as Campus from emp_position_role AS epr left join erp_campus as camp on camp.erp_campus_id = epr.erp_campus_id  where  epr.record_status='A' and camp.record_status='A'  order by camp.campus_name asc";	 
	 public static final String PROCESS_AND_POSITION_MAPPING_DUPLICATE_CHECK = "select bo from EmpPositionRoleDBO bo where bo.recordStatus='A' and bo.erpCampusId.id=:campusId and bo.processType=:processType";
	 public static final String HOLIDAY_AND_EVENTS_GET_GRID_DATA = " select bo from EmpHolidayEventsDBO bo where bo.recordStatus='A'  order by bo.erpAcademicYearId.academicYearName";
	 public static final String HOLIDAY_AND_EVENTS_DUPLICATE_CHECK = "select bo from EmpHolidayEventsDBO bo where bo.erpAcademicYearId.id=:yearId and "
	 		+ "bo.empHolidayEventsTypeName = :type  and bo.erpLocationId.id=:locId  and   bo.holidayEventsStartDate =:date and bo.recordStatus='A'" ;
	 public static final String HOLIDAY_AND_EVENTS_EVENTS_VACATION_DUPLICATE_CHECK = "select bo from EmpHolidayEventsDBO bo where bo.erpAcademicYearId.id=:yearId and "
		 	+ "bo.empHolidayEventsTypeName = :type  and bo.erpLocationId.id=:locId  and   bo.holidayEventsStartDate =:startDate and bo.holidayEventsEndDate =:endDate and bo.recordStatus='A'" ;
	 public static final String GET_EMPLOYEE_IDS_LOC = " select bo from EmpHolidayEventsEmployeewiseDBO bo where bo.recordStatus='A' and bo.empHolidayEventsDBO.id=:id and bo.empDBO.erpCampusDepartmentMappingDBO.erpCampusDBO.erpLocationDBO.id=:locId";
	 public static final String GET_EMPLOYEE_IDS = " select emp FROM EmpDBO emp WHERE emp.recordStatus='A' and emp.erpCampusDepartmentMappingDBO.erpCampusDBO.erpLocationDBO.id=:locId";
}
