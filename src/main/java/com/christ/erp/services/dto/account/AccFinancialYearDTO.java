package com.christ.erp.services.dto.account;

import com.christ.erp.services.dto.common.ExModelBaseDTO;

public class AccFinancialYearDTO {
	public String id;
	public ExModelBaseDTO financialYear;
	public String startDate;
	public String endDate;
	public Boolean currentYearForFee;
	public Boolean currentYearForCashCollection;
	public String currentYearForFeeDate;
	public String currentYearForCashCollectionDate;

}
