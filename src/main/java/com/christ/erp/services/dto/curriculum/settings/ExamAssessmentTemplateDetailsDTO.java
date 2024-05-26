package com.christ.erp.services.dto.curriculum.settings;

import com.christ.erp.services.dto.common.ExamAssessmentCategoryDTO;
import com.christ.erp.services.dto.common.ExamAssessmentModeDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ExamAssessmentTemplateDetailsDTO {
	
	private int id;
    private ExamAssessmentCategoryDTO examAssessmentCategoryDTO;
    private Integer categoryOrder;
    private Integer categoryCiaTotalMarks;
    private Integer categoryCiaScaleDownTo;
    private Integer categoryCiaMinMarks; 
    private Integer categoryEseTotalMarks; 
    private Integer categoryEseScaleDownTo;
    private Integer categoryEseMinMarks;
    private ExamAssessmentModeDTO examAssessmentModeDTO;
    private boolean isQPfromDB; 
    private Integer durationOfExam;
    private Integer noOfEvaluators;

}
