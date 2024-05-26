package com.christ.erp.services.dto.common;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class SysUserRoleMapDTO extends ModelBaseDTO implements Comparable<SysUserRoleMapDTO> {

	public LookupItemDTO role;
	public LookupItemDTO user;
    public List<LookupItemDTO> campusList;
	@Override
	public int compareTo(SysUserRoleMapDTO obj) {
		return Integer.parseInt(role.value) -  Integer.parseInt(obj.role.value);
	}
	
	public SysUserRoleMapDTO(int id,String roleName) {
		this.setId(String.valueOf(id));
		this.role = new LookupItemDTO();
		this.role.setLabel(roleName);
	}
}