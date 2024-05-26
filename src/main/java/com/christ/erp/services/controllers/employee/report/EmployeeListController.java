package com.christ.erp.services.controllers.employee.report;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.employee.report.EmployeeListDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.employee.report.EmployeeListHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/Report/EmployeeList")
public class EmployeeListController {
	
	@Autowired
	EmployeeListHandler employeeListHandler;
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/payScaleEnabled")
	public Mono<ResponseEntity<ApiResult>> payScaleEnabled() {
		return employeeListHandler.payScaleEnabled().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@PostMapping(value = "/getGridData")
	public Mono<List<EmployeeListDTO>> getGridData( @RequestBody Mono<EmployeeListDTO> data,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return employeeListHandler.getGridData(data,userId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/getEmployeeDetails")
	public Mono<ResponseEntity<InputStreamResource>>getEmployeeDetails( @RequestBody Mono<EmployeeListDTO> data,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId)  {
		return employeeListHandler.getEmployeeDetails(data,userId).defaultIfEmpty(ResponseEntity.badRequest().build());
	} 

}
