package com.christ.erp.services.dto.admission.applicationprocess;

import com.christ.erp.services.dto.admission.settings.AdmSelectionProcessVenueCityDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;

import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdmSelectionProcessPlanCenterBasedDTO  extends ModelBaseDTO {
	
	private int admSelectionProcessPlanCenterBased;
	public String venueTypeId;
	public ExModelBaseDTO city;
    public String maxseats;
	public String avaliableseats;
	private SelectDTO  state;
	private SelectDTO  country;
    private AdmSelectionProcessVenueCityDTO admSelectionProcessVenueCity;
}
