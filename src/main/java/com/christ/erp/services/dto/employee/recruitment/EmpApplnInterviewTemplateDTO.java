package com.christ.erp.services.dto.employee.recruitment;
import java.util.List;
import com.christ.erp.services.dto.common.ExModelBaseDTO;

public class EmpApplnInterviewTemplateDTO {
	
	public String id;
	public ExModelBaseDTO category;
	public String interviewName;
	public Boolean isPanelistCommentRequired;
	public List<EmpApplnInterviewTemplateGroupDTO> heading;
}
