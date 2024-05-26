package com.christ.erp.services.dto.admission.applicationprocess;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelectionProcessGroupEditDTO {
	
	private String id ;
	private String groupName;
	private String date;
	private String centerId;
	private String timeAllotmentId;
	private boolean flag ;
	private String newGroupId;
	private List<SelectionProcessGroupEditDetailsDTO> levels;

}
