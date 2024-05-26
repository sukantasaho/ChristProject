package com.christ.erp.services.dto.employee.attendance;

import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmpTimeZoneEmployeewiseDTO {

    private int id;
    private SelectDTO empTimeZone;
    private SelectDTO emp;
    private char recordStatus;
}
