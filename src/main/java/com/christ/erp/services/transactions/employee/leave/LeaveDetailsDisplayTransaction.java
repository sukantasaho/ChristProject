package com.christ.erp.services.transactions.employee.leave;

import java.util.List;

import javax.persistence.Tuple;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveEntryDBO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveEntryDTO;

import reactor.core.publisher.Mono;

@Repository
public class LeaveDetailsDisplayTransaction {
	
	@Autowired
    private Mutiny.SessionFactory sessionFactory;
	
	public Mono<List<Tuple>> getGridData(EmpLeaveEntryDTO data,Integer empId) {
        String str = " select "
        		+ " emp.emp_no, "
        		+ " emp.emp_name , "
        		+ " erp_campus.campus_name, erp_department.department_name, "
        		+ " emp_leave_entry.leave_start_date, "
        		+ " emp_leave_entry.leave_start_session, "
        		+ " emp_leave_entry.leave_end_date, "
        		+ " emp_leave_entry.leave_end_session, "
        		+ " emp_leave_type.leave_type_code,emp_leave_type.leave_type_name,emp_leave_entry.number_of_days_leave ,"
        		+ " emp_leave_entry.leave_reason "
        		+ " from emp_leave_entry "
        		+ " left join emp ON emp.emp_id= emp_leave_entry.emp_id AND emp.record_status='A' "
        		+ " left join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id= emp.erp_campus_department_mapping_id AND erp_campus_department_mapping.record_status='A' "
        		+ " left join erp_campus ON erp_campus.erp_campus_id=erp_campus_department_mapping.erp_campus_id AND erp_campus.record_status='A' "
        		+ " left join erp_department ON erp_department.erp_department_id = erp_campus_department_mapping.erp_department_id AND erp_department.record_status='A' "
        		+ " left join emp_leave_type ON emp_leave_type.emp_leave_type_id=emp_leave_entry.emp_leave_type_id AND emp_leave_type.record_status='A' "
        		+ " where leave_end_date>= Curdate() "
        		+ " and leave_start_date<= DATE_ADD(Curdate(), INTERVAL 30 DAY) "
        		+ " and emp_leave_approver_id =1210 "
        		+ " and emp_leave_entry.record_status ='A'";
        
        
        String finalStr = str;
		Mono<List<Tuple>> list = Mono.fromFuture(sessionFactory.withSession(s -> { Mutiny.Query<Tuple> query = s.createNativeQuery(finalStr, Tuple.class);
//		if(!Utils.isNullOrEmpty(yearId)) {
//			query.setParameter("yearId", yearId);
//		} else {
//			query.setParameter("yearId",currYear.getId());
//		}
		return  query.getResultList();
		}).subscribeAsCompletionStage());
		return list;
//        return Mono.fromFuture(sessionFactory.withSession(s->s.createNativeQuery(str, Tuple.class).setParameter("empId",empId).setParameter(0, str).getResultList()).subscribeAsCompletionStage());
    }

