package com.christ.erp.services.handlers.employee.recruitment;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpUsersDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnInterviewSchedulesDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnInterviewScoreDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnInterviewScoreDetailsDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnInterviewTemplateGroupDetailsDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpInterviewUniversityExternalsDBO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.recruitment.InterviewScoreEntryDTO;
import com.christ.erp.services.dto.employee.recruitment.InterviewScoreEntryGroupHeadDTO;
import com.christ.erp.services.dto.employee.recruitment.InteviewScoreEntryGroupDetailsDTO;
import com.christ.erp.services.helpers.employee.recruitment.InterviewScoreEntryHelper;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.employee.recruitment.InterviewScoreEntryTransaction;

import kotlin.collections.ArrayDeque;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class IntrviewScoreEntryHandler {

	@Autowired
	InterviewScoreEntryTransaction interviewScoreEntryransaction;

	@Autowired
	CommonApiTransaction commonApiTransaction1;

	InterviewScoreEntryHelper interviewScoreEntryHelper = InterviewScoreEntryHelper.getInstance();

	public void getIntervewTemplateForCategory(ApiResult<InterviewScoreEntryDTO> resultDto,
			InterviewScoreEntryDTO interviewScoreEntryDTO) throws Exception{
		Boolean isEdit = false;
		resultDto.dto = new InterviewScoreEntryDTO();
		resultDto.dto.applicationNumber = interviewScoreEntryDTO.applicationNumber;
		resultDto.dto.applicantName = interviewScoreEntryDTO.applicantName;
		resultDto.dto.interviewPanelMember = interviewScoreEntryDTO.interviewPanelMember;
		Tuple employeeCategory = interviewScoreEntryransaction.getEmployeeDetails(interviewScoreEntryDTO.applicationNumber);
		if(!Utils.isNullOrEmpty(employeeCategory)) {
			resultDto.dto.empApplnEntryId = Integer.parseInt(String.valueOf(employeeCategory.get("applnEntryId")));
			resultDto.dto.categoryId = String.valueOf(employeeCategory.get("categoryId"));
			resultDto.dto.applicantName = String.valueOf(employeeCategory.get("name"));
			resultDto.dto.applicationNumber = String.valueOf(employeeCategory.get("applnNo"));
			Tuple workflowProcess = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("EMP_STAGE1_SELECTED");
			if(!Utils.isNullOrEmpty(workflowProcess)) {
				int processOrder = Integer.parseInt(String.valueOf(workflowProcess.get("processOrder")));
				Tuple getEmployeeCurrentWorkFlow = interviewScoreEntryransaction.getEmployeeDetails(resultDto.dto.applicationNumber);
				if(!Utils.isNullOrEmpty(getEmployeeCurrentWorkFlow)) {
					int currentProcessOrder = Integer.parseInt(String.valueOf(getEmployeeCurrentWorkFlow.get("processOrder")));
					if(!Utils.isNullOrEmpty(processOrder) && !Utils.isNullOrEmpty(currentProcessOrder) && currentProcessOrder >= processOrder) {
						resultDto.dto.setEditable(false); 
					} else {
						resultDto.dto.setEditable(true);
					}
				}
			}
			List<Tuple> templateDetails = interviewScoreEntryransaction.getInterviewtemplate(resultDto);
			if(templateDetails != null && templateDetails.size() > 0) {
				interviewScoreEntryHelper.getInterviewTemplate(templateDetails, resultDto, true);
			}else {
				templateDetails = interviewScoreEntryransaction.getInterviewtemplateNew(resultDto);
				if(templateDetails != null && templateDetails.size() > 0) {
					interviewScoreEntryHelper.getInterviewTemplate(templateDetails, resultDto, isEdit);
				} else {
					resultDto.success = false;
					resultDto.failureMessage = "Error in finding template";
				}		
			}
		} else {
			resultDto.success = false;
			resultDto.failureMessage = "Invalid application number";
		}		
	}

	public void validateScoreDetails(InterviewScoreEntryDTO interviewScoreEntryDTO, ApiResult<ModelBaseDTO> result) throws Exception{
		Tuple employeeCategory = interviewScoreEntryransaction.getEmployeeDetails(interviewScoreEntryDTO.applicationNumber);
		if(!Utils.isNullOrEmpty(employeeCategory)) {
			interviewScoreEntryDTO.empApplnEntryId = Integer.parseInt(String.valueOf(employeeCategory.get("applnEntryId")));
			interviewScoreEntryDTO.categoryId = String.valueOf(employeeCategory.get("categoryId"));
			interviewScoreEntryDTO.applicantName = String.valueOf(employeeCategory.get("name"));
			interviewScoreEntryDTO.applicationNumber = String.valueOf(employeeCategory.get("applnNo"));
		}
		List<Tuple> checkMaxScoreResult = interviewScoreEntryransaction.getMaxScoreDetails(interviewScoreEntryDTO);
		Map<Integer, Integer> maxScoreOfDetailsMap = null;
		Map<Integer, String> detailsNameMap = null;

		if(checkMaxScoreResult != null) {
			maxScoreOfDetailsMap = new HashMap<Integer, Integer>();
			detailsNameMap = new HashMap<Integer, String>();
			for (Tuple tuple : checkMaxScoreResult) {
				String detailId =  String.valueOf(tuple.get("detail_id"));
				String detailParam =  String.valueOf(tuple.get("detail_parameter"));
				String detailMaxScore =  String.valueOf(tuple.get("detail_max_score"));
				detailsNameMap.put(Integer.parseInt(detailId), detailParam);
				maxScoreOfDetailsMap.put(Integer.parseInt(detailId), Integer.parseInt(detailMaxScore));
			}	
		}else {
			result.failureMessage = "Max Score Error";
		}
		InteviewScoreEntryGroupDetailsDTO todetails = null;
		for (Entry<Integer, InterviewScoreEntryGroupHeadDTO> map : interviewScoreEntryDTO.groupHeadMap.entrySet()) {
			InterviewScoreEntryGroupHeadDTO scoreEntrydetailTO = map.getValue();
			for (Entry<Integer, InteviewScoreEntryGroupDetailsDTO> detailDto : scoreEntrydetailTO.groupDetailsMap.entrySet()) {
				todetails = detailDto.getValue();
				if(todetails.obtainedScore != null ) {
					if( maxScoreOfDetailsMap.containsKey(Integer.parseInt(todetails.groupDetailId))) {
						int maxScore = maxScoreOfDetailsMap.get(Integer.parseInt(todetails.groupDetailId));
						int obtainedScore = todetails.obtainedScore;
						if(obtainedScore > maxScore) {
							result.failureMessage = "Obtained score is greater than Max score for "+detailsNameMap.get(Integer.parseInt(todetails.groupDetailId));
						}
					}
				}else {
					result.failureMessage = "Obtained score is empty for "+detailsNameMap.get(Integer.parseInt(todetails.groupDetailId));
				}
			}
		}
	}

	public void saveInterviewScore(InterviewScoreEntryDTO dto, ApiResult<ModelBaseDTO> result) throws Exception {   // save Entry
		boolean isTrue = false;
		EmpApplnInterviewScoreDBO empApplnInterviewScoreDBO = new EmpApplnInterviewScoreDBO();
		List<Tuple> InterviewScheduleId = interviewScoreEntryransaction.getInterviewScheduleId(dto);
		String scheduleId = !Utils.isNullOrEmpty(InterviewScheduleId.get(0)) ? String.valueOf(InterviewScheduleId.get(0).get("schedule_id")) : null;;
		Set<EmpApplnInterviewScoreDetailsDBO> interviewScoreEntryDetails = new HashSet<EmpApplnInterviewScoreDetailsDBO>();
		if(dto.id == null || dto.id.isEmpty()) {
			empApplnInterviewScoreDBO.createdUsersId = dto.userId;
			empApplnInterviewScoreDBO.modifiedUsersId = dto.userId;
		} else {
			empApplnInterviewScoreDBO.empApplnInterviewScoreId = Integer.parseInt(dto.id);
			empApplnInterviewScoreDBO.modifiedUsersId = dto.userId;
		}
		empApplnInterviewScoreDBO.empApplnEntriesDBO = new EmpApplnEntriesDBO();
		if(!Utils.isNullOrEmpty(dto.empApplnEntryId)) {
			empApplnInterviewScoreDBO.empApplnEntriesDBO.id = dto.empApplnEntryId;
		}
		if(!Utils.isNullOrEmpty(dto.getTotalScore())) {
			empApplnInterviewScoreDBO.setTotalScore(dto.getTotalScore());
		}
		if(!Utils.isNullOrEmpty(dto.getTotalMaxScore())) {
			empApplnInterviewScoreDBO.setMaxScore(dto.getTotalMaxScore());
		}
		if(!Utils.isNullOrEmpty(dto.getInterviewPanelMember().label)) {
			if(!dto.getInterviewPanelMember().label.contains("External")) {
				empApplnInterviewScoreDBO.erpUsersDBO = new ErpUsersDBO();
				empApplnInterviewScoreDBO.erpUsersDBO.id = Integer.parseInt(dto.interviewPanelMember.value); 	
			} else {
				empApplnInterviewScoreDBO.empInterviewUniversityExternalsDBO = new EmpInterviewUniversityExternalsDBO();
				empApplnInterviewScoreDBO.empInterviewUniversityExternalsDBO.id = Integer.parseInt(dto.interviewPanelMember.value);	
			}
		}
		empApplnInterviewScoreDBO.empApplnInterviewSchedulesDBO =  new EmpApplnInterviewSchedulesDBO();
		if(!scheduleId.isEmpty())
			empApplnInterviewScoreDBO.empApplnInterviewSchedulesDBO.empApplnInterviewSchedulesId = Integer.parseInt(scheduleId) ;
		if(dto.commentRequired != null && dto.commentRequired.equals("true")) {
			if(dto.comments != null && !dto.comments.isEmpty()) {
				empApplnInterviewScoreDBO.comments = dto.comments;
			}else {
				result.failureMessage = "Comments is required";
			}
		}else {
			empApplnInterviewScoreDBO.comments = null;
		}
		empApplnInterviewScoreDBO.recordStatus = 'A';
		for (Entry<Integer, InterviewScoreEntryGroupHeadDTO> map : dto.groupHeadMap.entrySet()) {
			InterviewScoreEntryGroupHeadDTO scoreEntrydetailTO = map.getValue();
			for (Entry<Integer, InteviewScoreEntryGroupDetailsDTO> detailDto : scoreEntrydetailTO.groupDetailsMap.entrySet()) {
				EmpApplnInterviewScoreDetailsDBO empscoreEntryDetail = new EmpApplnInterviewScoreDetailsDBO();
				if(detailDto.getValue().id == null || detailDto.getValue().id.isEmpty()) {
					empscoreEntryDetail.createdUserId = dto.userId;
					empscoreEntryDetail.modifiedUserId = dto.userId;
				}else {
					empscoreEntryDetail.interviewScoreDetailsId = Integer.parseInt(detailDto.getValue().id);
					empscoreEntryDetail.modifiedUserId = dto.userId;
				}
				empscoreEntryDetail.applnInterviewScoreId = empApplnInterviewScoreDBO;
				empscoreEntryDetail.applnInterviewTemplateGroupDetailId = new EmpApplnInterviewTemplateGroupDetailsDBO();
				empscoreEntryDetail.applnInterviewTemplateGroupDetailId.setId(Integer.parseInt(detailDto.getValue().groupDetailId));
				empscoreEntryDetail.recordStatus = 'A';
				empscoreEntryDetail.scoreEntered = detailDto.getValue().obtainedScore;
				interviewScoreEntryDetails.add(empscoreEntryDetail);				
			}
		}
		empApplnInterviewScoreDBO.empApplnInterviewScoreDetailsMap = interviewScoreEntryDetails;
		interviewScoreEntryransaction.saveOrUpdate(empApplnInterviewScoreDBO);
		BigInteger panelCount = interviewScoreEntryransaction.getPanelMemberCount(dto.empApplnEntryId);
		BigInteger scoreEnteredPanelCount = interviewScoreEntryransaction.getScoreEnteredPanelCount(dto.empApplnEntryId);
		if(!Utils.isNullOrEmpty(panelCount) && !Utils.isNullOrEmpty(scoreEnteredPanelCount) && panelCount.equals(scoreEnteredPanelCount)) {
			List<Object> objList = new ArrayList<Object>();
			EmpApplnEntriesDBO empApplnEntriesDBO = interviewScoreEntryransaction.getApplicantDetails(dto.empApplnEntryId);
			ErpWorkFlowProcessDBO erpWorkFlowProcessDBO = interviewScoreEntryransaction.getTemplate("EMP_STAGE1_INTERVIEW_COMPLETED");
			empApplnEntriesDBO.applicantCurrentProcessStatus = erpWorkFlowProcessDBO;
			empApplnEntriesDBO.applicantStatusTime = LocalDateTime.now();
			empApplnEntriesDBO.applicationCurrentProcessStatus = erpWorkFlowProcessDBO;
			empApplnEntriesDBO.applicationStatusTime = LocalDateTime.now();
			objList.add(empApplnEntriesDBO);
//			isTrue = interviewScoreEntryransaction.saveOrUpdate(empApplnEntriesDBO);
//			if(isTrue) {
				Integer id = empApplnEntriesDBO.getId();
				ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
				erpWorkFlowProcessStatusLogDBO.entryId = id;
				erpWorkFlowProcessStatusLogDBO.erpWorkFlowProcessDBO = new ErpWorkFlowProcessDBO();
				erpWorkFlowProcessStatusLogDBO.erpWorkFlowProcessDBO.id = erpWorkFlowProcessDBO.id;
				erpWorkFlowProcessStatusLogDBO.recordStatus = 'A';
				erpWorkFlowProcessStatusLogDBO.createdUsersId = dto.userId;
//				interviewScoreEntryransaction.saveOrUpdate(erpWorkFlowProcessStatusLogDBO);
				objList.add(erpWorkFlowProcessStatusLogDBO);
				interviewScoreEntryransaction.saveOrUpdateList(objList);
//			}
		}
	}

	public Flux<SelectDTO> getPaneListForAppln(Integer applicationNumber) {
		return interviewScoreEntryransaction.getPaneListForAppln(applicationNumber)
				.flatMapMany(Flux::fromIterable);
	}

//	public SelectDTO convertApplnDBOToDTO(EmpApplnInterviewPanelDBO dbo) {
//		SelectDTO dto = new SelectDTO();
//		if(!Utils.isNullOrEmpty(dbo)) {
//			if (dbo.isInternalPanel()) {
//				if(!Utils.isNullOrEmpty(dbo.getErpUsersDBO())) {
//					dto.setValue(String.valueOf(dbo.getErpUsersDBO().getId()));
//				}
//				if(!Utils.isNullOrEmpty(dbo.getErpUsersDBO().getEmpDBO())) {
//					dto.setLabel(dbo.getErpUsersDBO().getEmpDBO().getEmpName());
//				}
//			} else {
//				if(!Utils.isNullOrEmpty(dbo.getEmpInterviewUniversityExternalsDBO())) {
//					dto.setValue(String.valueOf(dbo.getEmpInterviewUniversityExternalsDBO().getId()));
//					if(!Utils.isNullOrEmpty(dbo.getEmpInterviewUniversityExternalsDBO().getPanelName())) {
//						dto.setLabel(dbo.getEmpInterviewUniversityExternalsDBO().getPanelName().concat(" (External)"));
//					}
//				}
//			}
//		}
//		return dto;
//	}

	public Flux<SelectDTO> getPanelListByUserId(Integer applicationNumber, String userId) {
		return Flux.from(interviewScoreEntryransaction.getPanelListByUserId(applicationNumber, userId));
	}

//	public SelectDTO convertApplDBOToDTO(EmpApplnInterviewPanelDBO dbo) {
//		SelectDTO dto = new SelectDTO();
//		if(!Utils.isNullOrEmpty(dbo)) {
//			if(!Utils.isNullOrEmpty(dbo.getErpUsersDBO())) {
//				dto.setValue(String.valueOf(dbo.getErpUsersDBO().getId()));
//			}
//			if (!Utils.isNullOrEmpty(dbo.getErpUsersDBO().getEmpDBO())) {
//				dto.setLabel(dbo.getErpUsersDBO().getEmpDBO().getEmpName());
//			}
//		}
//		return dto;
//	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> checkAdminUser() {
		ApiResult result = new ApiResult();
		result.setSuccess(true);
		return Mono.just(result);
	}
}
