package com.christ.erp.services.dbobjects.common;

import com.christ.erp.services.dto.employee.common.ErpProgrammeLevelDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpProgrammeDegreeDTO {

    public Integer id;
    public String programme_degree;
    public ErpProgrammeLevelDTO erpProgrammeLevelDTO;

}
