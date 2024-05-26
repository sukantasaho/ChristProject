package com.christ.erp.services.handlers.hostel.leavesandattendance;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessNotificationsDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dbobjects.hostel.fineanddisciplinary.HostelFineEntryDBO;
import com.christ.erp.services.dbobjects.hostel.leavesandattendance.HostelAttendanceDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelFineCategoryDBO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.hostel.leavesandattendance.AbsenteesListDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.common.CommonApiHandler;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.hostel.leavesandattendance.AbsenteesListTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Service
public class AbsenteesListHandler {

	@Autowired
	private CommonApiTransaction commonApiTransaction1;

	@Autowired
	private CommonApiHandler commonApiHandler;

	@Autowired
	private AbsenteesListTransaction absenteesListTransaction;

	public Mono<List<AbsenteesListDTO> > getGridData(Mono<AbsenteesListDTO> data) {
		return data.handle((absenteesListDTO,synchronousSink) -> {
			List<HostelAttendanceDBO> list = absenteesListTransaction.getGridData(absenteesListDTO);
			if(Utils.isNullOrEmpty(list)) {
				synchronousSink.error(new NotFoundException(null));
			} else {
				synchronousSink.next(list);
			}
		}).cast(ArrayList.class).map(data1 -> convertDBOToDTO(data1)).flatMap(s -> s );
	}

