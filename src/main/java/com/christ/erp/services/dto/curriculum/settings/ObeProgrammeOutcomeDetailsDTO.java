package com.christ.erp.services.dto.curriculum.settings;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObeProgrammeOutcomeDetailsDTO {
		private int id;
		private String referenceNo;
//		private String category;       removed from db 
		private String statements;
		private List<ErpProgrammePeoMissionMatrixDTO> erpProgrammePeoMissionMatrixDTOList;
}
