package com.christ.erp.services.handlers.employee.recruitment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Tuple;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpAppointmentLetterDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import com.christ.erp.services.dto.employee.recruitment.GenerateLetterofAppointmentDTO;
import com.christ.erp.services.transactions.employee.recruitment.GenerateLetterForAppointmentTransaction;

public class GenerateLetterForAppointmentHandler {
	
	private static volatile GenerateLetterForAppointmentHandler generateLetterForAppointmentHandler = null;
	
	GenerateLetterForAppointmentTransaction generateLetterForAppointmentTransaction = GenerateLetterForAppointmentTransaction.getInstance();

    public static GenerateLetterForAppointmentHandler getInstance() {
        if(generateLetterForAppointmentHandler==null) {
        	generateLetterForAppointmentHandler = new GenerateLetterForAppointmentHandler();
        }
        return generateLetterForAppointmentHandler; 
    }

	public  ErpTemplateDBO getAppointmentLetterTemplateData(String id) throws Exception {
		ErpTemplateDBO erpTemplateDBO=generateLetterForAppointmentTransaction.getAppointmentLetterTemplateData(id);
		return erpTemplateDBO;
	}

	public  Tuple getEmpDetailsBasedOnEmpId(String empid) throws Exception {
		return generateLetterForAppointmentTransaction.getEmpDetailsBasedOnEmpId(empid);
	}

	public  Boolean saveEmpAppointmentLetter(Tuple employee, String empid, String userId) throws Exception {
		EmpAppointmentLetterDBO empAppointmentLetterDBO=new EmpAppointmentLetterDBO();
        empAppointmentLetterDBO.empDBO=new EmpDBO();
        empAppointmentLetterDBO.empDBO.id=Integer.parseInt(String.valueOf(employee.get("ID")));
        empAppointmentLetterDBO.generatedAppointmentLetterUrl="E:/dhana/PDFfile/Appointmentletter"+empid+".pdf";
        empAppointmentLetterDBO.recordStatus='A';
        empAppointmentLetterDBO.createdUsersId=Integer.parseInt(userId);
		return generateLetterForAppointmentTransaction.saveEmpAppointmentLetter(empAppointmentLetterDBO);
	}

	public  EmpAppointmentLetterDBO getEmpAppointmentLetter(String empid) throws Exception {
		return generateLetterForAppointmentTransaction.getEmpAppointmentLetter(empid);
	}

	public  Boolean delete(EmpAppointmentLetterDBO empAppointmentLetter) throws Exception {
		Boolean isTrueorFalse=generateLetterForAppointmentTransaction.delete(empAppointmentLetter);
		return isTrueorFalse;
	}

	public  List<GenerateLetterofAppointmentDTO> getGenerateLetterofAppointmentList(String startDate, String endDate, String location,
			String campus) throws Exception {
		// DateFormat inputFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT' SSSS");
        
		LocalDate date = null;
		LocalDate date2 = null;
//		Date date =null;
//         Date date2=null;
//         try {
//	          date = inputFormat.parse(startDate);
//	          date2=inputFormat.parse(endDate);
//         } catch (ParseException e) {
//           e.printStackTrace();
//         }
		String sDate = startDate.trim();
		date = Utils.convertStringDateToLocalDate(sDate);
		date2 = Utils.convertStringDateToLocalDate(endDate);
		List<GenerateLetterofAppointmentDTO> result=new ArrayList<>();
		List<Tuple> mappings=generateLetterForAppointmentTransaction.getGenerateLetterofAppointmentList(date,date2,location,campus);
		if(mappings != null && mappings.size() > 0) {
            for(Tuple mapping : mappings) {
         	   GenerateLetterofAppointmentDTO mappingInfo = new GenerateLetterofAppointmentDTO();
         	    mappingInfo.id = !Utils.isNullOrEmpty(mapping.get("Id")) ? mapping.get("Id").toString() : "";
                mappingInfo.empName = !Utils.isNullOrEmpty(mapping.get("EmployeeName")) ? mapping.get("EmployeeName").toString() : "";
                mappingInfo.empId = !Utils.isNullOrEmpty(mapping.get("EmployeeID")) ? mapping.get("EmployeeID").toString() : "";
                mappingInfo.setDoj(!Utils.isNullOrEmpty(mapping.get("JoiningDate")) ? Utils.convertStringDateToLocalDate(mapping.get("JoiningDate").toString()) : null);
                mappingInfo.documentSubmissionStatus = String.valueOf(mapping.get("DocumentSubmissionStatus"));
                mappingInfo.generationStatus=!Utils.isNullOrEmpty(mapping.get("GenerationStatus")) ? mapping.get("GenerationStatus").toString() :"";
                result.add(mappingInfo);
              }
        }
		return result;
	}

	public  List<GenerateLetterofAppointmentDTO> getGenerateLetterofAppointmentPending(String startDate, String endDate, String location,
			String campus) throws Exception {
//		DateFormat inputFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT' SSSS");
//         Date date =null;
//          Date date2=null;
//         try {
//	          date = inputFormat.parse(startDate);
//	          date2=inputFormat.parse(endDate);
//         } catch (ParseException e) {
//           e.printStackTrace();
//         }
		LocalDate date = null;
		LocalDate date2 = null;
		date = Utils.convertStringDateToLocalDate(startDate.trim());
		date2 = Utils.convertStringDateToLocalDate(endDate);
		List<GenerateLetterofAppointmentDTO>  result= new ArrayList<>();
		List<Tuple> mappings=generateLetterForAppointmentTransaction.getGenerateLetterofAppointmentPending(date,date2,location,campus);
		if(mappings != null && mappings.size() > 0) {
             for(Tuple mapping : mappings) {
                 GenerateLetterofAppointmentDTO mappingInfo = new GenerateLetterofAppointmentDTO();
                 mappingInfo.empName = !Utils.isNullOrEmpty(mapping.get("EmployeeName")) ? mapping.get("EmployeeName").toString() : "";
                 mappingInfo.empId = !Utils.isNullOrEmpty(mapping.get("EmployeeID")) ? mapping.get("EmployeeID").toString() : "";
                 mappingInfo.setDoj(!Utils.isNullOrEmpty(mapping.get("JoiningDate")) ? Utils.convertStringDateToLocalDate(mapping.get("JoiningDate").toString()) : null);
                 mappingInfo.documentSubmissionStatus = String.valueOf(mapping.get("DocumentSubmissionStatus"));
                 mappingInfo.generationStatus=!Utils.isNullOrEmpty(mapping.get("GenerationStatus")) ? mapping.get("GenerationStatus").toString() :"";
                 result.add(mappingInfo);
             }
         }
		return result;
	}	
}
