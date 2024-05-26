package com.christ.erp.services.handlers.employee.leave;

import com.christ.erp.services.common.AWSS3FileStorageService;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpEmailsDBO;
import com.christ.erp.services.dbobjects.common.ErpNotificationsDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveEntryDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.common.SelectColorDTO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveEntryDTO;
import com.christ.erp.services.handlers.common.CommonApiHandler;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.employee.leave.LeaveApplicationApproverTransaction;
import com.christ.erp.services.transactions.employee.leave.LeaveApplicationTransaction;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import javax.persistence.Tuple;
import java.time.LocalDate;
import java.util.*;

@Service
public class LeaveApplicationApproverHandler {
    @Autowired
    LeaveApplicationApproverTransaction leaveApplicationApproverTransaction;
    @Autowired
    LeaveApplicationTransaction leaveApplicationTransaction;
    CommonApiTransaction commonApiTransaction = CommonApiTransaction.getInstance();

    @Autowired
    CommonApiTransaction commonApiTransaction1;

    @Autowired
    private CommonApiHandler commonApiHandler;

    @Autowired
    LeaveApplicationHandler leaveApplicationHandler;

    @Autowired
    AWSS3FileStorageService aWSS3FileStorageService;

    public Flux<EmpLeaveEntryDTO> getEmployeeDetailsForApprover(String userId) throws Exception {
        Integer empId = commonApiTransaction1.getEmployeesByUserId(userId);
        return leaveApplicationApproverTransaction.getEmployeeDetailsForApprover(empId).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
    }

