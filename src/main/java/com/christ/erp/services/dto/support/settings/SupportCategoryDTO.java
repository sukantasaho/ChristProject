package com.christ.erp.services.dto.support.settings;

import java.util.List;

import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupportCategoryDTO {
	private int id;
	private String supportCategoryName;
	private SelectDTO supportCategoryGroupDTO;
	private SelectDTO supportAreaTo;
	private SelectDTO erpDepartmentDTO;
    private List<SupportCategoryCampusDTO> supportCategoryCampusDTOList;
    private List<LookupItemDTO> supportCategoryUserGroupDTOList;
	private Boolean isUploadRequired;
	private Boolean notificationEmailRequired;
	private Boolean notificationSmsRequired;
	private String supportCampuses;
	private String userGroupNames;
	
}
