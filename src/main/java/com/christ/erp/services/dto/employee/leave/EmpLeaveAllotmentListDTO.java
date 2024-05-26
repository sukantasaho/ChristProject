package com.christ.erp.services.dto.employee.leave;
import com.christ.erp.services.dto.common.ExModelBaseDTO;

public class EmpLeaveAllotmentListDTO {
	public Integer leavetypeId;
	public String leavetypeName;
	public Integer allottedLeaves;	
	public Integer accumulatedLeave;
	public Boolean isApplicable;
	public ExModelBaseDTO addToLeaveType;
	public Integer leaveallotedId;
	public String leaveInitialiseMonth;
	public Integer displayOrder;
	public Boolean isInitializationRequired;
}
