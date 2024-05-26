package com.christ.erp.services.dto.hostel.settings;

import java.util.List;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.hostel.common.CommonHostelDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class HostelRoomsDTO {
	public String id;
	public String roomNumber;
	private SelectDTO hostelRoomTypeDTO;
	public CommonHostelDTO roomType;
	public List<HostelBedDTO> bedDetails; 
	
}
