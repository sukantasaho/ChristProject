package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErpDepartmentDTO {
	public String id;
	public String departmentName;
    public ErpDepartmentCategoryDTO category;
    public ExModelBaseDTO schoolName;
	public Integer userId;

}
