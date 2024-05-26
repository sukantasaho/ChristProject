package com.christ.erp.services.dto.curriculum.curriculumDesign;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProgrammeLearningOutcomeDTO {
	
	private int id;
	private String referenceNumber;
	private BigDecimal referenceNoOrder;
	private String statement;
	

}
