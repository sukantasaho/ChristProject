package com.christ.erp.services.dto.admission.applicationprocess;

import java.util.List;

import com.christ.erp.services.dto.admission.settings.AdmSelectionProcessVenueCityDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmSelectionProcessPlanDetailDTO  extends ModelBaseDTO {
	public List<AdmSelectionProcessPlanDetailAddSlotDTO> slotslist;
    public List<AdmSelectionProcessPlanDetailAddVenueDTO> venueslist;
    private int id;
    private Integer sessionId;
    private String sessionName;
    public String admSelectionProcessPlanDetailId;
    public String admSelectionProcessPlanId;
    public String admSelectionProcessTypeId;
    public String processOrder;
    public String selectionProcessDate;
    public String selectionProcessTime;
    public String slot;
    public String admSelectionProcessVenueCityId;
    public String availableSeats;
    public String isCandidateChooseSpVenue;
    public String isCandidateChooseSpDate;
    public String erpTemplateId;
    public String admSelectionProcessVenueCityName;
    public List<AdmSelectionProcessVenueCityDTO> admSelectionProcessVenueCityDTOList;
}
