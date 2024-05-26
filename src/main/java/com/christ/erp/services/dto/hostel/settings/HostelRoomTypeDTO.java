package com.christ.erp.services.dto.hostel.settings;

import java.util.List;
import com.christ.erp.services.dto.common.LookupItemDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class HostelRoomTypeDTO {
	public String id;
	public LookupItemDTO hostel;
	public String roomType;
	public String totalOccupants;
	public String roomTypeDescription;
	public LookupItemDTO roomTypeCategory;
	public List<HostelRoomTypeDetailsDTO> hostelFacilitySetting;
	public List<HostelRoomTypeDetailsDTO> roomImages;
	private Integer availableSeats;
    private Integer totalSeats;
    private Integer allocatedSeat;
	
}
