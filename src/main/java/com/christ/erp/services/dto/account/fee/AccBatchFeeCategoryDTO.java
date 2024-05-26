package com.christ.erp.services.dto.account.fee;

import java.util.List;

import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccBatchFeeCategoryDTO {
	private int id;
	private SelectDTO erpAdmissionCategoryDTO;
	private List<AccBatchFeeHeadDTO> AccBatchFeeHeadDTOList;
}
