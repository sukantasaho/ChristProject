package com.christ.erp.services.dto.common;

import java.util.List;

import com.christ.erp.services.dto.employee.settings.ErpTemplateDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpTemplateGroupDTO {
	private int id;
	private String templateGroupName;
	private String templateGroupCode;
    public String templatePurpose;
    public List<ErpTemplateDTO> erpTemplateDTOSet;
}
