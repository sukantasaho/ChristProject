package com.christ.erp.services.transactions.employee.attendance;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.employee.attendance.HigherAuthorityViewDTO;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.persistence.Tuple;
import java.util.List;
import java.util.Set;

@Repository
public class HigherAuthorityViewsTransaction {

    @Autowired
    private Mutiny.SessionFactory sessionFactory;

    public List<Tuple> getEmployeeAttendance(HigherAuthorityViewDTO data) {
        String str = " SELECT  emp_attendance.attendance_date as attendance_date, " +
                " dayname(emp_attendance.attendance_date) as dayName,  " +
                " emp_attendance.in_time as in_time, " +
                " emp_attendance.out_time as out_time, " +
                " emp_attendance.is_late_entry as is_late_entry,  " +
                " emp_attendance.is_early_exit as is_early_exit, " +
                " emp_attendance.late_entry_by as late_entry_by, " +
                " emp_attendance.early_exit_by as early_exit_by, " +
                " emp_attendance.is_exempted as is_exempted, " +
                " emp_attendance.exempted_session as exempted_session, " +
                " emp_attendance.is_one_time_punch as is_one_time_punch, " +
                " emp_attendance.is_sunday_working as is_sunday_working, " +
                " emp_attendance.is_sunday as is_sunday, " +
                " emp_attendance.total_hour as total_hour, " +
                " emp_attendance.leave_session as leave_session, " +
                " emp_attendance.holiday_events_session holiday_events_session, " +
                " emp_time_zone.emp_time_zone_id as emp_time_zone_id, " +
                " emp_time_zone.time_zone_name as time_zone_name, " +
                " emp_time_zone.is_general_time_zone as is_general_time_zone, " +
                " emp_time_zone.is_holiday_time_zone as is_holiday_time_zone, " +
                " emp_time_zone.is_vacation_time_zone as is_vacation_time_zone, " +
                " fn_leave_entry.emp_leave_entry_id as fn_leave_entry_id, " +
                " fn_leave_entry_details.leave_session as fn_leave_session,  " +
                " fn_leave_type.leave_type_name as fn_leave_type_name,   " +
                " an_leave_entry.emp_leave_entry_id as an_leave_entry_id, " +
                " an_leave_entry_details.leave_session as an_leave_session,  " +
                " an_leave_type.leave_type_name as an_leave_type_name, " +
                " emp_holiday_events.emp_holiday_events_id as emp_holiday_events_id,  " +
                " emp_holiday_events.emp_holiday_events_type_name as emp_holiday_events_type_name, " +
                " emp_holiday_events.holiday_events_description as holiday_events_description, "+
                " emp.emp_no empId,emp.emp_name empName " +
                " FROM emp_attendance     " +
                " INNER JOIN emp_time_zone ON emp_time_zone.emp_time_zone_id = emp_attendance.emp_time_zone_id   " +
                " LEFT JOIN emp_leave_entry as fn_leave_entry ON fn_leave_entry.emp_leave_entry_id = emp_attendance.fn_emp_leave_entry_id AND fn_leave_entry.record_status='A' " +
                " LEFT JOIN emp_leave_entry_details as fn_leave_entry_details ON fn_leave_entry.emp_leave_entry_id = fn_leave_entry_details.emp_leave_entry_id " +
                " AND fn_leave_entry_details.record_status='A' " +
                " LEFT JOIN emp_leave_type as fn_leave_type ON fn_leave_entry.emp_leave_type_id = fn_leave_type.emp_leave_type_id " +
                " LEFT JOIN emp_leave_entry as an_leave_entry ON an_leave_entry.emp_leave_entry_id = emp_attendance.an_emp_leave_entry_id AND an_leave_entry.record_status='A' " +
                " LEFT JOIN emp_leave_entry_details as an_leave_entry_details ON an_leave_entry.emp_leave_entry_id = an_leave_entry_details.emp_leave_entry_id " +
                " AND an_leave_entry_details.record_status='A' " +
                " LEFT JOIN emp_leave_type as an_leave_type ON an_leave_entry.emp_leave_type_id = an_leave_type.emp_leave_type_id " +
                " INNER JOIN emp on emp.emp_id = emp_attendance.emp_id AND emp.record_status = 'A'  " +
                " LEFT JOIN emp_holiday_events ON emp_holiday_events.emp_holiday_events_id = emp_attendance.emp_holiday_events_id   " ;

        if(!Utils.isNullOrEmpty(data.getCampus().getValue()) && !Utils.isNullOrEmpty(data.getDepartment().getValue())){
            str +=   " INNER JOIN erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id " +
                    " INNER JOIN erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id " +
                    " INNER JOIN erp_department ON erp_department.erp_department_id = erp_campus_department_mapping.erp_department_id " ;
        }else if(!Utils.isNullOrEmpty(data.getCampus().getValue()) && Utils.isNullOrEmpty(data.getDepartment().getValue())){
            str +=   " INNER JOIN erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id " +
                    " INNER JOIN erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id ";
        }

                str +=   " WHERE date(emp_attendance.attendance_date) BETWEEN date(:startDate) AND date(:endDate)   ";
        if(!Utils.isNullOrEmpty(data.getCampus().getValue()) && !Utils.isNullOrEmpty(data.getDepartment().getValue())){
            str +=   " AND emp_attendance.record_status = 'A' AND erp_campus.erp_campus_id =:campusId  " +
                    " AND erp_department.erp_department_id =:departmentId";
        }else if(!Utils.isNullOrEmpty(data.getCampus().getValue()) && Utils.isNullOrEmpty(data.getDepartment().getValue())){
            str +=   " AND emp_attendance.record_status = 'A' AND erp_campus.erp_campus_id =:campusId ";
        }
        if (!Utils.isNullOrEmpty(data.getEmpId()))
            str += " AND emp.emp_id LIKE CONCAT( :empId, '%') ";
        if (!Utils.isNullOrEmpty(data.getEmpName()))
            str += " AND emp.emp_name LIKE CONCAT(:empName, '%') ";
        str += " ORDER BY emp_attendance.attendance_date";
        String finlStr = str;
        List<Tuple> list = sessionFactory.withSession(s -> {
            Mutiny.Query<Tuple> query = s.createNativeQuery(finlStr, Tuple.class);
            if (!Utils.isNullOrEmpty(data.getStartDate()))
                query.setParameter("startDate", data.getStartDate());
            if (!Utils.isNullOrEmpty(data.getEndDate()))
                query.setParameter("endDate", data.getEndDate());
            if (!Utils.isNullOrEmpty(data.getCampus())) {
                if (!Utils.isNullOrEmpty(data.getCampus().getValue()))
                    query.setParameter("campusId", Integer.parseInt(data.getCampus().getValue()));
            }
            if (!Utils.isNullOrEmpty(data.getDepartment())) {
                if (!Utils.isNullOrEmpty(data.getDepartment().getValue()))
                    query.setParameter("departmentId", Integer.parseInt(data.getDepartment().getValue()));
            }
            if (!Utils.isNullOrEmpty(data.getEmpId()))
                query.setParameter("empId", Integer.parseInt(data.getEmpId().trim()));
            if (!Utils.isNullOrEmpty(data.getEmpName()))
                query.setParameter("empName", data.getEmpName().trim());
            return query.getResultList();
        }).await().indefinitely();
        return list;
    }

