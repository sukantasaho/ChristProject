package com.christ.erp.services.dto.employee.settings;

import java.util.List;

import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.common.EmpCampusDeaneryDepartmentDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErpAssignTitleDTO extends ModelBaseDTO {

	public ExModelBaseDTO user;
	public ExModelBaseDTO empTitle;
	public ExModelBaseDTO departmentCampus;
	public List<EmpCampusDeaneryDepartmentDTO> campusDeaneryDepartmentDTOs;
	public String[] checked;
	public List<Integer> editIds; 
}
