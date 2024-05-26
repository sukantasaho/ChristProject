package com.christ.erp.services.handlers.employee.letter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Tuple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.AppProperties;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessStatusLogDBO;
import com.christ.erp.services.dto.common.CommonDTO;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.employee.letter.LetterGenerateIssueDTO;
import com.christ.erp.services.dto.employee.letter.LetterGenerateIssueListDTO;
import com.christ.erp.services.helpers.employee.letter.LetterGenerateIssueHelper;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.employee.letter.LetterGenerateIssueTransaction;

@Service
public class LetterGenerateIssueHandler {


	private static final String EMPLOYEE_LETTER_REQUEST_FILESERVER_PATH;
	static {
		EMPLOYEE_LETTER_REQUEST_FILESERVER_PATH = AppProperties.get("erp.emp.letterprint.fileserver.path");
	}
	@Autowired
	LetterGenerateIssueTransaction transaction;
	@Autowired
	LetterGenerateIssueHelper helper;
	
	public void getEmployeeLocation(int userId, ApiResult<LetterGenerateIssueDTO> result) throws Exception {
		List<Tuple> location = transaction.getEmployeeLocation(userId);
		if(location != null && location.size() > 0) {
        	result.success = true;
        	result.dto = new LetterGenerateIssueDTO();
        	String locationId = "";
            for(Tuple mapping : location) {
            	locationId = !Utils.isNullOrEmpty(mapping.get("location_id")) ? mapping.get("location_id").toString() : "";
            }
            if(!locationId.isEmpty()) {
            	result.dto.location = locationId;
            }
        }
	}
	public List<LetterGenerateIssueListDTO> getLetterRequestlist(int userId, ApiResult<LetterGenerateIssueDTO> result) throws Exception {
		getEmployeeLocation(userId,result);
		LetterGenerateIssueDTO dto = new LetterGenerateIssueDTO();
		dto.location = result.dto.location;
		List<Tuple> requestList = transaction.getLetterRequestlist(dto,null,null);
		List<LetterGenerateIssueListDTO> list = helper.getLetterRequestList(requestList);
		return list;
	}
	public List<LetterGenerateIssueListDTO> getLetterRequestlistBySearch(LetterGenerateIssueDTO dto) throws Exception {
		LocalDate fromDate = null;
		LocalDate toDate = null;
		if(!Utils.isNullOrEmpty(dto.fromDate))
		fromDate = helper.getFormattedDates(dto.fromDate);
		if(!Utils.isNullOrEmpty(dto.toDate))
		toDate = helper.getFormattedDates(dto.toDate);
		List<Tuple> requestList = transaction.getLetterRequestlist(dto,fromDate,toDate);
		List<LetterGenerateIssueListDTO> list =  helper.getLetterRequestList(requestList);
		return list;
		
	}
	public String getLetterPrint(LetterGenerateIssueListDTO dto , ApiResult<LetterGenerateIssueDTO> result) throws Exception {
		List<Tuple> erpTemplateDBO = transaction.getLetterPrint(Integer.parseInt(dto.letterRequestId),result);
		String template = "";
		Tuple employee=transaction.getEmpDetailsBasedOnEmpId(dto.employeeId);
		if(erpTemplateDBO != null && erpTemplateDBO.size() > 0) {
        	result.success = true;
        	result.dto = new LetterGenerateIssueDTO();
        	String type_id = "";String letter_url = "";
            for(Tuple mapping : erpTemplateDBO) {
            	type_id = !Utils.isNullOrEmpty(mapping.get("type_id")) ? mapping.get("type_id").toString() : "";
            	template = !Utils.isNullOrEmpty(mapping.get("template")) ? mapping.get("template").toString() : "";
            	letter_url = !Utils.isNullOrEmpty(mapping.get("letter_url")) ? mapping.get("letter_url").toString() : "";
            }
            if(letter_url != null && !letter_url.isEmpty()) {
            	result.dto.letterUrl = letter_url;
            }else {
            	Tuple letterNo = transaction.updateLetterRequestType(type_id,dto);
            	ErpWorkFlowProcessStatusLogDBO logBo = new ErpWorkFlowProcessStatusLogDBO();
				logBo.entryId = Integer.parseInt(dto.letterRequestId);
				ErpWorkFlowProcessDBO wfProcess = new ErpWorkFlowProcessDBO();
				wfProcess.id = Integer.parseInt(dto.status.value);
				logBo.erpWorkFlowProcessDBO =wfProcess;
				logBo.createdUsersId = 1;
				CommonApiTransaction.getInstance().saveErpWorkFlowProcessStatusLogDBO(logBo);
            	if(letterNo != null) {
            		String prefix =  !Utils.isNullOrEmpty(letterNo.get("prefix")) ? letterNo.get("prefix").toString():"";
            		String curNo = 	 !Utils.isNullOrEmpty(letterNo.get("current_no")) ? letterNo.get("current_no").toString():"";
            		int cuNo = curNo != null && !curNo.isEmpty() ?Integer.parseInt(curNo):1;
            		String letterNum = prefix+cuNo;
            		template = template.replace("[LETTER_NO]", letterNum);
            		result.dto.letterUrl = EMPLOYEE_LETTER_REQUEST_FILESERVER_PATH+"/"+letterNum+".pdf";
            		
                }
            }
            template =  helper.getComputedTemplate(template,employee);
            String regex  = "";
            if(result.dto.letterUrl.indexOf("\\")>0){
            	 String fileName = result.dto.letterUrl.substring( result.dto.letterUrl.lastIndexOf("\\")+1, result.dto.letterUrl.length() );
                 String fileNameWithoutExtn = fileName.substring(0, fileName.lastIndexOf('.'));
                 template = template.replace("[LETTER_NO]", fileNameWithoutExtn);
            }else if(result.dto.letterUrl.indexOf("/")>0) {
            	String fileName = result.dto.letterUrl.substring( result.dto.letterUrl.lastIndexOf('/')+1, result.dto.letterUrl.length() );
                String fileNameWithoutExtn = fileName.substring(0, fileName.lastIndexOf('.'));
                template = template.replace("[LETTER_NO]", fileNameWithoutExtn);
            }
            		
           
           // result.dto.template = template;
        }
		
		return template;
	}
	
