package com.christ.erp.services.dto.curriculum.common;

import java.util.List;

import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpProgrammeApprovalLevelDocumentsDTO {
	private int id;
	private String approvalDocumentURL;
	private List<SelectDTO> approvalDoc;
}
