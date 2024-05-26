package com.christ.erp.services.dto.administraton.academicCalendar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.christ.erp.services.dto.student.common.StudentDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErpCalendarToDoListDTO {

	private int id;
	private String toDoNote;
	private LocalDate toDoDate;
	private boolean completed;
	private char recordStatus;
	private StudentDTO studentDTO;
	private Integer empId;
	private LocalDateTime toDoStart;
	private List<ErpCalendarPersonalDTO> erpCalendarPersonalDTOList;
}