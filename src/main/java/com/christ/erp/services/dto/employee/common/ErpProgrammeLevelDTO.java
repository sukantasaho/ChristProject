package com.christ.erp.services.dto.employee.common;
import java.util.List;

import com.christ.erp.services.dto.common.ModelBaseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpProgrammeLevelDTO extends ModelBaseDTO {
    public String label;
    public String value;
    public List<ErpProgrammeDTO> children;
    private int id;
}
