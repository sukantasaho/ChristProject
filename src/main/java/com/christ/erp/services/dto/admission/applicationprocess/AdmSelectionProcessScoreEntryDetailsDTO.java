package com.christ.erp.services.dto.admission.applicationprocess;

import java.math.BigDecimal;
import com.christ.erp.services.dto.admission.settings.AdmQualitativeParamterOptionDTO;
import com.christ.erp.services.dto.admission.settings.AdmScoreCardQualitativeParameterDTO;
import com.christ.erp.services.dto.admission.settings.AdmScoreCardQuantitativeParameterDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmSelectionProcessScoreEntryDetailsDTO {
	private Integer admSelectionProcessScoreEntryDetailsId;
	private AdmSelectionProcessScoreEntryDTO admSeletionProcessScoreEntryDto;
	private String scoreEnteredTime;
	private AdmScoreCardQualitativeParameterDTO admScoreCardQualitativeParameterDto;
	private AdmScoreCardQuantitativeParameterDTO admScoreCardQuantitativeParameterDto;
	private AdmQualitativeParamterOptionDTO admQualitativeParameterOptionDto;
	private String qualitativeParameterScoreEnteredText;
	private BigDecimal quantitativeParameterMaxScore;
	private BigDecimal quantitativeParameterScoreEntered;

}
