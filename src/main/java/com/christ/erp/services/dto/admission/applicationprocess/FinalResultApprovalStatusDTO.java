package com.christ.erp.services.dto.admission.applicationprocess;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinalResultApprovalStatusDTO {

	private String selected;
	private String notSelected;
	private String waitlisted;
}
