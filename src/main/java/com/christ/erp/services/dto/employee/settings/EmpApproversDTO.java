
package com.christ.erp.services.dto.employee.settings;

import java.util.List;

import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmpApproversDTO extends ModelBaseDTO {

	private String id;
	private SelectDTO types;
	private SelectDTO campus;
	private SelectDTO department;
	private SelectDTO erpCampusDepartmentMapping;
	private SelectDTO empCategory;
	private List<EmpApproversDetailsDTO> items;
	private String errorMsg;
	
}