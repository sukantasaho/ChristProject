package com.christ.erp.services.helpers.employee.letter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Tuple;

import org.springframework.stereotype.Service;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.CommonDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.letter.LetterGenerateIssueListDTO;

@Service
public class LetterGenerateIssueHelper {

	public List<LetterGenerateIssueListDTO> getLetterRequestList(List<Tuple> requestList) {
		List<LetterGenerateIssueListDTO> letterRequestList = new ArrayList<LetterGenerateIssueListDTO>();
		if(requestList != null && requestList.size() > 0) {
        	String label = "";
            for(Tuple mapping : requestList) {
            	LetterGenerateIssueListDTO dto = new LetterGenerateIssueListDTO();
            	dto.letterRequestId = !Utils.isNullOrEmpty(mapping.get("emp_letter_request_id")) ? mapping.get("emp_letter_request_id").toString() : "";
            	dto.employeeId = !Utils.isNullOrEmpty(mapping.get("emp_id")) ? mapping.get("emp_id").toString() : "";
            	dto.employeeName = !Utils.isNullOrEmpty(mapping.get("emp_name")) ? mapping.get("emp_name").toString() : "";
            	dto.employeeNumber = !Utils.isNullOrEmpty(mapping.get("emp_no")) ? mapping.get("emp_no").toString() : "";
            	dto.department = !Utils.isNullOrEmpty(mapping.get("dep_name")) ? mapping.get("dep_name").toString() : "";
            	dto.campus = !Utils.isNullOrEmpty(mapping.get("campus_name")) ? mapping.get("campus_name").toString() : "";
            	dto.appliedDate = !Utils.isNullOrEmpty(mapping.get("applied_date")) ? mapping.get("applied_date").toString() : "";
            	dto.requestReason = !Utils.isNullOrEmpty(mapping.get("reason")) ? mapping.get("reason").toString() : "";
            	dto.letterType = !Utils.isNullOrEmpty(mapping.get("letter_type_name")) ? mapping.get("letter_type_name").toString() : "";
            	dto.status = new SelectDTO(); 
    			dto.status.value = !Utils.isNullOrEmpty(mapping.get("status_id")) ? mapping.get("status_id").toString() : "";
    			label= !Utils.isNullOrEmpty(mapping.get("status_code")) ? mapping.get("status_code").toString() : "";
    			dto.status.label = getStatusCode(label);
    			
    			dto.issueDate = !Utils.isNullOrEmpty(mapping.get("issued_date")) ? mapping.get("issued_date").toString() : "";
            	dto.rejectReason = !Utils.isNullOrEmpty(mapping.get("comment")) ? mapping.get("comment").toString() : "";
            	letterRequestList.add(dto);
            }
        }
		return letterRequestList;
	}

