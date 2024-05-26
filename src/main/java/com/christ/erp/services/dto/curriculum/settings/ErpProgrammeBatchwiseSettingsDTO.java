package com.christ.erp.services.dto.curriculum.settings;

import java.util.List;

import com.christ.erp.services.dto.common.ErpProgrammeAddOnCoursesDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.curriculum.common.AcaSessionTypeDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpProgrammeBatchwiseSettingsDTO {
	private int id;
	private Boolean isSecondLanguage;
	private Boolean isSpecialization;
	private String programAdmissionEligibility;
	private String programObjectives;
	private String introductionToTheProgram;
	private Integer totalNoOfScheme;
//	private Integer schemeDuration;
	private Integer minNoOfSchemeForGraduation;
	private Integer minNoOfCreditsForGraduation;
	private String totalNoOfYears;
	private Integer minCoreCourses;
	private Integer minElectiveCourses;
	private Boolean isDissertationRequired;
	private Integer maxElectiveFromSameDisciplinary;
	private Integer maxElectiveFromOtherDisciplinary;
	private String programmeCode;
	private AcaSessionTypeDTO acaSessionType;
	private SelectDTO erpProgramme;
	private SelectDTO batchYear;
	private List<SelectDTO> erpProgrammeSecondLanguageSessionList;
	private List<ErpProgrammeSpecializationMappingDTO> erpProgrammeSpecializationMappingDTOList;
	private List<SelectDTO> erpProgrammeAccreditationList;
	private List<ErpCampusProgrammeMappingDetailsDTO> erpCampusProgrammeMappingDetailsDTOList;
	private List<ObeProgrammeOutcomeDTO> obeProgrammeOutcomeDTOList;
	private List<AcaBatchDTO> acaBatchDTOList;
	private List<ErpProgrammeSpecializationSessionMappingDTO> erpProgrammeSpecializationSessionMappingDTOList;
	private List<ErpProgrammeAddOnCoursesDTO> erpProgrammeAddOnCoursesDTOList;
}
