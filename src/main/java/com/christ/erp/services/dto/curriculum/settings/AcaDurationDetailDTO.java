package com.christ.erp.services.dto.curriculum.settings;

import java.time.LocalDate;

import com.christ.erp.services.dto.common.ErpCampusProgrammeMappingDTO;
import com.christ.erp.services.dto.curriculum.common.AcaSessionDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcaDurationDetailDTO {

	private int id;
	private AcaDurationDTO acaDurationDTO;
	private AcaSessionDTO acaSessionDTO;
	private AcaBatchDTO acaBatchDTO;
	private LocalDate sessionStartDate;
	private LocalDate sessionEndDate;
	private LocalDate sessionFirstInstructionDate;
	private LocalDate sessionLastInstructionDate;
	private LocalDate sessionFinalExamStartDate;
	private LocalDate sessionFinalExamEndDate;
	private LocalDate vacationEndDate;
	private ErpCampusProgrammeMappingDTO erpCampusProgrammeMappingDTO;
}
