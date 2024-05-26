package com.christ.erp.services.handlers.employee.recruitment;

import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.RedisAwsConfig;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpEmailsDBO;
import com.christ.erp.services.dbobjects.common.ErpNotificationsDBO;
import com.christ.erp.services.dbobjects.common.ErpSmsDBO;
import com.christ.erp.services.dbobjects.common.ErpUsersDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessNotificationsDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.common.UrlAccessLinkDBO;
import com.christ.erp.services.dbobjects.common.UrlFolderListDBO;
import com.christ.erp.services.dbobjects.employee.EmpPositionRoleDBO;
import com.christ.erp.services.dbobjects.employee.EmpPositionRoleSubDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpJobDetailsDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnDignitariesFeedbackDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnWorkExperienceDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleDetailsComponentsDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleDetailsDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.attendance.LetterTemplatesDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnWorkExperienceDTO;
import com.christ.erp.services.dto.employee.recruitment.FinalInterviewCommentsDTO;
import com.christ.erp.services.handlers.aws.AWSS3FileStorageServiceHandler;
import com.christ.erp.services.handlers.common.CommonApiHandler;
import com.christ.erp.services.helpers.employee.recruitment.FinalInterviewCommentsHelper;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.employee.recruitment.FinalInterviewCommentsTransaction;

import reactor.core.publisher.Mono;


@Service
public class FinalInterviewCommentsHandler {

	@Autowired
	FinalInterviewCommentsTransaction finalInterviewCommentsTransaction1;

	@Autowired
	FinalInterviewCommentsHelper finalInterviewCommentsHelper1;

	@Autowired
	CommonApiTransaction commonApiTransaction1;

	@Autowired
	CommonApiHandler commonApiHandler;

	@Autowired
	AWSS3FileStorageServiceHandler aWSS3FileStorageServiceHandler;

	@Autowired
	RedisAwsConfig redisAwsConfig;

	@Autowired
	EmployeeApplicationHandler employeeApplicationHandler;

	private static volatile FinalInterviewCommentsHandler finalInterviewCommentsHandler = null;
	FinalInterviewCommentsTransaction finalInterviewCommentsTransaction = FinalInterviewCommentsTransaction.getInstance();
	FinalInterviewCommentsHelper finalInterviewCommentsHelper = FinalInterviewCommentsHelper.getInstance();
	CommonApiTransaction commonApiTransaction = CommonApiTransaction.getInstance();

	public static FinalInterviewCommentsHandler getInstance() {
		if(finalInterviewCommentsHandler==null) {
			finalInterviewCommentsHandler = new FinalInterviewCommentsHandler();
		}
		return finalInterviewCommentsHandler;
	}

	public FinalInterviewCommentsDTO editFinalInterviewComments(Integer applicationNumber) throws Exception {
		FinalInterviewCommentsDTO finalInterviewCommentsDTO = new FinalInterviewCommentsDTO();
		Tuple applnEntrie = finalInterviewCommentsTransaction1.getFinalInterviewEmpApplnEntries(applicationNumber);
		if(!Utils.isNullOrEmpty(applnEntrie)) {
			finalInterviewCommentsHelper.convertEmpApplnEntrieToDTO(applnEntrie, finalInterviewCommentsDTO);
			List<Tuple> applnDignitariesList = finalInterviewCommentsTransaction1.getFinalInterviewApplnDignitariesFeedback(Integer.parseInt(String.valueOf(applnEntrie.get("applnId"))));
			if(!Utils.isNullOrEmpty(applnDignitariesList)) {
				if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO.campus)) {
					EmpPositionRoleDBO empPositionRoleDBO = finalInterviewCommentsTransaction1.getEmpPositionRoleSub(finalInterviewCommentsDTO.campus.id);
					if(!Utils.isNullOrEmpty(empPositionRoleDBO)) {
						List<EmpPositionRoleSubDBO> empPositionRoleSubDBOs = finalInterviewCommentsTransaction1.getEmpPositionRoleSubById(empPositionRoleDBO.id);
						if(!Utils.isNullOrEmpty(empPositionRoleSubDBOs)) {
							finalInterviewCommentsHelper.convertEmpApplnDignitariesFeedbackToDTO(applnDignitariesList,finalInterviewCommentsDTO,empPositionRoleSubDBOs, empPositionRoleDBO);
						}
					}
				}
			}
			else if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO.campus)) {
				EmpPositionRoleDBO empPositionRoleDBO = finalInterviewCommentsTransaction1.getEmpPositionRoleSub(finalInterviewCommentsDTO.campus.id);
				if(!Utils.isNullOrEmpty(empPositionRoleDBO)) {
					List<EmpPositionRoleSubDBO> empPositionRoleSubDBOs = finalInterviewCommentsTransaction1.getEmpPositionRoleSubById(empPositionRoleDBO.id);
					if(!Utils.isNullOrEmpty(empPositionRoleSubDBOs)) {
						finalInterviewCommentsHelper.convertEmpPositionRoleSubDBOToDTO(empPositionRoleSubDBOs, empPositionRoleDBO, finalInterviewCommentsDTO);
					}
				}
			}
			Tuple jobDetails = finalInterviewCommentsTransaction1.getFinalInterviewEmpJobDetails(Integer.parseInt(String.valueOf(applnEntrie.get("applnId"))));
			if(!Utils.isNullOrEmpty(jobDetails))
				finalInterviewCommentsHelper.convertEmpJobDetailsToDTO(jobDetails,finalInterviewCommentsDTO);
			Tuple empPayScaleDetail = finalInterviewCommentsTransaction1.getFinalInterviewEmpPayScaleDetails(Integer.parseInt(String.valueOf(applnEntrie.get("applnId"))));
			if(!Utils.isNullOrEmpty(empPayScaleDetail)) {
				if(!Utils.isNullOrEmpty(empPayScaleDetail.get("payScaleType"))) {
					finalInterviewCommentsDTO.selectedPayScaleType = String.valueOf(empPayScaleDetail.get("payScaleType"));
					if(!Utils.isNullOrEmpty(empPayScaleDetail.get("grossPay"))) {
						finalInterviewCommentsDTO.grossPay = empPayScaleDetail.get("grossPay").toString();
					}
					if(finalInterviewCommentsDTO.selectedPayScaleType.equalsIgnoreCase("SCALE PAY") || finalInterviewCommentsDTO.selectedPayScaleType.equalsIgnoreCase("CONSOLIDATED")) {
						if(!Utils.isNullOrEmpty(empPayScaleDetail.get("empPayScaleMatrixDetailId"))) {
							Tuple empPayScaleMatrixDetail = finalInterviewCommentsTransaction1.getFinalInterviewEmpPayScaleMatrixDetail(Integer.parseInt(empPayScaleDetail.get("empPayScaleMatrixDetailId").toString()));
							if(!Utils.isNullOrEmpty(empPayScaleMatrixDetail)) {
								if(finalInterviewCommentsDTO.selectedPayScaleType.equalsIgnoreCase("SCALE PAY"))
									finalInterviewCommentsHelper.convertEmpPayScaleMatrixDetailToDTO(empPayScaleMatrixDetail, finalInterviewCommentsDTO);
								else if(finalInterviewCommentsDTO.selectedPayScaleType.equalsIgnoreCase("CONSOLIDATED") && !Utils.isNullOrEmpty(empPayScaleDetail.get("wageRatePerType"))) {
									finalInterviewCommentsDTO.consolidatedAmount = String.valueOf(empPayScaleDetail.get("wageRatePerType")).split("\\.")[0];
									finalInterviewCommentsDTO.cellValue = new ExModelBaseDTO();
									finalInterviewCommentsDTO.cellValue.id = String.valueOf(empPayScaleMatrixDetail.get("cellId"));
									finalInterviewCommentsDTO.cellValue.text = String.valueOf(empPayScaleMatrixDetail.get("cellValue"));
								}
							}
						}
					}else if(finalInterviewCommentsDTO.selectedPayScaleType.equalsIgnoreCase("PER HOUR") && !Utils.isNullOrEmpty(empPayScaleDetail.get("wageRatePerType")))
					{
						finalInterviewCommentsDTO.perHourAmount = empPayScaleDetail.get("wageRatePerType").toString().split("\\.")[0];;
					}
					else if(finalInterviewCommentsDTO.selectedPayScaleType.equalsIgnoreCase("PER COURSE") && !Utils.isNullOrEmpty(empPayScaleDetail.get("wageRatePerType"))) {
						finalInterviewCommentsDTO.perCourseAmount = empPayScaleDetail.get("wageRatePerType").toString().split("\\.")[0];;
					}
					else if(finalInterviewCommentsDTO.selectedPayScaleType.equalsIgnoreCase("DAILY") && !Utils.isNullOrEmpty(empPayScaleDetail.get("wageRatePerType"))) {
						finalInterviewCommentsDTO.dailyAmount = empPayScaleDetail.get("wageRatePerType").toString().split("\\.")[0];;
						List<Tuple> dlyWgDataList = finalInterviewCommentsTransaction1.getEmployeeDlyWageDetails(finalInterviewCommentsDTO.category.id,finalInterviewCommentsDTO.jobCategory.id);
						finalInterviewCommentsHelper.convertEmpDailyWageSlabDetailsDBOToDTO(finalInterviewCommentsDTO.dailyAmount, dlyWgDataList, finalInterviewCommentsDTO);
					}
					if(finalInterviewCommentsDTO.selectedPayScaleType.equalsIgnoreCase("SCALE PAY") || finalInterviewCommentsDTO.selectedPayScaleType.equalsIgnoreCase("CONSOLIDATED") || finalInterviewCommentsDTO.selectedPayScaleType.equalsIgnoreCase("DAILY")) {
						List<Tuple> empPayScaleDetailsComponentsList = finalInterviewCommentsTransaction1.getFinalInterviewPayScaleDetailComponent(Integer.parseInt(String.valueOf(empPayScaleDetail.get("empPayScaleDetailsId"))));
						if(!Utils.isNullOrEmpty(empPayScaleDetailsComponentsList)) {
							finalInterviewCommentsHelper.convertEmpPayScaleDetailsComponentsToDTO(empPayScaleDetailsComponentsList, finalInterviewCommentsDTO);
						}
					}
				}
			}
		}
		return finalInterviewCommentsDTO;
	}

	public FinalInterviewCommentsDTO getFinalInterviewPrintData(Integer applicationNumber) throws Exception {
		FinalInterviewCommentsDTO finalInterviewCommentsDTO = new FinalInterviewCommentsDTO();
		Tuple applnEntrie = finalInterviewCommentsTransaction1.printEmpApplnEntriesDetails(applicationNumber);
		String hodCommentsStage1 = finalInterviewCommentsTransaction1.getHODComments(applicationNumber);
		if(!Utils.isNullOrEmpty(hodCommentsStage1)) {
			finalInterviewCommentsDTO.setStage1HodComments(hodCommentsStage1);
		}
		if(!Utils.isNullOrEmpty(applnEntrie)) {
			finalInterviewCommentsHelper.convertEmpApplnEntrieToPrintDTO(applnEntrie, finalInterviewCommentsDTO);
		}
		List<Tuple> qualificationLevelList = finalInterviewCommentsTransaction1.printQualificationLevelsDetails(applicationNumber);
//		applnDignitariesFeedbackDBOs = finalInterviewCommentsTransaction1.getApplnDignitariesFeedbackByApplnId(applnEntriesDBO.id);
		if(!Utils.isNullOrEmpty(qualificationLevelList)) {
			finalInterviewCommentsHelper.convertQualificationLevelDTO(qualificationLevelList,finalInterviewCommentsDTO);
		}
		Tuple salaryDetails = finalInterviewCommentsTransaction1.printSalaryDetail(applicationNumber);
		Tuple totalExperience = finalInterviewCommentsTransaction1.printTotalExperience(applicationNumber);
		if(!Utils.isNullOrEmpty(salaryDetails)) {
			finalInterviewCommentsHelper.convertSalaryDetailsDTO(salaryDetails,totalExperience,finalInterviewCommentsDTO);
		}else {
			Tuple salaryDetailsByPayScaleType = finalInterviewCommentsTransaction1.printSalaryDetailsByPayScaleType(applicationNumber);
			finalInterviewCommentsHelper.convertSalaryDetailsByPayScaleTypeDTO(salaryDetailsByPayScaleType, finalInterviewCommentsDTO);
		}
		List<Tuple> interviewScoreDetails = finalInterviewCommentsTransaction1.printInterviewScoreDetails(applicationNumber);
		if(!Utils.isNullOrEmpty(interviewScoreDetails)) {
			finalInterviewCommentsHelper.convertInterviewScoreDetailsToDTO(interviewScoreDetails, finalInterviewCommentsDTO);
		}
		return finalInterviewCommentsDTO;
	}

	public List<LetterTemplatesDTO> getLetterTemplate(String category) throws SQLException {
		List<LetterTemplatesDTO> letterTemplatesDTOs = new ArrayList<LetterTemplatesDTO>();
		List<Tuple> templates = finalInterviewCommentsTransaction1.getLetterTemplate(Integer.parseInt(category));
		if(!Utils.isNullOrEmpty(templates)) {
			if(templates != null && templates.size() > 0) {
				for(Tuple mapping : templates) {
					if(!Utils.isNullOrEmpty(mapping.get("ID"))) {
						LetterTemplatesDTO dto = new LetterTemplatesDTO();
						dto.isEdit=true;
						dto.id = mapping.get("ID").toString();
						if(!Utils.isNullOrEmpty(mapping.get("templateGroupCode"))) {
							dto.groupCode = mapping.get("templateGroupCode").toString();
						}
						if(!Utils.isNullOrEmpty(!Utils.isNullOrEmpty(mapping.get("Text")))) {
							dto.groupName = mapping.get("Text").toString();
						}
						if (!Utils.isNullOrEmpty(mapping.get("templateContent"))) {
							Object content = mapping.get("templateContent");
							if (content instanceof String) {
								dto.ckTemplate = (String) content;
							} else if (content instanceof Blob) {
								Blob blob = (Blob) content;
								byte[] bytes = blob.getBytes(1, (int) blob.length());
								dto.ckTemplate = new String(bytes);
							}
						}
						letterTemplatesDTOs.add(dto);
					}
				}
			}
		}
		return letterTemplatesDTOs;
	}

	public Mono<FinalInterviewCommentsDTO> getWorkExperienceView(String applicationId) {
		List<EmpApplnWorkExperienceDBO> list = finalInterviewCommentsTransaction1.getWorkExperienceView(applicationId);
		return convertWorkExperienceDboToDto(list);
	}

	private Mono<FinalInterviewCommentsDTO> convertWorkExperienceDboToDto(List<EmpApplnWorkExperienceDBO> list) {
		FinalInterviewCommentsDTO finalInterviewCommentsDTO = new FinalInterviewCommentsDTO();
		AtomicInteger fullTimeYears = new AtomicInteger(0);
		AtomicInteger fullTimeMonths = new AtomicInteger(0);
		AtomicInteger partTimeYears = new AtomicInteger(0);
		AtomicInteger partTimeMonths = new AtomicInteger(0);
		if(!Utils.isNullOrEmpty(list)) {
			List<EmpApplnWorkExperienceDTO> empApplnWorkExperienceDTOList = new ArrayList<EmpApplnWorkExperienceDTO>();
			list.forEach( applnWorkExperienceDBO ->  {
				EmpApplnWorkExperienceDTO empApplnWorkExperienceDTO = new EmpApplnWorkExperienceDTO();
				if(!Utils.isNullOrEmpty(applnWorkExperienceDBO.isPartTime) && applnWorkExperienceDBO.isPartTime) {
					empApplnWorkExperienceDTO.employmentType = "parttime";
					if(!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceYears)) {
						int i = partTimeYears.addAndGet(Integer.parseInt(applnWorkExperienceDBO.workExperienceYears.toString())*12);
						partTimeYears.set(i);
					}
					if(!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceMonth)) {
						int i  = partTimeMonths.addAndGet(Integer.parseInt(applnWorkExperienceDBO.workExperienceMonth.toString()));
						partTimeMonths.set(i);
					}
				} else {
					empApplnWorkExperienceDTO.employmentType = "fulltime";
					if(!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceYears)) {
						int i = fullTimeYears.addAndGet(Integer.parseInt(applnWorkExperienceDBO.workExperienceYears.toString())*12);
						fullTimeYears.set(i);
					}
					if(!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceMonth)) {
						int i = fullTimeMonths.addAndGet(Integer.parseInt(applnWorkExperienceDBO.workExperienceMonth.toString()));
						fullTimeMonths.set(i);
					}
				}
				empApplnWorkExperienceDTO.functionalArea = new LookupItemDTO();
				if(!Utils.isNullOrEmpty(applnWorkExperienceDBO.empApplnSubjectCategoryDBO) && !Utils.isNullOrEmpty(applnWorkExperienceDBO.empApplnSubjectCategoryDBO.id)) {
					empApplnWorkExperienceDTO.functionalArea.value = String.valueOf(applnWorkExperienceDBO.empApplnSubjectCategoryDBO.id);
					empApplnWorkExperienceDTO.functionalArea.label = applnWorkExperienceDBO.empApplnSubjectCategoryDBO.subjectCategory;
				} else if(!Utils.isNullOrEmpty(applnWorkExperienceDBO.getFunctionalAreaOthers())) {
					empApplnWorkExperienceDTO.setFunctionalAreaOthers(applnWorkExperienceDBO.getFunctionalAreaOthers());
				}
				if(!Utils.isNullOrEmpty(applnWorkExperienceDBO.empDesignation)) {
					empApplnWorkExperienceDTO.designation = applnWorkExperienceDBO.empDesignation;
				}
