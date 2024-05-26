package com.christ.erp.services.dto.admission.admissionprocess;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdmAdmissionScheduleDatesDTO {
    private int id;
	private LocalDate admScheduleDate;
	private Boolean isSunday;
	private Boolean isHoliday;
	private Boolean isDateNotAvailable;
    private char recordStatus;
    private List<AdmAdmissionScheduleTimeDTO> admAdmissionScheduleTimeDTOList;
    private Integer admAdmissionScheduleId;
	private DayOfWeek dayName;
	private Integer selectedNoOfSeatInSlotPerDay;
	private Integer maxNoOfSeatInSlotPerDay;

}
