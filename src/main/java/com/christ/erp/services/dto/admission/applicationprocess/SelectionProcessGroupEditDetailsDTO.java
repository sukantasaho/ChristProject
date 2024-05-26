package com.christ.erp.services.dto.admission.applicationprocess;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelectionProcessGroupEditDetailsDTO {
	
	private String id ;
	private String studentEntrieId;
	private String applicationNo;
	private String applicantName;
	private String programe;
	
	// SocreEntry Screen
	private String photoUrl;
	private String selectionProcessId;
}
