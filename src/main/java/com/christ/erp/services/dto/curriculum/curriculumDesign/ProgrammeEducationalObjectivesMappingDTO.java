package com.christ.erp.services.dto.curriculum.curriculumDesign;

import com.christ.erp.services.dto.curriculum.settings.DepartmentMissionVisionDetailsDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProgrammeEducationalObjectivesMappingDTO {

	private int id;
	private DepartmentMissionVisionDetailsDTO departmentMissionVisionDetailsDTO ;
	private int intrinsicValue;
	private String rationaleForMapping;
	
}
