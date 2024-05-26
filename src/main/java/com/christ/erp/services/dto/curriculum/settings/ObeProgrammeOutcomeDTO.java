package com.christ.erp.services.dto.curriculum.settings;

import java.util.List;

import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObeProgrammeOutcomeDTO {
	private int id;
	private String comments;
	private SelectDTO obeProgrammeOutcomeTypesDTO;
	private SelectDTO erpApprovalLevelsDTO;
	private List<ObeProgrammeOutcomeDetailsDTO> obeProgrammeOutcomeDetailsDTOList;
	private List<SelectDTO> obeProgrammeOutcomeUploadDetailsList;
}
