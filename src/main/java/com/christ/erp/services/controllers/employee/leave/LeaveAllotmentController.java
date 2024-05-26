package com.christ.erp.services.controllers.employee.leave;

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
import com.christ.erp.services.dto.employee.leave.EmpLeaveAllotmentDTO;
import com.christ.erp.services.handlers.employee.leave.LeaveAllotmentHandler;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/Leave/LeaveAllotment")
public class LeaveAllotmentController {
	
	LeaveAllotmentHandler leaveAllotmentHandler=LeaveAllotmentHandler.getInstance();
	
	@RequestMapping(value="/getGridData", method=RequestMethod.POST)
	public Mono<ApiResult<List<EmpLeaveAllotmentDTO>>> getGridData()  {
		ApiResult<List<EmpLeaveAllotmentDTO>> result = new ApiResult<List<EmpLeaveAllotmentDTO>>();
		try {
			result.dto = leaveAllotmentHandler.getGridData();
			if(!Utils.isNullOrEmpty(result.dto)) {
				result.success = true;
			} else {
				result.success = false;
				result.failureMessage = "Data not found";
			}
		}
		catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public Mono<ApiResult<EmpLeaveAllotmentDTO>> edit(@RequestParam("id") String id) {
		ApiResult<EmpLeaveAllotmentDTO> result = new ApiResult<EmpLeaveAllotmentDTO>();
		try {
			result.dto = leaveAllotmentHandler.edit(id);
			if(!Utils.isNullOrEmpty(result.dto))
				result.success = true;
		}
		catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value="/saveOrUpdate", method=RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@RequestBody EmpLeaveAllotmentDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			result.success = leaveAllotmentHandler.saveOrUpdate(data, result, userId);
		}
		catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> delete(@RequestParam("id") String id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			result.success = leaveAllotmentHandler.delete(id,userId);
		}
		catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}
}
