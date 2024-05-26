package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ExamAssessmentRatioDTO {
	
	private int id;
    private String assessmentRatio;
    private String ciaPercentage;
    private String esePercentage;

}
