package com.christ.erp.services.transactions.employee.attendance;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.exception.GeneralException;
import com.christ.erp.services.common.Utils;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ViewEmployeeAttendanceTransaction {

	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public List<Tuple> getEmployeeAttendance(String startDate, String endDate, String userId) {
		String str = "SELECT  " +
				" emp_attendance.attendance_date as attendance_date," +
				" dayname(emp_attendance.attendance_date) as dayName, " +
				" emp_attendance.in_time as in_time,  " +
				" emp_attendance.out_time as out_time," +
				" emp_attendance.total_hour as total_hour, " +
				" emp_attendance.is_late_entry as is_late_entry, " +
				" emp_attendance.is_early_exit as is_early_exit," +
				" emp_attendance.late_entry_by as late_entry_by, " +
				" emp_attendance.early_exit_by as early_exit_by," +
				" emp_attendance.is_exempted as is_exempted," +
				" emp_attendance.exempted_session as exempted_session, " +
				" emp_attendance.is_one_time_punch as is_one_time_punch," +
				" emp_attendance.is_sunday_working as is_sunday_working," +
				" emp_attendance.is_sunday as is_sunday, " +
				" emp_time_zone.emp_time_zone_id as emp_time_zone_id," +
				" emp_time_zone.time_zone_name as time_zone_name," +
				" fn_leave_entry.emp_leave_entry_id as fn_leave_entry_id," +
				" fn_leave_entry_details.leave_session as fn_leave_session, " +
				" fn_leave_type.leave_type_name as fn_leave_type_name,  " +
				" an_leave_entry.emp_leave_entry_id as an_leave_entry_id," +
				" an_leave_entry_details.leave_session as an_leave_session, " +
				" an_leave_type.leave_type_name as an_leave_type_name," +
				" emp_holiday_events.emp_holiday_events_id as emp_holiday_events_id, " +
				" emp_holiday_events.emp_holiday_events_type_name as emp_holiday_events_type_name, " +
				" emp_holiday_events.holiday_events_description as holiday_events_description, " +
				" emp_attendance.holiday_events_session as holiday_events_session " +
				" FROM emp_attendance    " +
				" INNER JOIN emp_time_zone ON emp_time_zone.emp_time_zone_id = emp_attendance.emp_time_zone_id  " +
				" LEFT JOIN emp_leave_entry as fn_leave_entry ON fn_leave_entry.emp_leave_entry_id = emp_attendance.fn_emp_leave_entry_id AND fn_leave_entry.record_status='A'" +
				" LEFT JOIN emp_leave_entry_details as fn_leave_entry_details ON fn_leave_entry.emp_leave_entry_id = fn_leave_entry_details.emp_leave_entry_id" +
				" AND fn_leave_entry_details.record_status='A'" +
				" LEFT JOIN emp_leave_type as fn_leave_type ON fn_leave_entry.emp_leave_type_id = fn_leave_type.emp_leave_type_id " +
				" LEFT JOIN emp_leave_entry as an_leave_entry ON an_leave_entry.emp_leave_entry_id = emp_attendance.an_emp_leave_entry_id AND an_leave_entry.record_status='A'" +
				" LEFT JOIN emp_leave_entry_details as an_leave_entry_details ON an_leave_entry.emp_leave_entry_id = an_leave_entry_details.emp_leave_entry_id" +
				" AND an_leave_entry_details.record_status='A'" +
				" LEFT JOIN emp_leave_type as an_leave_type ON an_leave_entry.emp_leave_type_id = an_leave_type.emp_leave_type_id " +
				" INNER  JOIN emp on emp.emp_id = emp_attendance.emp_id AND emp.record_status = 'A' " +
				" INNER JOIN erp_users on erp_users.emp_id = emp.emp_id and erp_users .record_status = 'A' " +
				" LEFT JOIN emp_holiday_events ON emp_holiday_events.emp_holiday_events_id = emp_attendance.emp_holiday_events_id  " +
				" AND emp_holiday_events.record_status='A'" +
				" WHERE erp_users.erp_users_id =:userId" +
				" AND date(emp_attendance.attendance_date) BETWEEN date(:startDate) AND date(:endDate)   " +
				" AND emp_attendance.record_status = 'A' ORDER BY attendance_date ASC";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str,Tuple.class);
			if(!Utils.isNullOrEmpty(startDate))
				query.setParameter("startDate",  Utils.convertStringDateToLocalDate(startDate));
			if(!Utils.isNullOrEmpty(endDate))
				query.setParameter("endDate",Utils.convertStringDateToLocalDate(endDate));
			if(!Utils.isNullOrEmpty(userId))
				query.setParameter("userId",Integer.parseInt(userId));
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}


