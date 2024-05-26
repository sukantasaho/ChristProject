package com.christ.erp.services.dto.employee.recruitment;

import java.util.List;

import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;

public class EmpApplnInterviewTemplateGroupDTO {
	public String id;
	public String templateGroupHeading;
	public String headingOrderNo;
	public List<EmpApplnInterviewTemplateGroupDetailsDTO> parameters;
}
