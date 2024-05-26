package com.christ.erp.services.dto.curriculum.timeTable;

import java.util.List;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TemplateMasterDTO {

	private int id;
	private List<SelectDTO> campus;
	private String nameOfThePeriodTemplate;
	private Integer noOfPeriods;
	private Integer periodDurationInMinutes;
	private Boolean isStatic;
	private List<TimeTableDetailsDTO> timeTableDetailsList;
	
}
