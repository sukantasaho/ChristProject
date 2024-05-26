package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SysPropertiesDetailsDTO {
	private int id;
	private SelectDTO erpCampusDTO;
	private SelectDTO erpLocationDTO;
	private String propertyDetailValue;
}
