package com.christ.erp.services.dto.employee.recruitment;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EducationAndExperienceDetailsDTO {
	public EducationalDetailsDTO qualificationDetails;
	public ProfessionalExperienceDTO experienceDetails;
}
