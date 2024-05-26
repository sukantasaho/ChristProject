package com.christ.erp.services.controllers.employee.salary;

import java.util.List;

import com.christ.erp.services.common.EntityManagerInstance;
import com.christ.utility.lib.Constants;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.salary.SalaryComponentDTO;
import com.christ.erp.services.handlers.employee.salary.SalaryComponentHandler;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;

// A new screen is developed in reactive way.
// @RestController 
// @RequestMapping("/Secured/Employee/salary/SalaryComponent")
public class SalaryComponentController extends BaseApiController {

	SalaryComponentHandler salaryComponentHandler = SalaryComponentHandler.getInstance();

	@RequestMapping(value = "/getGridData", method = RequestMethod.POST)
	public Mono<ApiResult<List<SalaryComponentDTO>>> getGridData() {
		ApiResult<List<SalaryComponentDTO>> result = new ApiResult<>();
		try {
			List<SalaryComponentDTO> salaryComponent = salaryComponentHandler.getGridData();
			if (!Utils.isNullOrEmpty(salaryComponent)) {
				result.success = true;
				result.dto = salaryComponent;
			} else {
				result.success = false;
			}
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdateSalaryComponent(@RequestBody SalaryComponentDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();

		try {
			ApiResult<ModelBaseDTO> to = salaryComponentHandler.saveOrUpdateSalaryComponent(data, userId);
			if (to.failureMessage == null || to.failureMessage.isEmpty()) {
				result.success = true;
			} else {
				result.success = false;
				result.failureMessage = to.failureMessage;
			}

		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public Mono<ApiResult<SalaryComponentDTO>> selectSalaryComponent(@RequestParam("id") String id) {
		ApiResult<SalaryComponentDTO> result = new ApiResult<SalaryComponentDTO>();
		SalaryComponentDTO salaryComponentDTO = salaryComponentHandler.selectSalaryComponent(id);
		if (salaryComponentDTO.id != null) {
			result.dto = salaryComponentDTO;
			result.success = true;
		} else {
			result.dto = null;
			result.success = false;
		}

		return Utils.monoFromObject(result);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Mono<ApiResult> deleteSalaryComponent(@RequestBody SalaryComponentDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult result = new ApiResult();
		try {
			result.success = salaryComponentHandler.deleteSalartComponent(data.id, userId);
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

}
