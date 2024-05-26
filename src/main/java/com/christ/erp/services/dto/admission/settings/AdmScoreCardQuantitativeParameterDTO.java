package com.christ.erp.services.dto.admission.settings;

import java.math.BigDecimal;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmScoreCardQuantitativeParameterDTO extends ModelBaseDTO { 
	public Integer admScoreCardQuantitativeParameterId;
	public Integer orderNo;
	public Integer maxValue;
	public BigDecimal valueInterval;
	public String parameterName;
	public BigDecimal scoreEntered;
	public ExModelBaseDTO admscorecardid; 
}
