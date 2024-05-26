package com.christ.erp.services.dto.curriculum.timeTable;

import java.util.List;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CourseWiseScheduleDetailsDTO {

	private int id;
	private String scheduleName;
	private SelectDTO courseNameAndCode;
	private List<SelectDTO> facultyList;
}
