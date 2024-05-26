package com.christ.erp.services.dto.employee.onboarding;

import java.time.LocalDate;
import java.util.Set;

import com.christ.erp.services.dto.common.ModelBaseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class EmpDocumentVerificationDTO extends ModelBaseDTO
{
	public String applicantid;
	public String employeeid;
	public String employeenumber;
	public String applicantnumber;
	public String empname;
	public String empcampus;
	public String empcountry;
	public String postapplied;
	public String waitfordocument;
	public String remarks;
//	public String submissionduedate;
	public String isInDraftMode;
	public Set<EmpDocumentVerificationDetailsDTO> empDocumentVerificationDetailsDTO;
	private LocalDate submissionduedate;
}
