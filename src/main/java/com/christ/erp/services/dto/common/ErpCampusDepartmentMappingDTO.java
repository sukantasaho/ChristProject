package com.christ.erp.services.dto.common;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ErpCampusDepartmentMappingDTO {
	private int id;
	private ErpCampusDTO erpCampusDTO;
	private ErpDepartmentDTO erpDepartmentDTO;
	private SelectDTO erpCampus;
	private List<SelectDTO> erpCampusList;
	private SelectDTO erpDepartmentSelect;
	private int empId;
	
	
	public ErpCampusDepartmentMappingDTO(int id,String departmentName,String campusName) {
		this.id = id;
		this.erpDepartmentSelect = new SelectDTO();
		this.erpDepartmentSelect.setLabel(departmentName);
		this.erpCampus =  new SelectDTO();
		this.erpCampus.setLabel(campusName);
		
	}
}
