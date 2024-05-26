package com.christ.erp.services.dto.account.fee;

import java.math.BigDecimal;

import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccBatchFeeAccountDTO {
	private int id;
	private SelectDTO accAccountsDTO;
	private SelectDTO erpCurrencyDTO;
	private SelectDTO accFeeAdjustmentCategoryDTO;
	private BigDecimal feeAccountAmount;
	private BigDecimal feeScholarshipAmount;
}
