package com.christ.erp.services.dto.account;

import java.math.BigDecimal;
import java.util.List;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccFeeHeadsDTO {
	private int id;
	private SelectDTO feeHeadsTypeDTO;
//	private String code;
	private String heading;
	private SelectDTO accFeeGroupDTO;
	private boolean isGstApplicable;
	private boolean isCgstApplicable;
	private boolean isSgstApplicable;
	private boolean isIgstApplicable;
//	private SelectDTO erpProgrammeDegreeDTO;
	private SelectDTO hostelDTO;
	private SelectDTO hostelRoomTypeDTO;
	private boolean isFixedAmount;
	private List<AccFeeHeadsAccountDTO> accFeeHeadsAccountDTOList;
	private BigDecimal amount;
}
