package com.christ.erp.services.handlers.account.settings;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.account.settings.AccFinancialYearDBO;
import com.christ.erp.services.dto.account.AccFinancialYearDTO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.transactions.account.settings.FinancialYearsTransaction;

public class FinancialYearsHandler {
	private static volatile FinancialYearsHandler financialYearsHandler = null;
	FinancialYearsTransaction financialYearsTransaction = FinancialYearsTransaction.getInstance();

	public static FinancialYearsHandler getInstance() {
		if (financialYearsHandler == null) {
			financialYearsHandler = new FinancialYearsHandler();
		}
		return financialYearsHandler;
	}

	public ApiResult<List<LookupItemDTO>> getFinancialYear() {
		ApiResult<List<LookupItemDTO>> financialYear = new ApiResult<List<LookupItemDTO>>();
		try {
			LocalDate d =  LocalDate.now();
			int year = d.getYear();
			Integer minYear = 2000;
			Integer currentYear = year + 1900;
			Integer maxYear = currentYear + 3;
			financialYear.dto = new ArrayList<>();
			for (int i = minYear; i <= maxYear; i++) {
				String val = i + "-" + (i + 1);
				LookupItemDTO itemInfo = new LookupItemDTO();
				itemInfo.value = String.valueOf(val);
				itemInfo.label = String.valueOf(val);
				financialYear.dto.add(itemInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return financialYear;
	}

	public ApiResult<ModelBaseDTO> saveOrUpdate(AccFinancialYearDTO data, String userId) throws Exception {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		if (!Utils.isNullOrEmpty(data)) {
			AccFinancialYearDBO dbo = new AccFinancialYearDBO();
			boolean flag = true;
			if (Utils.isNullOrEmpty(data.id)) {
				// Duplicate check
				if (financialYearsTransaction.duplicateCheck(data, userId)) {
					result.failureMessage = "Record exist with Financial Year : " + data.financialYear.text;
					flag = false;
				} else if(financialYearsTransaction.duplicateCheckCurrentYear(data, userId)){
					result.failureMessage = " Current Year for Fee Already set for Financial Year : " + data.currentYearForFeeDate;
					flag = false;
				} else if(financialYearsTransaction.duplicateCheckCurrentYearforCashCollection(data, userId)){
					result.failureMessage = " Current Year for Cash Collection Already set for Financial Year : " + data.currentYearForCashCollectionDate;
					flag = false;
				}else {
					dbo.createdUsersId = Integer.parseInt(userId);
				}
			} else {
				if(financialYearsTransaction.duplicateCheckCurrentYear(data, userId)){
					result.failureMessage = " Current Year for Fee Already set for Financial Year : " + data.currentYearForFeeDate;
					flag = false;
				}else if(financialYearsTransaction.duplicateCheckCurrentYearforCashCollection(data, userId)){
					result.failureMessage = " Current Year for Cash Collection Already set for Financial Year : " + data.currentYearForCashCollectionDate;
					flag = false;
				} else {
					dbo.modifiedUsersId = Integer.parseInt(userId);
					dbo.id = Integer.parseInt(data.id);
				}
			}
			if (flag) {
				dbo.financialYear = data.financialYear.text;
				dbo.financialYearStartDate = Utils.convertStringDateTimeToLocalDate(data.startDate);
				dbo.financialYearEndDate = Utils.convertStringDateTimeToLocalDate(data.endDate);
				dbo.isCurrentForCashCollection = data.currentYearForCashCollection;
				dbo.isCurrentForFee = data.currentYearForFee;
				dbo.recordStatus = 'A';
				result.success = financialYearsTransaction.saveOrUpdate(dbo);
			}
		}		
		return result;
	}

	public List<AccFinancialYearDTO> getGridData() throws Exception {
		List<AccFinancialYearDTO> list = new ArrayList<>();
		List<AccFinancialYearDBO> listFinancialYear = financialYearsTransaction.getGridData();
		for (AccFinancialYearDBO obj : listFinancialYear) {
			AccFinancialYearDTO accFinancialYearDTO = new AccFinancialYearDTO();
			accFinancialYearDTO.id = String.valueOf(obj.id);
			accFinancialYearDTO.financialYear = new ExModelBaseDTO();
			accFinancialYearDTO.financialYear.id = String.valueOf(obj.id);
			accFinancialYearDTO.financialYear.text = String.valueOf(obj.financialYear);
			accFinancialYearDTO.startDate = obj.financialYearStartDate.toString();
			accFinancialYearDTO.endDate = obj.financialYearEndDate.toString();
			accFinancialYearDTO.currentYearForCashCollection = obj.isCurrentForCashCollection.booleanValue();
			accFinancialYearDTO.currentYearForFee = obj.isCurrentForFee.booleanValue();
			list.add(accFinancialYearDTO);
		}
		return list;
	}

	public AccFinancialYearDTO edit(String id) throws Exception {
		AccFinancialYearDTO accFinancialYearDTO = new AccFinancialYearDTO();
		AccFinancialYearDBO obj = financialYearsTransaction.edit(id);
		accFinancialYearDTO.id = String.valueOf(obj.id);
		accFinancialYearDTO.financialYear = new ExModelBaseDTO();
		accFinancialYearDTO.financialYear.id = String.valueOf(obj.id);
		accFinancialYearDTO.financialYear.text = String.valueOf(obj.financialYear);
		accFinancialYearDTO.currentYearForCashCollection = obj.isCurrentForCashCollection;
		accFinancialYearDTO.currentYearForFee = obj.isCurrentForFee;
		accFinancialYearDTO.startDate = obj.financialYearStartDate.toString();
		accFinancialYearDTO.endDate = obj.financialYearEndDate.toString();
		return accFinancialYearDTO;
	}

	public boolean delete(String id, String userId) throws Exception {
		AccFinancialYearDBO obj = financialYearsTransaction.edit(id);
		obj.recordStatus = 'D';
		obj.modifiedUsersId = Integer.parseInt(userId);
		return financialYearsTransaction.delete(obj);
	}

}
