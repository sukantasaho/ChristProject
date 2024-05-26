package com.christ.erp.services.dto.hostel.settings;

import java.util.List;

import com.christ.erp.services.dto.common.LookupItemDTO;

public class RoomMasterDTO {

	public String id;
	public LookupItemDTO blockName;
	public LookupItemDTO hostelName;
	public LookupItemDTO unit;
	public String totalFloors;
	public String totalRoomCount;
	public List<HostelFloorDTO> floorDetails;
}
