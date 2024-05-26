package com.christ.erp.services.dto.account.fee;

import java.util.List;

import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccBatchFeeDurationsDetailsDTO {
	private int year;
	private int sem;
	private SelectDTO acaDurationDetailDTO;
	private List<AccBatchFeeCategoryDTO> accBatchFeeCategoryDTOList;
	private int order;
}
