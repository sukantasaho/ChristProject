package com.christ.erp.services.dto.admission.settings;

import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdmProgrammeBatchDTO {
    private SelectDTO programmeSettingsDTO;
    private List<SelectDTO> batchCampusList;
}
