package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpApprovalLevelsDTO {
	private Integer id;
	private String approver;
	private Boolean isExternal;
	private Boolean isForProgramme;
	private Boolean isMandatory;
}
