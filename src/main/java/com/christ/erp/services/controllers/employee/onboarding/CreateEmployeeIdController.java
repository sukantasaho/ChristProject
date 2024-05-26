package com.christ.erp.services.controllers.employee.onboarding;

import java.util.List;

import com.christ.utility.lib.Constants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveCategoryAllotmentDTO;
import com.christ.erp.services.dto.employee.onboarding.CreateEmployeeIdDTO;
import com.christ.erp.services.handlers.employee.onboarding.CreateEmployeeIdHandler;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/OnBoarding/CreateEmployeeId")
public class CreateEmployeeIdController extends BaseApiController {

	CreateEmployeeIdHandler createEmployeeIdHandler = CreateEmployeeIdHandler.getInstance();
	
	@Autowired
	CreateEmployeeIdHandler createEmployeeIdHandler1;

	@RequestMapping(value = "/getApplicantDetails", method = RequestMethod.POST)
	public Mono<ApiResult<CreateEmployeeIdDTO>> getApplicantDetails(
			@RequestParam("applicationNo") String applicationNo) {
		ApiResult<CreateEmployeeIdDTO> result = new ApiResult<CreateEmployeeIdDTO>();
		try {
			CreateEmployeeIdDTO dto = createEmployeeIdHandler1.getApplicantDetails(applicationNo);
			if (!Utils.isNullOrEmpty(dto)) {
				if(dto.getRecordStatus() == 'I') {
					result.success = true;
					result.dto = dto;
				} else {
					result.success = false;
					result.setFailureMessage("This applicant is already an employee. Please open 'Employee Profile' for any modification.");
				}
			} else {
				result.success = false;
				result.setFailureMessage("This applicant is not yet appointed");
			}
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public Mono<ApiResult> saveEmployeeId(@RequestBody CreateEmployeeIdDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult result = new ApiResult();
		try {
			result.success = createEmployeeIdHandler1.saveOrUpdate(data, userId);
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/getDepartmentByCampus", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getDepartmentByCampus(@RequestParam("campusId") String campusId) {
		ApiResult<List<LookupItemDTO>> departmentList = new ApiResult<List<LookupItemDTO>>();
		try {
			departmentList = createEmployeeIdHandler1.getDepartmentByCampus(campusId);
			if (!Utils.isNullOrEmpty(departmentList)) {
				departmentList.success = true;
			} else {
				departmentList.success = false;
			}
		} catch (Exception error) {
			departmentList.success = false;
			departmentList.dto = null;
			departmentList.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(departmentList);
	}
	
	@RequestMapping(value = "/getLeaveTypeByCategory", method = RequestMethod.POST)
	public Mono<ApiResult<EmpLeaveCategoryAllotmentDTO>> getLeaveTypeByCategory(@RequestParam("leaveCategory") String leaveCategoryId) throws Exception {
		ApiResult<EmpLeaveCategoryAllotmentDTO> result = new ApiResult<EmpLeaveCategoryAllotmentDTO>();
		EmpLeaveCategoryAllotmentDTO empLeaveCategoryAllotmentDTO = createEmployeeIdHandler1.getleavetypeByCategory(leaveCategoryId);
		if(empLeaveCategoryAllotmentDTO != null) {
			result.dto = empLeaveCategoryAllotmentDTO;
			result.success = true;
		}
		else {
			result.dto = null;
			result.success = false;
		}
		return Utils.monoFromObject(result);
	}
}