//				if(!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceFromDate)) {
//					empApplnWorkExperienceDTO.fromDate = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(applnWorkExperienceDBO.workExperienceFromDate.toString()));
//				}
//				if(!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceToDate)) {
//					empApplnWorkExperienceDTO.toDate = Utils.convertLocalDateToStringDate6(Utils.convertStringDateToLocalDate(applnWorkExperienceDBO.workExperienceToDate.toString()));
//				}
				if(!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceFromDate)) {
					empApplnWorkExperienceDTO.setFromDate(applnWorkExperienceDBO.workExperienceFromDate);
				}
				if(!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceToDate)) {
					empApplnWorkExperienceDTO.setToDate(applnWorkExperienceDBO.workExperienceToDate);
				}
				if(!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceYears)) {
					empApplnWorkExperienceDTO.years = String.valueOf(applnWorkExperienceDBO.workExperienceYears);
				}
				if(!Utils.isNullOrEmpty(applnWorkExperienceDBO.workExperienceMonth)) {
					empApplnWorkExperienceDTO.months = String.valueOf(applnWorkExperienceDBO.workExperienceMonth);
				}
				if(!Utils.isNullOrEmpty(applnWorkExperienceDBO.institution)) {
					empApplnWorkExperienceDTO.institution = empApplnWorkExperienceDTO.institution;
				}
				empApplnWorkExperienceDTOList.add(empApplnWorkExperienceDTO);
			});
			finalInterviewCommentsDTO.setEmpApplnWorkExperienceDTOList(empApplnWorkExperienceDTOList);
		}
		return Mono.just(finalInterviewCommentsDTO);
	}

	public Mono<ApiResult<FinalInterviewCommentsDTO>> saveOrUpdateStage2Comments(FinalInterviewCommentsDTO finalInterviewCommentsDTO, String userId) {
		ApiResult<FinalInterviewCommentsDTO> apiResult = new ApiResult<FinalInterviewCommentsDTO>();
		apiResult.setSuccess(false);
		if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO) && !Utils.isNullOrEmpty(finalInterviewCommentsDTO.applicationNumber)) {
			List<Object> objects = new ArrayList<Object>();
			EmpApplnEntriesDBO applnEntriesDBO = finalInterviewCommentsTransaction1.getEmpApplnEntries(Integer.parseInt(finalInterviewCommentsDTO.applicationNumber));
			if(!Utils.isNullOrEmpty(applnEntriesDBO)) {
				finalInterviewCommentsDTO.id = String.valueOf(applnEntriesDBO.id);
				if(finalInterviewCommentsDTO.getSelectionStatus().equalsIgnoreCase("Yes")) {
					List<EmpApplnDignitariesFeedbackDBO> applnDignitariesFeedbackDBOs = null;
					String forwardedWorkFlow = "EMP_STAGE3_PO_SCHEDULED, EMP_STAGE3_PO_RESCHEDULED, EMP_STAGE3_SCHEDULE_APPLICANT_ACCEPTED, EMP_STAGE3_SCHEDULE_APPLICANT_DECLINED, EMP_STAGE3_SELECTED,"
							+ " EMP_STAGE3_REJECTED, EMP_STAGE3_ONHOLD, EMP_OFFER_LETTER_REGENERATE, EMP_OFFER_ACCEPTED, EMP_REGRET_LETTER_GENERATED,EMP_OFFER_LETTER_GENERATED,EMP_OFFER_DECLINED";
					if(!forwardedWorkFlow.contains(applnEntriesDBO.getApplicantCurrentProcessStatus().getProcessCode())) {
						Integer workFlowProcessId = commonApiTransaction1.getWorkFlowProcessId("EMP_STAGE2_SELECTED");
						applnEntriesDBO.getApplicantCurrentProcessStatus().setId(workFlowProcessId);
						applnEntriesDBO.setApplicantStatusTime(LocalDateTime.now());
						applnEntriesDBO.getApplicationCurrentProcessStatus().setId(workFlowProcessId);
						applnEntriesDBO.setApplicationStatusTime(LocalDateTime.now());
					}
					if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO.processAndPosition)) {
						applnDignitariesFeedbackDBOs = new ArrayList<EmpApplnDignitariesFeedbackDBO>();
						applnDignitariesFeedbackDBOs = finalInterviewCommentsTransaction1.getApplnDignitariesFeedbackByApplnId(applnEntriesDBO.id);
					}
					EmpJobDetailsDBO jobDetailsDBO = null;
					jobDetailsDBO = finalInterviewCommentsTransaction1.getEmpJobDetails(applnEntriesDBO.getId());
					applnEntriesDBO = finalInterviewCommentsHelper1.convertEmpApplnEntriesDTOToDBO(applnEntriesDBO, finalInterviewCommentsDTO, applnDignitariesFeedbackDBOs, userId,objects);
					jobDetailsDBO = finalInterviewCommentsHelper1.convertEmpJobDetailsDTOToDBO(applnEntriesDBO, jobDetailsDBO, finalInterviewCommentsDTO, userId);
					applnEntriesDBO.empJobDetailsDBO = jobDetailsDBO;
					EmpPayScaleDetailsDBO empPayScaleDetailsDBO = null;
					empPayScaleDetailsDBO = finalInterviewCommentsTransaction1.getEmpPayScaleDetails(applnEntriesDBO.id); /// need to change here
					List<EmpPayScaleDetailsComponentsDBO> empPayScaleDetailsComponentsDBOsList = !Utils.isNullOrEmpty(empPayScaleDetailsDBO) ? finalInterviewCommentsTransaction1.getPayScaleDetailComponent(empPayScaleDetailsDBO.id) : new ArrayList<EmpPayScaleDetailsComponentsDBO>();
					empPayScaleDetailsDBO = finalInterviewCommentsHelper1.convertEmpPayScaleDetailsDTOToDBO(applnEntriesDBO, empPayScaleDetailsDBO, empPayScaleDetailsComponentsDBOsList, finalInterviewCommentsDTO, userId);
					objects.add(empPayScaleDetailsDBO);
				} else if(finalInterviewCommentsDTO.getSelectionStatus().equalsIgnoreCase("No")) {
					Integer workFlowProcessId = commonApiTransaction1.getWorkFlowProcessId("EMP_STAGE2_REJECTED");
					applnEntriesDBO.getApplicantCurrentProcessStatus().setId(workFlowProcessId);
					applnEntriesDBO.setApplicantStatusTime(LocalDateTime.now());
					applnEntriesDBO.getApplicationCurrentProcessStatus().setId(workFlowProcessId);
					applnEntriesDBO.setApplicationStatusTime(LocalDateTime.now());
				} else if(finalInterviewCommentsDTO.getSelectionStatus().equalsIgnoreCase("Onhold")) {
					Integer workFlowProcessId = commonApiTransaction1.getWorkFlowProcessId("EMP_STAGE2_ONHOLD");
					applnEntriesDBO.getApplicantCurrentProcessStatus().setId(workFlowProcessId);
					applnEntriesDBO.setApplicationStatusTime(LocalDateTime.now());
				}
				if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO.getStage2OnholdRejectedComments())) {
					applnEntriesDBO.setStage2OnholdRejectedComments(finalInterviewCommentsDTO.getStage2OnholdRejectedComments());
				}
				ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
				erpWorkFlowProcessStatusLogDBO.setEntryId(applnEntriesDBO.getId());
				erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(new ErpWorkFlowProcessDBO());
				erpWorkFlowProcessStatusLogDBO.getErpWorkFlowProcessDBO().setId(applnEntriesDBO.getApplicantCurrentProcessStatus().getId());
				erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
				erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
				objects.add(erpWorkFlowProcessStatusLogDBO);
				objects.add(applnEntriesDBO);
				apiResult.setSuccess(finalInterviewCommentsTransaction1.saveOrUpdate(objects));
				apiResult.setDto(finalInterviewCommentsDTO);
			}
		}
		return Mono.just(apiResult);
	}

	public Mono<ApiResult<FinalInterviewCommentsDTO>> getStage2InterviewCommentsData(String applicationNumber) {
		FinalInterviewCommentsDTO finalInterviewCommentsDTO = new FinalInterviewCommentsDTO();
		ApiResult<FinalInterviewCommentsDTO> apiResult = new ApiResult<FinalInterviewCommentsDTO>();
		Tuple applnEntrie = finalInterviewCommentsTransaction1.getFinalInterviewEmpApplnEntries(Integer.valueOf(applicationNumber));
		if(!Utils.isNullOrEmpty(applnEntrie)) {
			finalInterviewCommentsHelper.convertEmpApplnEntrieToDTO(applnEntrie, finalInterviewCommentsDTO);
			List<Tuple> applnDignitariesList = finalInterviewCommentsTransaction1.getFinalInterviewApplnDignitariesFeedback(Integer.parseInt(String.valueOf(applnEntrie.get("applnId"))));
			if(!Utils.isNullOrEmpty(applnDignitariesList)) {
				if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO.campus)) {
					EmpPositionRoleDBO empPositionRoleDBO = finalInterviewCommentsTransaction1.getEmpPositionRoleSub(finalInterviewCommentsDTO.campus.id);
					if(!Utils.isNullOrEmpty(empPositionRoleDBO)) {
						List<EmpPositionRoleSubDBO> empPositionRoleSubDBOs = finalInterviewCommentsTransaction1.getEmpPositionRoleSubById(empPositionRoleDBO.id);
						if(!Utils.isNullOrEmpty(empPositionRoleSubDBOs)) {
							finalInterviewCommentsHelper.convertEmpApplnDignitariesFeedbackToDTO(applnDignitariesList,finalInterviewCommentsDTO,empPositionRoleSubDBOs, empPositionRoleDBO);
						}
					}
				}
			}
			else if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO.campus)) {
				EmpPositionRoleDBO empPositionRoleDBO = finalInterviewCommentsTransaction1.getEmpPositionRoleSub(finalInterviewCommentsDTO.campus.id);
				if(!Utils.isNullOrEmpty(empPositionRoleDBO)) {
					List<EmpPositionRoleSubDBO> empPositionRoleSubDBOs = finalInterviewCommentsTransaction1.getEmpPositionRoleSubById(empPositionRoleDBO.id);
					if(!Utils.isNullOrEmpty(empPositionRoleSubDBOs)) {
						finalInterviewCommentsHelper.convertEmpPositionRoleSubDBOToDTO(empPositionRoleSubDBOs, empPositionRoleDBO, finalInterviewCommentsDTO);
					}
				}
			}
			Tuple jobDetails = finalInterviewCommentsTransaction1.getFinalInterviewEmpJobDetails(Integer.parseInt(String.valueOf(applnEntrie.get("applnId"))));
			if(!Utils.isNullOrEmpty(jobDetails))
				finalInterviewCommentsHelper.convertEmpJobDetailsToDTO(jobDetails,finalInterviewCommentsDTO);
			Tuple empPayScaleDetail = finalInterviewCommentsTransaction1.getFinalInterviewEmpPayScaleDetails(Integer.parseInt(String.valueOf(applnEntrie.get("applnId"))));
			if(!Utils.isNullOrEmpty(empPayScaleDetail)) {
				if(!Utils.isNullOrEmpty(empPayScaleDetail.get("payScaleType"))) {
					finalInterviewCommentsDTO.selectedPayScaleType = String.valueOf(empPayScaleDetail.get("payScaleType"));
					if(!Utils.isNullOrEmpty(empPayScaleDetail.get("grossPay"))) {
						finalInterviewCommentsDTO.grossPay = empPayScaleDetail.get("grossPay").toString();
					}
					if(finalInterviewCommentsDTO.selectedPayScaleType.equalsIgnoreCase("SCALE PAY")) {
						if(!Utils.isNullOrEmpty(empPayScaleDetail.get("empPayScaleMatrixDetailId"))) {
							Tuple empPayScaleMatrixDetail = finalInterviewCommentsTransaction1.getFinalInterviewEmpPayScaleMatrixDetail(Integer.parseInt(empPayScaleDetail.get("empPayScaleMatrixDetailId").toString()));
							if(!Utils.isNullOrEmpty(empPayScaleMatrixDetail)) {
								if(finalInterviewCommentsDTO.selectedPayScaleType.equalsIgnoreCase("SCALE PAY"))
									finalInterviewCommentsHelper.convertEmpPayScaleMatrixDetailToDTO(empPayScaleMatrixDetail, finalInterviewCommentsDTO);
//								else if(finalInterviewCommentsDTO.selectedPayScaleType.equalsIgnoreCase("CONSOLIDATED") && !Utils.isNullOrEmpty(empPayScaleDetail.get("wageRatePerType"))) {
//									finalInterviewCommentsDTO.consolidatedAmount = String.valueOf(empPayScaleDetail.get("wageRatePerType")).split("\\.")[0];
//									finalInterviewCommentsDTO.cellValue = new ExModelBaseDTO();
//									finalInterviewCommentsDTO.cellValue.id = String.valueOf(empPayScaleMatrixDetail.get("cellId"));
//									finalInterviewCommentsDTO.cellValue.text = String.valueOf(empPayScaleMatrixDetail.get("cellValue"));
//								}
							}
						}
					}else if(finalInterviewCommentsDTO.selectedPayScaleType.equalsIgnoreCase("PER HOUR") && !Utils.isNullOrEmpty(empPayScaleDetail.get("wageRatePerType")))
					{
						finalInterviewCommentsDTO.perHourAmount = empPayScaleDetail.get("wageRatePerType").toString().split("\\.")[0];;
					}
					else if(finalInterviewCommentsDTO.selectedPayScaleType.equalsIgnoreCase("PER COURSE") && !Utils.isNullOrEmpty(empPayScaleDetail.get("wageRatePerType"))) {
						finalInterviewCommentsDTO.perCourseAmount = empPayScaleDetail.get("wageRatePerType").toString().split("\\.")[0];;
					}
					else if(finalInterviewCommentsDTO.selectedPayScaleType.equalsIgnoreCase("DAILY") && !Utils.isNullOrEmpty(empPayScaleDetail.get("wageRatePerType"))) {
						finalInterviewCommentsDTO.dailyAmount = empPayScaleDetail.get("wageRatePerType").toString().split("\\.")[0];;
						List<Tuple> dlyWgDataList = finalInterviewCommentsTransaction1.getEmployeeDlyWageDetails(finalInterviewCommentsDTO.category.id,finalInterviewCommentsDTO.jobCategory.id);
						finalInterviewCommentsHelper.convertEmpDailyWageSlabDetailsDBOToDTO(finalInterviewCommentsDTO.dailyAmount, dlyWgDataList, finalInterviewCommentsDTO);
					}
					else if(finalInterviewCommentsDTO.selectedPayScaleType.equalsIgnoreCase("CONSOLIDATED") && !Utils.isNullOrEmpty(empPayScaleDetail.get("wageRatePerType"))) {
						finalInterviewCommentsDTO.setConsolidatedAmount(empPayScaleDetail.get("wageRatePerType").toString().split("\\.")[0]);;
					}
					if(finalInterviewCommentsDTO.selectedPayScaleType.equalsIgnoreCase("SCALE PAY") || finalInterviewCommentsDTO.selectedPayScaleType.equalsIgnoreCase("DAILY")) {
						List<Tuple> empPayScaleDetailsComponentsList = finalInterviewCommentsTransaction1.getFinalInterviewPayScaleDetailComponent(Integer.parseInt(String.valueOf(empPayScaleDetail.get("empPayScaleDetailsId"))));
						if(!Utils.isNullOrEmpty(empPayScaleDetailsComponentsList)) {
							finalInterviewCommentsHelper.convertEmpPayScaleDetailsComponentsToDTO(empPayScaleDetailsComponentsList, finalInterviewCommentsDTO);
						}
					}
				}
			}
			apiResult.setSuccess(true);
			apiResult.setDto(finalInterviewCommentsDTO);
		} else {
			apiResult.setSuccess(false);
			apiResult.setFailureMessage("This application forwarded from Stage2");
		}
		return Mono.just(apiResult);
	}

	public Mono<ApiResult<FinalInterviewCommentsDTO>> getStage3InterviewCommentsData(String applicationNumber) {
		ApiResult<FinalInterviewCommentsDTO> apiResult = new ApiResult<FinalInterviewCommentsDTO>();
		EmpApplnEntriesDBO applnEntriesDBO = finalInterviewCommentsTransaction1.getEmpApplnEntries(Integer.valueOf(applicationNumber));
		if(!Utils.isNullOrEmpty(applnEntriesDBO)) {
			FinalInterviewCommentsDTO finalInterviewCommentsDTO = new FinalInterviewCommentsDTO();
			if(!Utils.isNullOrEmpty(applnEntriesDBO.getApplicantName())) {
				finalInterviewCommentsDTO.setApplicantName(applnEntriesDBO.getApplicantName());
			}
			if(!Utils.isNullOrEmpty(applnEntriesDBO.getApplicationNo())) {
				finalInterviewCommentsDTO.setApplicationNumber(String.valueOf(applnEntriesDBO.getApplicationNo()));
			}
			if(!Utils.isNullOrEmpty(applnEntriesDBO.getApplicantCurrentProcessStatus())) {
				String eligible ="EMP_STAGE3_SELECTED ,EMP_STAGE3_REJECTED ,EMP_STAGE3_ONHOLD ,EMP_OFFER_LETTER_GENERATED ,EMP_OFFER_LETTER_REGENERATE ,EMP_OFFER_DECLINED ,EMP_REGRET_LETTER_GENERATED"
						+ "EMP_STAGE2_SELECTED, EMP_STAGE3_PO_SCHEDULED, EMP_STAGE3_PO_RESCHEDULED, EMP_STAGE3_SCHEDULE_APPLICANT_ACCEPTED,EMP_OFFER_ACCEPTED,EMP_STAGE3_SCHEDULE_APPLICANT_DECLINED";
				String forwarded = "EMP_CREATED";
				if(eligible.contains(applnEntriesDBO.getApplicantCurrentProcessStatus().getProcessCode())) {
					if(!Utils.isNullOrEmpty(applnEntriesDBO.getErpTemplateForOfferLetterDBO())) {
						finalInterviewCommentsDTO.setOfferLetterTemplate(new SelectDTO());
						finalInterviewCommentsDTO.getOfferLetterTemplate().setLabel(applnEntriesDBO.getErpTemplateForOfferLetterDBO().getTemplateName());
						finalInterviewCommentsDTO.getOfferLetterTemplate().setValue(String.valueOf(applnEntriesDBO.getErpTemplateForOfferLetterDBO().getId()));
					}
					if(!Utils.isNullOrEmpty(applnEntriesDBO.getErpTemplateForRegretLetterDBO())) {
						finalInterviewCommentsDTO.setRegretLetterTemplate(new SelectDTO());
						finalInterviewCommentsDTO.getRegretLetterTemplate().setLabel(applnEntriesDBO.getErpTemplateForRegretLetterDBO().getTemplateName());
						finalInterviewCommentsDTO.getRegretLetterTemplate().setValue(String.valueOf(applnEntriesDBO.getErpTemplateForRegretLetterDBO().getId()));
					}
					if(!Utils.isNullOrEmpty(applnEntriesDBO.getEmpEmployeeCategoryDBO())) {
						finalInterviewCommentsDTO.setCategory(new ExModelBaseDTO());
						finalInterviewCommentsDTO.getCategory().setId(applnEntriesDBO.getEmpEmployeeCategoryDBO().getId().toString());
						finalInterviewCommentsDTO.getCategory().setText(applnEntriesDBO.getEmpEmployeeCategoryDBO().getEmployeeCategoryName());
						finalInterviewCommentsDTO.setIsEmployeeCategoryAcademic(String.valueOf(applnEntriesDBO.getEmpEmployeeCategoryDBO().getIsEmployeeCategoryAcademic()));
					}
					if(!Utils.isNullOrEmpty(applnEntriesDBO.getEmpApplnSubjectCategoryDBO())) {
						finalInterviewCommentsDTO.setSubjectCategory(new ExModelBaseDTO());
						finalInterviewCommentsDTO.getSubjectCategory().setText(applnEntriesDBO.getEmpApplnSubjectCategoryDBO().getSubjectCategory());
						finalInterviewCommentsDTO.setSubjectCategoryName(applnEntriesDBO.getEmpApplnSubjectCategoryDBO().getSubjectCategory());
					}
					if(!Utils.isNullOrEmpty(applnEntriesDBO.getEmpApplnSubjectCategorySpecializationDBO())) {
						finalInterviewCommentsDTO.setSubjectCategorySpecialization(new ExModelBaseDTO());
						finalInterviewCommentsDTO.getSubjectCategorySpecialization().setText(applnEntriesDBO.getEmpApplnSubjectCategorySpecializationDBO().getSubjectCategorySpecializationName());
					}
					if(!Utils.isNullOrEmpty(applnEntriesDBO.getErpCampusDBO())) {
						finalInterviewCommentsDTO.setCampus(new ExModelBaseDTO());
						finalInterviewCommentsDTO.getCampus().setText(applnEntriesDBO.getErpCampusDBO().getCampusName());
					}
					if(!Utils.isNullOrEmpty(applnEntriesDBO.getErpCampusDepartmentMappingDBO())) {
						if(!Utils.isNullOrEmpty(applnEntriesDBO.getErpCampusDepartmentMappingDBO().getErpDepartmentDBO())) {
							finalInterviewCommentsDTO.setCampusDeptDto(new SelectDTO());
							finalInterviewCommentsDTO.getCampusDeptDto().setLabel(applnEntriesDBO.getErpCampusDepartmentMappingDBO().getErpDepartmentDBO().getDepartmentName());
						}
					}
					if(!Utils.isNullOrEmpty(applnEntriesDBO.getEmpJobDetailsDBO())) {
						if(!Utils.isNullOrEmpty(applnEntriesDBO.getEmpJobDetailsDBO().getStage3InterviewedBy())) {
							finalInterviewCommentsDTO.setStage3InterviewedBy(new SelectDTO());
							finalInterviewCommentsDTO.getStage3InterviewedBy().setValue(String.valueOf(applnEntriesDBO.getEmpJobDetailsDBO().getStage3InterviewedBy().getId()));
							//			finalInterviewCommentsDTO.getStage3InterviewedBy().setLabel(applnEntriesDBO.getEmpJobDetailsDBO().getStage3InterviewedBy().getEmpName());			
						}
						finalInterviewCommentsDTO.setStage3Comments(applnEntriesDBO.getEmpJobDetailsDBO().getStage3Comments());
						if(!Utils.isNullOrEmpty(applnEntriesDBO.getEmpJobDetailsDBO().getPreferedJoiningDateTime())) {
							finalInterviewCommentsDTO.setPreferedJoiningDateTime(applnEntriesDBO.getEmpJobDetailsDBO().getPreferedJoiningDateTime());
						}
						if(!Utils.isNullOrEmpty(applnEntriesDBO.getEmpJobDetailsDBO().getIsJoiningDateConfirmed())) {
							finalInterviewCommentsDTO.setIsJoiningDateConfirmed(applnEntriesDBO.getEmpJobDetailsDBO().getIsJoiningDateConfirmed());
						}
						if(!Utils.isNullOrEmpty(applnEntriesDBO.getEmpJobDetailsDBO().getJoiningDateRejectReason())) {
							finalInterviewCommentsDTO.setJoiningDateRejectReason(applnEntriesDBO.getEmpJobDetailsDBO().getJoiningDateRejectReason());
						}
						if(!Utils.isNullOrEmpty(applnEntriesDBO.getEmpJobDetailsDBO().getJoiningDate())) {
							finalInterviewCommentsDTO.setJoiningDateAndTime(applnEntriesDBO.getEmpJobDetailsDBO().getJoiningDate());
						}
					}
					if(!Utils.isNullOrEmpty(applnEntriesDBO.getApplicationCurrentProcessStatus())) {
						if(!Utils.isNullOrEmpty(applnEntriesDBO.getApplicationCurrentProcessStatus().getProcessCode())) {
							finalInterviewCommentsDTO.setIsRegretLetterGenerated(false);
							finalInterviewCommentsDTO.setIsOfferLetterGenerated(false);
							finalInterviewCommentsDTO.setIsOfferLetterRegenerated(false);
							if(applnEntriesDBO.getApplicationCurrentProcessStatus().getProcessCode().equalsIgnoreCase("EMP_STAGE3_SELECTED")) {
								finalInterviewCommentsDTO.setSelectionStatus("Yes");
							}else if("EMP_REGRET_LETTER_GENERATED , EMP_STAGE3_REJECTED".contains(applnEntriesDBO.getApplicationCurrentProcessStatus().getProcessCode())) {
								finalInterviewCommentsDTO.setSelectionStatus("No");
								if(applnEntriesDBO.getApplicationCurrentProcessStatus().getProcessCode().equalsIgnoreCase("EMP_REGRET_LETTER_GENERATED")) {
									finalInterviewCommentsDTO.setIsRegretLetterGenerated(true);
								}
							}else if(applnEntriesDBO.getApplicationCurrentProcessStatus().getProcessCode().equalsIgnoreCase("EMP_STAGE3_ONHOLD")) {
								finalInterviewCommentsDTO.setSelectionStatus("Onhold");
							}else if(applnEntriesDBO.getApplicationCurrentProcessStatus().getProcessCode().equalsIgnoreCase("EMP_OFFER_LETTER_GENERATED")) {
								finalInterviewCommentsDTO.setSelectionStatus("Yes");
								finalInterviewCommentsDTO.setIsOfferLetterGenerated(true);
							}else if(applnEntriesDBO.getApplicationCurrentProcessStatus().getProcessCode().equalsIgnoreCase("EMP_OFFER_LETTER_REGENERATE")) {
								finalInterviewCommentsDTO.setSelectionStatus("Yes");
								finalInterviewCommentsDTO.setIsOfferLetterRegenerated(true);
							}else if(applnEntriesDBO.getApplicationCurrentProcessStatus().getProcessCode().equalsIgnoreCase("EMP_OFFER_DECLINED")
									|| applnEntriesDBO.getApplicationCurrentProcessStatus().getProcessCode().equalsIgnoreCase("EMP_OFFER_ACCEPTED") ) {
								finalInterviewCommentsDTO.setSelectionStatus("Yes");
								if(!Utils.isNullOrEmpty(applnEntriesDBO.getEmpApplnNonAvailabilityDBO())) {
									finalInterviewCommentsDTO.setJobRejectionReason(applnEntriesDBO.getEmpApplnNonAvailabilityDBO().getNonAvailabilityName());
								} else if(!Utils.isNullOrEmpty(applnEntriesDBO.getJobRejectionReason())) {
									finalInterviewCommentsDTO.setJobRejectionReason(applnEntriesDBO.getJobRejectionReason());
								}
								if(!Utils.isNullOrEmpty(applnEntriesDBO.getEmpApplnNonAvailabilityDBO())) {

								}
							}
						}
					}
					if(!Utils.isNullOrEmpty(applnEntriesDBO.getOfferLetterUrlDbo()) && applnEntriesDBO.getOfferLetterUrlDbo().getRecordStatus() == 'A') {
						finalInterviewCommentsDTO.setOfferLetterUrlDto(new FileUploadDownloadDTO());
						finalInterviewCommentsDTO.getOfferLetterUrlDto().setOriginalFileName(applnEntriesDBO.getOfferLetterUrlDbo().getFileNameOriginal());
						finalInterviewCommentsDTO.getOfferLetterUrlDto().setUniqueFileName(applnEntriesDBO.getOfferLetterUrlDbo().getFileNameUnique());
						finalInterviewCommentsDTO.getOfferLetterUrlDto().setActualPath(applnEntriesDBO.getOfferLetterUrlDbo().getFileNameUnique());
						if(!Utils.isNullOrEmpty(applnEntriesDBO.getOfferLetterUrlDbo().getUrlFolderListDBO())) {
							finalInterviewCommentsDTO.getOfferLetterUrlDto().setProcessCode(applnEntriesDBO.getOfferLetterUrlDbo().getUrlFolderListDBO().getUploadProcessCode());
						}}
					if(!Utils.isNullOrEmpty(applnEntriesDBO.getRegretLetterUrlDbo()) && applnEntriesDBO.getRegretLetterUrlDbo().getRecordStatus() == 'A') {
						finalInterviewCommentsDTO.setRegretLetterUrlDto(new FileUploadDownloadDTO());
						finalInterviewCommentsDTO.getRegretLetterUrlDto().setOriginalFileName(applnEntriesDBO.getRegretLetterUrlDbo().getFileNameOriginal());
						finalInterviewCommentsDTO.getRegretLetterUrlDto().setUniqueFileName(applnEntriesDBO.getRegretLetterUrlDbo().getFileNameUnique());
						finalInterviewCommentsDTO.getRegretLetterUrlDto().setActualPath(applnEntriesDBO.getRegretLetterUrlDbo().getFileNameUnique());
						if(!Utils.isNullOrEmpty(applnEntriesDBO.getRegretLetterUrlDbo().getUrlFolderListDBO())) {
							finalInterviewCommentsDTO.getRegretLetterUrlDto().setProcessCode(applnEntriesDBO.getRegretLetterUrlDbo().getUrlFolderListDBO().getUploadProcessCode());
						}}
					apiResult.setSuccess(true);
					apiResult.setDto(finalInterviewCommentsDTO);
				} else if (forwarded.contains(applnEntriesDBO.getApplicantCurrentProcessStatus().getProcessCode())){
					apiResult.setSuccess(false);
					apiResult.setFailureMessage("The application is forwarded from Stage 3");
				} else {
					apiResult.setSuccess(false);
					apiResult.setFailureMessage("The application is not eligible for Stage 3");
				}
			}
		}
		return Mono.just(apiResult);
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> saveOrUpdateStageThree(Mono<FinalInterviewCommentsDTO> dto, String userId) {
		EmpApplnEntriesDBO dbo1 = null;
		List<FinalInterviewCommentsDTO> dto1 = new ArrayList<FinalInterviewCommentsDTO>();
		List<ErpWorkFlowProcessDBO> erpWorkFlowProcessDBOList = finalInterviewCommentsTransaction1.getWorkFlowProcessAll();
		Map<String, ErpWorkFlowProcessDBO> workFlowMap = !Utils.isNullOrEmpty(erpWorkFlowProcessDBOList)? erpWorkFlowProcessDBOList.stream().collect(Collectors.toMap(s -> s.getProcessCode(), s -> s)): new HashMap<String, ErpWorkFlowProcessDBO>();
		Tuple selectedStatus = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("EMP_STAGE3_SELECTED");
		Tuple rejectedStatus = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("EMP_STAGE3_REJECTED");
		Tuple offerGeneratedStatus = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("EMP_OFFER_LETTER_GENERATED");
		Tuple offerReGeneratedStatus = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("EMP_OFFER_LETTER_REGENERATE");
		Tuple regretLetterStatus = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("EMP_REGRET_LETTER_GENERATED");
		List<ErpEmailsDBO> emailsListApplicant = new ArrayList<ErpEmailsDBO>();
		List<ErpSmsDBO> smsListApplicant = new ArrayList<ErpSmsDBO>();
		List<ErpNotificationsDBO> notificationListApplicant = new ArrayList<ErpNotificationsDBO>();
		List<Integer> processCodeList = new ArrayList<Integer>();
		List<FileUploadDownloadDTO> uniqueFileNameList = new ArrayList<FileUploadDownloadDTO>();
		processCodeList.add(Integer.parseInt(selectedStatus.get("erp_work_flow_process_id").toString()));
		processCodeList.add(Integer.parseInt(rejectedStatus.get("erp_work_flow_process_id").toString()));
		processCodeList.add(Integer.parseInt(offerGeneratedStatus.get("erp_work_flow_process_id").toString()));
		processCodeList.add(Integer.parseInt(offerReGeneratedStatus.get("erp_work_flow_process_id").toString()));
		processCodeList.add(Integer.parseInt(regretLetterStatus.get("erp_work_flow_process_id").toString()));
		//		String notificationCodeApplicantSelected = "EMP_STAGE3_SELECTED_TO_CANDIDATE";
		//		String notificationCodeApplicantRejected= "EMP_STAGE3_REJECTED_TO_CANDIDATE";
		//		String notificationCodeOfferGenerated = "EMP_OFFER_LETTER_GENERATED_TO_CANDIDATE";
		//		String notificationCodeOfferRegenerated= "EMP_OFFER_LETTER_REGENERATED_TO_CANDIDATE";
		Map<String, ErpWorkFlowProcessNotificationsDBO> notificationMap = finalInterviewCommentsTransaction1.getErpNotifications(processCodeList).stream().collect(Collectors.toMap(s -> s.getNotificationCode(), s -> s));
		return dto.map(data ->
				convertDtoToDbo(data,dbo1,dto1,notificationMap,workFlowMap,emailsListApplicant,
						smsListApplicant,notificationListApplicant,userId, uniqueFileNameList)).flatMap(dbo -> {
			boolean result = finalInterviewCommentsTransaction1.saveOrUpdate(dbo);
			if(result) {
				Set<Integer> approversIdSet = new HashSet<Integer>();
				approversIdSet.add(Integer.parseInt(userId));
				FinalInterviewCommentsDTO interviewDTO = dto1.get(0);
				if(interviewDTO.getSelectionStatus().equalsIgnoreCase("Yes")) {
					if(interviewDTO.getIsOfferLetterRegenerated().equals(true)) {
						commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(Integer.parseInt(offerReGeneratedStatus.get("erp_work_flow_process_id").toString()),"EMP_OFFER_LETTER_REGENERATED_TO_CANDIDATE",approversIdSet,notificationListApplicant,smsListApplicant,emailsListApplicant);

					} else if(interviewDTO.getIsOfferLetterGenerated().equals(true)) {
						commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(Integer.parseInt(offerGeneratedStatus.get("erp_work_flow_process_id").toString()),"EMP_OFFER_LETTER_GENERATED_TO_CANDIDATE",approversIdSet,notificationListApplicant,smsListApplicant,emailsListApplicant);
					} else {
						commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(Integer.parseInt(selectedStatus.get("erp_work_flow_process_id").toString()),"EMP_STAGE3_SELECTED_TO_CANDIDATE",approversIdSet,notificationListApplicant,smsListApplicant,emailsListApplicant);
					}
				}
				if(interviewDTO.getSelectionStatus().equalsIgnoreCase("No")) {
					if(interviewDTO.getIsRegretLetterGenerated().equals(true)) {
						commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(Integer.parseInt(regretLetterStatus.get("erp_work_flow_process_id").toString()),"EMP_REGRET_LETTER_GENERATED_TO_CANDIDATE ",approversIdSet,notificationListApplicant,smsListApplicant,emailsListApplicant);
					} else {
						commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(Integer.parseInt(rejectedStatus.get("erp_work_flow_process_id").toString()),"EMP_STAGE3_REJECTED_TO_CANDIDATE ",approversIdSet,notificationListApplicant,smsListApplicant,emailsListApplicant);
					}
				}
						/*if(interviewDTO.getIsOfferLetterRegenerated().equals(true) || interviewDTO.getIsOfferLetterGenerated().equals(true)
								|| interviewDTO.getIsRegretLetterGenerated().equals(true)) {
							GenerateOfferLetterAndRegretLetterPDF(interviewDTO, userId);
						}*/
				if((uniqueFileNameList.size() > 0) ) {
					aWSS3FileStorageServiceHandler.moveMultipleObjects(uniqueFileNameList)
							.subscribe(res -> {
								if (res.success()) {
									System.out.println("Move operation succeeded");
								} else {
									System.out.println("Move operation failed: " + res.message());
								}
							});
				}
			}
			return Mono.just(Boolean.TRUE);
		}).map(Utils::responseResult);
	}

	private List<Object> convertDtoToDbo(FinalInterviewCommentsDTO dto, EmpApplnEntriesDBO dbo, List<FinalInterviewCommentsDTO> dto1, Map<String, ErpWorkFlowProcessNotificationsDBO> notificationMap,
										 Map<String, ErpWorkFlowProcessDBO> workFlowMap, List<ErpEmailsDBO> emailsListApplicant, List<ErpSmsDBO> smsListApplicant, List<ErpNotificationsDBO> notificationListApplicant, String userId,
										 List<FileUploadDownloadDTO> uniqueFileNameList ) {
		List<Object> objList = new ArrayList<Object>();
		dto1.add(dto);
		ErpWorkFlowProcessNotificationsDBO notification = null;
		if(!Utils.isNullOrEmpty(dto) && !Utils.isNullOrEmpty(dto.applicationNumber)) {
			dbo = finalInterviewCommentsTransaction1.getEmpApplnEntries(Integer.parseInt(dto.applicationNumber));
		}
		if(!Utils.isNullOrEmpty(dbo)) {
			if(!Utils.isNullOrEmpty(dbo.getEmpJobDetailsDBO())) {
				if(!Utils.isNullOrEmpty(dto.getStage3Comments())) {
					dbo.getEmpJobDetailsDBO().setStage3Comments(dto.getStage3Comments());
				}
				if(!Utils.isNullOrEmpty(dto.getStage3InterviewedBy())) {
					if(!Utils.isNullOrEmpty(dto.getStage3InterviewedBy().getValue())) {
						dbo.getEmpJobDetailsDBO().setStage3InterviewedBy(new EmpDBO());
						dbo.getEmpJobDetailsDBO().getStage3InterviewedBy().setId(Integer.parseInt(dto.getStage3InterviewedBy().getValue()));
					}
				}
				if(!Utils.isNullOrEmpty(dto.getJoiningDateAndTime())) {
					dbo.getEmpJobDetailsDBO().setJoiningDate(dto.getJoiningDateAndTime());
				}
			}
			if(!Utils.isNullOrEmpty(dto.getOfferLetterTemplate())) {
				if(!Utils.isNullOrEmpty(dto.getOfferLetterTemplate().getValue())) {
					dbo.setErpTemplateForOfferLetterDBO(new ErpTemplateDBO());
					dbo.getErpTemplateForOfferLetterDBO().setId(Integer.parseInt(dto.getOfferLetterTemplate().getValue()));
					dbo.setErpTemplateForRegretLetterDBO(null);
				}
			}
			if(!Utils.isNullOrEmpty(dto.getRegretLetterTemplate())) {
				if(!Utils.isNullOrEmpty(dto.getRegretLetterTemplate().getValue())) {
					dbo.setErpTemplateForRegretLetterDBO(new ErpTemplateDBO());
					dbo.getErpTemplateForRegretLetterDBO().setId(Integer.parseInt(dto.getRegretLetterTemplate().getValue()));
					dbo.setErpTemplateForOfferLetterDBO(null);
				}
			}
			if(dto.getSelectionStatus().equalsIgnoreCase("onHold")) {
				dbo.setErpTemplateForRegretLetterDBO(null);
				dbo.setErpTemplateForOfferLetterDBO(null);
			}
			ErpWorkFlowProcessDBO erpWorkFlowProcessDBO = null;
			if(!Utils.isNullOrEmpty(dto.getSelectionStatus())) {
				if(dto.getSelectionStatus().equalsIgnoreCase("Yes")) {
					if(dto.getIsOfferLetterRegenerated().equals(true)) {
						erpWorkFlowProcessDBO = workFlowMap.get("EMP_OFFER_LETTER_REGENERATE");
						//offer letter url to be replaced 
						dbo.setApplicationCurrentProcessStatus(erpWorkFlowProcessDBO);
						dbo.setApplicationStatusTime(LocalDateTime.now());
						dbo.setApplicantCurrentProcessStatus(erpWorkFlowProcessDBO);
						dbo.setApplicationStatusTime(LocalDateTime.now());
						notification = notificationMap.get("EMP_OFFER_LETTER_REGENERATED_TO_CANDIDATE");
						if(!Utils.isNullOrEmpty(notification)) {
							if(notification.getIsEmailActivated()) {
								ErpTemplateDBO erpTemplateDBO = notification.getErpEmailsTemplateDBO();
								emailsListApplicant.add(getEmailDBO(erpTemplateDBO,dbo.getId(),dbo,userId, dto));
							}
							if(notification.getIsSmsActivated()) {
								ErpTemplateDBO erpTemplateDBO = notification.getErpSmsTemplateDBO();
								smsListApplicant.add(getSMSDBO(erpTemplateDBO,dbo.getId(),dbo,userId,dto));
							}
							if(notification.getIsNotificationActivated()) {
								notificationListApplicant.add(getNotificationsDBO(notification.getId(),Integer.parseInt(userId),notification));
							}
						}
					} else if(dto.isOfferLetterGenerated.equals(true)) {
						erpWorkFlowProcessDBO = workFlowMap.get("EMP_OFFER_LETTER_GENERATED");
						dbo.setApplicationCurrentProcessStatus(erpWorkFlowProcessDBO);
						dbo.setApplicationStatusTime(LocalDateTime.now());
						dbo.setApplicantCurrentProcessStatus(erpWorkFlowProcessDBO);
						dbo.setApplicationStatusTime(LocalDateTime.now());
						// offer letter url saving to be included	
						dbo.setOfferLetterGeneratedDate(LocalDateTime.now());
						notification = notificationMap.get("EMP_OFFER_LETTER_GENERATED_TO_CANDIDATE");
						if(!Utils.isNullOrEmpty(notification)) {
							if(notification.getIsEmailActivated()) {
								ErpTemplateDBO erpTemplateDBO = notification.getErpEmailsTemplateDBO();
								emailsListApplicant.add(getEmailDBO(erpTemplateDBO,dbo.getId(), dbo, userId, dto));
							}
							if(notification.getIsSmsActivated()) {
								ErpTemplateDBO erpTemplateDBO = notification.getErpSmsTemplateDBO();
								smsListApplicant.add(getSMSDBO(erpTemplateDBO,dbo.getId(), dbo, userId,dto));
							}
							if(notification.getIsNotificationActivated()) {
								notificationListApplicant.add(getNotificationsDBO(notification.getId(),Integer.parseInt(userId),notification));
							}
						}
					} else {
						erpWorkFlowProcessDBO = workFlowMap.get("EMP_STAGE3_SELECTED");
						dbo.setApplicationCurrentProcessStatus(erpWorkFlowProcessDBO);
						dbo.setApplicationStatusTime(LocalDateTime.now());
						dbo.setApplicantCurrentProcessStatus(erpWorkFlowProcessDBO);
						dbo.setApplicationStatusTime(LocalDateTime.now());
						notification = notificationMap.get("EMP_STAGE3_SELECTED_TO_CANDIDATE");
						if(!Utils.isNullOrEmpty(notification)) {
							if(notification.getIsEmailActivated()) {
								ErpTemplateDBO erpTemplateDBO = notification.getErpEmailsTemplateDBO();
								emailsListApplicant.add(getEmailDBO(erpTemplateDBO,dbo.getId(), dbo, userId, dto));
							}
							if(notification.getIsSmsActivated()) {
								ErpTemplateDBO erpTemplateDBO = notification.getErpSmsTemplateDBO();
								smsListApplicant.add(getSMSDBO(erpTemplateDBO,dbo.getId(), dbo, userId,dto));
							}
							if(notification.getIsNotificationActivated()) {
								notificationListApplicant.add(getNotificationsDBO(notification.getId(),Integer.parseInt(userId),notification));
							}
						}
					}
				}
				if(dto.getSelectionStatus().equalsIgnoreCase("No")) {
					if(dto.getIsRegretLetterGenerated().equals(true)) {
						erpWorkFlowProcessDBO = workFlowMap.get("EMP_REGRET_LETTER_GENERATED");
						dbo.setApplicationCurrentProcessStatus(erpWorkFlowProcessDBO);
						dbo.setApplicationStatusTime(LocalDateTime.now());
						dbo.setApplicantCurrentProcessStatus(erpWorkFlowProcessDBO);
						dbo.setApplicationStatusTime(LocalDateTime.now());
						// regret url to be saved
						dbo.setRegretLetterGeneratedDate(LocalDateTime.now());
						notification = notificationMap.get("EMP_REGRET_LETTER_GENERATED_TO_CANDIDATE");
						if(!Utils.isNullOrEmpty(notification)) {
							if(notification.getIsEmailActivated()) {
								ErpTemplateDBO erpTemplateDBO = notification.getErpEmailsTemplateDBO();
								emailsListApplicant.add(getEmailDBO(erpTemplateDBO,dbo.getId(), dbo, userId, dto));
							}
							if(notification.getIsSmsActivated()) {
								ErpTemplateDBO erpTemplateDBO = notification.getErpSmsTemplateDBO();
								smsListApplicant.add(getSMSDBO(erpTemplateDBO,dbo.getId(), dbo, userId,dto));
							}
							if(notification.getIsNotificationActivated()) {
								notificationListApplicant.add(getNotificationsDBO(notification.getId(),Integer.parseInt(userId),notification));
							}
						}
					} else {
						erpWorkFlowProcessDBO = workFlowMap.get("EMP_STAGE3_REJECTED");
						dbo.setApplicationCurrentProcessStatus(erpWorkFlowProcessDBO);
						dbo.setApplicationStatusTime(LocalDateTime.now());
						dbo.setApplicantCurrentProcessStatus(erpWorkFlowProcessDBO);
						dbo.setApplicationStatusTime(LocalDateTime.now());
						notification = notificationMap.get("EMP_STAGE3_REJECTED_TO_CANDIDATE");
						if(!Utils.isNullOrEmpty(notification)) {
							if(notification.getIsEmailActivated()) {
								ErpTemplateDBO erpTemplateDBO = notification.getErpEmailsTemplateDBO();
								emailsListApplicant.add(getEmailDBO(erpTemplateDBO,dbo.getId(), dbo, userId, dto));
							}
							if(notification.getIsSmsActivated()) {
								ErpTemplateDBO erpTemplateDBO = notification.getErpSmsTemplateDBO();
								smsListApplicant.add(getSMSDBO(erpTemplateDBO,dbo.getId(), dbo, userId,dto));
							}
							if(notification.getIsNotificationActivated()) {
								notificationListApplicant.add(getNotificationsDBO(notification.getId(),Integer.parseInt(userId),notification));
							}
						}
					}
				}
				if(dto.getSelectionStatus().equalsIgnoreCase("onHold")) {
					erpWorkFlowProcessDBO = workFlowMap.get("EMP_STAGE3_ONHOLD");
					dbo.setApplicationCurrentProcessStatus(erpWorkFlowProcessDBO);
					dbo.setApplicationStatusTime(LocalDateTime.now());

				}
			}
			ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
			erpWorkFlowProcessStatusLogDBO.setEntryId(dbo.getId());
			erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
			erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
			erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(erpWorkFlowProcessDBO);
			//-----------generate offer letter and upload the same
			if((dto.getIsOfferLetterRegenerated().equals(true) || dto.getIsOfferLetterGenerated().equals(true)
					|| dto.getIsRegretLetterGenerated().equals(true)) && !dto.getSelectionStatus().equalsIgnoreCase("onHold")) {
				String processCode = "";
				if(dto.getIsOfferLetterGenerated() || dto.getIsOfferLetterRegenerated()) {
					processCode = "EMPLOYEE_OFFER_LETTER";
				}
				else if (dto.getIsRegretLetterGenerated()){
					processCode = "EMPLOYEE_REGRET_LETTER";
				}
				String[] awsConfig = redisAwsConfig.getAwsProperties(processCode);
				String tempPath = "";
				String fileName = "";
				String fileNameWithFolderPath = "";
				String bucketName = "";
				String actualPath = "";
				if(!Utils.isNullOrEmpty(awsConfig)) {
					tempPath = awsConfig[RedisAwsConfig.TEMP_PATH];
					actualPath = awsConfig[RedisAwsConfig.ACTUAL_PATH];
					bucketName = awsConfig[RedisAwsConfig.TEMP_BUCKET_NAME];
					UUID uuid = UUID.randomUUID();
					fileName = uuid.toString() + ".pdf";
					if(!Utils.isNullOrEmpty(tempPath)) {
						fileNameWithFolderPath = tempPath + fileName;
					}

					GenerateOfferLetterAndRegretLetterPDF(dto, userId, fileNameWithFolderPath, bucketName);

					UrlAccessLinkDBO urlAccessLinkDBO = null;
					UrlFolderListDBO folderListDBO = new UrlFolderListDBO();
					if(!Utils.isNullOrEmpty(awsConfig[RedisAwsConfig.FOLDER_LIST_ID])) {
						folderListDBO.setId(Integer.parseInt(awsConfig[RedisAwsConfig.FOLDER_LIST_ID]));
					}
					if(processCode.equalsIgnoreCase("EMPLOYEE_OFFER_LETTER")) {
						if(!Utils.isNullOrEmpty(dbo.getOfferLetterUrlDbo())) {
							urlAccessLinkDBO = dbo.getOfferLetterUrlDbo();
						}
						else {
							urlAccessLinkDBO = new UrlAccessLinkDBO();
						}
						if(!Utils.isNullOrEmpty(dbo.getRegretLetterUrlDbo())) {
							dbo.getRegretLetterUrlDbo().setRecordStatus('D');
						}
						if(!Utils.isNullOrEmpty(actualPath)) {
							urlAccessLinkDBO.setFileNameUnique(actualPath + fileName);
						}
						urlAccessLinkDBO.setTempFileNameUnique(fileNameWithFolderPath);
						urlAccessLinkDBO.setUrlFolderListDBO(folderListDBO);
						urlAccessLinkDBO.setIsQueued(false);
						urlAccessLinkDBO.setIsServiced(true);
						urlAccessLinkDBO.setFileNameOriginal(fileName);
						urlAccessLinkDBO.setCreatedUsersId(Integer.parseInt(userId));
						urlAccessLinkDBO.setRecordStatus('A');
						dbo.setOfferLetterUrlDbo(urlAccessLinkDBO);
					}else if (processCode.equalsIgnoreCase("EMPLOYEE_REGRET_LETTER")) {
						if(!Utils.isNullOrEmpty(dbo.getRegretLetterUrlDbo())) {
							urlAccessLinkDBO = dbo.getRegretLetterUrlDbo();
						}
						else {
							urlAccessLinkDBO = new UrlAccessLinkDBO();
						}
						if(!Utils.isNullOrEmpty(dbo.getOfferLetterUrlDbo())) {
							dbo.getOfferLetterUrlDbo().setRecordStatus('D');
						}
						if(!Utils.isNullOrEmpty(actualPath)) {
							urlAccessLinkDBO.setFileNameUnique(actualPath + fileName);
						}
						urlAccessLinkDBO.setTempFileNameUnique(fileNameWithFolderPath);
						urlAccessLinkDBO.setUrlFolderListDBO(folderListDBO);
						urlAccessLinkDBO.setIsQueued(false);
						urlAccessLinkDBO.setIsServiced(true);
						urlAccessLinkDBO.setFileNameOriginal(fileName);
						urlAccessLinkDBO.setCreatedUsersId(Integer.parseInt(userId));
						urlAccessLinkDBO.setRecordStatus('A');
						dbo.setRegretLetterUrlDbo(urlAccessLinkDBO);
					}
					uniqueFileNameList.addAll(employeeApplicationHandler.createFileListForActualCopy(processCode, fileName));
				}

			}
			else if(dto.getSelectionStatus().equalsIgnoreCase("onHold")) {
				if(!Utils.isNullOrEmpty(dbo.getRegretLetterUrlDbo())) {
					dbo.getRegretLetterUrlDbo().setRecordStatus('D');
				}
				if(!Utils.isNullOrEmpty(dbo.getOfferLetterUrlDbo())) {
					dbo.getOfferLetterUrlDbo().setRecordStatus('D');
				}
			}

			//-----------
			objList.add(dbo);
			objList.add(erpWorkFlowProcessStatusLogDBO);



			//			Set<Integer> approversIdSet = new HashSet<Integer>();
			//			approversIdSet.add(Integer.parseInt(userId));
			//			if(dto.getSelectionStatus().equalsIgnoreCase("Yes")) {
			//				if(dto.getIsOfferLetterRegenerated().equals(true)) {
			//					commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(Integer.parseInt(offerReGeneratedStatus.get("erp_work_flow_process_id").toString()),"EMP_OFFER_LETTER_REGENERATED_TO_CANDIDATE",approversIdSet,notificationListApplicant,smsListApplicant,emailsListApplicant);	
			//				} else if(dto.getIsOfferLetterGenerated().equals(true)) {
			//					commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(Integer.parseInt(offerGeneratedStatus.get("erp_work_flow_process_id").toString()),"EMP_OFFER_LETTER_GENERATED_TO_CANDIDATE",approversIdSet,notificationListApplicant,smsListApplicant,emailsListApplicant);	
			//				} else {
			//					commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(Integer.parseInt(selectedStatus.get("erp_work_flow_process_id").toString()),"EMP_STAGE3_SELECTED_TO_CANDIDATE",approversIdSet,notificationListApplicant,smsListApplicant,emailsListApplicant);	
			//				}
			//			}
			//			if(dto.getSelectionStatus().equalsIgnoreCase("No"))
			//				commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(Integer.parseInt(rejectedStatus.get("erp_work_flow_process_id").toString()),"EMP_STAGE3_REJECTED_TO_CANDIDATE ",approversIdSet,notificationListApplicant,smsListApplicant,emailsListApplicant);
		}
		return objList;
	}

	private ErpEmailsDBO getEmailDBO(ErpTemplateDBO erpTemplateDBO, Integer entryId, EmpApplnEntriesDBO dbo, String userId, FinalInterviewCommentsDTO dto) {
		ErpEmailsDBO erpEmailsDBO = new ErpEmailsDBO();
		erpEmailsDBO.entryId = entryId;
		ErpUsersDBO erpUsersDBO1 = new ErpUsersDBO();
		erpUsersDBO1.id = Integer.parseInt(userId);
		erpEmailsDBO.erpUsersDBO = erpUsersDBO1;
		String msgBody = erpTemplateDBO.getTemplateContent();
		if(!Utils.isNullOrEmpty(msgBody)) {
			if(!Utils.isNullOrEmpty(dbo.getApplicantName())) {
				msgBody = msgBody.replace("[EMP_APPLICANT_NAME]", dbo.getApplicantName());
			}
			if(!Utils.isNullOrEmpty(dbo.getApplicationNo())) {
				msgBody = msgBody.replace("[APPLICATION_NO]", String.valueOf(dbo.getApplicationNo()));
			}
			if(!Utils.isNullOrEmpty(dbo.getEmpJobDetailsDBO())) {
				if(!Utils.isNullOrEmpty(dbo.getEmpJobDetailsDBO().getReportingDate())) {
					msgBody = msgBody.replace("[EMP_REPORTING_DATE]", Utils.convertLocalDateTimeToStringDate(dbo.getEmpJobDetailsDBO().getReportingDate()));
				}
				if(!Utils.isNullOrEmpty( dbo.getEmpJobDetailsDBO().getJoiningDate())) {
					msgBody = msgBody.replace("[EMP_JOINING_DATE]", Utils.convertLocalDateTimeToStringDate(dbo.getEmpJobDetailsDBO().getJoiningDate()));
				}
			}
			if(!Utils.isNullOrEmpty(dbo.getPersonalEmailId())) {
				erpEmailsDBO.recipientEmail = dbo.getPersonalEmailId();
			}
			erpEmailsDBO.emailContent = msgBody;
			if(!Utils.isNullOrEmpty(erpTemplateDBO.getMailSubject()))
				erpEmailsDBO.emailSubject = erpTemplateDBO.getMailSubject();
			if(!Utils.isNullOrEmpty(erpTemplateDBO.getMailFromName()))
				erpEmailsDBO.senderName = erpTemplateDBO.getMailFromName();
		}
		erpEmailsDBO.createdUsersId = Integer.parseInt(userId);
		erpEmailsDBO.recordStatus = 'A';
		return erpEmailsDBO;
	}

	private ErpSmsDBO getSMSDBO(ErpTemplateDBO erpTemplateDBO1, Integer entryId, EmpApplnEntriesDBO dbo, String userId, FinalInterviewCommentsDTO dto) {
		ErpSmsDBO erpSmsDBO = new ErpSmsDBO();
		erpSmsDBO.entryId = entryId;
		ErpUsersDBO erpUsersDBO1 = new ErpUsersDBO();
		erpUsersDBO1.id =Integer.parseInt(userId);
		erpSmsDBO.erpUsersDBO = erpUsersDBO1;
		String msgBody = erpTemplateDBO1.getTemplateContent();
		if(!Utils.isNullOrEmpty(msgBody)) {
			if(!Utils.isNullOrEmpty(dbo.getApplicantName())) {
				msgBody = msgBody.replace("[EMP_APPLICANT_NAME]", dbo.getApplicantName());
			}
			if(!Utils.isNullOrEmpty(dbo.getApplicationNo())) {
				msgBody = msgBody.replace("[APPLICATION_NO]", String.valueOf(dbo.getApplicationNo()));
			}
			if(!Utils.isNullOrEmpty(dbo.getEmpJobDetailsDBO())) {
				if(!Utils.isNullOrEmpty(dbo.getEmpJobDetailsDBO().getReportingDate())) {
					msgBody = msgBody.replace("[EMP_REPORTING_DATE]", Utils.convertLocalDateTimeToStringDate(dbo.getEmpJobDetailsDBO().getReportingDate()));
				}
				if(!Utils.isNullOrEmpty( dbo.getEmpJobDetailsDBO().getJoiningDate())) {
					msgBody = msgBody.replace("[EMP_JOINING_DATE]", Utils.convertLocalDateTimeToStringDate(dbo.getEmpJobDetailsDBO().getJoiningDate()));
				}
			}
			if(!Utils.isNullOrEmpty(dbo.getMobileNo())) {
				erpSmsDBO.recipientMobileNo = dbo.getMobileNo();
			}
			erpSmsDBO.smsContent = msgBody;
			if(!Utils.isNullOrEmpty(erpTemplateDBO1.getTemplateId()))
				erpSmsDBO.setTemplateId(erpTemplateDBO1.getTemplateId());
		}
		erpSmsDBO.createdUsersId = Integer.parseInt(userId);
		erpSmsDBO.recordStatus = 'A';
		return erpSmsDBO;
	}

	private ErpNotificationsDBO getNotificationsDBO(int entryId, int userId, ErpWorkFlowProcessNotificationsDBO erpWorkFlowProcessNotificationsDBO) {
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

	private void GenerateOfferLetterAndRegretLetterPDF(FinalInterviewCommentsDTO dto, String userId, String fileName, String bucketName) {
		try {
			/*Document document = new Document();
			OutputStream outputStream = new FileOutputStream(new File("D:\\" + dto.getApplicationNumber() + "TestFile.pdf"));
			boolean result = false;*/

			String templateId= null;
			FinalInterviewCommentsDTO finalInterviewCommentsDTO = dto;
			if(!Utils.isNullOrEmpty(dto.getOfferLetterTemplate())) {
				templateId= dto.getOfferLetterTemplate().getValue();
			} else if (!Utils.isNullOrEmpty(dto.getRegretLetterTemplate())) {
				templateId =dto.getRegretLetterTemplate().getValue();
			}
			String template = finalInterviewCommentsHelper1.replaceTemplateTagData1(dto.getApplicationNumber(),templateId,dto.getSelectionStatus());
			if(!Utils.isNullOrEmpty(template)) {
				finalInterviewCommentsDTO.setOfferLetterPreview(template);
			} else {
				finalInterviewCommentsDTO.setOfferLetterPreview("Template not defined");
			}
			

			/*PdfWriter writer = PdfWriter.getInstance(document, outputStream);
			document.open();
			InputStream is = new ByteArrayInputStream(finalInterviewCommentsDTO.getOfferLetterPreview().getBytes());
			try {
				XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
			} catch (Exception e) {
				e.printStackTrace();
			}
			document.close();
			outputStream.close();*/
			if (!Utils.isNullOrEmpty(bucketName) && !Utils.isNullOrEmpty(fileName)) {
				String htmlString = finalInterviewCommentsDTO.getOfferLetterPreview();
				htmlString = htmlString.replaceAll("<br>", "<br/>");
				aWSS3FileStorageServiceHandler.generatePDFAndUploadFile(htmlString, bucketName,  fileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//return Mono.just(false);
	}

	public Mono<ApiResult<String>> getOfferLetterPreview(String applicationNumber,String selectionStatus, String templateId) {
		var apiResult = new ApiResult<String>();
		String template = finalInterviewCommentsHelper1.replaceTemplateTagData1(applicationNumber,templateId,selectionStatus);
		if(!Utils.isNullOrEmpty(template)) {
			apiResult.setDto(template);
			apiResult.setSuccess(true);
		} else {
			apiResult.setFailureMessage("Template not defined");
			apiResult.setSuccess(false);
		}
		return Mono.just(apiResult);
	}

	//	public boolean saveOrUpdate(FinalInterviewCommentsDTO finalInterviewCommentsDTO, String userId) throws Exception {
	//	boolean isSavedDetails = false;
	//	List<Object> objects = new ArrayList<Object>();
	//	if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO) && !Utils.isNullOrEmpty(finalInterviewCommentsDTO.applicationNumber)) {
	//		EmpApplnEntriesDBO applnEntriesDBO = finalInterviewCommentsTransaction1.getEmpApplnEntries(Integer.parseInt(finalInterviewCommentsDTO.applicationNumber));
	//		if(!Utils.isNullOrEmpty(applnEntriesDBO)) {
	//			finalInterviewCommentsDTO.id = String.valueOf(applnEntriesDBO.id);
	//			List<EmpApplnDignitariesFeedbackDBO> applnDignitariesFeedbackDBOs = null;
	//			if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO.processAndPosition)) {
	//				applnDignitariesFeedbackDBOs = new ArrayList<EmpApplnDignitariesFeedbackDBO>();
	//				applnDignitariesFeedbackDBOs = finalInterviewCommentsTransaction1.getApplnDignitariesFeedbackByApplnId(applnEntriesDBO.id);
	//			}
	//			applnEntriesDBO = finalInterviewCommentsHelper1.convertEmpApplnEntriesDTOToDBO(applnEntriesDBO, finalInterviewCommentsDTO, applnDignitariesFeedbackDBOs, userId,objects);
	//		}
	//		EmpJobDetailsDBO jobDetailsDBO = null;
	//		jobDetailsDBO = finalInterviewCommentsTransaction1.getEmpJobDetails(applnEntriesDBO.id);
	//		jobDetailsDBO = finalInterviewCommentsHelper.convertEmpJobDetailsDTOToDBO(applnEntriesDBO, jobDetailsDBO, finalInterviewCommentsDTO, userId);
	//		applnEntriesDBO.empJobDetailsDBO = jobDetailsDBO;
	//		EmpPayScaleDetailsDBO empPayScaleDetailsDBO = null;
	//		empPayScaleDetailsDBO = finalInterviewCommentsTransaction1.getEmpPayScaleDetails(applnEntriesDBO.id); /// need to change here
	//		List<EmpPayScaleDetailsComponentsDBO> empPayScaleDetailsComponentsDBOsList = !Utils.isNullOrEmpty(empPayScaleDetailsDBO) ? finalInterviewCommentsTransaction1.getPayScaleDetailComponent(empPayScaleDetailsDBO.id) : new ArrayList<EmpPayScaleDetailsComponentsDBO>();
	//		empPayScaleDetailsDBO = finalInterviewCommentsHelper1.convertEmpPayScaleDetailsDTOToDBO(applnEntriesDBO, empPayScaleDetailsDBO, empPayScaleDetailsComponentsDBOsList, finalInterviewCommentsDTO, userId);
	//		objects.add(empPayScaleDetailsDBO);
	//		ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
	//		erpWorkFlowProcessStatusLogDBO.setEntryId(applnEntriesDBO.getId());
	//		erpWorkFlowProcessStatusLogDBO.setErpWorkFlowProcessDBO(new ErpWorkFlowProcessDBO());
	//		erpWorkFlowProcessStatusLogDBO.getErpWorkFlowProcessDBO().setId(applnEntriesDBO.getApplicantCurrentProcessStatus().getId());
	//		erpWorkFlowProcessStatusLogDBO.setRecordStatus('A');
	//		erpWorkFlowProcessStatusLogDBO.setCreatedUsersId(Integer.parseInt(userId));
	//		objects.add(erpWorkFlowProcessStatusLogDBO);
	//		objects.add(applnEntriesDBO);
	//		isSavedDetails = finalInterviewCommentsTransaction1.saveOrUpdate(objects);
	//	}
	//	return isSavedDetails;
	//}

	//	public String getFinalTemplateData(String templateContent,String applicationNumber)throws Exception {
	//	Tuple applnEntrie = finalInterviewCommentsTransaction1.getFinalInterviewEmpApplnEntries(Integer.parseInt(applicationNumber));
	//	Tuple salaryDetails = finalInterviewCommentsTransaction1.printSalaryDetail(Integer.parseInt(applicationNumber));
	//	Tuple jobDetails = finalInterviewCommentsTransaction1.getFinalInterviewEmpJobDetails(Integer.parseInt(String.valueOf(applnEntrie.get("applnId"))));
	//	Tuple salaryDetailsByPayScaleType = finalInterviewCommentsTransaction1.printSalaryDetailsByPayScaleType(Integer.parseInt(applicationNumber));
	//	String template = finalInterviewCommentsHelper.replaceTemplateTagData(templateContent, applicationNumber,applnEntrie,salaryDetails,jobDetails,salaryDetailsByPayScaleType);
	//	return template;
	//}

	//	public boolean submitMailAndSms(FinalInterviewCommentsDTO finalInterviewCommentsDTO, String userId) throws Exception {
	//	boolean isSavedDetails = false;
	//	if(!Utils.isNullOrEmpty(finalInterviewCommentsDTO) && !Utils.isNullOrEmpty(finalInterviewCommentsDTO.applicationNumber)) {
	//		EmpApplnEntriesDBO applnEntriesDBO = finalInterviewCommentsTransaction1.getEmpApplnEntries(Integer.parseInt(finalInterviewCommentsDTO.applicationNumber));
	//		finalInterviewCommentsDTO.id = String.valueOf(applnEntriesDBO.id);
	//		if(!Utils.isNullOrEmpty(applnEntriesDBO)) {
	//			EmpJobDetailsDBO jobDetailsDBO = null;
	//			jobDetailsDBO = finalInterviewCommentsTransaction1.getEmpJobDetails(applnEntriesDBO.id);
	//			jobDetailsDBO = finalInterviewCommentsHelper.convertEmpJobDetailsDTOToDBO(applnEntriesDBO, jobDetailsDBO, finalInterviewCommentsDTO, userId);
	//			isSavedDetails = finalInterviewCommentsTransaction1.saveOrUpdateEmpJobDetails(jobDetailsDBO);
	//		}
	//	}
	//	return isSavedDetails;
	//}

	//public String saveOrUpdateOfferLetterDetails(String offerLetterUrl,String applicationNumber,Boolean isOfferLetterRegenerated, String userId,Boolean isVcComment,boolean selected,String vcComments, String id) throws Exception {
	//	boolean isSavedDetails = false;
	//	String applicationStatus = null;
	//	EmpApplnEntriesDBO applnEntriesDBO = finalInterviewCommentsTransaction1.getEmpApplnEntries(Integer.parseInt(applicationNumber));
	//	if(!Utils.isNullOrEmpty(applnEntriesDBO)) {
	//		if(selected&&isVcComment) {
	//			applnEntriesDBO.offerLetterUrl = offerLetterUrl;
	//			applnEntriesDBO.offerLetterGeneratedDate = LocalDateTime.now();
	////			applnEntriesDBO.empJobDetailsDBO.vcComments = vcComments;
	//			if(!Utils.isNullOrEmpty(id)) {
	//				applnEntriesDBO.erpTemplateForOfferLetterDBO = new ErpTemplateDBO();
	//				applnEntriesDBO.erpTemplateForOfferLetterDBO.id = Integer.parseInt(id);
	//			}
	//			String processCode = null;
	//			if(!isOfferLetterRegenerated)
	//				processCode = "EMP_SELECTED";
	//			else
	//				processCode = "EMP_OFFER_LETTER_REGENERATED";
	//			isSavedDetails = finalInterviewCommentsTransaction1.saveOrUpdateEmpApplnEntries(applnEntriesDBO);
	//			if(isSavedDetails) {
	//				Tuple tuple = commonApiTransaction.getErpWorkFlowProcessIdbyProcessCode(processCode);
	//				applicationStatus = String.valueOf(tuple.get("application_status_display_text"));
	//			}
	//		}
	//	}
	//	return applicationStatus;
	//}

	//public String workFlowProcess(String applicationStatus,boolean isSavedDetails,String processCode,EmpApplnEntriesDBO applnEntriesDBO,String userId) throws Exception{
	//	Tuple tuple = finalInterviewCommentsTransaction1.getErpWorkFlowProcessIdbyProcessCode(processCode);
	//	if(tuple.get("applicant_status_display_text")!=null && !Utils.isNullOrWhitespace(tuple.get("applicant_status_display_text").toString())) {
	//		if(!tuple.get("applicant_status_display_text").toString().isEmpty()) {
	//			ErpWorkFlowProcessDBO applicant =new ErpWorkFlowProcessDBO();
	//			applicant.id = Integer.parseInt(String.valueOf(tuple.get("erp_work_flow_process_id")));
	//			applnEntriesDBO.applicantCurrentProcessStatus = applicant;
	//		}				
	//	}
	//	if(tuple.get("application_status_display_text")!=null  && !Utils.isNullOrWhitespace(tuple.get("application_status_display_text").toString())) {
	//		if(!tuple.get("application_status_display_text").toString().isEmpty()) {
	//			ErpWorkFlowProcessDBO application =new ErpWorkFlowProcessDBO();
	//			application.id =Integer.parseInt(String.valueOf(tuple.get("erp_work_flow_process_id")));
	//			applnEntriesDBO.applicationCurrentProcessStatus = application;	
	//		}
	//	}
	//	isSavedDetails = finalInterviewCommentsTransaction1.saveOrUpdateEmpApplnEntries(applnEntriesDBO);
	//	if(isSavedDetails) {
	//		applicationStatus = String.valueOf(tuple.get("application_status_display_text"));
	//		if(tuple.get("application_status_display_text")!=null  && !Utils.isNullOrWhitespace(tuple.get("application_status_display_text").toString())) {
	//			if(!tuple.get("application_status_display_text").toString().isEmpty()) {
	//				ErpWorkFlowProcessStatusLogDBO erpWorkFlowStatusDbo= new ErpWorkFlowProcessStatusLogDBO();
	//				if(applnEntriesDBO.id!=0) {
	//					erpWorkFlowStatusDbo.entryId = applnEntriesDBO.id;
	//				}
	//				ErpWorkFlowProcessDBO applicant =new ErpWorkFlowProcessDBO();
	//				applicant.id =Integer.parseInt(String.valueOf(tuple.get("erp_work_flow_process_id")));
	//				erpWorkFlowStatusDbo.erpWorkFlowProcessDBO = applicant;
	//				erpWorkFlowStatusDbo.recordStatus='A';
	//				erpWorkFlowStatusDbo.createdUsersId=Integer.parseInt(userId);
	//				isSavedDetails = commonApiTransaction.saveErpWorkFlowProcessStatusLogDBO(erpWorkFlowStatusDbo);
	//			}
	//		}
	//	}
	//	return applicationStatus;
	//}
}
