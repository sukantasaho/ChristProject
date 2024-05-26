package com.christ.erp.services.dto.employee.onboarding;

import java.time.LocalDate;
import java.util.List;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveAllotmentListDTO;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class CreateEmployeeIdDTO {
	public String id;
	public String erpCampusDepartmentMappingId;
	public String employeeNo;
	public String applicantName;
	public ExModelBaseDTO department;
	public ExModelBaseDTO campus;
	public ExModelBaseDTO timeZone;
	public ExModelBaseDTO leaveApprover;
	public ExModelBaseDTO leaveAuthoriser;
	public ExModelBaseDTO workDiaryApprover;
	public ExModelBaseDTO levelOneAppraiser;
	public ExModelBaseDTO levelTwoAppraiser;
//	public String joiningDate;
	public LocalDate joiningLocalDate;
	public Boolean isVacationApplicable;
	public List<EmpLeaveAllotmentListDTO> leaveAllotmentList;
	public char recordStatus;
}
