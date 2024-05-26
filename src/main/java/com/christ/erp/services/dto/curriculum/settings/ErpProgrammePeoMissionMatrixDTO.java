package com.christ.erp.services.dto.curriculum.settings;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpProgrammePeoMissionMatrixDTO {
	private int id;
	private Integer intrinsicValue;
	private String rationaleForMapping;
	private DepartmentMissionVisionDetailsDTO departmentMissionVisionDetailsDTO;
}
