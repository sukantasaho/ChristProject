package com.christ.erp.services.dto.hostel.settings;

import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class HostelAdmissionsFacilityDTO {

	private int id;
	private String facilityDescription;
	private SelectDTO hostelAdmissionsDTO;
	private SelectDTO hostelFacilitySettingsDTO;
	private Boolean isChecked;
	private Boolean verifiedForCheckout;

}
