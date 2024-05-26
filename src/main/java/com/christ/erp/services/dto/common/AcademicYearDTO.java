package com.christ.erp.services.dto.common;

import java.util.List;

public class AcademicYearDTO extends ModelBaseDTO {
	public String year;
	public ExModelBaseDTO academicYear;
	public String academicYearName;
	public String isCurrent;
	public char recordStatus;
	public List<AcademicYearDetailsDTO> campuses;

}