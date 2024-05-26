package com.christ.erp.services.transactions.employee.leave;

import java.util.List;

import javax.persistence.Tuple;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveEntryDBO;

import reactor.core.publisher.Mono;

@Repository
public class LeaveEntryBySupervisorTransaction {
	
	@Autowired
    private Mutiny.SessionFactory sessionFactory;
	
	public Mono<List<EmpLeaveEntryDBO>> getGridData(Integer empId) {
        String str = "select bo from EmpLeaveEntryDBO bo where bo.recordStatus = 'A' and bo.isSupervisor = 1 and bo.approverId.id=:empId ORDER BY bo.startDate DESC";
        return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(str, EmpLeaveEntryDBO.class).setParameter("empId",empId).getResultList()).subscribeAsCompletionStage());
    }
	
	public Mono<List<EmpLeaveEntryDBO>> getEmployeeLeaveHistory(Integer empId) {
        String str = "select bo from EmpLeaveEntryDBO bo where bo.recordStatus = 'A' and bo.empID.id=:empId";
        return Mono.fromFuture(sessionFactory.withSession(s->s.createQuery(str, EmpLeaveEntryDBO.class).setParameter("empId",empId).getResultList()).subscribeAsCompletionStage());
    }
	
	
	public List<Tuple> getEmployeeIdOrName(String employeeIdOrName,Integer empId) {
		String query = "select emp.emp_id as id ,emp.emp_name as name ,emp.emp_no as empId from emp_approvers ea"
				      + " inner join emp on emp.emp_id=ea.emp_id  "
				      + " where ea.leave_approver_id = :empId and emp.record_status='A' and  ea.record_status = 'A' and (emp.emp_no like :employeeId or emp.emp_name like :empName)";
		return sessionFactory.withSession(s -> s.createNativeQuery(query,Tuple.class).setParameter("empName", employeeIdOrName+"%")
				.setParameter("employeeId", employeeIdOrName+"%").setParameter("empId", empId).getResultList()).await().indefinitely();
	}	
	
}
