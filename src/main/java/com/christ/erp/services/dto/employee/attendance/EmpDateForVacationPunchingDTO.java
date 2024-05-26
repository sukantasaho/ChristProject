package com.christ.erp.services.dto.employee.attendance;

import java.util.List;

import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.common.EmpCampusDeaneryDepartmentDTO;

public class EmpDateForVacationPunchingDTO extends ModelBaseDTO  {
	
	public ExModelBaseDTO empCategory;
	public List<ExModelBaseDTO> deneary;
	public List<ExModelBaseDTO> department;
    public String vacationPunchingStartDate;
	public String vacationPunchingEndDate;
	public String description;
	public String[] checked;
	public List<EmpCampusDeaneryDepartmentDTO> campusDeaneryDepartmentDTOs;  
}
