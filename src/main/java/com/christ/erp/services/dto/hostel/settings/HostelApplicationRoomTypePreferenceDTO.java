package com.christ.erp.services.dto.hostel.settings;

import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class HostelApplicationRoomTypePreferenceDTO { 

	private int id;
	private SelectDTO hostelRoomTypesDTO;
	private Integer preferenceOrder;	
	
}
