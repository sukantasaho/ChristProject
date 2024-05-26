package com.christ.erp.services.dto.account.fee;

import java.util.List;

import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class DemandDTO {
	  private SelectDTO programmeDTO;
	  private SelectDTO academicYearDTO;
	  private SelectDTO campusDTO;
	  private SelectDTO yearDTO;
	  private String registerNoFrom;
	  private String registerNoTo;
	  private List<DemandProgramDTO> programmeList;
	  private String definitionNotFoundDet;
	  private Boolean notFoundAccepted;
}
