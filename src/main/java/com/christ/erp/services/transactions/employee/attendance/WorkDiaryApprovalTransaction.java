package com.christ.erp.services.transactions.employee.attendance;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.Tuple;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.attendance.EmpWorkDiaryEntriesDBO;

@Repository
public class WorkDiaryApprovalTransaction {
	
	@Autowired
	private Mutiny.SessionFactory sessionFactory;
	
	public void merge(List<EmpWorkDiaryEntriesDBO> dbo) {
		sessionFactory.withTransaction((session, txt) -> session.mergeAll(dbo.toArray())).subscribeAsCompletionStage();
	}

	public List<Integer> getEmployeesList(String userId) {
		List<Integer> empList = new ArrayList<Integer>();
		String str =  " select emp_approvers.emp_id as empId from emp_approvers"
		            + " inner join erp_users ON erp_users.emp_id = emp_approvers.work_diary_approver_id"
		            + " where erp_users.erp_users_id=:userId and emp_approvers.record_status='A' and erp_users.record_status='A'";
		List<Tuple> empId = sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).setParameter("userId",Integer.parseInt(userId)).getResultList()).await().indefinitely();
		for (Tuple mapping : empId) {
		empList.add(Integer.parseInt(mapping.get("empID").toString()));
		}
		return empList;
	}
	
	public List<EmpWorkDiaryEntriesDBO> getEmployeeDetailsForApprover(Map<String,String> requestParams, List<Integer> employeesIdList ) {
		LocalDate startDate = Utils.convertStringDateToLocalDate(requestParams.get("startDate"));
		LocalDate endDate =  Utils.convertStringDateToLocalDate(requestParams.get("endDate"));
		String str = " select distinct dbo from EmpWorkDiaryEntriesDBO dbo "
			       + " left join fetch dbo.empWorkDiaryEntriesDetailsDBOSet dbos where dbo.recordStatus='A' and dbo.empDBO.id in (:employeesIdList) and dbo.erpWorkEntryDate between (:startDate) and (:endDate) order by dbo.empDBO.id  ASC";
		return sessionFactory.withSession(s -> s.createQuery(str,EmpWorkDiaryEntriesDBO.class).setParameter("startDate", startDate).setParameter("endDate", endDate).setParameter("employeesIdList", employeesIdList).getResultList()).await().indefinitely();
	}

	public List<EmpWorkDiaryEntriesDBO> getEmployeeDetailsForUpdate(Map<String,String> requestParams, List<Integer> empids) {
	    LocalDate startDate = Utils.convertStringDateToLocalDate(requestParams.get("startDate"));
		LocalDate endDate =  Utils.convertStringDateToLocalDate(requestParams.get("endDate"));
		String str = " select distinct dbo from EmpWorkDiaryEntriesDBO dbo"
				    +" where dbo.empDBO.id IN(:empids) and dbo.erpWorkEntryDate between (:startDate) and (:endDate) and dbo.recordStatus='A'";
		String finalStr = str;
        List<EmpWorkDiaryEntriesDBO> list = sessionFactory.withSession(s-> {
			Mutiny.Query<EmpWorkDiaryEntriesDBO> query = s.createQuery(finalStr,EmpWorkDiaryEntriesDBO.class);
			query.setParameter("empids", empids);
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
         	return query.getResultList();
		}).await().indefinitely();
		return list;	
	}
	
	public Tuple getApproversIdByEmployeeId1(int empIds) {
		String str = " select erp_users.erp_users_id as usersId, emp.emp_personal_email as personalEmailId from erp_users"
				   + " inner join emp ON emp.emp_id = erp_users.emp_id and emp.record_status='A'"
				   + " inner join emp_approvers ON emp_approvers.work_diary_approver_id = emp.emp_id and emp_approvers.record_status='A' "
				   + " where emp_approvers.emp_id=:empId and erp_users.record_status='A'";
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).setParameter("empId", empIds).getSingleResultOrNull()).await().indefinitely();					
	}

	public List<Tuple> getEmpIdandUserId(List<Integer> employeesIdList) {
		String str = "  select erp_users.erp_users_id as userId, emp.emp_id as empId, emp.emp_name as empName from erp_users "
				   + "  inner join emp on emp.emp_id=erp_users.emp_id "
				   + "  where emp.emp_id in (:employeesIdList) ";
		List<Tuple> empDetails = sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).setParameter("employeesIdList", employeesIdList).getResultList()).await().indefinitely();
		return empDetails;
	}

	public List<Tuple> getEmployeesLists(String userId) {
		String str =  " select emp_approvers.emp_id as empId, emp.emp_no as empNo, emp.emp_name as empName from emp_approvers"
				    + " inner join erp_users ON erp_users.emp_id = emp_approvers.work_diary_approver_id"
				    + " inner join emp ON emp.emp_id = emp_approvers.emp_id"
				    + "	where erp_users.erp_users_id=:userId and emp_approvers.record_status='A' and erp_users.record_status='A'"; 
		return sessionFactory.withSession(s -> s.createNativeQuery(str,Tuple.class).setParameter("userId", Integer.parseInt(userId)).getResultList()).await().indefinitely();
	}
}
