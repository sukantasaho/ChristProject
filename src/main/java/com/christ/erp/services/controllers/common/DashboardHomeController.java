package com.christ.erp.services.controllers.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.dto.common.NestedSelectDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.common.DashboardHomeHandler;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Protected/Common/Dashboard")
public class DashboardHomeController extends BaseApiController{
	DashboardHomeHandler dashboardHomeHandler = DashboardHomeHandler.getInstance();
	
	@Autowired 
	private DashboardHomeHandler dashboardHomeHandler1;
	
//	@RequestMapping(value = "/getDepartmentCampusAttendanceData", method = RequestMethod.POST)
//	public Mono<ApiResult<List<ViewEmployeeAttendanceDTO>>> getDepartmentCampusAttendanceDataDashboard(
//			@RequestParam Map<String, String> requestParams) {
//		ApiResult<List<ViewEmployeeAttendanceDTO>> result = new ApiResult<List<ViewEmployeeAttendanceDTO>>();
//		try {
//			result=dashboardHomeHandler.getAttendanceTypeForEmployees(requestParams, result);
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
	
	@PostMapping("/getDashBoardEmployeeDiversity")
	public Flux<SelectDTO> getDashBoardEmployeeDiversity(){
		return dashboardHomeHandler1.getDashBoardEmployeeDiversity().switchIfEmpty(Mono.error(new NotFoundException(null)));	
	}
	
	@PostMapping("/getDashBoardEmployeeExperience")
	public Flux<SelectDTO> getDashBoardEmployeeExperience(){
		return dashboardHomeHandler1.getDashBoardEmployeeExperience().switchIfEmpty(Mono.error(new NotFoundException(null)));	
	}
	
	@PostMapping("/getDashBoardEmployeeQualification")
	public Flux<NestedSelectDTO> getDashBoardEmployeeQualification(){
		return dashboardHomeHandler1.getDashBoardEmployeeQualification().switchIfEmpty(Mono.error(new NotFoundException(null)));	
	}
	
	@PostMapping("/getDashBoardEmployeeApplicationStatus")
	public Flux<SelectDTO> getDashBoardEmployeeApplicationStatus(){
		return dashboardHomeHandler1.getDashBoardEmployeeApplicationStatus().switchIfEmpty(Mono.error(new NotFoundException(null)));	
	}	
	
	@PostMapping("/getDashBoardEmployeeCount")
	public Flux<NestedSelectDTO> getDashBoardEmployeeCount(){
		return dashboardHomeHandler1.getDashBoardEmployeeCount().switchIfEmpty(Mono.error(new NotFoundException(null)));	
	}
}