	public LocalDate getFormattedDates(String sdate) {
		LocalDateTime date = Utils.convertStringDateTimeToLocalDateTime(sdate);
		LocalDate date1=date.toLocalDate();
		return date1;
		
	}
	public java.sql.Date convertStringToDate(String dateString)
	{
	    Date date = null;
	    java.sql.Date sDate = null;  
//	    DateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
//	    try{
//	        date = df.parse(dateString);  
//	        sDate=new java.sql.Date(date.getTime());
//	    }
//	    catch ( Exception ex ){
//	        System.out.println(ex);
//	    }
	    return sDate;
	}
	public String getTemplate(LetterGenerateIssueListDTO to, Tuple template) {
		String templateContent = "";
		if(template != null) {
        	templateContent = !Utils.isNullOrEmpty(template.get("template")) ? template.get("template").toString():"";
        	if(templateContent != null && !templateContent.isEmpty()){
        		templateContent = templateContent.replace("[EMP_NAME]", to.employeeName);
        		templateContent = templateContent.replace("],[COMPLETED_DATE]", to.issueDate);
        		templateContent = templateContent.replace("[LETTER_TYPE]", to.letterType);
        		if(to.rejectReason != null && !to.rejectReason.isEmpty())
        		templateContent = templateContent.replace("[REJECT_REASON]",to.rejectReason);
        		templateContent = templateContent.replace("[COPLETED_DATE]",to.issueDate);
        	}
        }
		return templateContent;
	}
	public void getStatusList(ApiResult<List<CommonDTO>> result, List<Tuple> statusList) {
		String label = null;
		for(Tuple mapping : statusList) {
        	CommonDTO itemInfo = new CommonDTO();
            itemInfo.value = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
            label  = !Utils.isNullOrEmpty(mapping.get("Text")) ? mapping.get("Text").toString() : "";
            if(label.equals("LETTER_REQUEST_SUBMISSION")) {
            	itemInfo.label = "Pending";
            }else if (label.equals("LETTER_REQUEST_CLARIFICATION")) {
            	itemInfo.label = "Clarify/Meet";
            }else if (label.equals("LETTER_REQUEST_COMPLETED")) {
            	itemInfo.label = "Completed";
            }else if (label.equals("LETTER_REQUEST_ISSUED")) {
            	itemInfo.label = "Issued";
            }else if (label.equals("LETTER_REQUEST_PRINTED")) {
            	itemInfo.label = "Printed";
            }else if (label.equals("LETTER_REQUEST_REJECTED")) {
            	itemInfo.label = "Reject";
            }
            result.dto.add(itemInfo);
        }
		
	}
	public String getStatusCode(String label){
		String code ="";
		if(label.equals("LETTER_REQUEST_SUBMISSION")) {
        	code = "Pending";
        }else if (label.equals("LETTER_REQUEST_CLARIFICATION")) {
        	code = "Clarify/Meet";
        }else if (label.equals("LETTER_REQUEST_COMPLETED")) {
        	code = "Completed";
        }else if (label.equals("LETTER_REQUEST_ISSUED")) {
        	code = "Issued";
        }else if (label.equals("LETTER_REQUEST_PRINTED")) {
        	code = "Printed";
        }else if (label.equals("LETTER_REQUEST_REJECTED")) {
        	code = "Reject";
        }
		return code;
	}
	public Map<String, String[]> getStatupdateInfo(List<Tuple> statusUpdateInfo) {
		Map<String, String[]> statusMap = new HashMap<String, String[]>();
		for(Tuple mapping : statusUpdateInfo) {
			 String[] a = new  String[2];
            String key = !Utils.isNullOrEmpty(mapping.get("ID")) ? mapping.get("ID").toString() : "";
            a[0]  = !Utils.isNullOrEmpty(mapping.get("admin_status")) ? !Utils.isNullOrWhitespace(mapping.get("admin_status").toString())?  
            		mapping.get("admin_status").toString():"":"";
            a[1]  = !Utils.isNullOrEmpty(mapping.get("applicant_status")) ? !Utils.isNullOrWhitespace(mapping.get("applicant_status").toString())?
            		mapping.get("applicant_status").toString():"": "";
            statusMap.put(key, a);
		}
		return statusMap;
	}
	public String getComputedTemplate(String template, Tuple employee) {
		if(employee != null) {
        	template = template.replace("[EMPLOYEE_NAME]", !Utils.isNullOrEmpty(employee.get("emp_name")) ? employee.get("emp_name").toString():"");
        	template = template.replace("[DESIGNATION]", !Utils.isNullOrEmpty(employee.get("designation")) ? employee.get("designation").toString():"");
			template = template.replace("[DOB]", !Utils.isNullOrEmpty(employee.get("dob")) ? employee.get("dob").toString():"");
			template = template.replace("[CAMPUS]", !Utils.isNullOrEmpty(employee.get("campus")) ? employee.get("campus").toString():"");
			template = template.replace("[MOBILE_NO]", !Utils.isNullOrEmpty(employee.get("mobile")) ? employee.get("mobile").toString():"");
			template = template.replace("[DEPARTMENT]", !Utils.isNullOrEmpty(employee.get("emp_dep")) ? employee.get("emp_dep").toString():"");
			String curAddress = getFullAddress(employee, "current");
			String permanentAddress = getFullAddress(employee, "permanent");
			template = template.replace("[CURRENT_ADDRESS]", curAddress);
			template = template.replace("[PERMANENT_ADDRESS]", permanentAddress);			
        }
		return template;
		
	}
	private String getFullAddress(Tuple employee, String addresstype) {
		
		String address =  "";
		if(addresstype.equals("current")) {		
			String cur_address_line1 = !Utils.isNullOrEmpty(employee.get("cur_address_line1")) ? employee.get("cur_address_line1").toString():"";
			String cur_address_line2 = !Utils.isNullOrEmpty(employee.get("cur_address_line2")) ? employee.get("cur_address_line2").toString():"";
			String cur_country = !Utils.isNullOrEmpty(employee.get("cur_country")) ? employee.get("cur_country").toString():"";
			String cur_state = !Utils.isNullOrEmpty(employee.get("cur_state")) ? employee.get("cur_state").toString():"";
			String state_others = !Utils.isNullOrEmpty(employee.get("state_others")) ? employee.get("state_others").toString():"";
			String cur_city = !Utils.isNullOrEmpty(employee.get("cur_city")) ? employee.get("cur_city").toString():"";
			String city_others = !Utils.isNullOrEmpty(employee.get("city_others")) ? employee.get("city_others").toString():"";
			address = address+cur_address_line1+", "+cur_address_line2+", "+ 
					  cur_city != null &&  !cur_city.isEmpty() ? cur_city : city_others;
			address = address + ", " + cur_state != null && !cur_state.isEmpty() ? cur_state : state_others;
			address = address + ", " +cur_country ;
		}else if(addresstype.equals("permanent")) {
		
			String per_add_line1 = !Utils.isNullOrEmpty(employee.get("per_add_line1")) ? employee.get("per_add_line1").toString():"";
			String per_add_line2 = !Utils.isNullOrEmpty(employee.get("per_add_line2")) ? employee.get("per_add_line2").toString():"";
			String per_state_others = !Utils.isNullOrEmpty(employee.get("per_state_others")) ? employee.get("per_state_others").toString():"";
			String per_city_others = !Utils.isNullOrEmpty(employee.get("per_city_others")) ? employee.get("per_city_others").toString():"";
			String per_county_name = !Utils.isNullOrEmpty(employee.get("per_county_name")) ? employee.get("per_county_name").toString():"";
			String per_state_name = !Utils.isNullOrEmpty(employee.get("per_state_name")) ? employee.get("per_state_name").toString():"";
			String per_city_name = !Utils.isNullOrEmpty(employee.get("per_city_name")) ? employee.get("per_city_name").toString():"";	
			address = address+per_add_line1+", "+per_add_line2+", "+ 
					  per_city_name != null &&  !per_city_name.isEmpty() ? per_city_name : per_city_others;
			address = address + ", " + per_state_name != null && !per_state_name.isEmpty() ? per_state_name : per_state_others;
			address = address + ", " +per_county_name ;
		}
		return address;
	}
}
