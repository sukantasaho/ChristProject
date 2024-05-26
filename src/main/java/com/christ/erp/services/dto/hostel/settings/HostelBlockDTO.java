package com.christ.erp.services.dto.hostel.settings;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class HostelBlockDTO {
	
	private Integer id;
	private String blockName;
	private HostelDTO hostelDTO;
	public List<HostelBlockUnitDTO> hostelBlockUnitDTOSet; 
	private HostelBlockUnitDTO hostelBlockUnitDTO;
}
