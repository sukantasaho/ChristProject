package com.christ.erp.services.dto.admission.settings;

import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmQualificationDegreeListDTO {
	
	private int id;
	private String degreeName;
	private boolean isNoMarksDetails;
	private SelectDTO qualification;
}