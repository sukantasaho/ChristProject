package com.christ.erp.services.dto.admission.applicationprocess;

import java.math.BigDecimal;
import java.util.List;
import com.christ.erp.services.dto.admission.settings.AdmScoreCardDTO;
import com.christ.erp.services.dto.common.ErpUsersDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmSelectionProcessScoreEntryDTO {
	private Integer admSelectionProcessScoreEntryId;
	private AdmSelectionProcessScoreDTO admSelectionProcessScoreDto;
	private AdmScoreCardDTO admScoreCardDto;
	private ErpUsersDTO erpUsersDto;
	private String scoreEnteredTime;
	private BigDecimal scoreEntered;
	private BigDecimal maxScore;
	private List<AdmSelectionProcessScoreEntryDetailsDTO> admSelectionProcessScoreEntryDetailsDTOList;
}
