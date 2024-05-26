package com.christ.erp.services.dto.employee.recruitment;

import java.util.List;

public class EmpApplnInterviewTemplateGroupDetailsDTO {
	public String id;
	public String parameterName;
	public String parameterOrderNo;
	public String parameterMaxScore;
	public String totalScore;
	public String totalMaxScore;
	public String type;
	public String printHeadingName;
	public List<InteviewScoreEntryGroupDetailsDTO> internalInterviewers;
	public List<InteviewScoreEntryGroupDetailsDTO> externalInterviewers;
}
