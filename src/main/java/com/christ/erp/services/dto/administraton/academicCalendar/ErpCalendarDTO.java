package com.christ.erp.services.dto.administraton.academicCalendar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.christ.erp.services.dto.common.ErpAcademicYearDTO;
import com.christ.erp.services.dto.common.ErpLocationDTO;
import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErpCalendarDTO { 
	private int id;
	private ErpAcademicYearDTO erpAcademicYearDTO;
	private ErpLocationDTO erpLocationDTO;
	private String activitiesEvents;
	private LocalDate fromDate;
	private LocalDate toDate;
	private String monthAndYear;
	private boolean published;
	private char recordStatus;
	private SelectDTO categorySelect;
	private List<SelectDTO> existCampusList;
	private List<SelectDTO> existUserTypeList;
	private SelectDTO locationSelect;
	private SelectDTO academicYearSelect;
	private SelectDTO importToYear;
	private SelectDTO importFromYear;
	private LocalDate importFromDate;
	private LocalDate importToDate;
	private Integer displayOrder;
	private List<ErpCalendarPersonalDTO> erpCalendarPersonalDTOList;
	private LocalDateTime start;
	private LocalDateTime end;
	private String title;
	private String color;
	private boolean allEvent;
	private String tableName;
	private boolean important;
	private ErpCalendarToDoListDTO erpCalendarToDoListDTO; 
}