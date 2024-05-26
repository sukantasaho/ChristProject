package com.christ.erp.services.dto.employee;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import java.util.List;
public class EmpPositionRoleDTO extends ModelBaseDTO {
    public ExModelBaseDTO campus;
    public String processType;
    public List<EmpPositionRoleSubDTO> Levels;
}
