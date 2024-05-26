package com.christ.erp.services.dto.admission.applicationprocess;

import java.util.List;

import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.student.common.StudentApplnEntriesDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmSelectionProcessScoreDTO {
	private Integer AdmSelectionProcessScoreId;
	private SelectionProcessGroupEditDetailsDTO selectionProcessGroupEditDetailsDTO;
	private List<AdmSelectionProcessDTO> admSelectionProcessDto;
	private StudentApplnEntriesDTO studentApplnEntriesDto;
	private int round;
	private List<SelectDTO> groupSelectionProcessList;
	private AdmSelectionProcessTypeDetailsDTO admSelectionProcessTypeDetailsDto;
	private String groupId;
	private Boolean isAbsent = false;
	private String date;
	private List<AdmSelectionProcessScoreEntryDTO> admSelectionProcessScoreEntryDTOList;
}
