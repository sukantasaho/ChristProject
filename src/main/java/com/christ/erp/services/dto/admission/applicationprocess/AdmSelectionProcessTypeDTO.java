package com.christ.erp.services.dto.admission.applicationprocess;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdmSelectionProcessTypeDTO {
	
	private Integer admSelectionProcessTypeId;
	public String selectionStageName;
	public String mode;
	public Boolean isShortlistAfterThisStage;
	private String date;
	private List<AdmSelectionProcessTypeDetailsDTO> admSelectionProcessTypeDetailsDto;

}
