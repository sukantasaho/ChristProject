package com.christ.erp.services.controllers.common;

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
import com.christ.erp.services.dto.common.ErpTemplateGroupDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.common.TemplateGroupHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Common/TemplateGroup")
public class TemplateGroupController
{
	@Autowired
	TemplateGroupHandler templateGroupHandler;
	
	//@GetMapping(value = "/getGridData", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@PostMapping(value = "/getGridData")
	public Flux<ErpTemplateGroupDTO> getGridData() {
		 return templateGroupHandler.getGridData().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
    @SuppressWarnings("rawtypes")
	@PostMapping(value = "/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<ErpTemplateGroupDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID)String userId) {
		return templateGroupHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
 	@PostMapping(value = "/edit")
	public Mono<ErpTemplateGroupDTO> edit(@RequestParam int id) { 
		return templateGroupHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/delete")
	public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID)String userId) {
		return templateGroupHandler.delete(id , userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
}
