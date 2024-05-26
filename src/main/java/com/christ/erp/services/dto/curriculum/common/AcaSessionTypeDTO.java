package com.christ.erp.services.dto.curriculum.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcaSessionTypeDTO {
	private int id;
	private String sessionTypeName;
	private Integer totalSessionIntakesInYear;
	private String curriculumCompletionType;
}
