package com.christ.erp.services.transactions.employee.leave;

import com.christ.erp.services.common.Utils;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LeaveApplicationApproverTransaction {
    @Autowired
    private Mutiny.SessionFactory sessionFactory;
    public Mono<List<Tuple>> getEmployeeDetailsForApprover(Integer employeeId) {
        String str ="SELECT e.emp_leave_entry_id AS ID, "
                + "emp.emp_id AS empID, "
                + "emp.emp_no AS empNo, "
                + "emp.emp_name AS empName, "
                + "e1.leave_type_name AS leaveTypeName, "
                + "e1.emp_leave_type_id AS leaveTypeID, "
                + "e.number_of_days_leave AS totalDays, "
                + "e.leave_start_date AS startDate, "
                + "e.leave_end_date AS endDate, "
                + "e.leave_start_session AS fromSession, "
                + "e.leave_end_session AS toSession, "
                + "e.leave_reason AS reason, "
                + "e.is_offline AS mode, "
                + "e.leave_document_url AS leaveDocumentUrl, "
                + "emp.emp_personal_email AS email, "
                + "p.applicant_status_display_text AS status, "
                + "p.process_code AS statusCode, "
                + "e.approver_latest_comment AS approverComment, "
                + "e.authorizer_latest_comment AS authorizerComment, "
                + "e.forwarded_1_latest_comment AS forwarded1LatestComment, "
                + "e.forwarded_2_latest_comment AS forwarded2LatestComment, "
                + "e.emp_leave_forwarded_1_id AS forwarder1Id, "
                + "e.emp_leave_forwarded_2_id AS forwarder2Id, "
                + "f1emp.emp_name AS forwarder1Name, "
                + "f2emp.emp_name AS forwarder2Name, "
                + "ual.file_name_unique AS doc_file_name_unique, "
                + "ual.file_name_original AS doc_file_name_original, "
                + "folder.upload_process_code AS doc_upload_process_code, "
                + "p_ual.file_name_unique AS profile_file_name_unique, "
                + "p_ual.file_name_original AS profile_file_name_original, "
                + "p_folder.upload_process_code AS profile_upload_process_code, "
                + "case when(emp_approvers.leave_approver_id=:employeeId) then emp_approvers.leave_approver_id else 0 end as approverId, "
                + "case when(f1emp.emp_id=:employeeId) then f1emp.emp_id else 0 end as forwarderId, "
                + "case when(f2emp.emp_id=:employeeId) then f2emp.emp_id else 0 end as forwarderForwardedId "
                + "from emp_leave_entry e "
                + "inner join emp_leave_type e1 on e1.emp_leave_type_id=e.emp_leave_type_id and e1.record_status='A' "
                + "inner join emp ON emp.emp_id = e.emp_id and emp.record_status='A' "
                + "inner join erp_work_flow_process p ON p.erp_work_flow_process_id = e.erp_application_work_flow_process_id and p.record_status='A' "
                + "inner join emp_approvers on emp_approvers.emp_id=e.emp_id and emp_approvers.record_status='A' "
                + "inner join emp_personal_data on emp_personal_data.emp_personal_data_id = emp.emp_personal_data_id "
                + "left join emp as f1emp on f1emp.emp_id = e.emp_leave_forwarded_1_id "
                + "left join emp as f2emp on f2emp.emp_id = e.emp_leave_forwarded_2_id "
                + "left join url_access_link as ual on ual.url_access_link_id = e.leave_document_url_id "
                + "left join url_folder_list as folder on ual.url_folder_list_id = folder.url_folder_list_id "
                + "left join url_access_link as p_ual on p_ual.url_access_link_id = emp_personal_data.profile_photo_url_id "
                + "left join url_folder_list as p_folder on p_ual.url_folder_list_id = p_folder.url_folder_list_id "
                + "where e.is_offline='0' and e.record_status='A' "
                + "and (( p.process_code ='LEAVE_APPLICATION_SUBMISSION' and  emp_approvers.leave_approver_id =:employeeId) "
                + "or (p.process_code='LEAVE_APPLICATION_APPROVER_FORWARDED' and  e.emp_leave_forwarded_1_id =:employeeId) "
                + "or (p.process_code ='LEAVE_APPLICATION_FORWARDER_FORWARDED' and  e.emp_leave_forwarded_2_id =:employeeId) "
                + "or (p.process_code ='LEAVE_APPLICATION_FORWARDER_REVERTED' and  emp_approvers.leave_approver_id =:employeeId) "
                + "or ( p.process_code ='LEAVE_APPLICATION_FORWARDER_2_REVERTED' and  e.emp_leave_forwarded_1_id =:employeeId)) ";
        Mono<List<Tuple>> list = Mono.fromFuture(sessionFactory.withSession(s -> {
            Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
            query.setParameter("employeeId", employeeId);
            return query.getResultList();
        }).subscribeAsCompletionStage());
        return list;
    }

    public List<Integer> getEmployeesListForApprovers(int userId) {
        List<Integer> empList = new ArrayList<Integer>();
        String str = " select emp_approvers.emp_id as empId from emp_approvers "
                + " inner join erp_users ON erp_users.emp_id=emp_approvers.leave_approver_id "
                + " where erp_users.erp_users_id=:userId and emp_approvers.record_status='A' and erp_users.record_status='A'";
        List<Tuple> empId = sessionFactory.withSession(s -> s.createNativeQuery(str, Tuple.class).setParameter("userId", userId).getResultList()).await().indefinitely();
        for (Tuple mapping : empId) {
            empList.add(Integer.parseInt(mapping.get("empID").toString()));
        }
        return empList;
    }

    public Mono<List<Tuple>> getEmployeeLeavesByApproverFilterStatus(Integer employeeId,String status) {
        String str = "";
        str = "SELECT e.emp_leave_entry_id AS ID, "
                + "emp.emp_id AS empID, "
                + "emp.emp_no AS empNo, "
                + "emp.emp_name AS empName, "
                + "e1.leave_type_name AS leaveTypeName, "
                + "e1.emp_leave_type_id AS leaveTypeID, "
                + "e.number_of_days_leave AS totalDays, "
                + "e.leave_start_date AS startDate, "
                + "e.leave_end_date AS endDate, "
                + "e.leave_start_session AS fromSession, "
                + "e.leave_end_session AS toSession, "
                + "e.leave_reason AS reason, "
                + "e.is_offline AS mode, "
                + "e.leave_document_url AS leaveDocumentUrl, "
                + "emp.emp_personal_email AS email, "
                + "p.applicant_status_display_text AS status, "
                + "p.process_code AS statusCode, "
                + "e.approver_latest_comment AS approverComment, "
                + "e.authorizer_latest_comment AS authorizerComment, "
                + "e.forwarded_1_latest_comment AS forwarded1LatestComment, "
                + "e.forwarded_2_latest_comment AS forwarded2LatestComment, "
                + "e.emp_leave_forwarded_1_id AS forwarder1Id, "
                + "e.emp_leave_forwarded_2_id AS forwarder2Id, "
                + "f1emp.emp_name AS forwarder1Name, "
                + "f2emp.emp_name AS forwarder2Name, "
                + "ual.file_name_unique AS doc_file_name_unique, "
                + "ual.file_name_original AS doc_file_name_original, "
                + "folder.upload_process_code AS doc_upload_process_code, "
                + "p_ual.file_name_unique AS profile_file_name_unique, "
                + "p_ual.file_name_original AS profile_file_name_original, "
                + "p_folder.upload_process_code AS profile_upload_process_code, "
                + "case when(emp_approvers.leave_approver_id=:employeeId) then emp_approvers.leave_approver_id else 0 end as approverId, "
                + "case when(f1emp.emp_id=:employeeId) then f1emp.emp_id else 0 end as forwarderId, "
                + "case when(f2emp.emp_id=:employeeId) then f2emp.emp_id else 0 end as forwarderForwardedId "
                + " from emp_leave_entry e "
                + "inner join emp_leave_type e1 on e1.emp_leave_type_id=e.emp_leave_type_id and e1.record_status='A' "
                + "inner join emp ON emp.emp_id = e.emp_id and emp.record_status='A' "
                + "inner join erp_work_flow_process p ON p.erp_work_flow_process_id = e.erp_application_work_flow_process_id and p.record_status='A' "
                + "inner join emp_approvers on emp_approvers.emp_id=e.emp_id and emp_approvers.record_status='A' "
                + "left join emp as f1emp on f1emp.emp_id = e.emp_leave_forwarded_1_id "
                + "left join emp as f2emp on f2emp.emp_id = e.emp_leave_forwarded_2_id "
                + "inner join emp_personal_data on emp_personal_data.emp_personal_data_id = emp.emp_personal_data_id "
                + "left join url_access_link as ual on ual.url_access_link_id = e.leave_document_url_id "
                + "left join url_folder_list as folder on ual.url_folder_list_id = folder.url_folder_list_id "
                + "left join url_access_link as p_ual on p_ual.url_access_link_id = emp_personal_data.profile_photo_url_id "
                + "left join url_folder_list as p_folder on p_ual.url_folder_list_id = p_folder.url_folder_list_id "
                + "where e.is_offline='0' and e.record_status='A' and ";
        if(status.equalsIgnoreCase("Pending")){
            str = str + " (( p.process_code ='LEAVE_APPLICATION_SUBMISSION' and  emp_approvers.leave_approver_id =:employeeId) "
                    + "or (p.process_code='LEAVE_APPLICATION_APPROVER_FORWARDED' and  e.emp_leave_forwarded_1_id =:employeeId) "
                    + "or (p.process_code ='LEAVE_APPLICATION_FORWARDER_FORWARDED' and  e.emp_leave_forwarded_2_id =:employeeId) "
                    + "or (p.process_code ='LEAVE_APPLICATION_FORWARDER_REVERTED' and  emp_approvers.leave_approver_id =:employeeId) "
                    + "or (p.process_code ='LEAVE_APPLICATION_FORWARDER_2_REVERTED' and  e.emp_leave_forwarded_1_id =:employeeId)) ";
        }
        else if(status.equalsIgnoreCase("Approved Leaves")){
            str = str + "((p.process_code ='LEAVE_APPLICATION_APPROVER_APPROVED' and  emp_approvers.leave_approver_id =:employeeId) "
                    + "or (p.process_code='LEAVE_APPLICATION_FORWARDER_APPROVED' and  e.emp_leave_forwarded_1_id =:employeeId) "
                    + "or (p.process_code ='LEAVE_APPLICATION_FORWARDER_2_APPROVED' and  e.emp_leave_forwarded_2_id =:employeeId)) ";
        }
        else if(status.equalsIgnoreCase("Forward Leaves")){
            str = str + "((p.process_code ='LEAVE_APPLICATION_APPROVER_FORWARDED' and  emp_approvers.leave_approver_id =:employeeId) "
                    + "or (p.process_code='LEAVE_APPLICATION_FORWARDER_FORWARDED' and  e.emp_leave_forwarded_1_id =:employeeId)) ";
        }
        else if(status.equalsIgnoreCase("Return Leaves")){
            str = str + "((p.process_code ='LEAVE_APPLICATION_APPROVER_RETURNED' and  emp_approvers.leave_approver_id =:employeeId) "
                    + "or (p.process_code='LEAVE_APPLICATION_FORWARDER_RETURNED' and  e.emp_leave_forwarded_1_id =:employeeId) "
                    + "or (p.process_code ='LEAVE_APPLICATION_FORWARDER_2_RETURNED' and  e.emp_leave_forwarded_2_id =:employeeId)) ";
        }
        else if(status.equalsIgnoreCase("Clarify/Meet")){
            str = str + "((p.process_code ='LEAVE_APPLICATION_APPROVER_REQUEST_CLARIFICATION' and  emp_approvers.leave_approver_id =:employeeId) "
                    + "or (p.process_code='LEAVE_APPLICATION_FORWARDER_REQUEST_CLARIFICATION' and  e.emp_leave_forwarded_1_id =:employeeId) "
                    + "or (p.process_code ='LEAVE_APPLICATION_FORWARDER_2_REQUEST_CLARIFICATION' and  e.emp_leave_forwarded_2_id =:employeeId)) ";
        }
        else if(status.equalsIgnoreCase("Reverted Leaves")){
            str = str + "((p.process_code='LEAVE_APPLICATION_FORWARDER_REVERTED' and  e.emp_leave_forwarded_1_id =:employeeId) "
                    + "or (p.process_code ='LEAVE_APPLICATION_FORWARDER_2_REVERTED' and  e.emp_leave_forwarded_2_id =:employeeId)) ";
        }
        String finalstr = str;
        Mono<List<Tuple>> list = Mono.fromFuture(sessionFactory.withSession(s -> {
            Mutiny.Query<Tuple> query = s.createNativeQuery(finalstr, Tuple.class);
            query.setParameter("employeeId", employeeId);
            return query.getResultList();
        }).subscribeAsCompletionStage());
        return list;
    }

    public Mono<List<Tuple>> getEmployeeLeavesOnSameDay(String employeeId,String startDate,String endDate) {
        String str = "SELECT e.emp_leave_entry_id AS ID, "
                + "emp.emp_id AS empID, "
                + "emp.emp_no AS empNo, "
                + "emp.emp_name AS empName, "
                + "e1.leave_type_name AS leaveTypeName, "
                + "e1.emp_leave_type_id AS leaveTypeID, "
                + "e.number_of_days_leave AS totalDays, "
                + "e.leave_start_date AS startDate, "
                + "e.leave_end_date AS endDate, "
                + "e.leave_start_session AS fromSession, "
                + "e.leave_end_session AS toSession, "
                + "e.leave_reason AS reason, "
                + "e.is_offline AS mode, "
                + "e.leave_document_url AS leaveDocumentUrl, "
                + "emp.emp_personal_email AS email, "
                + "p.applicant_status_display_text AS status, "
                + "p.process_code AS statusCode, "
                + "e.approver_latest_comment AS approverComment, "
                + "e.authorizer_latest_comment AS authorizerComment, "
                + "e.forwarded_1_latest_comment AS forwarded1LatestComment, "
                + "e.forwarded_2_latest_comment AS forwarded2LatestComment, "
                + "e.emp_leave_forwarded_1_id AS forwarder1Id, "
                + "e.emp_leave_forwarded_2_id AS forwarder2Id, "
                + "f1emp.emp_name AS forwarder1Name, "
                + "f2emp.emp_name AS forwarder2Name, "
                + "ual.file_name_unique AS doc_file_name_unique, "
                + "ual.file_name_original AS doc_file_name_original, "
                + "folder.upload_process_code AS doc_upload_process_code, "
                + "p_ual.file_name_unique AS profile_file_name_unique, "
                + "p_ual.file_name_original AS profile_file_name_original, "
                + "p_folder.upload_process_code AS profile_upload_process_code, "
                + "e.emp_leave_approver_id as approverId, "
                + "e.emp_leave_forwarded_1_id as forwarderId, "
                + "e.emp_leave_forwarded_2_id as forwarderForwardedId "
                + "from emp_leave_entry_details "
                + "left join emp_leave_entry e  ON e.emp_leave_entry_id = emp_leave_entry_details.emp_leave_entry_id and e.record_status='A' "
                + "left join emp ON emp.emp_id = e.emp_id and emp.record_status='A' "
                + "left join emp_leave_type e1 on e1.emp_leave_type_id = e.emp_leave_type_id and e.record_status='A' "
                + "left join emp as emp_appr on emp_appr.erp_campus_department_mapping_id=emp.erp_campus_department_mapping_id "
                + "left join emp as f1emp on f1emp.emp_id = e.emp_leave_forwarded_1_id "
                + "left join emp as f2emp on f2emp.emp_id = e.emp_leave_forwarded_2_id "
                + "inner join erp_work_flow_process p ON p.erp_work_flow_process_id = e.erp_application_work_flow_process_id and p.record_status='A' "
                + "inner join emp_personal_data ON emp_personal_data.emp_personal_data_id = emp.emp_personal_data_id "
                + "left join url_access_link as ual on ual.url_access_link_id = e.leave_document_url_id "
                + "left join url_folder_list as folder on ual.url_folder_list_id = folder.url_folder_list_id "
                + "left join url_access_link as p_ual on p_ual.url_access_link_id = emp_personal_data.profile_photo_url_id "
                + "left join url_folder_list as p_folder on p_ual.url_folder_list_id = p_folder.url_folder_list_id "
                + "where emp_leave_entry_details.record_status = 'A' "
                + "and emp_appr.emp_id=:employeeId and leave_date between :startDate and :endDate group by emp_leave_entry_details.emp_leave_entry_id";
        Mono<List<Tuple>> list = Mono.fromFuture(sessionFactory.withSession(s -> {
            Mutiny.Query<Tuple> query = s.createNativeQuery(str, Tuple.class);
            query.setParameter("employeeId", employeeId);
            query.setParameter("startDate", Utils.convertStringDateToLocalDate(startDate.trim()));
            query.setParameter("endDate", Utils.convertStringDateToLocalDate(endDate.trim()));
            return query.getResultList();
        }).subscribeAsCompletionStage());
        return list;
    }
}
