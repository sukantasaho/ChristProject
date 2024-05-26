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
import com.christ.erp.services.dto.administraton.academicCalendar.ErpCalendarCategoryDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.administraton.academicCalendar.AcademicCalendarCategoryHandler;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.christ.utility.lib.Constants;

@RestController
@RequestMapping("/Secured/Administration/AcademicCalendar/AcademicCalendarCategory")
public class AcademicCalendarCategoryController {

	@Autowired
	AcademicCalendarCategoryHandler academicCalendarCategoryHandler;
	
	@PostMapping(value="/getGridData")
	public Flux<ErpCalendarCategoryDTO> getGridData(){
		return academicCalendarCategoryHandler.getGridData().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value="/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<ErpCalendarCategoryDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return academicCalendarCategoryHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value= "/edit")
	public Mono<ErpCalendarCategoryDTO> edit(@RequestParam int id) {
		return academicCalendarCategoryHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value= "/delete")
	public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID)String userId) {
		return  academicCalendarCategoryHandler.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
}
