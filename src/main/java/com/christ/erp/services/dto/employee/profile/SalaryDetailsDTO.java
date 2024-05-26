package com.christ.erp.services.dto.employee.profile;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;
import org.apache.xpath.operations.Bool;

import java.math.BigDecimal;
import java.util.List;
@Getter
@Setter
public class SalaryDetailsDTO {
    private int id;
    private Boolean isCurrent;
    private SelectDTO payScaleType;
    private SelectDTO grade;
    private Integer year;
    private SelectDTO level;
    private SelectDTO scale;
    private SelectDTO cell;
    private List<PayScaleDetailsDTO> payScaleDetailsDTOList;
    private BigDecimal wageAmount;
    private BigDecimal grossPay;


    public SalaryDetailsDTO(){

    }
    public SalaryDetailsDTO(int id, boolean isCurrent, String payScaleType, Integer gradeId, String gradeName, Integer revisedYear,
                           Integer scaleId, String scaleName, Integer cellMatrixDetailId, Integer cellNo, Integer levelId,
                            String levelName, BigDecimal wageAmount, BigDecimal grossPay){
        this.id = id;
        this.isCurrent = isCurrent;
        this.year = revisedYear;
        this.wageAmount = wageAmount;
        this.grossPay = grossPay;
         if(!Utils.isNullOrEmpty(payScaleType)) {
            this.payScaleType = new SelectDTO();
            this.payScaleType.setLabel(payScaleType);
            this.payScaleType.setValue(payScaleType);
        }
       if(!Utils.isNullOrEmpty(gradeId)) {
            this.grade = new SelectDTO();
            this.grade.setLabel(gradeName);
            this.grade.setValue(Integer.toString(gradeId));
        }
        if(!Utils.isNullOrEmpty(scaleId)) {
            this.scale = new SelectDTO();
            this.scale.setLabel(scaleName);
            this.scale.setValue(Integer.toString(scaleId));
        }
        if(!Utils.isNullOrEmpty(cellMatrixDetailId)) {
            this.cell = new SelectDTO();
            this.cell.setLabel(Integer.toString(cellNo));
            this.cell.setValue(Integer.toString(cellMatrixDetailId));
        }
        if(!Utils.isNullOrEmpty(levelId)) {
            this.level = new SelectDTO();
            this.level.setLabel(levelName);
            this.level.setValue(Integer.toString(levelId));
        }
    }

}
