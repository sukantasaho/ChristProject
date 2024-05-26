package com.christ.erp.services.dto.employee.leave;
import java.util.List;
import java.util.Set;
import com.christ.erp.services.dto.common.ModelBaseDTO;

public class EmpLeaveAllotmentDTO extends ModelBaseDTO {
	public String leaveCategoryId;
	public String leaveCategoryName;
	public String month;	
	public List<EmpLeaveAllotmentListDTO> leaveAllotment;	
	public Set<Integer> allomentIds;
}
