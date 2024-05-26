package com.christ.erp.services.dto.curriculum.settings;

import java.util.List;
import com.christ.erp.services.dto.common.LookupItemDTO;

public class DepartmentMissionVisionDTO{
	
	public String id;
	public LookupItemDTO department;
	public String departmentVision;
	public String departmentMission;
	public List<DepartmentMissionVisionDetailsDTO> levels;
}
