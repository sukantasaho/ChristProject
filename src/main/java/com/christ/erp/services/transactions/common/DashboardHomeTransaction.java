package com.christ.erp.services.transactions.common;

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

import reactor.core.publisher.Mono;

@Repository
public class DashboardHomeTransaction {

	private static volatile DashboardHomeTransaction dashboardHomeTransaction = null;
	public static DashboardHomeTransaction getInstance() {
		if (dashboardHomeTransaction == null) {
			dashboardHomeTransaction = new DashboardHomeTransaction();
		}
		return dashboardHomeTransaction;
	}
	
	@Autowired
	private Mutiny.SessionFactory sessionFactory;
	
//	public List<Tuple> getEmployeesByUserIdForDepartmentCampusDashBoard(String userId) throws Exception {
//		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
//			@SuppressWarnings("unchecked")
//			@Override
//			public List<Tuple> onRun(EntityManager context) throws Exception {
//				String str = " select distinct e.emp_id As 'Employee Id' from emp e " +
//						"  inner join erp_campus_department_mapping cd ON cd.erp_campus_department_mapping_id = e.erp_campus_department_mapping_id and cd.record_status='A' " +
//						"  inner join erp_campus_department_user_title t ON t.erp_campus_department_mapping_id = cd.erp_campus_department_mapping_id and t.record_status='A' " +
//						"  where t.erp_users_id=:userId and e.record_status='A' ";
//				Query query = context.createNativeQuery(str, Tuple.class);
//				query.setParameter("userId", userId);
//				return query.getResultList();
//			}
//			@Override
//			public void onError(Exception error) throws Exception {
//				throw error;
//			}
//		});
//	}
//	public List<Tuple> getAttendanceTypesDataForEmployees(List<String> empIds, String startDate, String endDate) throws Exception {
//		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
//			@SuppressWarnings("unchecked")
//			@Override
//			public List<Tuple> onRun(EntityManager context) throws Exception {
//				String str = "SELECT  a.attendance_date As date," +
//						"   dayname(a.attendance_date) As dayName,"+
//						"	a.in_time AS 'timeIn',"+
//						"   a.out_time AS 'timeOut', " +
//						"   a.is_next_day AS 'nextDay', " +
//						"   a.is_late_entry AS 'lateEntry', " +
//						"   a.is_early_exit AS 'earlyExit', " +
//						"   a.leave_session AS 'leaveSession', " +
//						"   a.holiday_events_session AS 'holidayOrVacationOrExemptionSession', " +
//						"   a.emp_leave_entry_id as 'leaveEntryId', " +
//					    "   a.emp_holiday_events_id as 'holidayEventsId', " +
//					    "   a.is_sunday_working as 'isSundayWorking', " +
//					    "   a.is_weekly_off as 'isWeeklyOff', " +
//					    "   a.is_exempted as 'isExempted', " +
//						"   t.leave_type_name AS 'leaveType', " +
//						"   t.leave_type_color_code_hexvalue AS 'leaveTypeColor', " +
//						"   h.emp_holiday_events_type_name AS 'empHolidayEventsType', " +
//						"   h.holiday_events_description AS 'holidayEventsDescription'," +
//						"   c.campus_name As 'campusName', "+
//					    "   d.department_name As 'departmentName' ,"+
//					    "   c.erp_campus_id As 'campusId', "+
//					    "   d.erp_department_id As 'departmentId' "+
//						"   FROM emp_attendance a " +
//						"   left JOIN emp_leave_entry e ON e.emp_leave_entry_id = a.emp_leave_entry_id  AND e.record_status = 'A' " +
//						"   left JOIN emp_leave_type t ON t.emp_leave_type_id = e.emp_leave_type_id   AND t.record_status = 'A' " +
//						"   left JOIN emp_holiday_events h ON h.emp_holiday_events_id = a.emp_holiday_events_id   AND h.record_status = 'A' " +
//						"   inner join emp e1 ON e1.emp_id=a.emp_id  AND e1.record_status = 'A' "+
//					    "   inner join erp_campus_department_mapping cd ON cd.erp_campus_department_mapping_id = e1.erp_campus_department_mapping_id  AND cd.record_status = 'A' "+
//					    "   inner join erp_campus c ON c.erp_campus_id = cd.erp_campus_id  AND c.record_status = 'A' " +
//					    "   inner join erp_department d ON d.erp_department_id = cd.erp_department_id AND d.record_status = 'A' "+
//						"   WHERE   a.emp_id in (:empId) " +
//						"   AND a.attendance_date BETWEEN date(:startDate) AND date(:endDate) " +
//						"   AND a.record_status = 'A'  order by date";
//				Query query = context.createNativeQuery(str, Tuple.class);
//				query.setParameter("empId", empIds);
//				query.setParameter("startDate",Utils.convertStringDateToLocalDate(startDate));
//				query.setParameter("endDate",Utils.convertStringDateToLocalDate(endDate));
//				return query.getResultList();
//			}
//			@Override
//			public void onError(Exception error) throws Exception {
//				throw error;
//			}
//		});
//	}
	
	public Mono<List<Tuple>> getDashBoardEmployeeDiversity() {
		String str = "select erp_state_id, state, emp_count from vis_emp_diversity ";
		return Mono.fromFuture(sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str,Tuple.class);
			return query.getResultList();
		}).subscribeAsCompletionStage());
	}
	
	public Mono<List<Tuple>> getDashBoardEmployeeExperience() {
		String str = " select range_of_years_of_experience, emp_count from vis_emp_experience";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(str, Tuple.class).getResultList()).subscribeAsCompletionStage());	
	}
	
	public Mono<List<Tuple>> getDashBoardEmployeeQualification() {
		String str = " select erp_deanery_id, erp_deanery, erp_qualification_level_id, erp_qualification_level, emp_count from vis_emp_qualification ";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(str, Tuple.class).getResultList()).subscribeAsCompletionStage());	
	}
	
	public Mono<List<Tuple>> getDashBoardEmployeeApplicationStatus() {
		String str = "select  status, appln_count from vis_emp_appln_status";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(str, Tuple.class).getResultList()).subscribeAsCompletionStage());	
	}
	public Mono<List<Tuple>> getDashBoardEmployeeCount() {
		String str = "select  parent, erp_deanery_id, erp_deanery, erp_department_id, "
				+ " erp_department, emp_designation_id, emp_designation, emp_count  from vis_emp_count";
		return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(str, Tuple.class).getResultList()).subscribeAsCompletionStage());	
	}
}
