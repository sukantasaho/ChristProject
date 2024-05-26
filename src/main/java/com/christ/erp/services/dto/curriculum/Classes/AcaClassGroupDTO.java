package com.christ.erp.services.dto.curriculum.Classes;

import java.util.List;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.curriculum.settings.AcaDurationDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AcaClassGroupDTO {
	
	private int id;
	private String classGroupName;
	private SelectDTO acaCampusDTO;
	private SelectDTO acaDepartmentDTO;
	private SelectDTO acaCourseDTO;
	private SelectDTO attActivityDTO;
	private SelectDTO academicYearId;
	private SelectDTO sessionGroupId;
	private AcaDurationDTO acaDurationDTO;
	private String classNameList;
	private List<AcaClassGroupDetailsDTO> acaClassGroupDetailsDTO;
}
