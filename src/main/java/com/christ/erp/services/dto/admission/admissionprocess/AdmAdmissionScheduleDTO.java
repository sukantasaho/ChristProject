package com.christ.erp.services.dto.admission.admissionprocess;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdmAdmissionScheduleDTO {	
    private int id;
    private SelectDTO erpAcademicYear;
    private SelectDTO erpCampus;
	private LocalDate admScheduleFromDate;
	private LocalDate admScheduleToDate;
	private String saturdayEndTimeSlot;
    private char recordStatus;
    private List<AdmAdmissionScheduleDatesDTO> admAdmissionScheduleDatesDTOList;
    private LocalDate extendedDate;
    private boolean success;
    private Boolean isSundayInclude;
	private Boolean isHolidayInclude;
}