package com.christ.erp.services.dto.employee.appraisal;

import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;

public class EmpAppraisalSessionDTO extends ModelBaseDTO {	
	public String id;
	public ExModelBaseDTO academicYear;
	public ExModelBaseDTO location;
	public ExModelBaseDTO employeeCategory;
	public ExModelBaseDTO year;
	public ExModelBaseDTO month;
	public String sessionName;
	public ExModelBaseDTO type;
}
