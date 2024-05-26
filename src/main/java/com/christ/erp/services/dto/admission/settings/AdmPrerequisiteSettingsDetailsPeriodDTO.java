package com.christ.erp.services.dto.admission.settings;

import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;

import lombok.Setter;

@Getter
@Setter
public class AdmPrerequisiteSettingsDetailsPeriodDTO {
    private int id;
    private Integer year;
    private SelectDTO month;
}