//	private static volatile ViewEmployeeAttendanceTransaction viewEmployeeAttendanceTransaction = null;
//
//	public static ViewEmployeeAttendanceTransaction getInstance() {
//		if (viewEmployeeAttendanceTransaction == null) {
//			viewEmployeeAttendanceTransaction = new ViewEmployeeAttendanceTransaction();
//		}
//		return viewEmployeeAttendanceTransaction;
//	}
//
//	List<Tuple> query1=null;
//	public List<Tuple> getAttendanceDetailsForEmployee(Map<String, String> requestParams) {
//
//		try {
//			 DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
//					@SuppressWarnings("unchecked")
//					@Override
//					public List<Tuple> onRun(EntityManager context) throws Exception {
//
//
//						String str ="SELECT  a.attendance_date As date," +
//								"   dayname(a.attendance_date) As dayName,"+
//								"	a.in_time AS 'timeIn',"+
//								"   a.out_time AS 'timeOut', " +
////								"   a.is_next_day AS 'nextDay', " +
//								"   a.is_late_entry AS 'lateEntry', " +
//								"   a.is_early_exit AS 'earlyExit', " +
//								"   a.leave_session AS 'leaveSession', " +
//								"   a.holiday_events_session AS 'holidayOrVacationOrExemptionSession', " +
//								"   a.emp_leave_entry_id as 'leaveEntryId', " +
//							    "   a.emp_holiday_events_id as 'holidayEventsId', " +
//							    "   a.is_sunday_working as 'isSundayWorking', " +
////							    "   a.is_weekly_off as 'isWeeklyOff', " +
//							    "   a.is_exempted as 'isExempted', " +
//								"   t.leave_type_name AS 'leaveType', " +
//								"	t.leave_type_code AS 'leaveTypeCode', " +
//								"   t.leave_type_color_code_hexvalue AS 'leaveTypeColor', " +
//								"   h.emp_holiday_events_type_name AS 'empHolidayEventsType', " +
//								"   h.holiday_events_description AS 'holidayEventsDescription' " +
//								"   FROM emp_attendance a " +
//								"   left JOIN emp_leave_entry e ON e.emp_leave_entry_id = a.emp_leave_entry_id  AND e.record_status = 'A' " +
//								"   left JOIN emp_leave_type t ON t.emp_leave_type_id = e.emp_leave_type_id   AND t.record_status = 'A' " +
//								"   left JOIN emp_holiday_events h ON h.emp_holiday_events_id = a.emp_holiday_events_id   AND h.record_status = 'A' " +
//								"   WHERE   a.emp_id =:empId " +
//								"   AND a.attendance_date BETWEEN date(:startDate) AND date(:endDate) " +
//								"   AND a.record_status = 'A'  order by date";
//						Query query = context.createNativeQuery(str, Tuple.class);
//						query.setParameter("empId", requestParams.get("empId"));
//						query.setParameter("startDate", Utils.convertStringDateToLocalDate(requestParams.get("startDate").toString()));
//						query.setParameter("endDate", Utils.convertStringDateToLocalDate(requestParams.get("endDate").toString()));
//						query1=  query.getResultList();
//						return query1;
//					}
//					@Override
//					public void onError(Exception error) throws Exception {
//						throw error;
//					}
//				});
//		}
//		catch(Exception e) {
//			throw new GeneralException(e.getMessage());
//		}
//		return query1;
//	}
}
