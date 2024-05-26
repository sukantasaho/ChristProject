package com.christ.erp.services.dto.administraton.academicCalendar;

import java.util.List;

import com.christ.erp.services.dto.student.common.StudentDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErpCalendarPersonalDTO {

	private int id;
	private String eventsNote;
	private String importantPriority;
	private boolean completed;
	private StudentDTO studentDTO;
	private char recordStatus;
	private boolean important;
	private String color;
	private ErpCalendarToDoListDTO erpCalendarToDoListDTO;
	private ErpCalendarDTO erpCalendarDTO;
	private Integer empId;
	private List<ErpReminderNotificationsDTO> erpReminderNotificationsDTOList;
	private String name;
}