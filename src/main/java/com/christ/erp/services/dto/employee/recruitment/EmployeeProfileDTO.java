package com.christ.erp.services.dto.employee.recruitment;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmployeeProfileDTO {
	public EmployeeProfilePersonalDetailsDTO personalDetails;
	public JobInformationDTO jobDetails;
	public EducationAndExperienceDetailsDTO educationAndExperienceDetails;
	public SalaryAndLeaveDetailsDTO salaryAndLeaveDetails;
}
