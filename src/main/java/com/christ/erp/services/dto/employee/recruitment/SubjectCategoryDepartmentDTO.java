package com.christ.erp.services.dto.employee.recruitment;

import java.util.HashSet;
import java.util.List;

import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter

public class SubjectCategoryDepartmentDTO extends ModelBaseDTO {

    public List<ExModelBaseDTO> department;
    public Integer[] empCatgoryDepartments;
    public HashSet<Integer> recordIds;
    public SelectDTO category;
    private SelectDTO erpDepartment;
}