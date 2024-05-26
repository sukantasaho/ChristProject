package com.christ.erp.services.dto.admission.applicationprocess;

import java.util.List;

import com.christ.erp.services.dto.common.weightageGeneralDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmWeightageDefinitionGeneralDTO {

    private int id;
    private List<weightageGeneralDTO> erpReligionDTOList;
    private List<weightageGeneralDTO> erpReservationCategoryDTOList;
    private List<weightageGeneralDTO> erpGenderDTOList;
    private List<weightageGeneralDTO> erpInstitutionDTOList;
    private List<weightageGeneralDTO> erpResidentCategoryDTOList;
    private List<weightageGeneralDTO> admQualificationDegreeListDTOList;
    private List<weightageGeneralDTO> admWeightageDefinitionWorkExperienceDTOList;


}
