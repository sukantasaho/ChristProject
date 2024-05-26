package com.christ.erp.services.dto.curriculum.settings;

import java.util.List;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ExamAssessmentTemplateDTO {
	
	private int id;
    private SelectDTO examAssessmentRatio;
    private SelectDTO acaCourseType;
    private String templateName;
    private Integer totalScaleDownMarks;
    private Integer totalMinMarks;
    private String maxTotal;
    private boolean ciaCheck;
    private Integer ciaTotalMarks;
    private Integer ciaScaleDownTo;
    private Integer ciaMinMarks;
    private boolean eseCheck;
    private Integer noOfAssessment;
    private Integer eseTotalMarks;
    private Integer eseScaleDownTo;
    private Integer eseMinMarks;
    private Integer attendanceTotal;
    private ExamAssessmentTemplateAttendanceDTO examAssessmentTemplateAttendanceDTO;
    private List<ExamAssessmentTemplateDetailsDTO> examAssessmentTemplateDetailsDTO;
    
}
