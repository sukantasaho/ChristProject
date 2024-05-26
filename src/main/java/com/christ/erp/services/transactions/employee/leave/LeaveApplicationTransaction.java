package com.christ.erp.services.transactions.employee.leave;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.Tuple;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.DBGateway;
import com.christ.erp.services.common.ICommitGenericTransactional;
import com.christ.erp.services.common.ISelectGenericTransactional;
import com.christ.erp.services.common.ITransactional;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpNotificationsDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpHolidayEventsDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveAllocationDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveEntryDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveEntryDetailsDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveTypeDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateGroupDBO;
import com.christ.erp.services.dbqueries.employee.LeaveQueries;
import com.christ.erp.services.dto.employee.leave.EmpLeaveEntryDTO;
import reactor.core.publisher.Mono;

@Repository
public class LeaveApplicationTransaction {
	
	@Autowired
	private Mutiny.SessionFactory sessionFactory;

	public List<Tuple> getEmployeeDetails(String value) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select distinct emp.emp_id AS 'id', emp.emp_name as 'name' , d.emp_designation_name as 'designation', dep.department_name as 'department', c.campus_name as 'campus',c.erp_campus_id as 'campusId', c.erp_location_id as 'locationId' "
						+ " from emp as emp "
						+ " left join emp_designation d on d.emp_designation_id=emp.emp_designation_id  AND d.record_status = 'A' "
						+ " inner join erp_campus_department_mapping cd on cd.erp_campus_department_mapping_id=emp.erp_campus_department_mapping_id  AND cd.record_status = 'A' "
						+ " inner join erp_department dep on dep.erp_department_id=cd.erp_department_id  AND dep.record_status = 'A' "
						+ " inner join erp_campus c on c.erp_campus_id=cd.erp_campus_id  AND c.record_status = 'A' "
						+ " where emp.emp_id =:empId and emp.record_status= 'A' order BY emp.emp_name asc ";
				Query query = context.createNativeQuery(str, Tuple.class);
				query.setParameter("empId", value);
				return query.getResultList();
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getLeaveDetails(int empId, int year) throws Exception {
//		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
//			@SuppressWarnings("unchecked")
//			@Override
//			public List<Tuple> onRun(EntityManager context) throws Exception {
//				String str = "select e1.leave_type_code as 'LeaveTypeCode',e1.leave_type_name as 'LeaveTypeName', e.leaves_remaining as 'LeaveRemaining' "
//						+ " ,e1.leave_type_color_code_hexvalue as 'LeaveTypeColorCodeHexvalue',e.leaves_sanctioned as 'sanctionedLeaves', e.leaves_allocated as 'allocatedLeaves',e.leaves_pending as 'pendingLeave' from emp_leave_allocation e "
//						+ " inner join emp_leave_type e1 on e1.emp_leave_type_id=e.emp_leave_type_id and e1.record_status='A' "
//						+ " WHERE e.emp_id =:empId and e.year=:year and e.record_status='A' ";
//				Query query = context.createNativeQuery(str, Tuple.class);
//				query.setParameter("empId", empId);
//				query.setParameter("year", year);
//				return query.getResultList();
//			}
//
//			@Override
//			public void onError(Exception error) throws Exception {
//				throw error;
//			}
//		});
		
		
		String str = " select e1.leave_type_code as 'LeaveTypeCode',e1.leave_type_name as 'LeaveTypeName', e.leaves_remaining as 'LeaveRemaining' "
				+ "	,e1.leave_type_color_code_hexvalue as 'LeaveTypeColorCodeHexvalue',e.leaves_sanctioned as 'sanctionedLeaves', e.leaves_allocated as 'allocatedLeaves',e.leaves_pending as 'pendingLeave' from emp_leave_allocation e "
				+ "	inner join emp_leave_type e1 on e1.emp_leave_type_id=e.emp_leave_type_id and e1.record_status='A' "
				+ "	WHERE e.emp_id =:empId and e.year=:year and e.record_status='A' ";
		List<Tuple> list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
			query.setParameter("empId", empId);
			query.setParameter("year", year);
			return query.getResultList();
		}).await().indefinitely();
		return list;
		
	}

	public EmpLeaveEntryDBO saveOrUpdate(EmpLeaveEntryDBO dbo) throws Exception {
		return DBGateway.runJPA(new ICommitGenericTransactional<EmpLeaveEntryDBO>() {
			@Override
			public EmpLeaveEntryDBO onRun(EntityManager context) throws Exception {
				if (dbo.id == 0) {
					context.persist(dbo);
				} else {
					context.merge(dbo);
				}
				return dbo;
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	
	public EmpLeaveEntryDBO update(EmpLeaveEntryDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.find(EmpLeaveEntryDBO.class, dbo.id).call(() -> session.merge(dbo))).await().indefinitely();
		return dbo;
	}

	public EmpLeaveEntryDBO save(EmpLeaveEntryDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
		return dbo;
	}

	public EmpLeaveAllocationDBO getEmployeeLeaveAllocationDetails(EmpLeaveEntryDTO data, int year) {
		String str = " select e from  EmpLeaveAllocationDBO e where e.empDBO.id=:empId and e.leaveType.id=:empLeaveTypeId and e.year=:year and e.recordStatus='A'";
		EmpLeaveAllocationDBO empLeaveAllocationDBO = sessionFactory.withSession(s-> {
				Mutiny.Query<EmpLeaveAllocationDBO> query = s.createQuery(str,EmpLeaveAllocationDBO.class);
				query.setParameter("empId", Integer.parseInt(data.employeeId));
				query.setParameter("year", year);
				query.setParameter("empLeaveTypeId", Integer.parseInt(data.leaveTypecategory.id));
				return query.setLockMode(LockModeType.PESSIMISTIC_WRITE).getSingleResultOrNull();
			}).await().indefinitely();
			return empLeaveAllocationDBO;	
	}
	
	public void update(EmpLeaveAllocationDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.find(EmpLeaveEntryDBO.class, dbo.id).call(() -> session.merge(dbo))).await().indefinitely();
	}

	public void save(EmpLeaveAllocationDBO dbo) {
		sessionFactory.withTransaction((session, tx) -> session.persist(dbo)).await().indefinitely();
	}

	public Tuple getDataOnLeaveType(String empLeaveTypeId) throws Exception  {
		return DBGateway.runJPA(new ISelectGenericTransactional<Tuple>() {
			@SuppressWarnings("unchecked")
			@Override
			public Tuple onRun(EntityManager context) throws Exception {
				String str = "select  e.is_leave_type_document_required as 'isLeaveTypeDocumentRequired',e.leave_policy as 'leavePolicy',e.max_online_leave_in_month as maxOnlineLeaveMonth,e.is_continous_days as continousDays,e.is_leave as isLeave,e.is_partial_allowed as isPartialAllowed "
						+ ",e.is_leave_advance as isLeaveAdvance from emp_leave_type e "
						+ " WHERE e.emp_leave_type_id =:id  and e.record_status='A' ";
				Query query = context.createNativeQuery(str, Tuple.class);
				query.setParameter("id", empLeaveTypeId);
				return (Tuple) Utils.getUniqueResult(query.getResultList());
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getGridData( Integer empId, String year, String leaveApplication) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select e.emp_leave_entry_id as ID, e1.leave_type_name as LeaveTypeName, e.leave_start_date as StartDate, e.leave_end_date as EndDate,e.leave_start_session as FromSession,e.leave_end_session as ToSession,e.emp_id as EmpID,e1.emp_leave_type_id as LeaveTypeID,e.is_offline as mode,e.leave_document_url as leaveDocumentUrl, "
						+ " w.applicant_status_display_text as applicantStatusDisplayText,w.application_status_display_text as applicationStatusDisplayText,w.process_code as processCode,e.approver_latest_comment as approverLatestComment,e.authorizer_latest_comment as authorizerLatestComment,e.number_of_days_leave as totalNumofDays,e.leave_reason as reason,  "
						+ "  ual.file_name_unique AS doc_file_name_unique, ual.file_name_original AS doc_file_name_original,folder.upload_process_code AS doc_upload_process_code "
						+ " from emp_leave_entry e "
						+ " inner join emp_leave_type e1 ON e1.emp_leave_type_id = e.emp_leave_type_id and e1.record_status = 'A' "
						+ " left join erp_work_flow_process w ON w.erp_work_flow_process_id = e.erp_applicant_work_flow_process_id and w.record_status = 'A' "
						+ " left join url_access_link as ual on ual.url_access_link_id = e.leave_document_url_id and ual.record_status = 'A' "
						+ " left join url_folder_list as folder on ual.url_folder_list_id = folder.url_folder_list_id and folder.record_status = 'A' "
						+ " where e.emp_id=:empId  and e.record_status='A' and e.leave_year=:leave_year";
				if (leaveApplication.equalsIgnoreCase("offline")) {
					str = str + " and e.is_offline=:offline and w.erp_work_flow_process_id=:erpWorkFlowProcessId ";
				}
				if (leaveApplication.equalsIgnoreCase("supervisor")) {
					str = str + " and e.is_supervisor=:supervisor  ";
				}
				str = str + " ORDER BY e.leave_start_date DESC ";
				Query query = context.createNativeQuery(str, Tuple.class);
				query.setParameter("empId", empId);
				query.setParameter("leave_year", year);
				if (leaveApplication.equalsIgnoreCase("offline")) {
					query.setParameter("offline", true);
					query.setParameter("erpWorkFlowProcessId", 9);
				}else if(leaveApplication.equalsIgnoreCase("supervisor")){
					query.setParameter("supervisor", true);					
				}else {
					//query.setParameter("offline", false);
					//query.setParameter("erpWorkFlowProcessId", 1);
				}
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> duplicateCheckForLeaveEntry(EmpLeaveEntryDTO data) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = " select e.emp_leave_entry_id as ID,e.leave_start_session as startSession,e.leave_end_session as endSession  from emp_leave_entry e "
						+ " where e.emp_id=:empId and (((:startDate) between e.leave_start_date and e.leave_end_date ) or ((:endDate ) between e.leave_start_date and e.leave_end_date) "
						+ " or ((:startDate) <= e.leave_start_date and (:endDate) >= e.leave_end_date )) and e.record_status='A' ";
				Query query = context.createNativeQuery(str, Tuple.class);
				query.setParameter("empId", data.employeeId);
				query.setParameter("startDate", data.startDate);
				query.setParameter("endDate", data.endDate);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getEmpLeaveCategoryDetails(EmpLeaveEntryDTO data) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select d.allotted_leaves as allocatedLeaves from emp_leave_category_allotment a "
						+ " inner join emp_leave_category_allotment_details d on d.emp_leave_category_allotment_id = a.emp_leave_category_allotment_id and d.record_status='A' "
						+ " inner join emp_job_details j on j.emp_leave_category_allotment_id = a.emp_leave_category_allotment_id and j.record_status='A' and d.emp_leave_type_id=:leaveTypeId  "
						+ " and j.emp_id=:empId " + " and a.record_status ='A'";
				Query query = context.createNativeQuery(str, Tuple.class);
				query.setParameter("empId", data.employeeId);
				query.setParameter("leaveTypeId", data.leaveTypecategory.id);
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getEmpLeavesAppliedForOnline(EmpLeaveEntryDTO data) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select e.leave_start_date as startDate, e.leave_end_date as endDate, e.leave_start_session as startSession, e.leave_end_session as endSession from emp_leave_entry e "
						+ " where e.emp_id=:empId and e.emp_leave_type_id=:leaveTypeId and e.is_offline='false' "
						+ " and  (e.leave_start_date BETWEEN date(:startDate) AND date(:endDate)) or (e.leave_end_date BETWEEN date(:startDate) AND date(:endDate)) and e.record_status='A' ";
				Query query = context.createNativeQuery(str, Tuple.class);
				LocalDate start = data.startDate.withDayOfMonth(1);
				LocalDate end = data.startDate.withDayOfMonth((data.startDate).lengthOfMonth());
				query.setParameter("empId", data.employeeId);
				query.setParameter("leaveTypeId", data.leaveTypecategory.id);
				query.setParameter("startDate", start);
				query.setParameter("endDate", end);
				return query.getResultList();
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getEmpLeavesAppliedForOnlineNew(EmpLeaveEntryDTO data, int month, int year) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = "select emp_leave_entry_details.emp_id as emp_id, sum(emp_leave_entry_details.number_of_days_leave) "
						+ " as leaves_in_month from emp_leave_entry_details "
						+ " inner join emp_leave_entry on emp_leave_entry.emp_leave_entry_id= emp_leave_entry_details.emp_leave_entry_id and emp_leave_entry.record_status='A' "
						+ " left join erp_work_flow_process on erp_work_flow_process.erp_work_flow_process_id=emp_leave_entry.erp_application_work_flow_process_id and erp_work_flow_process.record_status='A' "
						+ " where (erp_work_flow_process.process_code='LEAVE_APPLICATION_AUTHORIZER_AUTHORIZED' or emp_leave_entry.is_pending=1) "
						+ " and emp_leave_entry_details.record_status='A' and year(leave_date)=:year "
						+ " and month(leave_date)=:month and emp_leave_entry.emp_leave_type_id=:leave_type_id "
						+ " and emp_leave_entry.emp_id=:emp_id group by emp_leave_entry_details.emp_id,emp_leave_entry_details.emp_leave_type_id";
				Query query = context.createNativeQuery(str, Tuple.class);
				query.setParameter("emp_id", data.employeeId);
				query.setParameter("leave_type_id", data.leaveTypecategory.id);
				query.setParameter("month", month);
				query.setParameter("year", year);
			
				return query.getResultList();
			}

			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public Tuple getInitilizeMonthForLeaveType(EmpLeaveEntryDTO data)  {
		String str = "select a.leave_initialize_month as 'month'  from emp_leave_category_allotment a "
				+ " inner join emp_leave_category_allotment_details d on a.emp_leave_category_allotment_id = d.emp_leave_category_allotment_id and d.record_status='A' "
				+ " inner join emp_leave_type t on d.emp_leave_type_id = t.emp_leave_type_id and t.record_status='A' "
				+ " inner join emp_job_details j on j.emp_leave_category_allotment_id = a.emp_leave_category_allotment_id "
				+ " inner join emp e ON e.emp_id = j.emp_id "
				+ " where t.emp_leave_type_id=:leaveTypeId and a.record_status='A' and e.emp_id=:empID";
		Tuple list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
			query.setParameter("leaveTypeId", data.leaveTypecategory.id);
			query.setParameter("empID", data.employeeId);
			return query.getSingleResultOrNull();
		}).await().indefinitely();
		return list;
	}

	public Tuple getIsSundayWorkingDay(EmpLeaveEntryDTO data) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<Tuple>() {
			@Override
			public Tuple onRun(EntityManager context) throws Exception {
				String str = "select j.is_sunday_working  as sundayWorking  From  emp_job_details j "
						+ "inner join emp e ON e.emp_id = j.emp_id  "
						+ "where  j.record_status='A' and e.emp_id=:empID and e.record_status='A' ";
				Query query = context.createNativeQuery(str, Tuple.class);
				query.setParameter("empID", data.employeeId);
				Tuple list = (Tuple) Utils.getUniqueResult(query.getResultList());
				return list;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}			

	public Tuple getIsHolidayWorkingDay(EmpLeaveEntryDTO data) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<Tuple>() {
			@Override
			public Tuple onRun(EntityManager context) throws Exception {
				String str = " select emp_job_details.is_holiday_working as holidayWorking from emp "
						+ " inner join emp_job_details ON emp_job_details.emp_job_details_id = emp.emp_job_details_id "
						+ " where emp.record_Status like 'A' and emp.emp_id=:empID "
						+ " and emp_job_details.record_status like 'A' ";
				Query query = context.createNativeQuery(str, Tuple.class);
				query.setParameter("empID", data.employeeId);
				Tuple list = (Tuple) Utils.getUniqueResult(query.getResultList());
				return list;
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public List<Tuple> getHolidays(String startDate1, String endDate1, String locationId) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = " select DATE_FORMAT(holiday_events_start_date, '%d/%m/%Y') as startDate , DATE_FORMAT(holiday_events_end_date, '%d/%m/%Y') as endDate "
						+ " from emp_holiday_events h "
						+ " inner join erp_location ON erp_location.erp_location_id = h.erp_location_id "
						+ " where h.record_status = 'A' and "
						+ " h.holiday_events_start_date between :startDate and :endDate and "
						+ " h.holiday_events_end_date between :startDate and :endDate "
						+ " and erp_location.record_status='A' and erp_location.erp_location_id=:locationId and emp_holiday_events_type_name='Holiday'";
				Query query = context.createNativeQuery(str, Tuple.class);
				if (startDate1.contains("Z") && endDate1.contains("Z")) {
					query.setParameter("startDate", Utils.convertStringDateTimeToLocalDate(startDate1));
					query.setParameter("endDate", Utils.convertStringDateTimeToLocalDate(endDate1));
				} else {
					query.setParameter("startDate", Utils.convertStringDateToLocalDate(startDate1));
					query.setParameter("endDate", Utils.convertStringDateToLocalDate(endDate1));
				}
				query.setParameter("locationId", Integer.parseInt(locationId));
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}

	public void saveErpNotification(ErpNotificationsDBO erpNotifications) {
		sessionFactory.withTransaction((session, tx) -> session.persist(erpNotifications)).await().indefinitely();
	}

	public ErpNotificationsDBO removeNotifications(int erpWorkFlowProcessNotificationsId, int userId,int empLeaveEntryId) {
		 String str = "select bo from ErpNotificationsDBO bo where bo.recordStatus = 'A' and bo.entryId=:empLeaveEntryId and bo.erpWorkFlowProcessNotificationsDBO.id=:erpWorkFlowProcessNotificationsId "; // and bo.erpNotificationUserEntriesDBO.id=:userId 
		    return (ErpNotificationsDBO) sessionFactory.withSession(s->s.createQuery(str, ErpNotificationsDBO.class)
		    		.setParameter("erpWorkFlowProcessNotificationsId", erpWorkFlowProcessNotificationsId)
		    		.setParameter("empLeaveEntryId", empLeaveEntryId)
		    		.getSingleResultOrNull()).await().indefinitely();
	}

	public void updateEmpLeaveEntries(List<EmpLeaveEntryDBO> dbos) {
		sessionFactory.withTransaction((session, txt) -> session.mergeAll(dbos.toArray())).subscribeAsCompletionStage();
	}

	public List<EmpLeaveEntryDBO> getEmpLeaveEntryDBOForUpdate(List<Integer> leaveApplicationEntriesId) {
		 String str = "select bo from EmpLeaveEntryDBO bo where bo.recordStatus = 'A' and bo.id in (:leaveApplicationEntriesId)"; //and bo.erpWorkFlowProcessNotificationsDBO.id=:erpWorkFlowProcessNotificationsId and bo.erpNotificationUserEntriesDBO.id=:userId 
		    return (List<EmpLeaveEntryDBO>) sessionFactory.withSession(s->s.createQuery(str, EmpLeaveEntryDBO.class)
		    		.setParameter("leaveApplicationEntriesId", leaveApplicationEntriesId)
		    		.getResultList()).await().indefinitely();
	}

	public List<Tuple> getEmpIdandUserId(List<Integer> employeesIdList) {
		String str = "   select erp_users.erp_users_id as userId, emp.emp_id as empId  from erp_users "
				+ "  inner join emp on emp.emp_id=erp_users.emp_id "
				+ "  where emp.emp_id in (:employeesIdList) and erp_users.record_status='A' ";
		List<Tuple> empDetails = sessionFactory.withSession(s -> s.createNativeQuery(str, Tuple.class).setParameter("employeesIdList", employeesIdList).getResultList()).await().indefinitely();
		return empDetails;
	}

	public EmpLeaveTypeDBO getDataOnLeaveType1(String empLeaveTypeId) {
		String str = "select bo from EmpLeaveTypeDBO bo where bo.recordStatus = 'A' and bo.id=:id ";
		EmpLeaveTypeDBO empLeaveTypeDBO = sessionFactory.withSession(s -> {
			Mutiny.Query<EmpLeaveTypeDBO> query = s.createQuery(str, EmpLeaveTypeDBO.class);
			query.setParameter("id", Integer.parseInt(empLeaveTypeId));
			return query.getSingleResultOrNull();
		}).await().indefinitely();
		return empLeaveTypeDBO;
	}
	public Tuple getInitilizeMonthForEmployee(int employeeId)  {
		String str = "select a.leave_initialize_month as 'month'  from emp_leave_category_allotment a "
				+ " inner join emp_job_details j on j.emp_leave_category_allotment_id = a.emp_leave_category_allotment_id "
				+ " inner join emp e ON e.emp_id = j.emp_id "
				+ " where  a.record_status='A' and e.emp_id=:empID";
		Tuple list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
			query.setParameter("empID", employeeId);
			return query.getSingleResultOrNull();
		}).await().indefinitely();
		return list;
	}
	public EmpLeaveTypeDBO getEmpLeaveType(String leaveTypeId) {
		String query = "select distinct dbo from EmpLeaveTypeDBO dbo where dbo.recordStatus='A' and dbo.id=:leaveTypeId";
		return sessionFactory.withSession(s -> s.createQuery(query,EmpLeaveTypeDBO.class).setParameter("leaveTypeId", Integer.parseInt(leaveTypeId)).getSingleResultOrNull()).await().indefinitely();
    }

	public List<EmpHolidayEventsDBO> getEmpHolidayEventsDBO(EmpLeaveEntryDTO data, int year) {
		String str = "select bo from EmpHolidayEventsDBO bo where bo.recordStatus = 'A' and bo.empHolidayEventsTypeName='Holiday' and  bo.erpLocationId.id=:locationId and"
				+ " bo.erpAcademicYearId.academicYear=:year "; //and (e.holidayEventsStartDate BETWEEN date(:startDate) AND date(:endDate)) or (e.holidayEventsEndDate BETWEEN date(:startDate) AND date(:endDate)

	    return (List<EmpHolidayEventsDBO>) sessionFactory.withSession(s->s.createQuery(str, EmpHolidayEventsDBO.class)
	    		.setParameter("locationId", Integer.parseInt(data.locationId))
	    		.setParameter("year", year)
	    		//.setParameter("startDate", data.startDate)
	    		//.setParameter("endDate", data.endDate)
	    		.getResultList()).await().indefinitely();
	}
	
	@SuppressWarnings("unchecked")
	public boolean leaveEntryDetailsDBOSave(List<EmpLeaveEntryDetailsDBO> dbo) {
		sessionFactory.withTransaction((session, tx) -> session.mergeAll(dbo.toArray())).await().indefinitely();
		return true;
	}
	public List<Tuple> getHolidaysList(String empId, String leaveType) throws Exception {
		return DBGateway.runJPA(new ISelectGenericTransactional<List<Tuple>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Tuple> onRun(EntityManager context) throws Exception {
				String str = " select Date_format(emp_holiday_events.holiday_events_start_date, '%Y-%m-%d') as startDate, emp_holiday_events.holiday_events_description reason From emp "
						+ " inner join emp_job_details ON emp_job_details.emp_job_details_id = emp.emp_job_details_id and emp_job_details.record_status='A' "
						+ " inner join erp_campus_department_mapping ON erp_campus_department_mapping.erp_campus_department_mapping_id = emp.erp_campus_department_mapping_id and erp_campus_department_mapping.record_status='A' "
						+ " inner join erp_campus ON erp_campus.erp_campus_id = erp_campus_department_mapping.erp_campus_id and erp_campus.record_status='A' "
							+ " inner join erp_location ON erp_location.erp_location_id = erp_campus.erp_location_id and erp_location.record_status='A' "
						+ " inner join emp_holiday_events on erp_location.erp_location_id = emp_holiday_events.erp_location_id and emp_holiday_events.record_status='A' "
						+ " where emp.emp_id=:empId and  emp_holiday_events_type_name=:holiday and (emp_job_details.is_holiday_working is null or emp_job_details.is_holiday_working=0) "
						+ " order by emp_holiday_events.holiday_events_start_date;";
				Query query = context.createNativeQuery(str, Tuple.class);
					query.setParameter("empId", empId);
					if(leaveType.equals("Restricted Holidays")) {
						query.setParameter("holiday", "Restricted Holiday");
					}else {
						query.setParameter("holiday", "Holiday");
					}
				return query.getResultList();
			}
			@Override
			public void onError(Exception error) throws Exception {
				throw error;
			}
		});
	}
	public Tuple getEmployeeDetails(String value, String userId){
		String qry = "select distinct emp.emp_id AS 'id', emp.emp_name as 'name' , d.emp_designation_name as 'designation',"
				+ " dep.department_name as 'department',c.campus_name as 'campus' ,emp.emp_no as 'empNo',  "
				+ " emp_job_details.is_sunday_working as 'isSundayWorking',emp_job_details.is_holiday_working as 'isHolidayWorking' from emp "
				+ " inner join emp_job_details on emp_job_details.emp_id = emp.emp_id  "
				+ " inner join erp_users on erp_users.emp_id = emp.emp_id "
				+ " left join emp_designation d on d.emp_designation_id=emp.emp_designation_id  AND d.record_status = 'A' "
				+ " inner join erp_campus_department_mapping cd on cd.erp_campus_department_mapping_id=emp.erp_campus_department_mapping_id  AND cd.record_status = 'A' "
				+ " inner join erp_department dep on dep.erp_department_id=cd.erp_department_id  AND dep.record_status = 'A'     "
				+ " inner join erp_campus c on c.erp_campus_id=cd.erp_campus_id  AND c.record_status = 'A' " 
				+ " where  emp.record_status= 'A' and ";
			if(!Utils.isNullOrEmpty(value) && !value.equalsIgnoreCase("0")) {
				if(value.matches("-?\\d+")) {
					qry = qry + " emp.emp_id=:empId";
				}else {
					qry = qry + " emp.emp_name=:empName";
				}
			}else {
				qry = qry + " erp_users.erp_users_id =:user_id ";
			}
			String qry1 = qry;
		Tuple list = sessionFactory.withSession(s -> {
			Mutiny.Query<Tuple> query = s.createNativeQuery(qry1, Tuple.class);
			if(!Utils.isNullOrEmpty(value) && !value.equalsIgnoreCase("0")) {
				if(value.matches("-?\\d+")) {
					query.setParameter("empId", value);
				}else {
					query.setParameter("empName", value);
				}
			}else {
				query.setParameter("user_id", userId);
			}
			return query.getSingleResultOrNull();
		}).await().indefinitely();
		return list;
	
//		ApiResult<List<EmpLeaveEntryDTO>> result = new ApiResult<List<EmpLeaveEntryDTO>>();
//		DBGateway.runJPA(new ITransactional() {
//			@Override
//			public void onRun(EntityManager context) {
//				Query query =null;
//
//				if(empId.matches("-?\\d+")) {
//				 query = context.createNativeQuery(LeaveQueries.LEAVE_EMPLOYEE_LEAVE_DETAILS_NEW,Tuple.class);
//				query.setParameter("user_id", empId);
//				}else {
//					try {
//					String empId1 = Utils.isNullOrEmpty(empId) ? String.valueOf(commonApiTransaction.getEmployeesByUserId(userId)) : empId;
//					query = context.createNativeQuery(LeaveQueries.LEAVE_EMPLOYEE_LEAVE_DETAILS_NEW,Tuple.class);
//					query.setParameter("user_id", userId);
//					}catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//				List<Tuple> mappings = query.getResultList();
//				if (mappings != null && mappings.size() > 0) {
//					result.success = true;
//					result.dto = new ArrayList<>();
//					for (Tuple mapping : mappings) {
//						EmpLeaveEntryDTO mappingInfo = new EmpLeaveEntryDTO();
//						mappingInfo.employeeId = mapping.get("empNo").toString();
//						mappingInfo.name = mapping.get("name").toString();
//						mappingInfo.department =  (String) mapping.get("department");
//						mappingInfo.designation = (String) mapping.get("designation");
//						mappingInfo.campus =  (String) mapping.get("campus");
//						mappingInfo.sundayWorking =  Boolean.parseBoolean(Utils.isNullOrEmpty(mapping.get("isSundayWorking")) ? mapping.get("isSundayWorking").toString():"0");
//						mappingInfo.holidayWorking =   Boolean.parseBoolean(Utils.isNullOrEmpty(mapping.get("isHolidayWorking")) ? mapping.get("isHolidayWorking").toString():"0");
//						result.dto.add(mappingInfo);
//					}
//				}
//				result.success = true;
//			}
//			
	
	}
}