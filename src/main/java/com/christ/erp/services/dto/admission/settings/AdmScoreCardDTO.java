package com.christ.erp.services.dto.admission.settings;

import java.util.Set;

import com.christ.erp.services.dto.common.ModelBaseDTO;

public class AdmScoreCardDTO extends ModelBaseDTO {
	public String scoreCardTemplateName;
	public Set<AdmScoreCardQualitativeParameterDTO> qualitativeparameters;
	public Set<AdmScoreCardQuantitativeParameterDTO> quantitativeparameters;
}