	public Mono<List<Tuple>> getEmpCampus(EmpLeaveEntryDTO data,String userId) {
        String str = " SELECT  erp_campus.erp_campus_id as campusID ,erp_campus.campus_name as campus "
        		+ " FROM erp_campus_department_user_title "
        		+ " INNER JOIN erp_deanery ON erp_deanery.erp_deanery_id=erp_campus_department_user_title.erp_deanery_id "
        		+ " AND erp_deanery.record_status ='A' "
        		+ " INNER join erp_department ON erp_department.erp_deanery_id = erp_deanery.erp_deanery_id "
        		+ " AND erp_department.record_status ='A' "
        		+ " INNER JOIN erp_campus_department_mapping ON erp_campus_department_mapping.erp_department_id = erp_department.erp_department_id "
        		+ " AND erp_campus_department_mapping.record_status ='A' "
        		+ " INNER JOIN erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id "
        		+ " AND erp_campus.record_status ='A' "
        		+ " INNER JOIN emp_title ON emp_title.emp_title_id = erp_campus_department_user_title.erp_title_id "
        		+ " AND emp_title.record_status ='A' "
        		+ " INNER JOIN erp_location ON erp_location.erp_location_id = erp_campus.erp_location_id "
        		+ " AND erp_location.record_status ='A' "
        		+ " WHERE erp_campus_department_user_title.record_status ='A' "
        		+ " AND erp_users_id=:userID "
        		+ " UNION "
        		+ " SELECT erp_campus.erp_campus_id as campusID ,erp_campus.campus_name as campus "
        		+ " FROM erp_campus_department_user_title "
        		+ " INNER JOIN erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = erp_campus_department_user_title.erp_campus_department_mapping_id "
        		+ " AND erp_campus_department_mapping.record_status ='A' "
        		+ " INNER JOIN erp_department ON erp_department.erp_department_id = erp_campus_department_mapping.erp_department_id "
        		+ " AND erp_department.record_status ='A' "
        		+ " INNER JOIN erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id "
        		+ " AND erp_campus.record_status ='A' "
        		+ " INNER JOIN erp_deanery ON erp_deanery.erp_deanery_id = erp_department.erp_deanery_id "
        		+ " AND erp_deanery.record_status ='A' "
        		+ " INNER JOIN emp_title ON emp_title.emp_title_id = erp_campus_department_user_title.erp_title_id "
        		+ " AND emp_title.record_status ='A' "
        		+ " INNER JOIN erp_location ON erp_location.erp_location_id = erp_campus.erp_location_id "
        		+ " AND erp_location.record_status ='A' "
        		+ " WHERE erp_campus_department_user_title.record_status ='A' "
        		+ " AND erp_users_id=:userID "
        		+ " UNION "
        		+ " SELECT erp_campus.erp_campus_id as campusID ,erp_campus.campus_name as campus "
        		+ " FROM erp_campus_department_user_title "
        		+ " INNER JOIN erp_campus ON erp_campus.erp_campus_id = erp_campus_department_user_title.erp_campus_id "
        		+ " AND erp_campus.record_status ='A' "
        		+ " INNER JOIN erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_id=erp_campus.erp_campus_id "
        		+ " AND erp_campus_department_mapping.record_status ='A' "
        		+ " INNER JOIN erp_department ON erp_department.erp_department_id = erp_campus_department_mapping.erp_department_id "
        		+ " AND erp_department.record_status ='A' "
        		+ " INNER JOIN erp_deanery ON erp_deanery.erp_deanery_id = erp_department.erp_deanery_id "
        		+ " AND erp_deanery.record_status ='A' "
        		+ " INNER JOIN emp_title ON emp_title.emp_title_id = erp_campus_department_user_title.erp_title_id "
        		+ " AND emp_title.record_status ='A' "
        		+ " INNER JOIN erp_location ON erp_location.erp_location_id = erp_campus.erp_location_id "
        		+ " AND erp_location.record_status ='A' "
        		+ " WHERE erp_campus_department_user_title.record_status ='A' "
        		+ " AND erp_users_id=:userID "
        		+ " UNION "
        		+ " SELECT erp_campus.erp_campus_id as campusID ,erp_campus.campus_name as campus "
        		+ " FROM erp_campus_department_user_title "
        		+ " INNER JOIN erp_department ON erp_department.erp_department_id = erp_campus_department_user_title.erp_department_id "
        		+ " AND erp_department.record_status ='A' "
        		+ " INNER JOIN erp_campus_department_mapping ON erp_campus_department_mapping.erp_department_id = erp_department.erp_department_id "
        		+ " AND erp_campus_department_mapping.record_status ='A' "
        		+ " INNER JOIN erp_deanery ON erp_deanery.erp_deanery_id = erp_department.erp_deanery_id "
        		+ " AND erp_deanery.record_status ='A' "
        		+ " INNER JOIN erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id "
        		+ " AND erp_campus.record_status ='A' "
        		+ " INNER JOIN emp_title ON emp_title.emp_title_id = erp_campus_department_user_title.erp_title_id "
        		+ " AND emp_title.record_status ='A' "
        		+ " INNER JOIN erp_location ON erp_location.erp_location_id = erp_campus.erp_location_id "
        		+ " AND erp_location.record_status ='A' "
        		+ " WHERE erp_campus_department_user_title.record_status ='A' "
        		+ " AND erp_users_id=:userID";
              
        String finalStr = str;
		Mono<List<Tuple>> list = Mono.fromFuture(sessionFactory.withSession(s -> { Mutiny.Query<Tuple> query = s.createNativeQuery(finalStr, Tuple.class);
		return  query.getResultList();
		}).subscribeAsCompletionStage());
		return list;       
    }
	public Mono<List<Tuple>> getEmpDepartment(EmpLeaveEntryDTO data,String userId) {
        String str = " select "
        		+ " emp.emp_no, "
        		+ " emp.emp_name , "
        		+ " erp_campus.campus_name, erp_department.department_name, "
        		+ " emp_leave_entry.leave_start_date, "
        		+ " emp_leave_entry.leave_start_session, "
        		+ " emp_leave_entry.leave_end_date, "
        		+ " emp_leave_entry.leave_end_session, "
        		+ " emp_leave_type.leave_type_code,emp_leave_type.leave_type_name,emp_leave_entry.number_of_days_leave ,"
        		+ " emp_leave_entry.leave_reason "
        		+ " from emp_leave_entry "
        		+ " left join emp ON emp.emp_id= emp_leave_entry.emp_id AND emp.record_status='A' "
        		+ " left join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id= emp.erp_campus_department_mapping_id AND erp_campus_department_mapping.record_status='A' "
        		+ " left join erp_campus ON erp_campus.erp_campus_id=erp_campus_department_mapping.erp_campus_id AND erp_campus.record_status='A' "
        		+ " left join erp_department ON erp_department.erp_department_id = erp_campus_department_mapping.erp_department_id AND erp_department.record_status='A' "
        		+ " left join emp_leave_type ON emp_leave_type.emp_leave_type_id=emp_leave_entry.emp_leave_type_id AND emp_leave_type.record_status='A' "
        		+ " where leave_end_date>= Curdate() "
        		+ " and leave_start_date<= DATE_ADD(Curdate(), INTERVAL 30 DAY) "
        		+ " and emp_leave_approver_id =1210 "
        		+ " and emp_leave_entry.record_status ='A'";
        
        
        String finalStr = str;
		Mono<List<Tuple>> list = Mono.fromFuture(sessionFactory.withSession(s -> { Mutiny.Query<Tuple> query = s.createNativeQuery(finalStr, Tuple.class);
		return  query.getResultList();
		}).subscribeAsCompletionStage());
		return list;
  }
	
	

}
