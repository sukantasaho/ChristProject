package com.christ.erp.services.dto.employee.recruitment;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SalaryAndLeaveDetailsDTO {
	public FinalInterviewCommentsDTO finalInterviewCommentsDTO;
//	public SalaryDetailsDTO salaryDetails;
	public EmployeeProfileLeaveDetailsDTO leaveDetails;
	public PfAndGratutyDetailsDTO pfAndNomineeDetails;
	public PfAndGratutyDetailsDTO gratuvityAndNomineeDetails;
}
