package com.christ.erp.services.controllers.employee.letter;

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
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.letter.EmpLetterRequestDTO;
import com.christ.erp.services.handlers.employee.letter.LetterTypesHandlers;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/Letter/LetterTypes")
public class LetterTypesController extends BaseApiController {

	LetterTypesHandlers letterTypesHandlers = LetterTypesHandlers.getInstance();

	@RequestMapping(value = "/getGridData", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpLetterRequestDTO>>> getGridData() {
		ApiResult<List<EmpLetterRequestDTO>> result = new ApiResult<>();
		try {
			List<EmpLetterRequestDTO> letterTypeData = letterTypesHandlers.getGridData();
			if (!Utils.isNullOrEmpty(letterTypeData)) {
				result.success = true;
				result.dto = letterTypeData;
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
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdateLetterTypes(@RequestBody EmpLetterRequestDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			ApiResult<ModelBaseDTO> to = letterTypesHandlers.saveOrUpdateLetterTypes(data, userId);
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
	public Mono<ApiResult<EmpLetterRequestDTO>> selectLetterTypes(@RequestParam("id") String id) {
		ApiResult<EmpLetterRequestDTO> result = new ApiResult<EmpLetterRequestDTO>();
		EmpLetterRequestDTO empLetterRequestDTO = letterTypesHandlers.selectLetterTypes(id);
		if (empLetterRequestDTO.id != null) {
			result.dto = empLetterRequestDTO;
			result.success = true;
		} else {
			result.dto = null;
			result.success = false;
		}
		return Utils.monoFromObject(result);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Mono<ApiResult> deleteLetterTypes(@RequestBody EmpLetterRequestDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult result = new ApiResult();
		try {
			result.success = letterTypesHandlers.deleteLetterTypes(data.id, userId);
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}
}
