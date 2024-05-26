package com.christ.erp.services.dto.admission.settings;

import java.util.List;

import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdmSelectionProcessVenueCityDTO extends ModelBaseDTO {
	
	private int admSelectionProcessVenueCityId;
	public Boolean isAddOrUpdate;
	public String index;
	public ExModelBaseDTO mode;
	public ExModelBaseDTO country;
	public ExModelBaseDTO state;
	public String address;
	public String venue;
	public String maxSeats;
	public String active;
	public AdmSelectionProcessCenterDetailsDTO newCenterDetails;
	public List<AdmSelectionProcessCenterDetailsDTO> centerDetails;
}
