package com.christ.erp.services.controllers.employee.attendance;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.employee.attendance.EmpWorkDiaryEntriesDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.common.CommonApiHandler;
import com.christ.erp.services.handlers.employee.attendance.WorkDiaryApprovalHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Employee/attendance/WorkDiaryApproval")

public class WorkDiaryApprovalController {

	@Autowired
	CommonApiHandler commonApiHandler;

	@Autowired
	WorkDiaryApprovalHandler workDiaryApprovalHandler;

	@PostMapping(value="/getEmployeeDetailsForApprover")
	public Flux<EmpWorkDiaryEntriesDTO> getEmployeeDetailsForApprover(@RequestParam Map<String,String> requestParams, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return workDiaryApprovalHandler.getEmployeeDetailsForApprover(requestParams, userId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/workDiaryApproverUpdate")
	public Mono<ResponseEntity<ApiResult>> workDiaryApproverUpdate(@RequestBody Mono<List<EmpWorkDiaryEntriesDTO>> data, @RequestParam Map<String,String> requestParams,@RequestParam List<String> empId, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return workDiaryApprovalHandler.workDiaryApproverUpdate(data,requestParams,empId, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}		
}




