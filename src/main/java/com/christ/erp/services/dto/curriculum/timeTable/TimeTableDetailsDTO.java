package com.christ.erp.services.dto.curriculum.timeTable;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeTableDetailsDTO {
	
	private int id;
	private String dayName; 
	private Integer dayOfWeek;
	private List<TimeTablePeriodDetailsDTO> timeTablePeriodDetailsList;
}