	public Mono<List<AbsenteesListDTO>> convertDBOToDTO( List<HostelAttendanceDBO> datas) {
		List<AbsenteesListDTO> dtos = new ArrayList<AbsenteesListDTO>();
		if(!Utils.isNullOrEmpty(datas)) {
			Map<Integer, HostelFineEntryDBO> fineEntryMap = absenteesListTransaction.getFineEntry(datas.get(0).getAttendanceDate(),datas.get(0).getMorningTime(),null).stream().collect(Collectors.toMap(s -> s.getHostelAdmissionsDBO().getId(), s -> s));
			if(!Utils.isNullOrEmpty(datas)) {
				datas.forEach(dbo -> {
					if(Utils.isNullOrEmpty(dbo.getMorningTime())) {
						AbsenteesListDTO dto;
						if(!Utils.isNullOrEmpty(dbo.getHostelLeaveApplicationsDBO())) {
							if(!("morning".equalsIgnoreCase(dbo.getLeaveSession())) || Utils.isNullOrEmpty(dbo.getLeaveSession()) ) {
								dto = this.convertDBOToDTO(dbo);
								dto.setMorning(true);
								dto.setLeaveSession("Morning");
								dto.setAlertParent(!Utils.isNullOrEmpty(dbo.getParentErpWorkFlowProcessDBO()) ? true : false);
								dto.setAlreadyParentAlertSent(dto.getAlertParent() ? true : false);
								dto.setAlertStudent(!Utils.isNullOrEmpty(dbo.getStudentErpWorkFlowProcessDBO()) ? true : false);
								dto.setAlreadyStudentAlertSent(dto.getAlertStudent() ? true : false);
								dto.setAddToFine(fineEntryMap.containsKey(dbo.getHostelAdmissionsDBO().getId()) ? true : false);
								dto.setAlreadyFineExists(dto.getAddToFine() ? true : false);
								dtos.add(dto);
							} 
						} else if( !Utils.isNullOrEmpty(dbo.getHostelHolidayEventsDBO())) {
							if(!("morning".equalsIgnoreCase(dbo.getHolidayEventSession())) || Utils.isNullOrEmpty(dbo.getHolidayEventSession())) {
								dto = this.convertDBOToDTO(dbo);
								dto.setMorning(true);
								dto.setLeaveSession("Morning");
								dto.setAlertParent(!Utils.isNullOrEmpty(dbo.getParentErpWorkFlowProcessDBO()) ? true : false);
								dto.setAlreadyParentAlertSent(dto.getAlertParent() ? true : false);
								dto.setAlertStudent(!Utils.isNullOrEmpty(dbo.getStudentErpWorkFlowProcessDBO()) ? true : false);
								dto.setAlreadyStudentAlertSent(dto.getAlertStudent() ? true : false);
								dto.setAddToFine(fineEntryMap.containsKey(dbo.getHostelAdmissionsDBO().getId()) ? true : false);
								dto.setAlreadyFineExists(dto.getAddToFine() ? true : false);
								dtos.add(dto);
							}	
						} else if (!Utils.isNullOrEmpty(dbo.getHostelPunchingExemptionDBO())) {
							if(!("morning".equalsIgnoreCase(dbo.getExemptedSession())) || Utils.isNullOrEmpty(dbo.getExemptedSession())) {
								dto = this.convertDBOToDTO(dbo);
								dto.setMorning(true);
								dto.setLeaveSession("Morning");
								dto.setAlertParent(!Utils.isNullOrEmpty(dbo.getParentErpWorkFlowProcessDBO()) ? true : false);
								dto.setAlreadyParentAlertSent(dto.getAlertParent() ? true : false);
								dto.setAlertStudent(!Utils.isNullOrEmpty(dbo.getStudentErpWorkFlowProcessDBO()) ? true : false);
								dto.setAlreadyStudentAlertSent(dto.getAlertStudent() ? true : false);
								dto.setAddToFine(fineEntryMap.containsKey(dbo.getHostelAdmissionsDBO().getId()) ? true : false);
								dto.setAlreadyFineExists(dto.getAddToFine() ? true : false);
								dtos.add(dto);
							}
						} else {
							dto = this.convertDBOToDTO(dbo);
							dto.setMorning(true);
							dto.setLeaveSession("Morning");
							dto.setAlertParent(!Utils.isNullOrEmpty(dbo.getParentErpWorkFlowProcessDBO()) ? true : false);
							dto.setAlreadyParentAlertSent(dto.getAlertParent() ? true : false);
							dto.setAlertStudent(!Utils.isNullOrEmpty(dbo.getStudentErpWorkFlowProcessDBO()) ? true : false);
							dto.setAlreadyStudentAlertSent(dto.getAlertStudent() ? true : false);
							dto.setAddToFine(fineEntryMap.containsKey(dbo.getHostelAdmissionsDBO().getId()) ? true : false);
							dto.setAlreadyFineExists(dto.getAddToFine() ? true : false);
							dtos.add(dto);
						}	
					} else {
						AbsenteesListDTO dto;
						if(!Utils.isNullOrEmpty(dbo.getHostelLeaveApplicationsDBO())) {
							if(!("evening".equalsIgnoreCase(dbo.getLeaveSession())) || Utils.isNullOrEmpty(dbo.getLeaveSession()) ) {
								dto = this.convertDBOToDTO(dbo);
								dto.setEvening(true);
								dto.setLeaveSession("Evening");
								dto.setAlertParent(!Utils.isNullOrEmpty(dbo.getParentErpWorkFlowProcessDBO()) ? true : false);
								dto.setAlreadyParentAlertSent(dto.getAlertParent() ? true : false);
								dto.setAlertStudent(!Utils.isNullOrEmpty(dbo.getStudentErpWorkFlowProcessDBO()) ? true : false);
								dto.setAlreadyStudentAlertSent(dto.getAlertStudent() ? true : false);
								dto.setAddToFine(fineEntryMap.containsKey(dbo.getHostelAdmissionsDBO().getId()) ? true : false);
								dto.setAlreadyFineExists(dto.getAddToFine() ? true : false);
								dtos.add(dto);
							} 
						} else if( !Utils.isNullOrEmpty(dbo.getHostelHolidayEventsDBO())) {
							if(!("evening".equalsIgnoreCase(dbo.getHolidayEventSession())) || Utils.isNullOrEmpty(dbo.getHolidayEventSession())) {
								dto = this.convertDBOToDTO(dbo);
								dto.setEvening(true);
								dto.setLeaveSession("Evening");
								dto.setAlertParent(!Utils.isNullOrEmpty(dbo.getParentErpWorkFlowProcessDBO()) ? true : false);
								dto.setAlreadyParentAlertSent(dto.getAlertParent() ? true : false);
								dto.setAlertStudent(!Utils.isNullOrEmpty(dbo.getStudentErpWorkFlowProcessDBO()) ? true : false);
								dto.setAlreadyStudentAlertSent(dto.getAlertStudent() ? true : false);
								dto.setAddToFine(fineEntryMap.containsKey(dbo.getHostelAdmissionsDBO().getId()) ? true : false);
								dto.setAlreadyFineExists(dto.getAddToFine() ? true : false);
								dtos.add(dto);
							}	
						} else if (!Utils.isNullOrEmpty(dbo.getHostelPunchingExemptionDBO())) {
							if(!("evening".equalsIgnoreCase(dbo.getExemptedSession())) || Utils.isNullOrEmpty(dbo.getExemptedSession())) {
								dto = this.convertDBOToDTO(dbo);
								dto.setEvening(true);
								dto.setLeaveSession("Evening");
								dto.setAlertParent(!Utils.isNullOrEmpty(dbo.getParentErpWorkFlowProcessDBO()) ? true : false);
								dto.setAlreadyParentAlertSent(dto.getAlertParent() ? true : false);
								dto.setAlertStudent(!Utils.isNullOrEmpty(dbo.getStudentErpWorkFlowProcessDBO()) ? true : false);
								dto.setAlreadyStudentAlertSent(dto.getAlertStudent() ? true : false);
								dto.setAddToFine(fineEntryMap.containsKey(dbo.getHostelAdmissionsDBO().getId()) ? true : false);
								dto.setAlreadyFineExists(dto.getAddToFine() ? true : false);
								dtos.add(dto);
							}
						} else {
							dto = this.convertDBOToDTO(dbo);
							dto.setEvening(true);
							dto.setLeaveSession("Evening");
							dto.setAlertParent(!Utils.isNullOrEmpty(dbo.getParentErpWorkFlowProcessDBO()) ? true : false);
							dto.setAlreadyParentAlertSent(dto.getAlertParent() ? true : false);
							dto.setAlertStudent(!Utils.isNullOrEmpty(dbo.getStudentErpWorkFlowProcessDBO()) ? true : false);
							dto.setAlreadyStudentAlertSent(dto.getAlertStudent() ? true : false);
							dto.setAddToFine(fineEntryMap.containsKey(dbo.getHostelAdmissionsDBO().getId()) ? true : false);
							dto.setAlreadyFineExists(dto.getAddToFine() ? true : false);
							dtos.add(dto);
						}	
					}
				});
			}
		}
		return !Utils.isNullOrEmpty(dtos) ? Mono.just(dtos) : Mono.empty(); 
	}

