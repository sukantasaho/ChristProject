package com.christ.erp.services.helpers.employee.letter;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.letter.EmpLetterRequestDBO;
import com.christ.erp.services.dbobjects.employee.letter.EmpLetterRequestReasonDBO;
import com.christ.erp.services.dbobjects.employee.letter.EmpLetterRequestTypeDBO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.letter.LetterRequestDTO;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.employee.letter.LetterRequestTransaction;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Tuple;

public class LetterRequestHandler {
	private static volatile LetterRequestHandler letterRequestHandler = null;
	LetterRequestTransaction letterRequestTransaction = LetterRequestTransaction.getInstance();
	CommonApiTransaction commonApiTransaction = CommonApiTransaction.getInstance();

	public static LetterRequestHandler getInstance() {
		if(letterRequestHandler==null) {
			letterRequestHandler = new LetterRequestHandler();
		}
		return letterRequestHandler;
	}

	public ApiResult<ModelBaseDTO> saveOrUpdate(LetterRequestDTO data, String userId,ApiResult<ModelBaseDTO> apiResult) throws Exception {
		boolean flag=false;
		boolean isSaveOrUpdated =false;
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		List<EmpLetterRequestDBO> list = letterRequestTransaction.getDuplicate(data);
		if(!Utils.isNullOrEmpty(list)) {
			for (EmpLetterRequestDBO bo : list) {
				if (Utils.isNullOrWhitespace(data.id) == true && data.id == null || data.id.isEmpty()) {
					result.failureMessage = " Duplicate entry for " + " " + bo.empLetterRequestTypeDBO.letterTypeName +  " " + " : " + " " + bo.empLetterRequestReasonDBO.letterRequestReasonName ;
				}
				else if(!data.id.equalsIgnoreCase(String.valueOf(bo.id)) && Utils.isNullOrWhitespace(data.id) == false) {
					result.failureMessage = " Duplicate entry for " + " " + bo.empLetterRequestTypeDBO.letterTypeName + " and " + bo.empLetterRequestReasonDBO.letterRequestReasonName ;
				}
			}
		}
		if (Utils.isNullOrEmpty(result.failureMessage)) {
			EmpLetterRequestDBO dbo =null;
			if(Utils.isNullOrWhitespace(data.id) == false) {
				dbo =letterRequestTransaction.edit(data.id);
				dbo.modifiedUsersId = Integer.parseInt(userId);
			}
			else {
				dbo = new EmpLetterRequestDBO();
				//dbo.letterRequestAppliedDate = new Date(); 
				dbo.letterRequestAppliedDate = LocalDate.now();
				//dbo.applicantStatusLogTime = new Date();
				dbo.applicantStatusLogTime = LocalDate.now();
				//dbo.applicationStatusLogTime = new Date();
				dbo.applicationStatusLogTime = LocalDate.now();
				dbo.createdUsersId = Integer.parseInt(userId);
			}
			if(!Utils.isNullOrEmpty(data.letterType)) {
				EmpLetterRequestTypeDBO type = new  EmpLetterRequestTypeDBO();
				type.id = Integer.parseInt(data.letterType.id);
				dbo.empLetterRequestTypeDBO = type;
			}
			if(!Utils.isNullOrEmpty(data.reasonType)) {
				EmpLetterRequestReasonDBO reason = new  EmpLetterRequestReasonDBO();
				reason.id =  Integer.parseInt(data.reasonType.id);
				dbo.empLetterRequestReasonDBO = reason;
			}
			if(!Utils.isNullOrEmpty(data.details)) {
				dbo.letterRequestDetails =data.details;
			}
			dbo.recordStatus='A';
			EmpDBO emp =new EmpDBO();
			emp.id=Integer.parseInt(userId);
			dbo.empDBO = emp;
			Tuple tuple = commonApiTransaction.getErpWorkFlowProcessIdbyProcessCode("LETTER_REQUEST_SUBMISSION");
			if(tuple.get("applicant_status_display_text")!=null && !Utils.isNullOrWhitespace(tuple.get("applicant_status_display_text").toString())) {
				ErpWorkFlowProcessDBO applicant =new ErpWorkFlowProcessDBO();
				applicant.id =Integer.parseInt(tuple.get("erp_work_flow_process_id").toString());
				dbo.erpApplicantWorkFlowProcessDBO = applicant;				
			}
			if(tuple.get("application_status_display_text")!=null  && !Utils.isNullOrWhitespace(tuple.get("application_status_display_text").toString())) {
				ErpWorkFlowProcessDBO application =new ErpWorkFlowProcessDBO();
				application.id =Integer.parseInt(tuple.get("erp_work_flow_process_id").toString());
				dbo.erpApplicationWorkFlowProcessDBO = application;				
			}
			ErpWorkFlowProcessStatusLogDBO erpWorkFlowStatusDbo= new ErpWorkFlowProcessStatusLogDBO();
			if(Utils.isNullOrEmpty(data.id)) {
				EmpLetterRequestDBO empLetterRequestBo = letterRequestTransaction.saveorUpdate(dbo, data);
				if(empLetterRequestBo.id!=0) {
					erpWorkFlowStatusDbo.entryId = empLetterRequestBo.id;
					isSaveOrUpdated = true;
				}
			}
			if(Utils.isNullOrWhitespace(data.id) == false) {
				erpWorkFlowStatusDbo.entryId = Integer.parseInt(data.id);	
				EmpLetterRequestDBO empLetterRequestBo = letterRequestTransaction.saveorUpdate(dbo, data);
				if(empLetterRequestBo.id!=0) {
					isSaveOrUpdated = true;
				}
			}
			ErpWorkFlowProcessDBO applicant =new ErpWorkFlowProcessDBO();
			applicant.id =Integer.parseInt(tuple.get("erp_work_flow_process_id").toString());
			erpWorkFlowStatusDbo.erpWorkFlowProcessDBO = applicant;
			erpWorkFlowStatusDbo.recordStatus='A';
			erpWorkFlowStatusDbo.createdUsersId=Integer.parseInt(userId);
			if(isSaveOrUpdated) {
			flag =  commonApiTransaction.saveErpWorkFlowProcessStatusLogDBO(erpWorkFlowStatusDbo);		
			}
			if(flag) {
				result.success = true;
				result.dto = new ModelBaseDTO();
				result.dto.id = String.valueOf(dbo.id);	
			}
		
		}
		return result;
	}

