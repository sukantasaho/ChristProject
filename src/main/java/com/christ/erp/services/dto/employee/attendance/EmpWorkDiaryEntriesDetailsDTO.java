package com.christ.erp.services.dto.employee.attendance;

import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmpWorkDiaryEntriesDetailsDTO {
	
	private int id;
	private String fromTime;
	private String toTime;
	private String totalTime;
    private SelectDTO activity;
    private String remarks;
	private String totalHour;
	private String otherActivity;
	private String otherActivityTotal;
	private String activityName;
	private String totalHours;
}
