package com.christ.erp.services.dto.employee.common;

import java.util.List;

import com.christ.erp.services.dto.admission.applicationprocess.ErpResidentCategoryDTO;
import com.christ.erp.services.dto.common.ErpCampusProgrammeMappingDTO;
import com.christ.erp.services.dto.common.ErpProgrammeAddtnlDetailsDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.curriculum.common.ErpProgrammeApprovalLevelMappingDTO;
import com.christ.erp.services.dto.curriculum.settings.ErpProgrammeBatchwiseSettingsDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpProgrammeDTO {
	public String label;
	public String value;
	private int id;
	private String programmeName;
	private SelectDTO coordinatingDepartment;
	private SelectDTO erpProgrammeDegree;
	private SelectDTO erpDeanery;
	private List<ErpProgrammeAddtnlDetailsDTO> erpProgrammeAddtnlDetailsDTOList;
	private List<SelectDTO> erpProgrammeDepartmentList;
	private List<ErpCampusProgrammeMappingDTO> erpCampusProgrammeMappingDTOList;
	private List<ErpProgrammeBatchwiseSettingsDTO> erpProgrammeBatchwiseSettingsDTOList;
	private String programmeCode;
	private List<SelectDTO> erpProgrammeRBTDomainsList ;
	private List<ErpProgrammeApprovalLevelMappingDTO> erpProgrammeApprovalLevelMappingDTOList;
	private SelectDTO erpResidentCategory;
	private Boolean isBatchWise;
	private Boolean isLatestProgramme;
	private List<ErpResidentCategoryDTO> erpResidentCategoryDTO;
	private SelectDTO ProgrammeClasses;
	private List<SelectDTO> programmeClassList;
}
