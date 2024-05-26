package com.christ.erp.services.handlers.employee.attendance;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
import com.christ.erp.services.dbobjects.common.ErpUsersDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpWorkDiaryEntriesDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.attendance.EmpWorkDiaryEntriesDTO;
import com.christ.erp.services.dto.employee.attendance.EmpWorkDiaryEntriesDetailsDTO;
import com.christ.erp.services.dto.employee.common.EmpDTO;
import com.christ.erp.services.handlers.common.CommonApiHandler;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.employee.attendance.WorkDiaryApprovalTransaction;
import com.christ.erp.services.transactions.employee.attendance.WorkDiaryEntryTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class WorkDiaryApprovalHandler {

	@Autowired
	WorkDiaryEntryTransaction workDiaryEntryTransaction;

	@Autowired
	CommonApiTransaction commonApiTransaction1;

	@Autowired
	CommonApiHandler commonApiHandler;

	@Autowired
	WorkDiaryApprovalTransaction workDiaryApprovalTransaction;

	public Flux<EmpWorkDiaryEntriesDTO> getEmployeeDetailsForApprover(Map<String,String> requestParams, String userId) {
		List<Integer> employeesIdList = workDiaryApprovalTransaction.getEmployeesList(userId);
		List<EmpWorkDiaryEntriesDTO> dto = new  ArrayList<EmpWorkDiaryEntriesDTO>();
		Map<String,EmpWorkDiaryEntriesDTO> empHours = new HashMap<String, EmpWorkDiaryEntriesDTO>();  
		Map<String, EmpWorkDiaryEntriesDTO> userMap = new HashMap<String, EmpWorkDiaryEntriesDTO>();
		Map<String, Map<String, EmpWorkDiaryEntriesDetailsDTO>> empActivityHours1 = new LinkedHashMap<String, Map<String, EmpWorkDiaryEntriesDetailsDTO>>();
		List<EmpWorkDiaryEntriesDBO> workDiaryList = workDiaryApprovalTransaction.getEmployeeDetailsForApprover(requestParams, employeesIdList);
		if(!Utils.isNullOrEmpty(workDiaryList)) {
		workDiaryList.forEach(dbos -> {  
		    EmpWorkDiaryEntriesDTO dtos = new EmpWorkDiaryEntriesDTO();
			dbos.getEmpWorkDiaryEntriesDetailsDBOSet().forEach(subdbo -> {
				if(subdbo.getRecordStatus() == 'A') {
					EmpWorkDiaryEntriesDetailsDTO subdto = new EmpWorkDiaryEntriesDetailsDTO();	
					if(!Utils.isNullOrEmpty(subdbo.getEmpWorkDiaryActivityDBO())) {
						if(!empActivityHours1.containsKey(dbos.getEmpDBO().getEmpNumber())) {
							Map<String, EmpWorkDiaryEntriesDetailsDTO> activityHours = new LinkedHashMap<String, EmpWorkDiaryEntriesDetailsDTO>();
							if(!empActivityHours1.containsKey(String.valueOf(subdbo.getEmpWorkDiaryActivityDBO().getActivityName()))) {
								subdto.setActivity(new SelectDTO());
								subdto.getActivity().setValue(String.valueOf(subdbo.getEmpWorkDiaryActivityDBO().getId()));
								subdto.getActivity().setLabel(subdbo.getEmpWorkDiaryActivityDBO().getActivityName().toString());
								long totaltime = 0;
								int hour = subdbo.getTotalTime().getHour();
								int minutes = subdbo.getTotalTime().getMinute();
								int sec = subdbo.getTotalTime().getSecond();
								totaltime = totaltime + (hour* 60 * 60 )+(minutes *60) +sec;
								subdto.setTotalHour(String.valueOf(totaltime));
								subdto.setActivityName(subdbo.getEmpWorkDiaryActivityDBO().getActivityName());
								long totalHours= totaltime / 3600;
								long totalMin= (totaltime % 3600) / 60;
								subdto.setTotalHours(String.valueOf(totalHours) + ":" + String.valueOf(totalMin) +" hours");
								activityHours.put(subdbo.getEmpWorkDiaryActivityDBO().getActivityName().toString(), subdto);
							} 
							empActivityHours1.put(dbos.getEmpDBO().getEmpNumber().toString(), activityHours);
						} else {
							Map<String, EmpWorkDiaryEntriesDetailsDTO> activityHour = empActivityHours1.get(dbos.getEmpDBO().getEmpNumber().toString());
							if(!activityHour.containsKey(String.valueOf(subdbo.getEmpWorkDiaryActivityDBO().getActivityName()))) {
								subdto.setActivity(new SelectDTO());
								subdto.getActivity().setValue(String.valueOf(subdbo.getEmpWorkDiaryActivityDBO().getId()));
								subdto.getActivity().setLabel(subdbo.getEmpWorkDiaryActivityDBO().getActivityName().toString());
								long totaltime = 0;
								int hour = subdbo.getTotalTime().getHour();
								int minutes = subdbo.getTotalTime().getMinute();
								int sec = subdbo.getTotalTime().getSecond();
								totaltime = totaltime + (hour* 60 * 60 )+(minutes *60) +sec;
								subdto.setTotalHour(String.valueOf(totaltime));
								subdto.setActivityName(subdbo.getEmpWorkDiaryActivityDBO().getActivityName());
								long totalHours= totaltime / 3600;
								long totalMin= (totaltime % 3600) / 60;
								subdto.setTotalHours(String.valueOf(totalHours) + ":" + String.valueOf(totalMin) + "hours");
								activityHour.put(subdbo.getEmpWorkDiaryActivityDBO().getActivityName().toString(), subdto);
							} else {
								EmpWorkDiaryEntriesDetailsDTO activityHrs = activityHour.get(String.valueOf(subdbo.getEmpWorkDiaryActivityDBO().getActivityName()));
								long totaltime = 0;
								int hour = subdbo.getTotalTime().getHour();
								int minutes = subdbo.getTotalTime().getMinute();
								int sec = subdbo.getTotalTime().getSecond();
								totaltime = totaltime + (hour* 60 * 60 )+(minutes *60) +sec ;
								long t4 = Long.parseLong(activityHrs.getTotalHour());
								long total = t4 + totaltime;
								activityHrs.setTotalHour(String.valueOf(total));
								long totalHours= total / 3600;
								long totalMin= (total % 3600) / 60;
								activityHrs.setTotalHours(String.valueOf(totalHours) + ":" + String.valueOf(totalMin) +" hours");
								activityHour.replace(subdbo.getEmpWorkDiaryActivityDBO().getActivityName().toString(), activityHrs);
							}
							empActivityHours1.put(dbos.getEmpDBO().getEmpNumber().toString(), activityHour);
						}
					} else {
						if(Utils.isNullOrEmpty(subdto.getOtherActivity())) {
							if(!empActivityHours1.containsKey(dbos.getEmpDBO().getEmpNumber())) {
								Map<String, EmpWorkDiaryEntriesDetailsDTO> activityHours = new LinkedHashMap<String, EmpWorkDiaryEntriesDetailsDTO>();
								if(!empActivityHours1.containsKey(String.valueOf(subdbo.getOtherActivity()))) {
									subdto.setOtherActivity(subdbo.getOtherActivity());
									long totaltime = 0;
									int hour = subdbo.getTotalTime().getHour();
									int minutes = subdbo.getTotalTime().getMinute();
									int sec = subdbo.getTotalTime().getSecond();
									totaltime = totaltime + (hour* 60 * 60 )+(minutes *60) +sec;
									subdto.setTotalHour(String.valueOf(totaltime));
									long totalHours= totaltime / 3600;
									long totalMin= (totaltime % 3600) / 60;
									subdto.setOtherActivityTotal(String.valueOf(totalHours) + ":" + String.valueOf(totalMin) +" hours");
									activityHours.put(subdbo.getOtherActivity().toString(), subdto);
								} 
								empActivityHours1.put(dbos.getEmpDBO().getEmpNumber().toString(), activityHours);
							} else {
								Map<String, EmpWorkDiaryEntriesDetailsDTO> activityHour = empActivityHours1.get(dbos.getEmpDBO().getEmpNumber().toString());
								if(!activityHour.containsKey(String.valueOf(subdbo.getOtherActivity()))) {
									subdto.setOtherActivity(subdbo.getOtherActivity());
									long totaltime = 0;
									int hour = subdbo.getTotalTime().getHour();
									int minutes = subdbo.getTotalTime().getMinute();
									int sec = subdbo.getTotalTime().getSecond();
									totaltime = totaltime + (hour* 60 * 60 )+(minutes *60) +sec;
									subdto.setTotalHour(String.valueOf(totaltime));
									long totalHours= totaltime / 3600;
									long totalMin= (totaltime % 3600) / 60;
									subdto.setOtherActivityTotal(String.valueOf(totalHours) + ":" + String.valueOf(totalMin) +" hours");
									activityHour.put(subdbo.getOtherActivity().toString(), subdto);
								} else {
									EmpWorkDiaryEntriesDetailsDTO activityHrs = activityHour.get(String.valueOf(subdbo.getOtherActivity()));
									long totaltime = 0;
									int hour = subdbo.getTotalTime().getHour();
									int minutes = subdbo.getTotalTime().getMinute();
									int sec = subdbo.getTotalTime().getSecond();
									totaltime = totaltime + (hour* 60 * 60 )+(minutes *60) +sec;
									long t4 = Long.parseLong(activityHrs.getTotalHour());
									long total = t4 + totaltime;
									activityHrs.setTotalHour(String.valueOf(total));
									long totalHours= total / 3600;
									long totalMin= (total % 3600) / 60;
									activityHrs.setOtherActivityTotal(String.valueOf(totalHours) + ":" + String.valueOf(totalMin) +" hours");
									activityHour.replace(subdbo.getOtherActivity().toString(), activityHrs);
								}
								empActivityHours1.put(dbos.getEmpDBO().getEmpNumber().toString(), activityHour);
							}
						}
					}
					if(!empHours.containsKey(dbos.getEmpDBO().getEmpNumber().toString())) {
						dtos.setEmpDTO(new EmpDTO());
						dtos.getEmpDTO().setEmpId(dbos.getEmpDBO().getId().toString());
						dtos.getEmpDTO().setEmpNo(dbos.getEmpDBO().getEmpNumber().toString());  
						dtos.getEmpDTO().setEmpName(dbos.getEmpDBO().getEmpName());
						dtos.setWorkFlowStatus(dbos.getErpApplicationWorkFlowProcessDBO().getProcessCode());
					    long totaltime = 0;
						int hour = subdbo.getTotalTime().getHour();
						int minutes = subdbo.getTotalTime().getMinute();
						int sec = subdbo.getTotalTime().getSecond();
						totaltime = totaltime + (hour * 3600) + (minutes * 60) + sec;
						dtos.setTotalHours(String.valueOf(totaltime));
						long totalHours= totaltime / 3600;
						long totalMin= (totaltime % 3600) / 60;
						dtos.setTotalHour(String.valueOf(totalHours) + ":" + String.valueOf(totalMin) +"hours");
						empHours.put(dbos.getEmpDBO().getEmpNumber().toString(), dtos);
					} else {
						EmpWorkDiaryEntriesDTO empNumber = empHours.get(dbos.getEmpDBO().getEmpNumber());
						long totaltime = 0;
						int hour = subdbo.getTotalTime().getHour();
						int minutes = subdbo.getTotalTime().getMinute();
						int sec = subdbo.getTotalTime().getSecond();
						totaltime = totaltime + (hour * 3600) + (minutes * 60) + sec;
						long t4 = Long.valueOf(empNumber.getTotalHours());
						long total = t4 + totaltime;
						empNumber.setTotalHours(String.valueOf(total));
						empHours.replace(dbos.getEmpDBO().getEmpNumber(), empNumber);
						long totalHours= total / 3600;
						long totalMin= (total % 3600) / 60;
						empNumber.setTotalHour(String.valueOf(totalHours) + ":" + String.valueOf(totalMin) +"hours");
					}	
				}
			});	
			
     	empActivityHours1.forEach((k, v) -> {
     	List<EmpWorkDiaryEntriesDetailsDTO> workDiaryDetailsLists = new  ArrayList<EmpWorkDiaryEntriesDetailsDTO>();
				v.forEach((k1, v1) -> {
					workDiaryDetailsLists.add(v1);
				});
				dtos.setEmpWorkDiaryEntriesDetails(workDiaryDetailsLists);
			});
			employeesIdList.remove(dbos.getEmpDBO().getId());
		});
	}
		empHours.forEach((k, v) -> {
			userMap.put(k,	v);
				dto.add(v);
		});
		List<Tuple> empList = workDiaryApprovalTransaction.getEmployeesLists(userId);
		if(!Utils.isNullOrEmpty(empList)) {
			empList.forEach(detailsDbo -> {
				if(!Utils.isNullOrEmpty((detailsDbo.get("empId")))) {
					if(employeesIdList.contains(Integer.parseInt(detailsDbo.get("empId").toString()))) {
						EmpWorkDiaryEntriesDTO dtos2 = new EmpWorkDiaryEntriesDTO();
						dtos2.setEmpDTO(new EmpDTO());
						dtos2.getEmpDTO().setEmpId(detailsDbo.get("empId").toString());
						if(!Utils.isNullOrEmpty((detailsDbo.get("empNo")))) {
							dtos2.getEmpDTO().setEmpNo(detailsDbo.get("empNo").toString());
						}
						if(!Utils.isNullOrEmpty((detailsDbo.get("empName")))) {
							dtos2.getEmpDTO().setEmpName(detailsDbo.get("empName").toString());
						}
						dtos2.setTotalHour("0");
						dto.add(dtos2);
					}
				}
			});
		}
		return Flux.fromIterable(dto);
	 }
		
	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> workDiaryApproverUpdate(Mono<List<EmpWorkDiaryEntriesDTO>> dto, Map<String, String> requestParams, List<String> empId, String userId) {
		return dto.map(data -> convertDtoToDbo(data,requestParams, empId, userId))
				.flatMap(s -> {
					workDiaryApprovalTransaction.merge(s);
					return Mono.just(Boolean.TRUE);
				}).map(Utils::responseResult);
	}

	private List<EmpWorkDiaryEntriesDBO> convertDtoToDbo(List<EmpWorkDiaryEntriesDTO> dto1, Map<String, String> requestParams, List<String> empId, String userId)  {
		Tuple tuple = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("WORK_DIARY_APPROVED");
		Tuple workFlowProcess = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("WORK_DIARY_CLARIFICATION");
		List<Integer> empids = empId.stream().map(Integer::parseInt).collect(Collectors.toList());
		List<EmpWorkDiaryEntriesDBO> workDiaryApprovalList =  workDiaryApprovalTransaction.getEmployeeDetailsForUpdate(requestParams,empids);
	    List<ErpWorkFlowProcessStatusLogDBO> statusList = new ArrayList<ErpWorkFlowProcessStatusLogDBO>();
		List<ErpEmailsDBO> emailsList = new ArrayList<ErpEmailsDBO>();
		Set<Integer> approversIdSet = new LinkedHashSet<Integer>();
		dto1.forEach(dtos -> {
		workDiaryApprovalList.forEach(dbo ->{
			// if(dbo.getEmpDBO().getId()==Integer.parseInt(dtos.getEmpDTO().getEmpId())) {
			if(dbo != null && dbo.getEmpDBO() != null && dtos != null && dtos.getEmpDTO() != null 
					   && dtos.getEmpDTO().getEmpId() != null && dbo.getEmpDBO().getId() != null 
					   && dbo.getEmpDBO().getId() == Integer.parseInt(dtos.getEmpDTO().getEmpId())) {
				if(!Utils.isNullOrEmpty(dtos.getStatus()) && dtos.getStatus().equalsIgnoreCase("approve")) {
					dbo.setModifiedUsersId(Integer.parseInt(userId));
					if(tuple.get("applicant_status_display_text")!= null  && !Utils.isNullOrWhitespace(tuple.get("applicant_status_display_text").toString())) {
						ErpWorkFlowProcessDBO applicant = new ErpWorkFlowProcessDBO();
						applicant.id =Integer.parseInt(tuple.get("erp_work_flow_process_id").toString());
						dbo.erpApplicantWorkFlowProcessDBO = applicant;
						dbo.setApplicantStatusLogTime(LocalDateTime.now());
					}
					if(tuple.get("application_status_display_text")!=null  && !Utils.isNullOrWhitespace(tuple.get("application_status_display_text").toString())) {
						ErpWorkFlowProcessDBO application = new ErpWorkFlowProcessDBO();
						application.id =Integer.parseInt(tuple.get("erp_work_flow_process_id").toString());
						dbo.erpApplicationWorkFlowProcessDBO = application;
						dbo.setApplicationStatusLogTime(LocalDateTime.now());
					}
					dbo.setApprovedDate(LocalDateTime.now());
				} else if(!Utils.isNullOrEmpty(dtos.getStatus()) && dtos.getStatus().equalsIgnoreCase("clarification")) {
					if(!Utils.isNullOrWhitespace(workFlowProcess.get("applicant_status_display_text").toString())) {
						dbo.setClarificationRemarks(dtos.getClarificationRemarks());
						ErpWorkFlowProcessDBO applicant = new ErpWorkFlowProcessDBO();
						applicant.id =Integer.parseInt(workFlowProcess.get("erp_work_flow_process_id").toString());
						dbo.erpApplicantWorkFlowProcessDBO = applicant;
						dbo.setApplicantStatusLogTime(LocalDateTime.now());
						ErpWorkFlowProcessDBO application = new ErpWorkFlowProcessDBO();
						application.id =Integer.parseInt(workFlowProcess.get("erp_work_flow_process_id").toString());
						dbo.erpApplicationWorkFlowProcessDBO = application;
						dbo.setApplicationStatusLogTime(LocalDateTime.now());
					}
					Integer empIds = workDiaryEntryTransaction.getEmployeeId(userId);
					Tuple approversDeatils = workDiaryApprovalTransaction.getApproversIdByEmployeeId1(empIds);
					 if(!Utils.isNullOrEmpty(approversDeatils)) {
					approversIdSet.add(Integer.parseInt(approversDeatils.get("usersId").toString()));
					List<String> templateTypes = new ArrayList<String>();
					templateTypes.add("Mail");
					List<String> templateNames = new ArrayList<String>();
					templateNames.add("WORK_DIARY_CLARIFICATION_INFORM_EMPLOYEE");
					List<ErpTemplateDBO> erpTemplateDBO = commonApiTransaction1.getErpTemplateByTemplateCodeAndTemplateType1(templateTypes, templateNames);
					ErpEmailsDBO erpEmailsDBO = new ErpEmailsDBO();
					erpEmailsDBO.entryId = dbo.getId();
					ErpUsersDBO erpUsersDBO = new ErpUsersDBO();
					erpUsersDBO.id = Integer.parseInt(approversDeatils.get("usersId").toString());
					erpEmailsDBO.erpUsersDBO = erpUsersDBO;
					String msgBody=erpTemplateDBO.iterator().next().getTemplateContent();
					msgBody = msgBody.replace("[EMPLOYEE_ID] ", dtos.getEmpDTO().getEmpNo());
					msgBody = msgBody.replace("[EMPLOYEE_NAME]", dtos.getEmpDTO().getEmpName());
					msgBody = msgBody.replace("[FROM_DATE]",requestParams.get("startDate"));
					msgBody = msgBody.replace("[TO_DATE]" , requestParams.get("endDate"));
					msgBody = msgBody.replace("[REMARKS]", dtos.getClarificationRemarks());
					erpEmailsDBO.emailContent = msgBody;
					if(!Utils.isNullOrEmpty(erpTemplateDBO.iterator().next().getMailSubject()))
						erpEmailsDBO.emailSubject = erpTemplateDBO.iterator().next().getMailSubject();
					if(!Utils.isNullOrEmpty(erpTemplateDBO.iterator().next().getMailFromName()))
						erpEmailsDBO.senderName = erpTemplateDBO.iterator().next().getMailFromName();
					erpEmailsDBO.recipientEmail=approversDeatils.get("personalEmailId").toString();
					erpEmailsDBO.createdUsersId = Integer.parseInt(userId);
					erpEmailsDBO.recordStatus = 'A';
					emailsList.add(erpEmailsDBO);
					commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(dbo.erpApplicantWorkFlowProcessDBO.id,"WORK_DIARY_CLARIFICATION_INFORM_EMPLOYEE",approversIdSet,null,null,emailsList);
				   }
				}
		ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
		erpWorkFlowProcessStatusLogDBO.entryId = dbo.getId();
		ErpWorkFlowProcessDBO workFlowProcessDBO = new ErpWorkFlowProcessDBO();
		if(!Utils.isNullOrEmpty(dbo.erpApplicantWorkFlowProcessDBO.id))
			workFlowProcessDBO.id = dbo.erpApplicantWorkFlowProcessDBO.id;
		else
			workFlowProcessDBO.id = dbo.erpApplicationWorkFlowProcessDBO.id;
			erpWorkFlowProcessStatusLogDBO.erpWorkFlowProcessDBO = workFlowProcessDBO;
			erpWorkFlowProcessStatusLogDBO.createdUsersId = Integer.parseInt(userId);
			erpWorkFlowProcessStatusLogDBO.recordStatus = 'A';
			statusList.add(erpWorkFlowProcessStatusLogDBO);
			}
		});
		});
		commonApiTransaction1.saveErpWorkFlowProcessStatusLogDBO1(statusList);
		return workDiaryApprovalList;
	}
}
	
