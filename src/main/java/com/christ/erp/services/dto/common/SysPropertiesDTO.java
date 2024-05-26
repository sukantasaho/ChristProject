package com.christ.erp.services.dto.common;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SysPropertiesDTO {

	private int id;
	private String propertyName;
	private String propertyValue;
	private Boolean commonProperty;
	private String propertyType;
	private String remarks;
	private List<SysPropertiesDetailsDTO> sysPropertiesDetailsDTOList;
	private List<SelectDTO> erpCampusDTOList;
	private List<SelectDTO> erpLocationDTOList;
	private   Boolean isEdit;
}
