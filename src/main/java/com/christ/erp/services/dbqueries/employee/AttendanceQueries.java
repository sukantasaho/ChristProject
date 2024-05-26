package com.christ.erp.services.dbqueries.employee;

public class AttendanceQueries {
	public static final String TIME_ZONE_ENTRY_GRID_DATA = "SELECT \n" +
	    "   emp_time_zone.emp_time_zone_id AS 'ID', \n" +
	    "   emp_time_zone.time_zone_name AS 'TimeZoneName', \n" +
	    "   emp_time_zone.is_holiday_time_zone AS 'isholidaytimezone', \n" +
	    "   emp_time_zone.is_vacation_time_zone AS 'isvacationtimezone', \n" +
	    "   emp_time_zone.is_general_time_zone AS 'isgeneraltimezone' \n" +
//	    "   emp_time_zone.is_night_shift_time_zone AS 'isnightshifttimezone' \n" +
	    "   FROM emp_time_zone where emp_time_zone.record_status='A'";
	
	public static final String TIME_ZONE_ENTRY_DUPLICATE_CHECK=" from  EmpTimeZoneDBO  bo where bo.timeZoneName=:TimeZoneName and bo.recordStatus='A'";
	
//	public static final String VIEW_EMPLOYEE_ATTENDANCE_DATA="SELECT  a.attendance_date As date," +
//		"   dayname(a.attendance_date) As dayName,"+
//		"	a.in_time AS 'timeIn',"+
//		"   a.out_time AS 'timeOut', " +
//		"   a.is_next_day AS 'nextDay', " +
//		"   a.is_late_entry AS 'lateEntry', " +
//		"   a.is_early_exit AS 'earlyExit', " +
//		"   a.leave_session AS 'leaveSession', " +
//		"   a.holiday_events_session AS 'holidayOrVacationOrExemptionSession', " +
//		"   a.emp_leave_entry_id as 'leaveEntryId', " +
//	    "   a.emp_holiday_events_id as 'holidayEventsId', " +
//	    "   a.is_sunday_working as 'isSundayWorking', " +
//	    "   a.is_weekly_off as 'isWeeklyOff', " +
//	    "   a.is_exempted as 'isExempted', " +
//		"   t.leave_type_name AS 'leaveType', " +
//		"   t.leave_type_color_code_hexvalue AS 'leaveTypeColor', " +
//		"   h.emp_holiday_events_type_name AS 'empHolidayEventsType', " +
//		"   h.holiday_events_description AS 'holidayEventsDescription' " +
//		"   FROM emp_attendance a " +
//		"   left JOIN emp_leave_entry e ON e.emp_leave_entry_id = a.emp_leave_entry_id  AND e.record_status = 'A' " +
//		"   left JOIN emp_leave_type t ON t.emp_leave_type_id = e.emp_leave_type_id   AND t.record_status = 'A' " +
//		"   left JOIN emp_holiday_events h ON h.emp_holiday_events_id = a.emp_holiday_events_id   AND h.record_status = 'A' " +
//		"   WHERE   a.emp_id =:empId " +
//		"   AND a.attendance_date BETWEEN date(:startDate) AND date(:endDate) " +
//		"   AND a.record_status = 'A'  order by date";
	
	public static final String DATE_FOR_VACATION_PUNCHING_GRID_DATA = " from EmpDateForVacationPunchingDBO d where d.recordStatus='A' ";
	
	public static final String DATE_FOR_VACATION_PUNCHING_DUPLICATE_CHECK = " from EmpDateForVacationPunchingDBO E where E.empEmployeeCategoryDBO.id=:EmployeeCategoryID and " + 
			"   (( E.vacationPunchingStartDate  >= :StartDate  and E.vacationPunchingEndDate <= :EndDate )) "+
		    "   and E.recordStatus='A' and E.id not in (:ID)  ";
	
//	public static final String VIEW_EMPLOYEE_ATTENDANCE_DATA_CUMULATIVE = "SELECT " +
//	    "	a.attendance_date as date, " +
//	    "   dayname(a.attendance_date) As dayName, " +
//		"	a.in_time AS 'timeIn', " +
//		"	a.out_time AS 'timeOut', " +
//		"	a.is_late_entry AS 'lateEntry', " +
//		"	a.is_early_exit AS 'earlyExit', " +
//		"	a.leave_session AS 'leaveSession', " +
//		"	a.holiday_events_session AS 'holidayOrVacationOrExemptionSession', " +
//		"	a.emp_leave_entry_id as 'leaveEntryId', " +
//		"	a.emp_holiday_events_id as 'holidayEventsId', " +
//		"   a.is_sunday_working as 'isSundayWorking', " +
//		"	t.leave_type_name AS 'leaveType', " +
//		"	t.leave_type_code AS 'leaveTypeCode' " +
//		"	FROM emp_attendance a " +
//		"	left JOIN emp_leave_entry e ON e.emp_leave_entry_id = a.emp_leave_entry_id  AND e.record_status = 'A' " +
//		"	left JOIN emp_leave_type t ON t.emp_leave_type_id = e.emp_leave_type_id   AND t.record_status = 'A' " +
//		"	left JOIN emp_holiday_events h ON h.emp_holiday_events_id = a.emp_holiday_events_id   AND h.record_status = 'A' " +
//		"	WHERE a.emp_id =:empId AND a.attendance_date BETWEEN date(:startDate) AND date(:endDate) " +
//		"	AND a.record_status = 'A' order by date ";
}
