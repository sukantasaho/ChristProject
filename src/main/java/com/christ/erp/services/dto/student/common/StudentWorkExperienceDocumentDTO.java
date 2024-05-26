package com.christ.erp.services.dto.student.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentWorkExperienceDocumentDTO {

	 private int id;
	 private String documentsUrl;
	 private String fileName;
	 private String extension;
	 private char recordStatus;
	 private Boolean newFile;
}
