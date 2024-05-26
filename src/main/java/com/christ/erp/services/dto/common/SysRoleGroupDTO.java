package com.christ.erp.services.dto.common;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SysRoleGroupDTO {
	
	private SelectDTO roleGroupName;
	private List<ModuleSubDTO> roles;

}
