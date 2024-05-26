package com.christ.erp.services.dto.employee.recruitment;

import com.christ.erp.services.dto.common.ExModelBaseDTO;
import java.util.List;

public class EmpApplnAddtnlInfoHeadingDTO {
	
	public String id;
	public Boolean isTypeResearch;
	public ExModelBaseDTO category;
	public String groupHeading;
	public String displayOrder;
	public List<EmpApplnAddtnlInfoParameterDTO> parameters;
}