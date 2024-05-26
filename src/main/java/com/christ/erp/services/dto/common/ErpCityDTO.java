package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpCityDTO {
	public int id;
	public String cityName;
	public SelectDTO erpStateDTO;
}
