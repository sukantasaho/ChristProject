package com.christ.erp.services.dto.employee.common;

import java.util.List;

import com.christ.erp.services.dto.common.ModelBaseDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmpCampusDeaneryDepartmentDTO extends ModelBaseDTO {
	public String value;
    public String label;
    public List<EmpCampusDeaneryDTO> children;
}
