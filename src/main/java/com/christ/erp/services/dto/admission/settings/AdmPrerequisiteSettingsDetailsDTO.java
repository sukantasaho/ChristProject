package com.christ.erp.services.dto.admission.settings;

import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdmPrerequisiteSettingsDetailsDTO {
    private int id;
    private SelectDTO erpLocationDTO;
    private SelectDTO admPrerequisiteExamDTO;
    private Integer minMarks;
    private Integer minMarksForChristite;
    private Integer totalMarks;
    private List<AdmPrerequisiteSettingsDetailsPeriodDTO> admPrerequisiteSettingsDetailsPeriodDTOList;
}
