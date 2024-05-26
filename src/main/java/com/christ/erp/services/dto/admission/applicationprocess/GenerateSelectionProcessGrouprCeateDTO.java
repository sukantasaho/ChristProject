package com.christ.erp.services.dto.admission.applicationprocess;

import com.christ.erp.services.dto.common.ExModelBaseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateSelectionProcessGrouprCeateDTO {

	  private Integer id;
	  private ExModelBaseDTO  academicYear;
	  private ExModelBaseDTO  level;
	  private ExModelBaseDTO  degree;
	  private ExModelBaseDTO  program;
	  private ExModelBaseDTO  selectionProcessDate;
	  private ExModelBaseDTO  venue;
	  private ExModelBaseDTO  startTime;
	  private ExModelBaseDTO  endTime;
	  private String  groupSize;  
}
