package com.christ.erp.services.dto.employee.leave;

import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmpLeaveAllocationDTO {
	public String id;
	public String leaveTypeName;
	public String leaveTypeId;
	public String leavesAllocated;
	public String leavesSanctioned;
	public String leavesRemaining;
	//public String year;
	public String leavesPending;
	private SelectDTO yearSelect;
}
