package com.christ.erp.services.dto.administraton.academicCalendar;

import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpCalendarUserTypesDTO {
	private int id;	
	private String userType;
	private boolean isStudent;
	private SelectDTO empEmployeeCategoryDTO;
}
