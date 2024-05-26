package com.christ.erp.services.dto.hostel.settings;

import com.christ.erp.services.dto.account.AccFeeHeadsDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class HostelFineCategoryDTO {

	private int id;
	private String fineCategory;
	private Integer fineAmount;
	private String fineGroup;
	private SelectDTO hostelDTO;
	private Boolean isAbsentFine;
	private Boolean isDisciplinaryFine;
	private Boolean isOthersFine;
	private AccFeeHeadsDTO accFeeHeadsDTO;
}
