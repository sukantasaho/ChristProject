package com.christ.erp.services.dto.curriculum.common;

import java.time.LocalDate;
import java.util.List;

import com.christ.erp.services.dto.common.ErpApprovalLevelsDTO;
import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter 
public class ErpProgrammeApprovalLevelMappingDTO {
	private int id;
	private ErpApprovalLevelsDTO erpApprovalLevelsDTO;
	private String approvalComments;
	private LocalDate approvalDate;	
	private List<SelectDTO> erpProgrammeApprovalLevelDocumentsList;
}
 