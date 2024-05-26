package com.christ.erp.services.dto.administraton.academicCalendar;

import com.christ.erp.services.dto.employee.common.EmpDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpCalendarCategoryRecipientsDTO {
	private int id;
	private ErpCalendarCategoryDTO erpCalendarCategoryDTO;
	private EmpDTO empDTO;
}