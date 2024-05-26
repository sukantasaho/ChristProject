package com.christ.erp.services.transactions.employee.leave;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.LockModeType;
import javax.persistence.Tuple;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveAllocationDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveTypeDBO;

@Repository
public class BulkLeaveUploadTransaction {
	
	@Autowired
	private Mutiny.SessionFactory sessionFactory;

		public List<Tuple> getEmployeesList() {
		String str = "select emp.emp_id as empId, emp.emp_no as fingerPrintId from emp "
				+ " where emp.record_status='A' order by emp.emp_id ASC ";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
			return query.getResultList();
		}).await().indefinitely();
		return list;
	}

		public List<Tuple> getLeaveTypeList() {
			String str = "select emp_leave_type.emp_leave_type_id as leaveTypeId, emp_leave_type.leave_type_name as leaveTypeName,emp_leave_type.is_leave as canAllotLeave  from emp_leave_type "
					+ " where emp_leave_type.record_status='A' order by emp_leave_type.emp_leave_type_id ASC ";
			List<Tuple> list = sessionFactory.withSession(s -> {
				Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
				return query.getResultList();
			}).await().indefinitely();
			return list;
		}

		public void update(List<Object> dbo) {
			sessionFactory.withTransaction((session, tx) -> session.mergeAll(dbo.toArray())).await().indefinitely();
		}

		public List<Tuple> getEmployeeSundayandHolidayList(List<Integer> empIdList) {
			String str = " select e.emp_id as empId , j.is_sunday_working  as sundayWorking , j.is_holiday_working as holidayWorking  From  emp_job_details j "
					+ "	inner join emp e ON e.emp_id = j.emp_id  "
					+ "	where  j.record_status='A' and e.emp_id in (:empId) and e.record_status='A' ";
			List<Tuple> list = sessionFactory.withSession(s -> {
				Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
				query.setParameter("empId", empIdList);
				return query.getResultList();
			}).await().indefinitely();
			return list;
		}

		public Map<String, EmpLeaveAllocationDBO> getEmployeeLeaveAllocationDetails() {
				String str = " select a.emp_leave_allocation_id as id, a.emp_id as empId, a.emp_leave_type_id as leaveTypeId, "
						+ " a.leaves_allocated as leaveAllocated, a.leaves_sanctioned as leaveSanctioned, a.leaves_remaining as leaveRemaining, "
						+ " a.leaves_pending as leavePending, a.`year` as year, a.month as month "
						+ " from emp_leave_allocation a "
						+ " where a.record_status='A' ";
				List<Tuple> list = sessionFactory.withSession(s -> {
					Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
					return query.getResultList();
				}).await().indefinitely();
				
				Map<String, EmpLeaveAllocationDBO> empLeaveAllocationDBOMap = new LinkedHashMap<String, EmpLeaveAllocationDBO>();
				for (Tuple dbo : list) {
					EmpLeaveAllocationDBO empLeaveAllocationDBO=new EmpLeaveAllocationDBO();
					empLeaveAllocationDBO.id=Integer.parseInt(dbo.get("id").toString());
					if (!Utils.isNullOrEmpty(dbo.get("empId"))) {
						EmpDBO empDBO = new EmpDBO();
						empDBO.id = Integer.parseInt(dbo.get("empId").toString());
						empLeaveAllocationDBO.empDBO = empDBO;
					}
					if (!Utils.isNullOrEmpty(dbo.get("leaveTypeId"))) {
						EmpLeaveTypeDBO empLeaveTypeDBO = new EmpLeaveTypeDBO();
						empLeaveTypeDBO.id = Integer.parseInt(dbo.get("leaveTypeId").toString());
						empLeaveAllocationDBO.leaveType = empLeaveTypeDBO;
					}
					empLeaveAllocationDBO.allottedLeaves=new BigDecimal(dbo.get("leaveAllocated").toString());
					empLeaveAllocationDBO.sanctionedLeaves=new BigDecimal(dbo.get("leaveSanctioned").toString());
					empLeaveAllocationDBO.leavesRemaining=new BigDecimal(dbo.get("leaveRemaining").toString());
					empLeaveAllocationDBO.leavesPending=new BigDecimal(dbo.get("leavePending").toString());
					if (!Utils.isNullOrEmpty(dbo.get("year"))) 
					empLeaveAllocationDBO.year=Integer.parseInt(dbo.get("year").toString());
					empLeaveAllocationDBO.month=dbo.get("month").toString();
					empLeaveAllocationDBO.recordStatus = 'A';
					if (!Utils.isNullOrEmpty(dbo.get("empId")) && !Utils.isNullOrEmpty(dbo.get("leaveTypeId")) && !Utils.isNullOrEmpty(dbo.get("year"))) 
					empLeaveAllocationDBOMap.put(empLeaveAllocationDBO.getEmpDBO().getId()+"-"+empLeaveAllocationDBO.getLeaveType().id+"-"+empLeaveAllocationDBO.year,empLeaveAllocationDBO);
				}
				return empLeaveAllocationDBOMap;
		}

		public Tuple bulkLeaveUploadDownloadFormat() {
			String str = " select url_access_link.file_name_unique as fileNameUnique, url_access_link.file_name_original as fileNameOriginal,"
					+ "url_folder_list.upload_process_code as uploadProcessCode from url_folder_list "
					+ "inner join url_access_link On url_access_link.url_folder_list_id = url_folder_list.url_folder_list_id "
					+ "inner join erp_bulk_upload_format On erp_bulk_upload_format.bulk_upload_format_url_id = url_access_link.url_access_link_id "
					+ "where erp_bulk_upload_format.bulk_upload_format_name=:name  "
					+ "and url_folder_list.record_status='A' and url_access_link.record_status='A' and erp_bulk_upload_format.record_status='A' ";
			Tuple list = (Tuple) sessionFactory.withSession(s -> {
				Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
				query.setParameter("name", "LEAVE_UPLOAD");
				return query.getSingleResult();
			}).await().indefinitely();
			return list;
		}

}
