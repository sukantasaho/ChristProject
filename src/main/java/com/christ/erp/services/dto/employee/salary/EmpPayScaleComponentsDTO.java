package com.christ.erp.services.dto.employee.salary;

import lombok.Data;
import java.math.BigDecimal;

import com.christ.erp.services.dto.common.SelectDTO;

@Data
public class EmpPayScaleComponentsDTO {
    private Integer id;
    private String salaryComponentName;
    private String salaryComponentShortName;
    private SelectDTO payScaleType;
    private Boolean isComponentBasic;
    private Integer salaryComponentDisplayOrder;
    private Boolean isCalculationTypePercentage;
    private BigDecimal percentage;
}
