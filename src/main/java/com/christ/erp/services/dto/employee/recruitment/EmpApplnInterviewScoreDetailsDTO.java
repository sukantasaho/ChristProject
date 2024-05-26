package com.christ.erp.services.dto.employee.recruitment;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmpApplnInterviewScoreDetailsDTO {

	private int interviewScoreDetailsId;
	private EmpApplnInterviewTemplateGroupDetailsDTO applnInterviewTemplateGroupDetailId;
	private String  parameterName;
	private Boolean autoCalculate;
	private Integer parameterOrderNumber;
	private Integer parameterMaxScore;
	private Integer scoreEntered;
}
