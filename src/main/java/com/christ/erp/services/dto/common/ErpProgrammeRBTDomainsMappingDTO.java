package com.christ.erp.services.dto.common;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpProgrammeRBTDomainsMappingDTO {
	private Integer id;
	private List<SelectDTO> erpRBTDomainsList;	
}