	public AbsenteesListDTO convertDBOToDTO(HostelAttendanceDBO dbo) {
		AbsenteesListDTO dto = new AbsenteesListDTO();
		dto.setId(dbo.getId());
		dto.setRegNo(dbo.getHostelAdmissionsDBO().getStudentDBO().getRegisterNo());
		dto.setStudentPhotoUrl(dbo.getHostelAdmissionsDBO().getStudentDBO().getStudentPersonalDataId().getStudentPersonalDataAddtnlDBO().getProfilePhotoUrl());
		dto.setName(dbo.getHostelAdmissionsDBO().getStudentDBO().getStudentName());
		dto.setRoom(dbo.getHostelAdmissionsDBO().getHostelBedDBO().getHostelRoomsDBO().getRoomNo());
		dto.setBed(dbo.getHostelAdmissionsDBO().getHostelBedDBO().getBedNo());
		dto.setAdmissionId(dbo.getHostelAdmissionsDBO().getId());
		dto.setSelectedHostel(new SelectDTO());
		dto.getSelectedHostel().setValue(String.valueOf(dbo.getHostelAdmissionsDBO().getHostelDBO().getId()));
		dto.getSelectedHostel().setLabel(dbo.getHostelAdmissionsDBO().getHostelDBO().getHostelName());
		dto.setLeaveDate(dbo.getAttendanceDate());
		dto.setStudentEmail(dbo.getHostelAdmissionsDBO().getStudentDBO().getStudentPersonalEmailId());
		dto.setStudentPhoneNo(dbo.getHostelAdmissionsDBO().getStudentDBO().getStudentMobileNo());
		dto.setParentEmail(dbo.getHostelAdmissionsDBO().getStudentDBO().getStudentPersonalDataId().getStudentPersonalDataAddressDBO().getFatherEmail());
		dto.setParentPhoneNo(dbo.getHostelAdmissionsDBO().getStudentDBO().getStudentPersonalDataId().getStudentPersonalDataAddressDBO().getFatherMobileNo());
		dto.setProgramme(dbo.getHostelAdmissionsDBO().getStudentDBO().getErpCampusProgrammeMappingId().getErpProgrammeDBO().getProgrammeName());
		dto.setClassProgramme(dbo.getHostelAdmissionsDBO().getStudentDBO().getAcaClassDBO().getClassName());
		return dto;
	}

