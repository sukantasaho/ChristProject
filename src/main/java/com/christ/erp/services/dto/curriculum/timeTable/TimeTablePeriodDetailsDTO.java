package com.christ.erp.services.dto.curriculum.timeTable;

import java.math.BigDecimal;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeTablePeriodDetailsDTO {

	private int id;
	private String periodName;
	private LocalTime fromTime;
	private LocalTime toTime;
	private Integer periodOrder;
	private BigDecimal durationInHour;
}
