package com.christ.erp.services.dto.curriculum.curriculumDesign;

import java.util.List;

import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProgrammeOutcomeDefinitionsDTO {
	
	private int batchwiseSettingId;
	private String degreeLevel;
	private String programme;
	private List<String> programmeOfferedCampus;
	private Boolean isDefined;
	private List<ProgrammeSpecificOutcomeDTO> programmeOutcomeList;
	private List<ProgrammeSpecificOutcomeDTO> programmeSpecificOutcomeList;
	private List<ProgrammeSpecificOutcomeDTO> programmeLearningGoalsList;
	private List<ProgrammeSpecificOutcomeDTO> programEducationalObjectiveList;
	private SelectDTO academicYear;
	private List<SelectDTO> programmeList;
	private SelectDTO fromYear;
	private SelectDTO toYear;
	private Boolean isPoDefined;
	private Boolean isPsoDefined;
	private Boolean isPlgDefined;
	private Boolean isPeoDefined;

	
	
}
