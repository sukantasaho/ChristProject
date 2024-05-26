package com.christ.erp.services.dto.admission.applicationprocess;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmPrerequisiteExamDTO {

	private int id;
	private String examName;
	private String examCode;
	private char recordStatus;
	private Integer parentId;
    private Integer score;
}
