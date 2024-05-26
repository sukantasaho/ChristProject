package com.christ.erp.services.dto.employee.attendance;

import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpTimeZoneDetailsDTO {
	
	private int id;
	private boolean isExempted;
	private String dayName;
	private LocalTime empInTime;
	private LocalTime inTimeEnds;
	private LocalTime empOutTime;
	private LocalTime halfDayStartTime;
	private LocalTime halfDayEndTime;
	private char recordStatus;
	private LocalTime timeInStartTime;
	private LocalTime timeOutEndTime;
	private  boolean isOneTimePunch;
	private LocalTime outTimeStart;
}
