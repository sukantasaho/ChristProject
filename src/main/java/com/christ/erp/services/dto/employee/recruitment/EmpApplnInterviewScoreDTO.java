package com.christ.erp.services.dto.employee.recruitment;

import java.util.List;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.onboarding.EmpApplnEntriesDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmpApplnInterviewScoreDTO {

	private int empApplnInterviewScoreId;
	private EmpApplnEntriesDTO empApplnEntries;
	private EmpApplnInterviewSchedulesDTO empApplnInterviewSchedules;
	private SelectDTO erpUsers;
	private SelectDTO empInterviewUniversityExternals;
	private Integer maxScore;
	private Integer totalScore;
	private String comments;
    private List<EmpApplnInterviewScoreDetailsDTO> scoreDetailsList;
    
    
    
    
    
}
