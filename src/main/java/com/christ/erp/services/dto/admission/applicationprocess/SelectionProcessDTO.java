package com.christ.erp.services.dto.admission.applicationprocess;

import lombok.Data;

import java.util.List;

@Data
public class SelectionProcessDTO {

    private List<SelectionProcessDateWiseDTO> conductedInIndia;
    private List<SelectionProcessDateWiseDTO> conductedOutsideIndia;
    private String processOrder;
    private String selectionProcessRoundOneDate;
    private String selectionProcessRoundOneVenue;
    private String selectionProcessRoundTwoDate;
    private String selectionProcessRoundTwoVenue;
}
