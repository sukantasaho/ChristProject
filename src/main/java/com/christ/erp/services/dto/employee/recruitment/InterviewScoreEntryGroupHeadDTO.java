package com.christ.erp.services.dto.employee.recruitment;

import java.util.List;
import java.util.Map;
import com.christ.erp.services.dto.common.ModelBaseDTO;

public class InterviewScoreEntryGroupHeadDTO extends ModelBaseDTO {

	public String templateGroupHeading;
	public Integer headingOrderNo; 
	public Map<Integer, InteviewScoreEntryGroupDetailsDTO> groupDetailsMap;
	public Map<String,List<InteviewScoreEntryGroupDetailsDTO>> inteviewScoreEntryGroupDetailsDTOs;
}
