package com.christ.erp.services.dto.employee.appraisal;

import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;

public class EmpAppraisalInstructionDTO extends ModelBaseDTO {
	
	public String id;
	public String instructionName;
	public ExModelBaseDTO appraisalType;
	public String instructionContent;
}
