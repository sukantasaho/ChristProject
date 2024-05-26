package com.christ.erp.services.dto.curriculum.curriculumDesign;

import java.math.BigDecimal;
import java.util.List;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnAdvertisementImagesDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProgrammeSpecificOutcomeDTO {
	
	private int id;
	private String referenceNumber;
	private String statement;
	private BigDecimal referenceNoOrder;
	private List<SelectDTO> graduateAttributes;
	private SelectDTO approvedBy;
	private String comments;
	private List<EmpApplnAdvertisementImagesDTO> uploadDocuments;
	private List<ProgrammeLearningOutcomeDTO> programmeLearningOutcomeList;
	private List<ProgrammeEducationalObjectivesMappingDTO> programmeEducationalObjectivesMappingDTO;
	
}
