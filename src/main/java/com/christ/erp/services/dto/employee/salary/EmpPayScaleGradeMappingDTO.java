package com.christ.erp.services.dto.employee.salary;

import com.christ.erp.services.dto.common.ExModelBaseDTO;

import java.util.List;

public class EmpPayScaleGradeMappingDTO {
	public String id;
    public ExModelBaseDTO category;
    public ExModelBaseDTO grade;
    public ExModelBaseDTO revisedYear;
    public List<EmpPayScaleGradeMappingDetailDTO>  levels;
    public List<EmpPayScaleGradeMappingDetailDTO> payScaleMatrixDataList;
}