	public void updateRequestStatus(LetterGenerateIssueDTO dto, ApiResult<LetterGenerateIssueDTO> result) throws Exception {
		result.dto = new LetterGenerateIssueDTO();
		result.success = false;
		if(dto.requestList != null && dto.requestList.size()>0) {
			List<Tuple> statusUpdateInfo = transaction.getStatupdateInfo();
			Map<String,String[]> statusMap = helper.getStatupdateInfo(statusUpdateInfo);
			for (LetterGenerateIssueListDTO to : dto.requestList) {
				if(to.isSelected && to.status != null && !to.status.label.isEmpty()) {
					String status = to.status.label;
					ApiResult<List<CommonDTO>> a = new ApiResult<List<CommonDTO>>();
					getLetterRequestStatusList(a);
					transaction.updateLetterRequeststatus(to,statusMap,a.dto);
					ErpWorkFlowProcessStatusLogDBO logBo = new ErpWorkFlowProcessStatusLogDBO();
					logBo.entryId = Integer.parseInt(to.letterRequestId);
					ErpWorkFlowProcessDBO wfProcess = new ErpWorkFlowProcessDBO();
					wfProcess.id = Integer.parseInt(to.status.value);
					logBo.erpWorkFlowProcessDBO =wfProcess;
					logBo.createdUsersId = dto.loggedinUserId;
					CommonApiTransaction.getInstance().saveErpWorkFlowProcessStatusLogDBO(logBo);
					
					if(status.equals("Completed")) {
						Tuple templateinfo = transaction.getErpTemplateByCodeBO("EMP_LETTER_REQUEST_COMPLETION");
						Tuple smsinfo = transaction.getErpTemplateByCodeBO("EMP_LETTER_REQUEST_COMPLETION_SMS");
						String template  = helper.getTemplate(to,templateinfo);
						
						System.out.println(template);
						
					}else if(status.equals("Issued")) {
						Tuple templateinfo = transaction.getErpTemplateByCodeBO("EMP_LETTER_REQUEST_ISSUED");
						Tuple smsinfo = transaction.getErpTemplateByCodeBO("EMP_LETTER_REQUEST_ISSUED_SMS");
						String template  = helper.getTemplate(to,templateinfo);
						System.out.println(template);
					}else if(status.equals("Reject")) {
						Tuple templateinfo = transaction.getErpTemplateByCodeBO("EMP_LETTER_REQUEST_REJECT");
						Tuple smsinfo = transaction.getErpTemplateByCodeBO("EMP_LETTER_REQUEST_REJECT_SMS");
						String template  = helper.getTemplate(to,templateinfo);
						System.out.println(template);
					}else if(status.equals("Clarify/Meet")) {
						Tuple templateinfo = transaction.getErpTemplateByCodeBO("EMP_LETTER_REQUEST_CLARIFY");
						Tuple smsinfo = transaction.getErpTemplateByCodeBO("EMP_LETTER_REQUEST_CLARIFY_SMS");
						String template  = helper.getTemplate(to,templateinfo);
						System.out.println(template);
					}else if(status.equals("Pending")) {
					}
					
				}
				result.success = true;
			}
		}
	}
	public void getLetterRequestStatusList(ApiResult<List<CommonDTO>> result) throws Exception {
		List<Tuple> statusList = transaction.getStatusCode();
		if(statusList != null) {
			result.success = true;
        	result.dto = new ArrayList<>();
        	helper.getStatusList(result,statusList);
            
		}
	}
	public void getEmpRequestLetter(String requestId, ApiResult<LetterGenerateIssueDTO> result) throws Exception {
		result.dto = new LetterGenerateIssueDTO();
		List<Tuple> statusList = transaction.getLetterPrint(Integer.parseInt(requestId),result);
		String url = "";
		for (Tuple tuple : statusList) {
			result.dto.letterUrl = !Utils.isNullOrEmpty(tuple.get("letter_url")) ? tuple.get("letter_url").toString() : "";
		}
		if(result.dto.letterUrl.indexOf("\\")>0){
       	 	String fileName = result.dto.letterUrl.substring( result.dto.letterUrl.lastIndexOf("\\")+1, result.dto.letterUrl.length() );
       	 	result.dto.fileName = fileName.substring(0, fileName.lastIndexOf('.'));
       }else if(url.indexOf("/")>0) {
       		String fileName = result.dto.letterUrl.substring( result.dto.letterUrl.lastIndexOf('/')+1, result.dto.letterUrl.length() );
       		result.dto.fileName = fileName.substring(0, fileName.lastIndexOf('.'));
       }
	}
}
