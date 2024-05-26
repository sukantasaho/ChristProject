package com.christ.erp.services.handlers.hostel.application;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpEmailsDBO;
import com.christ.erp.services.dbobjects.common.ErpNotificationsDBO;
import com.christ.erp.services.dbobjects.common.ErpSmsDBO;
import com.christ.erp.services.dbobjects.common.ErpUsersDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelApplicationDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelRoomTypeDBO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.hostel.settings.HostelApplicationDTO;
import com.christ.erp.services.dto.hostel.settings.HostelApplicationRoomTypePreferenceDTO;
import com.christ.erp.services.dto.student.common.StudentApplnEntriesDTO;
import com.christ.erp.services.dto.student.common.StudentDTO;
import com.christ.erp.services.handlers.common.CommonApiHandler;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.hostel.application.PublishSelectionTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressWarnings("rawtypes")
@Service
public class PublishSelectionHandler {
	@Autowired
	private PublishSelectionTransaction publishSelectionTransaction;

	@Autowired
	CommonApiTransaction commonApiTransaction1;

	@Autowired
	private CommonApiHandler commonApiHandler;

	public Flux<HostelApplicationDTO> getGridData(String yearId, String hostelId, Boolean isPublished) {
		List<HostelApplicationDBO> hostelApplicationDBOList = publishSelectionTransaction.getGridData(yearId, hostelId, isPublished);
		return this.convertDboToDto(hostelApplicationDBOList, yearId, hostelId, isPublished);
	}

	private Flux<HostelApplicationDTO> convertDboToDto(List<HostelApplicationDBO> hostelApplicationDBOList, String yearId, String hostelId, Boolean isPublished) {
		List<HostelApplicationDTO> hostelApplicationDTOList = new ArrayList<HostelApplicationDTO>();
		if(!Utils.isNullOrEmpty(hostelApplicationDBOList)) {
			hostelApplicationDBOList.forEach(dbo -> {
				HostelApplicationDTO dto = new HostelApplicationDTO();
				dto.setId(String.valueOf(dbo.getId()));
				if(!Utils.isNullOrEmpty(dbo.getApplicationNo())) {
					dto.setHostelApplicationNum(dbo.getApplicationNo());
				}
				if(isPublished.equals(false)) {
					if(!Utils.isNullOrEmpty(dbo.getHostelApplicationCurrentProcessStatus())) {
						if (dbo.hostelApplicationCurrentProcessStatus.processCode.equalsIgnoreCase("HOSTEL_APPLICATION_SELECTED_UPLOADED"))
							dto.setStatus("Selected");
						else if (dbo.hostelApplicationCurrentProcessStatus.processCode.equalsIgnoreCase("HOSTEL_APPLICATION_NOT_SELECTED_UPLOADED"))
							dto.setStatus("Not Selected");	
					} 
				} else {
					if(!Utils.isNullOrEmpty(dbo.getHostelApplicationCurrentProcessStatus())) {
						if (dbo.hostelApplicationCurrentProcessStatus.processCode.equalsIgnoreCase("HOSTEL_APPLICATION_SELECTED"))
							dto.setStatus("Selected");
						else if (dbo.hostelApplicationCurrentProcessStatus.processCode.equalsIgnoreCase("HOSTEL_APPLICATION_NOT_SELECTED"))
							dto.setStatus("Not Selected");	
					} 	
				}
				if(!Utils.isNullOrEmpty(dbo.getAllottedHostelRoomTypeDBO())) {
					dto.setAllotedRoomType(new SelectDTO());
					dto.getAllotedRoomType().setValue(dbo.getAllottedHostelRoomTypeDBO().getId().toString());
					dto.getAllotedRoomType().setLabel(dbo.getAllottedHostelRoomTypeDBO().getRoomType());
				}
				if(!Utils.isNullOrEmpty(dbo.getStudentApplnEntriesDBO())) {
					dto.setStudentApplnEntriesDTO(new StudentApplnEntriesDTO());
					dto.getStudentApplnEntriesDTO().setId(dbo.getStudentApplnEntriesDBO().getId());
					if(!Utils.isNullOrEmpty(dbo.getStudentApplnEntriesDBO().getApplicantName())) {
						dto.getStudentApplnEntriesDTO().setApplicantName(dbo.getStudentApplnEntriesDBO().getApplicantName());
					}
					if(!Utils.isNullOrEmpty(dbo.getStudentApplnEntriesDBO().getApplicationNo())) {
						dto.getStudentApplnEntriesDTO().setApplicationNumber(String.valueOf(dbo.getStudentApplnEntriesDBO().getApplicationNo()));
					}
					if(!Utils.isNullOrEmpty(dbo.getStudentApplnEntriesDBO().getPersonalEmailId())) {
						dto.getStudentApplnEntriesDTO().setPersonalEmailId(dbo.getStudentApplnEntriesDBO().getPersonalEmailId());
					}
					if(!Utils.isNullOrEmpty(dbo.getStudentApplnEntriesDBO().getMobileNo())) {
						dto.getStudentApplnEntriesDTO().setMobileNo(dbo.getStudentApplnEntriesDBO().getMobileNo());
					}
				}
				if(!Utils.isNullOrEmpty(dbo.getStudentDBO())) {
					dto.setStudent(new StudentDTO());
					dto.getStudent().setId(dbo.getStudentDBO().getId());
					if(!Utils.isNullOrEmpty(dbo.getStudentDBO().getStudentName())) {
						dto.getStudent().setStudentName(dbo.getStudentDBO().getStudentName());
					}
					if(!Utils.isNullOrEmpty(dbo.getStudentDBO().getRegisterNo())) {
						dto.getStudent().setRegisterNo(dbo.getStudentDBO().getRegisterNo());
					}
				}
				if(!Utils.isNullOrEmpty(dbo.getHostelApplicationRoomTypePreferenceDBO())) {
					List<HostelApplicationRoomTypePreferenceDTO> roomTypePreferenceList = new ArrayList<HostelApplicationRoomTypePreferenceDTO>();
					dbo.getHostelApplicationRoomTypePreferenceDBO().forEach(subDbo -> {
						HostelApplicationRoomTypePreferenceDTO subDTO = new HostelApplicationRoomTypePreferenceDTO();
						subDTO.setId(subDbo.getId());
						if(!Utils.isNullOrEmpty(subDbo.getPreferenceOrder())) {
							subDTO.setPreferenceOrder(subDbo.getPreferenceOrder());
						}
						if(!Utils.isNullOrEmpty(subDbo.getHostelRoomTypesDBO())) {
							subDTO.setHostelRoomTypesDTO(new SelectDTO());
							subDTO.getHostelRoomTypesDTO().setValue(String.valueOf(subDbo.getHostelRoomTypesDBO().getId()));
							subDTO.getHostelRoomTypesDTO().setLabel(subDbo.getHostelRoomTypesDBO().getRoomType());
							roomTypePreferenceList.add(subDTO);
						}
					});
					dto.setHostelApplicationRoomTypePreferenceDTO(roomTypePreferenceList);
					hostelApplicationDTOList.add(dto);
				}
			});
		}
		return Flux.fromIterable(hostelApplicationDTOList);
	}

