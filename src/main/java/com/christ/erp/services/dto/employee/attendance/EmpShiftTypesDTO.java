package com.christ.erp.services.dto.employee.attendance;

import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;

public class EmpShiftTypesDTO extends LookupItemDTO{

	public String id;
	public String shiftName;
	public String shiftShortName;
	public boolean isWeeklyOff;
	public ExModelBaseDTO campus;
	public ExModelBaseDTO timeZone;
	public boolean disabled;
	public String rosterDate;
	public Integer empRosterAllotmentId;
	public String colorText;
}
