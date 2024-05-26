package com.christ.erp.services.dto.admission.applicationprocess;

import com.christ.erp.services.dto.admission.settings.ProgramPreferenceDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import lombok.Data;

import java.util.List;

@Data
public class CalculateWeightageDTOWebFlux {

    private Integer id;
    private ExModelBaseDTO academicYear;
    private ExModelBaseDTO session;
    private List<ProgramPreferenceDTO> programPreference;
}
