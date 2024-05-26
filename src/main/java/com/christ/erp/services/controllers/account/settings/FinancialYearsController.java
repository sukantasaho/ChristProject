package com.christ.erp.services.controllers.account.settings;

import java.util.List;

import com.christ.utility.lib.Constants;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.account.AccFinancialYearDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.handlers.account.settings.FinancialYearsHandler;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Accounts/FinancialYears")
public class FinancialYearsController extends BaseApiController {
	FinancialYearsHandler financialYearsHandler = FinancialYearsHandler.getInstance();

	@RequestMapping(value = "/getFinancialYear", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getGeneratedYear() {
		ApiResult<List<LookupItemDTO>> generatedYear = new ApiResult<List<LookupItemDTO>>();
		try {
			generatedYear = financialYearsHandler.getFinancialYear();
			if (!Utils.isNullOrEmpty(generatedYear)) {
				generatedYear.success = true;
			} else {
				generatedYear.success = false;
			}
		} catch (Exception error) {
			generatedYear.success = false;
			generatedYear.dto = null;
			generatedYear.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(generatedYear);
	}

	@RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@RequestBody AccFinancialYearDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			ApiResult<ModelBaseDTO> result1 = new ApiResult<ModelBaseDTO>();
			result1=financialYearsHandler.saveOrUpdate(data, userId);
			result.success=result1.success;
			result.failureMessage=result1.failureMessage;
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value="/getGridData", method=RequestMethod.POST)
	public Mono<ApiResult<List<AccFinancialYearDTO>>> getGridData()  {
		ApiResult<List<AccFinancialYearDTO>> result = new ApiResult<>();
		try {
			List<AccFinancialYearDTO> financialYearData = financialYearsHandler.getGridData();
			if(!Utils.isNullOrEmpty(financialYearData)) {
				result.success = true;
				result.dto = financialYearData;
			}else {
				result.success = false;
			}
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}


	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public Mono<ApiResult<AccFinancialYearDTO>> edit(@RequestParam("id") String id) {
		ApiResult<AccFinancialYearDTO> result = new ApiResult<AccFinancialYearDTO>();
		AccFinancialYearDTO menuScreendto;
		try {
			menuScreendto = financialYearsHandler.edit(id);
			if(menuScreendto.id!=null) {
				result.dto = menuScreendto;
				result.success = true;
			}else {
				result.dto = null;
				result.success = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Utils.monoFromObject(result);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Mono<ApiResult> delete(@RequestBody AccFinancialYearDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult result = new ApiResult();
		try {
			result.success = financialYearsHandler.delete(data.id, userId);
		}catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}
}