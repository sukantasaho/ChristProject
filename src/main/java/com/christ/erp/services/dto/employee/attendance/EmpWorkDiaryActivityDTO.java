package com.christ.erp.services.dto.employee.attendance;

import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class EmpWorkDiaryActivityDTO {
	private int id;
	private String activityName;
	private boolean isForTeaching;
	private SelectDTO mainActivity;
	private SelectDTO depatment;
	
}
