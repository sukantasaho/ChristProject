package com.christ.erp.services.handlers.employee.recruitment;

import java.math.BigInteger;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.AWSS3FileStorageService;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.RedisSysPropertiesData;
import com.christ.erp.services.common.SysProperties;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;
import com.christ.erp.services.dbobjects.common.ErpEmailsDBO;
import com.christ.erp.services.dbobjects.common.ErpLocationDBO;
import com.christ.erp.services.dbobjects.common.ErpNotificationsDBO;
import com.christ.erp.services.dbobjects.common.ErpSmsDBO;
import com.christ.erp.services.dbobjects.common.ErpUsersDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessNotificationsDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnInterviewPanelDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnInterviewSchedulesDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnInterviewScoreDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpInterviewUniversityExternalsDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.onboarding.EmpApplnEntriesDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnInterviewSchedulesDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnInterviewScoreDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnInterviewScoreDetailsDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.common.CommonApiHandler;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.employee.recruitment.InterviewProcessTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class InterviewProcessHandler {

	private static volatile InterviewProcessHandler shortlistingOfApplicationHandler = null;

	public static InterviewProcessHandler getInstance() {
		if (shortlistingOfApplicationHandler == null) {
			shortlistingOfApplicationHandler = new InterviewProcessHandler();
		}
		return shortlistingOfApplicationHandler;
	}

	@Autowired
	InterviewProcessTransaction interviewProcessTransaction;;

	@Autowired
	CommonApiTransaction commonApiTransaction;;

	@Autowired
	InterviewProcessTransaction interviewProcessTransaction1;

	@Autowired
	CommonApiTransaction commonApiTransaction1;

	@Autowired
	CommonApiHandler commonApiHandler;

	@Autowired
	RedisSysPropertiesData redisSysPropertiesData;
	
	@Autowired
	AWSS3FileStorageService aWSS3FileStorageService;

	public boolean applicantShortlisted(List<EmpApplnEntriesDTO> empApplnEntriesDTOs,String userId,String departmentId,String locationId) {
		boolean isSaved = true;
		List<Object> objList = new ArrayList<Object>();
		Integer empId = interviewProcessTransaction1.getEmpId(Integer.parseInt(userId));
		List<Integer> applicationIds = !Utils.isNullOrEmpty(empApplnEntriesDTOs) ? empApplnEntriesDTOs.stream()
				.filter(dtos -> !Utils.isNullOrEmpty(dtos.getApplicantId()))
				.map(dtos -> Integer.parseInt(dtos.getApplicantId()))
				.collect(Collectors.toList()) : null;		
		List<EmpApplnEntriesDBO> empApplnEntriesDBOList = !Utils.isNullOrEmpty(applicationIds) ? interviewProcessTransaction1.getApplicantsDetails(applicationIds) : null;
		Map<Integer,EmpApplnEntriesDBO> empApplnEntriesDBOMap  = !Utils.isNullOrEmpty(empApplnEntriesDBOList) ? empApplnEntriesDBOList.stream().collect(Collectors.toMap(s -> s.getId(), s -> s)) : new HashMap<Integer, EmpApplnEntriesDBO>();
		Tuple workFlowProcess = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("EMP_STAGE1_HOD_SHORTLISTED");
		int workflowProcessId1 = 0;
		try {
			for(EmpApplnEntriesDTO empApplnEntriesDTO: empApplnEntriesDTOs) {
				if(empApplnEntriesDBOMap.containsKey(Integer.parseInt(empApplnEntriesDTO.getApplicantId()))) {
					EmpApplnEntriesDBO empApplnEntriesDBO = empApplnEntriesDBOMap.get(Integer.parseInt(empApplnEntriesDTO.getApplicantId()));
					if(!Utils.isNullOrEmpty(workFlowProcess)) {
						int workflowProcessId = Integer.parseInt(String.valueOf(workFlowProcess.get("erp_work_flow_process_id")));
						workflowProcessId1 = workflowProcessId;
						ErpWorkFlowProcessDBO erpWorkFlowProcessDBO = commonApiTransaction1.getErpWorkFlowProcess(workflowProcessId);
						if(!Utils.isNullOrEmpty(erpWorkFlowProcessDBO.getApplicationStatusDisplayText())) {
							empApplnEntriesDBO.setApplicationStatusTime(LocalDateTime.now());
							empApplnEntriesDBO.setApplicationCurrentProcessStatus(erpWorkFlowProcessDBO);
						}
					}
					if(!Utils.isNullOrEmpty(empId)) {
						empApplnEntriesDBO.setShortlistedEmployeeId(new EmpDBO());
						empApplnEntriesDBO.getShortlistedEmployeeId().setId(empId);
					}
					empApplnEntriesDBO.setIsShortlistedForInterview(true);
					empApplnEntriesDBO.setModifiedUsersId(Integer.parseInt(userId));
					if(!Utils.isNullOrEmpty(departmentId)) {
						empApplnEntriesDBO.setShortlistedDepartmentId(new ErpDepartmentDBO());
						empApplnEntriesDBO.getShortlistedDepartmentId().setId(Integer.parseInt(departmentId));
					}
					if(!Utils.isNullOrEmpty(locationId)) {
						empApplnEntriesDBO.setShortistedLocationId(new ErpLocationDBO());
						empApplnEntriesDBO.getShortistedLocationId().setId(Integer.parseInt(locationId));
					}
					objList.add(empApplnEntriesDBO);
					ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
					erpWorkFlowProcessStatusLogDBO.setEntryId(empApplnEntriesDBO.getId());
					erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
					erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
					erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(new ErpWorkFlowProcessDBO());
					erpWorkFlowProcessStatusLogDBO.getErpWorkFlowProcessDBO().setId(workflowProcessId1);
					objList.add(erpWorkFlowProcessStatusLogDBO);
				}
			}
			if(!Utils.isNullOrEmpty(objList)) {
				interviewProcessTransaction1.saveOrUpdate(objList);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isSaved;
	}

	public boolean submitInterviewScheduleApproval(List<EmpApplnEntriesDTO> empApplnEntriesDTOList,String userId) { 
		List<Object> objects = new ArrayList<Object>();
		boolean isSaved = true;
		List<Integer> applicationIds = !Utils.isNullOrEmpty(empApplnEntriesDTOList) ? empApplnEntriesDTOList.stream()
				.map(dto -> Integer.parseInt(dto.getApplicantId()))
				.collect(Collectors.toList()) : new ArrayList<Integer>();
		List<Integer> interviewScheduleIds = !Utils.isNullOrEmpty(empApplnEntriesDTOList) ? empApplnEntriesDTOList.stream().filter(s -> !Utils.isNullOrEmpty(s.getEmpApplnInterviewSchedulesDTO().getId()))
				.map(dto -> Integer.parseInt(dto.getEmpApplnInterviewSchedulesDTO().getId()))
				.collect(Collectors.toList()): new ArrayList<>();
		List<Integer> contactAndInternal = new ArrayList<Integer>();
		List<Integer> contactExternal = new ArrayList<Integer>();
		if(!Utils.isNullOrEmpty(empApplnEntriesDTOList)) {
			empApplnEntriesDTOList.forEach(employeeIds -> {
				if(!Utils.isNullOrEmpty(employeeIds.getEmpApplnInterviewSchedulesDTO())) {
					if(!Utils.isNullOrEmpty(employeeIds.getEmpApplnInterviewSchedulesDTO().getPointOfContactUsersId())) {
						contactAndInternal.add(Integer.parseInt(employeeIds.getEmpApplnInterviewSchedulesDTO().getPointOfContactUsersId()));
					}
					if(!Utils.isNullOrEmpty(employeeIds.getEmpApplnInterviewSchedulesDTO().getInternalPanelists())) {
						employeeIds.getEmpApplnInterviewSchedulesDTO().getInternalPanelists().forEach(internal -> {
							if(!Utils.isNullOrEmpty(internal.getValue())) {
								contactAndInternal.add(Integer.parseInt(internal.getValue()));
							}
						});
					}
					if(!Utils.isNullOrEmpty(employeeIds.getEmpApplnInterviewSchedulesDTO().getExternalPanelists())) {
						employeeIds.getEmpApplnInterviewSchedulesDTO().getExternalPanelists().forEach(external -> {
							if(!Utils.isNullOrEmpty(external.status)) {
								if(external.status) {
									if(!Utils.isNullOrEmpty(external.getValue())) {
										contactAndInternal.add(Integer.parseInt(external.getValue()));
									}
								}else {
									if(!Utils.isNullOrEmpty(external.getValue())) {
										contactExternal.add(Integer.parseInt(external.getValue()));
									}
								}
							}
						});
					}
				}
			});
		}
		Integer empId = interviewProcessTransaction1.getEmpId(Integer.parseInt(userId));
		List<Tuple> tuple = !Utils.isNullOrEmpty(contactAndInternal) ? interviewProcessTransaction1.getContactAndInternal(contactAndInternal) : new ArrayList<Tuple>();
		
		List<Tuple> external = !Utils.isNullOrEmpty(contactExternal) ? interviewProcessTransaction1.getExternal(contactExternal) : new ArrayList<Tuple>();
		Map<Integer,Tuple> externalMap = !Utils.isNullOrEmpty(external) ? external.stream().collect(Collectors.toMap(s -> Integer.parseInt(s.get("emp_interview_university_externals_id").toString()),  s -> s)) : new HashMap<Integer, Tuple>();
		
		Map<Integer,Tuple> tupleMapContacMap = !Utils.isNullOrEmpty(tuple) ? tuple.stream().collect(Collectors.toMap(s -> Integer.parseInt(s.get("userId").toString()),  s -> s)) : new HashMap<Integer, Tuple>();
		List<EmpApplnEntriesDBO> empApplnEntriesDBOList = !Utils.isNullOrEmpty(applicationIds) ? interviewProcessTransaction1.getEmpApplnEntriesDBO(applicationIds) : new ArrayList<EmpApplnEntriesDBO>();
		Map<Integer,EmpApplnEntriesDBO> empApplnEntriesDBOMap  = !Utils.isNullOrEmpty(empApplnEntriesDBOList) ? empApplnEntriesDBOList.stream().collect(Collectors.toMap(s -> s.getId(), s -> s)) : new HashMap<Integer, EmpApplnEntriesDBO>();
		List<EmpApplnInterviewSchedulesDBO> empApplnInterviewSchedulesDBOList = !Utils.isNullOrEmpty(interviewScheduleIds) ? interviewProcessTransaction1.getInterviewScheduleDetails(interviewScheduleIds) : new ArrayList<EmpApplnInterviewSchedulesDBO>();
		Map<Integer, EmpApplnInterviewSchedulesDBO> empApplnInterviewSchedulesDBOMap = !Utils.isNullOrEmpty(empApplnInterviewSchedulesDBOList) ? empApplnInterviewSchedulesDBOList.stream().collect(Collectors.toMap(s -> s.getEmpApplnInterviewSchedulesId(), s -> s)) : new HashMap<Integer, EmpApplnInterviewSchedulesDBO>();
		List<ErpEmailsDBO> emailsListApplicant = new ArrayList<ErpEmailsDBO>();
		List<ErpSmsDBO> smsListApplicant = new ArrayList<ErpSmsDBO>();
		List<ErpNotificationsDBO> notificationListApplicant = new ArrayList<ErpNotificationsDBO>();
		List<ErpEmailsDBO> emailsListEmployee = new ArrayList<ErpEmailsDBO>();
		List<ErpSmsDBO> smsListEmployee = new ArrayList<ErpSmsDBO>();
		List<ErpNotificationsDBO> notificationListEmployee = new ArrayList<ErpNotificationsDBO>();
		Tuple workFlowProcess = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("EMP_STAGE1_PO_APPROVES_SCHEDULE");
		String notificationCodeApplicant = "STAGE1_PO_APPROVAL_INFORM_APPLICANT";
		String notificationCodeEmployee = "STAGE1_PO_APPROVAL_INFORM_PANELIST";
		int	workflowProcessId = Integer.parseInt(workFlowProcess.get("erp_work_flow_process_id").toString());
		ErpWorkFlowProcessNotificationsDBO erpWorkFlowProcessNotificationsDBOApplicant = interviewProcessTransaction1.getErpWorkFlowProcessNotification(workflowProcessId,notificationCodeApplicant);
		ErpWorkFlowProcessNotificationsDBO erpWorkFlowProcessNotificationsDBOEmployee = interviewProcessTransaction1.getErpWorkFlowProcessNotification(workflowProcessId,notificationCodeEmployee);
		if(!Utils.isNullOrEmpty(empApplnEntriesDTOList)) {
			empApplnEntriesDTOList.forEach(dto -> {
				if(empApplnEntriesDBOMap.containsKey(Integer.parseInt(dto.getApplicantId()))) {
					if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO())) {
						EmpApplnEntriesDBO empApplnEntriesDBO = empApplnEntriesDBOMap.get(Integer.parseInt(dto.getApplicantId()));
						if(!Utils.isNullOrEmpty(workflowProcessId)) {
							empApplnEntriesDBO.setApplicationCurrentProcessStatus(new ErpWorkFlowProcessDBO());
							empApplnEntriesDBO.getApplicationCurrentProcessStatus().setId(workflowProcessId);
							empApplnEntriesDBO.setApplicationStatusTime(LocalDateTime.now());
							empApplnEntriesDBO.setApplicantCurrentProcessStatus(new ErpWorkFlowProcessDBO());
							empApplnEntriesDBO.getApplicantCurrentProcessStatus().setId(workflowProcessId);
							empApplnEntriesDBO.setApplicantStatusTime(LocalDateTime.now());
						}
						empApplnEntriesDBO.setModifiedUsersId(Integer.parseInt(userId));
						EmpApplnInterviewSchedulesDBO empApplnInterviewSchedulesDBO = null;
						if(empApplnInterviewSchedulesDBOMap.containsKey(Integer.parseInt(dto.getEmpApplnInterviewSchedulesDTO().getId()))) {
							empApplnInterviewSchedulesDBO = empApplnInterviewSchedulesDBOMap.get(Integer.parseInt(dto.getEmpApplnInterviewSchedulesDTO().getId()));
							empApplnInterviewSchedulesDBO.setModifiedUsersId(Integer.parseInt(userId));
							empApplnInterviewSchedulesDBOMap.remove(Integer.parseInt(dto.getEmpApplnInterviewSchedulesDTO().getId()));
						}else {
							empApplnInterviewSchedulesDBO = new EmpApplnInterviewSchedulesDBO();
							empApplnInterviewSchedulesDBO.setCreatedUsersId(Integer.parseInt(userId));
							empApplnInterviewSchedulesDBO.setRecordStatus('A');
						}
						empApplnInterviewSchedulesDBO.setIsApproved(true);
						if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getInterviewDateTime1())) {
							empApplnInterviewSchedulesDBO.setInterviewDateTime(dto.getEmpApplnInterviewSchedulesDTO().getInterviewDateTime1());
						}						
						if(!Utils.isNullOrEmpty(empId))
							empApplnInterviewSchedulesDBO.setApprovedBy(empId);
//						if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getPointOfContactUsersId())) {
//							empApplnInterviewSchedulesDBO.setErpUsersDBO(new ErpUsersDBO());
//							empApplnInterviewSchedulesDBO.getErpUsersDBO().setId(Integer.parseInt(dto.getEmpApplnInterviewSchedulesDTO().getPointOfContactUsersId()));
//						}
						if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getPointofContact())) {
							if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getPointofContact().getValue())){
								empApplnInterviewSchedulesDBO.setErpUsersDBO(new ErpUsersDBO());
								empApplnInterviewSchedulesDBO.getErpUsersDBO().setId(Integer.parseInt(dto.getEmpApplnInterviewSchedulesDTO().getPointofContact().getValue()));
							}
						}
						if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getInterviewVenue())) {
							empApplnInterviewSchedulesDBO.setInterviewVenue(dto.getEmpApplnInterviewSchedulesDTO().getInterviewVenue());
						}
						if(Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getInterviewDateTime1())) {
							empApplnInterviewSchedulesDBO.setInterviewDateTime(dto.getEmpApplnInterviewSchedulesDTO().getInterviewDateTime1());
						}
						Map<Integer,EmpApplnInterviewPanelDBO> existEmpApplnInterviewPanelDBOMap = new HashMap<Integer, EmpApplnInterviewPanelDBO>();
						Set<EmpApplnInterviewPanelDBO> empApplnInterviewPanelDBOSet = new HashSet<EmpApplnInterviewPanelDBO>();
						if(!Utils.isNullOrEmpty(empApplnInterviewSchedulesDBO.getEmpApplnInterviewPanelDBO())) {
							empApplnInterviewSchedulesDBO.getEmpApplnInterviewPanelDBO().forEach(internalPanelListDbo -> {
								if(internalPanelListDbo.recordStatus == 'A') {
									existEmpApplnInterviewPanelDBOMap.put(internalPanelListDbo.id, internalPanelListDbo);
								}
							});
						}
						if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO())) {
							EmpApplnInterviewSchedulesDBO empApplnInterviewSchedulesDBO1 = empApplnInterviewSchedulesDBO;
							dto.getEmpApplnInterviewSchedulesDTO().getInternalPanelists().forEach(internalPanelListDto -> {
								EmpApplnInterviewPanelDBO empApplnInterviewPanelDBO = new EmpApplnInterviewPanelDBO();
								empApplnInterviewPanelDBO.setCreatedUsersId(Integer.parseInt(userId));
								empApplnInterviewPanelDBO.setRecordStatus('A');
								empApplnInterviewPanelDBO.setInternalPanel(true);
								empApplnInterviewPanelDBO.setErpUsersDBO(new ErpUsersDBO());
								empApplnInterviewPanelDBO.getErpUsersDBO().setId(Integer.parseInt(internalPanelListDto.getValue()));
								empApplnInterviewPanelDBO.setEmpApplnInterviewSchedulesDBO(empApplnInterviewSchedulesDBO1);
								empApplnInterviewPanelDBOSet.add(empApplnInterviewPanelDBO);
								if(tupleMapContacMap.containsKey(Integer.parseInt(internalPanelListDto.getValue()))) {
									Tuple point = tupleMapContacMap.get(Integer.parseInt(internalPanelListDto.getValue()));
									if(!Utils.isNullOrEmpty(point.get("email"))) {
										dto.setEmployeeEmail(point.get("email").toString());
									}
									if(!Utils.isNullOrEmpty(point.get("mobileNo")) && !Utils.isNullOrEmpty(point.get("mobileCountry"))) {
										dto.setEmployeeMobileNo(point.get("mobileCountry").toString() + point.get("mobileNo").toString());
									}
									if(!Utils.isNullOrEmpty(point.get("empName"))){
										dto.setEmployeeName(point.get("empName").toString());
									}
									if(!Utils.isNullOrEmpty(erpWorkFlowProcessNotificationsDBOEmployee)) {
										if(erpWorkFlowProcessNotificationsDBOEmployee.getIsEmailActivated()) {
											ErpTemplateDBO erpTemplateDBO = erpWorkFlowProcessNotificationsDBOEmployee.getErpEmailsTemplateDBO();
											if(!Utils.isNullOrEmpty(erpTemplateDBO))
												emailsListEmployee.add(getEmailDBO(erpTemplateDBO,empApplnEntriesDBO.getId(),userId,dto,"Employee"));
										}
										if(erpWorkFlowProcessNotificationsDBOEmployee.getIsSmsActivated()) {
											ErpTemplateDBO erpTemplateDBO = erpWorkFlowProcessNotificationsDBOEmployee.getErpSmsTemplateDBO();
											if(!Utils.isNullOrEmpty(erpTemplateDBO))
												smsListEmployee.add(getSMSDBO(erpTemplateDBO,empApplnEntriesDBO.getId(),userId,dto,"Employee"));
										}
										if(erpWorkFlowProcessNotificationsDBOEmployee.getIsNotificationActivated()) {
											notificationListEmployee.add(getNotificationsDBO(empApplnEntriesDBO.getId(),Integer.parseInt(userId),"Employee",erpWorkFlowProcessNotificationsDBOEmployee));
										}
									}
								}
							});
						}
						if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getExternalPanelists())) {
							EmpApplnInterviewSchedulesDBO empApplnInterviewSchedulesDBO1 = empApplnInterviewSchedulesDBO;
							dto.getEmpApplnInterviewSchedulesDTO().getExternalPanelists().forEach(externalPanelListDto -> {
								if(!Utils.isNullOrEmpty(externalPanelListDto.status)) {
									if((externalPanelListDto.status == true & dto.getIsAcademic() == true) || (externalPanelListDto.status == false & dto.getIsAcademic() == false) || (externalPanelListDto.status == true & dto.getIsAcademic() == false)) {
										EmpApplnInterviewPanelDBO empApplnInterviewPanelDBO = new EmpApplnInterviewPanelDBO();
										empApplnInterviewPanelDBO.setCreatedUsersId(Integer.parseInt(userId));
										empApplnInterviewPanelDBO.setRecordStatus('A');
										empApplnInterviewPanelDBO.setErpUsersDBO(new ErpUsersDBO());
										empApplnInterviewPanelDBO.getErpUsersDBO().setId(Integer.parseInt(externalPanelListDto.getValue()));
										empApplnInterviewPanelDBO.setInternalPanel(false);
										empApplnInterviewPanelDBO.setEmpApplnInterviewSchedulesDBO(empApplnInterviewSchedulesDBO1);
										empApplnInterviewPanelDBOSet.add(empApplnInterviewPanelDBO);
										if(!Utils.isNullOrEmpty(dto.getApplicationStatus())) {
											if(tupleMapContacMap.containsKey(Integer.parseInt(externalPanelListDto.getValue()))) {
												Tuple point = tupleMapContacMap.get(Integer.parseInt(externalPanelListDto.getValue()));
												if(!Utils.isNullOrEmpty(point.get("email"))) {
													dto.setEmployeeEmail(point.get("email").toString());
												}
												if(!Utils.isNullOrEmpty(point.get("mobileNo")) && !Utils.isNullOrEmpty(point.get("mobileCountry"))) {
													dto.setEmployeeMobileNo(point.get("mobileCountry").toString() + point.get("mobileNo").toString());
												}
												if(!Utils.isNullOrEmpty(point.get("empName"))){
													dto.setEmployeeName(point.get("empName").toString());
												}
												if(!Utils.isNullOrEmpty(erpWorkFlowProcessNotificationsDBOEmployee)) {
													if(erpWorkFlowProcessNotificationsDBOEmployee.getIsEmailActivated()) {
														ErpTemplateDBO erpTemplateDBO = erpWorkFlowProcessNotificationsDBOEmployee.getErpEmailsTemplateDBO();
														if(!Utils.isNullOrEmpty(erpTemplateDBO))
															emailsListEmployee.add(getEmailDBO(erpTemplateDBO,empApplnEntriesDBO.getId(),userId,dto,"Employee"));
													}
													if(erpWorkFlowProcessNotificationsDBOEmployee.getIsSmsActivated()) {
														ErpTemplateDBO erpTemplateDBO = erpWorkFlowProcessNotificationsDBOEmployee.getErpSmsTemplateDBO();
														if(!Utils.isNullOrEmpty(erpTemplateDBO))
															smsListEmployee.add(getSMSDBO(erpTemplateDBO,empApplnEntriesDBO.getId(),userId,dto,"Employee"));
													}
													if(erpWorkFlowProcessNotificationsDBOEmployee.getIsNotificationActivated()) {
														notificationListEmployee.add(getNotificationsDBO(empApplnEntriesDBO.getId(),Integer.parseInt(userId),"Employee",erpWorkFlowProcessNotificationsDBOEmployee));
													}
												}
											}
										}
									}else {
										EmpApplnInterviewPanelDBO empApplnInterviewPanelDBO = new EmpApplnInterviewPanelDBO();
										empApplnInterviewPanelDBO.setCreatedUsersId(Integer.parseInt(userId));
										empApplnInterviewPanelDBO.setRecordStatus('A');
										empApplnInterviewPanelDBO.setEmpInterviewUniversityExternalsDBO(new EmpInterviewUniversityExternalsDBO());
										empApplnInterviewPanelDBO.getEmpInterviewUniversityExternalsDBO().setId(Integer.parseInt(externalPanelListDto.getValue()));
										empApplnInterviewPanelDBO.setInternalPanel(false);
										empApplnInterviewPanelDBO.setEmpApplnInterviewSchedulesDBO(empApplnInterviewSchedulesDBO1);
										empApplnInterviewPanelDBOSet.add(empApplnInterviewPanelDBO);
										if(!Utils.isNullOrEmpty(dto.getApplicationStatus())) {
											if(externalMap.containsKey(Integer.parseInt(externalPanelListDto.getValue()))) {
												Tuple point = externalMap.get(Integer.parseInt(externalPanelListDto.getValue()));
												if(!Utils.isNullOrEmpty(point.get("email"))) {
													dto.setEmployeeEmail(point.get("email").toString());
												}
												if(!Utils.isNullOrEmpty(point.get("mobileNo")) && !Utils.isNullOrEmpty(point.get("mobileCountry"))) {
													dto.setEmployeeMobileNo(point.get("mobileCountry").toString() + point.get("mobileNo").toString());
												}
												if(!Utils.isNullOrEmpty(point.get("empName"))){
													dto.setEmployeeName(point.get("empName").toString());
												}
												if(!Utils.isNullOrEmpty(erpWorkFlowProcessNotificationsDBOEmployee)) {
													if(erpWorkFlowProcessNotificationsDBOEmployee.getIsEmailActivated()) {
														ErpTemplateDBO erpTemplateDBO = erpWorkFlowProcessNotificationsDBOEmployee.getErpEmailsTemplateDBO();
														if(!Utils.isNullOrEmpty(erpTemplateDBO))
															emailsListEmployee.add(getEmailDBO(erpTemplateDBO,empApplnEntriesDBO.getId(),userId,dto,"Employee"));
													}
													if(erpWorkFlowProcessNotificationsDBOEmployee.getIsSmsActivated()) {
														ErpTemplateDBO erpTemplateDBO = erpWorkFlowProcessNotificationsDBOEmployee.getErpSmsTemplateDBO();
														if(!Utils.isNullOrEmpty(erpTemplateDBO))
															smsListEmployee.add(getSMSDBO(erpTemplateDBO,empApplnEntriesDBO.getId(),userId,dto,"Employee"));
													}
													if(erpWorkFlowProcessNotificationsDBOEmployee.getIsNotificationActivated()) {
														notificationListEmployee.add(getNotificationsDBO(empApplnEntriesDBO.getId(),Integer.parseInt(userId),"Employee",erpWorkFlowProcessNotificationsDBOEmployee));
													}
												}
											}
										}
									}
								}
							});
						}				
						if(!Utils.isNullOrEmpty(existEmpApplnInterviewPanelDBOMap)) {
							existEmpApplnInterviewPanelDBOMap.forEach((entry, value)-> {
								value.setModifiedUsersId(Integer.parseInt(userId));
								value.setRecordStatus('D');
								empApplnInterviewPanelDBOSet.add(value);
							});
						}
						empApplnInterviewSchedulesDBO.setEmpApplnInterviewPanelDBO(empApplnInterviewPanelDBOSet);
						empApplnInterviewSchedulesDBO.setEmpApplnEntriesDBO(empApplnEntriesDBO);
						Set<EmpApplnInterviewSchedulesDBO> empApplnInterviewSchedulesDBOSet = new HashSet<>();
						empApplnInterviewSchedulesDBOSet.add(empApplnInterviewSchedulesDBO); 
						empApplnEntriesDBO.setEmpApplnInterviewSchedulesDBOs(empApplnInterviewSchedulesDBOSet);
						objects.add(empApplnEntriesDBO);
						ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
						erpWorkFlowProcessStatusLogDBO.setEntryId(empApplnEntriesDBO.getId());
						erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
						erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
						erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(new ErpWorkFlowProcessDBO());
						erpWorkFlowProcessStatusLogDBO.getErpWorkFlowProcessDBO().setId(workflowProcessId);
						objects.add(erpWorkFlowProcessStatusLogDBO);
						if(!Utils.isNullOrEmpty(erpWorkFlowProcessNotificationsDBOApplicant)) {
							if(erpWorkFlowProcessNotificationsDBOApplicant.getIsEmailActivated()) {
								ErpTemplateDBO erpTemplateDBO = erpWorkFlowProcessNotificationsDBOApplicant.getErpEmailsTemplateDBO();
								if(!Utils.isNullOrEmpty(erpTemplateDBO))
									emailsListApplicant.add(getEmailDBO(erpTemplateDBO,empApplnEntriesDBO.getId(),userId,dto,null));
							}
							if(erpWorkFlowProcessNotificationsDBOApplicant.getIsSmsActivated()) {
								ErpTemplateDBO erpTemplateDBO = erpWorkFlowProcessNotificationsDBOApplicant.getErpSmsTemplateDBO();
								if(!Utils.isNullOrEmpty(erpTemplateDBO))
									smsListApplicant.add(getSMSDBO(erpTemplateDBO,empApplnEntriesDBO.getId(),userId,dto,null));
							}
							if(erpWorkFlowProcessNotificationsDBOApplicant.getIsNotificationActivated()) {
								notificationListApplicant.add(getNotificationsDBO(empApplnEntriesDBO.getId(),Integer.parseInt(userId),null,erpWorkFlowProcessNotificationsDBOApplicant));
							}
						}
					}
				}	
			});
		}
		isSaved=interviewProcessTransaction1.saveOrUpdate(objects);
		Set<Integer> approversIdSet = new HashSet<Integer>();
		approversIdSet.add(Integer.parseInt(userId));
		if(isSaved) {
			commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(workflowProcessId,"STAGE1_PO_APPROVAL_INFORM_APPLICANT",approversIdSet,notificationListApplicant,smsListApplicant,emailsListApplicant);
			commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(workflowProcessId,"STAGE1_PO_APPROVAL_INFORM_PANELIST",approversIdSet,notificationListEmployee,smsListEmployee,emailsListEmployee);
		}
		return isSaved;
	}

	private ErpEmailsDBO getEmailDBO(ErpTemplateDBO erpTemplateDBO, Integer entryId, String userId, EmpApplnEntriesDTO dto,String str) {
		ErpEmailsDBO erpEmailsDBO = new ErpEmailsDBO();
		erpEmailsDBO.entryId = entryId;
		ErpUsersDBO erpUsersDBO1 = new ErpUsersDBO();
		erpUsersDBO1.id = Integer.parseInt(userId);
		erpEmailsDBO.erpUsersDBO = erpUsersDBO1;
		String msgBody = erpTemplateDBO.getTemplateContent();
		if(Utils.isNullOrEmpty(str)) {
			msgBody = msgBody.replace("[EMP_APPLICANT_NAME]", dto.getApplicantName());
			msgBody = msgBody.replace("[APPLICATION_NO]", dto.getApplicantNumber());
			if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO()) && !Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getInterviewDateTime1())) {
//				msgBody = msgBody.replace("[INTERVIEW_DATE_TIME]", dto.getEmpApplnInterviewSchedulesDTO().getInterviewDateTime1().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
				msgBody = msgBody.replace("[INTERVIEW_DATE_TIME]", Utils.convertLocalDateTimeToStringDateTime(dto.getEmpApplnInterviewSchedulesDTO().getInterviewDateTime1()));
			}
			if(msgBody.contains("[VENUE]") && !Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO()) && !Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getInterviewVenue())) {
				msgBody = msgBody.replace("[VENUE]", dto.getEmpApplnInterviewSchedulesDTO().getInterviewVenue());
			}
			erpEmailsDBO.recipientEmail =dto.getApplicantEmail();
		}else {
			msgBody = msgBody.replace("[EMPLOYEE_NAME]", dto.getEmployeeName());
			if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO()) && !Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getInterviewDateTime1())) {
//				msgBody = msgBody.replace("[INTERVIEW_DATE_TIME]", dto.getEmpApplnInterviewSchedulesDTO().getInterviewDateTime1().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
				msgBody = msgBody.replace("[INTERVIEW_DATE_TIME]", Utils.convertLocalDateTimeToStringDateTime(dto.getEmpApplnInterviewSchedulesDTO().getInterviewDateTime1()));
			} 
			if(msgBody.contains("[EMP_APPLICANT_NAME]") && !Utils.isNullOrEmpty(dto.getApplicantName())) {
				msgBody = msgBody.replace("[EMP_APPLICANT_NAME]", dto.getApplicantName());
			}
			if(msgBody.contains("[APPLICATION_NO]") && !Utils.isNullOrEmpty(dto.getApplicantName())) {
				msgBody = msgBody.replace("[APPLICATION_NO]", dto.getApplicantNumber());
			}
			if(msgBody.contains("[VENUE]") && !Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO()) && !Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getInterviewVenue())) {
				msgBody = msgBody.replace("[VENUE]", dto.getEmpApplnInterviewSchedulesDTO().getInterviewVenue());
			}
			if(msgBody.contains("[DEPARTMENT]") && !Utils.isNullOrEmpty(dto.getShortlistedDepartment())) {
				msgBody = msgBody.replace("[DEPARTMENT]", dto.getShortlistedDepartment().getLabel());
			}
			erpEmailsDBO.recipientEmail =dto.getEmployeeEmail();
		}
		erpEmailsDBO.emailContent = msgBody;
		if(!Utils.isNullOrEmpty(erpTemplateDBO.getMailSubject()))
			erpEmailsDBO.emailSubject = erpTemplateDBO.getMailSubject();
		if(!Utils.isNullOrEmpty(erpTemplateDBO.getMailFromName()))
			erpEmailsDBO.senderName = erpTemplateDBO.getMailFromName();
		erpEmailsDBO.createdUsersId = Integer.parseInt(userId);
		erpEmailsDBO.recordStatus = 'A';
		return erpEmailsDBO;
	}

	private ErpSmsDBO getSMSDBO(ErpTemplateDBO erpTemplateDBO1, Integer entryId, String userId, EmpApplnEntriesDTO dto,String str) {
		ErpSmsDBO erpSmsDBO = new ErpSmsDBO();
		erpSmsDBO.entryId = entryId;
		ErpUsersDBO erpUsersDBO1 = new ErpUsersDBO();
		erpUsersDBO1.id =Integer.parseInt(userId);
		erpSmsDBO.erpUsersDBO = erpUsersDBO1;
		String msgBody = erpTemplateDBO1.getTemplateContent();
		if(Utils.isNullOrEmpty(str)) {
			msgBody = msgBody.replace("[EMP_APPLICANT_NAME]", dto.getApplicantName());
			msgBody = msgBody.replace("[APPLICATION_NO]", dto.getApplicantNumber());
//			msgBody = msgBody.replace("[INTERVIEW_DATE_TIME]", dto.getEmpApplnInterviewSchedulesDTO().getInterviewDateTime1().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
			msgBody = msgBody.replace("[INTERVIEW_DATE_TIME]", Utils.convertLocalDateTimeToStringDateTime(dto.getEmpApplnInterviewSchedulesDTO().getInterviewDateTime1()));
			if(msgBody.contains("[VENUE]") && !Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO()) && !Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getInterviewVenue())) {
				msgBody = msgBody.replace("[VENUE]", dto.getEmpApplnInterviewSchedulesDTO().getInterviewVenue());
			}
			erpSmsDBO.recipientMobileNo = dto.getApplicantMobile();
		}else {
			msgBody = msgBody.replace("[EMPLOYEE_NAME]", dto.getEmployeeName());
//			msgBody = msgBody.replace("[INTERVIEW_DATE_TIME]", dto.getEmpApplnInterviewSchedulesDTO().getInterviewDateTime1().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
			msgBody = msgBody.replace("[INTERVIEW_DATE_TIME]", Utils.convertLocalDateTimeToStringDateTime(dto.getEmpApplnInterviewSchedulesDTO().getInterviewDateTime1()));
			if(msgBody.contains("[EMP_APPLICANT_NAME]") && !Utils.isNullOrEmpty(dto.getApplicantName())) {
				msgBody = msgBody.replace("[EMP_APPLICANT_NAME]", dto.getApplicantName());
			}
			if(msgBody.contains("[APPLICATION_NO]") && !Utils.isNullOrEmpty(dto.getApplicantName())) {
				msgBody = msgBody.replace("[APPLICATION_NO]", dto.getApplicantNumber());
			}
			if(msgBody.contains("[VENUE]") && !Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO()) && !Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getInterviewVenue())) {
				msgBody = msgBody.replace("[VENUE]", dto.getEmpApplnInterviewSchedulesDTO().getInterviewVenue());
			}
			if(msgBody.contains("[DEPARTMENT]") && !Utils.isNullOrEmpty(dto.getShortlistedDepartment())) {
				msgBody = msgBody.replace("[DEPARTMENT]", dto.getShortlistedDepartment().getLabel());
			}
			erpSmsDBO.recipientMobileNo = dto.getEmployeeMobileNo();
		}
		if(!Utils.isNullOrEmpty(erpTemplateDBO1.getTemplateId()))
			erpSmsDBO.setTemplateId(erpTemplateDBO1.getTemplateId());
		erpSmsDBO.smsContent = msgBody;
		erpSmsDBO.createdUsersId = Integer.parseInt(userId);
		erpSmsDBO.recordStatus = 'A';
		return erpSmsDBO;
	}

	private ErpNotificationsDBO getNotificationsDBO(int entryId, int userId,String str, ErpWorkFlowProcessNotificationsDBO erpWorkFlowProcessNotificationsDBO) {
		ErpNotificationsDBO erpNotifications = new ErpNotificationsDBO();
		erpNotifications.entryId = entryId;
		ErpUsersDBO erpUsersDBO = new ErpUsersDBO();
		erpUsersDBO.id = userId;
		erpNotifications.setErpWorkFlowProcessNotificationsDBO(erpWorkFlowProcessNotificationsDBO);
		erpNotifications.erpUsersDBO = erpUsersDBO;
		erpNotifications.createdUsersId = userId;
		erpNotifications.recordStatus = 'A';
		return erpNotifications;
	}

	// need to uncomment after clearing redisSysProperties issue
	private ErpEmailsDBO getEmailDBOForPO(ErpWorkFlowProcessNotificationsDBO erpWorkFlowProcessNotificationsDBOSelectPo, String userId, ErpLocationDBO shortistedLocationId, EmpApplnEntriesDTO dto) {
		ErpEmailsDBO erpEmailsDBO = new ErpEmailsDBO();
				if(!Utils.isNullOrEmpty(erpWorkFlowProcessNotificationsDBOSelectPo)) {
					if(erpWorkFlowProcessNotificationsDBOSelectPo.getIsEmailActivated()) {
						ErpTemplateDBO erpTemplateDBO = erpWorkFlowProcessNotificationsDBOSelectPo.getErpEmailsTemplateDBO();
						String email = redisSysPropertiesData.getSysProperties(SysProperties.EMP_INTERVIEW_STAGE1_SELECTED_INTIMATION.name(), "L",shortistedLocationId.getId());
						erpEmailsDBO.entryId = Integer.parseInt(dto.getApplicantId());
						erpEmailsDBO.erpUsersDBO = null;
						String msgBody= null;
						Object content = erpTemplateDBO.getTemplateContent();
						if(content instanceof String) {
							String message = (String) content;
							msgBody = message.replace("[EMP_APPLICANT_NAME]",dto.getApplicantName());
							msgBody = msgBody.replace("[INTERVIEW_DATE_TIME]",Utils.convertLocalDateTimeToStringDateTime(dto.getEmpApplnInterviewSchedulesDTO().getInterviewDateTime1()));
							msgBody = msgBody.replace("[APPLICATION_NO]",dto.getApplicantNumber());
							if(msgBody.contains("[VENUE]") && !Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO()) && !Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getInterviewVenue())) {
								msgBody = msgBody.replace("[VENUE]", dto.getEmpApplnInterviewSchedulesDTO().getInterviewVenue());
							}
							if(msgBody.contains("[DEPARTMENT]") && !Utils.isNullOrEmpty(dto.getShortlistedDepartment())) {
								msgBody = msgBody.replace("[DEPARTMENT]", dto.getShortlistedDepartment().getLabel());
							}
						}else if (content instanceof Blob) {
							Blob blob = (Blob) content;
							byte[] bytes;
							try {
								bytes = blob.getBytes(1, (int) blob.length());
								String msg = new String(bytes);
								msgBody = msg.replace("[EMP_APPLICANT_NAME]",dto.getApplicantName());
								msgBody = msg.replace("[INTERVIEW_DATE_TIME]",dto.getInterviewDateTime());
								msgBody = msg.replace("[APPLICATION_NO]",dto.getApplicantNumber());
								if(msgBody.contains("[VENUE]") && !Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO()) && !Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getInterviewVenue())) {
									msgBody = msgBody.replace("[VENUE]", dto.getEmpApplnInterviewSchedulesDTO().getInterviewVenue());
								}
								if(msgBody.contains("[DEPARTMENT]") && !Utils.isNullOrEmpty(dto.getShortlistedDepartment())) {
									msgBody = msgBody.replace("[DEPARTMENT]", dto.getShortlistedDepartment().getLabel());
								}
							}catch (SQLException e) {
								e.printStackTrace();
							}
						}
						erpEmailsDBO.emailContent = msgBody;
						if(!Utils.isNullOrEmpty(erpTemplateDBO.getMailSubject()))
							erpEmailsDBO.emailSubject = erpTemplateDBO.getMailSubject();
						if(!Utils.isNullOrEmpty(erpTemplateDBO.getMailFromName()))
							erpEmailsDBO.senderName = erpTemplateDBO.getMailFromName();
						erpEmailsDBO.createdUsersId = Integer.parseInt(userId);
						erpEmailsDBO.recordStatus = 'A';
						erpEmailsDBO.recipientEmail = email;
					}
				}
		return erpEmailsDBO;
	}

	public List<EmpApplnEntriesDTO> getApplicants(EmpApplnEntriesDTO empApplnEntriesDTO,String departmentId,String locationId) throws Exception {
		List<Integer> idList =  new ArrayList<Integer>();
		if(!Utils.isNullOrEmpty(empApplnEntriesDTO)) {
			if(!Utils.isNullOrEmpty(empApplnEntriesDTO.getResearch())) {
				empApplnEntriesDTO.getResearch().forEach(data -> {
					if(!Utils.isNullOrEmpty(data.getValue())) {
						idList.add(Integer.parseInt(data.getValue()));
					}
				});
			}
		}
		List<EmpApplnEntriesDTO> empApplnEntriesDTOList = new ArrayList<>();
		Set<Integer> duplicate = new HashSet<Integer>();
		List<Tuple> list = interviewProcessTransaction.getApplicants(empApplnEntriesDTO,idList);
		for (Tuple tuple : list) {
			EmpApplnEntriesDTO empApplnEntriesDTO1 = new EmpApplnEntriesDTO();
			if(!Utils.isNullOrEmpty(empApplnEntriesDTO)) {
				if(!Utils.isNullOrEmpty(empApplnEntriesDTO.getResearch())) {
					empApplnEntriesDTO.getResearch().forEach(data -> {
						if(!Utils.isNullOrEmpty(data.getValue())) {
							if(!Utils.isNullOrEmpty(tuple.get("reserchId")) && !Utils.isNullOrEmpty(tuple.get("userEnteredValue"))) {
								if(Integer.parseInt(data.getValue()) == Integer.parseInt(tuple.get("reserchId").toString())) {
									if(Integer.parseInt(data.getLabel()) <= Integer.parseInt(tuple.get("userEnteredValue").toString())) {
										if(!Utils.isNullOrEmpty(tuple.get("Id"))) {
											if(!duplicate.contains(Integer.parseInt(tuple.get("Id").toString()))) {
												duplicate.add(Integer.parseInt(tuple.get("Id").toString()));
												empApplnEntriesDTOList.add(getDate(empApplnEntriesDTO1,tuple));
											}
										}
									}
								}
							}
						}
					});
				}else {
					if(!Utils.isNullOrEmpty(tuple.get("Id"))) {
						if(!duplicate.contains(Integer.parseInt(tuple.get("Id").toString()))) {
							duplicate.add(Integer.parseInt(tuple.get("Id").toString()));
							empApplnEntriesDTOList.add(getDate(empApplnEntriesDTO1,tuple));
						}
					}
				}
			}
		}
		return empApplnEntriesDTOList;
	}

	public EmpApplnEntriesDTO getDate(EmpApplnEntriesDTO empApplnEntriesDTO,Tuple tuple) {
		if(!Utils.isNullOrEmpty(tuple.get("Id"))) {
			empApplnEntriesDTO.setApplicantId(tuple.get("Id").toString());
		}	
		if(!Utils.isNullOrEmpty(tuple.get("name"))) {
			empApplnEntriesDTO.setApplicantName(tuple.get("name").toString());
		}
		if(!Utils.isNullOrEmpty(tuple.get("applicationNumber"))) {
			empApplnEntriesDTO.setApplicantNumber(tuple.get("applicationNumber").toString());
		}
		if(!Utils.isNullOrEmpty(tuple.get("subjectCategoryName"))) {
			empApplnEntriesDTO.setSubjectCategoryName(tuple.get("subjectCategoryName").toString());
		}
		if(!Utils.isNullOrEmpty(tuple.get("subjectCategorySpecializationName"))) {
			empApplnEntriesDTO.setSubjectCategorySpecializationName(tuple.get("subjectCategorySpecializationName").toString());
		}
		if(!Utils.isNullOrEmpty(tuple.get("submissionDate"))) {
			empApplnEntriesDTO.setApplicationDate(LocalDate.parse(tuple.get("submissionDate").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		}
		if(!Utils.isNullOrEmpty(tuple.get("employeeCategory"))) {
			empApplnEntriesDTO.setEmployeeCategoryName(tuple.get("employeeCategory").toString());
		}
		if(!Utils.isNullOrEmpty(tuple.get("qualificationLevelName"))) {
			empApplnEntriesDTO.setHighestQualificationLevel(tuple.get("qualificationLevelName").toString());
		}
		if(!Utils.isNullOrEmpty(tuple.get("totalPreviousExperienceYears")) && !Utils.isNullOrEmpty(tuple.get("totalPreviousExperienceMonths"))) {
			empApplnEntriesDTO.setTotalFullTimeExperience(tuple.get("totalPreviousExperienceYears").toString()+"."+tuple.get("totalPreviousExperienceMonths").toString());
		}else if(!Utils.isNullOrEmpty(tuple.get("totalPreviousExperienceYears")) && Utils.isNullOrEmpty(tuple.get("totalPreviousExperienceMonths"))) {
			empApplnEntriesDTO.setTotalFullTimeExperience(tuple.get("totalPreviousExperienceYears")+"."+"0");
		}else if(Utils.isNullOrEmpty(tuple.get("totalPreviousExperienceYears")) && !Utils.isNullOrEmpty(tuple.get("totalPreviousExperienceMonths"))) {
			empApplnEntriesDTO.setTotalFullTimeExperience("0"+"."+tuple.get("totalPreviousExperienceMonths").toString());
		}
		if(!Utils.isNullOrEmpty(tuple.get("examName"))) {
			empApplnEntriesDTO.setEligibilityId(tuple.get("examName").toString());
		}
		if(!Utils.isNullOrEmpty(tuple.get("processcode"))) {
			empApplnEntriesDTO.setApplicationStatus(tuple.get("processcode").toString());
		}
		if(!Utils.isNullOrEmpty(tuple.get("orchidNo"))) {
			empApplnEntriesDTO.orcidNo = tuple.get("orchidNo").toString();
		}
		if(!Utils.isNullOrEmpty(tuple.get("location_name"))) {
			empApplnEntriesDTO.setLocationPreference(tuple.get("location_name").toString());
		}
		if(!Utils.isNullOrEmpty(tuple.get("file_name_unique")) && !Utils.isNullOrEmpty(tuple.get("upload_process_code")) && !Utils.isNullOrEmpty(tuple.get("file_name_original"))) {
			FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
			fileUploadDownloadDTO.setActualPath(tuple.get("file_name_unique").toString());
			fileUploadDownloadDTO.setProcessCode(tuple.get("upload_process_code").toString());
			fileUploadDownloadDTO.setOriginalFileName(tuple.get("file_name_original").toString());
			aWSS3FileStorageService.generatePresignedUrlForDownload(fileUploadDownloadDTO);
			empApplnEntriesDTO.setProfilePhotoUrl(fileUploadDownloadDTO.getPreSignedUrl());
		}
		return empApplnEntriesDTO;	
	}

//	public void getInternalInterviewPanelists(String userId,ApiResult<List<LookupItemDTO>> result,String applicantId,String round) throws Exception {	
//		//		if(Utils.isNullOrEmpty(applicantId) || Utils.isNullOrEmpty(round)) {
//		//			List<Tuple> list = interviewProcessTransaction.getUserId(applicantId, round);
//		//			if(list.size()>0) {
//		//				for (Tuple tuple : list) {
//		//					userId = String.valueOf(tuple.get("erp_users_id"));
//		//				}
//		//			}
//		//		}
//		List<Tuple> mappings = interviewProcessTransaction.getInternalInterviewPanelists(userId);
//		if(mappings.size()>0) {
//			if(mappings != null && mappings.size() > 0) {
//				result.success = true;
//				result.dto = new ArrayList<>();
//				for(Tuple mapping : mappings) {
//					LookupItemDTO itemInfo = new LookupItemDTO();
//					itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
//					itemInfo.label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
//					result.dto.add(itemInfo);
//				}
//			}
//		}
//	}

//	public void getExternalInterviewPanelists(String userId,ApiResult<List<LookupItemDTO>> result,String applicantId,String round) throws Exception {
//		//		if(Utils.isNullOrEmpty(applicantId) || Utils.isNullOrEmpty(round)) {
//		//			List<Tuple> list = interviewProcessTransaction.getUserId(applicantId, round);
//		//			if(list.size()>0) {
//		//				for (Tuple tuple : list) {
//		//					userId = String.valueOf(tuple.get("erp_users_id"));
//		//				}
//		//			}
//		//		}
//		List<Tuple> mappings = interviewProcessTransaction.getExternalInterviewPanelists(userId);
//		if(mappings.size()>0) {
//			if(mappings != null && mappings.size() > 0) {
//				result.success = true;
//				result.dto = new ArrayList<>();
//				for(Tuple mapping : mappings) {
//					LookupItemDTO itemInfo = new LookupItemDTO();
//					itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
//					itemInfo.label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
//					itemInfo.status = !Utils.isNullOrEmpty(mapping.get("status")) ?  mapping.get("status").toString().equals("1") : false;
//					result.dto.add(itemInfo);
//				}
//			}
//		}
//	}

	//	public List<String> getHolidays(String userId) throws Exception {
	//		List<String> holidays  = new ArrayList<>();
	//		if(!Utils.isNullOrEmpty(userId)) {
	//			List<Tuple> list = interviewProcessTransaction.getHolidays(userId);
	//			if(list.size()>0) {
	//				for (Tuple tuple : list) {
	//					//SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy hh:mm aa");  
	//					//   String strDate = formatter.format(tuple.get("holidays"));
	//					String strDate = !Utils.isNullOrEmpty(tuple.get("holidays")) ? Utils.convertLocalDateToStringDate(Utils.convertStringDateToLocalDate(String.valueOf(tuple.get("holidays")))) : null;
	//					holidays.add(strDate);
	//				}
	//			}
	//		}
	//		return holidays;
	//	}

	//	public void getCampusDepartments(String userId,ApiResult<List<LookupItemDTO>> result) throws Exception {
	//		List<Tuple> mappings = interviewProcessTransaction.getCampusDepartments(userId);
	//		if(mappings.size()>0) {
	//			if(mappings != null && mappings.size() > 0) {
	//				result.success = true;
	//				result.dto = new ArrayList<>();
	//				for(Tuple mapping : mappings) {
	//					LookupItemDTO itemInfo = new LookupItemDTO();
	//					itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
	//					itemInfo.label = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
	//					result.dto.add(itemInfo);
	//				}
	//			}
	//		}
	//	}

	public Flux<EmpApplnEntriesDTO> getApplicationReceived(String departmentId,String locationId) throws Exception{
		List<Tuple> list = interviewProcessTransaction.getApplicationsSubmitted(departmentId,locationId);
		return convertApplicationReceivedDBOtoDto(list,departmentId);
	}

	public Flux<EmpApplnEntriesDTO> convertApplicationReceivedDBOtoDto(List<Tuple> list,String departmentId) throws Exception {
		EmpApplnInterviewSchedulesDTO empApplnInterviewSchedules = new EmpApplnInterviewSchedulesDTO();
		List<EmpApplnEntriesDTO> empApplnEntriesDTOList = new ArrayList<EmpApplnEntriesDTO>();
		List<Integer> entriesIdList = !Utils.isNullOrEmpty(list) 
			    ? list.stream()
			          .filter(s -> !Utils.isNullOrEmpty(s.get("emp_appln_entries_id")))
			          .map(s -> (Integer) s.get("emp_appln_entries_id"))
			          .collect(Collectors.toList())
			    : new ArrayList<Integer>();
		List<Tuple> subSpeList = !Utils.isNullOrEmpty(entriesIdList) ? interviewProcessTransaction1.getSubSpe(entriesIdList) : new ArrayList<Tuple>();
		Map<Integer,List<Tuple>> tupleMap = !Utils.isNullOrEmpty(subSpeList) ? subSpeList.stream().collect(Collectors.groupingBy(b -> Integer.parseInt(String.valueOf(b.get("emp_appln_entries_id")))))  : new HashMap<Integer,List<Tuple>>();
		if(!Utils.isNullOrEmpty(list)) {
			for (Tuple tuple : list) {
				EmpApplnEntriesDTO empApplnEntriesDTO = new EmpApplnEntriesDTO();
				empApplnEntriesDTO.applicantId = String.valueOf(tuple.get("emp_appln_entries_id"));
				empApplnEntriesDTO.applicantName = String.valueOf(tuple.get("applicant_name"));
				if(!Utils.isNullOrEmpty(tuple.get("application_no"))) {
					empApplnEntriesDTO.applicantNumber = String.valueOf(tuple.get("application_no"));
				}
//				if(!Utils.isNullOrEmpty(tuple.get("subject_category_name"))) {
//					empApplnEntriesDTO.subjectCategoryName = String.valueOf(tuple.get("subject_category_name"));
//				}
//				if(!Utils.isNullOrEmpty(tuple.get("subject_category_specialization_name"))) {
//					empApplnEntriesDTO.subjectCategorySpecializationName = tuple.get("subject_category_specialization_name").toString();
//				}
				if(!Utils.isNullOrEmpty(tuple.get("gender_name"))) {
					empApplnEntriesDTO.setGender(new SelectDTO());
					empApplnEntriesDTO.getGender().setLabel(String.valueOf(tuple.get("gender_name")));
				}
				if(!Utils.isNullOrEmpty(tupleMap)) {
					if(tupleMap.containsKey(Integer.parseInt(String.valueOf(tuple.get("emp_appln_entries_id"))))) {
						List<String> subSpeList1 = new ArrayList<String>();
						List<Tuple> tupleList = tupleMap.get(Integer.parseInt(String.valueOf(tuple.get("emp_appln_entries_id"))));
//						String subject = !Utils.isNullOrEmpty(tupleList) ? tupleList.stream().filter(s -> !Utils.isNullOrEmpty(s.get("subject_category_name"))).map(s -> String.valueOf(s.get("subject_category_name"))) .collect(Collectors.joining(", ")): null;
//						String specialization = !Utils.isNullOrEmpty(tupleList) ? tupleList.stream().filter(s -> !Utils.isNullOrEmpty(s.get("subject_category_specialization_name"))).map(s -> String.valueOf(s.get("subject_category_specialization_name"))) .collect(Collectors.joining(", ")): null;
						List<String> subject = !Utils.isNullOrEmpty(tupleList) ? tupleList.stream().filter(s -> !Utils.isNullOrEmpty(s.get("subject_category_name"))).map(s -> String.valueOf(s.get("subject_category_name")).trim()) .collect(Collectors.toList()): null;
						Map<String,List<Tuple>> subSpecMap = !Utils.isNullOrEmpty(tupleList) ? tupleList.stream().collect(Collectors.groupingBy(b -> String.valueOf(b.get("subject_category_name")).trim()))  : new HashMap<String,List<Tuple>>();
						if(!Utils.isNullOrEmpty(subject) && !Utils.isNullOrEmpty(subSpecMap)) {
							subject.forEach(data -> {
								if(subSpecMap.containsKey(data)) {
									List<Tuple> dataList = subSpecMap.get(data);
									String specialization = !Utils.isNullOrEmpty(dataList) ? dataList.stream().filter(s -> !Utils.isNullOrEmpty(s.get("subject_category_specialization_name"))).map(s -> String.valueOf(s.get("subject_category_specialization_name"))) .collect(Collectors.joining(" | ")): null;
									if(!Utils.isNullOrEmpty(specialization)) {
										if(!subSpeList1.contains(data+"- ("+specialization+") ")) {
											subSpeList1.add(data+"- ("+specialization+") ");
										}
									}
									else {
										if(!subSpeList1.contains(data)) {
											subSpeList1.add(data);
										}
									}
								}
							});
//							empApplnEntriesDTO.subjectCategoryName = subject;
						}
						if(!Utils.isNullOrEmpty(subSpeList1)) {
							 String string = String.join(",", subSpeList1);
							empApplnEntriesDTO.subjectCategoryName = string;
						}
					}
				}
				if(!Utils.isNullOrEmpty(tuple.get("submission_date"))) {
					String date = tuple.get("submission_date").toString().trim();
					empApplnEntriesDTO.setApplicationDate(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
				}
				if(!Utils.isNullOrEmpty(tuple.get("employee_category_name"))) {
					empApplnEntriesDTO.employeeCategoryName = tuple.get("employee_category_name").toString();
				}
				if(!Utils.isNullOrEmpty(tuple.get("location_name"))) {
					empApplnEntriesDTO.locationPreference = tuple.get("location_name").toString();
				}
				if(!Utils.isNullOrEmpty(tuple.get("is_shortlisted_for_interview"))) {
					empApplnEntriesDTO.shortlisted = (Boolean)tuple.get("is_shortlisted_for_interview");
				}
				if(!Utils.isNullOrEmpty(tuple.get("qualification_level_name"))) {
					empApplnEntriesDTO.highestQualificationLevel = tuple.get("qualification_level_name").toString();
				}
				if(!Utils.isNullOrEmpty(tuple.get("process_code"))) {
					empApplnEntriesDTO.applicationStatus = tuple.get("process_code").toString();
				}
				if(!Utils.isNullOrEmpty(tuple.get("orcid_no"))) {
					empApplnEntriesDTO.orcidNo = tuple.get("orcid_no").toString();
				}
				if(!Utils.isNullOrEmpty(tuple.get("total_previous_experience_years")) && !Utils.isNullOrEmpty(tuple.get("total_previous_experience_months"))) {
					String experYear = tuple.get("total_previous_experience_years").toString();
					String experMonth = tuple.get("total_previous_experience_months").toString();
					empApplnEntriesDTO.setTotalFullTimeExperience(experYear.trim()+"."+experMonth.trim());
				}else if(Utils.isNullOrEmpty(tuple.get("total_previous_experience_years")) && !Utils.isNullOrEmpty(tuple.get("total_previous_experience_months"))){
					String experMonth = tuple.get("total_previous_experience_months").toString();
					empApplnEntriesDTO.setTotalFullTimeExperience("."+experMonth.trim());
				}else if(!Utils.isNullOrEmpty(tuple.get("total_previous_experience_years")) && Utils.isNullOrEmpty(tuple.get("total_previous_experience_months"))){
					String experYear = tuple.get("total_previous_experience_years").toString();
					empApplnEntriesDTO.setTotalFullTimeExperience(experYear.trim());
				}
				/*
				if(!Utils.isNullOrEmpty(tuple.get("profile_photo_url"))) {
					empApplnEntriesDTO.setProfilePhotoUrl(tuple.get("profile_photo_url").toString());
				}*/
				if(!Utils.isNullOrEmpty(tuple.get("file_name_unique")) && !Utils.isNullOrEmpty(tuple.get("upload_process_code")) && !Utils.isNullOrEmpty(tuple.get("file_name_original"))) {
					FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
					fileUploadDownloadDTO.setActualPath(tuple.get("file_name_unique").toString());
					fileUploadDownloadDTO.setProcessCode(tuple.get("upload_process_code").toString());
					fileUploadDownloadDTO.setOriginalFileName(tuple.get("file_name_original").toString());
					aWSS3FileStorageService.generatePresignedUrlForDownload(fileUploadDownloadDTO);
					empApplnEntriesDTO.setProfilePhotoUrl(fileUploadDownloadDTO.getPreSignedUrl());
					
				}
				empApplnEntriesDTO.empApplnInterviewSchedulesDTO = empApplnInterviewSchedules;
				empApplnEntriesDTOList.add(empApplnEntriesDTO);
			}
		}
		return Flux.fromIterable(empApplnEntriesDTOList);
	}

	public Flux<EmpApplnEntriesDTO> getScheduleStageOne(String departmentId,String processCode,String locationId)  {
		List<EmpApplnEntriesDBO> list = interviewProcessTransaction1.getScheduleStageOne(departmentId,processCode,locationId);
		return convertCandidatesDBOtoDto(list);
	}

	public Flux<EmpApplnEntriesDTO> getSheduledForApprovalStageOne(String departmentId,String locationId) {
		List<EmpApplnEntriesDBO> list = interviewProcessTransaction1.getSheduledForApprovalStageOne(departmentId,locationId);
		return convertCandidatesDBOtoDto(list);
	}

	private Flux<EmpApplnEntriesDTO> convertCandidatesDBOtoDto(List<EmpApplnEntriesDBO> list) {
		List<EmpApplnEntriesDTO> empApplnEntriesDTOList = new ArrayList<EmpApplnEntriesDTO>();
		Set<Integer> applicationDepartment = new HashSet<Integer>();
		Set<Integer> userIds = new HashSet<Integer>();
		List<Integer> pointList = new ArrayList<Integer>();
		if(!Utils.isNullOrEmpty(list)) {
			list.forEach(dbo -> {
				if(!Utils.isNullOrEmpty(dbo.getEmpApplnInterviewSchedulesDBOs())) {
					dbo.getEmpApplnInterviewSchedulesDBOs().forEach(dbo1 -> {
						if(dbo1.getRecordStatus() == 'A') {
							if(!Utils.isNullOrEmpty(dbo1.getErpUsersDBO())) {
								pointList.add(dbo1.getErpUsersDBO().getId());
							}
							if(!Utils.isNullOrEmpty(dbo1.getEmpApplnInterviewPanelDBO())) {
								dbo1.getEmpApplnInterviewPanelDBO().forEach(dbo2 -> {
									if(dbo2.getRecordStatus() == 'A') {
										if(!Utils.isNullOrEmpty(dbo2.isInternalPanel)) {
											if(dbo2.isInternalPanel) {
												if(!Utils.isNullOrEmpty(dbo2.getErpUsersDBO())) {
													userIds.add(dbo2.getErpUsersDBO().getId());
												}
											}else {
												if(!Utils.isNullOrEmpty(dbo2.getErpUsersDBO())) {
													userIds.add(dbo2.getErpUsersDBO().getId());
												}
											}
										}
									}
								});
							}	
						}
					});
				}
			});
		}
		List<Tuple> pointTuple = (!Utils.isNullOrEmpty(pointList)) ? interviewProcessTransaction1.getPointOfContact(pointList) : null;
		Map<Integer, String> pointMap = !Utils.isNullOrEmpty(pointTuple) ? pointTuple.stream().collect(Collectors.toMap(s -> Integer.parseInt(s.get("erpUserId").toString()), p->(p.get("name").toString()+ p.get("department").toString()))) : new HashMap<Integer, String>();
		List<Tuple> empList = !Utils.isNullOrEmpty(userIds) ? interviewProcessTransaction1.getEmpFromUser(userIds) : new ArrayList<Tuple>();
		Map<Integer,String> empNameMap = !Utils.isNullOrEmpty(empList) ? empList.stream().filter(s -> !Utils.isNullOrEmpty(s.get("erp_users_id"))).collect(Collectors.toMap(s -> Integer.parseInt(s.get("erp_users_id").toString()), p -> p.get("name").toString())) : new HashMap<Integer,String>();
		if(!Utils.isNullOrEmpty(list)) {
			list.forEach(dbo -> {
				EmpApplnEntriesDTO empApplnEntriesDTO = new EmpApplnEntriesDTO();
				if(!Utils.isNullOrEmpty(dbo.getApplicationNo())) {
					if(!applicationDepartment.contains(dbo.getApplicationNo())){
						applicationDepartment.add(dbo.getApplicationNo());
						if(!Utils.isNullOrEmpty(dbo.getId())) {
							empApplnEntriesDTO.setApplicantId(String.valueOf(dbo.getId()));
						}
						if(!Utils.isNullOrEmpty(dbo.getApplicationNo())) {
							empApplnEntriesDTO.setApplicantNumber(String.valueOf(dbo.getApplicationNo()));
						}
						if(!Utils.isNullOrEmpty(dbo.getApplicantName())) {
							empApplnEntriesDTO.setApplicantName(dbo.getApplicantName());
						}
						if(!Utils.isNullOrEmpty(dbo.getApplicationCurrentProcessStatus().getApplicationStatusDisplayText())) {
							empApplnEntriesDTO.setStatus(dbo.getApplicationCurrentProcessStatus().getApplicationStatusDisplayText());
						}
						if(!Utils.isNullOrEmpty(dbo.getApplicationCurrentProcessStatus().getProcessCode())) {
							empApplnEntriesDTO.setApplicationStatus(dbo.getApplicationCurrentProcessStatus().getProcessCode());
						}
						if(!Utils.isNullOrEmpty(dbo.getPersonalEmailId())) {
							empApplnEntriesDTO.setApplicantEmail(dbo.getPersonalEmailId());
						}
						if(!Utils.isNullOrEmpty(dbo.getMobileNoCountryCode()) && !Utils.isNullOrEmpty(dbo.getMobileNo())) {
							empApplnEntriesDTO.setApplicantMobile(dbo.getMobileNoCountryCode().toString()+dbo.getMobileNo().toString());
						}
						if(!Utils.isNullOrEmpty(dbo.getSubmissionDate())) {
							empApplnEntriesDTO.setApplicationDate(dbo.getSubmissionDate().toLocalDate());
						}
						if(!Utils.isNullOrEmpty(dbo.getShortlistedDepartmentId())) {
							empApplnEntriesDTO.setShortlistedDepartment(new SelectDTO());
							empApplnEntriesDTO.getShortlistedDepartment().setValue(String.valueOf(dbo.getShortlistedDepartmentId().getId()));
							empApplnEntriesDTO.getShortlistedDepartment().setLabel(dbo.getShortlistedDepartmentId().getDepartmentName());
						}
						if(!Utils.isNullOrEmpty(dbo.getShortistedLocationId())) {
							empApplnEntriesDTO.setLocationPreference(dbo.getShortistedLocationId().getId().toString());
						}
						if(!Utils.isNullOrEmpty(dbo.getEmpEmployeeCategoryDBO())) {
							if(!Utils.isNullOrEmpty(dbo.getEmpEmployeeCategoryDBO().getIsEmployeeCategoryAcademic())) {
								empApplnEntriesDTO.setIsAcademic(dbo.getEmpEmployeeCategoryDBO().getIsEmployeeCategoryAcademic());
							}
						}
						if(!Utils.isNullOrEmpty(dbo.getEmpApplnPersonalDataDBO())) {
							if(!Utils.isNullOrEmpty(dbo.getEmpApplnPersonalDataDBO().getPhotoDocumentUrlDBO())) {
								if(!Utils.isNullOrEmpty(dbo.getEmpApplnPersonalDataDBO().getPhotoDocumentUrlDBO().getUrlFolderListDBO())) {
									if(!Utils.isNullOrEmpty(dbo.getEmpApplnPersonalDataDBO().getPhotoDocumentUrlDBO().getFileNameUnique()) && !Utils.isNullOrEmpty(dbo.getEmpApplnPersonalDataDBO().getPhotoDocumentUrlDBO().getUrlFolderListDBO().getUploadProcessCode()) && !Utils.isNullOrEmpty(dbo.getEmpApplnPersonalDataDBO().getPhotoDocumentUrlDBO().getFileNameOriginal())) {
										FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
										fileUploadDownloadDTO.setActualPath(dbo.getEmpApplnPersonalDataDBO().getPhotoDocumentUrlDBO().getFileNameUnique());
										fileUploadDownloadDTO.setProcessCode(dbo.getEmpApplnPersonalDataDBO().getPhotoDocumentUrlDBO().getUrlFolderListDBO().getUploadProcessCode());
										fileUploadDownloadDTO.setOriginalFileName(dbo.getEmpApplnPersonalDataDBO().getPhotoDocumentUrlDBO().getFileNameOriginal());
										aWSS3FileStorageService.generatePresignedUrlForDownload(fileUploadDownloadDTO);
										empApplnEntriesDTO.setProfilePhotoUrl(fileUploadDownloadDTO.getPreSignedUrl());
									}
								}
							}
						}
						if(!Utils.isNullOrEmpty(dbo.getEmpApplnInterviewSchedulesDBOs())) {
							dbo.getEmpApplnInterviewSchedulesDBOs().forEach(dbo1 -> {
								if(dbo1.getRecordStatus() == 'A') {
									EmpApplnInterviewSchedulesDTO empApplnInterviewSchedulesDTO = new EmpApplnInterviewSchedulesDTO();
									if(!Utils.isNullOrEmpty(dbo1.getEmpApplnInterviewSchedulesId())) {
										empApplnInterviewSchedulesDTO.setId(String.valueOf(dbo1.getEmpApplnInterviewSchedulesId()));
									}
									if(!Utils.isNullOrEmpty(dbo1.getInterviewVenue())) {
										empApplnInterviewSchedulesDTO.setInterviewVenue(dbo1.getInterviewVenue());
									}
									if(!Utils.isNullOrEmpty(dbo1.getInterviewDateTime())) {
										empApplnInterviewSchedulesDTO.setInterviewDateTime1(dbo1.getInterviewDateTime());
									}
//									if(!Utils.isNullOrEmpty(dbo1.getErpUsersDBO())) {
//										empApplnInterviewSchedulesDTO.setPointOfContactUsersId(dbo1.getErpUsersDBO().getId().toString());
//									}
									if(!Utils.isNullOrEmpty(pointMap)) {
										if(pointMap.containsKey(dbo1.getErpUsersDBO().getId())) {
											empApplnInterviewSchedulesDTO.setPointofContact(new SelectDTO());
											empApplnInterviewSchedulesDTO.getPointofContact().setValue(dbo1.getErpUsersDBO().getId().toString());
											empApplnInterviewSchedulesDTO.getPointofContact().setLabel(pointMap.get(dbo1.getErpUsersDBO().getId()));
										}
									}
									empApplnEntriesDTO.setEmpApplnInterviewSchedulesDTO(empApplnInterviewSchedulesDTO);
									if(!Utils.isNullOrEmpty(dbo1.getEmpApplnInterviewPanelDBO())) {
										empApplnEntriesDTO.getEmpApplnInterviewSchedulesDTO().setInternalPanelists(new ArrayList<LookupItemDTO>());
										empApplnEntriesDTO.getEmpApplnInterviewSchedulesDTO().setExternalPanelists(new ArrayList<LookupItemDTO>());
										dbo1.getEmpApplnInterviewPanelDBO().forEach(dbo2 -> {
											if(dbo2.getRecordStatus() == 'A') {
												if(!Utils.isNullOrEmpty(dbo2.isInternalPanel)) {
													if(dbo2.isInternalPanel) {
														LookupItemDTO panelistId = new LookupItemDTO();
														if(!Utils.isNullOrEmpty(dbo2.getErpUsersDBO())) {
															panelistId.setValue(dbo2.getErpUsersDBO().getId().toString());
															if(empNameMap.containsKey(dbo2.getErpUsersDBO().getId())){
																String empName = empNameMap.get(dbo2.getErpUsersDBO().getId());
																panelistId.setLabel(empName);
															}
														}
														empApplnEntriesDTO.getEmpApplnInterviewSchedulesDTO().getInternalPanelists().add(panelistId);
													}else {
														LookupItemDTO panelistId = new LookupItemDTO();
														if(!Utils.isNullOrEmpty(dbo2.getErpUsersDBO())) {
															panelistId.setValue(dbo2.getErpUsersDBO().getId().toString());
															if(empNameMap.containsKey(dbo2.getErpUsersDBO().getId())){
																String empName = empNameMap.get(dbo2.getErpUsersDBO().getId());
																panelistId.setLabel(empName);
															}
															panelistId.setStatus(true);
														}
														else {
															if(!Utils.isNullOrEmpty(dbo2.getEmpInterviewUniversityExternalsDBO())) {
																panelistId.setValue(dbo2.getEmpInterviewUniversityExternalsDBO().getId().toString());
																if(!Utils.isNullOrEmpty(dbo2.getEmpInterviewUniversityExternalsDBO().getPanelName()))
																		panelistId.setLabel(dbo2.getEmpInterviewUniversityExternalsDBO().getPanelName());
																panelistId.setStatus(false);
															}
														}
														empApplnEntriesDTO.getEmpApplnInterviewSchedulesDTO().getExternalPanelists().add(panelistId);
													}
												}
											}
										});
									}
								}
							});
						}
					}
				}
				if(!Utils.isNullOrEmpty(empApplnEntriesDTO.getApplicantId()))
					empApplnEntriesDTOList.add(empApplnEntriesDTO);
			});
		}
		return Flux.fromIterable(empApplnEntriesDTOList);
	}

	public Flux<EmpApplnEntriesDTO> getOfferStatus(String departmentId,String processCode,String locationId) {
		List<Tuple> list = interviewProcessTransaction1.getOfferStatus(departmentId,processCode,locationId);
		return convertOfferLetter(list,processCode);
	}

	private Flux<EmpApplnEntriesDTO> convertOfferLetter(List<Tuple> list,String processCode) {
		List<EmpApplnEntriesDTO> empApplnEntriesDTOList = new ArrayList<EmpApplnEntriesDTO>();
		if(!Utils.isNullOrEmpty(list)) {
			list.forEach(tuple -> {
				EmpApplnEntriesDTO empApplnEntriesDTO = new EmpApplnEntriesDTO();
				if(!Utils.isNullOrEmpty(tuple.get("emp_appln_entries_id"))) {
					empApplnEntriesDTO.applicantId = tuple.get("emp_appln_entries_id").toString();	
				}
				if(!Utils.isNullOrEmpty(tuple.get("applicant_name"))) {
					empApplnEntriesDTO.applicantName = tuple.get("applicant_name").toString();	
				}
				if(!Utils.isNullOrEmpty(tuple.get("application_no"))) {
					empApplnEntriesDTO.applicantNumber = tuple.get("application_no").toString();
				}
				if(!Utils.isNullOrEmpty(tuple.get("process_code"))) {
					empApplnEntriesDTO.applicationStatus = tuple.get("process_code").toString();
				}
//				if(!Utils.isNullOrEmpty(tuple.get("profile_photo_url"))) {
//					empApplnEntriesDTO.setProfilePhotoUrl(tuple.get("profile_photo_url").toString());
//				}
				if(!Utils.isNullOrEmpty(tuple.get("joining_date"))) {
					empApplnEntriesDTO.setJoiningDateTime(Utils.convertStringDateTimeToLocalDateTime(tuple.get("joining_date").toString()));
				}
				if(!Utils.isNullOrEmpty(tuple.get("prefered_joining_date"))) {
					empApplnEntriesDTO.setPreferedJoiningDateTime(Utils.convertStringDateTimeToLocalDateTime(tuple.get("prefered_joining_date").toString()));
				}
				if(!Utils.isNullOrEmpty(tuple.get("joining_date_reject_reason"))) {
					empApplnEntriesDTO.setJoiningDateRejectReason(tuple.get("joining_date_reject_reason").toString());
				}
				if(!Utils.isNullOrEmpty(tuple.get("is_joining_date_confirmed"))) {
					if(tuple.get("is_joining_date_confirmed").toString().equals("1") || tuple.get("is_joining_date_confirmed").toString().equals("true")) {
						empApplnEntriesDTO.setIsJoiningDateConfirmed(true);
					} else 	if(tuple.get("is_joining_date_confirmed").toString().equals("0") || tuple.get("is_joining_date_confirmed").toString().equals("false")) {
						empApplnEntriesDTO.setIsJoiningDateConfirmed(false);
					}
				}
				if(!Utils.isNullOrEmpty(tuple.get("file_name_unique")) && !Utils.isNullOrEmpty(tuple.get("upload_process_code")) && !Utils.isNullOrEmpty(tuple.get("file_name_original"))) {
					FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
					fileUploadDownloadDTO.setActualPath(tuple.get("file_name_unique").toString());
					fileUploadDownloadDTO.setProcessCode(tuple.get("upload_process_code").toString());
					fileUploadDownloadDTO.setOriginalFileName(tuple.get("file_name_original").toString());
					aWSS3FileStorageService.generatePresignedUrlForDownload(fileUploadDownloadDTO);
					empApplnEntriesDTO.setProfilePhotoUrl(fileUploadDownloadDTO.getPreSignedUrl());
					
				}
				if(!Utils.isNullOrEmpty(tuple.get("text"))) {
					empApplnEntriesDTO.setStatus(tuple.get("text").toString());
				}
				empApplnEntriesDTOList.add(empApplnEntriesDTO);
			});
		}
		return Flux.fromIterable(empApplnEntriesDTOList);
	}

	public Flux<EmpApplnEntriesDTO> getInterviewStatusStageOne(String departmentId,String processCode, String locationId) {
		List<EmpApplnEntriesDBO> list = interviewProcessTransaction1.getInterviewStatusStageOne(departmentId,locationId,processCode);
		return InterviewStatusStage1DBOToDTO(list);
	}

	private Flux<EmpApplnEntriesDTO> InterviewStatusStage1DBOToDTO(List<EmpApplnEntriesDBO> list) {
		List<EmpApplnEntriesDTO> empApplnEntriesDTOList = new ArrayList<EmpApplnEntriesDTO>();
		List<Integer> idList = !Utils.isNullOrEmpty(list) ? list.stream().map(s -> s.getId()).collect(Collectors.toList()) : new ArrayList<Integer>();
		List<Tuple> PanelList = !Utils.isNullOrEmpty(list) ? interviewProcessTransaction1.getPanelMemberCount(idList) : null;
		List<Tuple> scoreEnteredPanelCountList = !Utils.isNullOrEmpty(list) ? interviewProcessTransaction1.getScoreEnteredPanelCount(idList) : null;
		List<Tuple> totalScore = !Utils.isNullOrEmpty(list) ? interviewProcessTransaction1.getTotalScore(idList) : null;
		Map<Integer,Integer> panelMap = !Utils.isNullOrEmpty(PanelList) ? PanelList.stream().collect(Collectors.toMap( s -> Integer.parseInt(s.get("Ids").toString()), s -> !Utils.isNullOrEmpty(s.get("countt")) ? Integer.parseInt(s.get("countt").toString()) : 0)) : new HashMap<Integer, Integer>();
		Map<Integer,Integer> scoreEnteredPanelCountMap = !Utils.isNullOrEmpty(scoreEnteredPanelCountList) ? scoreEnteredPanelCountList.stream().collect(Collectors.toMap( s -> Integer.parseInt(s.get("Ids").toString()), s -> !Utils.isNullOrEmpty(s.get("countt")) ? Integer.parseInt(s.get("countt").toString()) : 0)) : new HashMap<Integer, Integer>(); 
		Map<Integer,Integer> totalScoreMap = !Utils.isNullOrEmpty(totalScore) ? totalScore.stream().collect(Collectors.toMap( s -> Integer.parseInt(s.get("Ids").toString()), s -> !Utils.isNullOrEmpty(s.get("summ")) ? Integer.parseInt(s.get("summ").toString()) : 0)) : new HashMap<Integer, Integer>(); 
		Set<Integer> applicationDepartment = new HashSet<Integer>();
		Set<Integer> userIds = new HashSet<Integer>();
		List<Integer> pointList = new ArrayList<Integer>();
		if(!Utils.isNullOrEmpty(list)) {
			list.forEach(dbo -> {
				if(!Utils.isNullOrEmpty(dbo.getEmpApplnInterviewSchedulesDBOs())) {
					dbo.getEmpApplnInterviewSchedulesDBOs().forEach(dbo1 -> {
						if(dbo1.getRecordStatus() == 'A') {
							if(!Utils.isNullOrEmpty(dbo1.getErpUsersDBO())) {
								pointList.add(dbo1.getErpUsersDBO().getId());
							}
							if(!Utils.isNullOrEmpty(dbo1.getEmpApplnInterviewPanelDBO())) {
								dbo1.getEmpApplnInterviewPanelDBO().forEach(dbo2 -> {
									if(dbo2.getRecordStatus() == 'A') {
										if(!Utils.isNullOrEmpty(dbo2.isInternalPanel)) {
											if(dbo2.isInternalPanel) {
												if(!Utils.isNullOrEmpty(dbo2.getErpUsersDBO())) {
													userIds.add(dbo2.getErpUsersDBO().getId());
												}
											}else {
												if(!Utils.isNullOrEmpty(dbo2.getErpUsersDBO())) {
													userIds.add(dbo2.getErpUsersDBO().getId());
												}
											}
										}
									}
								});
							}	
						}
					});
				}
			});
		}
		List<Tuple> pointTuple = (!Utils.isNullOrEmpty(pointList)) ? interviewProcessTransaction1.getPointOfContact(pointList) : null;
		Map<Integer, String> pointMap = !Utils.isNullOrEmpty(pointTuple) ? pointTuple.stream().collect(Collectors.toMap(s -> Integer.parseInt(s.get("erpUserId").toString()), p->(p.get("name").toString()+ p.get("department").toString()))) : new HashMap<Integer, String>();
		List<Tuple> empList = !Utils.isNullOrEmpty(userIds) ? interviewProcessTransaction1.getEmpFromUser(userIds) : new ArrayList<Tuple>();
		Map<Integer,String> empNameMap = !Utils.isNullOrEmpty(empList) ? empList.stream().filter(s -> !Utils.isNullOrEmpty(s.get("erp_users_id"))).collect(Collectors.toMap(s -> Integer.parseInt(s.get("erp_users_id").toString()), p -> p.get("name").toString())) : new HashMap<Integer,String>();
		if(!Utils.isNullOrEmpty(list)) {
			list.forEach(dbo -> {
				EmpApplnEntriesDTO empApplnEntriesDTO = new EmpApplnEntriesDTO();
				if(!Utils.isNullOrEmpty(dbo.getApplicationNo())) {
					if(!applicationDepartment.contains(dbo.getApplicationNo())){
						applicationDepartment.add(dbo.getApplicationNo());
						if(!Utils.isNullOrEmpty(dbo.getId())) {
							empApplnEntriesDTO.setApplicantId(String.valueOf(dbo.getId()));
						}
						if(!Utils.isNullOrEmpty(dbo.getApplicationNo())) {
							empApplnEntriesDTO.setApplicantNumber(String.valueOf(dbo.getApplicationNo()));
						}
						if(!Utils.isNullOrEmpty(dbo.getApplicantName())) {
							empApplnEntriesDTO.setApplicantName(dbo.getApplicantName());
						}
						if(!Utils.isNullOrEmpty(dbo.getApplicationCurrentProcessStatus())) {
							if(!Utils.isNullOrEmpty(dbo.getApplicationCurrentProcessStatus().getApplicationStatusDisplayText()))
								empApplnEntriesDTO.setStatus(dbo.getApplicationCurrentProcessStatus().getApplicationStatusDisplayText());
							if(!Utils.isNullOrEmpty(dbo.getApplicationCurrentProcessStatus().getProcessCode())) 
								empApplnEntriesDTO.setApplicationStatus(dbo.getApplicationCurrentProcessStatus().getProcessCode());
						}
						if(!Utils.isNullOrEmpty(dbo.getSubmissionDate())) {
							empApplnEntriesDTO.setApplicationDate(dbo.getSubmissionDate().toLocalDate());
						}
						if(!Utils.isNullOrEmpty(dbo.getShortlistedDepartmentId())) {
							empApplnEntriesDTO.setShortlistedDepartment(new SelectDTO());
							empApplnEntriesDTO.getShortlistedDepartment().setValue(String.valueOf(dbo.getShortlistedDepartmentId().getId()));
							empApplnEntriesDTO.getShortlistedDepartment().setLabel(dbo.getShortlistedDepartmentId().getDepartmentName());
						}
						if(!Utils.isNullOrEmpty(dbo.getShortistedLocationId())) {
							empApplnEntriesDTO.setLocationPreference(dbo.getShortistedLocationId().getId().toString());
						}
						if(!Utils.isNullOrEmpty(dbo.getPersonalEmailId())) {
							empApplnEntriesDTO.setApplicantEmail(dbo.getPersonalEmailId());
						}
						if(!Utils.isNullOrEmpty(dbo.getMobileNoCountryCode()) && !Utils.isNullOrEmpty(dbo.getMobileNo())) {
							empApplnEntriesDTO.setApplicantMobile(dbo.getMobileNoCountryCode().toString()+dbo.getMobileNo().toString());
						}
						if(!Utils.isNullOrEmpty(dbo.getEmpApplnPersonalDataDBO())) {
							if(!Utils.isNullOrEmpty(dbo.getEmpApplnPersonalDataDBO().getPhotoDocumentUrlDBO())) {
								if(!Utils.isNullOrEmpty(dbo.getEmpApplnPersonalDataDBO().getPhotoDocumentUrlDBO().getUrlFolderListDBO())) {
									if(!Utils.isNullOrEmpty(dbo.getEmpApplnPersonalDataDBO().getPhotoDocumentUrlDBO().getFileNameUnique()) && !Utils.isNullOrEmpty(dbo.getEmpApplnPersonalDataDBO().getPhotoDocumentUrlDBO().getUrlFolderListDBO().getUploadProcessCode()) && !Utils.isNullOrEmpty(dbo.getEmpApplnPersonalDataDBO().getPhotoDocumentUrlDBO().getFileNameOriginal())) {
										FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
										fileUploadDownloadDTO.setActualPath(dbo.getEmpApplnPersonalDataDBO().getPhotoDocumentUrlDBO().getFileNameUnique());
										fileUploadDownloadDTO.setProcessCode(dbo.getEmpApplnPersonalDataDBO().getPhotoDocumentUrlDBO().getUrlFolderListDBO().getUploadProcessCode());
										fileUploadDownloadDTO.setOriginalFileName(dbo.getEmpApplnPersonalDataDBO().getPhotoDocumentUrlDBO().getFileNameOriginal());
										aWSS3FileStorageService.generatePresignedUrlForDownload(fileUploadDownloadDTO);
										empApplnEntriesDTO.setProfilePhotoUrl(fileUploadDownloadDTO.getPreSignedUrl());
									}
								}
							}
						}
						if(panelMap.containsKey(dbo.getId())) {
							Integer panelCount = panelMap.get(dbo.getId());
							if(!Utils.isNullOrEmpty(panelCount) || (panelCount!=0)) {
								if(scoreEnteredPanelCountMap.containsKey(dbo.getId())) {
									Integer scoreEnteredPanelCount = scoreEnteredPanelCountMap.get(dbo.getId());
									if(panelCount.equals(scoreEnteredPanelCount)) {
										empApplnEntriesDTO.setScoreEntry("Completed");
										if(!Utils.isNullOrEmpty(totalScoreMap)) {
											Integer total = totalScoreMap.get(dbo.getId());
											if(!Utils.isNullOrEmpty(total) || (total != 0)) {
												int score = total.intValue();
												int totalPanel = scoreEnteredPanelCount.intValue();
												float totalScoreFloat = (float) score / totalPanel;
												empApplnEntriesDTO.setTotalScore(totalScoreFloat);
											}
										}
									}
									if(scoreEnteredPanelCount.equals(0)) {
										empApplnEntriesDTO.setScoreEntry("Pending");
									}
									if(scoreEnteredPanelCount<panelCount) {
										empApplnEntriesDTO.setScoreEntry("In Progress");
										if(!Utils.isNullOrEmpty(totalScoreMap)) {
											Integer total = totalScoreMap.get(dbo.getId());
											if(!Utils.isNullOrEmpty(total) || (total != 0)) {
												int score = total.intValue();
												int totalPanel = scoreEnteredPanelCount.intValue();
												float totalScoreFloat = (float) score / totalPanel;
												empApplnEntriesDTO.setTotalScore(totalScoreFloat);
											}
										}
									}
								}else  {
									empApplnEntriesDTO.setScoreEntry("Pending");
								}
							}else {
								empApplnEntriesDTO.setScoreEntry("Pending");
							}	
						}else {
							empApplnEntriesDTO.setScoreEntry("Pending");
						}
						if(!Utils.isNullOrEmpty(dbo.getEmpApplnInterviewSchedulesDBOs())) {
							dbo.getEmpApplnInterviewSchedulesDBOs().forEach(dbo1 -> {
								if(dbo1.getRecordStatus() == 'A') {
									EmpApplnInterviewSchedulesDTO empApplnInterviewSchedulesDTO = new EmpApplnInterviewSchedulesDTO();
									if(!Utils.isNullOrEmpty(dbo1.getEmpApplnInterviewSchedulesId())) {
										empApplnInterviewSchedulesDTO.setId(String.valueOf(dbo1.getEmpApplnInterviewSchedulesId()));
									}
									if(!Utils.isNullOrEmpty(dbo1.getInterviewVenue())) {
										empApplnInterviewSchedulesDTO.setInterviewVenue(dbo1.getInterviewVenue());
									}
									if(!Utils.isNullOrEmpty(dbo1.getInterviewDateTime())) {
										empApplnInterviewSchedulesDTO.setInterviewDateTime1(dbo1.getInterviewDateTime());
									}
//									if(!Utils.isNullOrEmpty(dbo1.getErpUsersDBO())) {
//										empApplnInterviewSchedulesDTO.setPointOfContactUsersId(dbo1.getErpUsersDBO().getId().toString());
//									}
									if(!Utils.isNullOrEmpty(pointMap)) {
										if(pointMap.containsKey(dbo1.getErpUsersDBO().getId())) {
											empApplnInterviewSchedulesDTO.setPointofContact(new SelectDTO());
											empApplnInterviewSchedulesDTO.getPointofContact().setValue(dbo1.getErpUsersDBO().getId().toString());
											empApplnInterviewSchedulesDTO.getPointofContact().setLabel(pointMap.get(dbo1.getErpUsersDBO().getId()));
										}
									}
									if(!Utils.isNullOrEmpty(dbo1.getComments())) {
										empApplnInterviewSchedulesDTO.setComments(dbo1.getComments());
									}
									empApplnEntriesDTO.setEmpApplnInterviewSchedulesDTO(empApplnInterviewSchedulesDTO);
									if(!Utils.isNullOrEmpty(dbo1.getEmpApplnInterviewPanelDBO())) {
										empApplnEntriesDTO.getEmpApplnInterviewSchedulesDTO().setInternalPanelists(new ArrayList<LookupItemDTO>());
										empApplnEntriesDTO.getEmpApplnInterviewSchedulesDTO().setExternalPanelists(new ArrayList<LookupItemDTO>());
										dbo1.getEmpApplnInterviewPanelDBO().forEach(dbo2 -> {
											if(dbo2.getRecordStatus() == 'A') {
												if(!Utils.isNullOrEmpty(dbo2.isInternalPanel)) {
													if(dbo2.isInternalPanel) {
														LookupItemDTO panelistId = new LookupItemDTO();
														if(!Utils.isNullOrEmpty(dbo2.getErpUsersDBO())) {
															panelistId.setValue(dbo2.getErpUsersDBO().getId().toString());
															if(empNameMap.containsKey(dbo2.getErpUsersDBO().getId())){
																String empName = empNameMap.get(dbo2.getErpUsersDBO().getId());
																panelistId.setLabel(empName);
															}
														}
														empApplnEntriesDTO.getEmpApplnInterviewSchedulesDTO().getInternalPanelists().add(panelistId);
													}else {
														LookupItemDTO panelistId = new LookupItemDTO();
														if(!Utils.isNullOrEmpty(dbo2.getErpUsersDBO())) {
															panelistId.setValue(dbo2.getErpUsersDBO().getId().toString());
															if(empNameMap.containsKey(dbo2.getErpUsersDBO().getId())){
																String empName = empNameMap.get(dbo2.getErpUsersDBO().getId());
																panelistId.setLabel(empName);
															}
															panelistId.setStatus(true);
														}
														else {
															if(!Utils.isNullOrEmpty(dbo2.getEmpInterviewUniversityExternalsDBO())) {
																panelistId.setValue(dbo2.getEmpInterviewUniversityExternalsDBO().getId().toString());
																if(!Utils.isNullOrEmpty(dbo2.getEmpInterviewUniversityExternalsDBO().getPanelName())) {
																	panelistId.setLabel(dbo2.getEmpInterviewUniversityExternalsDBO().getPanelName());
																}
																panelistId.setStatus(false);
															}
														}
														empApplnEntriesDTO.getEmpApplnInterviewSchedulesDTO().getExternalPanelists().add(panelistId);
													}
												}
											}
										});
									}
								}
							});
						}
					}
				}
				if(!Utils.isNullOrEmpty(empApplnEntriesDTO.getApplicantId()))
					empApplnEntriesDTOList.add(empApplnEntriesDTO);
			});
		}
		return Flux.fromIterable(empApplnEntriesDTOList);
	}

	public Flux<EmpApplnEntriesDTO> getScheduleStageTwo(String departmentId,String processCode, String locationId) {
		List<Tuple> list = interviewProcessTransaction1.getScheduleStageTwo(departmentId,processCode,locationId);
		return convertScheduleStageTwoDBOToDTO(list);
	}

	private Flux<EmpApplnEntriesDTO> convertScheduleStageTwoDBOToDTO(List<Tuple> list) {
		List<Integer> idList = !Utils.isNullOrEmpty(list) ? list.stream().map(s -> Integer.parseInt(s.get("id").toString())).collect(Collectors.toList()) : new ArrayList<Integer>();
		List<EmpApplnEntriesDTO> empApplnEntriesDTOList = new ArrayList<EmpApplnEntriesDTO>();
		List<Tuple> PanelList = !Utils.isNullOrEmpty(list) ? interviewProcessTransaction1.getPanelMemberCount(idList) : null;
		List<Tuple> scoreEnteredPanelCountList = !Utils.isNullOrEmpty(list) ? interviewProcessTransaction1.getScoreEnteredPanelCount(idList) : null;
		List<Tuple> totalScore = !Utils.isNullOrEmpty(list) ? interviewProcessTransaction1.getTotalScore(idList) : null;
		Map<Integer,Integer> panelMap = !Utils.isNullOrEmpty(PanelList) ? PanelList.stream().collect(Collectors.toMap( s -> Integer.parseInt(s.get("Ids").toString()), s -> !Utils.isNullOrEmpty(s.get("countt")) ? Integer.parseInt(s.get("countt").toString()) : 0)) : new HashMap<Integer, Integer>();
		Map<Integer,Integer> scoreEnteredPanelCountMap = !Utils.isNullOrEmpty(scoreEnteredPanelCountList) ? scoreEnteredPanelCountList.stream().collect(Collectors.toMap( s -> Integer.parseInt(s.get("Ids").toString()), s -> !Utils.isNullOrEmpty(s.get("countt")) ? Integer.parseInt(s.get("countt").toString()) : 0)) : new HashMap<Integer, Integer>(); 
		Map<Integer,Integer> totalScoreMap = !Utils.isNullOrEmpty(totalScore) ? totalScore.stream().collect(Collectors.toMap( s -> Integer.parseInt(s.get("Ids").toString()), s -> !Utils.isNullOrEmpty(s.get("summ")) ? Integer.parseInt(s.get("summ").toString()) : 0)) : new HashMap<Integer, Integer>(); 
		List<Integer> pointIds = !Utils.isNullOrEmpty(list) ? list.stream().filter(s -> !Utils.isNullOrEmpty(s.get("pointOfContactUsersId"))).map(s -> (Integer) s.get("pointOfContactUsersId")).collect(Collectors.toList()) : new ArrayList<Integer>();		
		List<Tuple> pointList = !Utils.isNullOrEmpty(pointIds) ? interviewProcessTransaction1.getPointOfContact(pointIds) : null;
		Map<Integer, String> pointMap = !Utils.isNullOrEmpty(pointList) ? pointList.stream().filter(s -> (!Utils.isNullOrEmpty(s.get("erpUserId")) && !Utils.isNullOrEmpty(s.get("name")) && !Utils.isNullOrEmpty(s.get("department")))).collect(Collectors.toMap(s -> Integer.parseInt( s.get("erpUserId").toString()), s -> String.valueOf(s.get("name"))+ String.valueOf(s.get("department")))): new HashMap<Integer, String>();
		if(!Utils.isNullOrEmpty(list)) {
			list.forEach(tuple -> {
				EmpApplnEntriesDTO empApplnEntriesDTO = new EmpApplnEntriesDTO();
				if(!Utils.isNullOrEmpty(tuple.get("id"))) {
					empApplnEntriesDTO.setApplicantId(tuple.get("id").toString());
				}
				if(!Utils.isNullOrEmpty(tuple.get("applicationNo"))) {
					empApplnEntriesDTO.setApplicantNumber(tuple.get("applicationNo").toString());
				}
				if(!Utils.isNullOrEmpty(tuple.get("name"))) {
					empApplnEntriesDTO.setApplicantName(tuple.get("name").toString());
				}
				if(!Utils.isNullOrEmpty(tuple.get("departmentId"))) {
					empApplnEntriesDTO.setShortlistedDepartment(new SelectDTO());
					empApplnEntriesDTO.getShortlistedDepartment().setValue(String.valueOf(tuple.get("departmentId")));
					empApplnEntriesDTO.getShortlistedDepartment().setLabel(String.valueOf(tuple.get("departmentName")));
				}
//				if(!Utils.isNullOrEmpty(tuple.get("photoUrl"))) {
//					empApplnEntriesDTO.setProfilePhotoUrl(tuple.get("photoUrl").toString());
//				}
				if(!Utils.isNullOrEmpty(tuple.get("file_name_unique")) && !Utils.isNullOrEmpty(tuple.get("upload_process_code")) && !Utils.isNullOrEmpty(tuple.get("file_name_original"))) {
					FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
					fileUploadDownloadDTO.setActualPath(tuple.get("file_name_unique").toString());
					fileUploadDownloadDTO.setProcessCode(tuple.get("upload_process_code").toString());
					fileUploadDownloadDTO.setOriginalFileName(tuple.get("file_name_original").toString());
					aWSS3FileStorageService.generatePresignedUrlForDownload(fileUploadDownloadDTO);
					empApplnEntriesDTO.setProfilePhotoUrl(fileUploadDownloadDTO.getPreSignedUrl());
				}
				empApplnEntriesDTO.setEmpApplnInterviewSchedulesDTO(new EmpApplnInterviewSchedulesDTO());
				if(!Utils.isNullOrEmpty(tuple.get("interviewId"))) {
					empApplnEntriesDTO.getEmpApplnInterviewSchedulesDTO().setId(tuple.get("interviewId").toString());
				}
				if(!Utils.isNullOrEmpty(tuple.get("interviewvenue"))) {
					empApplnEntriesDTO.getEmpApplnInterviewSchedulesDTO().setInterviewVenue(tuple.get("interviewvenue").toString());
				}
				if(!Utils.isNullOrEmpty(tuple.get("interviewDateTime"))) {
					empApplnEntriesDTO.getEmpApplnInterviewSchedulesDTO().setInterviewDateTime1(Utils.convertStringDateTimeToLocalDateTime(tuple.get("interviewDateTime").toString()));
				}
//				if(!Utils.isNullOrEmpty(tuple.get("pointOfContactUsersId"))) {
//					empApplnEntriesDTO.getEmpApplnInterviewSchedulesDTO().setPointOfContactUsersId(tuple.get("pointOfContactUsersId").toString());
//				}
				if(!Utils.isNullOrEmpty(pointMap)){
					if(!Utils.isNullOrEmpty(tuple.get("pointOfContactUsersId"))) {
						if(pointMap.containsKey(Integer.parseInt(String.valueOf(tuple.get("pointOfContactUsersId"))))) {
							empApplnEntriesDTO.getEmpApplnInterviewSchedulesDTO().setPointofContact(new SelectDTO());
							empApplnEntriesDTO.getEmpApplnInterviewSchedulesDTO().getPointofContact().setValue(String.valueOf(tuple.get("pointOfContactUsersId")));
							empApplnEntriesDTO.getEmpApplnInterviewSchedulesDTO().getPointofContact().setLabel(pointMap.get(Integer.parseInt(String.valueOf(tuple.get("pointOfContactUsersId")))));
						}
					}
				}
				if(!Utils.isNullOrEmpty(tuple.get("processCode"))) {
					empApplnEntriesDTO.setApplicationStatus(tuple.get("processCode").toString());
				}
				if(!Utils.isNullOrEmpty(tuple.get("email"))) {
					empApplnEntriesDTO.setApplicantEmail(tuple.get("email").toString());
				}
				if(!Utils.isNullOrEmpty(tuple.get("mobileCountryCode")) && !Utils.isNullOrEmpty(tuple.get("mobileNo"))) {
					empApplnEntriesDTO.setApplicantMobile(tuple.get("mobileCountryCode").toString()+tuple.get("mobileNo").toString());
				}
				if(!Utils.isNullOrEmpty(tuple.get("hodComments"))) {
					empApplnEntriesDTO.getEmpApplnInterviewSchedulesDTO().setComments(tuple.get("hodComments").toString());
				}		
				if(panelMap.containsKey(Integer.parseInt(tuple.get("id").toString()))) {
					Integer panelCount = panelMap.get(Integer.parseInt(tuple.get("id").toString()));
					if(!Utils.isNullOrEmpty(panelCount) || (panelCount!=0)) {
						if(scoreEnteredPanelCountMap.containsKey(Integer.parseInt(tuple.get("id").toString()))) {
							Integer scoreEnteredPanelCount = scoreEnteredPanelCountMap.get(Integer.parseInt(tuple.get("id").toString()));
							if(panelCount.equals(scoreEnteredPanelCount)) {
								if(!Utils.isNullOrEmpty(totalScoreMap)) {
									Integer total = totalScoreMap.get(Integer.parseInt(tuple.get("id").toString()));
									if(!Utils.isNullOrEmpty(total) || (total != 0)) {
										int score = total.intValue();
										int totalPanel = scoreEnteredPanelCount.intValue();
										float totalScoreFloat = (float) score / totalPanel;
										empApplnEntriesDTO.setTotalScore(totalScoreFloat);
									}
								}
							}
							if(scoreEnteredPanelCount<panelCount) {
								if(!Utils.isNullOrEmpty(totalScoreMap)) {
									Integer total = totalScoreMap.get(Integer.parseInt(tuple.get("id").toString()));
									if(!Utils.isNullOrEmpty(total) || (total != 0)) {
										int score = total.intValue();
										int totalPanel = scoreEnteredPanelCount.intValue();
										float totalScoreFloat = (float) score / totalPanel;
										empApplnEntriesDTO.setTotalScore(totalScoreFloat);
									}
								}
							}
						}
					}
				}
				empApplnEntriesDTOList.add(empApplnEntriesDTO);
			});
		}
		return Flux.fromIterable(empApplnEntriesDTOList);
	}

	public boolean submitInterviewScheduleDetailsStageOne(List<EmpApplnEntriesDTO> empApplnEntriesDTOList, String userId) {
		List<Object> objects = new ArrayList<Object>();
		boolean isSaved = false;
		List<Integer> applicationIds = !Utils.isNullOrEmpty(empApplnEntriesDTOList) ? empApplnEntriesDTOList.stream()
				.map(dto -> Integer.parseInt(dto.getApplicantId()))
				.collect(Collectors.toList()) : new ArrayList<Integer>();
		List<Integer> interviewScheduleIds = !Utils.isNullOrEmpty(empApplnEntriesDTOList) ? empApplnEntriesDTOList.stream().filter(s -> !Utils.isNullOrEmpty(s.getEmpApplnInterviewSchedulesDTO().getId()))
				.map(dto -> Integer.parseInt(dto.getEmpApplnInterviewSchedulesDTO().getId()))
				.collect(Collectors.toList()): new ArrayList<>();
		List<EmpApplnEntriesDBO> empApplnEntriesDBOList = !Utils.isNullOrEmpty(applicationIds) ? interviewProcessTransaction1.getEmpApplnEntriesDBO(applicationIds) : new ArrayList<EmpApplnEntriesDBO>();
		Map<Integer,EmpApplnEntriesDBO> empApplnEntriesDBOMap  = !Utils.isNullOrEmpty(empApplnEntriesDBOList) ? empApplnEntriesDBOList.stream().collect(Collectors.toMap(s -> s.getId(), s -> s)) : new HashMap<Integer, EmpApplnEntriesDBO>();
		List<EmpApplnInterviewSchedulesDBO> empApplnInterviewSchedulesDBOList = !Utils.isNullOrEmpty(interviewScheduleIds) ? interviewProcessTransaction1.getInterviewScheduleDetails(interviewScheduleIds) : new ArrayList<EmpApplnInterviewSchedulesDBO>();
		Map<Integer, EmpApplnInterviewSchedulesDBO> empApplnInterviewSchedulesDBOMap = !Utils.isNullOrEmpty(empApplnInterviewSchedulesDBOList) ? empApplnInterviewSchedulesDBOList.stream().collect(Collectors.toMap(s -> s.getEmpApplnInterviewSchedulesId(), s -> s)) : new HashMap<Integer, EmpApplnInterviewSchedulesDBO>();
		List<ErpWorkFlowProcessDBO> erpWorkFlowProcessDBOList = interviewProcessTransaction1.getWorkFlowProcess();
		Map<String, ErpWorkFlowProcessDBO> workFlowMap = !Utils.isNullOrEmpty(erpWorkFlowProcessDBOList)? erpWorkFlowProcessDBOList.stream().collect(Collectors.toMap(s -> s.getProcessCode(), s -> s)): new HashMap<String, ErpWorkFlowProcessDBO>();
		List<ErpEmailsDBO> emailsListApplicant = new ArrayList<ErpEmailsDBO>();
		List<ErpSmsDBO> smsListApplicant = new ArrayList<ErpSmsDBO>();
		List<ErpNotificationsDBO> notificationListApplicant = new ArrayList<ErpNotificationsDBO>();
		List<ErpEmailsDBO> emailsListEmployee = new ArrayList<ErpEmailsDBO>();
		List<ErpSmsDBO> smsListEmployee = new ArrayList<ErpSmsDBO>();
		List<ErpNotificationsDBO> notificationListEmployee = new ArrayList<ErpNotificationsDBO>();
		Tuple workFlowProcess = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("EMP_STAGE1_HOD_RESCHEDULED");
		String notificationCodeApplicant = "EMP_STAGE1_RESCHEDULED_APPLICANT";
		String notificationCodeEmployee = "EMP_STAGE1_RESCHEDULED_EMPLOYEE";
		int	workflowProcessId = Integer.parseInt(workFlowProcess.get("erp_work_flow_process_id").toString());
		ErpWorkFlowProcessNotificationsDBO erpWorkFlowProcessNotificationsDBOApplicant = interviewProcessTransaction1.getErpWorkFlowProcessNotification(workflowProcessId,notificationCodeApplicant);
		ErpWorkFlowProcessNotificationsDBO erpWorkFlowProcessNotificationsDBOEmployee = interviewProcessTransaction1.getErpWorkFlowProcessNotification(workflowProcessId,notificationCodeEmployee);
		List<Integer> contactAndInternal = new ArrayList<Integer>();
		List<Integer> contactAndExternal = new ArrayList<Integer>();
		if(!Utils.isNullOrEmpty(empApplnEntriesDTOList)) {
			empApplnEntriesDTOList.forEach(employeeIds -> {
				if(!Utils.isNullOrEmpty(employeeIds.getEmpApplnInterviewSchedulesDTO())) {
					if(!Utils.isNullOrEmpty(employeeIds.getEmpApplnInterviewSchedulesDTO().getInternalPanelists())) {
						employeeIds.getEmpApplnInterviewSchedulesDTO().getInternalPanelists().forEach(internal -> {
							if(!Utils.isNullOrEmpty(internal.getValue())) {
								contactAndInternal.add(Integer.parseInt(internal.getValue()));
							}
						});
					}
					if(!Utils.isNullOrEmpty(employeeIds.getEmpApplnInterviewSchedulesDTO().getExternalPanelists())) {
						employeeIds.getEmpApplnInterviewSchedulesDTO().getExternalPanelists().forEach(external -> {
							if(!Utils.isNullOrEmpty(external.getValue())) {
								if(!Utils.isNullOrEmpty(external.status)) {
									if(external.status) {
										contactAndInternal.add(Integer.parseInt(external.getValue()));
									}else {
										contactAndExternal.add(Integer.parseInt(external.getValue()));
									}
								}
							}
						});
					}
				}
			});
		}
		AtomicBoolean atomicBoolean = new AtomicBoolean();
		atomicBoolean.set(false);
		List<Tuple> tuple = !Utils.isNullOrEmpty(contactAndInternal) ? interviewProcessTransaction1.getContactAndInternal(contactAndInternal) : new ArrayList<Tuple>();
		Map<Integer,Tuple> tupleMapContacMap = !Utils.isNullOrEmpty(tuple) ? tuple.stream().collect(Collectors.toMap(s -> Integer.parseInt(s.get("userId").toString()),  s -> s)) : new HashMap<Integer, Tuple>();
		
		List<Tuple> externalList = !Utils.isNullOrEmpty(contactAndExternal) ? interviewProcessTransaction1.getExternal(contactAndExternal) : new ArrayList<Tuple>();
		Map<Integer,Tuple> externalMap = !Utils.isNullOrEmpty(externalList) ? externalList.stream().collect(Collectors.toMap(s -> Integer.parseInt(s.get("emp_interview_university_externals_id").toString()),  s -> s)) : new HashMap<Integer, Tuple>();

		if(!Utils.isNullOrEmpty(empApplnEntriesDTOList)) {
			empApplnEntriesDTOList.forEach(dto -> {
				if(empApplnEntriesDBOMap.containsKey(Integer.parseInt(dto.getApplicantId()))) {
					if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO())) {
						EmpApplnEntriesDBO empApplnEntriesDBO = empApplnEntriesDBOMap.get(Integer.parseInt(dto.getApplicantId()));
						empApplnEntriesDBO.setModifiedUsersId(Integer.parseInt(userId));
						EmpApplnInterviewSchedulesDBO empApplnInterviewSchedulesDBO = new EmpApplnInterviewSchedulesDBO();
						empApplnInterviewSchedulesDBO.setCreatedUsersId(Integer.parseInt(userId));
						empApplnInterviewSchedulesDBO.setRecordStatus('A');
						empApplnInterviewSchedulesDBO.setInterviewRound(1);
//						if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getPointOfContactUsersId())) {
//							empApplnInterviewSchedulesDBO.setErpUsersDBO(new ErpUsersDBO());
//							empApplnInterviewSchedulesDBO.getErpUsersDBO().setId(Integer.parseInt(dto.getEmpApplnInterviewSchedulesDTO().getPointOfContactUsersId()));
//						}
						if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getPointofContact())) {
							if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getPointofContact().getValue())){
								empApplnInterviewSchedulesDBO.setErpUsersDBO(new ErpUsersDBO());
								empApplnInterviewSchedulesDBO.getErpUsersDBO().setId(Integer.parseInt(dto.getEmpApplnInterviewSchedulesDTO().getPointofContact().getValue()));
							}
						}
						if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getInterviewVenue())) {
							empApplnInterviewSchedulesDBO.setInterviewVenue(dto.getEmpApplnInterviewSchedulesDTO().getInterviewVenue());
						}
						if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getInterviewDateTime1())) {
							empApplnInterviewSchedulesDBO.setInterviewDateTime(dto.getEmpApplnInterviewSchedulesDTO().getInterviewDateTime1());
						}
						Map<Integer,EmpApplnInterviewPanelDBO> existEmpApplnInterviewPanelDBOMap = new HashMap<Integer, EmpApplnInterviewPanelDBO>();
						Set<EmpApplnInterviewPanelDBO> empApplnInterviewPanelDBOSet = new HashSet<EmpApplnInterviewPanelDBO>();
						if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getId()) && empApplnInterviewSchedulesDBOMap.containsKey(Integer.parseInt(dto.getEmpApplnInterviewSchedulesDTO().getId()))) {
							EmpApplnInterviewSchedulesDBO empApplnInterviewSchedulesDBO1 = empApplnInterviewSchedulesDBOMap.get(Integer.parseInt(dto.getEmpApplnInterviewSchedulesDTO().getId()));
							if(!Utils.isNullOrEmpty(empApplnInterviewSchedulesDBO1.getEmpApplnInterviewPanelDBO())) {
								empApplnInterviewSchedulesDBO1.getEmpApplnInterviewPanelDBO().forEach(internalPanelListDbo -> {
									if(internalPanelListDbo.recordStatus == 'A') {
										existEmpApplnInterviewPanelDBOMap.put(internalPanelListDbo.id, internalPanelListDbo);
									}
								});
							}
						}
						if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO())) {
							EmpApplnInterviewSchedulesDBO empApplnInterviewSchedulesDBO1 = empApplnInterviewSchedulesDBO;
							dto.getEmpApplnInterviewSchedulesDTO().getInternalPanelists().forEach(internalPanelListDto -> {
								EmpApplnInterviewPanelDBO empApplnInterviewPanelDBO = new EmpApplnInterviewPanelDBO();
								empApplnInterviewPanelDBO.setCreatedUsersId(Integer.parseInt(userId));
								empApplnInterviewPanelDBO.setRecordStatus('A');
								empApplnInterviewPanelDBO.setInternalPanel(true);
								empApplnInterviewPanelDBO.setErpUsersDBO(new ErpUsersDBO());
								empApplnInterviewPanelDBO.getErpUsersDBO().setId(Integer.parseInt(internalPanelListDto.getValue()));
								empApplnInterviewPanelDBO.setEmpApplnInterviewSchedulesDBO(empApplnInterviewSchedulesDBO1);
								empApplnInterviewPanelDBOSet.add(empApplnInterviewPanelDBO);
								if(!Utils.isNullOrEmpty(dto.getApplicationStatus())) {
									if(dto.getApplicationStatus().trim().equalsIgnoreCase("EMP_STAGE1_PO_APPROVES_SCHEDULE") || dto.getApplicationStatus().trim().equalsIgnoreCase("EMP_STAGE1_HOD_RESCHEDULED")){
										if(tupleMapContacMap.containsKey(Integer.parseInt(internalPanelListDto.getValue()))) {
											Tuple point = tupleMapContacMap.get(Integer.parseInt(internalPanelListDto.getValue()));
											if(!Utils.isNullOrEmpty(point.get("email"))) {
												dto.setEmployeeEmail(point.get("email").toString());
											}
											if(!Utils.isNullOrEmpty(point.get("mobileNo")) && !Utils.isNullOrEmpty(point.get("mobileCountry"))) {
												dto.setEmployeeMobileNo(point.get("mobileNo").toString() + point.get("mobileCountry").toString());
											}
											if(!Utils.isNullOrEmpty(point.get("empName"))){
												dto.setEmployeeName(point.get("empName").toString());
											}
											if(!Utils.isNullOrEmpty(erpWorkFlowProcessNotificationsDBOEmployee)) {
												if(erpWorkFlowProcessNotificationsDBOEmployee.getIsEmailActivated()) {
													ErpTemplateDBO erpTemplateDBO = erpWorkFlowProcessNotificationsDBOEmployee.getErpEmailsTemplateDBO();
													if(!Utils.isNullOrEmpty(erpTemplateDBO))
														emailsListEmployee.add(getEmailDBO(erpTemplateDBO,empApplnEntriesDBO.getId(),userId,dto,"Employee"));
												}
												if(erpWorkFlowProcessNotificationsDBOEmployee.getIsSmsActivated()) {
													ErpTemplateDBO erpTemplateDBO = erpWorkFlowProcessNotificationsDBOEmployee.getErpSmsTemplateDBO();
													if(!Utils.isNullOrEmpty(erpTemplateDBO))
														smsListEmployee.add(getSMSDBO(erpTemplateDBO,empApplnEntriesDBO.getId(),userId,dto,"Employee"));
												}
												if(erpWorkFlowProcessNotificationsDBOEmployee.getIsNotificationActivated()) {
													notificationListEmployee.add(getNotificationsDBO(empApplnEntriesDBO.getId(),Integer.parseInt(userId),"Employee",erpWorkFlowProcessNotificationsDBOEmployee));
												}
											}
										}
									}
								}
							});
							if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getExternalPanelists())) {
								dto.getEmpApplnInterviewSchedulesDTO().getExternalPanelists().forEach(externalPanelListDto -> {
									if(!Utils.isNullOrEmpty(externalPanelListDto.status)) {
										if((externalPanelListDto.status == true & dto.getIsAcademic() == true) || (externalPanelListDto.status == false & dto.getIsAcademic() == false)) {
											EmpApplnInterviewPanelDBO empApplnInterviewPanelDBO = new EmpApplnInterviewPanelDBO();
											empApplnInterviewPanelDBO.setCreatedUsersId(Integer.parseInt(userId));
											empApplnInterviewPanelDBO.setRecordStatus('A');
											empApplnInterviewPanelDBO.setErpUsersDBO(new ErpUsersDBO());
											empApplnInterviewPanelDBO.getErpUsersDBO().setId(Integer.parseInt(externalPanelListDto.getValue()));
											empApplnInterviewPanelDBO.setInternalPanel(false);
											empApplnInterviewPanelDBO.setEmpApplnInterviewSchedulesDBO(empApplnInterviewSchedulesDBO1);
											empApplnInterviewPanelDBOSet.add(empApplnInterviewPanelDBO);
											if(!Utils.isNullOrEmpty(dto.getApplicationStatus())) {
												if(dto.getApplicationStatus().trim().equalsIgnoreCase("EMP_STAGE1_PO_APPROVES_SCHEDULE") || dto.getApplicationStatus().trim().equalsIgnoreCase("EMP_STAGE1_HOD_RESCHEDULED")) {
													if(tupleMapContacMap.containsKey(Integer.parseInt(externalPanelListDto.getValue()))) {
														Tuple point = tupleMapContacMap.get(Integer.parseInt(externalPanelListDto.getValue()));
														if(!Utils.isNullOrEmpty(point.get("email"))) {
															dto.setEmployeeEmail(point.get("email").toString());
														}
														if(!Utils.isNullOrEmpty(point.get("mobileNo")) && !Utils.isNullOrEmpty(point.get("mobileCountry"))) {
															dto.setEmployeeMobileNo(point.get("mobileNo").toString() + point.get("mobileCountry").toString());
														}
														if(!Utils.isNullOrEmpty(point.get("empName"))){
															dto.setEmployeeName(point.get("empName").toString());
														}
														if(!Utils.isNullOrEmpty(erpWorkFlowProcessNotificationsDBOEmployee)) {
															if(erpWorkFlowProcessNotificationsDBOEmployee.getIsEmailActivated()) {
																ErpTemplateDBO erpTemplateDBO = erpWorkFlowProcessNotificationsDBOEmployee.getErpEmailsTemplateDBO();
																if(!Utils.isNullOrEmpty(erpTemplateDBO))
																	emailsListEmployee.add(getEmailDBO(erpTemplateDBO,empApplnEntriesDBO.getId(),userId,dto,"Employee"));
															}
															if(erpWorkFlowProcessNotificationsDBOEmployee.getIsSmsActivated()) {
																ErpTemplateDBO erpTemplateDBO = erpWorkFlowProcessNotificationsDBOEmployee.getErpSmsTemplateDBO();
																if(!Utils.isNullOrEmpty(erpTemplateDBO))
																	smsListEmployee.add(getSMSDBO(erpTemplateDBO,empApplnEntriesDBO.getId(),userId,dto,"Employee"));
															}
															if(erpWorkFlowProcessNotificationsDBOEmployee.getIsNotificationActivated()) {
																notificationListEmployee.add(getNotificationsDBO(empApplnEntriesDBO.getId(),Integer.parseInt(userId),"Employee",erpWorkFlowProcessNotificationsDBOEmployee));
															}
														}
													}
												}
											}
										}else {
											EmpApplnInterviewPanelDBO empApplnInterviewPanelDBO = new EmpApplnInterviewPanelDBO();
											empApplnInterviewPanelDBO.setCreatedUsersId(Integer.parseInt(userId));
											empApplnInterviewPanelDBO.setRecordStatus('A');
											empApplnInterviewPanelDBO.setEmpInterviewUniversityExternalsDBO(new EmpInterviewUniversityExternalsDBO());
											empApplnInterviewPanelDBO.getEmpInterviewUniversityExternalsDBO().setId(Integer.parseInt(externalPanelListDto.getValue()));
											empApplnInterviewPanelDBO.setInternalPanel(false);
											empApplnInterviewPanelDBO.setEmpApplnInterviewSchedulesDBO(empApplnInterviewSchedulesDBO1);
											empApplnInterviewPanelDBOSet.add(empApplnInterviewPanelDBO);
											if(!Utils.isNullOrEmpty(dto.getApplicationStatus())) {
												if(dto.getApplicationStatus().trim().equalsIgnoreCase("EMP_STAGE1_PO_APPROVES_SCHEDULE") || dto.getApplicationStatus().trim().equalsIgnoreCase("EMP_STAGE1_HOD_RESCHEDULED")) {
													if(externalMap.containsKey(Integer.parseInt(externalPanelListDto.getValue()))) {
														Tuple point = externalMap.get(Integer.parseInt(externalPanelListDto.getValue()));
														if(!Utils.isNullOrEmpty(point.get("email"))) {
															dto.setEmployeeEmail(point.get("email").toString());
														}
														if(!Utils.isNullOrEmpty(point.get("mobileNo")) && !Utils.isNullOrEmpty(point.get("mobileCountry"))) {
															dto.setEmployeeMobileNo(point.get("mobileNo").toString() + point.get("mobileCountry").toString());
														}
														if(!Utils.isNullOrEmpty(point.get("empName"))){
															dto.setEmployeeName(point.get("empName").toString());
														}
														if(!Utils.isNullOrEmpty(erpWorkFlowProcessNotificationsDBOEmployee)) {
															if(erpWorkFlowProcessNotificationsDBOEmployee.getIsEmailActivated()) {
																ErpTemplateDBO erpTemplateDBO = erpWorkFlowProcessNotificationsDBOEmployee.getErpEmailsTemplateDBO();
																if(!Utils.isNullOrEmpty(erpTemplateDBO))
																	emailsListEmployee.add(getEmailDBO(erpTemplateDBO,empApplnEntriesDBO.getId(),userId,dto,"Employee"));
															}
															if(erpWorkFlowProcessNotificationsDBOEmployee.getIsSmsActivated()) {
																ErpTemplateDBO erpTemplateDBO = erpWorkFlowProcessNotificationsDBOEmployee.getErpSmsTemplateDBO();
																if(!Utils.isNullOrEmpty(erpTemplateDBO))
																	smsListEmployee.add(getSMSDBO(erpTemplateDBO,empApplnEntriesDBO.getId(),userId,dto,"Employee"));
															}
															if(erpWorkFlowProcessNotificationsDBOEmployee.getIsNotificationActivated()) {
																notificationListEmployee.add(getNotificationsDBO(empApplnEntriesDBO.getId(),Integer.parseInt(userId),"Employee",erpWorkFlowProcessNotificationsDBOEmployee));
															}
														}
													}
												}
											}
										}
									}
								});
							}	
						}
						if(!Utils.isNullOrEmpty(existEmpApplnInterviewPanelDBOMap)) {
							existEmpApplnInterviewPanelDBOMap.forEach((entry, value)-> {
								value.setModifiedUsersId(Integer.parseInt(userId));
								value.setRecordStatus('D');
								empApplnInterviewPanelDBOSet.add(value);
							});
						}
						empApplnInterviewSchedulesDBO.setEmpApplnInterviewPanelDBO(empApplnInterviewPanelDBOSet);
						Set<EmpApplnInterviewSchedulesDBO> empApplnInterviewSchedulesDBOSet = new HashSet<>();
						if(!Utils.isNullOrEmpty(empApplnInterviewSchedulesDBOMap)) {
							empApplnInterviewSchedulesDBOMap.forEach((entry, value)-> {
								value.setModifiedUsersId(Integer.parseInt(userId));
								value.setRecordStatus('D');
								empApplnInterviewSchedulesDBOSet.add(value);
							});
						}
						empApplnInterviewSchedulesDBO.setEmpApplnEntriesDBO(empApplnEntriesDBO);
						empApplnInterviewSchedulesDBOSet.add(empApplnInterviewSchedulesDBO); 
						empApplnEntriesDBO.setEmpApplnInterviewSchedulesDBOs(empApplnInterviewSchedulesDBOSet);
						if(!Utils.isNullOrEmpty(workFlowMap)) {
							if(workFlowMap.containsKey(dto.getApplicationStatus())) {
								if(dto.getApplicationStatus().trim().equalsIgnoreCase("EMP_STAGE1_HOD_SHORTLISTED")) {
									ErpWorkFlowProcessDBO erpWorkFlowProcessDBO = workFlowMap.get("EMP_STAGE1_HOD_SCHEDULED");
									empApplnEntriesDBO.setApplicationCurrentProcessStatus(erpWorkFlowProcessDBO);
									empApplnEntriesDBO.setApplicationStatusTime(LocalDateTime.now());
									ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
									erpWorkFlowProcessStatusLogDBO.setEntryId(empApplnEntriesDBO.getId());
									erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
									erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
									erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(erpWorkFlowProcessDBO);
									objects.add(erpWorkFlowProcessStatusLogDBO);
								}else if(dto.getApplicationStatus().trim().equalsIgnoreCase("EMP_STAGE1_PO_APPROVES_SCHEDULE") || dto.getApplicationStatus().trim().equalsIgnoreCase("EMP_STAGE1_HOD_RESCHEDULED") 
										|| dto.getApplicationStatus().trim().equalsIgnoreCase("EMP_STAGE1_APPLICANT_ACCEPTED") || dto.getApplicationStatus().trim().equalsIgnoreCase("EMP_STAGE1_APPLICANT_DECLINED")){
									atomicBoolean.set(true);
									ErpWorkFlowProcessDBO erpWorkFlowProcessDBO = workFlowMap.get("EMP_STAGE1_HOD_RESCHEDULED");
									empApplnEntriesDBO.setApplicationCurrentProcessStatus(erpWorkFlowProcessDBO);
									empApplnEntriesDBO.setApplicationStatusTime(LocalDateTime.now());
									empApplnEntriesDBO.setApplicantCurrentProcessStatus(erpWorkFlowProcessDBO);
									empApplnEntriesDBO.setApplicantStatusTime(LocalDateTime.now());
									ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
									erpWorkFlowProcessStatusLogDBO.setEntryId(empApplnEntriesDBO.getId());
									erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
									erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
									erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(erpWorkFlowProcessDBO);
									if(!Utils.isNullOrEmpty(erpWorkFlowProcessNotificationsDBOApplicant)) {
										if(erpWorkFlowProcessNotificationsDBOApplicant.getIsEmailActivated()) {
											ErpTemplateDBO erpTemplateDBO = erpWorkFlowProcessNotificationsDBOApplicant.getErpEmailsTemplateDBO();
											if(!Utils.isNullOrEmpty(erpTemplateDBO))
												emailsListApplicant.add(getEmailDBO(erpTemplateDBO,empApplnEntriesDBO.getId(),userId,dto,null));
										}
										if(erpWorkFlowProcessNotificationsDBOApplicant.getIsSmsActivated()) {
											ErpTemplateDBO erpTemplateDBO = erpWorkFlowProcessNotificationsDBOApplicant.getErpSmsTemplateDBO();
											if(!Utils.isNullOrEmpty(erpTemplateDBO))
												smsListApplicant.add(getSMSDBO(erpTemplateDBO,empApplnEntriesDBO.getId(),userId,dto,null));
										}
										if(erpWorkFlowProcessNotificationsDBOApplicant.getIsNotificationActivated()) {
											notificationListApplicant.add(getNotificationsDBO(empApplnEntriesDBO.getId(),Integer.parseInt(userId),null,erpWorkFlowProcessNotificationsDBOApplicant));
										}
									}
									objects.add(erpWorkFlowProcessStatusLogDBO);
								}
							}
						}
						objects.add(empApplnEntriesDBO);
					}
				}
			});
		}
		isSaved = interviewProcessTransaction1.saveOrUpdate(objects);
		Set<Integer> approversIdSet = new HashSet<Integer>();
		approversIdSet.add(Integer.parseInt(userId));
		if(isSaved && atomicBoolean.get()) {
			commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(workflowProcessId,"EMP_STAGE1_RESCHEDULED_APPLICANT",approversIdSet,notificationListApplicant,smsListApplicant,emailsListApplicant);
			commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(workflowProcessId,"EMP_STAGE1_RESCHEDULED_EMPLOYEE",approversIdSet,notificationListEmployee,smsListEmployee,emailsListEmployee);	
		}
		return isSaved;
	}

	public boolean submitInterviewScheduleDetailsStageTwo(List<EmpApplnEntriesDTO> empApplnEntriesDTOList, String userId) {
		List<Object> objects = new ArrayList<Object>();
		boolean isSaved = false;
		List<Integer> applicationIds = !Utils.isNullOrEmpty(empApplnEntriesDTOList) ? empApplnEntriesDTOList.stream()
				.map(dto -> Integer.parseInt(dto.getApplicantId()))
				.collect(Collectors.toList()) : new ArrayList<Integer>();
		List<Integer> interviewScheduleIds = !Utils.isNullOrEmpty(empApplnEntriesDTOList) ? empApplnEntriesDTOList.stream().filter(s -> !Utils.isNullOrEmpty(s.getEmpApplnInterviewSchedulesDTO().getId()))
				.map(dto -> Integer.parseInt(dto.getEmpApplnInterviewSchedulesDTO().getId()))
				.collect(Collectors.toList()): new ArrayList<>();
		List<EmpApplnEntriesDBO> empApplnEntriesDBOList = !Utils.isNullOrEmpty(applicationIds) ? interviewProcessTransaction1.getEmpApplnEntriesDBO(applicationIds) : new ArrayList<EmpApplnEntriesDBO>();
		Map<Integer,EmpApplnEntriesDBO> empApplnEntriesDBOMap  = !Utils.isNullOrEmpty(empApplnEntriesDBOList) ? empApplnEntriesDBOList.stream().collect(Collectors.toMap(s -> s.getId(), s -> s)) : new HashMap<Integer, EmpApplnEntriesDBO>();
		List<EmpApplnInterviewSchedulesDBO> empApplnInterviewSchedulesDBOList = !Utils.isNullOrEmpty(interviewScheduleIds) ? interviewProcessTransaction1.getInterviewScheduleDetailsTwo(interviewScheduleIds) : new ArrayList<EmpApplnInterviewSchedulesDBO>();
		Map<Integer, EmpApplnInterviewSchedulesDBO> empApplnInterviewSchedulesDBOMap = !Utils.isNullOrEmpty(empApplnInterviewSchedulesDBOList) ? empApplnInterviewSchedulesDBOList.stream().collect(Collectors.toMap(s -> s.getEmpApplnInterviewSchedulesId(), s -> s)) : new HashMap<Integer, EmpApplnInterviewSchedulesDBO>();
		List<ErpWorkFlowProcessDBO> erpWorkFlowProcessDBOList = interviewProcessTransaction1.getWorkFlowProcess1();
		Map<String, ErpWorkFlowProcessDBO> workFlowMap = !Utils.isNullOrEmpty(erpWorkFlowProcessDBOList)? erpWorkFlowProcessDBOList.stream().collect(Collectors.toMap(s -> s.getProcessCode(), s -> s)): new HashMap<String, ErpWorkFlowProcessDBO>();
		List<ErpEmailsDBO> emailsListApplicant = new ArrayList<ErpEmailsDBO>();
		List<ErpSmsDBO> smsListApplicant = new ArrayList<ErpSmsDBO>();
		List<ErpNotificationsDBO> notificationListApplicant = new ArrayList<ErpNotificationsDBO>();
		Tuple workFlowProcessSchedule = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("EMP_STAGE2_PO_SCHEDULED");
		String notificationCodeApplicant = "STAGE2_PO_APPROVAL_INFORM_APPLICANT";
		int	workflowProcessIdSchedule = Integer.parseInt(workFlowProcessSchedule.get("erp_work_flow_process_id").toString());
		ErpWorkFlowProcessNotificationsDBO erpWorkFlowProcessNotificationsDBOApplicant = interviewProcessTransaction1.getErpWorkFlowProcessNotification(workflowProcessIdSchedule,notificationCodeApplicant);
		if(!Utils.isNullOrEmpty(empApplnEntriesDTOList)) {
			empApplnEntriesDTOList.forEach(dto -> {
				if(empApplnEntriesDBOMap.containsKey(Integer.parseInt(dto.getApplicantId()))) {
					EmpApplnEntriesDBO empApplnEntriesDBO = empApplnEntriesDBOMap.get(Integer.parseInt(dto.getApplicantId()));
					empApplnEntriesDBO.setModifiedUsersId(Integer.parseInt(userId));
					EmpApplnInterviewSchedulesDBO empApplnInterviewSchedulesDBO = new EmpApplnInterviewSchedulesDBO(); 
					empApplnInterviewSchedulesDBO.setCreatedUsersId(Integer.parseInt(userId));
					empApplnInterviewSchedulesDBO.setRecordStatus('A');
					empApplnInterviewSchedulesDBO.setInterviewRound(2);
//					if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getPointOfContactUsersId())) {
//						empApplnInterviewSchedulesDBO.setErpUsersDBO(new ErpUsersDBO());
//						empApplnInterviewSchedulesDBO.getErpUsersDBO().setId(Integer.parseInt(dto.getEmpApplnInterviewSchedulesDTO().getPointOfContactUsersId()));
//					}
					if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getPointofContact())) {
						if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getPointofContact().getValue())){
							empApplnInterviewSchedulesDBO.setErpUsersDBO(new ErpUsersDBO());
							empApplnInterviewSchedulesDBO.getErpUsersDBO().setId(Integer.parseInt(dto.getEmpApplnInterviewSchedulesDTO().getPointofContact().getValue()));
						}
					}
					if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getInterviewVenue())) {
						empApplnInterviewSchedulesDBO.setInterviewVenue(dto.getEmpApplnInterviewSchedulesDTO().getInterviewVenue());
					}
					if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getInterviewDateTime1())) {
						empApplnInterviewSchedulesDBO.setInterviewDateTime(dto.getEmpApplnInterviewSchedulesDTO().getInterviewDateTime1());
					}
					empApplnInterviewSchedulesDBO.setEmpApplnEntriesDBO(empApplnEntriesDBO);
					Set<EmpApplnInterviewSchedulesDBO> empApplnInterviewSchedulesDBOSet = new HashSet<EmpApplnInterviewSchedulesDBO>();
					empApplnInterviewSchedulesDBOSet.add(empApplnInterviewSchedulesDBO);
					if(!Utils.isNullOrEmpty(empApplnInterviewSchedulesDBOMap)) {
						empApplnInterviewSchedulesDBOMap.forEach((entry, value)-> {
							value.setModifiedUsersId(Integer.parseInt(userId));
							value.setRecordStatus('D');
							empApplnInterviewSchedulesDBOSet.add(value);
						});
					}
					empApplnEntriesDBO.setEmpApplnInterviewSchedulesDBOs(empApplnInterviewSchedulesDBOSet);
					if(!Utils.isNullOrEmpty(workFlowMap)) {
						if(workFlowMap.containsKey(dto.getApplicationStatus())) {
							if(dto.getApplicationStatus().trim().equalsIgnoreCase("EMP_STAGE1_SELECTED")) {
								ErpWorkFlowProcessDBO erpWorkFlowProcessDBO = workFlowMap.get("EMP_STAGE2_PO_SCHEDULED");
								empApplnEntriesDBO.setApplicationCurrentProcessStatus(erpWorkFlowProcessDBO);
								empApplnEntriesDBO.setApplicationStatusTime(LocalDateTime.now());
								empApplnEntriesDBO.setApplicantCurrentProcessStatus(erpWorkFlowProcessDBO);
								empApplnEntriesDBO.setApplicationStatusTime(LocalDateTime.now());
								ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
								erpWorkFlowProcessStatusLogDBO.setEntryId(empApplnEntriesDBO.getId());
								erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
								erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
								erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(erpWorkFlowProcessDBO);
								objects.add(erpWorkFlowProcessStatusLogDBO);
							}else if(dto.getApplicationStatus().trim().equalsIgnoreCase("EMP_STAGE2_PO_SCHEDULED") || dto.getApplicationStatus().trim().equalsIgnoreCase("EMP_STAGE2_PO_RESCHEDULED") 
									|| dto.getApplicationStatus().trim().equalsIgnoreCase("EMP_STAGE2_SCHEDULE_APPLICANT_DECLINED") ||  dto.getApplicationStatus().trim().equalsIgnoreCase("EMP_STAGE2_SCHEDULE_APPLICANT_ACCEPTED")) {
								ErpWorkFlowProcessDBO erpWorkFlowProcessDBO = workFlowMap.get("EMP_STAGE2_PO_RESCHEDULED");
								empApplnEntriesDBO.setApplicationCurrentProcessStatus(erpWorkFlowProcessDBO);
								empApplnEntriesDBO.setApplicationStatusTime(LocalDateTime.now());
								empApplnEntriesDBO.setApplicationStatusTime(LocalDateTime.now());
								empApplnEntriesDBO.setApplicantCurrentProcessStatus(erpWorkFlowProcessDBO);
								ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
								erpWorkFlowProcessStatusLogDBO.setEntryId(empApplnEntriesDBO.getId());
								erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
								erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
								erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(erpWorkFlowProcessDBO);
								objects.add(erpWorkFlowProcessStatusLogDBO);
							}
						}
					}
					objects.add(empApplnEntriesDBO);
					if(!Utils.isNullOrEmpty(erpWorkFlowProcessNotificationsDBOApplicant)) {
						if(erpWorkFlowProcessNotificationsDBOApplicant.getIsEmailActivated()) {
							ErpTemplateDBO erpTemplateDBO = erpWorkFlowProcessNotificationsDBOApplicant.getErpEmailsTemplateDBO();
							if(!Utils.isNullOrEmpty(erpTemplateDBO))
								emailsListApplicant.add(getEmailDBO(erpTemplateDBO,empApplnEntriesDBO.getId(),userId,dto,null));
						}
						if(erpWorkFlowProcessNotificationsDBOApplicant.getIsSmsActivated()) {
							ErpTemplateDBO erpTemplateDBO = erpWorkFlowProcessNotificationsDBOApplicant.getErpSmsTemplateDBO();
							if(!Utils.isNullOrEmpty(erpTemplateDBO))
								smsListApplicant.add(getSMSDBO(erpTemplateDBO,empApplnEntriesDBO.getId(),userId,dto,null));
						}
						if(erpWorkFlowProcessNotificationsDBOApplicant.getIsNotificationActivated()) {
							notificationListApplicant.add(getNotificationsDBO(empApplnEntriesDBO.getId(),Integer.parseInt(userId),null,erpWorkFlowProcessNotificationsDBOApplicant));
						}
					}
				}
			});
		}
		isSaved = interviewProcessTransaction1.saveOrUpdate(objects);
		if(isSaved) {
			Set<Integer> approversIdSet = new HashSet<Integer>();
			commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(workflowProcessIdSchedule,"STAGE2_PO_APPROVAL_INFORM_APPLICANT",approversIdSet,notificationListApplicant,smsListApplicant,emailsListApplicant);
		}
		return isSaved;
	}

	public Mono<EmpApplnEntriesDTO> getCount(String departmentId, String locationId) throws Exception {
		List<Tuple> list = interviewProcessTransaction1.getCount(departmentId,locationId);
		BigInteger count = interviewProcessTransaction1.getCountOfApplicationSubmission(departmentId,locationId);
		return calculateCount(list,count);
	}

	private Mono<EmpApplnEntriesDTO> calculateCount(List<Tuple> list,BigInteger count) {
		EmpApplnEntriesDTO empApplnEntriesDTO = new EmpApplnEntriesDTO();
		if(!Utils.isNullOrEmpty(list)) {
			list.forEach(tuple -> {
				if(!Utils.isNullOrEmpty(tuple.get("processCode"))) {
					if(tuple.get("processCode").toString().trim().equalsIgnoreCase("EMP_STAGE1_HOD_SHORTLISTED")) { // || tuple.get("processCode").toString().trim().equalsIgnoreCase("EMP_STAGE1_APPLICANT_DECLINED") filter option for Applicant Declines is missing in Schedule box so removed.
						if(!Utils.isNullOrEmpty(tuple.get("countt")))
							if(!Utils.isNullOrEmpty(tuple.get("countt").toString()))
								empApplnEntriesDTO.setCountScheduleStageOne(empApplnEntriesDTO.getCountScheduleStageOne() + Integer.parseInt(tuple.get("countt").toString()));
					}
					if(tuple.get("processCode").toString().trim().equalsIgnoreCase("EMP_STAGE1_HOD_SCHEDULED")) {
						if(!Utils.isNullOrEmpty(tuple.get("countt")))
							if(!Utils.isNullOrEmpty(tuple.get("countt").toString()))
								empApplnEntriesDTO.setCountScheduleStageOneApprovel(empApplnEntriesDTO.getCountScheduleStageOneApprovel() + Integer.parseInt(tuple.get("countt").toString()));
					}
					if(tuple.get("processCode").toString().trim().equalsIgnoreCase("EMP_STAGE1_INTERVIEW_COMPLETED") || tuple.get("processCode").toString().trim().equalsIgnoreCase("EMP_STAGE1_APPLICANT_ACCEPTED") || tuple.get("processCode").toString().trim().equalsIgnoreCase("EMP_STAGE1_PO_APPROVES_SCHEDULE") || tuple.get("processCode").toString().trim().equalsIgnoreCase("EMP_STAGE1_HOD_RESCHEDULED")) {
						if(!Utils.isNullOrEmpty(tuple.get("countt")))
							if(!Utils.isNullOrEmpty(tuple.get("countt").toString()))
								empApplnEntriesDTO.setCountInterviewStatusOne(empApplnEntriesDTO.getCountInterviewStatusOne() + Integer.parseInt(tuple.get("countt").toString()));
					}
					if(tuple.get("processCode").toString().trim().equalsIgnoreCase("EMP_STAGE1_SELECTED") || tuple.get("processCode").toString().trim().equalsIgnoreCase("EMP_STAGE2_SCHEDULE_APPLICANT_DECLINED")) {
						if(!Utils.isNullOrEmpty(tuple.get("countt")))
							if(!Utils.isNullOrEmpty(tuple.get("countt").toString()))
								empApplnEntriesDTO.setCountScheduleStageTwo(empApplnEntriesDTO.getCountScheduleStageTwo() + Integer.parseInt(tuple.get("countt").toString()));
					}
					if(tuple.get("processCode").toString().trim().equalsIgnoreCase("EMP_STAGE2_SCHEDULE_APPLICANT_ACCEPTED") || tuple.get("processCode").toString().trim().equalsIgnoreCase("EMP_STAGE2_PO_SCHEDULED") || tuple.get("processCode").toString().trim().equalsIgnoreCase("EMP_STAGE2_PO_RESCHEDULED")) {
						if(!Utils.isNullOrEmpty(tuple.get("countt")))
							if(!Utils.isNullOrEmpty(tuple.get("countt").toString()))
								empApplnEntriesDTO.setCountInterviewStatusTwo(empApplnEntriesDTO.getCountInterviewStatusTwo() + Integer.parseInt(tuple.get("countt").toString()));
					}
					if(tuple.get("processCode").toString().trim().equalsIgnoreCase("EMP_STAGE2_SELECTED") || tuple.get("processCode").toString().trim().equalsIgnoreCase("EMP_STAGE3_SCHEDULE_APPLICANT_DECLINED")) {
						if(!Utils.isNullOrEmpty(tuple.get("countt")))
							if(!Utils.isNullOrEmpty(tuple.get("countt").toString()))
								empApplnEntriesDTO.setCountIcheduleStageThree(empApplnEntriesDTO.getCountIcheduleStageThree() + Integer.parseInt(tuple.get("countt").toString()));
					}
					if(tuple.get("processCode").toString().trim().equalsIgnoreCase("EMP_STAGE3_SCHEDULE_APPLICANT_ACCEPTED") || tuple.get("processCode").toString().trim().equalsIgnoreCase("EMP_STAGE3_PO_SCHEDULED") || tuple.get("processCode").toString().trim().equalsIgnoreCase("EMP_STAGE3_PO_RESCHEDULED")) {
						if(!Utils.isNullOrEmpty(tuple.get("countt")))
							if(!Utils.isNullOrEmpty(tuple.get("countt").toString()))
								empApplnEntriesDTO.setCountInterviewStatusThree(empApplnEntriesDTO.getCountInterviewStatusThree() + Integer.parseInt(tuple.get("countt").toString()));
					}
					if(tuple.get("processCode").toString().trim().equalsIgnoreCase("EMP_OFFER_ACCEPTED") || tuple.get("processCode").toString().trim().equalsIgnoreCase("EMP_OFFER_DECLINED") || tuple.get("processCode").toString().trim().equalsIgnoreCase("EMP_OFFER_LETTER_REGENERATE") || tuple.get("processCode").toString().trim().equalsIgnoreCase("EMP_OFFER_LETTER_GENERATED") || tuple.get("processCode").toString().trim().equalsIgnoreCase("EMP_STAGE3_SELECTED")) {
						if(!Utils.isNullOrEmpty(tuple.get("countt")))
							if(!Utils.isNullOrEmpty(tuple.get("countt").toString()))
								empApplnEntriesDTO.setCountOfferStatus(empApplnEntriesDTO.getCountOfferStatus() + Integer.parseInt(tuple.get("countt").toString()));
					}
				}
			});
		}
		if(!Utils.isNullOrEmpty(count)) {
			int value = count.intValue();
			empApplnEntriesDTO.setCountApplicationReceived(empApplnEntriesDTO.getCountApplicationReceived() + value);
		}else {
			empApplnEntriesDTO.setCountApplicationReceived(0);
		}
		return Mono.just(empApplnEntriesDTO);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdateInterviewStatusStageOne(Mono<EmpApplnEntriesDTO> dto, String userId,boolean value) {
		List<EmpApplnEntriesDTO> empApplnEntriesDTO1 = new ArrayList<EmpApplnEntriesDTO>();
		List<ErpWorkFlowProcessDBO> erpWorkFlowProcessDBOList = interviewProcessTransaction1.getWorkFlowProcess3();
		Map<String, ErpWorkFlowProcessDBO> workFlowMap = !Utils.isNullOrEmpty(erpWorkFlowProcessDBOList)? erpWorkFlowProcessDBOList.stream().collect(Collectors.toMap(s -> s.getProcessCode(), s -> s)): new HashMap<String, ErpWorkFlowProcessDBO>();
		List<ErpEmailsDBO> emailsListApplicant = new ArrayList<ErpEmailsDBO>();
		List<ErpSmsDBO> smsListApplicant = new ArrayList<ErpSmsDBO>();
		List<ErpNotificationsDBO> notificationListApplicant = new ArrayList<ErpNotificationsDBO>();
		List<ErpEmailsDBO> emailsListEmployee = new ArrayList<ErpEmailsDBO>();
		Tuple workFlowProcess = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("EMP_STAGE1_REJECTED");
		Tuple workFlowProcessEmp = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("EMP_STAGE1_SELECTED");
		int workFlowEmployee = Integer.parseInt(workFlowProcessEmp.get("erp_work_flow_process_id").toString());
		int	workflowProcessId = Integer.parseInt(workFlowProcess.get("erp_work_flow_process_id").toString());
		return dto
				.handle((empApplnEntriesDTO, synchronousSink) -> {
					boolean istrue = false;
					if(!Utils.isNullOrEmpty(empApplnEntriesDTO.getScoreEntry())){
						if((empApplnEntriesDTO.getScoreEntry().trim().equalsIgnoreCase("Pending") || empApplnEntriesDTO.getScoreEntry().trim().equalsIgnoreCase("In Progress")) && empApplnEntriesDTO.getSelectionStatus().equalsIgnoreCase("Forwarded to PO"))
							istrue = true;
					}
					if (istrue && !value) {
						synchronousSink.error(new DuplicateException("Interview score entry is not completed by all panelists. Do you want to forward the candidate to the next stage?"));
					} else {
						synchronousSink.next(empApplnEntriesDTO);
					}
				}).cast(EmpApplnEntriesDTO.class)
				.map(data -> convertDtoToDbo(data,empApplnEntriesDTO1, userId,workFlowMap,emailsListApplicant,smsListApplicant,notificationListApplicant,emailsListEmployee,workFlowEmployee,workflowProcessId))
				.flatMap(s -> {
					if (!Utils.isNullOrEmpty(s)) {
						boolean isSaved = interviewProcessTransaction1.saveOrUpdate(s);
						if(isSaved) {
							Set<Integer> approversIdSet = new HashSet<Integer>();
							approversIdSet.add(Integer.parseInt(userId));
							EmpApplnEntriesDTO empApplnEntriesDTO = empApplnEntriesDTO1.get(0);
							if(empApplnEntriesDTO.getSelectionStatus().equalsIgnoreCase("Rejected"))
								commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(workflowProcessId,"EMP_STAGE1_REJECTED_TO_CANDIDATE",approversIdSet,notificationListApplicant,smsListApplicant,emailsListApplicant);
							if(empApplnEntriesDTO.getSelectionStatus().equalsIgnoreCase("Forwarded to PO"))
								commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(workFlowEmployee,"EMP_STAGE1_SELECTED_TO_PO ",null,null,null,emailsListEmployee);
						}
					} 
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	private List<Object> convertDtoToDbo(EmpApplnEntriesDTO dto, List<EmpApplnEntriesDTO> empApplnEntriesDTO1, String userId, Map<String, ErpWorkFlowProcessDBO> workFlowMap, List<ErpEmailsDBO> emailsListApplicant, List<ErpSmsDBO> smsListApplicant, List<ErpNotificationsDBO> notificationListApplicant, List<ErpEmailsDBO> emailsListEmployee, int workFlowEmployee, int workflowProcessId) {
		List<Object> objList = new ArrayList<Object>();
		empApplnEntriesDTO1.add(dto);
		String notificationCodeApplicant = "EMP_STAGE1_REJECTED_TO_CANDIDATE";
		String notificationCodeEmployee = "EMP_STAGE1_SELECTED_TO_PO";
		ErpWorkFlowProcessNotificationsDBO erpWorkFlowProcessNotificationsDBORejectedApplicant = interviewProcessTransaction1.getErpWorkFlowProcessNotification(workflowProcessId,notificationCodeApplicant);
		ErpWorkFlowProcessNotificationsDBO erpWorkFlowProcessNotificationsDBOSelectPo = interviewProcessTransaction1.getErpWorkFlowProcessNotification(workFlowEmployee,notificationCodeEmployee);
		EmpApplnEntriesDBO empApplnEntriesDBO = !Utils.isNullOrEmpty(dto.getApplicantId()) ? interviewProcessTransaction1.getempApplnInterviewSchedules(Integer.parseInt(dto.getApplicantId())) : null;
		empApplnEntriesDBO.setModifiedUsersId(Integer.parseInt(userId));
		if(!Utils.isNullOrEmpty(dto.getSelectionStatus())) {
			ErpWorkFlowProcessDBO erpWorkFlowProcessDBO = null;
			if(dto.getSelectionStatus().equalsIgnoreCase("Rejected")) {
				erpWorkFlowProcessDBO = workFlowMap.get("EMP_STAGE1_REJECTED");
				empApplnEntriesDBO.setApplicationCurrentProcessStatus(erpWorkFlowProcessDBO);
				empApplnEntriesDBO.setApplicationStatusTime(LocalDateTime.now());
				empApplnEntriesDBO.setApplicantCurrentProcessStatus(erpWorkFlowProcessDBO);
				empApplnEntriesDBO.setApplicationStatusTime(LocalDateTime.now());
				if(!Utils.isNullOrEmpty(erpWorkFlowProcessNotificationsDBORejectedApplicant)) {
					if(erpWorkFlowProcessNotificationsDBORejectedApplicant.getIsEmailActivated()) {
						ErpTemplateDBO erpTemplateDBO = erpWorkFlowProcessNotificationsDBORejectedApplicant.getErpEmailsTemplateDBO();
						if(!Utils.isNullOrEmpty(erpTemplateDBO))
							emailsListApplicant.add(getEmailDBO(erpTemplateDBO,empApplnEntriesDBO.getId(),userId,dto,null));
					}
					if(erpWorkFlowProcessNotificationsDBORejectedApplicant.getIsSmsActivated()) {
						ErpTemplateDBO erpTemplateDBO = erpWorkFlowProcessNotificationsDBORejectedApplicant.getErpSmsTemplateDBO();
						if(!Utils.isNullOrEmpty(erpTemplateDBO))
							smsListApplicant.add(getSMSDBO(erpTemplateDBO,empApplnEntriesDBO.getId(),userId,dto,null));
					}
					if(erpWorkFlowProcessNotificationsDBORejectedApplicant.getIsNotificationActivated()) {
						notificationListApplicant.add(getNotificationsDBO(empApplnEntriesDBO.getId(),Integer.parseInt(userId),null,erpWorkFlowProcessNotificationsDBORejectedApplicant));
					}
				}
			}
			if(dto.getSelectionStatus().equalsIgnoreCase("Forwarded to PO")) {
				erpWorkFlowProcessDBO = workFlowMap.get("EMP_STAGE1_SELECTED");
				empApplnEntriesDBO.setApplicationCurrentProcessStatus(erpWorkFlowProcessDBO);
				empApplnEntriesDBO.setApplicationStatusTime(LocalDateTime.now());
				empApplnEntriesDBO.setApplicantCurrentProcessStatus(erpWorkFlowProcessDBO);
				empApplnEntriesDBO.setApplicationStatusTime(LocalDateTime.now());
				emailsListEmployee.add(getEmailDBOForPO(erpWorkFlowProcessNotificationsDBOSelectPo,userId,empApplnEntriesDBO.getShortistedLocationId(),dto));
			}
			if(dto.getSelectionStatus().equalsIgnoreCase("On Hold")) {
				erpWorkFlowProcessDBO = workFlowMap.get("EMP_STAGE1_ONHOLD");
				empApplnEntriesDBO.setApplicationCurrentProcessStatus(erpWorkFlowProcessDBO);
				empApplnEntriesDBO.setApplicationStatusTime(LocalDateTime.now());
			}
			if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO())) {
				if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getComments())) {
					if(!Utils.isNullOrEmpty(empApplnEntriesDBO.getEmpApplnInterviewSchedulesDBOs())) {
						empApplnEntriesDBO.getEmpApplnInterviewSchedulesDBOs().forEach(dbo1 -> {
							if(dbo1.getRecordStatus() == 'A') {
								dbo1.setModifiedUsersId(Integer.parseInt(userId));
								dbo1.setComments(dto.getEmpApplnInterviewSchedulesDTO().getComments());
							}
						});
					}
				}
			}
			ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
			erpWorkFlowProcessStatusLogDBO.setEntryId(empApplnEntriesDBO.getId());
			erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
			erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
			erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(erpWorkFlowProcessDBO);
			objList.add(empApplnEntriesDBO);
			objList.add(erpWorkFlowProcessStatusLogDBO);
		}		
		return objList;
	}

	public Mono<List<EmpApplnEntriesDTO>> getStageTwoInterviewStatus(String departmentId, String locationId, String filterStatus) {
		List<Tuple> data = interviewProcessTransaction1.getStageTwoInterviewStatus(departmentId,locationId, filterStatus);
		List<Integer> empIds = data.stream().filter(s -> !Utils.isNullOrEmpty(s.get("id"))).map(s ->Integer.parseInt(String.valueOf(s.get("id")))).collect(Collectors.toList());
		Map<Integer, EmpApplnInterviewSchedulesDBO> scoreMap = interviewProcessTransaction1.getAvergeScore(empIds).stream().collect(Collectors.toMap(s -> s.getEmpApplnEntriesDBO().getId(), s -> s));
		return this.convertStageTwoInterviewStatusDBOToDTO(data,scoreMap);
	}

	public Mono<List<EmpApplnEntriesDTO>> convertStageTwoInterviewStatusDBOToDTO(List<Tuple> dbos, Map<Integer, EmpApplnInterviewSchedulesDBO> scoreMap) {
		List<EmpApplnEntriesDTO> dtos = new ArrayList<EmpApplnEntriesDTO>();
		List<Integer> pointOfIds = !Utils.isNullOrEmpty(dbos) ? dbos.stream().filter(s -> !Utils.isNullOrEmpty(s.get("pointOfContactId"))).map(s -> Integer.parseInt(s.get("pointOfContactId").toString())).collect(Collectors.toList()) : new ArrayList<Integer>();
		List<Tuple> pointList = !Utils.isNullOrEmpty(pointOfIds) ? interviewProcessTransaction1.getPointOfContact(pointOfIds) : new ArrayList<Tuple>();
		Map<Integer,String> pointMap = !Utils.isNullOrEmpty(pointList) ? pointList.stream().collect(Collectors.toMap(s -> Integer.parseInt(s.get("erpUserId").toString()), p->(p.get("name").toString()+ p.get("department").toString()))) : new HashMap<Integer,String>();
		if(!Utils.isNullOrEmpty(dbos)) {
			dbos.forEach(dbo -> {
				EmpApplnEntriesDTO dto = new  EmpApplnEntriesDTO();
				dto.setApplicantId(!Utils.isNullOrEmpty(dbo.get("id")) ? String.valueOf(dbo.get("id")) : null);
				dto.setApplicantNumber(!Utils.isNullOrEmpty(dbo.get("applicationNo")) ? String.valueOf(dbo.get("applicationNo")) : null);
				dto.setApplicantName(!Utils.isNullOrEmpty(dbo.get("name")) ? String.valueOf(dbo.get("name")) : null);
				dto.setStatus(String.valueOf(dbo.get("status")));
				if(!Utils.isNullOrEmpty(dbo.get("stage2_onhold_rejected_comments"))) {
					dto.setStage2OnholdRejectedComments(String.valueOf(dbo.get("stage2_onhold_rejected_comments")));
				}
				if(!Utils.isNullOrEmpty(dbo.get("departmentId"))) {
					dto.setShortlistedDepartment(new SelectDTO());
					dto.getShortlistedDepartment().setValue(String.valueOf(dbo.get("departmentId")));
					dto.getShortlistedDepartment().setLabel(String.valueOf(dbo.get("departmentName")));
				}
				if(!Utils.isNullOrEmpty(dbo.get("file_name_unique")) && !Utils.isNullOrEmpty(dbo.get("upload_process_code")) && !Utils.isNullOrEmpty(dbo.get("file_name_original"))) {
					FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
					fileUploadDownloadDTO.setActualPath(dbo.get("file_name_unique").toString());
					fileUploadDownloadDTO.setProcessCode(dbo.get("upload_process_code").toString());
					fileUploadDownloadDTO.setOriginalFileName(dbo.get("file_name_original").toString());
					aWSS3FileStorageService.generatePresignedUrlForDownload(fileUploadDownloadDTO);
					dto.setProfilePhotoUrl(fileUploadDownloadDTO.getPreSignedUrl());
				}
//				dto.setProfilePhotoUrl(!Utils.isNullOrEmpty(dbo.get("photoUrl"))?String.valueOf(dbo.get("photoUrl")):null);
				LocalDateTime dateTime = !Utils.isNullOrEmpty(dbo.get("interviewDateTime"))?
						Utils.convertStringDateTimeToLocalDateTime(String.valueOf(dbo.get("interviewDateTime"))):null;
				dto.setEmpApplnInterviewSchedulesDTO(new EmpApplnInterviewSchedulesDTO());
				dto.getEmpApplnInterviewSchedulesDTO().setInterviewDateTime1(dateTime);
				if(!Utils.isNullOrEmpty(dbo.get("venue")))
					dto.getEmpApplnInterviewSchedulesDTO().setInterviewVenue(String.valueOf(dbo.get("venue")));
//				if(!Utils.isNullOrEmpty(dbo.get("pointOfContactId")))
//					dto.getEmpApplnInterviewSchedulesDTO().setPointOfContactUsersId(String.valueOf(dbo.get("pointOfContactId")));
				if(!Utils.isNullOrEmpty(pointMap) && !Utils.isNullOrEmpty(dbo.get("pointOfContactId"))) {
					if(pointMap.containsKey(Integer.parseInt(String.valueOf(dbo.get("pointOfContactId"))))) {
						dto.getEmpApplnInterviewSchedulesDTO().setPointofContact(new SelectDTO());
						dto.getEmpApplnInterviewSchedulesDTO().getPointofContact().setValue(String.valueOf(dbo.get("pointOfContactId")));
						dto.getEmpApplnInterviewSchedulesDTO().getPointofContact().setLabel(pointMap.get(Integer.parseInt(String.valueOf(dbo.get("pointOfContactId")))));
					}
				}
				if(scoreMap.containsKey(Integer.parseInt(dto.getApplicantId()))) {
					EmpApplnInterviewSchedulesDBO schedules = scoreMap.get(Integer.parseInt(dto.getApplicantId()));
					AtomicInteger panelistCount = new AtomicInteger(0);
					AtomicInteger totalScore = new AtomicInteger(0);
					if(!Utils.isNullOrEmpty(schedules)) {
						schedules.getEmpApplnInterviewScoreDBO().forEach( score -> {
							if(!Utils.isNullOrEmpty(score.getErpUsersDBO()) || !Utils.isNullOrEmpty(score.getEmpInterviewUniversityExternalsDBO())) {
								panelistCount.getAndAdd(1);
								totalScore.getAndAdd(!Utils.isNullOrEmpty(score.getTotalScore()) ? score.getTotalScore():0);
							}
						});
						Float avergeScore = null;
						if(!Utils.isNullOrEmpty(totalScore.get()) && !Utils.isNullOrEmpty(panelistCount.get())) {
							avergeScore = (float) (totalScore.get()/panelistCount.get());
							dto.setTotalScore(avergeScore);
						}
					}
				}
				dtos.add(dto);
			});
		}
		return  !dtos.isEmpty()? Mono.just(dtos):Mono.error(new NotFoundException("Data Not Found"));
	}

	public Mono<List<EmpApplnEntriesDTO>> getStageThreeApplicants(String departmentId, String locationId, String filterStatus) {
		List<Tuple> data = interviewProcessTransaction1.getStageThreeApplicants(departmentId,locationId, filterStatus);
		List<Integer> empIds = data.stream().filter(s -> !Utils.isNullOrEmpty(s.get("id"))).map(s ->Integer.parseInt(String.valueOf(s.get("id")))).collect(Collectors.toList());
		Map<Integer, EmpApplnInterviewSchedulesDBO> scoreMap = interviewProcessTransaction1.getAvergeScore(empIds).stream().collect(Collectors.toMap(s -> s.getEmpApplnEntriesDBO().getId(), s -> s));
		return this.convertStageThreeApplicantsDBOToDTO(data,scoreMap);
	}

	public Mono<List<EmpApplnEntriesDTO>> convertStageThreeApplicantsDBOToDTO(List<Tuple> dbos, Map<Integer, EmpApplnInterviewSchedulesDBO> scoreMap) {
		List<EmpApplnEntriesDTO> dtos = new ArrayList<EmpApplnEntriesDTO>();
		List<Integer> pointIds = !Utils.isNullOrEmpty(dbos) ? dbos.stream().filter(s -> !Utils.isNullOrEmpty(s.get("pointOfContactId"))).map(s -> (Integer) s.get("pointOfContactId")).collect(Collectors.toList()) : new ArrayList<Integer>();		
		List<Tuple> pointList = !Utils.isNullOrEmpty(pointIds) ? interviewProcessTransaction1.getPointOfContact(pointIds) : null;
		Map<Integer, String> pointMap = !Utils.isNullOrEmpty(pointList) ? pointList.stream().filter(s -> (!Utils.isNullOrEmpty(s.get("erpUserId")) && !Utils.isNullOrEmpty(s.get("name")) && !Utils.isNullOrEmpty(s.get("department")))).collect(Collectors.toMap(s -> Integer.parseInt( s.get("erpUserId").toString()), s -> String.valueOf(s.get("name"))+ String.valueOf(s.get("department")))): new HashMap<Integer, String>();	
		if(!Utils.isNullOrEmpty(dbos)) {
			dbos.forEach(dbo -> {
				EmpApplnEntriesDTO dto = new  EmpApplnEntriesDTO();
				dto.setApplicantId(String.valueOf(dbo.get("id")));
				dto.setApplicantNumber(String.valueOf(dbo.get("applicationNo")));
				dto.setApplicantName(String.valueOf(dbo.get("name")));
				if(!Utils.isNullOrEmpty(dbo.get("departmentId"))) {
					dto.setShortlistedDepartment(new SelectDTO());
					dto.getShortlistedDepartment().setValue(String.valueOf(dbo.get("departmentId")));
					dto.getShortlistedDepartment().setLabel(String.valueOf(dbo.get("departmentName")));
				}
//				dto.setProfilePhotoUrl(!Utils.isNullOrEmpty(dbo.get("photoUrl"))?String.valueOf(dbo.get("photoUrl")):null);
				if(!Utils.isNullOrEmpty(dbo.get("file_name_unique")) && !Utils.isNullOrEmpty(dbo.get("upload_process_code")) && !Utils.isNullOrEmpty(dbo.get("file_name_original"))) {
					FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
					fileUploadDownloadDTO.setActualPath(dbo.get("file_name_unique").toString());
					fileUploadDownloadDTO.setProcessCode(dbo.get("upload_process_code").toString());
					fileUploadDownloadDTO.setOriginalFileName(dbo.get("file_name_original").toString());
					aWSS3FileStorageService.generatePresignedUrlForDownload(fileUploadDownloadDTO);
					dto.setProfilePhotoUrl(fileUploadDownloadDTO.getPreSignedUrl());
					
				}
				dto.setStatus(String.valueOf(dbo.get("status")));
				dto.setApplicantEmail(!Utils.isNullOrEmpty(dbo.get("mail"))?String.valueOf(dbo.get("mail")):null);
				dto.setApplicantMobile(!Utils.isNullOrEmpty(dbo.get("mobileNumber"))?String.valueOf(dbo.get("mobileCode"))+String.valueOf("mobileNumber"):null);
				dto.setApplicationStatus(!Utils.isNullOrEmpty(dbo.get("processCode"))?String.valueOf(dbo.get("processCode")):null);
				dto.setEmpApplnInterviewSchedulesDTO(new EmpApplnInterviewSchedulesDTO());
				dto.getEmpApplnInterviewSchedulesDTO().setId(String.valueOf(dbo.get("scheduleId")));
				LocalDateTime dateTime = !Utils.isNullOrEmpty(dbo.get("interviewDateTime"))?
						Utils.convertStringDateTimeToLocalDateTime(String.valueOf(dbo.get("interviewDateTime"))):null;
				dto.getEmpApplnInterviewSchedulesDTO().setInterviewDateTime1(dateTime);
//				if(!Utils.isNullOrEmpty(dbo.get("pointOfContactId")))
//					dto.getEmpApplnInterviewSchedulesDTO().setPointOfContactUsersId(String.valueOf(dbo.get("pointOfContactId")));
				if(!Utils.isNullOrEmpty(pointMap) && !Utils.isNullOrEmpty(dbo.get("pointOfContactId"))) {
					if(pointMap.containsKey(Integer.parseInt(String.valueOf(dbo.get("pointOfContactId"))))) {
						dto.getEmpApplnInterviewSchedulesDTO().setPointofContact(new SelectDTO());
						dto.getEmpApplnInterviewSchedulesDTO().getPointofContact().setValue(String.valueOf(dbo.get("pointOfContactId")));
						dto.getEmpApplnInterviewSchedulesDTO().getPointofContact().setLabel(pointMap.get(Integer.parseInt(String.valueOf(dbo.get("pointOfContactId")))));
					}
				}
				if(!Utils.isNullOrEmpty(dbo.get("venue")))
					dto.getEmpApplnInterviewSchedulesDTO().setInterviewVenue(String.valueOf(dbo.get("venue")));
				if(scoreMap.containsKey(Integer.parseInt(dto.getApplicantId()))) {
					EmpApplnInterviewSchedulesDBO schedules = scoreMap.get(Integer.parseInt(dto.getApplicantId()));
					AtomicInteger panelistCount = new AtomicInteger(0);
					AtomicInteger totalScore = new AtomicInteger(0);
					if(!Utils.isNullOrEmpty(schedules)) {
						schedules.getEmpApplnInterviewScoreDBO().forEach( score -> {
							if(!Utils.isNullOrEmpty(score.getErpUsersDBO()) || !Utils.isNullOrEmpty(score.getEmpInterviewUniversityExternalsDBO())) {
								panelistCount.getAndAdd(1);
								totalScore.getAndAdd(!Utils.isNullOrEmpty(score.getTotalScore()) ? score.getTotalScore():0);
							}
						});
						Float avergeScore = null;
						if(!Utils.isNullOrEmpty(totalScore.get()) && !Utils.isNullOrEmpty(panelistCount.get())) {
							avergeScore = (float) (totalScore.get()/panelistCount.get());
							dto.setTotalScore(avergeScore);
						}
					}
				}
				dtos.add(dto);
			});
		}
		return  !dtos.isEmpty()? Mono.just(dtos):Mono.error(new NotFoundException("Data Not Found"));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Mono<ApiResult> submitStageThreeSchedule(Mono<List<EmpApplnEntriesDTO>> dto,String userId) {

		List<Integer> entriesIds = new ArrayList<Integer>();
		Map<Integer,EmpApplnEntriesDBO> entriesDBOMap = new HashMap<Integer, EmpApplnEntriesDBO>();
		Map<Integer,EmpApplnInterviewSchedulesDBO> scheduleDetailsMap = new HashMap<Integer, EmpApplnInterviewSchedulesDBO>();
		List<ErpEmailsDBO> scheduleEmailsList = new ArrayList<ErpEmailsDBO>();
		List<ErpSmsDBO> scheduleSmsList = new ArrayList<ErpSmsDBO>();
		List<ErpNotificationsDBO> scheduleNotificationList = new ArrayList<ErpNotificationsDBO>();
		List<ErpEmailsDBO> reScheduleEmailsList = new ArrayList<ErpEmailsDBO>();
		List<ErpSmsDBO> reScheduleSmsList = new ArrayList<ErpSmsDBO>();
		List<ErpNotificationsDBO> reScheduleNotificationList = new ArrayList<ErpNotificationsDBO>();
		Tuple poScheduled = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("EMP_STAGE3_PO_SCHEDULED");
		Tuple poReScheduled = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("EMP_STAGE3_PO_RESCHEDULED");
		return dto.handle((empApplnEntriesDTO, synchronousSink) -> {
			empApplnEntriesDTO.forEach( value -> {
				entriesIds.add(Integer.parseInt(value.getApplicantId()));
			});
			Map<Integer,EmpApplnEntriesDBO> entriesDBOs = interviewProcessTransaction1.getEmpApplnEntriesDBO(entriesIds).stream().collect(Collectors.toMap(s -> s.getId(), s -> s));
			entriesDBOMap.putAll(entriesDBOs);
			Map<Integer,EmpApplnInterviewSchedulesDBO> detailsMap = interviewProcessTransaction1.getInterviewSchedules(entriesIds).stream().collect(Collectors.toMap(s -> s.getEmpApplnEntriesDBO().getId(), s -> s));
			scheduleDetailsMap.putAll(detailsMap);
			synchronousSink.next(empApplnEntriesDTO);
		}).cast(ArrayList.class).map(data -> convertDtoToDbo(data,entriesDBOMap,scheduleDetailsMap,poScheduled,poReScheduled,scheduleNotificationList,scheduleSmsList,scheduleEmailsList,
				reScheduleEmailsList,reScheduleSmsList,reScheduleNotificationList,userId))
				.flatMap( s -> {
					if (!Utils.isNullOrEmpty(s)) {
						Boolean updated = interviewProcessTransaction1.update(s);
						if(updated) {
							Set<Integer>  approversIdSet = new HashSet<Integer>();
							approversIdSet.add(Integer.parseInt(userId));
							commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(Integer.parseInt(poScheduled.get("erp_work_flow_process_id").toString()),"STAGE3_PO_SCHEDULED_APPROVAL_INFORM_APPLICANT",approversIdSet,scheduleNotificationList,scheduleSmsList,scheduleEmailsList);
							commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(Integer.parseInt(poReScheduled.get("erp_work_flow_process_id").toString()),"STAGE3_PO_RESCHEDULED_APPROVAL_INFORM_APPLICANT",approversIdSet,reScheduleNotificationList,reScheduleSmsList,reScheduleEmailsList);
						}
					}
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	public List<Object> convertDtoToDbo(List<EmpApplnEntriesDTO> dtos, Map<Integer, EmpApplnEntriesDBO> entriesDBOMap, Map<Integer, EmpApplnInterviewSchedulesDBO> scheduleDetailsMap, Tuple poScheduled, Tuple poReScheduled,
			List<ErpNotificationsDBO> scheduleNotificationList, List<ErpSmsDBO> scheduleSmsList, List<ErpEmailsDBO> scheduleEmailsList,
			List<ErpEmailsDBO> reScheduleEmailsList, List<ErpSmsDBO> reScheduleSmsList, List<ErpNotificationsDBO> reScheduleNotificationList, String userId) {
		List<Object> data = new ArrayList<Object>();
		List<ErpWorkFlowProcessStatusLogDBO> statusLogList = new ArrayList<ErpWorkFlowProcessStatusLogDBO>();

		List<Integer> processCodeList = new ArrayList<Integer>();
		processCodeList.add(Integer.parseInt(poScheduled.get("erp_work_flow_process_id").toString()));
		processCodeList.add(Integer.parseInt(poReScheduled.get("erp_work_flow_process_id").toString()));
		Map<String, ErpWorkFlowProcessNotificationsDBO> notificationMap = interviewProcessTransaction1.getErpNotifications(processCodeList).stream().collect(Collectors.toMap(s -> s.getErpWorkFlowProcessDBO().getProcessCode(), s -> s));

		dtos.forEach(dto -> {

			//updating the status in EmpApplnEntriesDBO
			EmpApplnEntriesDBO dbo = entriesDBOMap.get(Integer.parseInt(dto.getApplicantId()));
			ErpWorkFlowProcessNotificationsDBO notification = null;

			if(dto.getApplicationStatus().equalsIgnoreCase("EMP_STAGE2_SELECTED")) {
				dbo.setApplicantCurrentProcessStatus(new ErpWorkFlowProcessDBO());
				dbo.getApplicantCurrentProcessStatus().setId(Integer.parseInt(poScheduled.get("erp_work_flow_process_id").toString()));
				dbo.setApplicantStatusTime(LocalDateTime.now());
				dbo.setApplicationCurrentProcessStatus(new ErpWorkFlowProcessDBO());
				dbo.getApplicationCurrentProcessStatus().setId(Integer.parseInt(poScheduled.get("erp_work_flow_process_id").toString()));
				dbo.setApplicationStatusTime(LocalDateTime.now());
				notification = notificationMap.get("EMP_STAGE3_PO_SCHEDULED");
			}
			if(dto.getApplicationStatus().equalsIgnoreCase("EMP_STAGE3_PO_SCHEDULED") || dto.getApplicationStatus().equalsIgnoreCase("EMP_STAGE3_PO_RESHEDULED") 
					|| dto.getApplicationStatus().equalsIgnoreCase("EMP_STAGE3_SCHEDULE_APPLICANT_DECLINED") || dto.getApplicationStatus().equalsIgnoreCase("EMP_STAGE3_SCHEDULE_APPLICANT_ACCEPTED")) {
				dbo.setApplicantCurrentProcessStatus(new ErpWorkFlowProcessDBO());
				dbo.getApplicantCurrentProcessStatus().setId(Integer.parseInt(poReScheduled.get("erp_work_flow_process_id").toString()));
				dbo.setApplicantStatusTime(LocalDateTime.now());
				dbo.setApplicationCurrentProcessStatus(new ErpWorkFlowProcessDBO());
				dbo.getApplicationCurrentProcessStatus().setId(Integer.parseInt(poReScheduled.get("erp_work_flow_process_id").toString()));
				dbo.setApplicationStatusTime(LocalDateTime.now());
				notification = notificationMap.get("EMP_STAGE3_PO_RESCHEDULED");
			}

			//scheduleing data
			Set<EmpApplnInterviewSchedulesDBO> empApplnInterviewSchedulesDBOSet = new HashSet<>(); 
			EmpApplnInterviewSchedulesDBO empApplnInterviewSchedulesDBO = new EmpApplnInterviewSchedulesDBO();
			empApplnInterviewSchedulesDBO.setEmpApplnEntriesDBO(dbo);
			empApplnInterviewSchedulesDBO.setInterviewDateTime(dto.getEmpApplnInterviewSchedulesDTO().getInterviewDateTime1());
			empApplnInterviewSchedulesDBO.setErpUsersDBO(new ErpUsersDBO());
//			empApplnInterviewSchedulesDBO.getErpUsersDBO().setId(Integer.parseInt(dto.getEmpApplnInterviewSchedulesDTO().getPointOfContactUsersId()));
			if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getPointofContact())) {
				if(!Utils.isNullOrEmpty(dto.getEmpApplnInterviewSchedulesDTO().getPointofContact().getValue())){
					empApplnInterviewSchedulesDBO.setErpUsersDBO(new ErpUsersDBO());
					empApplnInterviewSchedulesDBO.getErpUsersDBO().setId(Integer.parseInt(dto.getEmpApplnInterviewSchedulesDTO().getPointofContact().getValue()));
				}
			}
			empApplnInterviewSchedulesDBO.setInterviewVenue(dto.getEmpApplnInterviewSchedulesDTO().getInterviewVenue());
			empApplnInterviewSchedulesDBO.setInterviewRound(3);
			empApplnInterviewSchedulesDBO.setCreatedUsersId(Integer.parseInt(userId));
			empApplnInterviewSchedulesDBO.setRecordStatus('A');
			empApplnInterviewSchedulesDBOSet.add(empApplnInterviewSchedulesDBO);
			dbo.setEmpApplnInterviewSchedulesDBOs(empApplnInterviewSchedulesDBOSet);
			if(scheduleDetailsMap.containsKey(Integer.parseInt(dto.getApplicantId()))) {
				EmpApplnInterviewSchedulesDBO oldScheduleDetails = scheduleDetailsMap.get(Integer.parseInt(dto.getApplicantId()));
				oldScheduleDetails.setRecordStatus('D');
				empApplnInterviewSchedulesDBOSet.add(oldScheduleDetails);
				scheduleDetailsMap.remove(Integer.parseInt(dto.getApplicantId()));
			}
			data.add(dbo);

			//sending notification 
			if(!Utils.isNullOrEmpty(notification)) {

				if(notification.getIsEmailActivated()) {
					ErpTemplateDBO erpTemplateDBO = notification.getErpEmailsTemplateDBO();
					if(dto.getApplicationStatus().equalsIgnoreCase("EMP_STAGE2_SELECTED")) {
						if(!Utils.isNullOrEmpty(erpTemplateDBO))
							scheduleEmailsList.add(getEmailDBO(erpTemplateDBO,dbo.getId(),userId,dto,null));
					} else {
						if(!Utils.isNullOrEmpty(erpTemplateDBO))
							reScheduleEmailsList.add(getEmailDBO(erpTemplateDBO,dbo.getId(),userId,dto,null));
					}
				}

				if(notification.getIsSmsActivated()) {
					ErpTemplateDBO erpTemplateDBO = notification.getErpSmsTemplateDBO();
					if(dto.getApplicationStatus().equalsIgnoreCase("EMP_STAGE2_SELECTED")) {
						if(!Utils.isNullOrEmpty(erpTemplateDBO))
							scheduleSmsList.add(getSMSDBO(erpTemplateDBO,dbo.getId(),userId,dto,null));
					} else {
						if(!Utils.isNullOrEmpty(erpTemplateDBO))
							reScheduleSmsList.add(getSMSDBO(erpTemplateDBO,dbo.getId(),userId,dto,null));
					}
				}
				if(notification.getIsNotificationActivated()) {
					if(dto.getApplicationStatus().equalsIgnoreCase("EMP_STAGE2_SELECTED")) {
						scheduleNotificationList.add(getNotificationsDBO(dbo.getId(), Integer.parseInt(userId),null,notification));
					} else {
						reScheduleNotificationList.add(getNotificationsDBO(dbo.getId(), Integer.parseInt(userId),null,notification));
					}
				}
			}

			//status log
			ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
			erpWorkFlowProcessStatusLogDBO.setEntryId(dbo.getId());
			if(dto.getApplicationStatus().equalsIgnoreCase("EMP_STAGE2_SELECTED")) {
				erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(new ErpWorkFlowProcessDBO());
				erpWorkFlowProcessStatusLogDBO.getErpWorkFlowProcessDBO().setId(Integer.parseInt(poScheduled.get("erp_work_flow_process_id").toString()));
			} else {
				erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(new ErpWorkFlowProcessDBO());
				erpWorkFlowProcessStatusLogDBO.getErpWorkFlowProcessDBO().setId(Integer.parseInt(poReScheduled.get("erp_work_flow_process_id").toString()));
			}
			erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
			erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
			statusLogList.add(erpWorkFlowProcessStatusLogDBO);
			data.addAll(statusLogList);

		});
		//	commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(Integer.parseInt(selected.get("erp_work_flow_process_id").toString()),"STAGE3_PO_APPROVAL_INFORM_APPLICANT",approversIdSet,notificationList,smsList,emailsList);

		return data;
	}

	public Mono<List<EmpApplnEntriesDTO>> getStageThreeInterviewStatus(String departmentId, String locationId, String filterStatus) {
		List<Tuple> data = interviewProcessTransaction1.getStageThreeInterviewStatus(departmentId,locationId, filterStatus);
		List<Integer> empIds = data.stream().filter(s -> !Utils.isNullOrEmpty(s.get("id"))).map(s ->Integer.parseInt(String.valueOf(s.get("id")))).collect(Collectors.toList());
		Map<Integer, EmpApplnInterviewSchedulesDBO> scoreMap = interviewProcessTransaction1.getAvergeScore(empIds).stream().collect(Collectors.toMap(s -> s.getEmpApplnEntriesDBO().getId(), s -> s));
		return this.convertStageThreeInterviewStatusDBOToDTO(data,scoreMap);
	}

	public Mono<List<EmpApplnEntriesDTO>> convertStageThreeInterviewStatusDBOToDTO(List<Tuple> dbos, Map<Integer, EmpApplnInterviewSchedulesDBO> scoreMap) {
		List<EmpApplnEntriesDTO> dtos = new ArrayList<EmpApplnEntriesDTO>();
		List<Integer> pointIds = !Utils.isNullOrEmpty(dbos) ? dbos.stream().filter(s -> !Utils.isNullOrEmpty(s.get("pointOfContactId"))).map(s -> (Integer) s.get("pointOfContactId")).collect(Collectors.toList()) : new ArrayList<Integer>();		
		List<Tuple> pointList = !Utils.isNullOrEmpty(pointIds) ? interviewProcessTransaction1.getPointOfContact(pointIds) : null;
		Map<Integer, String> pointMap = !Utils.isNullOrEmpty(pointList) ? pointList.stream().filter(s -> (!Utils.isNullOrEmpty(s.get("erpUserId")) && !Utils.isNullOrEmpty(s.get("name")) && !Utils.isNullOrEmpty(s.get("department")))).collect(Collectors.toMap(s -> Integer.parseInt( s.get("erpUserId").toString()), s -> String.valueOf(s.get("name"))+ String.valueOf(s.get("department")))): new HashMap<Integer, String>();	
		if(!Utils.isNullOrEmpty(dbos)) {
			dbos.forEach(dbo -> {
				EmpApplnEntriesDTO dto = new  EmpApplnEntriesDTO();
				dto.setApplicantId(String.valueOf(dbo.get("id")));
				dto.setApplicantNumber(String.valueOf(dbo.get("applicationNo")));
				dto.setApplicantName(String.valueOf(dbo.get("name")));
				dto.setStatus(String.valueOf(dbo.get("status")));
				if(!Utils.isNullOrEmpty(dbo.get("stage3_comments"))) {
					dto.setStage3Comments(String.valueOf(dbo.get("stage3_comments")));
				}
				if(!Utils.isNullOrEmpty(dbo.get("departmentId"))) {
					dto.setShortlistedDepartment(new SelectDTO());
					dto.getShortlistedDepartment().setValue(String.valueOf(dbo.get("departmentId")));
					dto.getShortlistedDepartment().setLabel(String.valueOf(dbo.get("departmentName")));
				}
//				dto.setProfilePhotoUrl(!Utils.isNullOrEmpty(dbo.get("photoUrl"))?String.valueOf(dbo.get("photoUrl")):null);
				if(!Utils.isNullOrEmpty(dbo.get("file_name_unique")) && !Utils.isNullOrEmpty(dbo.get("upload_process_code")) && !Utils.isNullOrEmpty(dbo.get("file_name_original"))) {
					FileUploadDownloadDTO fileUploadDownloadDTO = new FileUploadDownloadDTO();
					fileUploadDownloadDTO.setActualPath(dbo.get("file_name_unique").toString());
					fileUploadDownloadDTO.setProcessCode(dbo.get("upload_process_code").toString());
					fileUploadDownloadDTO.setOriginalFileName(dbo.get("file_name_original").toString());
					aWSS3FileStorageService.generatePresignedUrlForDownload(fileUploadDownloadDTO);
					dto.setProfilePhotoUrl(fileUploadDownloadDTO.getPreSignedUrl());
					
				}
			     LocalDateTime dateTime = !Utils.isNullOrEmpty(dbo.get("interviewDateTime"))?
						Utils.convertStringDateTimeToLocalDateTime(String.valueOf(dbo.get("interviewDateTime"))):null;
				dto.setEmpApplnInterviewSchedulesDTO(new EmpApplnInterviewSchedulesDTO());
				dto.getEmpApplnInterviewSchedulesDTO().setInterviewDateTime1(dateTime);
				if(!Utils.isNullOrEmpty(dbo.get("venue")))
					dto.getEmpApplnInterviewSchedulesDTO().setInterviewVenue(String.valueOf(dbo.get("venue")));
//				if(!Utils.isNullOrEmpty(dbo.get("pointOfContactId")))
//					dto.getEmpApplnInterviewSchedulesDTO().setPointOfContactUsersId(String.valueOf(dbo.get("pointOfContactId")));
				if(!Utils.isNullOrEmpty(pointMap) && !Utils.isNullOrEmpty(dbo.get("pointOfContactId"))) {
					if(pointMap.containsKey(Integer.parseInt(String.valueOf(dbo.get("pointOfContactId"))))) {
						dto.getEmpApplnInterviewSchedulesDTO().setPointofContact(new SelectDTO());
						dto.getEmpApplnInterviewSchedulesDTO().getPointofContact().setValue(String.valueOf(dbo.get("pointOfContactId")));
						dto.getEmpApplnInterviewSchedulesDTO().getPointofContact().setLabel(pointMap.get(Integer.parseInt(String.valueOf(dbo.get("pointOfContactId")))));
					}
				}
				if(scoreMap.containsKey(Integer.parseInt(dto.getApplicantId()))) {
					EmpApplnInterviewSchedulesDBO schedules = scoreMap.get(Integer.parseInt(dto.getApplicantId()));
					AtomicInteger panelistCount = new AtomicInteger(0);
					AtomicInteger totalScore = new AtomicInteger(0);
					if(!Utils.isNullOrEmpty(schedules)) {
						schedules.getEmpApplnInterviewScoreDBO().forEach( score -> {
							if(!Utils.isNullOrEmpty(score.getErpUsersDBO()) || !Utils.isNullOrEmpty(score.getEmpInterviewUniversityExternalsDBO())) {
								panelistCount.getAndAdd(1);
								totalScore.getAndAdd(!Utils.isNullOrEmpty(score.getTotalScore()) ? score.getTotalScore():0);
							}
						});
						Float avergeScore = null;
						if(!Utils.isNullOrEmpty(totalScore.get()) && !Utils.isNullOrEmpty(panelistCount.get())) {
							avergeScore = (float) (totalScore.get()/panelistCount.get());
							dto.setTotalScore(avergeScore);
						}
					}
				}
				dtos.add(dto);
			});
		}
		return  !dtos.isEmpty()? Mono.just(dtos):Mono.error(new NotFoundException("Data Not Found"));
	}

	public Mono<EmpApplnEntriesDTO> getApplicantsInterviewScoreDetails(String empApplnEntriesId, String applicationNo) {
		EmpApplnInterviewSchedulesDBO dbo = interviewProcessTransaction1.getApplicantsInterviewScoreDetails(empApplnEntriesId);
		if(!Utils.isNullOrEmpty(dbo)) {
		List<Integer> empIds = new ArrayList<Integer>();
		empIds	= dbo.getEmpApplnInterviewPanelDBO().stream().filter(s -> !Utils.isNullOrEmpty(s.getErpUsersDBO()) && s.getRecordStatus() == 'A').map(score -> score.getErpUsersDBO().getEmpDBO().getId()).collect(Collectors.toList());
		Map<Integer,String> empNameMap = interviewProcessTransaction1.getEmpName(empIds).stream()
				.collect(Collectors.toMap(s -> Integer.parseInt(String.valueOf(s.get("empId"))), s ->String.valueOf(s.get("empName"))));
		Tuple interviewTemplateId =  interviewProcessTransaction1.getInterview_template(applicationNo);
		List<Tuple> parameterList = interviewProcessTransaction1.getInterviewParameters(String.valueOf(interviewTemplateId.get("templateId")));
		return this.convertScoreDboToDto(dbo,empNameMap,parameterList);
		} else {
			return Mono.empty();
		}		
	}

	public Mono<EmpApplnEntriesDTO> convertScoreDboToDto(EmpApplnInterviewSchedulesDBO dbo, Map<Integer, String> empNameMap, List<Tuple> parameterList) {
		EmpApplnEntriesDTO dto = null;
		if(!Utils.isNullOrEmpty(dbo)) {
			dto = new EmpApplnEntriesDTO();
			List<EmpApplnInterviewScoreDTO> scoreDtoList = new ArrayList<EmpApplnInterviewScoreDTO>();
			Map<Integer,EmpApplnInterviewScoreDBO> panelScoremap =  new HashMap<Integer, EmpApplnInterviewScoreDBO>();
			dbo.getEmpApplnInterviewScoreDBO().forEach( score -> {
				if(score.getRecordStatus() == 'A') {
					if(!Utils.isNullOrEmpty(score.getErpUsersDBO())) {
						panelScoremap.put(score.getErpUsersDBO().getId(), score);
					} else {
						panelScoremap.put(score.getEmpInterviewUniversityExternalsDBO().getId(), score);
					}
				}
			});

			dbo.getEmpApplnInterviewPanelDBO().forEach( panelMember -> {
				if(!Utils.isNullOrEmpty(panelMember) && panelMember.getRecordStatus() == 'A') {
					EmpApplnInterviewScoreDTO scoreDto = new EmpApplnInterviewScoreDTO();
					//					scoreDto.setEmpApplnInterviewScoreId(score.getEmpApplnInterviewScoreId());
					if(panelMember.isInternalPanel  && !Utils.isNullOrEmpty(panelMember.getErpUsersDBO())) {
						scoreDto.setErpUsers(new SelectDTO());
						scoreDto.getErpUsers().setValue(String.valueOf(panelMember.getErpUsersDBO().getId()));
						if(!Utils.isNullOrEmpty(empNameMap)  && empNameMap.containsKey(panelMember.getErpUsersDBO().getEmpDBO().getId())) {
							scoreDto.getErpUsers().setLabel(empNameMap.get(panelMember.getErpUsersDBO().getEmpDBO().getId()));
						}
					}
					else if(!panelMember.isInternalPanel  && !Utils.isNullOrEmpty(panelMember.getEmpInterviewUniversityExternalsDBO())) {
						scoreDto.setEmpInterviewUniversityExternals(new SelectDTO());
						scoreDto.getEmpInterviewUniversityExternals().setValue(String.valueOf(panelMember.getEmpInterviewUniversityExternalsDBO().getId()));
						scoreDto.getEmpInterviewUniversityExternals().setLabel(panelMember.getEmpInterviewUniversityExternalsDBO().getPanelName());
					}
					else if(!panelMember.isInternalPanel  && Utils.isNullOrEmpty(panelMember.getEmpInterviewUniversityExternalsDBO())) {
						scoreDto.setEmpInterviewUniversityExternals(new SelectDTO());
						scoreDto.getEmpInterviewUniversityExternals().setValue(String.valueOf(panelMember.getErpUsersDBO().getId()));
						if(!Utils.isNullOrEmpty(empNameMap)  && empNameMap.containsKey(panelMember.getErpUsersDBO().getEmpDBO().getId())) {
							scoreDto.getEmpInterviewUniversityExternals().setLabel(empNameMap.get(panelMember.getErpUsersDBO().getEmpDBO().getId()));
						}
					}

					EmpApplnInterviewScoreDBO score = null;
					if(!Utils.isNullOrEmpty(panelMember.getErpUsersDBO()) && panelScoremap.containsKey(panelMember.getErpUsersDBO().getId())){
						score =  panelScoremap.get(panelMember.getErpUsersDBO().getId());
					} if(!Utils.isNullOrEmpty(panelMember.getEmpInterviewUniversityExternalsDBO()) && panelScoremap.containsKey(panelMember.getEmpInterviewUniversityExternalsDBO().getId())) {
						score =  panelScoremap.get(panelMember.getEmpInterviewUniversityExternalsDBO().getId());
					}
					List<EmpApplnInterviewScoreDetailsDTO> scoreDetailsList = new ArrayList<EmpApplnInterviewScoreDetailsDTO>();
					if(!Utils.isNullOrEmpty(score)) {
						scoreDto.setMaxScore(score.getMaxScore());
						scoreDto.setTotalScore(score.getTotalScore());
						scoreDto.setComments(score.getComments());
						score.getEmpApplnInterviewScoreDetailsMap().forEach( scoreDetailsDbo -> {
							if(!Utils.isNullOrEmpty(scoreDetailsDbo) && scoreDetailsDbo.getRecordStatus() == 'A') {
								EmpApplnInterviewScoreDetailsDTO scoreDetailsDto = new  EmpApplnInterviewScoreDetailsDTO();
								scoreDetailsDto.setInterviewScoreDetailsId(scoreDetailsDbo.getInterviewScoreDetailsId());
								scoreDetailsDto.setParameterMaxScore(scoreDetailsDbo.getApplnInterviewTemplateGroupDetailId().getParameterMaxScore());
								scoreDetailsDto.setParameterName(scoreDetailsDbo.getApplnInterviewTemplateGroupDetailId().getParameterName());
								scoreDetailsDto.setAutoCalculate(scoreDetailsDbo.getApplnInterviewTemplateGroupDetailId().getAutoCalculate());
								scoreDetailsDto.setParameterOrderNumber(scoreDetailsDbo.getApplnInterviewTemplateGroupDetailId().getParameterOrderNo());
								scoreDetailsDto.setScoreEntered(scoreDetailsDbo.getScoreEntered());
								scoreDetailsList.add(scoreDetailsDto);
							}
						});	
					}  else {
						parameterList.forEach(parameter -> {
							EmpApplnInterviewScoreDetailsDTO scoreDetailsDto = new  EmpApplnInterviewScoreDetailsDTO();
							scoreDetailsDto.setParameterMaxScore(Integer.parseInt(String.valueOf(parameter.get("maxScore"))));
							scoreDetailsDto.setParameterName(String.valueOf(parameter.get("parameterName")));
							scoreDetailsDto.setAutoCalculate(Boolean.valueOf(String.valueOf(parameter.get("acal"))));
							scoreDetailsDto.setParameterOrderNumber(Integer.parseInt(String.valueOf(parameter.get("orderNo"))));
							scoreDetailsList.add(scoreDetailsDto);
						});
					}
					scoreDetailsList.sort(Comparator.comparing(EmpApplnInterviewScoreDetailsDTO::getParameterOrderNumber));
					scoreDto.setScoreDetailsList(scoreDetailsList);
					scoreDtoList.add(scoreDto);
				}

			});
			dto.setApplicantInterviewScore(scoreDtoList);
		}
		return !Utils.isNullOrEmpty(dto)? Mono.just(dto):Mono.error(new NotFoundException("Data Not Found"));
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> departmentEnable() {
		ApiResult result = new ApiResult();
		result.setSuccess(true);
		return Mono.just(result);
	}

	public Flux<SelectDTO> getInternalPanel(String departmentId, String locationId) throws Exception {
		List<Tuple> list = interviewProcessTransaction1.getInternalPanel(departmentId,locationId);
		return convertPanel(list);
	}

	private Flux<SelectDTO> convertPanel(List<Tuple> list) {
		List<SelectDTO> selectDTOList = new ArrayList<SelectDTO>();
		if(!Utils.isNullOrEmpty(list)) {
			list.forEach(data -> {
				if(!Utils.isNullOrEmpty(data.get("ID")) && !Utils.isNullOrEmpty(data.get("Text"))) {
					SelectDTO selectDTO = new SelectDTO();
					selectDTO.setValue(data.get("ID").toString());
					selectDTO.setLabel(data.get("Text").toString());
					selectDTOList.add(selectDTO);
				}

			});		
		}
		return Flux.fromIterable(selectDTOList);
	}

	public Flux<LookupItemDTO> getExternalPanel(String departmentId, String locationId) {
		List<Tuple> list = interviewProcessTransaction1.getExternalPanel(departmentId,locationId);
		return convertExternalPanelDboToDto(list);
	}

	private Flux<LookupItemDTO> convertExternalPanelDboToDto(List<Tuple> list) {
		List<LookupItemDTO> selectDTOList = new ArrayList<LookupItemDTO>();
		if(!Utils.isNullOrEmpty(list)) {
			list.forEach(data -> {
				if(!Utils.isNullOrEmpty(data.get("ID")) && !Utils.isNullOrEmpty(data.get("Text")) && !Utils.isNullOrEmpty(data.get("status"))) {
					LookupItemDTO selectDTO = new LookupItemDTO();
					selectDTO.setValue(String.valueOf(data.get("ID")));
					selectDTO.setLabel(String.valueOf(data.get("Text")));
					if(String.valueOf(data.get("status")).equalsIgnoreCase("1") ) {
						selectDTO.setStatus(true);
					}  else {
						selectDTO.setStatus(false);
					}
					selectDTOList.add(selectDTO);
				}
			});		
		}
		return Flux.fromIterable(selectDTOList);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveRemoveShortList(Mono<EmpApplnEntriesDTO> dto, String userId) {
		return dto
				.handle((empApplnEntriesDTO, synchronousSink) -> {
					synchronousSink.next(empApplnEntriesDTO);
				}).cast(EmpApplnEntriesDTO.class)
				.map(data -> convertDtoToDboOne(data, userId))
				.flatMap(s -> {
					if (!Utils.isNullOrEmpty(s)) {
						interviewProcessTransaction1.saveOrUpdate(s);
					}
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);

	}

	private List<Object> convertDtoToDboOne(EmpApplnEntriesDTO data, String userId) {
		List<Object> objList = new ArrayList<Object>();
		if(!Utils.isNullOrEmpty(data)) {
			if(!Utils.isNullOrEmpty(data.getApplicationStatus().trim().equalsIgnoreCase("EMP_STAGE1_HOD_SHORTLISTED"))) {
				EmpApplnEntriesDBO empApplnEntriesDBO = interviewProcessTransaction1.getData(Integer.parseInt(data.getApplicantId()));
				empApplnEntriesDBO.setShortistedLocationId(null);
				empApplnEntriesDBO.setShortlistedDepartmentId(null);
				empApplnEntriesDBO.setShortlistedEmployeeId(null);
				empApplnEntriesDBO.setIsShortlistedForInterview(false);
				empApplnEntriesDBO.setModifiedUsersId(Integer.parseInt(userId));
				Tuple workFlowProcess = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("EMP_APPLICATION_SUBMITTED");
				if(!Utils.isNullOrEmpty(workFlowProcess)) {
					int workflowProcessId = Integer.parseInt(String.valueOf(workFlowProcess.get("erp_work_flow_process_id")));
					ErpWorkFlowProcessDBO erpWorkFlowProcessDBO = commonApiTransaction1.getErpWorkFlowProcess(workflowProcessId);
					empApplnEntriesDBO.setApplicationStatusTime(LocalDateTime.now());
					empApplnEntriesDBO.setApplicationCurrentProcessStatus(erpWorkFlowProcessDBO);
					empApplnEntriesDBO.setApplicantStatusTime(LocalDateTime.now());
					empApplnEntriesDBO.setApplicantCurrentProcessStatus(erpWorkFlowProcessDBO);
					ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
					erpWorkFlowProcessStatusLogDBO.setEntryId(empApplnEntriesDBO.getId());
					erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
					erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
					erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(new ErpWorkFlowProcessDBO());
					erpWorkFlowProcessStatusLogDBO.getErpWorkFlowProcessDBO().setId(workflowProcessId);
					objList.add(erpWorkFlowProcessStatusLogDBO);
				}                         
				objList.add(empApplnEntriesDBO);
			}
		}	
		return objList;
	}

	public Flux<SelectDTO> getPanelList() {
		return interviewProcessTransaction1.getPanelList().flatMapMany(Flux::fromIterable).map(this::convertPanelList);
	}
	
	public SelectDTO convertPanelList(Tuple tuple) {
		if(!Utils.isNullOrEmpty(tuple)) {
			if(!Utils.isNullOrEmpty(tuple.get("ID")) && !Utils.isNullOrEmpty(tuple.get("Text"))) {
				SelectDTO selectDTO = new SelectDTO();
				selectDTO.setValue(tuple.get("ID").toString());
				selectDTO.setLabel(tuple.get("Text").toString());
				return selectDTO;
			}
		}
		return null;
	}
}