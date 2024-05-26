package com.christ.erp.services.dto.admission.settings;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmProgrammeQualificationSubjectEligibilityDTO {
	public String id;
	public String subjectname;
	public String eligibilitypercentage;
	private AdmProgrammeQualificationSettingsDTO admProgrammeQualificationSettingsDTO;
	
}
