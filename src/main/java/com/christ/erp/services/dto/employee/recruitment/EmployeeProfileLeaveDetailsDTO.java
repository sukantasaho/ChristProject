package com.christ.erp.services.dto.employee.recruitment;

import java.util.List;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveAllocationDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmployeeProfileLeaveDetailsDTO {
	public LookupItemDTO leaveCategory;
	public List<EmpLeaveAllocationDTO> leaveTypeDetails;
//	private SelectDTO yearSelectDTO;
}
