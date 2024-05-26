package com.christ.erp.services.dto.employee.attendance;

import java.util.List;

import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;

public class DutyRosterAllotmentDTO extends ModelBaseDTO{

	public ExModelBaseDTO campus;
	public List<EmpRosterAllotmentDTO> weekList;
	public List<EmpRosterAllotmentDTO> dutyRosterAllotmentList;
	public String fromDate;
	public String toDate;
}
