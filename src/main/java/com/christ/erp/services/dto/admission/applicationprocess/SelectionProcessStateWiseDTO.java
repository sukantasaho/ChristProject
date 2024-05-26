package com.christ.erp.services.dto.admission.applicationprocess;

import com.christ.erp.services.dto.admission.settings.AdmSelectionProcessVenueCityDTO;
import lombok.Data;

import java.util.List;

@Data
public class SelectionProcessStateWiseDTO {

    private String stateId;
    private String stateName;
    private List<AdmSelectionProcessVenueCityDTO> venueList;
}
