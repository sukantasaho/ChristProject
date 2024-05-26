package com.christ.erp.services.dto.common;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoleOrUserPermissionDTO extends ModelBaseDTO {

	/* role permission */
	public SysRoleDTO roleName;
	/* user permission */
	public ErpUsersDTO userName;
	public List<ModuleDTO> screens;
	private List<SysFunctionDTO> functionsList;
}