	public Mono<ApiResult> publishSelectionUpdate(Mono<List<HostelApplicationDTO>> data1,String userId) {
		return data1.map(data -> convertDtoToDbo(data, userId)).flatMap(dbos -> {
			publishSelectionTransaction.publishSelectionUpdate(dbos);
			return Mono.just(Boolean.TRUE);
		}).map(Utils::responseResult);
	}

	String  notificationCodeString = "";
	int workFlowProcessIdValue = 0;

	private List<Object> convertDtoToDbo(List<HostelApplicationDTO> dto, String userId) {
		List<Object> data = new ArrayList<Object>();
		List<Integer> applicationIds = new ArrayList<Integer>();
		List<ErpWorkFlowProcessStatusLogDBO> statusLogList = new ArrayList<ErpWorkFlowProcessStatusLogDBO>();
		List<ErpNotificationsDBO> notificationList = new ArrayList<ErpNotificationsDBO>();
		Set<Integer> approversIdSet = new LinkedHashSet<Integer>();
		List<ErpSmsDBO> smsList = new ArrayList<ErpSmsDBO>();
		List<ErpEmailsDBO> emailsList = new ArrayList<ErpEmailsDBO>();
		dto.forEach(dtos -> {
			applicationIds.add(Integer.parseInt(dtos.getId()));
		});
		List<HostelApplicationDBO> hostelApplicationDBOList = publishSelectionTransaction.getData(applicationIds);
		Map<Integer, HostelApplicationDBO> hostelApplicationMap = new HashMap<Integer, HostelApplicationDBO>();
		hostelApplicationDBOList.forEach(exist -> {
			hostelApplicationMap.put(exist.getId(), exist);
		});
		dto.forEach(hostelApplicationDTO -> {
			HostelApplicationDBO dbo = null;
			if(hostelApplicationMap.containsKey(Integer.parseInt(hostelApplicationDTO.getId()))) {
				dbo = hostelApplicationMap.get(Integer.parseInt(hostelApplicationDTO.getId()));
				if(!Utils.isNullOrEmpty(hostelApplicationDTO.getFeePaymentEndDate())) {
					dbo.setFeePaymentEndDate(hostelApplicationDTO.getFeePaymentEndDate());
				}
				if(!Utils.isNullOrEmpty(hostelApplicationDTO.getAllotedRoomType())) {
					dbo.setAllottedHostelRoomTypeDBO(new HostelRoomTypeDBO());
					dbo.getAllottedHostelRoomTypeDBO().setId(Integer.parseInt(hostelApplicationDTO.getAllotedRoomType().getValue()));
				}
				if(!Utils.isNullOrEmpty(hostelApplicationDTO.getStatus()) && hostelApplicationDTO.getStatus().equalsIgnoreCase("Selected")) {
					Tuple selected = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("HOSTEL_APPLICATION_SELECTED");
					if(selected.get("applicant_status_display_text")!=null  && !Utils.isNullOrWhitespace(selected.get("applicant_status_display_text").toString())) {
						ErpWorkFlowProcessDBO applicant = new ErpWorkFlowProcessDBO();
						applicant.id =Integer.parseInt(selected.get("erp_work_flow_process_id").toString());
						dbo.hostelApplicantCurrentProcessStatus = applicant;
						dbo.setHostelApplicantStatusTime(LocalDateTime.now());
					}
					if(selected.get("application_status_display_text")!=null  && !Utils.isNullOrWhitespace(selected.get("application_status_display_text").toString())) {
						ErpWorkFlowProcessDBO application = new ErpWorkFlowProcessDBO();
						application.id =Integer.parseInt(selected.get("erp_work_flow_process_id").toString());
						dbo.hostelApplicationCurrentProcessStatus = application;
						dbo.setHostelApplicationStatusTime(LocalDateTime.now());
					}
					notificationList.add(getNotificationsDBO(dbo.id,Integer.parseInt(userId)));
					ErpTemplateDBO erpTemplateDBO = commonApiTransaction1.getErpTemplateByTemplateCodeAndTemplateTypes("Mail", "HOSTEL_APPLICATION_SELECTED_EMAIL");
					emailsList.add(getEmailDBO(erpTemplateDBO, dbo.id, userId, hostelApplicationDTO));
					// SMS		
					ErpTemplateDBO erpTemplateDBO1 = commonApiTransaction1.getErpTemplateByTemplateCodeAndTemplateTypes("SMS", "HOSTEL_APPLICATION_SELECTED_SMS");
					smsList.add(getSMSDBO(erpTemplateDBO1, dbo.getId(), userId, hostelApplicationDTO));
					approversIdSet.add(Integer.parseInt(userId));
					notificationCodeString = "HOSTEL_APPLICATION_SELECTED";
					workFlowProcessIdValue = (Integer) selected.get("erp_work_flow_process_id");
				}
				if(!Utils.isNullOrEmpty(hostelApplicationDTO.getStatus()) && hostelApplicationDTO.getStatus().equalsIgnoreCase("Not Selected")) {
					Tuple notSelected = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("HOSTEL_APPLICATION_NOT_SELECTED");
					if(notSelected.get("applicant_status_display_text")!=null  && !Utils.isNullOrWhitespace(notSelected.get("applicant_status_display_text").toString())) {
						ErpWorkFlowProcessDBO applicant = new ErpWorkFlowProcessDBO();
						applicant.id =Integer.parseInt(notSelected.get("erp_work_flow_process_id").toString());
						dbo.hostelApplicantCurrentProcessStatus = applicant;
						dbo.setHostelApplicantStatusTime(LocalDateTime.now());
					}
					if(notSelected.get("application_status_display_text")!=null  && !Utils.isNullOrWhitespace(notSelected.get("application_status_display_text").toString())) {
						ErpWorkFlowProcessDBO application = new ErpWorkFlowProcessDBO();
						application.id =Integer.parseInt(notSelected.get("erp_work_flow_process_id").toString());
						dbo.hostelApplicationCurrentProcessStatus = application;
						dbo.setHostelApplicationStatusTime(LocalDateTime.now());
					}
					notificationCodeString = "HOSTEL_APPLICATION_NOT_SELECTED";
					workFlowProcessIdValue = (Integer) notSelected.get("erp_work_flow_process_id");
					approversIdSet.add(Integer.parseInt(userId));
					notificationList.add(getNotificationsDBO(dbo.id,Integer.parseInt(userId)));
					ErpTemplateDBO erpTemplateDBO = commonApiTransaction1.getErpTemplateByTemplateCodeAndTemplateTypes("Mail", "HOSTEL_APPLICATION_NOT_SELECTED_EMAIL");
					emailsList.add(getEmailDBO(erpTemplateDBO, dbo.id, userId, hostelApplicationDTO));
					ErpTemplateDBO erpTemplateDBO1 = commonApiTransaction1.getErpTemplateByTemplateCodeAndTemplateTypes("SMS", "HOSTEL_APPLICATION_NOT_SELECTED_SMS");
					smsList.add(getSMSDBO(erpTemplateDBO1, dbo.getId(), userId, hostelApplicationDTO));
				}
			}
			ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
			erpWorkFlowProcessStatusLogDBO.setEntryId(Integer.parseInt(hostelApplicationDTO.getId()));
			erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(new ErpWorkFlowProcessDBO());
			erpWorkFlowProcessStatusLogDBO.getErpWorkFlowProcessDBO().setId(dbo.getHostelApplicationCurrentProcessStatus().getId());
			erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
			erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
			statusLogList.add(erpWorkFlowProcessStatusLogDBO);
		});
		commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(workFlowProcessIdValue, notificationCodeString, approversIdSet, notificationList, smsList, emailsList);
		data.addAll(statusLogList);
		data.addAll(hostelApplicationDBOList);
		return data;
	}

