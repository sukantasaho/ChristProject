package com.christ.erp.services.controllers.admission.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.admission.settings.AdmQualificationDegreeListDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.admission.settings.QualificationDegreeListHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Admission/Settings/QualificationDegreeList")
public class QualificationDegreeListController {

	@Autowired
	QualificationDegreeListHandler qualificationDegreeListHandler;
	
	//@GetMapping(value = "/getGridData",produces =  MediaType.TEXT_EVENT_STREAM_VALUE)
	@PostMapping(value = "/getGridData")
	public Flux<AdmQualificationDegreeListDTO> getGridData(){
		return qualificationDegreeListHandler.getGridData().switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}
	
	@PostMapping(value = "/edit")
	public Mono<AdmQualificationDegreeListDTO> edit(@RequestParam int id){
		return qualificationDegreeListHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/delete")
	public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return qualificationDegreeListHandler.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<AdmQualificationDegreeListDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return qualificationDegreeListHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}	
}
