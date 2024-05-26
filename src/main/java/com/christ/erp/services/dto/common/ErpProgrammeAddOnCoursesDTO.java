package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpProgrammeAddOnCoursesDTO {
	private int id;
	private SelectDTO erpCourseCategory;
	private Integer minNoOfCourses;
}