	public List<LetterRequestDTO> getGridData() throws Exception {
		List<LetterRequestDTO> gridList = null;
		List <EmpLetterRequestDBO> list = letterRequestTransaction.getGridData();
		if(!Utils.isNullOrEmpty(list)) {
			gridList= new ArrayList<LetterRequestDTO>();
			for (EmpLetterRequestDBO dbo: list) {
				LetterRequestDTO dto = new LetterRequestDTO(); 
				dto.id = String.valueOf(dbo.id);
				dto.appliedDate =!Utils.isNullOrEmpty(dbo.letterRequestAppliedDate) ? dbo.letterRequestAppliedDate.format(DateTimeFormatter.ofPattern("dd/MMM/yyyy")) : "";
				dto.letterType = new ExModelBaseDTO();
				if (!Utils.isNullOrEmpty(dbo.empLetterRequestTypeDBO)) {
					dto.letterType.id = !Utils.isNullOrEmpty(dbo.empLetterRequestTypeDBO.id) ?(dbo.empLetterRequestTypeDBO.id.toString()) : "";
					dto.letterType.text = !Utils.isNullOrEmpty(dbo.empLetterRequestTypeDBO.letterTypeName) ?(dbo.empLetterRequestTypeDBO.letterTypeName) : "";
				} 
				dto.reasonType = new ExModelBaseDTO();
				if (!Utils.isNullOrEmpty(dbo.empLetterRequestReasonDBO)) {
					dto.reasonType.id = !Utils.isNullOrEmpty(dbo.empLetterRequestReasonDBO.id) ?(dbo.empLetterRequestReasonDBO.id.toString()) : "";
					dto.reasonType.text = !Utils.isNullOrEmpty(dbo.empLetterRequestReasonDBO.letterRequestReasonName) ?(dbo.empLetterRequestReasonDBO.letterRequestReasonName) : "";
				} 
				dto.details = !Utils.isNullOrEmpty(dbo.letterRequestDetails) ?(dbo.letterRequestDetails) : "";
				if (!Utils.isNullOrEmpty(dbo.erpApplicantWorkFlowProcessDBO)) {
					if(!Utils.isNullOrEmpty(dbo.erpApplicantWorkFlowProcessDBO.applicantStatusDisplayText) && !Utils.isNullOrEmpty(dbo.letterRequestPoComment)) {
						dto.status =dbo.erpApplicantWorkFlowProcessDBO.applicantStatusDisplayText;
						dto.reasonText = dbo.letterRequestPoComment;
						dto.processCode = dbo.erpApplicantWorkFlowProcessDBO.processCode;
							
					}
					else {
						dto.status = !Utils.isNullOrEmpty(dbo.erpApplicantWorkFlowProcessDBO.applicantStatusDisplayText) ?(dbo.erpApplicantWorkFlowProcessDBO.applicantStatusDisplayText) : "";
						dto.processCode = dbo.erpApplicantWorkFlowProcessDBO.processCode;
					}
				}
				gridList.add(dto);	                           
			}           
		}
		return gridList;
	}

	public boolean delete(String id) throws Exception {
		return letterRequestTransaction.delete(id);      
	}

	public LetterRequestDTO edit(String id) throws Exception {
		EmpLetterRequestDBO bo = letterRequestTransaction.edit(id);
		LetterRequestDTO dto = new LetterRequestDTO();
		String letter = null;
		if(bo != null) {
			dto.id = String.valueOf(bo.id);
			dto.reasonType = new ExModelBaseDTO();
			dto.reasonType.id = String.valueOf(bo.empLetterRequestReasonDBO.id);
			dto.letterType = new ExModelBaseDTO();
			dto.letterType.id = String.valueOf(bo.empLetterRequestTypeDBO.id);
			if(!Utils.isNullOrEmpty(bo.empLetterRequestTypeDBO.letterHelpText)) {
				dto.helpText = bo.empLetterRequestTypeDBO.letterHelpText;
			}
			dto.details = bo.letterRequestDetails;
		}
		return dto;
	}

	public LetterRequestDTO getLetterRequestHelpText(LetterRequestDTO data) throws Exception {
		String letter = null;
		LetterRequestDTO dto = new LetterRequestDTO();
		letter = letterRequestTransaction.getLetterType(String.valueOf(data.letterType.id));
		if(letter!=null) {
			dto.helpText = letter;
		}
		return dto;
	}
}