	public Flux<AbsenteesListDTO> getAbsenteeListofStudent(int yearId,int admissionId, int month) {
		return absenteesListTransaction.getAbsenteeListofStudent(yearId,admissionId,month).flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO1);
	}

	public AbsenteesListDTO convertDBOToDTO1(HostelAttendanceDBO dbo) {
		AbsenteesListDTO dto = new AbsenteesListDTO();
		dto.setLeaveDate(dbo.getAttendanceDate());
		dto.setSessionsList(new ArrayList<SelectDTO>());
		if(Utils.isNullOrEmpty(dbo.getMorningTime())) {
			if(!Utils.isNullOrEmpty(dbo.getHostelLeaveApplicationsDBO())) {
				if(!("morning".equalsIgnoreCase(dbo.getLeaveSession())) || Utils.isNullOrEmpty(dbo.getLeaveSession()) ) {
					SelectDTO select = new SelectDTO();
					select.setLabel("Morning");
					dto.getSessionsList().add(select);
				} 
			} else if( !Utils.isNullOrEmpty(dbo.getHostelHolidayEventsDBO())) {
				if(!("morning".equalsIgnoreCase(dbo.getHolidayEventSession())) || Utils.isNullOrEmpty(dbo.getHolidayEventSession())) {
					SelectDTO select = new SelectDTO();
					select.setLabel("Morning");
					dto.getSessionsList().add(select);
				}	
			} else if (!Utils.isNullOrEmpty(dbo.getHostelPunchingExemptionDBO())) {
				if(!("morning".equalsIgnoreCase(dbo.getExemptedSession())) || Utils.isNullOrEmpty(dbo.getExemptedSession())) {
					SelectDTO select = new SelectDTO();
					select.setLabel("Morning");
					dto.getSessionsList().add(select);
				}
			} else {
				SelectDTO select = new SelectDTO();
				select.setLabel("Morning");
				dto.getSessionsList().add(select);
			}
		}
		if(Utils.isNullOrEmpty(dbo.getEveningTime()))  {
			if(!Utils.isNullOrEmpty(dbo.getHostelLeaveApplicationsDBO())) {
				if(!("evening".equalsIgnoreCase(dbo.getLeaveSession())) || Utils.isNullOrEmpty(dbo.getLeaveSession()) ) {
					SelectDTO select = new SelectDTO();
					select.setLabel("Evening");
					dto.getSessionsList().add(select);
				} 
			} else if( !Utils.isNullOrEmpty(dbo.getHostelHolidayEventsDBO())) {
				if(!("evening".equalsIgnoreCase(dbo.getHolidayEventSession())) || Utils.isNullOrEmpty(dbo.getHolidayEventSession())) {
					SelectDTO select = new SelectDTO();
					select.setLabel("Evening");
					dto.getSessionsList().add(select);
				}	
			} else if (!Utils.isNullOrEmpty(dbo.getHostelPunchingExemptionDBO())) {
				if(!("evening".equalsIgnoreCase(dbo.getExemptedSession())) || Utils.isNullOrEmpty(dbo.getExemptedSession())) {
					SelectDTO select = new SelectDTO();
					select.setLabel("Evening");
					dto.getSessionsList().add(select);
				}
			} else {
				SelectDTO select = new SelectDTO();
				select.setLabel("Evening");
				dto.getSessionsList().add(select);
			}
		}
		return dto;
	}

	public Mono<ApiResult> save(Mono<List<AbsenteesListDTO>> dto, String userId) {
		List<String> code = new ArrayList<String>();
		code.add("HOSTEL_ATTENDANCE_ALERTED_PARENT");
		code.add("HOSTEL_ATTENDANCE_ALERTED_STUDENT");
		Map<String, ErpWorkFlowProcessNotificationsDBO> notificationMap = absenteesListTransaction.getErpNotifications(code).stream().collect(Collectors.toMap(s -> s.getNotificationCode(), s -> s));
		return dto.handle((AbsenteesListDTO, synchronousSink) ->  {
			synchronousSink.next(AbsenteesListDTO);
		}).cast(ArrayList.class)
				.map(data -> convertDTOToDBO(data,notificationMap,userId))
				.flatMap( s -> { absenteesListTransaction.save(s);              
				return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	public List<HostelAttendanceDBO> convertDTOToDBO(List<AbsenteesListDTO> dtos, Map<String, ErpWorkFlowProcessNotificationsDBO> notificationMap , String userId) {
		HostelFineCategoryDBO fineCategory = absenteesListTransaction.getFineCategory(dtos.get(0));
		List<HostelFineEntryDBO> fineList = new ArrayList<HostelFineEntryDBO>();
		List<ErpWorkFlowProcessStatusLogDBO> statusLogList = new ArrayList<ErpWorkFlowProcessStatusLogDBO>();
		Map<String, List<ErpEmailsDBO>> emailMap = new HashMap<String, List<ErpEmailsDBO>>();
		Map<String, List<ErpSmsDBO>> smsMap = new HashMap<String, List<ErpSmsDBO>>();
		Map<String, List<ErpNotificationsDBO>> notificationsMap = new HashMap<String, List<ErpNotificationsDBO>>();
		List<String> templateTypes = new ArrayList<String>();
		List<String> processCodes = new ArrayList<String>();
		Set<Integer>  approversIdSet = new HashSet<Integer>();
		approversIdSet.add(Integer.parseInt(userId));
		templateTypes.add("Mail");
		templateTypes.add("sms");
		List<String> templateNames = new ArrayList<String>();
		templateNames.add("HOSTEL_ATTENDANCE_ALERTED_PARENT_EMAIL");
		templateNames.add("HOSTEL_ATTENDANCE_ALERTED_PARENT_SMS");
		templateNames.add("HOSTEL_ATTENDANCE_ALERTED_STUDENT_EMAIL");
		templateNames.add("HOSTEL_ATTENDANCE_ALERTED_STUDENT_SMS");
		Map<Integer, HostelFineEntryDBO> fineEntryData = absenteesListTransaction.getFineEntry(dtos.get(0).getLeaveDate(), LocalTime.now(),dtos.get(0).getLeaveSession()).stream().collect(Collectors.toMap(s -> s.getHostelAdmissionsDBO().getId(), s -> s));
		Map<String, ErpTemplateDBO> templateMap = commonApiTransaction1.getErpTemplateByTemplateCodeAndTemplateType1(templateTypes, templateNames).stream().collect(Collectors.toMap(s -> s.getTemplateCode(), s -> s));
		Map<Integer, HostelAttendanceDBO>  valuesMap = absenteesListTransaction.getData(dtos.get(0).getLeaveDate(), dtos.get(0).getLeaveSession()).stream().collect(Collectors.toMap(s -> s.getHostelAdmissionsDBO().getId(), s -> s));
		Tuple parent = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("HOSTEL_ATTENDANCE_ALERTED_PARENT");
		Tuple student = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("HOSTEL_ATTENDANCE_ALERTED_STUDENT");
		List<HostelAttendanceDBO> dbos = new ArrayList<HostelAttendanceDBO>();
		List<ErpEmailsDBO> emailsList = new ArrayList<ErpEmailsDBO>();
		List<ErpSmsDBO> smsList = new ArrayList<ErpSmsDBO>();
		List<ErpNotificationsDBO> notificationList = new ArrayList<ErpNotificationsDBO>();
		List<ErpEmailsDBO> studentemailsList = new ArrayList<ErpEmailsDBO>();
		List<ErpSmsDBO> studentsmsList = new ArrayList<ErpSmsDBO>();
		List<ErpNotificationsDBO> studentnotificationList = new ArrayList<ErpNotificationsDBO>();
		dtos.forEach(dto -> {
			HostelAttendanceDBO dbo = valuesMap.get(dto.getAdmissionId());
			if(dto.getAlertParent()) {
				processCodes.add("HOSTEL_ATTENDANCE_ALERTED_PARENT");
				ErpWorkFlowProcessNotificationsDBO notifications = notificationMap.get("HOSTEL_ATTENDANCE_ALERTED_PARENT");
				if(notifications.isEmailActivated || notifications.isSmsActivated || notifications.isNotificationActivated) {
					dbo.setParentErpWorkFlowProcessDBO(new ErpWorkFlowProcessDBO());
					dbo.getParentErpWorkFlowProcessDBO().setId(Integer.parseInt(parent.get("erp_work_flow_process_id").toString()));
				}
				//email
				if(notifications.isEmailActivated) {
					ErpTemplateDBO erpTemplateDBO = templateMap.get("HOSTEL_ATTENDANCE_ALERTED_PARENT_EMAIL");
					if(!emailMap.containsKey("HOSTEL_ATTENDANCE_ALERTED_PARENT")) {
						emailsList.add(getEmailDBO(erpTemplateDBO, dto.getId(), userId,dto,"parent"));
						emailMap.put("HOSTEL_ATTENDANCE_ALERTED_PARENT", emailsList);
					} else {
						List<ErpEmailsDBO> emailsList1 = emailMap.get("HOSTEL_ATTENDANCE_ALERTED_PARENT");
						emailsList1.add(getEmailDBO(erpTemplateDBO, dto.getId(), userId,dto,"parent"));
						emailMap.replace("HOSTEL_ATTENDANCE_ALERTED_PARENT", emailsList1);
					}

				} 
				//sms
				if(notifications.isSmsActivated) {
					ErpTemplateDBO erpTemplateDBO = templateMap.get("HOSTEL_ATTENDANCE_ALERTED_PARENT_SMS");
					if(!smsMap.containsKey("HOSTEL_ATTENDANCE_ALERTED_PARENT")) {
						smsList.add(getSMSDBO(erpTemplateDBO, dto.getId(), userId, dto,"parent"));
						smsMap.put("HOSTEL_ATTENDANCE_ALERTED_PARENT", smsList);
					} else {
						List<ErpSmsDBO> smsList1 = smsMap.get("HOSTEL_ATTENDANCE_ALERTED_PARENT");
						smsList1.add(getSMSDBO(erpTemplateDBO, dto.getId(), userId, dto,"parent"));
						smsMap.replace("HOSTEL_ATTENDANCE_ALERTED_PARENT", smsList1);
					}
				}
				//notification
				if(notifications.isNotificationActivated) {
					if(!notificationsMap.containsKey("HOSTEL_ATTENDANCE_ALERTED_PARENT")) {
						notificationList.add(getNotificationsDBO( dto.getId(), Integer.parseInt(userId)));
						notificationsMap.put("HOSTEL_ATTENDANCE_ALERTED_PARENT", notificationList);
					} else {
						List<ErpNotificationsDBO> notificationList1 = notificationsMap.get("HOSTEL_ATTENDANCE_ALERTED_PARENT");
						notificationList1.add(getNotificationsDBO( dto.getId(), Integer.parseInt(userId)));
						notificationsMap.replace("HOSTEL_ATTENDANCE_ALERTED_PARENT", notificationList1);
					}
				}
				ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
				erpWorkFlowProcessStatusLogDBO.setEntryId(dto.getId());
				erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(new ErpWorkFlowProcessDBO());
				erpWorkFlowProcessStatusLogDBO.getErpWorkFlowProcessDBO().setId(Integer.parseInt(parent.get("erp_work_flow_process_id").toString()));
				erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
				erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
				statusLogList.add(erpWorkFlowProcessStatusLogDBO);
			}
			if(dto.getAlertStudent()) {
				processCodes.add("HOSTEL_ATTENDANCE_ALERTED_STUDENT");
				ErpWorkFlowProcessNotificationsDBO notifications = notificationMap.get("HOSTEL_ATTENDANCE_ALERTED_STUDENT");
				if(notifications.isEmailActivated || notifications.isSmsActivated ||  notifications.isNotificationActivated) {
					dbo.setStudentErpWorkFlowProcessDBO(new ErpWorkFlowProcessDBO());
					dbo.getStudentErpWorkFlowProcessDBO().setId(Integer.parseInt(student.get("erp_work_flow_process_id").toString()));
				}
				//email
				if(notifications.isEmailActivated) {
					ErpTemplateDBO erpTemplateDBO = templateMap.get("HOSTEL_ATTENDANCE_ALERTED_STUDENT_EMAIL");
					if(!emailMap.containsKey("HOSTEL_ATTENDANCE_ALERTED_STUDENT")) {
						studentemailsList.add(getEmailDBO(erpTemplateDBO, dto.getId(), userId, dto,"student"));
						emailMap.put("HOSTEL_ATTENDANCE_ALERTED_STUDENT", studentemailsList);
					} else {
						List<ErpEmailsDBO> studentemailsList1 = emailMap.get("HOSTEL_ATTENDANCE_ALERTED_STUDENT");
						studentemailsList1.add(getEmailDBO(erpTemplateDBO, dto.getId(), userId, dto,"student"));
						emailMap.replace("HOSTEL_ATTENDANCE_ALERTED_STUDENT", studentemailsList1);
					}
				} 
				//sms
				if(notifications.isSmsActivated) {
					ErpTemplateDBO erpTemplateDBO = templateMap.get("HOSTEL_ATTENDANCE_ALERTED_STUDENT_SMS");
					if(!smsMap.containsKey("HOSTEL_ATTENDANCE_ALERTED_STUDENT")) {
						studentsmsList.add(getSMSDBO(erpTemplateDBO, dto.getId(), userId, dto,"student"));
						smsMap.put("HOSTEL_ATTENDANCE_ALERTED_STUDENT", studentsmsList);
					} else {
						List<ErpSmsDBO> studentsmsList1 = smsMap.get("HOSTEL_ATTENDANCE_ALERTED_STUDENT");
						studentsmsList1.add(getSMSDBO(erpTemplateDBO, dto.getId(), userId, dto,"student"));
						smsMap.replace("HOSTEL_ATTENDANCE_ALERTED_STUDENT", studentsmsList1);
					}
				}
				//notification
				if(notifications.isNotificationActivated) {
					if(!notificationsMap.containsKey("HOSTEL_ATTENDANCE_ALERTED_STUDENT")) {
						studentnotificationList.add(getNotificationsDBO( dto.getId(), Integer.parseInt(userId)));
						notificationsMap.put("HOSTEL_ATTENDANCE_ALERTED_STUDENT", studentnotificationList);
					} else {
						List<ErpNotificationsDBO> studentnotificationList1 = notificationsMap.get("HOSTEL_ATTENDANCE_ALERTED_STUDENT");
						studentnotificationList1.add(getNotificationsDBO( dto.getId(), Integer.parseInt(userId)));
						notificationsMap.replace("HOSTEL_ATTENDANCE_ALERTED_STUDENT", studentnotificationList1);
					}
				}
				ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
				erpWorkFlowProcessStatusLogDBO.setEntryId(dto.getId());
				erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(new ErpWorkFlowProcessDBO());
				erpWorkFlowProcessStatusLogDBO.getErpWorkFlowProcessDBO().setId(Integer.parseInt(student.get("erp_work_flow_process_id").toString()));
				erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
				erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
				statusLogList.add(erpWorkFlowProcessStatusLogDBO);
			}
			if(dto.getAddToFine()) {
				if(!fineEntryData.containsKey(dto.getAdmissionId())) {
					HostelFineEntryDBO fine = new HostelFineEntryDBO();
					fine.setHostelAdmissionsDBO(new HostelAdmissionsDBO());
					fine.getHostelAdmissionsDBO().setId(dbo.getHostelAdmissionsDBO().getId());
					fine.setHostelFineCategoryDBO(new HostelFineCategoryDBO());
					fine.getHostelFineCategoryDBO().setId(fineCategory.getId());
					fine.setFineAmount(fineCategory.getFineAmount());
					LocalDate date =  dto.getLeaveDate();
					fine.setDate(date);
					if(dto.getLeaveSession().equalsIgnoreCase("morning")) {
						fine.setMorningHostelAttendanceDBO(dbo);
					} else {
						fine.setEveningHostelAttendanceDBO(dbo);
					}
					fine.setRecordStatus('A');
					fine.setCreatedUsersId(Integer.parseInt(userId));
					fineList.add(fine);
				}
			}
			dbos.add(dbo);
		});
		processCodes.forEach( code ->{
			Integer workFlowProcessId = code.equalsIgnoreCase("HOSTEL_ATTENDANCE_ALERTED_PARENT") ? (Integer)parent.get("erp_work_flow_process_id") : (Integer)student.get("erp_work_flow_process_id");
			String notificationCode = code.equalsIgnoreCase("HOSTEL_ATTENDANCE_ALERTED_PARENT") ? "HOSTEL_ATTENDANCE_ALERTED_PARENT" : "HOSTEL_ATTENDANCE_ALERTED_STUDENT";
			List<ErpNotificationsDBO> notificationList1 = notificationsMap.get(notificationCode);
			List<ErpSmsDBO> listsms = smsMap.get(notificationCode);
			List<ErpEmailsDBO> Listemail = emailMap.get(notificationCode);
			commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId( workFlowProcessId,notificationCode,approversIdSet,notificationList1,listsms,Listemail);
		});
		absenteesListTransaction.saveFine(fineList);
		commonApiTransaction1.saveErpWorkFlowProcessStatusLogDBO1(statusLogList);
		return dbos;
	}

	private ErpEmailsDBO getEmailDBO(ErpTemplateDBO erpTemplateDBO, Integer entryId, String userId, AbsenteesListDTO dto, String  type) {
		ErpEmailsDBO erpEmailsDBO = new ErpEmailsDBO();
		erpEmailsDBO.entryId = entryId;
		ErpUsersDBO erpUsersDBO1 = new ErpUsersDBO();
		erpUsersDBO1.id = Integer.parseInt(userId);
		erpEmailsDBO.erpUsersDBO = erpUsersDBO1;
		String msgBody = erpTemplateDBO.getTemplateContent();
		msgBody = msgBody.replace("[STUDENT_NAME]", dto.getName());
		msgBody = msgBody.replace("[REGISTER_NO]", dto.getRegNo());
		msgBody = msgBody.replace("[HOSTEL_NAME]", dto.getSelectedHostel().getLabel());
		msgBody = msgBody.replace("[DATE]", Utils.convertLocalDateToStringDate(dto.getLeaveDate()));
		msgBody = msgBody.replace("[SESSION_NAME]", dto.getLeaveSession().equalsIgnoreCase("morning") ? "Morning Session":"Evening Session");
		erpEmailsDBO.emailContent = msgBody;
		if(type.equals("parent")) {
			erpEmailsDBO.recipientEmail =dto.getParentEmail();
		}
		if(type.equals("student")){
			erpEmailsDBO.recipientEmail =dto.getStudentEmail();
		}
		if(!Utils.isNullOrEmpty(erpTemplateDBO.getMailSubject()))
			erpEmailsDBO.emailSubject = erpTemplateDBO.getMailSubject();
		if(!Utils.isNullOrEmpty(erpTemplateDBO.getMailFromName()))
			erpEmailsDBO.senderName = erpTemplateDBO.getMailFromName();
		erpEmailsDBO.createdUsersId = Integer.parseInt(userId);
		erpEmailsDBO.recordStatus = 'A';
		return erpEmailsDBO;
	}

	private ErpSmsDBO getSMSDBO(ErpTemplateDBO erpTemplateDBO1, Integer entryId, String userId, AbsenteesListDTO dto,String  type) {
		ErpSmsDBO erpSmsDBO = new ErpSmsDBO();
		erpSmsDBO.entryId = entryId;
		ErpUsersDBO erpUsersDBO1 = new ErpUsersDBO();
		erpUsersDBO1.id =Integer.parseInt(userId);
		erpSmsDBO.erpUsersDBO = erpUsersDBO1;
		String msgBody = erpTemplateDBO1.getTemplateContent();
		msgBody = msgBody.replace("[STUDENT_NAME]", dto.getName());
		msgBody = msgBody.replace("[REGISTER_NO]", dto.getRegNo());
		msgBody = msgBody.replace("[HOSTEL_NAME]", dto.getSelectedHostel().getLabel());
		msgBody = msgBody.replace("[DATE]", Utils.convertLocalDateToStringDate(dto.getLeaveDate()));
		msgBody = msgBody.replace("[SESSION_NAME]", dto.getLeaveSession().equalsIgnoreCase("morning") ? "Morning Session":"Evening Session");
		erpSmsDBO.smsContent = msgBody;
		if(type.equals("parent")) {
			erpSmsDBO.recipientMobileNo = dto.getParentPhoneNo();
		}
		if(type.equals("student")){
			erpSmsDBO.recipientMobileNo = dto.getStudentPhoneNo();
		}
		if(!Utils.isNullOrEmpty(erpTemplateDBO1.getTemplateId()))
			erpSmsDBO.setTemplateId(erpTemplateDBO1.getTemplateId());
		erpSmsDBO.createdUsersId = Integer.parseInt(userId);
		erpSmsDBO.recordStatus = 'A';
		return erpSmsDBO;
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

}