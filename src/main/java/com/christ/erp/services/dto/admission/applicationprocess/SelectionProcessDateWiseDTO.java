package com.christ.erp.services.dto.admission.applicationprocess;

import com.christ.erp.services.dto.admission.settings.AdmSelectionProcessVenueCityDTO;
import lombok.Data;

import java.util.List;

@Data
public class SelectionProcessDateWiseDTO {

    private String selectionProcessDate;
    private Integer admSelectionProcessPlanDetailId;
    private List<SelectionProcessStateWiseDTO> selectionProcessStateWiseDTOS;
    private List<AdmSelectionProcessVenueCityDTO> venuesWithoutStateList;
}
