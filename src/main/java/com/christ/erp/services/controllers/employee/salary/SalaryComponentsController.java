package com.christ.erp.services.controllers.employee.salary;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.employee.salary.EmpPayScaleComponentsDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.employee.salary.SalaryComponentsHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Employee/Salary/SalaryComponents")
public class SalaryComponentsController {

	@Autowired
	SalaryComponentsHandler salaryComponentsHandler;

	@PostMapping(value = "/getGridData")
	public Flux<EmpPayScaleComponentsDTO> getGridData() {
		return salaryComponentsHandler.getGridData().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<EmpPayScaleComponentsDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return salaryComponentsHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value = "/edit")
	public Mono<EmpPayScaleComponentsDTO> edit(@RequestParam int id) {
		return salaryComponentsHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/delete")
	public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return salaryComponentsHandler.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/isDuplicateDisplayOrder")
	public Mono<ResponseEntity<ApiResult>> isDuplicateDisplayOrder(@RequestParam String payScaleType,@RequestParam  String displayOrder,@RequestParam  (required = false) String id) {
		return salaryComponentsHandler.isDuplicateDisplayOrder(payScaleType,id,displayOrder).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
}