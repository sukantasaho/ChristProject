package com.christ.erp.services.dto.curriculum.settings;

import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExternalsAdditionalDetailsDTO {
	private int id;
	private String address;
	private String experienceDetails;
	private String organizationDetails;
	private String qualificationDescription;
	private SelectDTO erpQualificationLevel;
}
