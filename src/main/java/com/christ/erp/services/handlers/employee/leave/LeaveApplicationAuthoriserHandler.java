package com.christ.erp.services.handlers.employee.leave;

import com.christ.erp.services.common.AWSS3FileStorageService;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpEmailsDBO;
import com.christ.erp.services.dbobjects.common.ErpNotificationsDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveAllocationDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveEntryDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveTypeDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.SelectColorDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveEntryDTO;
import com.christ.erp.services.handlers.common.CommonApiHandler;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.employee.leave.LeaveApplicationAuthoriserTransaction;
import com.christ.erp.services.transactions.employee.leave.LeaveApplicationTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import javax.persistence.Tuple;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class LeaveApplicationAuthoriserHandler {
    @Autowired
    LeaveApplicationAuthoriserTransaction leaveApplicationAuthoriserTransaction;
    @Autowired
    LeaveApplicationTransaction leaveApplicationTransaction;
    @Autowired
    CommonApiTransaction commonApiTransaction1;
    @Autowired
    LeaveApplicationHandler leaveApplicationHandler;
    @Autowired
    private CommonApiHandler commonApiHandler;

    @Autowired
    AWSS3FileStorageService aWSS3FileStorageService;
    public Flux<EmpLeaveEntryDTO> getEmployeeDetailsForAuthoriser(String userId) throws Exception {
        List<Integer> empWorkFlowProcessIdList=new ArrayList<Integer>();
        Integer empId = commonApiTransaction1.getEmployeesByUserId(userId);
        return leaveApplicationAuthoriserTransaction.getEmployeeDetailsForAuthoriser(empId).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
    }

    public Mono<ApiResult> leaveAuthoriserStatusUpdate(Mono<List<EmpLeaveEntryDTO>> dto, Integer userId) throws Exception {
    	Integer empId = commonApiTransaction1.getEmployeesByUserId(String.valueOf(userId));
        return dto.map(data -> convertDtoToDboForAuthoriser(data, userId,empId)).flatMap(s -> {
            leaveApplicationTransaction.updateEmpLeaveEntries(s);
            return Mono.just(Boolean.TRUE);
        }).map(Utils::responseResult);
    }

    public Flux<EmpLeaveEntryDTO> getEmployeeLeavesByAuthoriserFilterStatus(String userId,String status) throws Exception {
        Integer empId = commonApiTransaction1.getEmployeesByUserId(userId);
        return leaveApplicationAuthoriserTransaction.getEmployeeLeavesByAuthoriserFilterStatus(empId,status).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
    }

    String notificationCodeString="";
    int workFlowProcessIdValue=0;
    Map<String, ErpTemplateDBO> emailandSMSTemplates = null;
    private List<EmpLeaveEntryDBO> convertDtoToDboForAuthoriser(List<EmpLeaveEntryDTO> dto1, Integer userId,Integer empId) {
        Tuple erpWorkFlowProcessIdForAuthoriserApprove = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("LEAVE_APPLICATION_AUTHORIZER_AUTHORIZED");
        Tuple erpWorkFlowProcessIdForAuthoriserReturned = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("LEAVE_APPLICATION_AUTHORIZER_RETURNED");
        Tuple erpWorkFlowProcessIdForAuthoriserRequestDocument = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("LEAVE_APPLICATION_AUTHORIZER_REQUEST_DOCUMENT");
        Tuple erpWorkFlowProcessIdForAuthoriserClarification = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("LEAVE_APPLICATION_AUTHORIZER_REQUEST_CLARIFICATION");
        List<EmpLeaveEntryDBO> empLeaveEntryDBOData = new ArrayList<EmpLeaveEntryDBO>();
        List<Integer> leaveApplicationEntriesId = new ArrayList<Integer>();
        dto1.forEach(dto -> {
            leaveApplicationEntriesId.add(dto.id);
        });

        List<Integer> employeesIdList = leaveApplicationAuthoriserTransaction.getEmployeesListForAuthoriser(userId);
        List<Tuple> list = leaveApplicationTransaction.getEmpIdandUserId(employeesIdList);
        Map<Integer, Integer> empUserIdMap = new HashMap<Integer, Integer>();
        list.forEach(exist -> {
            empUserIdMap.put(Integer.parseInt(exist.get("empId").toString()),
                    Integer.parseInt(exist.get("userId").toString()));
        });

        List<EmpLeaveEntryDBO> empLeaveEntryDBOList = leaveApplicationTransaction.getEmpLeaveEntryDBOForUpdate(leaveApplicationEntriesId);
        Map<Integer, EmpLeaveEntryDBO> empLeaveEntriesMap = new HashMap<Integer, EmpLeaveEntryDBO>();

        Map<String, List<ErpEmailsDBO>> emailMap = new LinkedHashMap<String, List<ErpEmailsDBO>>();
        Map<String, List<ErpNotificationsDBO>> notificationMap = new LinkedHashMap<String, List<ErpNotificationsDBO>>();

        List<ErpWorkFlowProcessStatusLogDBO> statusLogList = new ArrayList<ErpWorkFlowProcessStatusLogDBO>();
        empLeaveEntryDBOList.forEach(exist -> {
            empLeaveEntriesMap.put(exist.id, exist);
        });

        List<String> templateTypes = new ArrayList<String>();
        templateTypes.add("Mail");

        List<String> templateNames = new ArrayList<String>();
        templateNames.add("LEAVE_APPLICATION_AUTHORIZER_AUTHORIZED");
        templateNames.add("LEAVE_APPLICATION_AUTHORIZER_RETURNED");
        templateNames.add("LEAVE_APPLICATION_AUTHORIZER_REQUEST_DOCUMENT");
        templateNames.add("LEAVE_APPLICATION_AUTHORIZER_REQUEST_CLARIFICATION");

        emailandSMSTemplates = new HashMap<String, ErpTemplateDBO>();
        List<ErpTemplateDBO> erpTemplateDBOList = commonApiTransaction1.getErpTemplateByTemplateCodeAndTemplateType1(templateTypes,templateNames);
        erpTemplateDBOList.forEach(erpMailandSMSTemplateDBO -> {
            emailandSMSTemplates.put( erpMailandSMSTemplateDBO.templateCode + "-" + erpMailandSMSTemplateDBO.templateType , erpMailandSMSTemplateDBO);
        });
        dto1.forEach(dto -> {
            try{
                List<ErpNotificationsDBO> notificationList = new ArrayList<ErpNotificationsDBO>();
                List<ErpEmailsDBO> emailsList = new ArrayList<ErpEmailsDBO>();
                if (empLeaveEntriesMap.containsKey(dto.id)) {
                    EmpLeaveEntryDBO dbo = empLeaveEntriesMap.get(dto.id);
                    if (!Utils.isNullOrEmpty(dto.authoriserStatus) && dto.authoriserStatus.label.equalsIgnoreCase("Authorise")) {
                        ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
                        e.id = (Integer) erpWorkFlowProcessIdForAuthoriserApprove.get("erp_work_flow_process_id");
                        dbo.erpApplicantWorkFlowProcessDBO = e;
                        dbo.erpApplicationWorkFlowProcessDBO = e;
                        dbo.authorizerLatestComment = null;
                        dbo.schedulerStatus = "Pending";
                        dbo.isPending = false;
                        EmpDBO emp = new EmpDBO();
                        emp.setId(empId);
                        dbo.empLeaveAuthorizerId = emp;
                        EmpLeaveTypeDBO empLeaveTypeDBO = leaveApplicationTransaction.getDataOnLeaveType1(dto.leaveTypecategory.id);
                        int year = leaveApplicationHandler.getYearOnInitilizeMonthofLeaveType(dto);
                        EmpLeaveAllocationDBO empLeaveAllocationDBO = leaveApplicationTransaction.getEmployeeLeaveAllocationDetails(dto, year);
                        if (empLeaveAllocationDBO != null) {
                            BigDecimal b = new BigDecimal(dto.totalDays);
                            if (empLeaveAllocationDBO.sanctionedLeaves == null)
                                empLeaveAllocationDBO.sanctionedLeaves = b;
                            else
                                empLeaveAllocationDBO.sanctionedLeaves = empLeaveAllocationDBO.sanctionedLeaves.add(b);
                            if (empLeaveTypeDBO.isLeave) {
                                if (empLeaveAllocationDBO.leavesRemaining != null)
                                    empLeaveAllocationDBO.leavesRemaining = empLeaveAllocationDBO.leavesRemaining.subtract(b);
                            }
                            empLeaveAllocationDBO.modifiedUsersId = userId;
                            if (!Utils.isNullOrEmpty(empLeaveAllocationDBO.id)) {
                                leaveApplicationTransaction.update(empLeaveAllocationDBO);
                            }
                        }
                        ErpTemplateDBO erpTemplateDBO = emailandSMSTemplates.get("LEAVE_APPLICATION_AUTHORIZER_AUTHORIZED" + "-" + "Mail");
                        statusLogList.add(leaveApplicationHandler.getErpWorkFlowProcessStatusLogDBO(dto.id, dbo, userId));
                        notificationCodeString = "LEAVE_APPLICATION_AUTHORIZER_AUTHORIZED";
                        workFlowProcessIdValue = (Integer) erpWorkFlowProcessIdForAuthoriserApprove.get("erp_work_flow_process_id");

                        String s = String.valueOf(workFlowProcessIdValue) + "-" + notificationCodeString + "-" + userId;
                        if (!emailMap.containsKey(s)) {
                            if (erpTemplateDBO != null && !Utils.isNullOrEmpty(empUserIdMap)) {
                                emailsList.add(leaveApplicationHandler.getEmailDBO(erpTemplateDBO, dbo.id,empUserIdMap.get(Integer.parseInt(dto.employeeId)), dto));
                                emailMap.put(s, emailsList);
                            }
                        } else {
                            List<ErpEmailsDBO> emails = emailMap.get(s);
                            if (erpTemplateDBO != null && !Utils.isNullOrEmpty(empUserIdMap))
                                emails.add(leaveApplicationHandler.getEmailDBO(erpTemplateDBO, dbo.id, empUserIdMap.get(Integer.parseInt(dto.employeeId)), dto));
                        }

                    } else if (!Utils.isNullOrEmpty(dto.authoriserStatus) && dto.authoriserStatus.label.equalsIgnoreCase("Return")) {
                        ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
                        e.id = (Integer) erpWorkFlowProcessIdForAuthoriserReturned.get("erp_work_flow_process_id");
                        dbo.erpApplicantWorkFlowProcessDBO = e;
                        dbo.erpApplicationWorkFlowProcessDBO = e;
                        dbo.authorizerLatestComment =dto.getAuthoriserComments();
                        dbo.isPending = false;
                        EmpDBO emp = new EmpDBO();
                        emp.setId(empId);
                        dbo.empLeaveAuthorizerId = emp;
                        ErpTemplateDBO erpTemplateDBO = emailandSMSTemplates.get("LEAVE_APPLICATION_AUTHORIZER_RETURNED" + "-" + "Mail");
                        statusLogList.add(leaveApplicationHandler.getErpWorkFlowProcessStatusLogDBO(dto.id, dbo, userId));
                        notificationCodeString = "LEAVE_APPLICATION_AUTHORIZER_RETURNED";
                        workFlowProcessIdValue = (Integer) erpWorkFlowProcessIdForAuthoriserReturned.get("erp_work_flow_process_id");
                        leaveApplicationHandler.updateEmpLeaveAllocation(String.valueOf(userId), dto, dbo);
                        String s = String.valueOf(workFlowProcessIdValue) + "-" + notificationCodeString + "-" + userId;
                        if (!emailMap.containsKey(s)) {
                            if (erpTemplateDBO != null && !Utils.isNullOrEmpty(empUserIdMap)) {
                                emailsList.add(leaveApplicationHandler.getEmailDBO(erpTemplateDBO, dbo.id, empUserIdMap.get(Integer.parseInt(dto.employeeId)), dto));
                                emailMap.put(s, emailsList);
                            }
                        } else {
                            List<ErpEmailsDBO> emails = emailMap.get(s);
                            if (erpTemplateDBO != null && !Utils.isNullOrEmpty(empUserIdMap))
                                emails.add(leaveApplicationHandler.getEmailDBO(erpTemplateDBO, dbo.id, empUserIdMap.get(Integer.parseInt(dto.employeeId)), dto));
                        }

                        if (!notificationMap.containsKey(s)) {
                            if (erpTemplateDBO != null && !Utils.isNullOrEmpty(empUserIdMap)) {
                                notificationList.add(leaveApplicationHandler.getNotificationsDBO(dbo.id, empUserIdMap.get(Integer.parseInt(dto.employeeId))));
                                notificationMap.put(s, notificationList);
                            }
                        } else {
                            List<ErpNotificationsDBO> notifications = notificationMap.get(s);
                            if (erpTemplateDBO != null && !Utils.isNullOrEmpty(empUserIdMap))
                                notifications.add(leaveApplicationHandler.getNotificationsDBO(dbo.id, empUserIdMap.get(Integer.parseInt(dto.employeeId))));
                        }

                    } else if (!Utils.isNullOrEmpty(dto.authoriserStatus) && dto.authoriserStatus.label.equalsIgnoreCase("Request Document")) {
                        ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
                        e.id = (Integer) erpWorkFlowProcessIdForAuthoriserRequestDocument.get("erp_work_flow_process_id");
                        dbo.erpApplicantWorkFlowProcessDBO = e;
                        dbo.erpApplicationWorkFlowProcessDBO = e;
                        dbo.authorizerLatestComment =dto.getAuthoriserComments();
                        EmpDBO emp = new EmpDBO();
                        emp.setId(empId);
                        dbo.empLeaveAuthorizerId = emp;
                        ErpTemplateDBO erpTemplateDBO = emailandSMSTemplates.get("LEAVE_APPLICATION_AUTHORIZER_REQUEST_DOCUMENT" + "-" + "Mail");
                        statusLogList.add(leaveApplicationHandler.getErpWorkFlowProcessStatusLogDBO(dto.id, dbo, userId));
                        notificationCodeString = "LEAVE_APPLICATION_AUTHORIZER_REQUEST_DOCUMENT";
                        workFlowProcessIdValue = (Integer) erpWorkFlowProcessIdForAuthoriserRequestDocument.get("erp_work_flow_process_id");


                        String s = String.valueOf(workFlowProcessIdValue) + "-" + notificationCodeString + "-" + userId;
                        if (!emailMap.containsKey(s)) {
                            if (erpTemplateDBO != null && !Utils.isNullOrEmpty(empUserIdMap)) {
                                emailsList.add(leaveApplicationHandler.getEmailDBO(erpTemplateDBO, dbo.id, empUserIdMap.get(Integer.parseInt(dto.employeeId)), dto));
                                emailMap.put(s, emailsList);
                            }
                        } else {
                            List<ErpEmailsDBO> emails = emailMap.get(s);
                            if (erpTemplateDBO != null && !Utils.isNullOrEmpty(empUserIdMap))
                                emails.add(leaveApplicationHandler.getEmailDBO(erpTemplateDBO, dbo.id, empUserIdMap.get(Integer.parseInt(dto.employeeId)), dto));
                        }

                        if (!notificationMap.containsKey(s)) {
                            if (erpTemplateDBO != null && !Utils.isNullOrEmpty(empUserIdMap)) {
                                notificationList.add(leaveApplicationHandler.getNotificationsDBO(dbo.id, empUserIdMap.get(Integer.parseInt(dto.employeeId))));
                                notificationMap.put(s, notificationList);
                            }
                        } else {
                            List<ErpNotificationsDBO> notifications = notificationMap.get(s);
                            if (erpTemplateDBO != null && !Utils.isNullOrEmpty(empUserIdMap))
                                notifications.add(leaveApplicationHandler.getNotificationsDBO(dbo.id, empUserIdMap.get(Integer.parseInt(dto.employeeId))));
                        }
                    }else if (!Utils.isNullOrEmpty(dto.authoriserStatus) && dto.authoriserStatus.label.equalsIgnoreCase("Clarify/Meet")) {
                        ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
                        e.id = (Integer) erpWorkFlowProcessIdForAuthoriserClarification.get("erp_work_flow_process_id");
                        dbo.erpApplicantWorkFlowProcessDBO = e;
                        dbo.erpApplicationWorkFlowProcessDBO = e;
                        dbo.authorizerLatestComment =dto.getAuthoriserComments();
                        EmpDBO emp = new EmpDBO();
                        emp.setId(empId);
                        dbo.empLeaveAuthorizerId = emp;
                        ErpTemplateDBO erpTemplateDBO = emailandSMSTemplates.get("LEAVE_APPLICATION_AUTHORIZER_REQUEST_CLARIFICATION" + "-" + "Mail");
                        statusLogList.add(leaveApplicationHandler.getErpWorkFlowProcessStatusLogDBO(dto.id, dbo, userId));
                        notificationCodeString = "LEAVE_APPLICATION_AUTHORIZER_REQUEST_CLARIFICATION";
                        workFlowProcessIdValue = (Integer) erpWorkFlowProcessIdForAuthoriserClarification.get("erp_work_flow_process_id");


                        String s = String.valueOf(workFlowProcessIdValue) + "-" + notificationCodeString + "-" + userId;
                        if (!emailMap.containsKey(s)) {
                            if (erpTemplateDBO != null && !Utils.isNullOrEmpty(empUserIdMap)) {
                                emailsList.add(leaveApplicationHandler.getEmailDBO(erpTemplateDBO, dbo.id, empUserIdMap.get(Integer.parseInt(dto.employeeId)), dto));
                                emailMap.put(s, emailsList);
                            }
                        } else {
                            List<ErpEmailsDBO> emails = emailMap.get(s);
                            if (erpTemplateDBO != null && !Utils.isNullOrEmpty(empUserIdMap))
                                emails.add(leaveApplicationHandler.getEmailDBO(erpTemplateDBO, dbo.id, empUserIdMap.get(Integer.parseInt(dto.employeeId)), dto));
                        }

                        if (!notificationMap.containsKey(s)) {
                            if (erpTemplateDBO != null && !Utils.isNullOrEmpty(empUserIdMap)) {
                                notificationList.add(leaveApplicationHandler.getNotificationsDBO(dbo.id, empUserIdMap.get(Integer.parseInt(dto.employeeId))));
                                notificationMap.put(s, notificationList);
                            }
                        } else {
                            List<ErpNotificationsDBO> notifications = notificationMap.get(s);
                            if (erpTemplateDBO != null && !Utils.isNullOrEmpty(empUserIdMap))
                                notifications.add(leaveApplicationHandler.getNotificationsDBO(dbo.id, empUserIdMap.get(Integer.parseInt(dto.employeeId))));
                        }
                    }
                    dbo.applicantStatusLogTime = LocalDate.now();
                    dbo.applicationStatusLogTime = LocalDate.now();
                    dbo.modifiedUsersId = userId;
                    empLeaveEntryDBOData.add(dbo);
                }
            }catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        for (Map.Entry<String, List<ErpEmailsDBO>> entry : emailMap.entrySet()) {
            List<ErpEmailsDBO> emailsList = entry.getValue();
            List<ErpNotificationsDBO> notificationsList = notificationMap.get(entry.getKey());
            Set<Integer> approversIdSet = new LinkedHashSet<Integer>();
            approversIdSet.add(Integer.parseInt(entry.getKey().split("-")[2]));
            commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(Integer.parseInt(entry.getKey().split("-")[0]),
                    entry.getKey().split("-")[1], approversIdSet, notificationsList, null, emailsList);
        }
        commonApiTransaction1.saveErpWorkFlowProcessStatusLogDBO1(statusLogList);
        return empLeaveEntryDBOData;
    }

    public EmpLeaveEntryDTO convertDBOToDTO (Tuple  dbo){
        EmpLeaveEntryDTO dto = new EmpLeaveEntryDTO();
        if (!Utils.isNullOrEmpty(dbo.get("ID"))) {
            dto.id= Integer.parseInt( dbo.get("ID").toString());
        }
        if (!Utils.isNullOrEmpty(dbo.get("empID"))) {
            dto.employeeId=dbo.get("empID").toString();
        }
        if (!Utils.isNullOrEmpty(dbo.get("empNo"))) {
            dto.empNo =dbo.get("empNo").toString();
        }
        if (!Utils.isNullOrEmpty(dbo.get("empName"))) {
            dto.name=dbo.get("empName").toString()+" ("+dbo.get("empNo").toString()+")";
        }
        if (!Utils.isNullOrEmpty(dbo.get("email"))) {
            dto.email=dbo.get("email").toString();
        }
        dto.leaveTypecategory = new ExModelBaseDTO();
        if (!Utils.isNullOrEmpty(dbo.get("leaveTypeID"))) {
            dto.leaveTypecategory.id = String.valueOf(dbo.get("leaveTypeID"));
        }
        if (!Utils.isNullOrEmpty(dbo.get("leaveTypeName"))) {
            dto.leaveTypecategory.tag = (String) dbo.get("leaveTypeName");
            dto.setLeaveTypeName((String) dbo.get("leaveTypeName"));
        }
        if (!Utils.isNullOrEmpty(dbo.get("reason"))) {
            dto.reason=(String) dbo.get("reason");
        }
        if (dbo.get("mode").toString().equalsIgnoreCase("1")) {
            dto.mode = "offline";
        } else {
            dto.mode = "online";
        }
        if (!Utils.isNullOrEmpty(dbo.get("totalDays"))) {
            dto.totalDays=dbo.get("totalDays").toString();
        }
        if (!Utils.isNullOrEmpty(dbo.get("startDate"))) {
            dto.startDate=Utils.convertStringDateToLocalDate(dbo.get("startDate").toString());
        }
        if (!Utils.isNullOrEmpty(dbo.get("endDate"))) {
            dto.endDate=Utils.convertStringDateToLocalDate(dbo.get("endDate").toString());
        }
        dto.fromSession = new ExModelBaseDTO();
        if (dbo.get("fromSession") != null && dbo.get("fromSession").toString().equalsIgnoreCase("FN")) {
            dto.fromSession.id = "2";
            dto.fromSession.tag = "Forenoon";
        } else if (dbo.get("fromSession") != null && dbo.get("fromSession").toString().equalsIgnoreCase("AN")) {
            dto.fromSession.id = "3";
            dto.fromSession.tag = "Afternoon";
        } else if (dbo.get("fromSession") != null && dbo.get("fromSession").toString().equalsIgnoreCase("FD")) {
            dto.fromSession.id = "1";
            dto.fromSession.tag = "Full Day";
        }
        dto.toSession = new ExModelBaseDTO();
        if (dbo.get("toSession") != null && dbo.get("toSession").toString().equalsIgnoreCase("FN")) {
            dto.toSession.id = "2";
            dto.toSession.tag = "Forenoon";
        } else if (dbo.get("toSession") != null && dbo.get("toSession").toString().equalsIgnoreCase("AN")) {
            dto.toSession.id = "3";
            dto.toSession.tag = "Afternoon";
        } else if (dbo.get("toSession") != null && dbo.get("toSession").toString().equalsIgnoreCase("FD")) {
            dto.toSession.id = "1";
            dto.toSession.tag = "Full Day";
        }
        if (!Utils.isNullOrEmpty(dbo.get("status")))
            dto.status = (String) dbo.get("status");
        if (!Utils.isNullOrEmpty(dbo.get("statusCode")))
            dto.processCode = (String) dbo.get("statusCode");
        if (!Utils.isNullOrEmpty(dbo.get("statusCode"))) {
            dto.setApproverStatus(new SelectColorDTO());
            dto.setAuthoriserStatus(new SelectColorDTO());
            switch ((String) dbo.get("statusCode")) {
                case "LEAVE_APPLICATION_AUTHORIZER_AUTHORIZED" -> {
                    dto.getAuthoriserStatus().setLabel("Authorise");
                    dto.getAuthoriserStatus().setValue("1");
                    dto.getAuthoriserStatus().setColor("#000000");
                }
                case "LEAVE_APPLICATION_AUTHORIZER_RETURNED" -> {
                    dto.getAuthoriserStatus().setLabel("Return");
                    dto.getAuthoriserStatus().setValue("2");
                    dto.getAuthoriserStatus().setColor("red");
                }
                case "LEAVE_APPLICATION_AUTHORIZER_REQUEST_DOCUMENT" -> {
                    dto.getAuthoriserStatus().setLabel("Request Document");
                    dto.getAuthoriserStatus().setValue("3");
                    dto.getAuthoriserStatus().setColor("red");
                }
                case "LEAVE_APPLICATION_AUTHORIZER_REQUEST_CLARIFICATION" -> {
                    dto.getAuthoriserStatus().setLabel("Clarify/Meet");
                    dto.getAuthoriserStatus().setValue("4");
                    dto.getAuthoriserStatus().setColor("red");
                }
                default -> {
                    dto.setAuthoriserStatus(null);
                }
            }
            switch ((String) dbo.get("statusCode")) {
                case "LEAVE_APPLICATION_APPROVER_APPROVED" -> {
                    if (!Utils.isNullOrEmpty(dbo.get("approverName")))
                        dto.setApprovedName(dbo.get("approverName").toString());
                }
                case "LEAVE_APPLICATION_FORWARDER_APPROVED" -> {
                    if (!Utils.isNullOrEmpty(dbo.get("forwarder1Name")))
                        dto.setApprovedName(dbo.get("forwarder1Name").toString());
                }
                case "LEAVE_APPLICATION_FORWARDER_2_APPROVED" -> {
                    if (!Utils.isNullOrEmpty(dbo.get("forwarder2Name")))
                        dto.setApprovedName(dbo.get("forwarder2Name").toString());
                }
                default -> {
                    dto.setApprovedName(null);
                }
            }
        }
        if (!Utils.isNullOrEmpty(dbo.get("approverComment"))) {
            dto.setApproverComments(dbo.get("approverComment").toString());
        }
        if (!Utils.isNullOrEmpty(dbo.get("authorizerComment"))) {
            dto.setAuthoriserComments(dbo.get("authorizerComment").toString());
        }
        dto.leaveDocumentUrl=(String)dbo.get("leaveDocumentUrl");
        if (!Utils.isNullOrEmpty(dbo.get("forwarded1LatestComment"))) {
            dto.setApproverForwardedEmployeeComments(dbo.get("forwarded1LatestComment").toString());
        }
        if (!Utils.isNullOrEmpty(dbo.get("forwarded2LatestComment"))) {
            dto.setApproverForwarded2EmployeeComments(dbo.get("forwarded2LatestComment").toString());
        }
        if (!Utils.isNullOrEmpty(dbo.get("forwarder1Name")) && !Utils.isNullOrEmpty(dbo.get("forwarder1Id"))) {
            SelectDTO forwarder1DTO = new SelectDTO();
            forwarder1DTO.label = (String) dbo.get("forwarder1Name");
            forwarder1DTO.value = String.valueOf(dbo.get("forwarder1Id"));
            dto.approverForwardedEmployee=forwarder1DTO;
        }
        if (!Utils.isNullOrEmpty(dbo.get("forwarder2Name")) && !Utils.isNullOrEmpty(dbo.get("forwarder2Id"))) {
            SelectDTO forwarder2DTO = new SelectDTO();
            forwarder2DTO.label = (String) dbo.get("forwarder2Name");
            forwarder2DTO.value = String.valueOf(dbo.get("forwarder2Id"));
            dto.approverForwarded2Employee=forwarder2DTO;
        }
        if(!Utils.isNullOrEmpty(dbo.get("profile_file_name_unique")) && !Utils.isNullOrEmpty(dbo.get("profile_upload_process_code")) && !Utils.isNullOrEmpty(dbo.get("profile_file_name_original"))) {
            FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
            fileUploadDownloadDTO.setActualPath(dbo.get("profile_file_name_unique").toString());
            fileUploadDownloadDTO.setProcessCode(dbo.get("profile_upload_process_code").toString());
            fileUploadDownloadDTO.setOriginalFileName(dbo.get("profile_file_name_original").toString());
            aWSS3FileStorageService.generatePresignedUrlForDownload(fileUploadDownloadDTO);
            dto.setEmployeePhotoUrl(fileUploadDownloadDTO.getPreSignedUrl());
        }
        if(!Utils.isNullOrEmpty(dbo.get("doc_file_name_unique")) && !Utils.isNullOrEmpty(dbo.get("doc_upload_process_code")) && !Utils.isNullOrEmpty(dbo.get("doc_file_name_original"))) {
            FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
            fileUploadDownloadDTO.setActualPath(dbo.get("doc_file_name_unique").toString());
            fileUploadDownloadDTO.setProcessCode(dbo.get("doc_upload_process_code").toString());
            fileUploadDownloadDTO.setOriginalFileName(dbo.get("doc_file_name_original").toString());
            dto.setFileUploadDownloadDTO(fileUploadDownloadDTO);
        }
        return dto;
    }

}
