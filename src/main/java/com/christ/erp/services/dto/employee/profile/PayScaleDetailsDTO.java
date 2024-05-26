package com.christ.erp.services.dto.employee.profile;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class PayScaleDetailsDTO {
    private int id;
    private Integer payScaleId;
    private String componentName;
    private SelectDTO component;
    private String payScaleType;
    private Boolean isPayScaleBasic;
    private Integer displayOrder;
    private Boolean isCalculationTypePercentage;
    private BigDecimal percentage;
    private BigDecimal componentValue;

    public PayScaleDetailsDTO(){

    }
    public PayScaleDetailsDTO(int id, Integer payScaleId, Integer componentId, String componentShortName, String payScaleType, Boolean isPayScaleBasic,
                              Integer displayOrder, Boolean isCalculationTypePercentage,BigDecimal percentage, BigDecimal componentValue){
        this.id = id;
        this.payScaleId = payScaleId;
        if(!Utils.isNullOrEmpty(componentId)) {
            this.component = new SelectDTO();
            this.component.setLabel(componentShortName);
            this.component.setValue(Integer.toString(componentId));
        }
        this.displayOrder = displayOrder;
        this.payScaleType = payScaleType;
        this.isPayScaleBasic = isPayScaleBasic;
        this.isCalculationTypePercentage = isCalculationTypePercentage;
        this.percentage = percentage;
        this.componentValue = componentValue;
    }

    public PayScaleDetailsDTO(Integer id, String salaryComponentShortName, String payScaleType, Boolean isComponentBasic, Integer salaryComponentDisplayOrder,
                              Boolean isCalculationTypePercentage, BigDecimal percentage, Integer displayOrder){
        this.id = id;
        this.component = new SelectDTO();
        this.component.setLabel(salaryComponentShortName);
        this.component.setValue(Integer.toString(id));
        this.displayOrder = displayOrder;
        this.payScaleType = payScaleType;
        this.isPayScaleBasic = isComponentBasic;
        this.isCalculationTypePercentage = isCalculationTypePercentage;
        this.percentage = percentage;
    }

    public PayScaleDetailsDTO(int id, BigDecimal levelCellValue){
        this.id = id;
        this.setComponentValue(levelCellValue);
    }


}
