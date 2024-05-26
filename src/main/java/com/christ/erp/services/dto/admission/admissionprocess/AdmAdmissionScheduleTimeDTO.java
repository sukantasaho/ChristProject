package com.christ.erp.services.dto.admission.admissionprocess;

import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdmAdmissionScheduleTimeDTO {

	private int id;
	private String admScheduleTimeSlot;
	private Integer maxNoOfSeatInSlot;
	private Integer selectedNoOfSeatInSlot;
	private char recordStatus;
	private Integer AdmAdmissionScheduleDatesId;
}
