package com.christ.erp.services.dto.common;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErpCommitteeProgrammeDTO {
	
	private int id;
    private SelectDTO erpProgramme ;
    private SelectDTO programmeCoordinator;
	private LocalDate programmeStructureEntryLastDate;
	private List<SelectDTO> erpProgrammeList; 

}
