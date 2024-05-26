package com.christ.erp.services.dto.curriculum.settings;

import com.christ.erp.services.dto.common.ErpCampusProgrammeMappingDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpCampusProgrammeMappingDetailsDTO {
	private int id; 
	private Integer approvedIntake;
	private Integer revisedIntake;
	private ErpCampusProgrammeMappingDTO erpCampusProgrammeMappingDTO;
}
