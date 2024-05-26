package com.christ.erp.services.dto.common;

import java.util.List;

import com.christ.erp.services.dto.employee.common.ErpProgrammeDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpCampusProgrammeMappingDTO extends ModelBaseDTO {
	public int id;
	public String mappingId;
	public ExModelBaseDTO program;
	public ExModelBaseDTO location;
	public ExModelBaseDTO campus;
	public String combinedName;

    private ErpLocationDTO erpLocationDTO;
    private ErpCampusDTO campusDTO;
    private ErpProgrammeDTO erpProgrammeDTO;
    private int programmeCommenceYear;
    private Integer programmeInactivatedYear;
    private SelectDTO campusSelect;
    private List<SelectDTO> accountNo;
    private String programName;
    private String campusName;
    private List<SelectDTO> accountNoList;
    private SelectDTO accountNoSelect;
    private String campusOrLocation;
    private Boolean isForBatchCreation;
}
