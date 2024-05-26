package com.christ.erp.services.dto.curriculum.settings;

import java.time.LocalDate;
import java.util.List;

import com.christ.erp.services.dto.common.ErpCampusProgrammeMappingDTO;
import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcaBatchDTO {
	private int id;
	private String batchName;
	private Integer inTakeBatchNumber;
	private Boolean isMultipleExit;
	private LocalDate batchCommencementDate;
	private Integer batchCommencementMonth;   
	private Integer programmeCompletionMonth;
	private Integer approvedIntakeForBatch;
	private Integer revisedIntakeForBatch;
	private ErpCampusProgrammeMappingDTO erpCampusProgrammeMappingDTO;
	private List<SelectDTO> acaBatchList;
}
