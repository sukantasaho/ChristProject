package com.christ.erp.services.dto.admission.applicationprocess;

import java.util.List;
import java.util.Set;

import com.christ.erp.services.dto.admission.settings.ProgramPreferenceDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmWeightageDefinitionDTO {

	    private int id;
	    private LookupItemDTO erpAcademicYearDTO;
	    private LookupItemDTO erpProgrammeDTO;
	    private LookupItemDTO erpProgrammeDegreeDTO;
	    private LookupItemDTO erpProgrammeLevelDTO;
	    private Integer preRequisiteWeigtageTotal;
	    private Integer educationWeightageTotal;
	    private Integer interviewWeightageTotal;
	    private Integer overallTotal;
	    private List<ProgramPreferenceDTO> campusOrlocationsMappping;
	    private Set<AdmWeightageDefinitionDetailDTO> admWeightageDefinitionDetailDTOSet;
	    private Set<AdmWeightageDefinitionGeneralDTO>  admWeightageDefinitionGeneralDTOsSet;


}