	private ErpSmsDBO getSMSDBO(ErpTemplateDBO erpTemplateDBO1, Integer entryId, String userId, HostelApplicationDTO dto) {
		ErpSmsDBO erpSmsDBO = new ErpSmsDBO();
		erpSmsDBO.entryId = entryId;
		ErpUsersDBO erpUsersDBO1 = new ErpUsersDBO();
		erpUsersDBO1.id =Integer.parseInt(userId);
		erpSmsDBO.erpUsersDBO = erpUsersDBO1;
		String msgBody = erpTemplateDBO1.getTemplateContent();
		msgBody = msgBody.replace("[APPLICANT_NAME]", dto.getStudentApplnEntriesDTO().getApplicantName());
		msgBody = msgBody.replace("[APPLICATION_NO]", dto.getHostelApplicationNum().toString());
		msgBody = msgBody.replace("[STATUS]", dto.getStatus());
		erpSmsDBO.smsContent = msgBody;
		if(!Utils.isNullOrEmpty(erpTemplateDBO1.getTemplateId()))
			erpSmsDBO.setTemplateId(erpTemplateDBO1.getTemplateId());
		erpSmsDBO.recipientMobileNo = dto.getStudentApplnEntriesDTO().getMobileNo();
		erpSmsDBO.createdUsersId = Integer.parseInt(userId);
		erpSmsDBO.recordStatus = 'A';
		return erpSmsDBO;
	}

