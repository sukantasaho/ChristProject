package com.christ.erp.services.dto.employee.salary;

import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;

import java.util.List;

public class PayScaleMappingDTO extends ModelBaseDTO {
    public ExModelBaseDTO category;
    public ExModelBaseDTO grade;
    public String revisedYear;
    public List<PayScaleMappingItemDTO> levels;
}
