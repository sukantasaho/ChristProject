package com.christ.erp.services.dto.curriculum.settings;

import java.util.List;

import com.christ.erp.services.dto.common.AttTypeDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ExamAssessmentTemplateAttendanceDTO {
	
	private int id;
    private List<AttTypeDTO> attTypeDTO;

}
