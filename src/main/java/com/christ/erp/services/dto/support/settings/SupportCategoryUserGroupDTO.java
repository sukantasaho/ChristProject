package com.christ.erp.services.dto.support.settings;

import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupportCategoryUserGroupDTO {
	private int id;
	private SelectDTO erpUserGroupDTO;
}
