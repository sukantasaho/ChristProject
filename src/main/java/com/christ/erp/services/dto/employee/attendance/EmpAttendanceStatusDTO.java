package com.christ.erp.services.dto.employee.attendance;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmpAttendanceStatusDTO {

    private int id;
    private String statusCode;
    private  String statusDescription;
    private  String statusColorCode;
    private Boolean isLabel;
    private  Boolean isColorChange;
    private char recordStatus;

}
