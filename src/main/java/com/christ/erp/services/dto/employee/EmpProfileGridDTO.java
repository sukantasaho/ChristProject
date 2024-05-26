package com.christ.erp.services.dto.employee;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpProfileGridDTO {
	private int empId;
	private String empName;
	private String empNo;
	private String empUniversityMail;
	private String campusName;
	private String departmentName;
	private String empDesignation; 
	private String profilePhotoUrl;
	private String fileNameOriginal;
	private String processCode;
	
	public EmpProfileGridDTO(int empId, String empName, String empNo, String empUniversityMail, String campusName, String departmentName, String empDesignation, 
	    		String profilePhotoUrl, String fileNameOriginal, String processCode) {
	    	this.empId = empId;
	    	this.empName = empName;
	    	this.empNo = empNo;
	    	this.empUniversityMail = empUniversityMail;
	    	this.campusName = campusName;    	
	    	this.departmentName = departmentName;
	    	this.empDesignation = empDesignation;
	    	this.profilePhotoUrl = profilePhotoUrl;
	    	this.fileNameOriginal = fileNameOriginal;
	    	this.processCode = processCode;
	    }
	    
}
