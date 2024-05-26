package com.christ.erp.services.dto.admission.settings;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdmProgrammeBatchPreferencesDTO {
    private AdmProgrammeSettingsDTO admProgrammeSettingsDTO;
    private List<AdmProgrammeBatchDTO> admProgrammeBatchDTOList;
}