	private ErpEmailsDBO getEmailDBO(ErpTemplateDBO erpTemplateDBO, Integer entryId, String userId, HostelApplicationDTO dto) {
		ErpEmailsDBO erpEmailsDBO = new ErpEmailsDBO();
		erpEmailsDBO.entryId = entryId;
		ErpUsersDBO erpUsersDBO1 = new ErpUsersDBO();
		erpUsersDBO1.id = Integer.parseInt(userId);
		erpEmailsDBO.erpUsersDBO = erpUsersDBO1;
		String msgBody = erpTemplateDBO.getTemplateContent();
		msgBody = msgBody.replace("[APPLICANT_NAME]", dto.getStudentApplnEntriesDTO().getApplicantName());
		msgBody = msgBody.replace("[APPLICATION_NO]", dto.getHostelApplicationNum().toString());
		msgBody = msgBody.replace("[STATUS]", dto.getStatus());
		erpEmailsDBO.emailContent = msgBody;
		if(!Utils.isNullOrEmpty(erpTemplateDBO.getMailSubject()))
			erpEmailsDBO.emailSubject = erpTemplateDBO.getMailSubject();
		if(!Utils.isNullOrEmpty(erpTemplateDBO.getMailFromName()))
			erpEmailsDBO.senderName = erpTemplateDBO.getMailFromName();
		erpEmailsDBO.recipientEmail = dto.getStudentApplnEntriesDTO().getPersonalEmailId();
		erpEmailsDBO.createdUsersId = Integer.parseInt(userId);
		erpEmailsDBO.recordStatus = 'A';
		return erpEmailsDBO;
	}

	private ErpNotificationsDBO getNotificationsDBO(int entryId, int userId) {
		ErpNotificationsDBO erpNotifications = new ErpNotificationsDBO();
		erpNotifications.entryId = entryId;
		ErpUsersDBO erpUsersDBO = new ErpUsersDBO();
		erpUsersDBO.id = userId;
		erpNotifications.erpUsersDBO = erpUsersDBO;
		erpNotifications.createdUsersId = userId;
		erpNotifications.recordStatus = 'A';
		return erpNotifications;
	}

	public Flux<SelectDTO> getHostelStatus() {
		return publishSelectionTransaction.getHostelStatus().flatMapMany(Flux::fromIterable).map(this::convertStatusDboToDto);
	}

	public SelectDTO convertStatusDboToDto(ErpWorkFlowProcessDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getProcessCode()));
			if (dbo.processCode.equalsIgnoreCase("HOSTEL_APPLICATION_SELECTED"))
				dto.setLabel("Selected");
			else if (dbo.processCode.equalsIgnoreCase("HOSTEL_APPLICATION_NOT_SELECTED"))
				dto.setLabel("Not Selected");
		}
		return dto;
	}
}
