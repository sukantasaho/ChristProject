package com.christ.erp.services.handlers.admission.admissionprocess;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpEmailsDBO;
import com.christ.erp.services.dbobjects.common.ErpSmsDBO;
import com.christ.erp.services.dbobjects.common.ErpStatusDBO;
import com.christ.erp.services.dbobjects.common.ErpStatusLogDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessNotificationsDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelApplicationDBO;
import com.christ.erp.services.dbobjects.student.common.CancellationType;
import com.christ.erp.services.dbobjects.student.common.RefundAccountHolderType;
import com.christ.erp.services.dbobjects.student.common.RefundAccountType;
import com.christ.erp.services.dbobjects.student.common.RefundType;
import com.christ.erp.services.dbobjects.student.common.StudentApplnCancellationReasonsDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesCancellationDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dto.student.common.StudentAdmissionCancellationOfflineDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.handlers.common.CommonApiHandler;
import com.christ.erp.services.transactions.admission.admissionprocess.AdmissionCancellationOfflineTransaction;
import reactor.core.publisher.Mono;

@Service
@SuppressWarnings("rawtypes")
public class AdmissionCancellationOfflineHandler {
	
	@Autowired
	AdmissionCancellationOfflineTransaction admissionCancellationOfflineTransaction;
	
    @Autowired
    private CommonApiHandler commonApiHandler1;
    
    Map<String,String> studentDetails = new HashMap<String, String>();
	
