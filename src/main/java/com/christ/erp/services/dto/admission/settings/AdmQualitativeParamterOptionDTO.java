package com.christ.erp.services.dto.admission.settings;

import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmQualitativeParamterOptionDTO {
	public String id;
	public String option;
	public char recordStatus;
	private SelectDTO parameterOptions;
}
