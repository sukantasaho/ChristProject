package com.christ.erp.services.dto.employee.salary;

import org.jetbrains.annotations.NotNull;

public class EmpPayScaleMatrixDetailDTO implements Comparable<EmpPayScaleMatrixDetailDTO> {

    public Integer id;
    public String displayOrder;
    public Integer levelCellNo;
    public String levelCellValue;
    public Integer mappingDetailId;

    @Override
    public int compareTo(@NotNull EmpPayScaleMatrixDetailDTO obj) {
        return this.displayOrder.compareTo(obj.displayOrder);
    }
}
