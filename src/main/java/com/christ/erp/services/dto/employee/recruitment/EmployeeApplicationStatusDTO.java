package com.christ.erp.services.dto.employee.recruitment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeApplicationStatusDTO {

	public String empApplnEntriesId;
	public String applicationNo;
	public String applicantCurrentProcessStatus;
	public String applicationCurrentProcessStatus;
	public String applicantName;
	public String applicantProcessCode;
	public String applicationProcessCode;
	public String interviewRound;
	public String empApplnPersonalDataId;
	public String empEmployeeCategoryId;
	public String empApplnInterviewSchedulesId;
	public String submissionDate;
	public String emailId;
}
