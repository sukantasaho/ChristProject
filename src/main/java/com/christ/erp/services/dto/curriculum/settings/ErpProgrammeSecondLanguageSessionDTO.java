package com.christ.erp.services.dto.curriculum.settings;

import java.util.List;

import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpProgrammeSecondLanguageSessionDTO {	
	private int id; 
	private List<SelectDTO> acaSessionDTOList;
}
