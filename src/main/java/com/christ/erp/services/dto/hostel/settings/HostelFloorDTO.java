package com.christ.erp.services.dto.hostel.settings;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
 
public class HostelFloorDTO {
	public String id;
	public String floorNumber;
	public String roomCount;
	public List<HostelRoomsDTO> roomDetails;	
}
