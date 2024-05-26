package com.christ.erp.services.dto.employee;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter

public class EmpHolidayDTO {
	private List<EmpHolidayListDTO> holidayList;
	private boolean listDisable = false;
}
