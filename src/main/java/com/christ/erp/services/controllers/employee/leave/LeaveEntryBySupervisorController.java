package com.christ.erp.services.controllers.employee.leave;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveEntryDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.employee.leave.LeaveEntryBySupervisorHandler;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/leave/LeaveEntryBySupervisor")
public class LeaveEntryBySupervisorController {
	
	@Autowired 
	LeaveEntryBySupervisorHandler leaveEntryBySupervisorHandler;
	CommonApiTransaction commonApiTransaction = CommonApiTransaction.getInstance();
	
	
	@PostMapping(value = "/getGridData")
	public Flux<EmpLeaveEntryDTO> getGridData(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) throws Exception {
		Integer empId = commonApiTransaction.getEmployeesByUserId(userId);
		return leaveEntryBySupervisorHandler.getGridData(empId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@RequestMapping(value = "/getEmployeeIdsOrNames", method = RequestMethod.POST)
	public Mono<List<SelectDTO>> getEmployeeIdOrName(@RequestParam("employeeIdOrName") String employeeIdOrName,Boolean isNumber,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) throws Exception {    
		Integer empId = commonApiTransaction.getEmployeesByUserId(userId);
		return leaveEntryBySupervisorHandler.getEmployeeIdOrName(employeeIdOrName,isNumber,empId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/getEmployeeLeaveHistory")
	public Flux<EmpLeaveEntryDTO> getEmployeeLeaveHistory(@RequestParam("value") String value,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) throws Exception {
		return leaveEntryBySupervisorHandler.getEmployeeLeaveHistory(Integer.parseInt(value)).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
}
