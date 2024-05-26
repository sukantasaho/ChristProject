package com.christ.erp.services.dto.employee.settings;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpApplnNumberGenerationDTO  {

	 private int empApplnNumberGenerationId;	
	 private Integer applnNumberFrom;
	 private Integer applnNumberTo;
	 private Integer calendarYear;
	 private Boolean isCurrentRange;
}