	public Mono<ApiResult> admissionCancellation(Mono<StudentAdmissionCancellationOfflineDTO> data, String userId) {
		return data.handle((studentAdmissionCancellationDTO, synchronousSink) -> {
			StudentApplnEntriesDBO studentData = admissionCancellationOfflineTransaction.checkStudentAdmissions(studentAdmissionCancellationDTO);
			Boolean notAdmitted = false;
			AtomicBoolean hostelAdmitted = new AtomicBoolean(false);
			
			if(!Utils.isNullOrEmpty(studentData)) {
				if(!studentData.getApplicationCurrentProcessStatus().getProcessCode().equalsIgnoreCase("ADM_APPLN_ADMITTED")) {
					notAdmitted = true;
				}
				if(!studentAdmissionCancellationDTO.getIsHostelAdmitted()) {
					if(!Utils.isNullOrEmpty(studentData.getHostelAdmissionsDBOSet()) ) {
						studentData.getHostelAdmissionsDBOSet().forEach(hostelAdmission -> {
							if(hostelAdmission.getRecordStatus() == 'A') {
								hostelAdmitted.set(true);
							}
						});
					}
				}
			}
			if(notAdmitted) {
				synchronousSink.error(new DuplicateException("Entered Application Number  Not Have Admitted status"));
			} 
			else if(hostelAdmitted.get()) {
				synchronousSink.error(new DuplicateException("Entered Application Number has processed hostel Admission"));
			}
			else {
				synchronousSink.next(studentAdmissionCancellationDTO);
			}
		}).cast(StudentAdmissionCancellationOfflineDTO.class)
				.map(data1 -> convertDtoToDbo(data1,userId))
				.flatMap( s -> {
				    Boolean result = admissionCancellationOfflineTransaction.update(s);
				    if(result) {
				    	sendMailsAndSms(userId);
				    }
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	public List<Object> convertDtoToDbo(StudentAdmissionCancellationOfflineDTO dto,String userId) {
		List<Object> dbos = new ArrayList<Object>();
		StudentApplnEntriesDBO studentData = admissionCancellationOfflineTransaction.getStudentDetails(dto);
		
		List<String> processCodes = new ArrayList<String>();
		processCodes.add("ADM_APPLN_CANCELLATION_REQUEST");
		processCodes.add("HOSTEL_APPLICATION_ADMISSION_CANCELLED");
		Map<String, Integer> erpWorkFlowProcessDBOIds = admissionCancellationOfflineTransaction.getErpWorkFlowProcessIdbyProcessCode1(processCodes).stream().collect(Collectors.toMap(s -> s.get("code").toString(),
				 										s -> Integer.parseInt(s.get("erp_work_flow_process_id").toString())));
		Integer admissionCancelled = erpWorkFlowProcessDBOIds.get("ADM_APPLN_CANCELLATION_REQUEST");
		Integer hostelAdmissionCancelled = erpWorkFlowProcessDBOIds.get("HOSTEL_APPLICATION_ADMISSION_CANCELLED") ;

		List<String> statusCodes = new ArrayList<String>();
		statusCodes.add("STUDENT_CANCELLED");
		statusCodes.add("HOSTEL_CANCELLED");
		Map<String, ErpStatusDBO> erpStatusDBOs = admissionCancellationOfflineTransaction.getErpStatus(statusCodes).stream().collect(Collectors.toMap(s -> s.getStatusCode(), s -> s));
		ErpStatusDBO studentCancelled = erpStatusDBOs.get("STUDENT_CANCELLED");
		ErpStatusDBO hostelCancelled = erpStatusDBOs.get("HOSTEL_CANCELLED");
		
		studentDetails.put("StudentEntriesId",String.valueOf(studentData.getId()));
		studentDetails.put("ApplicantName", studentData.getApplicantName());
		studentDetails.put("ApplicationNo", studentData.getApplicationNo().toString());
		studentDetails.put("PersonalEmailId", studentData.getPersonalEmailId());
		studentDetails.put("MobileNo", studentData.getMobileNo());
		studentDetails.put("admissionCancelled", admissionCancelled.toString());
		studentDetails.put("hostelAdmissionCancelled", hostelAdmissionCancelled.toString());
		studentDetails.put("IsHostelAdmitted", dto.getIsHostelAdmitted().toString());

		StudentApplnEntriesCancellationDBO cancellationDBO = new StudentApplnEntriesCancellationDBO();
		if(!Utils.isNullOrEmpty(dto.getReasonForCancellation())) {
			cancellationDBO.setStudentApplnCancellationReasonsDBO(new StudentApplnCancellationReasonsDBO());
			cancellationDBO.getStudentApplnCancellationReasonsDBO().setId(Integer.parseInt(dto.getReasonForCancellation().getValue()));
			cancellationDBO.setReasonForCancellationOthers(null);
		} else {
			cancellationDBO.setReasonForCancellationOthers(dto.getReasonForCancellationOthers());
			cancellationDBO.setStudentApplnCancellationReasonsDBO(null);
		}
		if(dto.getRefundType().equalsIgnoreCase("ONLINE")) {
			cancellationDBO.setRefundType(RefundType.valueOf(dto.getRefundType()));
			cancellationDBO.setRefundBankName(dto.getRefundBankName());
			cancellationDBO.setRefundIfscCode(dto.getRefundIfscCode());
			cancellationDBO.setRefundAccountNumber(dto.getRefundAccountNumber());
			cancellationDBO.setRefundAccountHolderType(RefundAccountHolderType.valueOf(dto.getRefundAccountHolderType()));
			cancellationDBO.setRefundAccountHolderName(dto.getRefundAccountHolderName());
			cancellationDBO.setRefundAccountType(RefundAccountType.valueOf(dto.getRefundAccountType()));
		} else if(dto.getRefundType().equalsIgnoreCase("CHEQUE")) {
			cancellationDBO.setRefundType(RefundType.valueOf(dto.getRefundType()));
			cancellationDBO.setChequeInFavour(dto.getChequeInFavour());
		} else {
			cancellationDBO.setRefundType(RefundType.valueOf(dto.getRefundType()));
			cancellationDBO.setRefundBankName(dto.getRefundBankName());
			cancellationDBO.setRefundSwiftCode(dto.getRefundSwiftCode());
			cancellationDBO.setRefundAccountNumber(dto.getRefundAccountNumber());
			cancellationDBO.setRefundAccountHolderType(RefundAccountHolderType.valueOf(dto.getRefundAccountHolderType()));
			cancellationDBO.setRefundAccountHolderName(dto.getRefundAccountHolderName());
			cancellationDBO.setRefundAccountType(RefundAccountType.valueOf(dto.getRefundAccountType()));
			cancellationDBO.setRefundIbanNo(dto.getRefundIbanNo());
		}
		cancellationDBO.setStudentApplnEntriesDBO(studentData);
		cancellationDBO.setCancellationRequestDateTime(LocalDateTime.now());
		cancellationDBO.setCancellationType(CancellationType.valueOf("OFFLINE"));
		cancellationDBO.setCreatedUsersId(Integer.parseInt(userId));
		cancellationDBO.setRecordStatus('A');

		if(!Utils.isNullOrEmpty(studentData)) {
			//updating the erp workFlow process in student_appln_entries table
			studentData.setApplicantCurrentProcessStatus(new ErpWorkFlowProcessDBO());
			studentData.getApplicantCurrentProcessStatus().setId(admissionCancelled);
			studentData.setApplicantStatusTime(LocalDateTime.now());
			studentData.setApplicationCurrentProcessStatus(new ErpWorkFlowProcessDBO());
			studentData.getApplicationCurrentProcessStatus().setId(admissionCancelled);
			studentData.setApplicationStatusTime(LocalDateTime.now());
			studentData.setModifiedUsersId(Integer.parseInt(userId));
			
			//Adding ErpWorkFlowProcessStatusLog 
			ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
			erpWorkFlowProcessStatusLogDBO.setEntryId(studentData.getId());
			erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(new ErpWorkFlowProcessDBO());
			erpWorkFlowProcessStatusLogDBO.getErpWorkFlowProcessDBO().setId(admissionCancelled);
//			erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
			erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
			erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
			dbos.add(erpWorkFlowProcessStatusLogDBO);

			//For Hostel Admission cancellations
			if(dto.getIsHostelAdmitted()) {
				if(!Utils.isNullOrEmpty(studentData.getHostelAdmissionsDBOSet())) {
					studentData.getHostelAdmissionsDBOSet().forEach( hostelAdmission -> {
						if(!Utils.isNullOrEmpty(hostelAdmission) && hostelAdmission.getRecordStatus() == 'A') {
							if(hostelAdmission.getHostelApplicationDBO().getRecordStatus() == 'A') {

								//updating the erp status in hostel Admission table
								hostelAdmission.setErpStatusDBO(new ErpStatusDBO());
								hostelAdmission.getErpStatusDBO().setId(hostelCancelled.getId());
								hostelAdmission.setErpCurrentStatusTime(LocalDateTime.now());
								hostelAdmission.setModifiedUsersId(Integer.parseInt(userId));
								
								//Adding ErpStatusLog
								ErpStatusLogDBO statusLogDBO = new ErpStatusLogDBO();
								statusLogDBO.setEntryId(studentData.getId());  
								ErpStatusDBO statusDBO = new ErpStatusDBO();
								statusDBO.setId(hostelCancelled.getId());
								statusLogDBO.setErpStatusDBO(statusDBO);
								statusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
								statusLogDBO.setRecordStatus('A');
								dbos.add(statusLogDBO);

								//updating the erp workFlow process in hostel Application table
								HostelApplicationDBO studentHostelApplication = hostelAdmission.getHostelApplicationDBO();
								studentHostelApplication.setHostelApplicantCurrentProcessStatus(new ErpWorkFlowProcessDBO());
								studentHostelApplication.getHostelApplicantCurrentProcessStatus().setId(hostelAdmissionCancelled);
								studentHostelApplication.setHostelApplicantStatusTime(LocalDateTime.now());
								studentHostelApplication.setHostelApplicationCurrentProcessStatus(new ErpWorkFlowProcessDBO());
								studentHostelApplication.getHostelApplicationCurrentProcessStatus().setId(hostelAdmissionCancelled);
								studentHostelApplication.setHostelApplicationStatusTime(LocalDateTime.now());
								studentHostelApplication.setModifiedUsersId(Integer.parseInt(userId));
								
								//Adding ErpWorkFlowProcessStatusLog 
								ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO1 = new ErpWorkFlowProcessStatusLogDBO();
								erpWorkFlowProcessStatusLogDBO1.setEntryId(studentData.getId());
								erpWorkFlowProcessStatusLogDBO1.setErpWorkFlowProcessDBO(new ErpWorkFlowProcessDBO());
								erpWorkFlowProcessStatusLogDBO1.getErpWorkFlowProcessDBO().setId(hostelAdmissionCancelled);
								erpWorkFlowProcessStatusLogDBO1.setRecordStatus('A');
								erpWorkFlowProcessStatusLogDBO1.setCreatedUsersId(Integer.parseInt(userId));
								dbos.add(erpWorkFlowProcessStatusLogDBO1);
							}
						}
					});
				}
			}

			//updating the erp status in student table
			if(!Utils.isNullOrEmpty(studentData.getStudentDBOS())  && studentData.getRecordStatus() == 'A') {
				studentData.getStudentDBOS().forEach(student -> {
					if(student.getRecordStatus() == 'A') {
						student.setErpStatus(new ErpStatusDBO());
						student.getErpStatus().setId(studentCancelled.getId());
						student.setErpCurrentStatusTime(LocalDateTime.now());
						student.setModifiedUsersId(Integer.parseInt(userId));
						
						//Adding ErpStatusLog
						ErpStatusLogDBO statusLogDBO = new ErpStatusLogDBO();
						statusLogDBO.setEntryId(studentData.getId());  
						ErpStatusDBO statusDBO = new ErpStatusDBO();
						statusDBO.setId(studentCancelled.getId());
						statusLogDBO.setErpStatusDBO(statusDBO);
						statusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
						statusLogDBO.setRecordStatus('A');
						dbos.add(statusLogDBO);
					}
				});
			}
		}
		dbos.add(studentData);
		dbos.add(cancellationDBO);
		return dbos;
	}
	
	public void sendMailsAndSms (String userId) {
		List<ErpEmailsDBO> emailsList = new ArrayList<ErpEmailsDBO>();
		List<ErpSmsDBO> smsList = new ArrayList<ErpSmsDBO>();
		List<String> code = new ArrayList<String>();
		code.add("ADM_APPLN_CANCELLATION_REQUEST_CANDIDATE");
		code.add("HOSTEL_APPLICATION_ADMISSION_CANCELLED_CANDIDATE");
		code.add("ADM_APPLN_CANCELLATION_REQUEST_OFFICE");
		code.add("HOSTEL_APPLICATION_ADMISSION_CANCELLED_OFFICE");
		Map<String, ErpWorkFlowProcessNotificationsDBO> notificationMap = admissionCancellationOfflineTransaction.getErpNotifications(code).stream().collect(Collectors.toMap(s -> s.getNotificationCode(), s -> s));
		
		if(!Utils.isNullOrEmpty(notificationMap)) {
			//Sending sms and email to Student for cancellation of Programme
			if(notificationMap.containsKey("ADM_APPLN_CANCELLATION_REQUEST_CANDIDATE")) {
				ErpWorkFlowProcessNotificationsDBO admApplnCancellationRequestNotificationDBO = notificationMap.get("ADM_APPLN_CANCELLATION_REQUEST_CANDIDATE");
				if(admApplnCancellationRequestNotificationDBO.getIsEmailActivated()) {
					ErpTemplateDBO emailTemplateContent = admApplnCancellationRequestNotificationDBO.getErpEmailsTemplateDBO();
					emailsList.add(getEmailDBO(emailTemplateContent,"Student",userId));
				}

				if(admApplnCancellationRequestNotificationDBO.getIsSmsActivated()) {
					ErpTemplateDBO smsTemplateContent = admApplnCancellationRequestNotificationDBO.getErpSmsTemplateDBO();
					smsList.add(getSMSDBO(smsTemplateContent,"Student",userId));
				}
				commonApiHandler1.send_ERP_Notification_SMS_Email_By_UserId(Integer.parseInt(studentDetails.get("admissionCancelled")),"ADM_APPLN_CANCELLATION_REQUEST_CANDIDATE",null,null,smsList,emailsList);
				emailsList.clear();
				smsList.clear();
			}
			
			//Sending sms and email to Office for cancellation of Programme by student
			if(notificationMap.containsKey("ADM_APPLN_CANCELLATION_REQUEST_OFFICE")) {
				ErpWorkFlowProcessNotificationsDBO admApplnCancellationRequestNotificationDBO = notificationMap.get("ADM_APPLN_CANCELLATION_REQUEST_OFFICE");
				if(admApplnCancellationRequestNotificationDBO.getIsEmailActivated()) {
					ErpTemplateDBO emailTemplateContent = admApplnCancellationRequestNotificationDBO.getErpEmailsTemplateDBO();
					emailsList.add(getEmailDBO(emailTemplateContent,"Office",userId));
				}

				if(admApplnCancellationRequestNotificationDBO.getIsSmsActivated()) {
					ErpTemplateDBO smsTemplateContent = admApplnCancellationRequestNotificationDBO.getErpSmsTemplateDBO();
					smsList.add(getSMSDBO(smsTemplateContent,"Office",userId));
				}
				commonApiHandler1.send_ERP_Notification_SMS_Email_By_UserId(Integer.parseInt(studentDetails.get("admissionCancelled")),"ADM_APPLN_CANCELLATION_REQUEST_OFFICE",null,null,smsList,emailsList);
				emailsList.clear();
				smsList.clear();
			}

			if(studentDetails.get("IsHostelAdmitted").equalsIgnoreCase("true")) {
				//Sending sms and email to Student for cancellation of hostel
				if(notificationMap.containsKey("HOSTEL_APPLICATION_ADMISSION_CANCELLED_CANDIDATE")) {
					ErpWorkFlowProcessNotificationsDBO admApplnCancellationRequestNotificationDBO = notificationMap.get("HOSTEL_APPLICATION_ADMISSION_CANCELLED_CANDIDATE");
					if(admApplnCancellationRequestNotificationDBO.getIsEmailActivated()) {
						ErpTemplateDBO emailTemplateContent = admApplnCancellationRequestNotificationDBO.getErpEmailsTemplateDBO();
						emailsList.add(getEmailDBO(emailTemplateContent,"Student",userId));
					}

					if(admApplnCancellationRequestNotificationDBO.getIsSmsActivated()) {
						ErpTemplateDBO smsTemplateContent = admApplnCancellationRequestNotificationDBO.getErpSmsTemplateDBO();
						smsList.add(getSMSDBO(smsTemplateContent,"Student",userId));
					}
					commonApiHandler1.send_ERP_Notification_SMS_Email_By_UserId(Integer.parseInt(studentDetails.get("hostelAdmissionCancelled")),"HOSTEL_APPLICATION_ADMISSION_CANCELLED_CANDIDATE",null,null,smsList,emailsList);
					emailsList.clear();
					smsList.clear();
				}
				//Sending sms and email to Office for cancellation of Programme by student
				if(notificationMap.containsKey("HOSTEL_APPLICATION_ADMISSION_CANCELLED_OFFICE")) {
					ErpWorkFlowProcessNotificationsDBO admApplnCancellationRequestNotificationDBO = notificationMap.get("HOSTEL_APPLICATION_ADMISSION_CANCELLED_OFFICE");
					if(admApplnCancellationRequestNotificationDBO.getIsEmailActivated()) {
						ErpTemplateDBO emailTemplateContent = admApplnCancellationRequestNotificationDBO.getErpEmailsTemplateDBO();
						emailsList.add(getEmailDBO(emailTemplateContent,"Office",userId));
					}

					if(admApplnCancellationRequestNotificationDBO.getIsSmsActivated()) {
						ErpTemplateDBO smsTemplateContent = admApplnCancellationRequestNotificationDBO.getErpSmsTemplateDBO();
						smsList.add(getSMSDBO(smsTemplateContent,"Office",userId));
					}
					commonApiHandler1.send_ERP_Notification_SMS_Email_By_UserId(Integer.parseInt(studentDetails.get("hostelAdmissionCancelled")),"HOSTEL_APPLICATION_ADMISSION_CANCELLED_OFFICE",null,null,smsList,emailsList);
					emailsList.clear();
					smsList.clear();
				}
			}	
		}
	}

	private ErpEmailsDBO getEmailDBO(ErpTemplateDBO erpTemplateDBO, String type, String userId ){
		ErpEmailsDBO erpEmailsDBO = new ErpEmailsDBO();
		erpEmailsDBO.entryId = Integer.parseInt(studentDetails.get("StudentEntriesId"));
		String msgBody = erpTemplateDBO.getTemplateContent();
		msgBody = msgBody.replace("[CANDIDATE_NAME]", studentDetails.get("ApplicantName"));
		msgBody = msgBody.replace("[APPLICATION_NO]", studentDetails.get("ApplicationNo"));
		erpEmailsDBO.emailContent = msgBody;
		if(type.equalsIgnoreCase("Office")) {
			//			erpEmailsDBO.recipientEmail = redisSysPropertiesData.getSysProperties(SysProperties.EMPLOYEE_JOINING_INTIMATION_EMAIL.name(), "C",Integer.parseInt(campusId));
			erpEmailsDBO.recipientEmail = "office@gmail.com";
		} else {
			erpEmailsDBO.recipientEmail = studentDetails.get("PersonalEmailId");
		}
		erpEmailsDBO.recordStatus = 'A';
		erpEmailsDBO.setCreatedUsersId(Integer.parseInt(userId));
		return erpEmailsDBO;
	}

	private ErpSmsDBO getSMSDBO(ErpTemplateDBO erpTemplateDBO1, String type, String userId) {
		
		ErpSmsDBO erpSmsDBO = new ErpSmsDBO();
		erpSmsDBO.entryId = Integer.parseInt(studentDetails.get("StudentEntriesId"));
		String msgBody = erpTemplateDBO1.getTemplateContent();
		msgBody = msgBody.replace("[CANDIDATE_NAME]", studentDetails.get("ApplicantName"));
		msgBody = msgBody.replace("[APPLICATION_NO]", studentDetails.get("ApplicationNo"));
		erpSmsDBO.smsContent = msgBody;
		if(type.equalsIgnoreCase("Office")) {
			//			erpEmailsDBO.recipientEmail = redisSysPropertiesData.getSysProperties(SysProperties.EMPLOYEE_JOINING_INTIMATION_EMAIL.name(), "C",Integer.parseInt(campusId));
			erpSmsDBO.recipientMobileNo = "1111222";
		} else {
			erpSmsDBO.recipientMobileNo = studentDetails.get("MobileNo");
		}
		erpSmsDBO.recordStatus = 'A';
		erpSmsDBO.setCreatedUsersId(Integer.parseInt(userId));
		return erpSmsDBO;
	}

}
