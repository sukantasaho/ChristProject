package com.christ.erp.services.dto.curriculum.settings;

import java.util.List;

import com.christ.erp.services.dto.common.ErpCampusProgrammeMappingDTO;
import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpProgrammeSpecializationSessionMappingDTO {
	private int id; 
	private List<SelectDTO> acaSessionDTOList;
	private ErpCampusProgrammeMappingDTO erpCampusProgrammeMappingDTO;
//	private SelectDTO erpCampusProgrammeMapping;
}
