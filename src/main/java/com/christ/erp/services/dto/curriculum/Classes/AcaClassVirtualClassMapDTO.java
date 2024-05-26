package com.christ.erp.services.dto.curriculum.Classes;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AcaClassVirtualClassMapDTO {
	
	private int id;
	private char recordStatus;
	private String baseClassName;
	private Integer baseClassId;
}