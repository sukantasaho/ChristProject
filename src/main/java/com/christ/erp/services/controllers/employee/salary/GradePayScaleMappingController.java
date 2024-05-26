package com.christ.erp.services.controllers.employee.salary;

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
import com.christ.erp.services.dto.employee.salary.EmpPayScaleGradeMappingDTO;
import com.christ.erp.services.handlers.employee.salary.GradePayScaleMappingHandler;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Employee/Salary/GradePayScaleMapping")
public class GradePayScaleMappingController extends BaseApiController {

	GradePayScaleMappingHandler gradePayScaleMappingHandler = GradePayScaleMappingHandler.getInstance();
	@RequestMapping(value = "/getGridData", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpPayScaleGradeMappingDTO>>> getGridData() {
		ApiResult<List<EmpPayScaleGradeMappingDTO>> result = new ApiResult<>();
		try {
			List<EmpPayScaleGradeMappingDTO> empPayScaleGradeMapping = gradePayScaleMappingHandler.getGridData();
			if (!Utils.isNullOrEmpty(empPayScaleGradeMapping)) {
				result.success = true;
				result.dto = empPayScaleGradeMapping;
			} else {
				result.success = false;
				result.failureMessage = "Data Not Found";
			}
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdateGradePayScaleMapping(
			@RequestBody EmpPayScaleGradeMappingDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			ApiResult<ModelBaseDTO> to = gradePayScaleMappingHandler.saveOrUpdateGradePayScaleMapping(data,
					userId);
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
	public Mono<ApiResult<EmpPayScaleGradeMappingDTO>> selectPayScaleGradeMapping(@RequestParam("id") String id) {
		ApiResult<EmpPayScaleGradeMappingDTO> result = new ApiResult<EmpPayScaleGradeMappingDTO>();
		EmpPayScaleGradeMappingDTO empPayScaleGradeMappingDTO = gradePayScaleMappingHandler
				.selectPayScaleGradeMapping(id);
		if (empPayScaleGradeMappingDTO.id != null) {
			result.dto = empPayScaleGradeMappingDTO;
			result.success = true;
		} else {
			result.dto = null;
			result.success = false;
		}
		return Utils.monoFromObject(result);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Mono<ApiResult> deletePayScaleGradeMapping(@RequestBody EmpPayScaleGradeMappingDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult result = new ApiResult();
		try {
			result.success = gradePayScaleMappingHandler.deletePayScaleGradeMapping(data.id, userId);
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}
}
