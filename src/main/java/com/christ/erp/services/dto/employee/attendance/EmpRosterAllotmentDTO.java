package com.christ.erp.services.dto.employee.attendance;

import java.util.List;

public class EmpRosterAllotmentDTO {
	
	public int id;
	public String rosterDate;
	public Integer empId;
	public Integer empShiftTypesId;
	public String empName;
	public List<EmpShiftTypesDTO> shiftTypeList;
	public boolean disabled;
	public String day;
	
}
