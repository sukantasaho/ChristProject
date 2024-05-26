package com.christ.erp.services.handlers.admission.applicationprocess;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.RedisSysPropertiesData;
import com.christ.erp.services.common.RedisVaultKeyConfig;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeAccountDBO;
import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeCategoryDBO;
import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeDBO;
import com.christ.erp.services.dbobjects.account.fee.AccBatchFeeHeadDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsAccountDBO;
import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsDBO;
import com.christ.erp.services.dbobjects.admission.settings.AdmProgrammeSettingsDBO;
import com.christ.erp.services.dbobjects.common.ErpAdmissionCategoryDBO;
import com.christ.erp.services.dbobjects.common.ErpEmailsDBO;
import com.christ.erp.services.dbobjects.common.ErpSmsDBO;
import com.christ.erp.services.dbobjects.common.ErpUsersDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;
import com.christ.erp.services.dto.account.fee.AccBatchFeeAccountDTO;
import com.christ.erp.services.dto.account.fee.AccBatchFeeCategoryDTO;
import com.christ.erp.services.dto.account.fee.AccBatchFeeDTO;
import com.christ.erp.services.dto.account.fee.AccBatchFeeDurationsDTO;
import com.christ.erp.services.dto.account.fee.AccBatchFeeDurationsDetailsDTO;
import com.christ.erp.services.dto.account.fee.AccBatchFeeHeadDTO;
import com.christ.erp.services.dto.account.fee.DemandResponseDTO;
import com.christ.erp.services.dto.admission.applicationprocess.FinalResultApprovalDTO;
import com.christ.erp.services.dto.admission.applicationprocess.FinalResultApprovalStatusDTO;
import com.christ.erp.services.dto.admission.applicationprocess.FinalResultApprovalTemplatesDTO;
import com.christ.erp.services.dto.common.JwsObjectDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.student.common.FirstYearStudentDTO;
import com.christ.erp.services.handlers.account.common.CommonAccountHandler;
import com.christ.erp.services.handlers.aws.AWSS3FileStorageServiceHandler;
import com.christ.erp.services.handlers.common.CommonApiHandler;
import com.christ.erp.services.helpers.account.fee.DemandSlipGenertaionHelper;
import com.christ.erp.services.transactions.account.fee.DemandSlipGenerationTransaction;
import com.christ.erp.services.transactions.admission.CommonAdmissionTransaction;
import com.christ.erp.services.transactions.admission.applicationprocess.FinalResultApprovalTransaction;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.utility.lib.Constants;
import com.christ.utility.lib.ServiceURL;
import com.google.gson.Gson;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class FinalResultApprovalHandler {

	@Autowired
	FinalResultApprovalTransaction finalResultApprovalTransaction;

	@Autowired
	CommonApiTransaction commonApiTransaction1;

	@Autowired
	CommonAdmissionTransaction commonAdmissionTransaction;

	@Autowired
	private CommonApiHandler commonApiHandler;
	
	@Autowired
	DemandSlipGenerationTransaction demandSlipGenerationTransaction;
	
	@Autowired
	DemandSlipGenertaionHelper demandSlipGenertaionHelper;

	@Autowired
	CommonAccountHandler commonAccountHandler;
	@Autowired
	RedisVaultKeyConfig redisVaultKeyConfig;

	@Autowired
	RedisSysPropertiesData redisSysPropertiesData;
	
	@Autowired
	AWSS3FileStorageServiceHandler aWSS3FileStorageServiceHandler;


	public Flux<FinalResultApprovalDTO> getGridData(String applicantCurrentProcessStatus, String applNo,String campusId, String locationId, String programmeLevelId, String programmeId) {
		List<String> finalResultApproverStatus = new ArrayList<String>();
		finalResultApproverStatus.add("ADM_APPLN_SELECTED_UPLOADED");
		finalResultApproverStatus.add("ADM_APPLN_NOT_SELECTED_UPLOADED");
		finalResultApproverStatus.add("ADM_APPLN_WAITLISTED_UPLOADED");
		finalResultApproverStatus.add("ADM_APPLN_SELECTED");
		return finalResultApprovalTransaction.getGridData(applicantCurrentProcessStatus, applNo, campusId, locationId,programmeLevelId,programmeId, finalResultApproverStatus)
				.flatMapMany(Flux::fromIterable).map(this::convertTupleToDTO);
	}

	public FinalResultApprovalDTO convertTupleToDTO(Tuple dbo) {
		FinalResultApprovalDTO dto = new FinalResultApprovalDTO();
		dto.setApplicantName(dbo.get("applicantName").toString());
		if (dbo.get("applnNum") != null && !dbo.get("applnNum").toString().isEmpty())
			dto.setApplicationNumber(dbo.get("applnNum").toString());
		if (dbo.get("locationName") != null && !dbo.get("locationName").toString().isEmpty())
			dto.setLocation(dbo.get("locationName").toString());
		if (dbo.get("campusName") != null && !dbo.get("campusName").toString().isEmpty())
			dto.setCampus(dbo.get("campusName").toString());
		if (dbo.get("programeName") != null && !dbo.get("programeName").toString().isEmpty())
			dto.setPrograme(dbo.get("programeName").toString());
		if (dbo.get("programeId") != null && !dbo.get("programeId").toString().isEmpty())
			dto.setProgrameId(dbo.get("programeId").toString());
		if (dbo.get("residentCategoryId") != null && dbo.get("residentCategoryId").toString() != null) {
			SelectDTO selectDTO = new SelectDTO();
			selectDTO.label = dbo.get("residentCategoryName").toString();
			selectDTO.value = dbo.get("residentCategoryId").toString();
			dto.setResidentCategory(selectDTO);
		}
		if (dbo.get("remarks") != null && !dbo.get("remarks").toString().isEmpty())
			dto.setRemarks(dbo.get("remarks").toString());
		if (dbo.get("emailId") != null && !dbo.get("emailId").toString().isEmpty())
			dto.setEmailId(dbo.get("emailId").toString());
		if (dbo.get("mobileNumber") != null && !dbo.get("mobileNumber").toString().isEmpty())
			dto.setMobileNumber(dbo.get("mobileNumber").toString());
		if (dbo.get("erpCampusProgrameMappingId") != null
				&& !dbo.get("erpCampusProgrameMappingId").toString().isEmpty())
			dto.setErpCampusProgrameMappingId(dbo.get("erpCampusProgrameMappingId").toString());
		if (dbo.get("studentApplnEntriesId") != null && !dbo.get("studentApplnEntriesId").toString().isEmpty())
			dto.setStudentAdmApplnId(dbo.get("studentApplnEntriesId").toString());
		if (dbo.get("acaBatchId") != null && !dbo.get("acaBatchId").toString().isEmpty())
			dto.setAcaBatchId(dbo.get("acaBatchId").toString());
		if (dbo.get("processCode") != null && !dbo.get("processCode").toString().isEmpty())
			dto.setErpWorkFlowProcessCode(dbo.get("processCode").toString());
		if (dbo.get("admissionCategoryId") != null && dbo.get("admissionCategoryId").toString() != null) {
			SelectDTO selectDTO = new SelectDTO();
			selectDTO.label = dbo.get("admissionCategoryName").toString();
			selectDTO.value = dbo.get("admissionCategoryId").toString();
			dto.setAdmissionCategory(selectDTO);
		}
		if (dbo.get("feePaymentDateTime") != null && !dbo.get("feePaymentDateTime").toString().isEmpty())
			dto.setLastDateTimeOfFeePayment(Utils.convertStringDateTimeToLocalDateTime(dbo.get("feePaymentDateTime").toString()));
		if (dbo.get("admissionStartDate") != null && !dbo.get("admissionStartDate").toString().isEmpty())
			dto.setAdmissionStartDateTime(Utils.convertStringDateTimeToLocalDateTime(dbo.get("admissionStartDate").toString()));
		if (dbo.get("admissionFinalDate") != null && !dbo.get("admissionFinalDate").toString().isEmpty())
			dto.setAdmissionEndDateTime(Utils.convertStringDateTimeToLocalDateTime(dbo.get("admissionFinalDate").toString()));
		if (dbo.get("status") != null && !dbo.get("status").toString().isEmpty())
			dto.setStatus(dbo.get("status").toString());
		if (dbo.get("modeOfStudy") != null && !dbo.get("modeOfStudy").toString().isEmpty())
			dto.setModeOfStudy(dbo.get("modeOfStudy").toString());
		return dto;
	}
	
	@SuppressWarnings("unchecked")
	public Mono<ApiResult<FinalResultApprovalDTO>> finalResultApprovalStatusUpdate(String applicantCurrentProcessStatus, Mono<List<FinalResultApprovalDTO>> data1,String userId) {
		var apiResult = new ApiResult<FinalResultApprovalDTO>();
		return data1.map(s-> {
			var status = EmailandSMSTemplateCheck(s) ;
			if(!Utils.isNullOrEmpty(status)) {
				apiResult.setDto(new FinalResultApprovalDTO());
				apiResult.getDto().setTemplateDetails(status);
				apiResult.setSuccess(false);
			} else {
				Object[] obj = convertDtoToDbo(applicantCurrentProcessStatus,(List<FinalResultApprovalDTO>) s,userId);
				if(!Utils.isNullOrEmpty(obj[1]) && !Utils.isNullOrEmpty(obj[1].toString()))  {
					apiResult.setFailureMessage(obj[1].toString());
					apiResult.setSuccess(false);
				}
				else {
					List<Integer> entryIdList = new ArrayList<Integer>();
					boolean isUpdated = finalResultApprovalTransaction.finalResultApprovalStatusUpdate(obj, entryIdList);
					/* demand generation commented for time being
					List<FirstYearStudentDTO> list = (List<FirstYearStudentDTO>) obj[3];
					try {
						ApiResult apiResult1 = generateDemandForFirstYearStudents(list, Integer.parseInt(userId));
						if(apiResult1.success) {
							List<DemandResponseDTO> responseDTOList = apiResult1.getDtoList();
							if(!Utils.isNullOrEmpty(responseDTOList)) {
								List<Integer> entryIdList = responseDTOList.stream().map(DemandResponseDTO::getEntryId).collect(Collectors.toList());
								boolean isUpdated = finalResultApprovalTransaction.finalResultApprovalStatusUpdate(obj, entryIdList);
							}
						}
					} catch (IOException e) {
						throw new RuntimeException(e);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}*/
					apiResult.setSuccess(true);
				}
			}
			return apiResult;
		});
	}
	
	Map<String, ErpTemplateDBO> emailandSMSTemplates = null;
	Map<String, ErpTemplateDBO> admissionCardTemplates = null;
	Map<String, List<FinalResultApprovalTemplatesDTO>> emailandSMSandAdmissionCardErrorDetails = null;
	private Map<String, List<FinalResultApprovalTemplatesDTO>> EmailandSMSTemplateCheck(List<FinalResultApprovalDTO> finalResultApprovalDTO1) {
		List<Integer> erpProgCampusIds = new ArrayList<Integer>();
		List<Integer> admissionCategoryIds = new ArrayList<Integer>();
 		finalResultApprovalDTO1.forEach(finalResultApprovalDTO -> {
			erpProgCampusIds.add(Integer.parseInt(finalResultApprovalDTO.getErpCampusProgrameMappingId()));
			admissionCategoryIds.add(Integer.parseInt(finalResultApprovalDTO.getAdmissionCategory().getValue()));
		});

		List<String> templateTypes = new ArrayList<String>();
		templateTypes.add("Mail");
		templateTypes.add("SMS");

		List<String> templateNames = new ArrayList<String>();
		templateNames.add("Final Result Selected");
		templateNames.add("Final Result Not Selected");
		templateNames.add("Final Result Waitlisted");

		List<String> groupTemplateNames = new ArrayList<String>();
		groupTemplateNames.add("Final Result Selected");
		groupTemplateNames.add("Final Result Not Selected");
		groupTemplateNames.add("Final Result Waitlisted");

		emailandSMSTemplates = new HashMap<String, ErpTemplateDBO>();
		List<ErpTemplateDBO> erpMailandSMSTemplateDBOList = commonApiTransaction1.getErpTemplateByTypeAndCampusProgMappingIdAndTemplateName1(templateTypes, erpProgCampusIds,templateNames, groupTemplateNames);
		erpMailandSMSTemplateDBOList.forEach(erpMailandSMSTemplateDBO -> {
			emailandSMSTemplates.put(erpMailandSMSTemplateDBO.getErpTemplateGroupDBO().getTemplateGroupName() + "-"
					+ erpMailandSMSTemplateDBO.templateName + "-" + erpMailandSMSTemplateDBO.templateType + "-"
					+ erpMailandSMSTemplateDBO.erpCampusProgrammeMappingDBO.id, erpMailandSMSTemplateDBO);
			System.out.println("value:" + erpMailandSMSTemplateDBO.getErpTemplateGroupDBO().getTemplateGroupName() + "-"
					+ erpMailandSMSTemplateDBO.templateName + "-" + erpMailandSMSTemplateDBO.templateType + "-"
					+ erpMailandSMSTemplateDBO.erpCampusProgrammeMappingDBO.id);
		});

		admissionCardTemplates = new HashMap<String, ErpTemplateDBO>();
		List<Integer> erpGroupTemplateId = commonApiTransaction1.getTemplateGroupIdByAdmissionCategory1(admissionCategoryIds);
		List<ErpTemplateDBO> erpAdmissionCardTemplateDBOList = commonApiTransaction1.getErpTemplateByCodeAndTypeAndCampusProgMapping1("Print", erpGroupTemplateId, erpProgCampusIds);
		erpAdmissionCardTemplateDBOList.forEach(erpAdmissionCardTemplateDBO -> {
			admissionCardTemplates.put(erpAdmissionCardTemplateDBO.getErpTemplateGroupDBO().getTemplateGroupName() + "-"
					+ erpAdmissionCardTemplateDBO.templateType + "-"
					+ erpAdmissionCardTemplateDBO.erpCampusProgrammeMappingDBO.id, erpAdmissionCardTemplateDBO);
			System.out.println("value:" + erpAdmissionCardTemplateDBO.getErpTemplateGroupDBO().getTemplateGroupName()
					+ "-" + erpAdmissionCardTemplateDBO.templateType + "-"
					+ erpAdmissionCardTemplateDBO.erpCampusProgrammeMappingDBO.id);
		});

		emailandSMSandAdmissionCardErrorDetails = new LinkedHashMap<String, List<FinalResultApprovalTemplatesDTO>>();
		finalResultApprovalDTO1.forEach(finalResultApprovalDTO -> {
			if (finalResultApprovalDTO.getErpWorkFlowProcessCode().equalsIgnoreCase("ADM_APPLN_SELECTED_UPLOADED")) {
				String groupTemplateName = "Final Result Selected";
				String templateName = "Final Result Selected";
				String type = "Mail";
				ErpTemplateDBO erpTemplateDBO = emailandSMSTemplates.get(groupTemplateName + "-" + templateName + "-"
						+ type + "-" + finalResultApprovalDTO.getErpCampusProgrameMappingId());

				String templateName1 = "Final Result Selected";
				String type1 = "SMS";
				ErpTemplateDBO erpTemplateDBO1 = emailandSMSTemplates.get(groupTemplateName + "-" + templateName1 + "-"
						+ type1 + "-" + finalResultApprovalDTO.getErpCampusProgrameMappingId());

				String groupTemplateName2 = "Admission Card";
				String type2 = "Print";
				ErpTemplateDBO erpTemplateDBO2 = admissionCardTemplates.get(groupTemplateName2 + "-" + type2 + "-"
						+ finalResultApprovalDTO.getErpCampusProgrameMappingId());

				List<FinalResultApprovalTemplatesDTO> finalResultApprovalTemplatesDTOList = new ArrayList<FinalResultApprovalTemplatesDTO>();
				if (!emailandSMSandAdmissionCardErrorDetails.containsKey(finalResultApprovalDTO.getPrograme())) {
					FinalResultApprovalTemplatesDTO dto = new FinalResultApprovalTemplatesDTO();
					dto.setCampus(finalResultApprovalDTO.getCampus());
					FinalResultApprovalStatusDTO emailTemplate = new FinalResultApprovalStatusDTO();
					FinalResultApprovalStatusDTO smsTemplate = new FinalResultApprovalStatusDTO();
					boolean value = false;
					if (erpTemplateDBO == null) {
						emailTemplate.setSelected("Not Present");
						value = true;
					}
					if (erpTemplateDBO1 == null) {
						smsTemplate.setSelected("Not Present");
						value = true;
					}
					FinalResultApprovalStatusDTO AdmissionCardTemplate = new FinalResultApprovalStatusDTO();
					if (erpTemplateDBO2 == null) {
						AdmissionCardTemplate.setSelected("Not present");
						value = true;
					}
					dto.setSmsTemplate(smsTemplate);
					dto.setEmailTemplate(emailTemplate);
					dto.setAdmissionCardTemplate(AdmissionCardTemplate);
					if (value) {
						finalResultApprovalTemplatesDTOList.add(dto);
						emailandSMSandAdmissionCardErrorDetails.put(finalResultApprovalDTO.getPrograme(),finalResultApprovalTemplatesDTOList);
					}
				} else {
					List<FinalResultApprovalTemplatesDTO> finalResultApprovalTemplatesDTOList1 = emailandSMSandAdmissionCardErrorDetails.get(finalResultApprovalDTO.getPrograme());

					Optional<FinalResultApprovalTemplatesDTO> optionalDTO=finalResultApprovalTemplatesDTOList1.stream().filter(o -> o.getCampus().equals(finalResultApprovalDTO.getCampus())).findAny();
					if(!optionalDTO.isPresent()) {
						FinalResultApprovalTemplatesDTO dto = new FinalResultApprovalTemplatesDTO();
						dto.setCampus(finalResultApprovalDTO.getCampus());
						FinalResultApprovalStatusDTO emailTemplate = new FinalResultApprovalStatusDTO();
						FinalResultApprovalStatusDTO smsTemplate = new FinalResultApprovalStatusDTO();
						if (erpTemplateDBO == null)
							emailTemplate.setSelected("Not Present");
						if (erpTemplateDBO1 == null)
							smsTemplate.setSelected("Not Present");
						FinalResultApprovalStatusDTO AdmissionCardTemplate = new FinalResultApprovalStatusDTO();
						if (erpTemplateDBO2 == null)
							AdmissionCardTemplate.setSelected("Not present");
						dto.setSmsTemplate(smsTemplate);
						dto.setEmailTemplate(emailTemplate);
						dto.setAdmissionCardTemplate(AdmissionCardTemplate);

						finalResultApprovalTemplatesDTOList1.add(dto);
					}else {
						FinalResultApprovalTemplatesDTO dto = optionalDTO.get();
						FinalResultApprovalStatusDTO emailTemplate = dto.getEmailTemplate();
						FinalResultApprovalStatusDTO smsTemplate = dto.getSmsTemplate();
						if (erpTemplateDBO == null)
							emailTemplate.setSelected("Not Present");
						if (erpTemplateDBO1 == null)
							smsTemplate.setSelected("Not Present");

						FinalResultApprovalStatusDTO AdmissionCardTemplate = dto.getAdmissionCardTemplate();
						if (erpTemplateDBO2 == null)
							AdmissionCardTemplate.setSelected("Not present");

						dto.setSmsTemplate(smsTemplate);
						dto.setEmailTemplate(emailTemplate);
						dto.setAdmissionCardTemplate(AdmissionCardTemplate);
					}
				}

			} else if (finalResultApprovalDTO.getErpWorkFlowProcessCode().equalsIgnoreCase("ADM_APPLN_NOT_SELECTED_UPLOADED")) {
				String groupTemplateName = "Final Result Not Selected";
				String templateName = "Final Result Not Selected";
				String type = "Mail";
				ErpTemplateDBO erpTemplateDBO = emailandSMSTemplates.get(groupTemplateName + "-" + templateName + "-"
						+ type + "-" + finalResultApprovalDTO.getErpCampusProgrameMappingId());

				String templateName1 = "Final Result Not Selected";
				String type1 = "SMS";
				ErpTemplateDBO erpTemplateDBO1 = emailandSMSTemplates.get(groupTemplateName + "-" + templateName1 + "-"
						+ type1 + "-" + finalResultApprovalDTO.getErpCampusProgrameMappingId());

				if (!emailandSMSandAdmissionCardErrorDetails.containsKey(finalResultApprovalDTO.getPrograme())) {
					List<FinalResultApprovalTemplatesDTO> finalResultApprovalTemplatesDTOList = new ArrayList<FinalResultApprovalTemplatesDTO>();
					String programe = finalResultApprovalDTO.getPrograme();

					FinalResultApprovalTemplatesDTO dto = new FinalResultApprovalTemplatesDTO();
					dto.setCampus(finalResultApprovalDTO.getCampus());

					boolean value = false;
					FinalResultApprovalStatusDTO emailTemplate = new FinalResultApprovalStatusDTO();
					FinalResultApprovalStatusDTO smsTemplate = new FinalResultApprovalStatusDTO();
					if (erpTemplateDBO == null) {
						emailTemplate.setNotSelected("Not Present");
						value = true;
					}
					if (erpTemplateDBO1 == null) {
						smsTemplate.setNotSelected("Not Present");
						value = true;
					}
					FinalResultApprovalStatusDTO AdmissionCardTemplate = new FinalResultApprovalStatusDTO();
					dto.setSmsTemplate(smsTemplate);
					dto.setEmailTemplate(emailTemplate);
					dto.setAdmissionCardTemplate(AdmissionCardTemplate);

					if (value) {
						finalResultApprovalTemplatesDTOList.add(dto);
						emailandSMSandAdmissionCardErrorDetails.put(programe, finalResultApprovalTemplatesDTOList);
					}
				} else {
					List<FinalResultApprovalTemplatesDTO> finalResultApprovalTemplatesDTOList1 = emailandSMSandAdmissionCardErrorDetails.get(finalResultApprovalDTO.getPrograme());
					Optional<FinalResultApprovalTemplatesDTO> optionalDTO=finalResultApprovalTemplatesDTOList1.stream().filter(o -> o.getCampus().equals(finalResultApprovalDTO.getCampus())).findAny();
					if(!optionalDTO.isPresent()) {
						FinalResultApprovalTemplatesDTO dto = new FinalResultApprovalTemplatesDTO();
						dto.setCampus(finalResultApprovalDTO.getCampus());
						FinalResultApprovalStatusDTO emailTemplate = new FinalResultApprovalStatusDTO();
						FinalResultApprovalStatusDTO smsTemplate = new FinalResultApprovalStatusDTO();
						if (erpTemplateDBO == null)
							emailTemplate.setNotSelected("Not Present");

						if (erpTemplateDBO1 == null)
							smsTemplate.setNotSelected("Not Present");

						FinalResultApprovalStatusDTO AdmissionCardTemplate = new FinalResultApprovalStatusDTO();
						dto.setSmsTemplate(smsTemplate);
						dto.setEmailTemplate(emailTemplate);
						dto.setAdmissionCardTemplate(AdmissionCardTemplate);

						finalResultApprovalTemplatesDTOList1.add(dto);
					}else {
						FinalResultApprovalTemplatesDTO dto = optionalDTO.get();
						FinalResultApprovalStatusDTO smsTemplate = dto.getSmsTemplate();
						FinalResultApprovalStatusDTO emailTemplate = dto.getEmailTemplate();
						if (erpTemplateDBO == null)
							emailTemplate.setNotSelected("Not Present");
						if (erpTemplateDBO1 == null)
							smsTemplate.setNotSelected("Not Present");

						FinalResultApprovalStatusDTO AdmissionCardTemplate = dto.getAdmissionCardTemplate();
						dto.setSmsTemplate(smsTemplate);
						dto.setEmailTemplate(emailTemplate);
						dto.setAdmissionCardTemplate(AdmissionCardTemplate);
					}
				}
			} else if (finalResultApprovalDTO.getErpWorkFlowProcessCode().equalsIgnoreCase("ADM_APPLN_WAITLISTED_UPLOADED")) {
				String groupTemplateName = "Final Result Waitlisted";
				String templateName = "Final Result Waitlisted";
				String type = "Mail";
				ErpTemplateDBO erpTemplateDBO = emailandSMSTemplates.get(groupTemplateName + "-" + templateName + "-"
						+ type + "-" + finalResultApprovalDTO.getErpCampusProgrameMappingId());

				String templateName1 = "Final Result Waitlisted";
				String type1 = "SMS";
				ErpTemplateDBO erpTemplateDBO1 = emailandSMSTemplates.get(groupTemplateName + "-" + templateName1 + "-"
						+ type1 + "-" + finalResultApprovalDTO.getErpCampusProgrameMappingId());

				if (!emailandSMSandAdmissionCardErrorDetails.containsKey(finalResultApprovalDTO.getPrograme())) {
					List<FinalResultApprovalTemplatesDTO> finalResultApprovalTemplatesDTOList = new ArrayList<FinalResultApprovalTemplatesDTO>();
					String programe = finalResultApprovalDTO.getPrograme();

					FinalResultApprovalTemplatesDTO dto = new FinalResultApprovalTemplatesDTO();
					dto.setCampus(finalResultApprovalDTO.getCampus());
					FinalResultApprovalStatusDTO emailTemplate = new FinalResultApprovalStatusDTO();
					FinalResultApprovalStatusDTO smsTemplate = new FinalResultApprovalStatusDTO();
					boolean value = false;
					if (erpTemplateDBO == null) {
						emailTemplate.setWaitlisted("Not Present");
						value=true;
					}
					if (erpTemplateDBO1 == null) {
						smsTemplate.setWaitlisted("Not Present");
						value=true;
					}
					FinalResultApprovalStatusDTO AdmissionCardTemplate = new FinalResultApprovalStatusDTO();
					dto.setSmsTemplate(smsTemplate);
					dto.setEmailTemplate(emailTemplate);
					dto.setAdmissionCardTemplate(AdmissionCardTemplate);
					if (value) {
						finalResultApprovalTemplatesDTOList.add(dto);
						emailandSMSandAdmissionCardErrorDetails.put(programe, finalResultApprovalTemplatesDTOList);
					}
				} else {
					List<FinalResultApprovalTemplatesDTO> finalResultApprovalTemplatesDTOList1 = emailandSMSandAdmissionCardErrorDetails.get(finalResultApprovalDTO.getPrograme());
				
					Optional<FinalResultApprovalTemplatesDTO> optionalDTO=finalResultApprovalTemplatesDTOList1.stream().filter(o -> o.getCampus().equals(finalResultApprovalDTO.getCampus())).findAny();
					if(!optionalDTO.isPresent()) {
						FinalResultApprovalTemplatesDTO dto = new FinalResultApprovalTemplatesDTO();
						dto.setCampus(finalResultApprovalDTO.getCampus());
						FinalResultApprovalStatusDTO emailTemplate = new FinalResultApprovalStatusDTO();
						FinalResultApprovalStatusDTO smsTemplate = new FinalResultApprovalStatusDTO();
						if (erpTemplateDBO == null)
							emailTemplate.setWaitlisted("Not Present");
						if (erpTemplateDBO1 == null)
							smsTemplate.setWaitlisted("Not Present");
						FinalResultApprovalStatusDTO AdmissionCardTemplate = new FinalResultApprovalStatusDTO();
						dto.setSmsTemplate(smsTemplate);
						dto.setEmailTemplate(emailTemplate);
						dto.setAdmissionCardTemplate(AdmissionCardTemplate);

						finalResultApprovalTemplatesDTOList1.add(dto);
					}else {
						FinalResultApprovalTemplatesDTO dto = optionalDTO.get();
						FinalResultApprovalStatusDTO emailTemplate = new FinalResultApprovalStatusDTO();
						FinalResultApprovalStatusDTO smsTemplate = dto.getSmsTemplate();
						if (erpTemplateDBO == null)
							emailTemplate.setWaitlisted("Not Present");
						if (erpTemplateDBO1 == null)
							smsTemplate.setWaitlisted("Not Present");
						FinalResultApprovalStatusDTO AdmissionCardTemplate = dto.getAdmissionCardTemplate();
						dto.setSmsTemplate(smsTemplate);
						dto.setEmailTemplate(emailTemplate);
						dto.setAdmissionCardTemplate(AdmissionCardTemplate);
					}
				}
			}
		});
		return emailandSMSandAdmissionCardErrorDetails;
	}

	String notificationCodeString = "";
	int workFlowProcessIdValue = 0;
	String error = "";

	Map<Integer, StudentApplnEntriesDBO> studentApplnEntriesDBOMap = new HashMap<Integer, StudentApplnEntriesDBO>();
	private Object[] convertDtoToDbo(String applicantCurrentProcessStatus,List<FinalResultApprovalDTO> dto1, String userId) {
		List<StudentApplnEntriesDBO> studentApplnEntriesDBO = new ArrayList<StudentApplnEntriesDBO>();
		List<Integer> studentApplnEntriesId = new ArrayList<Integer>();
		dto1.forEach(dto -> {
			studentApplnEntriesId.add(Integer.parseInt(dto.getStudentAdmApplnId()));
		});
		List<StudentApplnEntriesDBO> studentApplnEntries = finalResultApprovalTransaction.getStudentAppilnEntries(studentApplnEntriesId);
		studentApplnEntries.forEach(exist -> {
			studentApplnEntriesDBOMap.put(exist.id, exist);
		});
		Map<String, List<ErpEmailsDBO>> emailMap = new LinkedHashMap<String, List<ErpEmailsDBO>>();
		Map<String, List<ErpSmsDBO>> smsMap = new LinkedHashMap<String, List<ErpSmsDBO>>();
		List<ErpWorkFlowProcessStatusLogDBO> statusLogList = new ArrayList<ErpWorkFlowProcessStatusLogDBO>();
		StringBuffer errorList = new StringBuffer("");
		List<FirstYearStudentDTO> firstYearStudentDTOList = new ArrayList<FirstYearStudentDTO>();

		//-----------------jismy
		/*Map<Pair<Integer,String>, List<AccBatchFeeDBO>> feeTypewiseBatchFeeMap = new HashMap<Pair<Integer,String>, List<AccBatchFeeDBO>>();
		AccFeeDemandCombinationTypeDBO accFeeDemandCombinationTypeDBO = new AccFeeDemandCombinationTypeDBO();
		Map<Integer, List<AccFeeHeadsAccountDBO>> headAccountCampusMap = new HashMap<Integer, List<AccFeeHeadsAccountDBO>>();
		if(applicantCurrentProcessStatus.equalsIgnoreCase("ADM_APPLN_SELECTED_UPLOADED")) {
			boolean isDemandExists =  finalResultApprovalTransaction.isDemandExists(studentApplnEntriesDBOMap.keySet());
			if(isDemandExists) {
				errorList.append("Demand already generated.Please verify.");
			}
			else {
				Set<Integer> batchIdSet = studentApplnEntries.stream().collect(Collectors.groupingBy(s->s.getAcaBatchDBO().getId())).keySet();
				List<AccBatchFeeDBO> batchFeeDBOList = demandSlipGenerationTransaction.generateTuitionFeeDemandNew(batchIdSet);
				
				feeTypewiseBatchFeeMap = batchFeeDBOList.stream().collect(Collectors.groupingBy(f->(Pair.of(f.getAcaBatchDBO().getId(),  f.getFeeCollectionSet() == null ?"common": f.getErpSpecializationDBO()!=null? f.getFeeCollectionSet() + "_" + f.getErpSpecializationDBO().getId():f.getFeeCollectionSet()))));
				
				accFeeDemandCombinationTypeDBO = demandSlipGenerationTransaction.getAccFeeCombinationType("STUDENT_APPLICATION");
				AccFeeHeadsDBO accFeeHeadsDBO = demandSlipGenerationTransaction.generateAdditionalFeeDemand("Admission Processing Fee"); //to add additional fee
				headAccountCampusMap = !Utils.isNullOrEmpty(accFeeHeadsDBO)?
						accFeeHeadsDBO.getAccFeeHeadsAccountList().stream().filter(h->h.getErpCampusDBO()!=null && h.getRecordStatus() == 'A').collect(Collectors.groupingBy(h->h.getErpCampusDBO().getId())): new HashMap<Integer, List<AccFeeHeadsAccountDBO>>();
			}
		}*/
		if(errorList.isEmpty()) {
		/*	Map<Pair<Integer,String>, List<AccBatchFeeDBO>> feeTypewiseBatchFeeMapNew = new HashMap<Pair<Integer,String>, List<AccBatchFeeDBO>>();
			feeTypewiseBatchFeeMapNew.putAll(feeTypewiseBatchFeeMap);
			AccFeeDemandCombinationTypeDBO accFeeDemandCombinationTypeDBONew = accFeeDemandCombinationTypeDBO;
			Map<Integer, List<AccFeeHeadsAccountDBO>> headAccountCampusMapNew = new HashMap<Integer, List<AccFeeHeadsAccountDBO>>();
			headAccountCampusMapNew.putAll(headAccountCampusMap);*/
			
			//-------------
			Tuple erpWorkFlowProcessIdForAdmApplnWaitlisted = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("ADM_APPLN_WAITLISTED");
			Tuple erpWorkFlowProcessIdForAdmApplnNotSelected = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("ADM_APPLN_NOT_SELECTED");
			Tuple erpWorkFlowProcessIdForAdmApplnSelected = commonApiTransaction1.getErpWorkFlowProcessIdbyProcessCode1("ADM_APPLN_SELECTED");
			dto1.forEach(dto -> {
				List<ErpSmsDBO> smsList = new ArrayList<ErpSmsDBO>();
				List<ErpEmailsDBO> emailsList = new ArrayList<ErpEmailsDBO>();
				Set<Integer> approversIdSet = new LinkedHashSet<Integer>();
				if (studentApplnEntriesDBOMap.containsKey(Integer.parseInt(dto.getStudentAdmApplnId()))) {
					StudentApplnEntriesDBO dbo = studentApplnEntriesDBOMap.get(Integer.parseInt(dto.getStudentAdmApplnId()));
					if (dto.getAdmissionCategory() != null && dto.getAdmissionCategory().value != null) {
						ErpAdmissionCategoryDBO erpAdmissionCategoryDBO = new ErpAdmissionCategoryDBO();
						erpAdmissionCategoryDBO.setId(Integer.parseInt(dto.getAdmissionCategory().value));
						dbo.setErpAdmissionCategoryDBO(erpAdmissionCategoryDBO);
					}
					if (!Utils.isNullOrEmpty(dto.getLastDateTimeOfFeePayment())) {
						dbo.setFeePaymentFinalDateTime(dto.getLastDateTimeOfFeePayment());
					}
					
					if (!Utils.isNullOrEmpty(dto.getAdmissionStartDateTime())) {
						dbo.setAdmissionStartDatetime(dto.getAdmissionStartDateTime());
					}
					
					if (!Utils.isNullOrEmpty(dto.getAdmissionEndDateTime())) {
						dbo.setAdmissionFinalDatetime(dto.getAdmissionEndDateTime());
					}
	
					if (dto.getErpWorkFlowProcessCode().equalsIgnoreCase("ADM_APPLN_SELECTED_UPLOADED")) {
						
						ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
						e.id = (Integer) erpWorkFlowProcessIdForAdmApplnSelected.get("erp_work_flow_process_id");
						dbo.applicantCurrentProcessStatus = e;
						dbo.applicationCurrentProcessStatus = e;
						dbo.applicantStatusTime = LocalDateTime.now();
						dbo.applicationStatusTime = LocalDateTime.now();
	
						notificationCodeString = "ADM_APPLN_SELECTED";
						workFlowProcessIdValue = (Integer) erpWorkFlowProcessIdForAdmApplnSelected.get("erp_work_flow_process_id");
						approversIdSet.add(Integer.parseInt(userId));
						
						String groupTemplateName = "Final Result Selected";
						String templateName = "Final Result Selected";
						String type = "Mail";
						ErpTemplateDBO erpTemplateDBO = emailandSMSTemplates.get(groupTemplateName + "-" + templateName
								+ "-" + type + "-" + dto.getErpCampusProgrameMappingId());
	
						String templateName1 = "Final Result Selected";
						String type1 = "SMS";
						ErpTemplateDBO erpTemplateDBO1 = emailandSMSTemplates.get(groupTemplateName + "-" + templateName1
								+ "-" + type1 + "-" + dto.getErpCampusProgrameMappingId());
	
						String s = String.valueOf(workFlowProcessIdValue) + "-" + notificationCodeString + "-" + userId;
						if (!emailMap.containsKey(s)) {
							if (erpTemplateDBO != null) {
								emailsList.add(getEmailDBO(erpTemplateDBO, dbo.id, userId, dto));
								emailMap.put(s, emailsList);
							}
						} else {
							List<ErpEmailsDBO> emails = emailMap.get(s);
							if (erpTemplateDBO != null)
								emails.add(getEmailDBO(erpTemplateDBO, dbo.id, userId, dto));
						}
						if (!smsMap.containsKey(s)) {
							if (erpTemplateDBO1 != null) {
								smsList.add(getSMSDBO(erpTemplateDBO1, dbo.id, userId, dto));
								smsMap.put(s, smsList);
							}
						} else {
							List<ErpSmsDBO> emails = smsMap.get(s);
							if (erpTemplateDBO1 != null)
								emails.add(getSMSDBO(erpTemplateDBO1, dbo.id, userId, dto));
						}
	
						statusLogList.add(getErpWorkFlowProcessStatusLogDBO(Integer.parseInt(dto.getStudentAdmApplnId()),dbo, userId));
						FirstYearStudentDTO firstYearStudentDTO =  new FirstYearStudentDTO();
						firstYearStudentDTO.setStudentApplnEntriesId(dbo.getId());
						firstYearStudentDTO.setBatchId(dbo.getAcaBatchDBO().getId());
						if(!Utils.isNullOrEmpty(dbo.getModeOfStudy())) {
							firstYearStudentDTO.setModeOfStudy(dbo.getModeOfStudy().toString());
						}
						if(!Utils.isNullOrEmpty(dbo.getErpSpecializationDBO())) {
							firstYearStudentDTO.setSpecializationId(dbo.getErpSpecializationDBO().getId());
						}
 						if(!Utils.isNullOrEmpty(dbo.getSelectedDurationDetailDBO()) && !Utils.isNullOrEmpty(dbo.getSelectedDurationDetailDBO().getAcaDurationDBO())) {
							firstYearStudentDTO.setAcaDurationId(dbo.getSelectedDurationDetailDBO().getAcaDurationDBO().getId());
							firstYearStudentDTO.setAcaDurationDetailId(dbo.getSelectedDurationDetailDBO().getId());
						}
						 if(!Utils.isNullOrEmpty(dbo.getErpCampusProgrammeMappingDBO())) {
							 firstYearStudentDTO.setCampusId(dbo.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getId());
						 }
						 if(!Utils.isNullOrEmpty(dbo.getErpAdmissionCategoryDBO())) {
							 firstYearStudentDTO.setAdmissionCategoryId(dbo.getErpAdmissionCategoryDBO().getId());
						 }
						firstYearStudentDTOList.add(firstYearStudentDTO);
						
						//-------------create demand
						/*List<AccBatchFeeDBO> accBatchFeeDBOList = new ArrayList<AccBatchFeeDBO>();
						if(!Utils.isNullOrEmpty(dbo.getErpSpecializationDBO())) {
							accBatchFeeDBOList = feeTypewiseBatchFeeMapNew.get(Pair.of(dbo.getAcaBatchDBO().getId(),"Specialisation wise Fee" + "_" + dbo.getErpSpecializationDBO().getId()));
							
						}
						else if(!Utils.isNullOrEmpty(dbo.getModeOfStudy())) {
							accBatchFeeDBOList = feeTypewiseBatchFeeMapNew.get(Pair.of(dbo.getAcaBatchDBO().getId(), dbo.getModeOfStudy()));
						}
						if(Utils.isNullOrEmpty(accBatchFeeDBOList)) {
							accBatchFeeDBOList = feeTypewiseBatchFeeMapNew.get(Pair.of(dbo.getAcaBatchDBO().getId(),"common")); 
						}
						if(Utils.isNullOrEmpty( accBatchFeeDBOList)) {
							if(errorList.isEmpty()) {
								errorList.append("Fee Definition not found for the batches");
							}
							errorList.append(dbo.getAcaBatchDBO().getBatchName() + ",");
						}*/
						
						if(errorList.isEmpty()){
							/*if(!Utils.isNullOrEmpty( accBatchFeeDBOList)) {
								List<AccBatchFeeDurationsDBO> accBatchFeeDurationsDBOList = accBatchFeeDBOList.get(0).getAccBatchFeeDurationsDBOSet().stream()
										.filter(d->d.getAcaDurationDBO().getId() == dbo.getSelectedDurationDetailDBO().getAcaDurationDBO().getId()).toList();
								AccBatchFeeDurationsDBO accBatchFeeDurationsDBO = new AccBatchFeeDurationsDBO();
								if(!Utils.isNullOrEmpty(accBatchFeeDurationsDBOList)) {
									accBatchFeeDurationsDBO = accBatchFeeDurationsDBOList.get(0);
								}
								if(!Utils.isNullOrEmpty( accBatchFeeDurationsDBO.getAccBatchFeeDurationsDetailDBOSet())) {
									List<AccBatchFeeDurationsDetailDBO> accBatchFeeDurationsDetailDBOList = accBatchFeeDurationsDBO.getAccBatchFeeDurationsDetailDBOSet().stream()
											.filter(a->a.getAcaDurationDetailDBO() == null).toList();
									AccBatchFeeDurationsDetailDBO accBatchFeeDurationsDetailDBO = new AccBatchFeeDurationsDetailDBO();
									if(!Utils.isNullOrEmpty(accBatchFeeDurationsDetailDBOList)) {
										accBatchFeeDurationsDetailDBO = accBatchFeeDurationsDetailDBOList.get(0);
									}
									if(!Utils.isNullOrEmpty(accBatchFeeDurationsDetailDBO.getAccBatchFeeCategoryDBOSet())) {
										AccBatchFeeCategoryDBO accBatchFeeCategoryDBO = new AccBatchFeeCategoryDBO();
										List<AccBatchFeeCategoryDBO> accBatchFeeCategoryDBOList = accBatchFeeDurationsDetailDBO.getAccBatchFeeCategoryDBOSet().stream()
												.filter(c->c.getErpAdmissionCategoryDBO().getId() == dbo.getErpAdmissionCategoryDBO().getId()).toList();
										if(!Utils.isNullOrEmpty(accBatchFeeCategoryDBOList)) {
											accBatchFeeCategoryDBO = accBatchFeeCategoryDBOList.get(0);
										}
										if(!Utils.isNullOrEmpty(accBatchFeeCategoryDBO)) {
											List<AccFeeHeadsAccountDBO> admissionProcessingFeeList = headAccountCampusMapNew.get(dbo.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getId());
											dto.setAdmissionProcessingFeeList(admissionProcessingFeeList);
											dto.setAccBatchFeeCategoryDBO(accBatchFeeCategoryDBO);
											Set<AccFeeDemandDBO> accFeeDemandDBOSet = demandSlipGenertaionHelper.copyAccBatchFeeToDemandDBO(accBatchFeeCategoryDBO, userId, admissionProcessingFeeList, accFeeDemandCombinationTypeDBONew, null, dbo);
											dbo.setAccFeeDemandDBOSet(accFeeDemandDBOSet);
										}
									}
								}
							}*/
							//GenerateAdmissionCardPDF(dto, userId);
						}
						//-------------
	
					} else if (dto.getErpWorkFlowProcessCode().equalsIgnoreCase("ADM_APPLN_NOT_SELECTED_UPLOADED")) {
						
						ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
						e.id = (Integer) erpWorkFlowProcessIdForAdmApplnNotSelected.get("erp_work_flow_process_id");
						dbo.applicantCurrentProcessStatus = e;
						dbo.applicationCurrentProcessStatus = e;
	
						notificationCodeString = "ADM_APPLN_NOT_SELECTED";
						workFlowProcessIdValue = (Integer) erpWorkFlowProcessIdForAdmApplnNotSelected.get("erp_work_flow_process_id");
						approversIdSet.add(Integer.parseInt(userId));
	
						String groupTemplateName = "Final Result Waitlisted";
						String templateName = "Final Result Waitlisted";
						String type = "Mail";
						ErpTemplateDBO erpTemplateDBO = emailandSMSTemplates.get(groupTemplateName + "-" + templateName
								+ "-" + type + "-" + dto.getErpCampusProgrameMappingId());
	
						String templateName1 = "Final Result Waitlisted";
						String type1 = "SMS";
						ErpTemplateDBO erpTemplateDBO1 = emailandSMSTemplates.get(groupTemplateName + "-" + templateName1
								+ "-" + type1 + "-" + dto.getErpCampusProgrameMappingId());
	
						String s = String.valueOf(workFlowProcessIdValue) + "-" + notificationCodeString + "-" + userId;
						if (!emailMap.containsKey(s)) {
							if (erpTemplateDBO != null) {
								emailsList.add(getEmailDBO(erpTemplateDBO, dbo.id, userId, dto));
								emailMap.put(s, emailsList);
							}
						} else {
							List<ErpEmailsDBO> emails = emailMap.get(s);
							if (erpTemplateDBO != null)
								emails.add(getEmailDBO(erpTemplateDBO, dbo.id, userId, dto));
						}
	
						if (!smsMap.containsKey(s)) {
							if (erpTemplateDBO1 != null) {
								smsList.add(getSMSDBO(erpTemplateDBO1, dbo.id, userId, dto));
								smsMap.put(s, smsList);
							}
						} else {
							List<ErpSmsDBO> emails = smsMap.get(s);
							if (erpTemplateDBO1 != null)
								emails.add(getSMSDBO(erpTemplateDBO1, dbo.id, userId, dto));
						}
						statusLogList.add(getErpWorkFlowProcessStatusLogDBO(Integer.parseInt(dto.getStudentAdmApplnId()),dbo, userId));
					} else if (dto.getErpWorkFlowProcessCode().equalsIgnoreCase("ADM_APPLN_WAITLISTED_UPLOADED")) {
						ErpWorkFlowProcessDBO e = new ErpWorkFlowProcessDBO();
						e.id = (Integer) erpWorkFlowProcessIdForAdmApplnWaitlisted.get("erp_work_flow_process_id");
						dbo.applicantCurrentProcessStatus = e;
						dbo.applicationCurrentProcessStatus = e;
	
						notificationCodeString = "ADM_APPLN_WAITLISTED";
						workFlowProcessIdValue = (Integer) erpWorkFlowProcessIdForAdmApplnWaitlisted.get("erp_work_flow_process_id");
	
						String groupTemplateName = "Final Result Waitlisted";
						String templateName = "Final Result Waitlisted";
						String type = "Mail";
						ErpTemplateDBO erpTemplateDBO = emailandSMSTemplates.get(groupTemplateName + "-" + templateName
								+ "-" + type + "-" + dto.getErpCampusProgrameMappingId());
	
						String templateName1 = "Final Result Waitlisted";
						String type1 = "SMS";
						ErpTemplateDBO erpTemplateDBO1 = emailandSMSTemplates.get(groupTemplateName + "-" + templateName1
								+ "-" + type1 + "-" + dto.getErpCampusProgrameMappingId());
	
						String s = String.valueOf(workFlowProcessIdValue) + "-" + notificationCodeString + "-" + userId;
						if (!emailMap.containsKey(s)) {
							if (erpTemplateDBO != null)
								emailsList.add(getEmailDBO(erpTemplateDBO, dbo.id, userId, dto));
							emailMap.put(s, emailsList);
						} else {
							List<ErpEmailsDBO> emails = emailMap.get(s);
							if (erpTemplateDBO != null)
								emails.add(getEmailDBO(erpTemplateDBO, dbo.id, userId, dto));
						}
	
						if (!smsMap.containsKey(s)) {
							if (erpTemplateDBO1 != null) {
								smsList.add(getSMSDBO(erpTemplateDBO1, dbo.id, userId, dto));
								smsMap.put(s, smsList);
							}
						} else {
							List<ErpSmsDBO> emails = smsMap.get(s);
							if (erpTemplateDBO1 != null)
								emails.add(getSMSDBO(erpTemplateDBO1, dbo.id, userId, dto));
						}
	
						statusLogList.add(getErpWorkFlowProcessStatusLogDBO(Integer.parseInt(dto.getStudentAdmApplnId()),dbo, userId));
					}
					dbo.setModifiedUsersId(Integer.parseInt(userId));
					studentApplnEntriesDBO.add(dbo);
				}
			});

			for (Entry<String, List<ErpEmailsDBO>> entry : emailMap.entrySet()) {
				List<ErpEmailsDBO> emailList = entry.getValue();
				List<ErpSmsDBO> smsList = smsMap.get(entry.getKey());
				Set<Integer> approversIdSet = new LinkedHashSet<Integer>();
				approversIdSet.add(Integer.parseInt(entry.getKey().split("-")[2]));
				System.out.println(entry.getKey() + ":" + entry.getValue());
				System.out.println(entry.getKey().split("-")[0]);
			//	commonApiHandler.send_ERP_Notification_SMS_Email_By_UserId(Integer.parseInt(entry.getKey().split("-")[0]),
						//entry.getKey().split("-")[1], approversIdSet, null, smsList, emailList);
			}
		}
		//commonApiTransaction1.saveErpWorkFlowProcessStatusLogDBO1(statusLogList);
		//return studentApplnEntriesDBO;
		Object[] objArr = new Object[4];
		objArr[0] = !Utils.isNullOrEmpty(studentApplnEntriesDBO)? studentApplnEntriesDBO:new ArrayList<StudentApplnEntriesDBO>();
		objArr[1] = errorList.toString();
		objArr[2] = statusLogList;
		objArr[3] = firstYearStudentDTOList;
		return objArr;
	}

	private void GenerateAdmissionCardPDF(FinalResultApprovalDTO dto, String userId) {
		try {
//			// Create Document instance.
//			Document document = new Document();
//
//			// Create OutputStream instance.
//			OutputStream outputStream = new FileOutputStream(new File("D:\\" + dto.getStudentAdmApplnId() + "TestFile.pdf"));
//			
			FinalResultApprovalDTO finalResultApprovalDTO = convertDtoToDbo1(dto, userId, true/*pdf*/);
//			PdfWriter writer = PdfWriter.getInstance(document, outputStream);
//			document.open();
//			InputStream is = new ByteArrayInputStream(finalResultApprovalDTO.getAdmissionCardPreview().getBytes());
//			try {
//				XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
//			} catch (Exception e) {
//				e.printStackTrace();
//			
//			}
//			document.close();
//			outputStream.close();
//			System.out.println("Pdf created successfully.");
			
			
//			String bucketName="erp-uat.server-temp";
//			String content = finalResultApprovalDTO.getAdmissionCardPreview().replaceAll("<br>", "<br/>");
//			aWSS3FileStorageServiceHandler.generatePDFAndUploadFile(content, bucketName,  dto.getStudentAdmApplnId() + ".pdf");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ErpSmsDBO getSMSDBO(ErpTemplateDBO erpTemplateDBO1, Integer entryId, String userId,
			FinalResultApprovalDTO dto) {
		ErpSmsDBO erpSmsDBO = new ErpSmsDBO();
		erpSmsDBO.entryId = entryId;
		ErpUsersDBO erpUsersDBO1 = new ErpUsersDBO();
		erpUsersDBO1.id = Integer.parseInt(userId);
		erpSmsDBO.erpUsersDBO = erpUsersDBO1;
		String msgBody = erpTemplateDBO1.getTemplateContent();
		msgBody = msgBody.replace("[APPLICANT_NAME]", dto.getApplicantName());
		msgBody = msgBody.replace("[APPLICATION_NO]", dto.getApplicationNumber());
		msgBody = msgBody.replace("[STATUS]", dto.getStatus());
		erpSmsDBO.smsContent = msgBody;
		erpSmsDBO.recipientMobileNo = dto.getMobileNumber();
		if(!Utils.isNullOrEmpty(erpTemplateDBO1.getTemplateId()))
			erpSmsDBO.setTemplateId(erpTemplateDBO1.getTemplateId());
		erpSmsDBO.createdUsersId = Integer.parseInt(userId);
		erpSmsDBO.recordStatus = 'A';
		return erpSmsDBO;
	}

	private ErpEmailsDBO getEmailDBO(ErpTemplateDBO erpTemplateDBO, Integer entryId, String userId,FinalResultApprovalDTO dto) {
		ErpEmailsDBO erpEmailsDBO = new ErpEmailsDBO();
		erpEmailsDBO.entryId = entryId;
		ErpUsersDBO erpUsersDBO1 = new ErpUsersDBO();
		erpUsersDBO1.id = Integer.parseInt(userId);
		erpEmailsDBO.erpUsersDBO = erpUsersDBO1;
		String msgBody = erpTemplateDBO.getTemplateContent();
		msgBody = msgBody.replace("[APPLICATION_NO]", dto.getApplicationNumber());
		msgBody = msgBody.replace("[APPLICANT_NAME]", dto.getApplicantName());
		msgBody = msgBody.replace("[STATUS]", dto.getStatus());
		msgBody = msgBody.replace("[PROGRAMME]", dto.getPrograme());
		msgBody = msgBody.replace("[CAMPUS]", dto.getCampus());
		erpEmailsDBO.emailContent = msgBody;
		if(!Utils.isNullOrEmpty(erpTemplateDBO.getMailSubject()))
			erpEmailsDBO.emailSubject = erpTemplateDBO.getMailSubject();
		if(!Utils.isNullOrEmpty(erpTemplateDBO.getMailFromName()))
			erpEmailsDBO.senderName = erpTemplateDBO.getMailFromName();
		erpEmailsDBO.recipientEmail = dto.getEmailId();
		erpEmailsDBO.createdUsersId = Integer.parseInt(userId);
		erpEmailsDBO.recordStatus = 'A';
		return erpEmailsDBO;
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> viewAdmissionCard(Mono<FinalResultApprovalDTO> dto, String userId) {
		var apiResult = new ApiResult<FinalResultApprovalDTO>();
		return dto.map(data -> convertDtoToDbo1(data, userId, false)).flatMap(s -> {
			apiResult.setDto(s);
			apiResult.setSuccess(true);
			return Mono.just(apiResult);
		});
	}

	StudentApplnEntriesDBO studentApplnEntriesDBO =null;
	private FinalResultApprovalDTO convertDtoToDbo1(FinalResultApprovalDTO finalResultApprovalDTO, String userId, boolean isPdf) {
			studentApplnEntriesDBO = studentApplnEntriesDBOMap.get(Integer.parseInt(finalResultApprovalDTO.getStudentAdmApplnId()));
		if(Utils.isNullOrEmpty(studentApplnEntriesDBO))
			studentApplnEntriesDBO = commonAdmissionTransaction.getadmApplnDetails(finalResultApprovalDTO);

		Tuple erpGroupTemplateId = commonApiTransaction1.getTemplateGroupIdByAdmissionCategory(studentApplnEntriesDBO.getErpAdmissionCategoryDBO().getId());
		ErpTemplateDBO erpTemplateDBO = commonApiTransaction1.getErpTemplateByCodeAndTypeAndCampusProgMapping("Print",Integer.parseInt(erpGroupTemplateId.get("templateGroupId").toString()),
				finalResultApprovalDTO.getErpCampusProgrameMappingId());
		
		if (erpTemplateDBO != null) {
			String msgBody = erpTemplateDBO.getTemplateContent();
			if (!Utils.isNullOrEmpty(finalResultApprovalDTO.getApplicationNumber()))
				msgBody = msgBody.replace("[APPLICATION_NO]", finalResultApprovalDTO.getApplicationNumber());
			if (!Utils.isNullOrEmpty(finalResultApprovalDTO.getApplicantName()))
				msgBody = msgBody.replace("[APPLICANT_NAME]", finalResultApprovalDTO.getApplicantName());
			if (!Utils.isNullOrEmpty(finalResultApprovalDTO.getStatus()))
				msgBody = msgBody.replace("[STATUS]", finalResultApprovalDTO.getStatus());
			if (!Utils.isNullOrEmpty(finalResultApprovalDTO.getPrograme()))
				msgBody = msgBody.replace("[SELECTED_PROGRAMME]", finalResultApprovalDTO.getPrograme());
			if (!Utils.isNullOrEmpty(finalResultApprovalDTO.getCampus()))
				msgBody = msgBody.replace("[SELECTED_CAMPUS]", finalResultApprovalDTO.getCampus());
			if (!Utils.isNullOrEmpty(studentApplnEntriesDBO.getStudentPersonalDataDBO()))
				//	msgBody = msgBody.replace("[PHOTO]", studentApplnEntriesDBO.getStudentPersonalDataDBO()
			//		.getStudentPersonalDataAddtnlDBO().getProfilePhotoUrl());
			// msgBody = msgBody.replace("[LOGO]",data.getCampus());
			if (!Utils.isNullOrEmpty(studentApplnEntriesDBO.dob))
				msgBody = msgBody.replace("[DOB]", Utils.convertLocalDateToStringDate1(studentApplnEntriesDBO.dob));
			if (!Utils.isNullOrEmpty(studentApplnEntriesDBO.getStudentPersonalDataDBO()))
			msgBody = msgBody.replace("[NATIONALITY]", studentApplnEntriesDBO.getStudentPersonalDataDBO()
					.getStudentPersonalDataAddtnlDBO().getErpCountryDBO().getNationalityName());
			if (!Utils.isNullOrEmpty(studentApplnEntriesDBO.getStudentPersonalDataDBO()))
			msgBody = msgBody.replace("[RELIGION]", studentApplnEntriesDBO.getStudentPersonalDataDBO()
					.getStudentPersonalDataAddtnlDBO().getErpReligionDBO().getReligionName());
			if (!Utils.isNullOrEmpty(studentApplnEntriesDBO.erpResidentCategoryDBO.getResidentCategoryName()))
				msgBody = msgBody.replace("[DOMICILE_CATEGORY]",
						studentApplnEntriesDBO.erpResidentCategoryDBO.getResidentCategoryName());
			if (!Utils.isNullOrEmpty(studentApplnEntriesDBO.getPersonalEmailId()))
				msgBody = msgBody.replace("[EMAIL]", studentApplnEntriesDBO.getPersonalEmailId());
			//msgBody = msgBody.replace("[ADMISSION_CATEGORY]",Utils.convertLocalDateTimeToStringDate(studentApplnEntriesDBO.getFeePaymentFinalDateTime()));
			//msgBody = msgBody.replace("[LAST_DATE_OF_ADMISSION]",Utils.convertLocalDateTimeToStringDate(studentApplnEntriesDBO.getFeePaymentFinalDateTime()));
			if (!Utils.isNullOrEmpty(studentApplnEntriesDBO.getFeePaymentFinalDateTime()))
			msgBody = msgBody.replace("[LAST_DATE_OF_FEE_PAYMENT]",Utils.convertLocalDateTimeToStringDate(studentApplnEntriesDBO.getFeePaymentFinalDateTime()));
			//msgBody = msgBody.replace("[DATE_APPROVAL_RESULT]", Utils.convertLocalDateToStringDate(LocalDate.now()));
		
//			if(!isPdf) {
//				AccBatchFeeCategoryDBO accBatchFeeCategoryDBO = finalResultApprovalTransaction.getStudentwiseDemand(studentApplnEntriesDBO);
//				finalResultApprovalDTO.setAccBatchFeeCategoryDBO(accBatchFeeCategoryDBO);
//				AccFeeHeadsDBO accFeeHeadsDBO = demandSlipGenerationTransaction.generateAdditionalFeeDemand("Admission Processing Fee"); //to add additional fee
//				List<AccFeeHeadsAccountDBO> headAccountCampusList = accFeeHeadsDBO.getAccFeeHeadsAccountList().stream().filter(h->h.getErpCampusDBO().getId() == studentApplnEntriesDBO.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getId() && h.getRecordStatus() == 'A').toList();
//				finalResultApprovalDTO.setAdmissionProcessingFeeList(headAccountCampusList);
//			}
			//getFeeDetails(finalResultApprovalDTO, userId);
		
//			if(!Utils.isNullOrEmpty(finalResultApprovalDTO.getAccBatchFeeCategoryDBO()) && !Utils.isNullOrEmpty(finalResultApprovalDTO.getAccBatchFeeCategoryDBO().getAccBatchFeeHeadDBOSet())) {
//				StringBuilder htmlBuilder = new StringBuilder();
//				htmlBuilder.append("<html>");
//				htmlBuilder.append("<head><title></title></head>");
//				htmlBuilder.append("<br></br>"
//						+ " <table border='1'  style='border-collapse: collapse;width:100%; ' >"
//						+ "<tr style='height:50px'>"
//						+ "<td style='border: 1px solid;text-align: center;'>Head</td>"
//						);
//				List<AccBatchFeeAccountDBO> accListHead = finalResultApprovalDTO.getAccBatchFeeCategoryDBO().getAccBatchFeeHeadDBOSet().stream().toList().get(0).getAccBatchFeeAccountDBOSet().stream().sorted(Comparator.comparing(o -> o.getAccAccountsDBO().getId())).collect(Collectors.toList());
//				accListHead.forEach(tempAcc->{
//					htmlBuilder.append("<td style='border: 1px solid;text-align: center;'>" + tempAcc.getAccAccountsDBO().getAccountNo() + "</td>");	
//				});
//				htmlBuilder.append("<td style='border: 1px solid;text-align: center;'>Scholarship</td>");
//				htmlBuilder.append("</tr>");
//				if(!Utils.isNullOrEmpty(finalResultApprovalDTO.getAccBatchFeeCategoryDBO().getAccBatchFeeHeadDBOSet())) {
//					finalResultApprovalDTO.getAccBatchFeeCategoryDBO().getAccBatchFeeHeadDBOSet().forEach(h->{
//						htmlBuilder.append("<tr style='border-top:1px solid black; border-bottom:1px solid black;height:50px'>");
//						htmlBuilder.append("<td style='border: 1px solid;text-align: center;'>");
//						htmlBuilder.append(h.getAccFeeHeadsDBO().getHeading());
//						htmlBuilder.append("</td>");
//						List<AccBatchFeeAccountDBO> accList = h.getAccBatchFeeAccountDBOSet().stream().sorted(Comparator.comparing(o -> o.getAccAccountsDBO().getId())).collect(Collectors.toList());
//						accList.forEach(acc->{
//							htmlBuilder.append("<td  style='border: 1px solid;text-align: center;'>" + (Utils.isNullOrEmpty(acc.getErpCurrencyDBO().getCurrencyCode())?acc.getErpCurrencyDBO().getCurrencyCode():"")
//									+ " " + acc.getFeeAccountAmount() +"</td>");
//							if(!Utils.isNullOrEmpty(acc.getFeeScholarshipAmount())) {
//								htmlBuilder.append("<td  style='border: 1px solid;text-align: center;'>" + acc.getFeeScholarshipAmount() +"</td>");
//							}
//						});
//						htmlBuilder.append("</tr>")	;
//					});
//				}
//				htmlBuilder.append("</table>");
//				if(!Utils.isNullOrEmpty(finalResultApprovalDTO.getAdmissionProcessingFeeList())) {
//					htmlBuilder.append(" <table border='0'>");
//					finalResultApprovalDTO.getAdmissionProcessingFeeList().forEach(additional->{
//						htmlBuilder.append("<tr style='height:50px' >");
//						htmlBuilder.append("<td style='text-align: center;'>");
//						htmlBuilder.append(additional.getAccFeeHeadsDBO().getHeading() + ": ");
//						htmlBuilder.append("</td>");
//						htmlBuilder.append("<td style='text-align: center;'>" + additional.getAmount() +"</td>");
//						htmlBuilder.append("</tr>")	;
//					});
//					htmlBuilder.append("</table>");
//				}
//				htmlBuilder.append("</html>");
//				String html = htmlBuilder.toString();
//				//msgBody = msgBody.replace("[FEES_DETAILS]", html);
//			}
			
			if(!isPdf) {
				finalResultApprovalDTO.setAdmissionCardPreview(Jsoup.parse(msgBody).html());
				finalResultApprovalDTO.setAccBatchFeeCategoryDBO(null);
				finalResultApprovalDTO.setAdmissionProcessingFeeList(null);
			}
			else {
				finalResultApprovalDTO.setAdmissionCardPreview(Jsoup.parse(msgBody).html());
			}
			// finalResultApprovalDTO.setAdmissionCardPreview(msgBody);
			finalResultApprovalDTO.setShowAdmissionCard("true");
		} else {
			finalResultApprovalDTO.setAdmissionCardPreview("Template Not Defined.");
			finalResultApprovalDTO.setShowAdmissionCard("false");
		}
		return finalResultApprovalDTO;
	}

	@SuppressWarnings("rawtypes")
	public Mono<ApiResult> viewFeeDetails(Mono<FinalResultApprovalDTO> dto, String userId) {
		var apiResult = new ApiResult<FinalResultApprovalDTO>();
		return dto.map(data -> convertToFeeDetailsDTO(data)).flatMap(s -> {
			apiResult.setDto(s);
			apiResult.setSuccess(true);
			return Mono.just(apiResult);
		});
	}

//	private FinalResultApprovalDTO getFeeDetails(FinalResultApprovalDTO data, String userId) {
//		List<AccBatchFeeDBO> accBatchFeeDBOList = finalResultApprovalTransaction.getAccountFeeDetails(data.getAdmissionCategory().value, data.getAcaBatchId());
//		List<AdmProgrammeSettingsDBO> admProgrammeSettingsDBOList = finalResultApprovalTransaction.getAdmProgrammeSettings(data.getProgrameId());
//		List<AccBatchFeeDTO> accBatchFeeDTOList = new ArrayList<AccBatchFeeDTO>();
//		AccBatchFeeDTO accBatchFeeDTO = null;
//		for (AccBatchFeeDBO accBatchFeeDBO : accBatchFeeDBOList) {
//			Optional<AccBatchFeeDTO> accBatchFeeDTO1 = Optional.empty();
//			if (accBatchFeeDBO.getFeeCollectionSet() != null) {
//				var list = accBatchFeeDTOList.stream().filter(s -> s.getFeeCollectionSet() != null)
//						.collect(Collectors.toList());
//				if (!Utils.isNullOrEmpty(list)) {
//					accBatchFeeDTO1 = list.stream().filter(s -> s.getFeeCollectionSet().getLabel()
//							.equalsIgnoreCase(accBatchFeeDBO.getFeeCollectionSet())).findAny();
//				}
//			} else if (accBatchFeeDBO.getErpSpecializationDBO() != null
//					&& accBatchFeeDBO.getErpSpecializationDBO().specializationName != null) {
//
//				var list = accBatchFeeDTOList.stream().filter(s -> s.getErpSpecializationDTO() != null)
//						.collect(Collectors.toList());
//				if (!Utils.isNullOrEmpty(list)) {
//					accBatchFeeDTO1 = list.stream()
//							.filter(s -> s.getErpSpecializationDTO().getLabel()
//									.equalsIgnoreCase(accBatchFeeDBO.getErpSpecializationDBO().getSpecializationName())).findAny();
//				}
//			} else {
//				var list = accBatchFeeDTOList.stream()
//						.filter(s -> (s.getErpSpecializationDTO() == null && s.getFeeCollectionSet() == null))
//						.collect(Collectors.toList());
//				if (!Utils.isNullOrEmpty(list)) {
//					accBatchFeeDTO1 = list.stream()
//							.filter(s -> (s.getErpSpecializationDTO() == null && s.getFeeCollectionSet() == null)).findAny();
//				}
//			}
//			if (accBatchFeeDTO1.isPresent()) {
//				accBatchFeeDTO = accBatchFeeDTO1.get();
//			} else
//				accBatchFeeDTO = new AccBatchFeeDTO();
//
//			if (!Utils.isNullOrEmpty(accBatchFeeDBO)) {
//				accBatchFeeDTO.setId(accBatchFeeDBO.getId());
//				if (!Utils.isNullOrEmpty(accBatchFeeDBO.getFeeCollectionSet())) {
//					SelectDTO feeCollectionSet = new SelectDTO();
//					feeCollectionSet.setValue(accBatchFeeDBO.getFeeCollectionSet());
//					feeCollectionSet.setLabel(accBatchFeeDBO.getFeeCollectionSet());
//					accBatchFeeDTO.setFeeCollectionSet(feeCollectionSet);
//				} else {
//					SelectDTO feeCollectionSet = new SelectDTO();
//					feeCollectionSet.setValue("NULL");
//					feeCollectionSet.setLabel("NULL");
//					accBatchFeeDTO.setFeeCollectionSet(feeCollectionSet);
//				}
//				List<AccBatchFeeDurationsDTO> accBatchFeeDurationsDTOList = new ArrayList<AccBatchFeeDurationsDTO>();
//				if (!Utils.isNullOrEmpty(accBatchFeeDBO.getAccBatchFeeDurationsDBOSet())) {
//					accBatchFeeDBO.getAccBatchFeeDurationsDBOSet().stream()
//							.filter(durationDbo -> durationDbo.getRecordStatus() == 'A').collect(Collectors.toList())
//							.forEach(durationDbo -> {
//								AccBatchFeeDurationsDTO accBatchFeeDurationsDTO = new AccBatchFeeDurationsDTO();
//								
//								//accBatchFeeDurationsDTO.setId(durationDbo.getId());
//								//SelectDTO acaDurationDetailDTO = new SelectDTO();
//								//accBatchFeeDurationsDTO.setAcaDurationDetailDTO(acaDurationDetailDTO);
//								/*if (!Utils.isNullOrEmpty(durationDbo.getAcaDurationDetailDBO().getAcaSessionDBO())) {
//									accBatchFeeDurationsDTO.setYearNo(
//											durationDbo.getAcaDurationDetailDBO().getAcaSessionDBO().getYearNumber());
//								}*/
//							    List<AccBatchFeeDurationsDetailsDTO> accBatchFeeDurationsDetailsDTOList = new ArrayList<AccBatchFeeDurationsDetailsDTO>();
//								durationDbo.getAccBatchFeeDurationsDetailDBOSet().forEach(durDet->{
//									AccBatchFeeDurationsDetailsDTO accBatchFeeDurationsDetailsDTO = new AccBatchFeeDurationsDetailsDTO();
//									if (!Utils.isNullOrEmpty(durDet.getAccBatchFeeCategoryDBOSet())) {
//									List<AccBatchFeeCategoryDTO> accBatchFeeCategoryDTOList = new ArrayList<AccBatchFeeCategoryDTO>();
//									durDet.getAccBatchFeeCategoryDBOSet().stream()
//											.filter(category -> category.getRecordStatus() == 'A')
//											.collect(Collectors.toList()).forEach(category -> {
//												AccBatchFeeCategoryDTO accBatchFeeCategoryDTO = new AccBatchFeeCategoryDTO();
//												accBatchFeeCategoryDTO.setId(category.getId());
//												SelectDTO erpAdmissionCategoryDTO = new SelectDTO();
//												erpAdmissionCategoryDTO.setValue(Integer.toString(category.getErpAdmissionCategoryDBO().getId()));
//												erpAdmissionCategoryDTO.setLabel(category.getErpAdmissionCategoryDBO().getAdmissionCategoryName());
//												accBatchFeeCategoryDTO.setErpAdmissionCategoryDTO(erpAdmissionCategoryDTO);
//
//												if (!Utils.isNullOrEmpty(category.getAccBatchFeeHeadDBOSet())) {
//													List<AccBatchFeeHeadDTO> accBatchFeeHeadDTOList = new ArrayList<AccBatchFeeHeadDTO>();
//													category.getAccBatchFeeHeadDBOSet().stream()
//															.filter(head -> head.getRecordStatus() == 'A')
//															.collect(Collectors.toList()).forEach(head -> {
//																AccBatchFeeHeadDTO accBatchFeeHeadDTO = new AccBatchFeeHeadDTO();
//																accBatchFeeHeadDTO.setId(head.getId());
//																SelectDTO accFeeHeadDTO = new SelectDTO();
//																accFeeHeadDTO.setValue(Integer.toString(head.getAccFeeHeadsDBO().getId()));
//																accFeeHeadDTO.setLabel(head.getAccFeeHeadsDBO().getHeading());
//																accBatchFeeHeadDTO.setAccFeeHeadsDTO(accFeeHeadDTO);
//
//																if (!Utils.isNullOrEmpty(
//																		head.getAccBatchFeeAccountDBOSet())) {
//																	List<AccBatchFeeAccountDTO> accBatchFeeAccountDTOList = new ArrayList<AccBatchFeeAccountDTO>();
//																	head.getAccBatchFeeAccountDBOSet().stream().filter(
//																			account -> account.getRecordStatus() == 'A')
//																			.collect(Collectors.toList())
//																			.forEach(account -> {
//																				AccBatchFeeAccountDTO accBatchFeeAccountDTO = new AccBatchFeeAccountDTO();
//																				accBatchFeeAccountDTO.setId(account.getId());
//																				SelectDTO accAccountDTO = new SelectDTO();
//																				accAccountDTO.setValue(Integer.toString(account.getAccAccountsDBO().getId()));
//																				accAccountDTO.setLabel(account.getAccAccountsDBO().getAccountNo());
//																				accBatchFeeAccountDTO.setAccAccountsDTO(accAccountDTO);
//																				SelectDTO erpCurrencyDTO = new SelectDTO();
//																				erpCurrencyDTO.setValue(Integer.toString(account.getErpCurrencyDBO().getId()));
//																				erpCurrencyDTO.setLabel(account.getErpCurrencyDBO().getCurrencyCode());
//																				accBatchFeeAccountDTO.setErpCurrencyDTO(erpCurrencyDTO);
//																				if (!Utils.isNullOrEmpty(account.getFeeAccountAmount())) {
//																					accBatchFeeAccountDTO.setFeeAccountAmount(account.getFeeAccountAmount());
//																				}
//																				if (!Utils.isNullOrEmpty(account.getFeeScholarshipAmount())) {
//																					accBatchFeeAccountDTO.setFeeScholarshipAmount(account.getFeeScholarshipAmount());
//																				}
//																				accBatchFeeAccountDTOList.add(accBatchFeeAccountDTO);
//																			});
//																	List<AccBatchFeeAccountDTO> accBatchFeeAccountDTOListSorted = accBatchFeeAccountDTOList
//																			.stream().sorted(Comparator.comparing(o -> o.getAccAccountsDTO().getLabel())).collect(Collectors.toList());
//																	accBatchFeeHeadDTO.setAccBatchFeeAccountDTOList(accBatchFeeAccountDTOListSorted);
//																}
//																accBatchFeeHeadDTOList.add(accBatchFeeHeadDTO);
//															});
//													accBatchFeeCategoryDTO.setAccBatchFeeHeadDTOList(accBatchFeeHeadDTOList);
//												}
//												accBatchFeeCategoryDTOList.add(accBatchFeeCategoryDTO);
//											});
//										List<AccBatchFeeCategoryDTO> accBatchFeeCategoryDTOListSorted = accBatchFeeCategoryDTOList
//												.stream().sorted(Comparator.comparing(o -> o.getErpAdmissionCategoryDTO().getLabel())).collect(Collectors.toList());
//										accBatchFeeDurationsDetailsDTO.setAccBatchFeeCategoryDTOList(accBatchFeeCategoryDTOListSorted);
//										accBatchFeeDurationsDetailsDTOList.add(accBatchFeeDurationsDetailsDTO);
//									}
//								});
//								accBatchFeeDurationsDTO.setAccBatchFeeDurationsDetailsDTOList(accBatchFeeDurationsDetailsDTOList);
//								accBatchFeeDurationsDTOList.add(accBatchFeeDurationsDTO);
//							});
//				}
//				accBatchFeeDTO.setAccBatchFeeDurationsDTOList(accBatchFeeDurationsDTOList);
//			}
//			if (!accBatchFeeDTO1.isPresent())
//				accBatchFeeDTOList.add(accBatchFeeDTO);
//		}
//		data.setAccBatchFeeDTOList(accBatchFeeDTOList);
//		String feePaymentModes = null;
////		for (AdmProgrammeSettingsDBO admProgrammeSettings : admProgrammeSettingsDBOList) {
////			if (admProgrammeSettings.getOnlinePaymentModes() != null
////					&& admProgrammeSettings.getOnlinePaymentModes().equalsIgnoreCase("O")) {
////				if (feePaymentModes == null)
////					feePaymentModes = "Online";
////				else
////					feePaymentModes = feePaymentModes + " , " + "Online";
////			} else if (admProgrammeSettings.getOnlinePaymentModes() != null
////					&& admProgrammeSettings.getOnlinePaymentModes().equalsIgnoreCase("N")) {
////				if (feePaymentModes == null)
////					feePaymentModes = "NEFT";
////				else
////					feePaymentModes = feePaymentModes + " , " + "NEFT";
////			} else if (admProgrammeSettings.getOnlinePaymentModes() != null
////					&& admProgrammeSettings.getOnlinePaymentModes().equalsIgnoreCase("C")) {
////				if (feePaymentModes == null)
////					feePaymentModes = "Cash";
////				else
////					feePaymentModes = feePaymentModes + " , " + "Cash";
////			}
////		}
//		data.setFeePaymentModes(feePaymentModes);
//		return data;
//	}

	public Flux<SelectDTO> getFinalResultApprovalStatusList() {
		return finalResultApprovalTransaction.getFinalResultApprovalStatusList().flatMapMany(Flux::fromIterable).map(this::convertDBOToDTO);
	}

	public SelectDTO convertDBOToDTO(ErpWorkFlowProcessDBO dbo) {
		SelectDTO dto = new SelectDTO();
		if (!Utils.isNullOrEmpty(dbo)) {
			dto.setValue(String.valueOf(dbo.getProcessCode()));
			if (dbo.processCode.equalsIgnoreCase("ADM_APPLN_SELECTED_UPLOADED"))
				dto.setLabel("Selected");
			else if (dbo.processCode.equalsIgnoreCase("ADM_APPLN_NOT_SELECTED_UPLOADED"))
				dto.setLabel("Not Selected");
			else if (dbo.processCode.equalsIgnoreCase("ADM_APPLN_WAITLISTED_UPLOADED"))
				dto.setLabel("Waitlisted");
			else if (dbo.processCode.equalsIgnoreCase("ADM_APPLN_SELECTED"))
				dto.setLabel("Selected & Approved");
		}
		return dto;
	}
	
	private FinalResultApprovalDTO convertToFeeDetailsDTO(FinalResultApprovalDTO finalResultApprovalDTO) {
		StudentApplnEntriesDBO studentApplnEntriesDBO = commonAdmissionTransaction.getadmApplnDetails(finalResultApprovalDTO);
		//List<Integer> admProgrammeSettingsDBOList=null;stream().filter(a->!Utils.isNullOrEmpty(a.getSapCode())).map(a->a.getSapCode()).collect(Collectors.toList());
		List<Integer> admProgrammeSettingsDBOList = finalResultApprovalTransaction.getAdmProgrammeSettings(finalResultApprovalDTO.getProgrameId()).stream().map(a->a.getId()).collect(Collectors.toList());
		String s=finalResultApprovalTransaction.getFeeModes(admProgrammeSettingsDBOList);
		finalResultApprovalDTO.setFeePaymentModes(s);
		
			AccBatchFeeCategoryDBO accBatchFeeCategoryDBO = finalResultApprovalTransaction.getStudentwiseDemand(studentApplnEntriesDBO);
			//finalResultApprovalDTO.setAccBatchFeeCategoryDBO(accBatchFeeCategoryDBO);
			AccFeeHeadsDBO accFeeHeadsDBO = demandSlipGenerationTransaction.generateAdditionalFeeDemand("Admission Processing Fee"); //to add additional fee
			List<AccFeeHeadsAccountDBO> headAccountCampusList = accFeeHeadsDBO.getAccFeeHeadsAccountList().stream().filter(h->h.getErpCampusDBO().getId() == studentApplnEntriesDBO.getErpCampusProgrammeMappingDBO().getErpCampusDBO().getId() && h.getRecordStatus() == 'A').toList();
			//finalResultApprovalDTO.setAdmissionProcessingFeeList(headAccountCampusList);
		 
			List<AccBatchFeeHeadDTO> accBatchFeeList = new ArrayList<AccBatchFeeHeadDTO>();
			if(!Utils.isNullOrEmpty(accBatchFeeCategoryDBO)) {
				if(!Utils.isNullOrEmpty(accBatchFeeCategoryDBO.getAccBatchFeeHeadDBOSet())) {
					//accBatchFeeCategoryDBO.getAccBatchFeeHeadDBOSet().forEach(h->{
						for (AccBatchFeeHeadDBO h : accBatchFeeCategoryDBO.getAccBatchFeeHeadDBOSet()) {
						AccBatchFeeHeadDTO accBatchFeeHeadDTO = new AccBatchFeeHeadDTO();
						SelectDTO accHeadsDTO = new SelectDTO();
						accHeadsDTO.setValue(Integer.toString(h.getAccFeeHeadsDBO().getId()));
						accHeadsDTO.setLabel(h.getAccFeeHeadsDBO().getHeading());
						accBatchFeeHeadDTO.setAccFeeHeadsDTO(accHeadsDTO);
						List<AccBatchFeeAccountDTO> accBatchFeeAccountDTOList = new ArrayList<AccBatchFeeAccountDTO>();
						if(!Utils.isNullOrEmpty(h.getAccBatchFeeAccountDBOSet())){
							//h.getAccBatchFeeAccountDBOSet().forEach(acc->{
							for (AccBatchFeeAccountDBO acc : h.getAccBatchFeeAccountDBOSet()) {
								AccBatchFeeAccountDTO accBatchFeeAccountDTO = new AccBatchFeeAccountDTO();
								SelectDTO accAccountsDTO = new SelectDTO();
								accAccountsDTO.setValue(Integer.toString(acc.getAccAccountsDBO().getId()));
								accAccountsDTO.setLabel(acc.getAccAccountsDBO().getAccountNo());
								accBatchFeeAccountDTO.setAccAccountsDTO(accAccountsDTO);
								accBatchFeeAccountDTO.setFeeAccountAmount(acc.getFeeAccountAmount());
								accBatchFeeAccountDTO.setFeeScholarshipAmount(acc.getFeeScholarshipAmount());
								SelectDTO erpCurrencyDTO = new SelectDTO();
								erpCurrencyDTO.setValue(Integer.toString(acc.getErpCurrencyDBO().getId()));
								erpCurrencyDTO.setLabel(acc.getErpCurrencyDBO().getCurrencyCode());
								accBatchFeeAccountDTO.setErpCurrencyDTO(erpCurrencyDTO);
								accBatchFeeAccountDTOList.add(accBatchFeeAccountDTO);
							}
							//});
						}
						accBatchFeeHeadDTO.setAccBatchFeeAccountDTOList(accBatchFeeAccountDTOList);
						accBatchFeeList.add(accBatchFeeHeadDTO);
						}
					//});
				}
			}
			if(!Utils.isNullOrEmpty(headAccountCampusList)) {
				List<AccBatchFeeAccountDTO> accBatchFeeAccountDTOList = new ArrayList<AccBatchFeeAccountDTO>();
				//headAccountCampusList.forEach(additional->{
				for (AccFeeHeadsAccountDBO additional : headAccountCampusList) {
				
					AccBatchFeeAccountDTO accBatchFeeAccountDTO = new AccBatchFeeAccountDTO();
					AccBatchFeeHeadDTO accBatchFeeHeadDTO = new AccBatchFeeHeadDTO();
					 SelectDTO accHeadsDTO = new SelectDTO();
					 accHeadsDTO.setLabel(additional.getAccFeeHeadsDBO().getHeading());
					 accBatchFeeHeadDTO.setAccFeeHeadsDTO(accHeadsDTO);
					 SelectDTO erpCurrencyDTO = new SelectDTO();
					 erpCurrencyDTO.setValue("");
					 erpCurrencyDTO.setLabel("");
					 accBatchFeeAccountDTO.setErpCurrencyDTO(erpCurrencyDTO);
					 SelectDTO accAccountsDTO = new SelectDTO();
					 accAccountsDTO.setValue(Integer.toString(additional.getAccAccountsDBO().getId()));
					 accAccountsDTO.setLabel(additional.getAccAccountsDBO().getAccountNo());
				     accBatchFeeAccountDTO.setAccAccountsDTO(accAccountsDTO);
				     accBatchFeeAccountDTO.setFeeScholarshipAmount(new BigDecimal(0));
				     accBatchFeeAccountDTO.setFeeAccountAmount(additional.getAmount());
					 accBatchFeeAccountDTOList.add(accBatchFeeAccountDTO);
					 accBatchFeeHeadDTO.setAccBatchFeeAccountDTOList(accBatchFeeAccountDTOList);
					 accBatchFeeList.add(accBatchFeeHeadDTO);
				}
				//});
				 
			}
			finalResultApprovalDTO.setAccBatchFeeHeadDTOList(accBatchFeeList);

		 
		return finalResultApprovalDTO;
	}
	public  ErpWorkFlowProcessStatusLogDBO getErpWorkFlowProcessStatusLogDBO(int entryId, StudentApplnEntriesDBO dbo,String userId) {
		ErpWorkFlowProcessStatusLogDBO erpWorkFlowProcessStatusLogDBO = new ErpWorkFlowProcessStatusLogDBO();
		erpWorkFlowProcessStatusLogDBO.entryId = entryId;
		ErpWorkFlowProcessDBO e1 = new ErpWorkFlowProcessDBO();
		if (!Utils.isNullOrEmpty(dbo.applicantCurrentProcessStatus.id))
			e1.id = dbo.applicantCurrentProcessStatus.id;
		else
			e1.id = dbo.applicantCurrentProcessStatus.id;
		erpWorkFlowProcessStatusLogDBO.erpWorkFlowProcessDBO = e1;
		erpWorkFlowProcessStatusLogDBO.createdUsersId = Integer.parseInt(userId);
		erpWorkFlowProcessStatusLogDBO.recordStatus = 'A';
		return erpWorkFlowProcessStatusLogDBO;

	}
	public ApiResult generateDemandForFirstYearStudents(List<FirstYearStudentDTO> firstYearStudentDTOList, Integer userId) throws IOException, InterruptedException {
		ApiResult apiResult = new ApiResult();
		String failureMessage = "";
		if (!Utils.isNullOrEmpty(firstYearStudentDTOList)) {
				Gson gson = new Gson();
				String jsonStr = gson.toJson(firstYearStudentDTOList);
				String jwsObjectString = commonAccountHandler.createJwsObject(jsonStr);
				String applicationNoGenerationUrl = redisVaultKeyConfig.getServiceKeys(Constants.PAYMENT_SERVICE_URL).concat(ServiceURL.FIRST_YEAR_DEMAND_GENERATION_URL);
				if (!Utils.isNullOrEmpty(jwsObjectString)) {
					ByteBuffer buffer = ByteBuffer.wrap(jwsObjectString.getBytes(StandardCharsets.UTF_8));
					HttpClient httpClient = HttpClient.newHttpClient();
					// Create the request with the buffer as the body
					HttpRequest request = HttpRequest.newBuilder()
							.uri(URI.create(applicationNoGenerationUrl)) // Replace with your API endpoint
							.headers("Content-Type", "application/octet-stream", Constants.HEADER_JWT_USER_ID,Integer.toString(userId))
							.POST(HttpRequest.BodyPublishers.ofByteArray(buffer.array()))
							.build();
					HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

					// Process the response
					String responseBody = new String(response.body(), StandardCharsets.UTF_8);
					System.out.println("Received response: " + responseBody);
					if (response.statusCode() == 200) {
						System.out.println("success response");
						JwsObjectDTO jwsObjectDTO = commonApiHandler.verifySignedRequest(responseBody);
						List<DemandResponseDTO> demandResponseList =  commonApiHandler.convertJwsObjectToDTO(jwsObjectDTO, DemandResponseDTO.class);
						if(!Utils.isNullOrEmpty(demandResponseList)){
							System.out.println(demandResponseList.size());
							apiResult.setSuccess(true);
							apiResult.setDtoList(demandResponseList);
							//List<DemandResponseDTO> demandResponseDTOList = demandResponse.getDtoList();
						}
						//ApiResult demandApiResult = response.body();
					}
					else{
						apiResult.setSuccess(false);
						apiResult.setFailureMessage(responseBody);
					}

					/*HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
					if (response.statusCode() == 200) {
 						ApiResult<FirstYearStudentDTO> demandApiResult = commonAccountHandler.verifyPaymentStatusRequest(response.body());
						if(demandApiResult.isSuccess()){
							if(!Utils.isNullOrEmpty(demandApiResult.dto)) {
								ObjectMapper objectMapper = new ObjectMapper(); // Initialize Jackson ObjectMapper
								List<FirstYearStudentDTO> firstYearStudentResponseDTOList = objectMapper.convertValue(demandApiResult.getDtoList(), ArrayList.class);
								if (!Utils.isNullOrEmpty(firstYearStudentResponseDTOList)) {
									//applicationSubmissionStatusDTO.setEntryNo(applicationSubmissionResponseDTO.getEntryNo());
									//applicationSubmissionStatusDTO.setProgrammeName(applicationSubmissionResponseDTO.getProgrammeName());
									//applicationSubmissionStatusDTO.setPaymentStatus('S');
									//apiResult.dto = applicationSubmissionStatusDTO;
									apiResult.success = true;
									//apiResult.setSuccessMessage("Your payment is successful and application no " +
									//		paymentResponseDTO.getEntryNo() + " is generated successfully. Please note the payment acknowledgement and application print available in your dashboard. Click here to open the dashboard");
								} else {
									apiResult.success = false;
									apiResult.setFailureMessage(failureMessage);
								}
							}
							else{
								apiResult.success = false;
								apiResult.setFailureMessage(failureMessage);
							}
						}
						else{
							apiResult.success = false;
							if(!Utils.isNullOrEmpty(demandApiResult.getFailureMessage())){
								apiResult.setFailureMessage(demandApiResult.getFailureMessage());
							}
							else {
								apiResult.setFailureMessage(failureMessage);
							}
							//demandApiResultapplicationSubmissionStatusDTO.setPaymentStatus('N');
							//apiResult.dto = applicationSubmissionStatusDTO;
						}
					}
					else{
						//applicationSubmissionStatusDTO.setPaymentStatus('N');
						//apiResult.dto = applicationSubmissionStatusDTO;
						apiResult.success = false;
						apiResult.setFailureMessage(failureMessage);
					}*/
				}  else {
					//applicationSubmissionStatusDTO.setPaymentStatus('N');
					//apiResult.dto = applicationSubmissionStatusDTO;
					apiResult.success = false;
					apiResult.setFailureMessage(failureMessage);
				}

			}
		return apiResult;
	}

	public Mono<ApiResult<FinalResultApprovalDTO>> getFinalResultApprovalStatusListCount() {
		var apiResult = new ApiResult<FinalResultApprovalDTO>();
		FinalResultApprovalDTO dto=new FinalResultApprovalDTO();
		apiResult.dto=finalResultApprovalTransaction.getFinalResultApprovalStatusListCount(dto);
		apiResult.success=true;
		return Mono.just(apiResult);
	}
}
