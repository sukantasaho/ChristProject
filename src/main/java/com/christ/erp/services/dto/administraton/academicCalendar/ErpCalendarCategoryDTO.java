package com.christ.erp.services.dto.administraton.academicCalendar;

import java.util.List;

import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpCalendarCategoryDTO {
	private int id;
	private String categoryName;
	private Integer reminderDays;
	private List<SelectDTO> employeList;
}
