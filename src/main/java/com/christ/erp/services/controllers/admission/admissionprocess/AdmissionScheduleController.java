package com.christ.erp.services.controllers.admission.admissionprocess;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.admission.admissionprocess.AdmAdmissionScheduleDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.admission.admissionprocess.AdmissionScheduleHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Admission/AdmissionProcess/AdmissionSchedule")
public class AdmissionScheduleController {

	@Autowired
	AdmissionScheduleHandler admissionScheduleHandler;

	@PostMapping(value = "/getGridData")
	public Flux<AdmAdmissionScheduleDTO> getGridData(){
		return admissionScheduleHandler.getGridData().switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}
	
	@PostMapping(value = "/edit")
	public Mono<AdmAdmissionScheduleDTO> edit(@RequestParam String id) {
		return admissionScheduleHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value="/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<AdmAdmissionScheduleDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return admissionScheduleHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value="/delete")
	public Mono<ResponseEntity<ApiResult>> delete(@RequestParam String id,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return admissionScheduleHandler.delete(id,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value="/onSearch")
	public Mono<ResponseEntity<ApiResult>> onSearch(@RequestBody Mono<AdmAdmissionScheduleDTO> data) {
		return admissionScheduleHandler.onSearch(data).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

}
