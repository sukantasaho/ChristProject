package com.christ.erp.services.controllers.employee.salary;

import java.util.List;
import java.util.Map;

import com.christ.utility.lib.Constants;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.employee.salary.EmpDailyWageSlabDTO;
import com.christ.erp.services.handlers.employee.salary.DailyWagePaymentAndBasicsHandler;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/Salary/DailyWagePaymentAndBasics")
public class DailyWagePaymentAndBasicsController extends BaseApiController {
	DailyWagePaymentAndBasicsHandler dailyWagePaymentAndBasicsHandler = DailyWagePaymentAndBasicsHandler.getInstance();

	@RequestMapping(value = "/getGridData", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpDailyWageSlabDTO>>> getGridData() {
		ApiResult<List<EmpDailyWageSlabDTO>> result = new ApiResult<>();
		try {
			List<EmpDailyWageSlabDTO> gridData = dailyWagePaymentAndBasicsHandler.getGridData();
			if(!Utils.isNullOrEmpty(gridData)) {
				result.success = true;
				result.dto = gridData;
			}else {
				result.success = false;
			}
		} catch(Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
	public Mono<ApiResult> saveMappingJPA(@RequestBody EmpDailyWageSlabDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult result = new ApiResult();
		Boolean duplicate = false;
		try {
			duplicate = dailyWagePaymentAndBasicsHandler.dupcheck(data);
			if(!duplicate) {
				result.success = dailyWagePaymentAndBasicsHandler.saveOrUpdate(data, userId);
				result.success = true;
			}else {
				result.failureMessage = "Already details exists for " + data.empCategory.text +" and " + data.jobCategory.text + "  ";
				result.success = false;
			}
			return Utils.monoFromObject(result);
		} catch(Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public Mono<ApiResult<EmpDailyWageSlabDTO>> getDlyWgDetails(@RequestBody Map<String, String> data) {
		ApiResult<EmpDailyWageSlabDTO> result = new ApiResult<>();
		try {
			EmpDailyWageSlabDTO DlyWgData = dailyWagePaymentAndBasicsHandler.getDlyWgData(data);
			if(!Utils.isNullOrEmpty(DlyWgData)) {
				result.success = true;
				result.dto = DlyWgData;
			} else {
				result.success = false;
			}
		} catch(Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Mono<ApiResult> delete(@RequestBody Map<String, String> data) {
		@SuppressWarnings("rawtypes")
		ApiResult result = new ApiResult();
		try {
			result.success = dailyWagePaymentAndBasicsHandler.delete(data);
		} catch (Exception e) {
			e.printStackTrace();
			result.success = false;
			result.failureMessage = e.getMessage();
		}
		return Utils.monoFromObject(result);
	}
}
