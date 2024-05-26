package com.christ.erp.services.dto.student.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StudentApplnSelectionProcessRescheduleDTO {

	public Integer id;
	public LocalDateTime requestReceivedDateTime;
	public LocalDate rescheduleRequestedDate;
	public LocalTime rescheduleRequestedTime;

}
