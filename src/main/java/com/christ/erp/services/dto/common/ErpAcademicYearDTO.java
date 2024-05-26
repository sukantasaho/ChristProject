package com.christ.erp.services.dto.common;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpAcademicYearDTO {

	private Integer id;	
	private Integer academicYear;
	private String academicYearName;
	private char recordStatus;
	private Boolean isCurrentAcademicYear;
	private List<ErpAcademicYearDetailsDTO> academicYearDetails ;
	private String value;
	private String label;
	private Boolean isCurrentAdmissionYear;
}
