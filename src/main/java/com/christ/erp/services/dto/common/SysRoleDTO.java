package com.christ.erp.services.dto.common;

import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class SysRoleDTO extends ModelBaseDTO implements Comparable<SysRoleDTO>{
	
	public String roleName;
	public LookupItemDTO roleGroup;
	public String userName;
	public Set<String> roles;
	private char recordStatus;
	@Override
	public int compareTo(SysRoleDTO o) {
		return this.userName.toLowerCase().trim().compareTo(o.userName.toLowerCase().trim());
	}
	
	public SysRoleDTO(int id,String loginName,char recordStatus) {
		this.setId(String.valueOf(id));
		this.setUserName(loginName);
		this.setRecordStatus(recordStatus);
	}

}
