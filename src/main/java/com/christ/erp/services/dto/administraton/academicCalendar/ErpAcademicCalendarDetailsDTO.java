package com.christ.erp.services.dto.administraton.academicCalendar;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErpAcademicCalendarDetailsDTO {
	
	private int id;
	private ErpCalendarDTO erpCalendarDTO;
	private LocalDate date;
	private Integer createdUsersId;
	private Integer modifiedUsersId;
	private char recordStatus;	
}