    public EmpLeaveEntryDTO convertDBOToDTO (Tuple dbo){
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
                case "LEAVE_APPLICATION_APPROVER_APPROVED" -> {
                    dto.getApproverStatus().setLabel("Approve");
                    dto.getApproverStatus().setValue("1");
                    dto.getApproverStatus().setColor("#000000");
                }
                case "LEAVE_APPLICATION_APPROVER_FORWARDED" -> {
                    dto.getApproverStatus().setLabel("Forward");
                    dto.getApproverStatus().setValue("2");
                    dto.getApproverStatus().setColor("blue");
                }
                case "LEAVE_APPLICATION_APPROVER_RETURNED" ->  {
                    dto.getApproverStatus().setLabel("Return");
                    dto.getApproverStatus().setValue("3");
                    dto.getApproverStatus().setColor("red");
                }
                case "LEAVE_APPLICATION_APPROVER_REQUEST_CLARIFICATION" -> {
                    dto.getApproverStatus().setLabel("Clarify/Meet");
                    dto.getApproverStatus().setValue("4");
                    dto.getApproverStatus().setColor("red");
                }
                case "LEAVE_APPLICATION_FORWARDER_REVERTED" -> {
                    dto.getApproverStatus().setLabel("Revert");
                    dto.getApproverStatus().setValue("5");
                    dto.getApproverStatus().setColor("red");
                }
                case "LEAVE_APPLICATION_FORWARDER_FORWARDED" -> {
                    dto.getApproverStatus().setLabel("Forward");
                    dto.getApproverStatus().setValue("6");
                    dto.getApproverStatus().setColor("blue");
                }
                case "LEAVE_APPLICATION_FORWARDER_RETURNED" ->  {
                    dto.getApproverStatus().setLabel("Return");
                    dto.getApproverStatus().setValue("7");
                    dto.getApproverStatus().setColor("red");
                }
                case "LEAVE_APPLICATION_FORWARDER_2_RETURNED" ->  {
                    dto.getApproverStatus().setLabel("Return");
                    dto.getApproverStatus().setValue("8");
                    dto.getApproverStatus().setColor("red");
                }
                case "LEAVE_APPLICATION_FORWARDER_REQUEST_CLARIFICATION" -> {
                    dto.getApproverStatus().setLabel("Clarify/Meet");
                    dto.getApproverStatus().setValue("9");
                    dto.getApproverStatus().setColor("red");
                }
                case "LEAVE_APPLICATION_FORWARDER_2_REQUEST_CLARIFICATION" -> {
                    dto.getApproverStatus().setLabel("Clarify/Meet");
                    dto.getApproverStatus().setValue("10");
                    dto.getApproverStatus().setColor("red");
                }
                case "LEAVE_APPLICATION_FORWARDER_2_REVERTED" -> {
                    dto.getApproverStatus().setLabel("Revert");
                    dto.getApproverStatus().setValue("11");
                    dto.getApproverStatus().setColor("red");
                }
                case "LEAVE_APPLICATION_FORWARDER_APPROVED" -> {
                    dto.getApproverStatus().setLabel("Approve");
                    dto.getApproverStatus().setValue("2");
                    dto.getApproverStatus().setColor("#000000");
                }
                case "LEAVE_APPLICATION_FORWARDER_2_APPROVED" -> {
                    dto.getApproverStatus().setLabel("Approve");
                    dto.getApproverStatus().setValue("3");
                    dto.getApproverStatus().setColor("#000000");
                }
                default -> {
                    dto.setAuthoriserStatus(null);
                    dto.setApproverStatus(null);
                }
            }
        }
        if (!Utils.isNullOrEmpty(dbo.get("approverComment"))) {
            dto.setApproverComments(dbo.get("approverComment").toString());
        }
        if (!Utils.isNullOrEmpty(dbo.get("authorizerComment"))) {
            dto.setAuthoriserComments(dbo.get("authorizerComment").toString());
        }
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
        if(!Utils.isNullOrEmpty(dbo.get("approverId"))){
            if(Integer.parseInt(dbo.get("approverId").toString()) > 0)
                dto.approverId = dbo.get("approverId").toString();
        }
        if(!Utils.isNullOrEmpty(dbo.get("forwarderId"))){
            if(Integer.parseInt(dbo.get("forwarderId").toString()) > 0)
                dto.setForwarderId(dbo.get("forwarderId").toString());
        }
        if(!Utils.isNullOrEmpty(dbo.get("forwarderForwardedId"))){
            if(Integer.parseInt(dbo.get("forwarderForwardedId").toString()) > 0)
                dto.setForwarderForwardedId(dbo.get("forwarderForwardedId").toString());
        }
        return dto;
    }

    public Mono<ApiResult> leaveApproverStatusUpdate(Mono<List<EmpLeaveEntryDTO>> dto, Integer userId) {
        return dto.map(data -> convertDtoToDbo(data, userId)).flatMap(s -> {
            leaveApplicationTransaction.updateEmpLeaveEntries(s);
            return Mono.just(Boolean.TRUE);
        }).map(Utils::responseResult);
    }

    String notificationCodeString="";
    int workFlowProcessIdValue=0;
    Map<String, ErpTemplateDBO> emailandSMSTemplates = null;
    @NotNull
    private List<EmpLeaveEntryDBO> convertDtoToDbo(List<EmpLeaveEntryDTO> dto1, Integer userId) {
        Tuple approversEmploeeId = commonApiTransaction1.getApproversEmployeeIdByUserId((userId));
        Tuple erpWorkFlowProcessIdForApproverApprove = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("LEAVE_APPLICATION_APPROVER_APPROVED");
        Tuple erpWorkFlowProcessIdForApproverForwarded = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("LEAVE_APPLICATION_APPROVER_FORWARDED");
        Tuple erpWorkFlowProcessIdForApproverReturned = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("LEAVE_APPLICATION_APPROVER_RETURNED");
        Tuple erpWorkFlowProcessIdForApproverClarification = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("LEAVE_APPLICATION_APPROVER_REQUEST_CLARIFICATION");
        Tuple erpWorkFlowProcessIdForForwarderApproved = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("LEAVE_APPLICATION_FORWARDER_APPROVED");
        Tuple erpWorkFlowProcessIdForForwarderForwarded = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("LEAVE_APPLICATION_FORWARDER_FORWARDED");
        Tuple erpWorkFlowProcessIdForForwarderReturned = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("LEAVE_APPLICATION_FORWARDER_RETURNED");
        Tuple erpWorkFlowProcessIdForForwarderClarification= commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("LEAVE_APPLICATION_FORWARDER_REQUEST_CLARIFICATION");
        Tuple erpWorkFlowProcessIdForForwarderReverted = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("LEAVE_APPLICATION_FORWARDER_REVERTED");
        Tuple erpWorkFlowProcessIdForForwarder2Approved = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("LEAVE_APPLICATION_FORWARDER_2_APPROVED");
        Tuple erpWorkFlowProcessIdForForwarder2Returned = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("LEAVE_APPLICATION_FORWARDER_2_RETURNED");
        Tuple erpWorkFlowProcessIdForForwarder2Clarification = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("LEAVE_APPLICATION_FORWARDER_2_REQUEST_CLARIFICATION");
        Tuple erpWorkFlowProcessIdForForwarder2Reverted = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("LEAVE_APPLICATION_FORWARDER_2_REVERTED");

        List<Integer> leaveApplicationEntriesId = new ArrayList<Integer>();
        dto1.forEach(dto -> {
            leaveApplicationEntriesId.add(dto.id);
        });
        List<EmpLeaveEntryDBO> empLeaveEntryDBOList = leaveApplicationTransaction.getEmpLeaveEntryDBOForUpdate(leaveApplicationEntriesId);
        Map<Integer, EmpLeaveEntryDBO> empLeaveEntriesMap = new HashMap<Integer, EmpLeaveEntryDBO>();
        empLeaveEntryDBOList.forEach(exist -> {
            empLeaveEntriesMap.put(exist.id, exist);
        });


        List<String> templateTypes = new ArrayList<String>();
        templateTypes.add("Mail");
        templateTypes.add("SMS");
        List<String> templateNames = new ArrayList<String>();
        templateNames.add("LEAVE_APPLICATION_APPROVER_APPROVED");
        templateNames.add("LEAVE_APPLICATION_APPROVER_FORWARDED");
        templateNames.add("LEAVE_APPLICATION_APPROVER_RETURNED");
        templateNames.add("LEAVE_APPLICATION_APPROVER_REQUEST_CLARIFICATION");
        templateNames.add("LEAVE_APPLICATION_FORWARDER_APPROVED");
        templateNames.add("LEAVE_APPLICATION_FORWARDER_FORWARDED");
        templateNames.add("LEAVE_APPLICATION_FORWARDER_RETURNED");
        templateNames.add("LEAVE_APPLICATION_FORWARDER_REQUEST_CLARIFICATION");
        templateNames.add("LEAVE_APPLICATION_FORWARDER_REVERTED");
        templateNames.add("LEAVE_APPLICATION_FORWARDER_2_APPROVED");
        templateNames.add("LEAVE_APPLICATION_FORWARDER_2_RETURNED");
        templateNames.add("LEAVE_APPLICATION_FORWARDER_2_REQUEST_CLARIFICATION");
        templateNames.add("LEAVE_APPLICATION_FORWARDER_2_REVERTED");


        emailandSMSTemplates = new HashMap<String, ErpTemplateDBO>();
        List<ErpTemplateDBO> erpTemplateDBOList = commonApiTransaction1.getErpTemplateByTemplateCodeAndTemplateType1(templateTypes,templateNames);
        if(!Utils.isNullOrEmpty(erpTemplateDBOList)) {
            erpTemplateDBOList.forEach(erpMailandSMSTemplateDBO -> {
                emailandSMSTemplates.put( erpMailandSMSTemplateDBO.templateCode + "-" + erpMailandSMSTemplateDBO.templateType , erpMailandSMSTemplateDBO);
            });
        }

        List<EmpLeaveEntryDBO> empLeaveEntryDBOData = new ArrayList<EmpLeaveEntryDBO>();
        Map<String, List<ErpEmailsDBO>> emailMap = new LinkedHashMap<String, List<ErpEmailsDBO>>();
        Map<String, List<ErpNotificationsDBO>> notificationMap = new LinkedHashMap<String, List<ErpNotificationsDBO>>();
        List<ErpWorkFlowProcessStatusLogDBO> statusLogList = new ArrayList<ErpWorkFlowProcessStatusLogDBO>();
        dto1.forEach(dto -> {
            try{
                List<ErpNotificationsDBO> notificationList = new ArrayList<ErpNotificationsDBO>();
                List<ErpEmailsDBO> emailList = new ArrayList<ErpEmailsDBO>();
                if (empLeaveEntriesMap.containsKey(dto.id)) {
                    EmpLeaveEntryDBO dbo = empLeaveEntriesMap.get(dto.id);
                    Tuple empUserId = null;
                    empUserId = commonApiTransaction1.getUserIdByEmployeeId(dto.employeeId);
                    if (!Utils.isNullOrEmpty(dto.approverStatus) && dto.approverStatus.label.equalsIgnoreCase("Approve") && !Utils.isNullOrEmpty(dto.processCode)) {
                        ErpTemplateDBO erpTemplateDBO = new ErpTemplateDBO();
                        if ((dbo.erpApplicationWorkFlowProcessDBO.processCode.equalsIgnoreCase("LEAVE_APPLICATION_SUBMISSION") || dbo.erpApplicationWorkFlowProcessDBO.processCode.equalsIgnoreCase("LEAVE_APPLICATION_FORWARDER_REVERTED")) && dto.processCode.equalsIgnoreCase("LEAVE_APPLICATION_APPROVER_APPROVED")) {
                            ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
                            if (!Utils.isNullOrEmpty(erpWorkFlowProcessIdForApproverApprove.get("erp_work_flow_process_id"))) {
                                e.id = (Integer) erpWorkFlowProcessIdForApproverApprove.get("erp_work_flow_process_id");
                            }
                            dbo.erpApplicantWorkFlowProcessDBO = e;
                            dbo.erpApplicationWorkFlowProcessDBO = e;
                            dbo.approverComments = null;
                            erpTemplateDBO = emailandSMSTemplates.get("LEAVE_APPLICATION_APPROVER_APPROVED" + "-" + "Mail");
                            notificationCodeString = "LEAVE_APPLICATION_APPROVER_APPROVED";
                            if (!Utils.isNullOrEmpty(erpWorkFlowProcessIdForApproverApprove.get("erp_work_flow_process_id"))) {
                                workFlowProcessIdValue = (Integer) erpWorkFlowProcessIdForApproverApprove.get("erp_work_flow_process_id");
                            }
                        } else if ((dbo.erpApplicationWorkFlowProcessDBO.processCode.equalsIgnoreCase("LEAVE_APPLICATION_APPROVER_FORWARDED")
                                || dbo.erpApplicationWorkFlowProcessDBO.processCode.equalsIgnoreCase("LEAVE_APPLICATION_FORWARDER_2_REVERTED"))
                                && dto.processCode.equalsIgnoreCase("LEAVE_APPLICATION_FORWARDER_APPROVED")) {
                            ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
                            if (!Utils.isNullOrEmpty(erpWorkFlowProcessIdForForwarderApproved.get("erp_work_flow_process_id"))) {
                                e.id = (Integer) erpWorkFlowProcessIdForForwarderApproved.get("erp_work_flow_process_id");
                            }
                            dbo.erpApplicantWorkFlowProcessDBO = e;
                            dbo.erpApplicationWorkFlowProcessDBO = e;
                            dbo.approverComments = null;
                            erpTemplateDBO = emailandSMSTemplates.get("LEAVE_APPLICATION_FORWARDER_APPROVED" + "-" + "Mail");
                            notificationCodeString = "LEAVE_APPLICATION_FORWARDER_APPROVED";
                            if (!Utils.isNullOrEmpty(erpWorkFlowProcessIdForForwarderApproved.get("erp_work_flow_process_id"))) {
                                workFlowProcessIdValue = (Integer) erpWorkFlowProcessIdForForwarderApproved.get("erp_work_flow_process_id");
                            }
                        } else if (dbo.erpApplicationWorkFlowProcessDBO.processCode.equalsIgnoreCase("LEAVE_APPLICATION_FORWARDER_FORWARDED") && dto.processCode.equalsIgnoreCase("LEAVE_APPLICATION_FORWARDER_2_APPROVED")) {
                            ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
                            if (!Utils.isNullOrEmpty(erpWorkFlowProcessIdForForwarder2Approved.get("erp_work_flow_process_id"))) {
                                e.id = (Integer) erpWorkFlowProcessIdForForwarder2Approved.get("erp_work_flow_process_id");
                            }
                            dbo.erpApplicantWorkFlowProcessDBO = e;
                            dbo.erpApplicationWorkFlowProcessDBO = e;
                            dbo.approverComments = null;
                            erpTemplateDBO = emailandSMSTemplates.get("LEAVE_APPLICATION_FORWARDER_2_APPROVED" + "-" + "Mail");
                            notificationCodeString = "LEAVE_APPLICATION_FORWARDER_2_APPROVED";
                            if (!Utils.isNullOrEmpty(erpWorkFlowProcessIdForForwarder2Approved.get("erp_work_flow_process_id"))) {
                                workFlowProcessIdValue = (Integer) erpWorkFlowProcessIdForForwarder2Approved.get("erp_work_flow_process_id");
                            }
                        }
                        Tuple authoriserEmployeeId = commonApiTransaction1.getAuthoriserUserIdByEmployeeId(dbo.empID.id);
                        statusLogList.add(leaveApplicationHandler.getErpWorkFlowProcessStatusLogDBO(dto.id, dbo, userId));
                        if (!Utils.isNullOrEmpty(authoriserEmployeeId)) {
                            String s = String.valueOf(workFlowProcessIdValue) + "-" + notificationCodeString + "-" + authoriserEmployeeId.get("usersId").toString();
                            if (!emailMap.containsKey(s)) {
                                if (erpTemplateDBO != null && !Utils.isNullOrEmpty(empUserId)) {
                                    emailList.add(leaveApplicationHandler.getEmailDBO(erpTemplateDBO, dbo.id, Integer.parseInt(authoriserEmployeeId.get("usersId").toString()), dto));
                                    emailList.add(leaveApplicationHandler.getEmailDBO(erpTemplateDBO, dbo.id, Integer.parseInt(empUserId.get("userId").toString()), dto));
                                    emailMap.put(s, emailList);
                                }
                            } else {
                                List<ErpEmailsDBO> emails = emailMap.get(s);
                                if (erpTemplateDBO != null && !Utils.isNullOrEmpty(empUserId))
                                    emails.add(leaveApplicationHandler.getEmailDBO(erpTemplateDBO, dbo.id, Integer.parseInt(empUserId.get("userId").toString()), dto));
                            }

                            if (!notificationMap.containsKey(s)) {
                                notificationList.add(leaveApplicationHandler.getNotificationsDBO(dbo.id, Integer.parseInt(authoriserEmployeeId.get("usersId").toString())));
                                notificationMap.put(s, notificationList);
                            } else {
                                List<ErpNotificationsDBO> notifications = notificationMap.get(s);
                                notifications.add(leaveApplicationHandler.getNotificationsDBO(dbo.id, Integer.parseInt(authoriserEmployeeId.get("usersId").toString())));
                            }
                        }
                        if (!Utils.isNullOrEmpty(approversEmploeeId)) {
                            EmpDBO emp = new EmpDBO();
                            if (!Utils.isNullOrEmpty(approversEmploeeId.get("empId"))) {
                                emp.setId(Integer.parseInt(approversEmploeeId.get("empId").toString()));
                            }
                            dbo.approverId = emp;
                        }
                        dbo.applicantStatusLogTime = LocalDate.now();
                        dbo.applicationStatusLogTime = LocalDate.now();
                        dbo.modifiedUsersId = userId;
                    } else if (!Utils.isNullOrEmpty(dto.approverStatus) && dto.approverStatus.label.equalsIgnoreCase("Forward") && !Utils.isNullOrEmpty(dto.processCode)) {
                        ErpTemplateDBO erpTemplateDBO = new ErpTemplateDBO();
                        empUserId = null;
                        if ((dbo.erpApplicationWorkFlowProcessDBO.processCode.equalsIgnoreCase("LEAVE_APPLICATION_SUBMISSION") || dbo.erpApplicationWorkFlowProcessDBO.processCode.equalsIgnoreCase("LEAVE_APPLICATION_FORWARDER_REVERTED")) && dto.processCode.equalsIgnoreCase("LEAVE_APPLICATION_APPROVER_FORWARDED")) {
                            if (!Utils.isNullOrEmpty(dto.approverForwardedEmployee)) {
                                if (dbo.erpApplicationWorkFlowProcessDBO.processCode.equalsIgnoreCase("LEAVE_APPLICATION_FORWARDER_REVERTED")) {
                                    dbo.empLeaveForwarded2Id = null;
                                    dbo.forwarded2LatestComment = null;
                                }
                                ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
                                if (!Utils.isNullOrEmpty(erpWorkFlowProcessIdForApproverForwarded.get("erp_work_flow_process_id"))) {
                                    e.id = (Integer) erpWorkFlowProcessIdForApproverForwarded.get("erp_work_flow_process_id");
                                }
                                dbo.erpApplicantWorkFlowProcessDBO = e;
                                dbo.erpApplicationWorkFlowProcessDBO = e;
                                EmpDBO forwarder1 = new EmpDBO();
                                forwarder1.setId(Integer.parseInt(dto.approverForwardedEmployee.value));
                                dbo.empLeaveForwarded1Id = forwarder1;
                                if (!Utils.isNullOrEmpty(dto.getApproverForwardedEmployeeComments()))
                                    dbo.forwarded1LatestComment = dto.getApproverForwardedEmployeeComments();
                                empUserId = commonApiTransaction1.getUserIdByEmployeeId(dto.approverForwardedEmployee.value);
                                statusLogList.add(leaveApplicationHandler.getErpWorkFlowProcessStatusLogDBO(dto.id, dbo, userId));
                                erpTemplateDBO = emailandSMSTemplates.get("LEAVE_APPLICATION_APPROVER_FORWARDED" + "-" + "Mail");
                                notificationCodeString = "LEAVE_APPLICATION_APPROVER_FORWARDED";
                                if (!Utils.isNullOrEmpty(erpWorkFlowProcessIdForApproverForwarded.get("erp_work_flow_process_id"))) {
                                    workFlowProcessIdValue = (Integer) erpWorkFlowProcessIdForApproverForwarded.get("erp_work_flow_process_id");
                                }
                            }
                        } else if ((dbo.erpApplicationWorkFlowProcessDBO.processCode.equalsIgnoreCase("LEAVE_APPLICATION_APPROVER_FORWARDED")
                                || dbo.erpApplicationWorkFlowProcessDBO.processCode.equalsIgnoreCase("LEAVE_APPLICATION_FORWARDER_2_REVERTED"))
                                && dto.processCode.equalsIgnoreCase("LEAVE_APPLICATION_FORWARDER_FORWARDED")) {
                            if (!Utils.isNullOrEmpty(dto.approverForwarded2Employee)) {
                                ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
                                if (!Utils.isNullOrEmpty(erpWorkFlowProcessIdForForwarderForwarded.get("erp_work_flow_process_id"))) {
                                    e.id = (Integer) erpWorkFlowProcessIdForForwarderForwarded.get("erp_work_flow_process_id");
                                }
                                dbo.erpApplicantWorkFlowProcessDBO = e;
                                dbo.erpApplicationWorkFlowProcessDBO = e;
                                EmpDBO forwarder2 = new EmpDBO();
                                forwarder2.setId(Integer.parseInt(dto.approverForwarded2Employee.value));
                                dbo.empLeaveForwarded2Id = forwarder2;
                                if (!Utils.isNullOrEmpty(dto.getApproverForwarded2EmployeeComments()))
                                    dbo.forwarded2LatestComment = dto.getApproverForwarded2EmployeeComments();
                                empUserId = commonApiTransaction1.getUserIdByEmployeeId(dto.approverForwarded2Employee.value);
                                statusLogList.add(leaveApplicationHandler.getErpWorkFlowProcessStatusLogDBO(dto.id, dbo, userId));
                                erpTemplateDBO = emailandSMSTemplates.get("LEAVE_APPLICATION_FORWARDER_FORWARDED" + "-" + "Mail");
                                notificationCodeString = "LEAVE_APPLICATION_FORWARDER_FORWARDED";
                                if (!Utils.isNullOrEmpty(erpWorkFlowProcessIdForForwarderForwarded.get("erp_work_flow_process_id"))) {
                                    workFlowProcessIdValue = (Integer) erpWorkFlowProcessIdForForwarderForwarded.get("erp_work_flow_process_id");
                                }
                            }
                        }
                        String s = String.valueOf(workFlowProcessIdValue) + "-" + notificationCodeString + "-" + userId;
                        if (!emailMap.containsKey(s)) {
                            if (erpTemplateDBO != null && !Utils.isNullOrEmpty(empUserId)) {
                                emailList.add(leaveApplicationHandler.getEmailDBO(erpTemplateDBO, dbo.id, Integer.parseInt(empUserId.get("userId").toString()), dto));
                                emailMap.put(s, emailList);
                            }
                        } else {
                            List<ErpEmailsDBO> emails = emailMap.get(s);
                            if (erpTemplateDBO != null && !Utils.isNullOrEmpty(empUserId))
                                emails.add(leaveApplicationHandler.getEmailDBO(erpTemplateDBO, dbo.id, Integer.parseInt(empUserId.get("userId").toString()), dto));
                        }
                        if (!notificationMap.containsKey(s) && !Utils.isNullOrEmpty(empUserId)) {
                            notificationList.add(leaveApplicationHandler.getNotificationsDBO(dbo.id, Integer.parseInt(empUserId.get("userId").toString())));
                            notificationMap.put(s, notificationList);
                        } else {
                            List<ErpNotificationsDBO> notifications = notificationMap.get(s);
                            if (!Utils.isNullOrEmpty(empUserId)) {
                                notifications.add(leaveApplicationHandler.getNotificationsDBO(dbo.id, Integer.parseInt(empUserId.get("userId").toString())));
                            }
                        }
                        if (!Utils.isNullOrEmpty(approversEmploeeId)) {
                            EmpDBO emp = new EmpDBO();
                            if (!Utils.isNullOrEmpty(approversEmploeeId.get("empId"))) {
                                emp.setId(Integer.parseInt(approversEmploeeId.get("empId").toString()));
                            }
                            dbo.approverId = emp;
                        }
                        dbo.applicantStatusLogTime = LocalDate.now();
                        dbo.applicationStatusLogTime = LocalDate.now();
                        dbo.modifiedUsersId = userId;
                    } else if (!Utils.isNullOrEmpty(dto.approverStatus) && dto.approverStatus.label.equalsIgnoreCase("Return") && !Utils.isNullOrEmpty(dto.processCode)) {
                        ErpTemplateDBO erpTemplateDBO = new ErpTemplateDBO();
                        if ((dbo.erpApplicationWorkFlowProcessDBO.processCode.equalsIgnoreCase("LEAVE_APPLICATION_SUBMISSION") || dbo.erpApplicationWorkFlowProcessDBO.processCode.equalsIgnoreCase("LEAVE_APPLICATION_FORWARDER_REVERTED")) && dto.processCode.equalsIgnoreCase("LEAVE_APPLICATION_APPROVER_RETURNED")) {
                            ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
                            if (!Utils.isNullOrEmpty(erpWorkFlowProcessIdForApproverReturned.get("erp_work_flow_process_id"))) {
                                e.id = (Integer) erpWorkFlowProcessIdForApproverReturned.get("erp_work_flow_process_id");
                            }
                            dbo.erpApplicantWorkFlowProcessDBO = e;
                            dbo.erpApplicationWorkFlowProcessDBO = e;
                            erpTemplateDBO = emailandSMSTemplates.get("LEAVE_APPLICATION_APPROVER_RETURNED" + "-" + "Mail");
                            notificationCodeString = "LEAVE_APPLICATION_APPROVER_RETURNED";
                            if (!Utils.isNullOrEmpty(erpWorkFlowProcessIdForApproverReturned.get("erp_work_flow_process_id"))) {
                                workFlowProcessIdValue = (Integer) erpWorkFlowProcessIdForApproverReturned.get("erp_work_flow_process_id");
                            }
                        } else if ((dbo.erpApplicationWorkFlowProcessDBO.processCode.equalsIgnoreCase("LEAVE_APPLICATION_APPROVER_FORWARDED")
                                || dbo.erpApplicationWorkFlowProcessDBO.processCode.equalsIgnoreCase("LEAVE_APPLICATION_FORWARDER_2_REVERTED"))
                                && dto.processCode.equalsIgnoreCase("LEAVE_APPLICATION_FORWARDER_RETURNED")) {
                            ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
                            if (!Utils.isNullOrEmpty(erpWorkFlowProcessIdForForwarderReturned.get("erp_work_flow_process_id"))) {
                                e.id = (Integer) erpWorkFlowProcessIdForForwarderReturned.get("erp_work_flow_process_id");
                            }
                            dbo.erpApplicantWorkFlowProcessDBO = e;
                            dbo.erpApplicationWorkFlowProcessDBO = e;
                            erpTemplateDBO = emailandSMSTemplates.get("LEAVE_APPLICATION_FORWARDER_RETURNED" + "-" + "Mail");
                            notificationCodeString = "LEAVE_APPLICATION_FORWARDER_RETURNED";
                            if (!Utils.isNullOrEmpty(erpWorkFlowProcessIdForForwarderReturned.get("erp_work_flow_process_id"))) {
                                workFlowProcessIdValue = (Integer) erpWorkFlowProcessIdForForwarderReturned.get("erp_work_flow_process_id");
                            }
                        } else if (dbo.erpApplicationWorkFlowProcessDBO.processCode.equalsIgnoreCase("LEAVE_APPLICATION_FORWARDER_FORWARDED") && dto.processCode.equalsIgnoreCase("LEAVE_APPLICATION_FORWARDER_2_RETURNED")) {
                            ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
                            if (!Utils.isNullOrEmpty(erpWorkFlowProcessIdForForwarder2Returned.get("erp_work_flow_process_id"))) {
                                e.id = (Integer) erpWorkFlowProcessIdForForwarder2Returned.get("erp_work_flow_process_id");
                            }
                            dbo.erpApplicantWorkFlowProcessDBO = e;
                            dbo.erpApplicationWorkFlowProcessDBO = e;
                            erpTemplateDBO = emailandSMSTemplates.get("LEAVE_APPLICATION_FORWARDER_2_RETURNED" + "-" + "Mail");
                            notificationCodeString = "LEAVE_APPLICATION_FORWARDER_2_RETURNED";
                            if (!Utils.isNullOrEmpty(erpWorkFlowProcessIdForForwarder2Returned.get("erp_work_flow_process_id"))) {
                                workFlowProcessIdValue = (Integer) erpWorkFlowProcessIdForForwarder2Returned.get("erp_work_flow_process_id");
                            }
                        }
                        leaveApplicationHandler.updateEmpLeaveAllocation(String.valueOf(userId), dto, dbo);
                        statusLogList.add(leaveApplicationHandler.getErpWorkFlowProcessStatusLogDBO(dto.id, dbo, userId));
                        String s = String.valueOf(workFlowProcessIdValue) + "-" + notificationCodeString + "-" + userId;
                        if (!emailMap.containsKey(s)) {
                            if (erpTemplateDBO != null && !Utils.isNullOrEmpty(empUserId)) {
                                emailList.add(leaveApplicationHandler.getEmailDBO(erpTemplateDBO, dbo.id, Integer.parseInt(empUserId.get("userId").toString()), dto));
                                emailMap.put(s, emailList);
                            }
                        } else {
                            List<ErpEmailsDBO> emails = emailMap.get(s);
                            if (erpTemplateDBO != null && !Utils.isNullOrEmpty(empUserId))
                                emails.add(leaveApplicationHandler.getEmailDBO(erpTemplateDBO, dbo.id, Integer.parseInt(empUserId.get("userId").toString()), dto));
                        }

                        if (!notificationMap.containsKey(s) && !Utils.isNullOrEmpty(empUserId)) {
                            notificationList.add(leaveApplicationHandler.getNotificationsDBO(dbo.id, Integer.parseInt(empUserId.get("userId").toString())));
                            notificationMap.put(s, notificationList);
                        } else {
                            List<ErpNotificationsDBO> notifications = notificationMap.get(s);
                            if (!Utils.isNullOrEmpty(empUserId)) {
                                notifications.add(leaveApplicationHandler.getNotificationsDBO(dbo.id, Integer.parseInt(empUserId.get("userId").toString())));
                            }
                        }
                        if (!Utils.isNullOrEmpty(approversEmploeeId)) {
                            EmpDBO emp = new EmpDBO();
                            if (!Utils.isNullOrEmpty(approversEmploeeId.get("empId"))) {
                                emp.setId(Integer.parseInt(approversEmploeeId.get("empId").toString()));
                            }
                            dbo.approverId = emp;
                        }
                        dbo.approverComments = dto.approverComments;
                        dbo.isPending = false;
                        dbo.applicantStatusLogTime = LocalDate.now();
                        dbo.applicationStatusLogTime = LocalDate.now();
                        dbo.modifiedUsersId = userId;
                    } else if (!Utils.isNullOrEmpty(dto.approverStatus) && dto.approverStatus.label.equalsIgnoreCase("Clarify/Meet") && !Utils.isNullOrEmpty(dto.processCode)) {
                        ErpTemplateDBO erpTemplateDBO = new ErpTemplateDBO();
                        if ((dbo.erpApplicationWorkFlowProcessDBO.processCode.equalsIgnoreCase("LEAVE_APPLICATION_SUBMISSION") || dbo.erpApplicationWorkFlowProcessDBO.processCode.equalsIgnoreCase("LEAVE_APPLICATION_FORWARDER_REVERTED")) && dto.processCode.equalsIgnoreCase("LEAVE_APPLICATION_APPROVER_REQUEST_CLARIFICATION")) {
                            ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
                            if (!Utils.isNullOrEmpty(erpWorkFlowProcessIdForApproverClarification.get("erp_work_flow_process_id"))) {
                                e.id = (Integer) erpWorkFlowProcessIdForApproverClarification.get("erp_work_flow_process_id");
                            }
                            dbo.erpApplicantWorkFlowProcessDBO = e;
                            dbo.erpApplicationWorkFlowProcessDBO = e;
                            erpTemplateDBO = emailandSMSTemplates.get("LEAVE_APPLICATION_APPROVER_REQUEST_CLARIFICATION" + "-" + "Mail");
                            notificationCodeString = "LEAVE_APPLICATION_APPROVER_REQUEST_CLARIFICATION";
                            if (!Utils.isNullOrEmpty(erpWorkFlowProcessIdForApproverClarification.get("erp_work_flow_process_id"))) {
                                workFlowProcessIdValue = (Integer) erpWorkFlowProcessIdForApproverClarification.get("erp_work_flow_process_id");
                            }
                        } else if ((dbo.erpApplicationWorkFlowProcessDBO.processCode.equalsIgnoreCase("LEAVE_APPLICATION_APPROVER_FORWARDED")
                                || dbo.erpApplicationWorkFlowProcessDBO.processCode.equalsIgnoreCase("LEAVE_APPLICATION_FORWARDER_2_REVERTED"))
                                && dto.processCode.equalsIgnoreCase("LEAVE_APPLICATION_FORWARDER_REQUEST_CLARIFICATION")) {
                            ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
                            if (!Utils.isNullOrEmpty(erpWorkFlowProcessIdForForwarderClarification.get("erp_work_flow_process_id"))) {
                                e.id = (Integer) erpWorkFlowProcessIdForForwarderClarification.get("erp_work_flow_process_id");
                            }
                            dbo.erpApplicantWorkFlowProcessDBO = e;
                            dbo.erpApplicationWorkFlowProcessDBO = e;
                            erpTemplateDBO = emailandSMSTemplates.get("LEAVE_APPLICATION_FORWARDER_REQUEST_CLARIFICATION" + "-" + "Mail");
                            notificationCodeString = "LEAVE_APPLICATION_FORWARDER_REQUEST_CLARIFICATION";
                            if (!Utils.isNullOrEmpty(erpWorkFlowProcessIdForForwarderClarification.get("erp_work_flow_process_id"))) {
                                workFlowProcessIdValue = (Integer) erpWorkFlowProcessIdForForwarderClarification.get("erp_work_flow_process_id");
                            }
                        } else if (dbo.erpApplicationWorkFlowProcessDBO.processCode.equalsIgnoreCase("LEAVE_APPLICATION_FORWARDER_FORWARDED") && dto.processCode.equalsIgnoreCase("LEAVE_APPLICATION_FORWARDER_2_REQUEST_CLARIFICATION")) {
                            ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
                            if (!Utils.isNullOrEmpty(erpWorkFlowProcessIdForForwarder2Clarification.get("erp_work_flow_process_id"))) {
                                e.id = (Integer) erpWorkFlowProcessIdForForwarder2Clarification.get("erp_work_flow_process_id");
                            }
                            dbo.erpApplicantWorkFlowProcessDBO = e;
                            dbo.erpApplicationWorkFlowProcessDBO = e;
                            erpTemplateDBO = emailandSMSTemplates.get("LEAVE_APPLICATION_FORWARDER_2_REQUEST_CLARIFICATION" + "-" + "Mail");
                            notificationCodeString = "LEAVE_APPLICATION_FORWARDER_2_REQUEST_CLARIFICATION";
                            if (!Utils.isNullOrEmpty(erpWorkFlowProcessIdForForwarder2Clarification.get("erp_work_flow_process_id"))) {
                                workFlowProcessIdValue = (Integer) erpWorkFlowProcessIdForForwarder2Clarification.get("erp_work_flow_process_id");
                            }
                        }
                        statusLogList.add(leaveApplicationHandler.getErpWorkFlowProcessStatusLogDBO(dto.id, dbo, userId));
                        String s = String.valueOf(workFlowProcessIdValue) + "-" + notificationCodeString + "-" + userId;
                        if (!emailMap.containsKey(s)) {
                            if (erpTemplateDBO != null && !Utils.isNullOrEmpty(empUserId)) {
                                emailList.add(leaveApplicationHandler.getEmailDBO(erpTemplateDBO, dbo.id, Integer.parseInt(empUserId.get("userId").toString()), dto));
                                emailMap.put(s, emailList);
                            }
                        } else {
                            List<ErpEmailsDBO> emails = emailMap.get(s);
                            if (erpTemplateDBO != null && !Utils.isNullOrEmpty(empUserId))
                                emails.add(leaveApplicationHandler.getEmailDBO(erpTemplateDBO, dbo.id, Integer.parseInt(empUserId.get("userId").toString()), dto));
                        }

                        if (!notificationMap.containsKey(s) && !Utils.isNullOrEmpty(empUserId)) {
                            notificationList.add(leaveApplicationHandler.getNotificationsDBO(dbo.id, Integer.parseInt(empUserId.get("userId").toString())));
                            notificationMap.put(s, notificationList);
                        } else {
                            List<ErpNotificationsDBO> notifications = notificationMap.get(s);
                            if (!Utils.isNullOrEmpty(empUserId)) {
                                notifications.add(leaveApplicationHandler.getNotificationsDBO(dbo.id, Integer.parseInt(empUserId.get("userId").toString())));
                            }
                        }
                        if (!Utils.isNullOrEmpty(approversEmploeeId)) {
                            EmpDBO emp = new EmpDBO();
                            if (!Utils.isNullOrEmpty(approversEmploeeId.get("empId"))) {
                                emp.setId(Integer.parseInt(approversEmploeeId.get("empId").toString()));
                            }
                            dbo.approverId = emp;
                        }
                        dbo.approverComments = dto.approverComments;
                        dbo.applicantStatusLogTime = LocalDate.now();
                        dbo.applicationStatusLogTime = LocalDate.now();
                        dbo.modifiedUsersId = userId;
                    } else if (!Utils.isNullOrEmpty(dto.approverStatus) && dto.approverStatus.label.equalsIgnoreCase("Revert") && !Utils.isNullOrEmpty(dto.processCode)) {
                        ErpTemplateDBO erpTemplateDBO = new ErpTemplateDBO();
                        empUserId = null;
                        if ((dbo.erpApplicationWorkFlowProcessDBO.processCode.equalsIgnoreCase("LEAVE_APPLICATION_APPROVER_FORWARDED") ||
                                dbo.erpApplicationWorkFlowProcessDBO.processCode.equalsIgnoreCase("LEAVE_APPLICATION_FORWARDER_2_REVERTED")) && dto.processCode.equalsIgnoreCase("LEAVE_APPLICATION_FORWARDER_REVERTED")) {
                            ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
                            if (!Utils.isNullOrEmpty(erpWorkFlowProcessIdForForwarderReverted.get("erp_work_flow_process_id"))) {
                                e.id = (Integer) erpWorkFlowProcessIdForForwarderReverted.get("erp_work_flow_process_id");
                            }
                            dbo.erpApplicantWorkFlowProcessDBO = e;
                            dbo.erpApplicationWorkFlowProcessDBO = e;
                            System.out.println(dbo.getApproverId().id);
                            empUserId = commonApiTransaction1.getUserIdByEmployeeId(String.valueOf(dbo.getApproverId().id));
                            statusLogList.add(leaveApplicationHandler.getErpWorkFlowProcessStatusLogDBO(dto.id, dbo, userId));
                            erpTemplateDBO = emailandSMSTemplates.get("LEAVE_APPLICATION_FORWARDER_REVERTED" + "-" + "Mail");
                            notificationCodeString = "LEAVE_APPLICATION_FORWARDER_REVERTED";
                            if (!Utils.isNullOrEmpty(erpWorkFlowProcessIdForForwarderReverted.get("erp_work_flow_process_id"))) {
                                workFlowProcessIdValue = (Integer) erpWorkFlowProcessIdForForwarderReverted.get("erp_work_flow_process_id");
                            }
                        } else if (dbo.erpApplicationWorkFlowProcessDBO.processCode.equalsIgnoreCase("LEAVE_APPLICATION_FORWARDER_FORWARDED") && dto.processCode.equalsIgnoreCase("LEAVE_APPLICATION_FORWARDER_2_REVERTED")) {
                            ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
                            if (!Utils.isNullOrEmpty(erpWorkFlowProcessIdForForwarder2Reverted.get("erp_work_flow_process_id"))) {
                                e.id = (Integer) erpWorkFlowProcessIdForForwarder2Reverted.get("erp_work_flow_process_id");
                            }
                            dbo.erpApplicantWorkFlowProcessDBO = e;
                            dbo.erpApplicationWorkFlowProcessDBO = e;
                            empUserId = commonApiTransaction1.getUserIdByEmployeeId(String.valueOf(dbo.empLeaveForwarded1Id.id));
                            statusLogList.add(leaveApplicationHandler.getErpWorkFlowProcessStatusLogDBO(dto.id, dbo, userId));
                            erpTemplateDBO = emailandSMSTemplates.get("LEAVE_APPLICATION_FORWARDER_2_REVERTED" + "-" + "Mail");
                            notificationCodeString = "LEAVE_APPLICATION_FORWARDER_2_REVERTED";
                            if (!Utils.isNullOrEmpty(erpWorkFlowProcessIdForForwarder2Reverted.get("erp_work_flow_process_id"))) {
                                workFlowProcessIdValue = (Integer) erpWorkFlowProcessIdForForwarder2Reverted.get("erp_work_flow_process_id");
                            }
                        }
                        String s = String.valueOf(workFlowProcessIdValue) + "-" + notificationCodeString + "-" + userId;
                        if (!emailMap.containsKey(s)) {
                            if (erpTemplateDBO != null && !Utils.isNullOrEmpty(empUserId)) {
                                emailList.add(leaveApplicationHandler.getEmailDBO(erpTemplateDBO, dbo.id, Integer.parseInt(empUserId.get("userId").toString()), dto));
                                emailMap.put(s, emailList);
                            }
                        } else {
                            List<ErpEmailsDBO> emails = emailMap.get(s);
                            if (erpTemplateDBO != null && !Utils.isNullOrEmpty(empUserId))
                                emails.add(leaveApplicationHandler.getEmailDBO(erpTemplateDBO, dbo.id, Integer.parseInt(empUserId.get("userId").toString()), dto));
                        }
                        if (!notificationMap.containsKey(s) && !Utils.isNullOrEmpty(empUserId)) {
                            notificationList.add(leaveApplicationHandler.getNotificationsDBO(dbo.id, Integer.parseInt(empUserId.get("userId").toString())));
                            notificationMap.put(s, notificationList);
                        } else {
                            List<ErpNotificationsDBO> notifications = notificationMap.get(s);
                            if (!Utils.isNullOrEmpty(empUserId)) {
                                notifications.add(leaveApplicationHandler.getNotificationsDBO(dbo.id, Integer.parseInt(empUserId.get("userId").toString())));
                            }
                        }
                        if (!Utils.isNullOrEmpty(approversEmploeeId)) {
                            EmpDBO emp = new EmpDBO();
                            if (!Utils.isNullOrEmpty(approversEmploeeId.get("empId"))) {
                                emp.setId(Integer.parseInt(approversEmploeeId.get("empId").toString()));
                            }
                            dbo.approverId = emp;
                        }
                        dbo.approverComments = dto.approverComments;
                        dbo.applicantStatusLogTime = LocalDate.now();
                        dbo.applicationStatusLogTime = LocalDate.now();
                        dbo.modifiedUsersId = userId;
                    }
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
        //	commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(workFlowProcessIdValue, notificationCodeString,authoriserUserIdSet, notificationList, null, emailsList);
        commonApiTransaction1.saveErpWorkFlowProcessStatusLogDBO1(statusLogList);
        return empLeaveEntryDBOData;
    }

    public Flux<EmpLeaveEntryDTO> getEmployeeLeavesByApproverFilterStatus(String userId,String status) throws Exception {
        Integer empId = commonApiTransaction1.getEmployeesByUserId(userId);
        return leaveApplicationApproverTransaction.getEmployeeLeavesByApproverFilterStatus(empId,status).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
    }

    public Flux<EmpLeaveEntryDTO> getEmployeeLeavesOnSameDay(String employeeId,String startDate,String endDate) throws Exception {
        return leaveApplicationApproverTransaction.getEmployeeLeavesOnSameDay(employeeId,startDate,endDate).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
    }

}
