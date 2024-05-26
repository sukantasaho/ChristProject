package com.christ.erp.services.dto.admission.applicationprocess;

import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AdmSelectionProcessPlanProgrammeDTO {
    private int id;
    private SelectDTO admissionType;
    private List<SelectDTO> admProgrammeBatch;
}
