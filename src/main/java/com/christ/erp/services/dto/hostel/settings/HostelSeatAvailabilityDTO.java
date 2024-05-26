package com.christ.erp.services.dto.hostel.settings;

import java.util.List;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class HostelSeatAvailabilityDTO {
	private int id;
	private SelectDTO hostelDTO;
	private SelectDTO academicYearDTO;
	private List<HostelSeatAvailabilityDetailsDTO> hostelSeatAvailabilityDetails;
	private Integer availableSeats;

}
