package com.christ.erp.services.dto.admission.applicationprocess;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmWeightageDefinitionDetailDTO {
	
    private List<AdmPrerequisiteExamDTO> admPrerequisiteExamDTOList;
    private List<AdmQualificationListDTO> admQualificationListDTOList;
    private List<AdmSelectionProcessPlanDetailDTO> admSelectionProcessPlanDetailDTOList;
}
