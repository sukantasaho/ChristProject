package com.christ.erp.services.dto.common;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErpProgrammeDepartmentMappingDTO {
	
	private int id;
    private List<SelectDTO> erpDepartmentList;
}
