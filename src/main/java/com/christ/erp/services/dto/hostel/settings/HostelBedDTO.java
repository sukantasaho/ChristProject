package com.christ.erp.services.dto.hostel.settings;

import com.christ.erp.services.dto.student.common.StudentDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class HostelBedDTO {
	
	public String id;
	public String bedName;
	private StudentDTO studentDTO;
	private Boolean isOccupied;
}
