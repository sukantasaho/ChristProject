package com.christ.erp.services.dto.admission.settings;

import java.util.List;

import lombok.Data;

@Data
public class AdmQualitativeParamterDTO {

	public String id;
	public String qualitativeParameterLabel;
	public String fieldType;
	public String enteredValue;
	public List<AdmQualitativeParamterOptionDTO> options;
	
}
