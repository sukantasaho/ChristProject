package com.christ.erp.services.controllers.employee.leave;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.employee.leave.EmpLeaveEntryDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.employee.leave.LeaveDetailsDisplayHandler;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/leave/LeaveDetailsDisplay")
public class LeaveDetailsDisplayController {

	
	@Autowired 
	LeaveDetailsDisplayHandler leaveDetailsDisplayHandler;
	CommonApiTransaction commonApiTransaction = CommonApiTransaction.getInstance();
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/adminPrivilege")
	public Mono<ResponseEntity<ApiResult>> adminPrivilege() {
		return leaveDetailsDisplayHandler.adminPrivilege().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/approverPrivilege")
	public Mono<ResponseEntity<ApiResult>> approverPrivilege() {
		return leaveDetailsDisplayHandler.approverPrivilege().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/hodPrivilege")
	public Mono<ResponseEntity<ApiResult>> hodPrivilege() {
		return leaveDetailsDisplayHandler.hodPrivilege().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/deanPrivilege")
	public Mono<ResponseEntity<ApiResult>> deanPrivilege() {
		return leaveDetailsDisplayHandler.deanPrivilege().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	
	@PostMapping(value = "/getEmpCampus")
	public Flux<EmpLeaveEntryDTO> getEmpCampus(@RequestBody EmpLeaveEntryDTO data,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) throws Exception {						
		return leaveDetailsDisplayHandler.getEmpCampus(data,userId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/getEmpDepartment")
	public Flux<EmpLeaveEntryDTO> getEmpDepartment(@RequestBody EmpLeaveEntryDTO data,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) throws Exception {						
		return leaveDetailsDisplayHandler.getEmpDepartment(data,userId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	
	@PostMapping(value = "/getEmpLeaveGridDetails")
	public Flux<EmpLeaveEntryDTO> getGridData(@RequestBody EmpLeaveEntryDTO data,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) throws Exception {						
		return leaveDetailsDisplayHandler.getGridData(data,userId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
}
