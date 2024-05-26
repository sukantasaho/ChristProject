package com.christ.erp.services.dto.support.settings;

import java.util.List;

import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.support.common.SupportRoleDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupportCategoryCampusDetailsDTO {
	private int id;
	private SupportRoleDTO supportRoleDTO;
	private String maxTimeToResolve; 
	private String groupEmailId;
	private List<SelectDTO> supportCategoryCampusDetailsEmployeeDTOList; 
}
