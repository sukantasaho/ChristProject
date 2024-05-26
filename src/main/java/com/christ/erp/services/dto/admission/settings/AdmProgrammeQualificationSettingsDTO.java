package com.christ.erp.services.dto.admission.settings;

import java.util.List;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmProgrammeQualificationSettingsDTO extends ModelBaseDTO {

	public String displayorder;
	public ExModelBaseDTO qualificationlevel;
	public Boolean qualificationlevelmandatory;
	public Boolean uploadrequired;
	public Boolean uploadmandatory;
//	public ExModelBaseDTO universityrequired;
	private SelectDTO universityBoard;
	public Boolean examnamerequired;
	private Boolean examRegisterNumberRequired;
	public ExModelBaseDTO marksentrytype;
	public Boolean subjecteligibilityrequired;
	public int minsubjectforeligibility;
	public double aggregatesubjectspercentage;
	public Boolean backlogRequired;
	public List<AdmProgrammeQualificationSubjectEligibilityDTO> subject;
}
