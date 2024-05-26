package com.christ.erp.services.dto.common;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SysFunctionDTO extends ModelBaseDTO implements Comparable<SysFunctionDTO> {
	
	public String permissionName;
	public boolean granted;
	public Boolean isAdditionalPrivilege;
	public List<ErpCampusDTO> campusList;
	@Override
	public int compareTo(@NotNull SysFunctionDTO dto) {
		  return this.id.compareTo(dto.id);
	}
	
//	private String id;
	private String overrideId;
	private String functionName;
	private String functionDescription;
//	private String functionComponent ;
	private String accessToken;
	private String unauthorisedMessage;
	private Boolean authorised;
}
