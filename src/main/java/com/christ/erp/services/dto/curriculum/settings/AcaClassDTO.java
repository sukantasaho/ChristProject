package com.christ.erp.services.dto.curriculum.settings;

import java.util.List;

import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.curriculum.Classes.AcaClassVirtualClassMapDTO;
import com.christ.erp.services.dto.curriculum.common.AcaSessionDTO;
import com.christ.erp.services.dto.employee.common.ErpProgrammeDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AcaClassDTO {
	
	private int id;
	private String className;
	private List<AcaClassVirtualClassMapDTO> acaClassVirtualClassMapDTOList;
	private String classCode;
	private String specializationName;
	private boolean virtualClass;
	private boolean havingVirtualClass;
	private List<SelectDTO> classNameList;
	private String programmeName;
	private SelectDTO campusSelect;
	private SelectDTO departmentSelect;
	private List<SelectDTO> selectedClassList;
	private SelectDTO academicYear;
	private SelectDTO levelId;
	private SelectDTO sessionGroup;
	private AcaDurationDTO acaDurationDTO;
	private List<ErpProgrammeDTO> erpProgrammeDTOList;
	private SelectDTO acaDurationDetailDTO;
	private String section;
	private String campusCode;
	private SelectDTO erpRooms;
	private ErpProgrammeDTO erpProgrammeDTO;
	private AcaSessionDTO sessionDTO;
	private String sessionType;
}
