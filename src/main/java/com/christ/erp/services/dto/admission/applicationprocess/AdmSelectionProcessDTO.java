package com.christ.erp.services.dto.admission.applicationprocess;

import java.util.List;
import com.christ.erp.services.dto.admission.settings.AdmSelectionProcessCenterDetailsDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.student.common.StudentApplnEntriesDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmSelectionProcessDTO {
	private Integer admSelectionProcessId;
	private LookupItemDTO erpAcademicYearDTO;	
	private StudentApplnEntriesDTO studentApplnEntriesDto;
	private String SelectionDate;
	private List<AdmSelectionProcessTypeDTO> admissionSelectionProcessTypeList;
	private AdmSelectionProcessTypeDTO admselectionProcessTypeDto;
	private AdmSelectionProcessPlanDetailDTO admSelectionProcessPlanDetailDto;
	private AdmSelectionProcessPlanDetailAllotmentDTO admSelectionProcessPlanDetailAllotmentDto;
	private AdmSelectionProcessCenterDetailsDTO admSelectionProcessCenterDetailsDto;
	private String spAdmitCardUrl;
	private Boolean isAdmitCardNill; 
}
