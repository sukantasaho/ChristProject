package com.christ.erp.services.controllers.administration.academicCalandar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.administraton.academicCalendar.ErpCalendarUserTypesDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.administraton.academicCalendar.AcademicCalendarUserTypeHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Administration/AcademicCalendar/AcademicCalendarUserType")
public class AcademicCalendarUserTypeController {
	
	@Autowired
	AcademicCalendarUserTypeHandler academicCalendarUserTypeHandler;
	
	@PostMapping(value="/getGridData")
	public Flux<ErpCalendarUserTypesDTO> getGridData(){
		return academicCalendarUserTypeHandler.getGridData().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value="/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<ErpCalendarUserTypesDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return academicCalendarUserTypeHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value= "/edit")
	public Mono<ErpCalendarUserTypesDTO> edit(@RequestParam int id) {
		return academicCalendarUserTypeHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value= "/delete")
	public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID)String userId) {
		return academicCalendarUserTypeHandler.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
}
