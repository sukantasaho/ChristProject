package com.christ.erp.services.controllers.employee.attendance;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.christ.erp.services.exception.NotFoundException;
import com.christ.utility.lib.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.employee.attendance.EmpAttendanceDTO;
import com.christ.erp.services.handlers.employee.attendance.ViewEmployeeAttendanceHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ViewEmployeeAttendanceController {

	@Autowired
	ViewEmployeeAttendanceHandler viewEmployeeAttendanceHandler;

//	ViewEmployeeAttendanceHandler viewEmployeeAttendanceHandler =ViewEmployeeAttendanceHandler.getInstance();
//
//	@RequestMapping(value = "/getAttendanceDataDatewise", method = RequestMethod.POST)
//	public Mono<ApiResult<List<EmpAttendanceDTO>>> getAttendanceData(@RequestParam Map<String,String> requestParams ) {
//		ApiResult<List<EmpAttendanceDTO>> result = new ApiResult<List<EmpAttendanceDTO>>();
//		try {
//			result=viewEmployeeAttendanceHandler.getAttendanceDataForEmployee(requestParams, result);
//			if(Utils.isNullOrWhitespace(result.failureMessage)) {
//				result.success = true;
//			}else {
//				result.success = false;
//			}
//		} catch (Exception error) {
//			result.success = false;
//			result.dto = null;
//			result.failureMessage = error.getMessage();
//		}
//		return Utils.monoFromObject(result);
//	}
//
//	@RequestMapping(value = "/getAttendanceDataForCumulative", method = RequestMethod.POST)
//	public Mono<ApiResult<List<EmpAttendanceDTO>>> getAttendanceDataForCumulative(@RequestParam Map<String,String> requestParams ) {
//		ApiResult<List<EmpAttendanceDTO>> result = new ApiResult<List<EmpAttendanceDTO>>();
//		try {
//			result=viewEmployeeAttendanceHandler.getAttendanceDataForEmployeeCumulative(requestParams, result);
//			if(Utils.isNullOrWhitespace(result.failureMessage)) {
//				result.success = true;
//			}else {
//				result.success = false;
//			}
//		} catch (Exception error) {
//			result.success = false;
//			result.dto = null;
//			result.failureMessage = error.getMessage();
//		}
//		return Utils.monoFromObject(result);
//	}

	@PostMapping(value = "/Secured/Employee/Attendance/ViewEmployeeAttendance/getEmployeeAttendance")
	public Flux<EmpAttendanceDTO> getEmployeeAttendance(@RequestParam String startDate,@RequestParam String endDate, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return viewEmployeeAttendanceHandler.getEmployeeAttendance(startDate,endDate,userId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}
}
