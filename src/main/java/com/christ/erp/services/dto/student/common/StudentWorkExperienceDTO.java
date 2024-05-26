package com.christ.erp.services.dto.student.common;

import java.time.LocalDate;
import java.util.List;

import com.christ.erp.services.common.Utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class StudentWorkExperienceDTO {
	
    private int studentWorkExperienceId;
    private StudentApplnEntriesDTO studentApplnEntries;
    private StudentDTO student;
    private String organizationName;
    private String organizationAddress;
    private String designation;
    private LocalDate workExperienceFromDate;
    private LocalDate workExperienceToDate;
    private String workExperienceYears;
    private String workExperienceMonth;
	private String FunctionalArea;
    private List<StudentWorkExperienceDocumentDTO> StudentWorkExperienceDocumentDTOList;
    
    public StudentWorkExperienceDTO(int id,Integer studentApplnEntriesId,String organizationName,String occupationName,String organizationAddress,String designation,LocalDate workExperienceFromDate,LocalDate workExperienceToDate
    		,Integer workExperienceYears,Integer workExperienceMonth,String occupationOthers) {
    	this.studentWorkExperienceId = id;
    	if(!Utils.isNullOrEmpty(studentApplnEntriesId)) {
    		this.setStudentApplnEntries(new StudentApplnEntriesDTO());
    		this.getStudentApplnEntries().setId(studentApplnEntriesId);
    	}
    	this.setOrganizationName(organizationName);
    	this.setOrganizationAddress(organizationAddress);
    	this.setDesignation(designation);
    	if(!Utils.isNullOrEmpty(workExperienceFromDate)) {
    		this.setWorkExperienceFromDate(workExperienceFromDate);
    	}
    	if(!Utils.isNullOrEmpty(workExperienceToDate)) {
    		this.setWorkExperienceToDate(workExperienceToDate);
    	}
    	if(!Utils.isNullOrEmpty(workExperienceYears)) {
    		this.setWorkExperienceYears(String.valueOf(workExperienceYears));
    	}
    	if(!Utils.isNullOrEmpty(workExperienceMonth)) {
    		this.setWorkExperienceMonth(String.valueOf(workExperienceMonth));
    	}
		if(!Utils.isNullOrEmpty(occupationName)){
			this.setFunctionalArea(occupationName);
		} else {
			this.setFunctionalArea(occupationOthers);
		}
    }
}
