package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpCampusDTO extends ModuleDTO {

	public Integer overrideId;
	public String campusName;
	public String shortName;
	public Boolean isGranted = false;
	public Boolean isAdditionalPrivilege;
//	public int id;
	private Boolean commonPrivilege = false;
	private SelectDTO campusList;
	private String campusId;

	@Override
	public int compareTo(ModuleDTO obj) {
		return this.id.compareTo(obj.id);
	}
}
