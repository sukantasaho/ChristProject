package com.christ.erp.services.dto.employee.attendance;

import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class HigherAuthorityViewDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private SelectDTO campus;
    private SelectDTO department;
    private String empId;
    private String empName;
}