    public Mono<List<Tuple>> getEmployeeIdAndName(int locationId, int campusId, int departmentId, int denearyId) {
        String str = " Select  emp.emp_no as empNo, " +
                " emp.emp_name as empName " +
                " from emp  " +
                " inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id " +
                " inner join erp_department ON erp_department.erp_department_id = erp_campus_department_mapping.erp_department_id " +
                " inner join erp_deanery ON erp_deanery.erp_deanery_id = erp_department.erp_deanery_id " +
                " inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id " +
                " inner join erp_location ON erp_location.erp_location_id = erp_campus.erp_location_id " +
                " where emp.record_status='A' and  " +
                " erp_location.erp_location_id =:locationId and  " +
                " erp_department.erp_department_id =:departmentId and " +
                " erp_deanery.erp_deanery_id =:denearyId and  " +
                " erp_campus.erp_campus_id =:campusId ";
        return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(str, Tuple.class).setParameter("locationId",locationId).setParameter("campusId",campusId)
                .setParameter("departmentId",departmentId).setParameter("denearyId",denearyId).getResultList()).subscribeAsCompletionStage());

    }

    public List<Tuple> getUserSpecificCampusDept(String userId) {
        String str = " SELECT  " +
                "  erp_location.erp_location_id  as locationID " +
                "  ,erp_location.location_name as location " +
                "  ,erp_campus.erp_campus_id as campusID " +
                "  ,erp_campus.campus_name as campus " +
                "  ,erp_deanery.erp_deanery_id as deaneryID " +
                "  ,erp_deanery.deanery_name as deanery " +
                "  ,erp_department.erp_department_id as departmentID " +
                "  ,erp_department.department_name as department " +
                "  ,emp_title.emp_title_id as titleID " +
                "  ,emp_title.title_name as title " +
                "FROM erp_campus_department_user_title " +
                "INNER JOIN erp_deanery ON erp_deanery.erp_deanery_id=erp_campus_department_user_title.erp_deanery_id " +
                "AND erp_deanery.record_status ='A' " +
                "INNER join erp_department ON erp_department.erp_deanery_id = erp_deanery.erp_deanery_id " +
                "AND erp_department.record_status ='A' " +
                "INNER JOIN erp_campus_department_mapping ON erp_campus_department_mapping.erp_department_id = erp_department.erp_department_id " +
                "AND erp_campus_department_mapping.record_status ='A' " +
                "INNER JOIN erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id " +
                "AND erp_campus.record_status ='A' " +
                "INNER JOIN emp_title ON emp_title.emp_title_id = erp_campus_department_user_title.erp_title_id " +
                "AND emp_title.record_status ='A' " +
                "INNER JOIN erp_location ON erp_location.erp_location_id = erp_campus.erp_location_id " +
                "AND erp_location.record_status ='A' " +
                "WHERE erp_campus_department_user_title.record_status ='A' " +
                "AND erp_users_id=:userId " +
                "UNION " +
                "SELECT  " +
                "  erp_location.erp_location_id  as locationID " +
                "  ,erp_location.location_name as location " +
                "  ,erp_campus.erp_campus_id as campusID " +
                "  ,erp_campus.campus_name as campus " +
                "  ,erp_deanery.erp_deanery_id as deaneryID " +
                "  ,erp_deanery.deanery_name as deanery " +
                "  ,erp_department.erp_department_id as departmentID " +
                "  ,erp_department.department_name as department " +
                "  ,emp_title.emp_title_id as titleID " +
                "  ,emp_title.title_name as title " +
                "FROM erp_campus_department_user_title " +
                "INNER JOIN erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = erp_campus_department_user_title.erp_campus_department_mapping_id " +
                "AND erp_campus_department_mapping.record_status ='A' " +
                "INNER JOIN erp_department ON erp_department.erp_department_id = erp_campus_department_mapping.erp_department_id " +
                "AND erp_department.record_status ='A' " +
                "INNER JOIN erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id " +
                "AND erp_campus.record_status ='A' " +
                "INNER JOIN erp_deanery ON erp_deanery.erp_deanery_id = erp_department.erp_deanery_id " +
                "AND erp_deanery.record_status ='A' " +
                "INNER JOIN emp_title ON emp_title.emp_title_id = erp_campus_department_user_title.erp_title_id " +
                "AND emp_title.record_status ='A' " +
                "INNER JOIN erp_location ON erp_location.erp_location_id = erp_campus.erp_location_id " +
                "AND erp_location.record_status ='A' " +
                "WHERE erp_campus_department_user_title.record_status ='A' " +
                "AND erp_users_id=:userId " +
                "UNION " +
                "SELECT  " +
                "  erp_location.erp_location_id  as locationID " +
                "  ,erp_location.location_name as location " +
                "  ,erp_campus.erp_campus_id as campusID " +
                "  ,erp_campus.campus_name as campus " +
                "  ,erp_deanery.erp_deanery_id as deaneryID " +
                "  ,erp_deanery.deanery_name as deanery " +
                "  ,erp_department.erp_department_id as departmentID " +
                "  ,erp_department.department_name as department " +
                "  ,emp_title.emp_title_id as titleID " +
                "  ,emp_title.title_name as title " +
                "FROM erp_campus_department_user_title " +
                "INNER JOIN erp_campus ON erp_campus.erp_campus_id = erp_campus_department_user_title.erp_campus_id  " +
                "AND erp_campus.record_status ='A' " +
                "INNER JOIN erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_id=erp_campus.erp_campus_id " +
                "AND erp_campus_department_mapping.record_status ='A' " +
                "INNER JOIN erp_department ON erp_department.erp_department_id = erp_campus_department_mapping.erp_department_id " +
                "AND erp_department.record_status ='A' " +
                "INNER JOIN erp_deanery ON erp_deanery.erp_deanery_id = erp_department.erp_deanery_id " +
                "AND erp_deanery.record_status ='A' " +
                "INNER JOIN emp_title ON emp_title.emp_title_id = erp_campus_department_user_title.erp_title_id " +
                "AND emp_title.record_status ='A' " +
                "INNER JOIN erp_location ON erp_location.erp_location_id = erp_campus.erp_location_id " +
                "AND erp_location.record_status ='A' " +
                "WHERE erp_campus_department_user_title.record_status ='A' " +
                "AND erp_users_id=:userId " +
                "UNION " +
                "SELECT  " +
                "  erp_location.erp_location_id  as locationID " +
                "  ,erp_location.location_name as location " +
                "  ,erp_campus.erp_campus_id as campusID " +
                "  ,erp_campus.campus_name as campus " +
                "  ,erp_deanery.erp_deanery_id as deaneryID " +
                "  ,erp_deanery.deanery_name as deanery " +
                "  ,erp_department.erp_department_id as departmentID " +
                "  ,erp_department.department_name as department " +
                "  ,emp_title.emp_title_id as titleID " +
                "  ,emp_title.title_name as title " +
                "FROM erp_campus_department_user_title " +
                "INNER JOIN erp_department ON erp_department.erp_department_id = erp_campus_department_user_title.erp_department_id " +
                "AND erp_department.record_status ='A' " +
                "INNER JOIN erp_campus_department_mapping ON erp_campus_department_mapping.erp_department_id = erp_department.erp_department_id " +
                "AND erp_campus_department_mapping.record_status ='A' " +
                "INNER JOIN erp_deanery ON erp_deanery.erp_deanery_id = erp_department.erp_deanery_id " +
                "AND erp_deanery.record_status ='A' " +
                "INNER JOIN erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id " +
                "AND erp_campus.record_status ='A' " +
                "INNER JOIN emp_title ON emp_title.emp_title_id = erp_campus_department_user_title.erp_title_id " +
                "AND emp_title.record_status ='A' " +
                "INNER JOIN erp_location ON erp_location.erp_location_id = erp_campus.erp_location_id " +
                "AND erp_location.record_status ='A' " +
                "WHERE erp_campus_department_user_title.record_status ='A' " +
                "AND erp_users_id=:userId ";
        return sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).setParameter("userId", Integer.parseInt(userId)).getResultList()).await().indefinitely();
    }

    public Mono<List<Tuple>>  getAdminViewEmployee(List<Integer> campusIdsList,List<Integer> dept) {
        String str = " select emp.emp_name empName, " +
                " emp.emp_no empNo " +
                " from emp " +
                " inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id and erp_campus_department_mapping.record_status = 'A' " +
                " inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id and erp_campus.record_status='A' ";
        if (!Utils.isNullOrEmpty(dept)) {
            str += " inner join erp_department ON erp_department.erp_department_id = erp_campus_department_mapping.erp_department_id and erp_department.record_status = 'A' ";
        }
        if (!Utils.isNullOrEmpty(campusIdsList)) {
            str += " where erp_campus.erp_campus_id IN (:campusIdsList) ";
        }
        if (!Utils.isNullOrEmpty(dept)) {
            str += "  and erp_department.erp_department_id IN (:dept) ";
        }
        str += " and emp.record_status = 'A' ORDER BY emp.emp_name ";
        String finalStr = str;
        if (!Utils.isNullOrEmpty(campusIdsList) && Utils.isNullOrEmpty(dept)) {
            return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(finalStr, Tuple.class)
                    .setParameter("campusIdsList", campusIdsList).getResultList()).subscribeAsCompletionStage());
        } else {
            return Mono.fromFuture(sessionFactory.withSession(s -> s.createNativeQuery(finalStr, Tuple.class)
                    .setParameter("campusIdsList", campusIdsList).setParameter("dept", dept).getResultList()).subscribeAsCompletionStage());
        }
    }

}
