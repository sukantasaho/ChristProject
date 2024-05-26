package com.christ.erp.services.dto.admission.settings;

import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmScoreCardQualitativeParameterDTO extends ModelBaseDTO{
	public ExModelBaseDTO admscorecardid;
    public String orderNo;
	public ExModelBaseDTO qualitativeparameter;
	private int adm_scorecard_qualitative_parameter_id;
	private AdmQualitativeParamterDTO admQualitativeParameterDto;
}
