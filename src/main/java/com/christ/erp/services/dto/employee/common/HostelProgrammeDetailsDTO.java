package com.christ.erp.services.dto.employee.common;
import java.util.List;
import com.christ.erp.services.dto.common.ModelBaseDTO;

public class HostelProgrammeDetailsDTO extends ModelBaseDTO {
    public String value;
    public String label;
    public List<String> checked;
    public List<ErpProgrammeLevelDTO> children;
}
