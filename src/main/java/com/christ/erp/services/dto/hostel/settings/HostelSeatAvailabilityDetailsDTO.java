package com.christ.erp.services.dto.hostel.settings;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HostelSeatAvailabilityDetailsDTO {
	
	private int id;
	private HostelRoomTypeDTO hostelRoomTypeDTO;
	private Integer totalSeats;
	private Integer availableSeats;
}
