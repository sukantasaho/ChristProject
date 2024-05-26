package com.christ.erp.services.dto.admission.applicationprocess;

import java.util.List;
import com.christ.erp.services.dto.admission.settings.AdmScoreCardQualitativeParameterDTO;
import com.christ.erp.services.dto.admission.settings.AdmScoreCardQuantitativeParameterDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmSelectionProcessTypeDetailsDTO {
	private Integer id;
	private SelectDTO subProcessName;
	private String scoreCardId;
	private List<AdmScoreCardQualitativeParameterDTO> qualitativeParameters;
	private List<AdmScoreCardQuantitativeParameterDTO> quantitativeParameters;
}
