package com.christ.erp.services.controllers.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.common.SysPropertiesDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.common.PropertiesHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Common/Properties")
public class PropertiesController {
	
	@Autowired
	PropertiesHandler propertiesHandler;
	
	@PostMapping(value = "/getGridData")
	public Flux<SysPropertiesDTO> getGridData(){
		return propertiesHandler.getGridData().switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}
	
	@SuppressWarnings("rawtypes")
	@DeleteMapping(value = "/delete")
	public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return propertiesHandler.delete(id,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<SysPropertiesDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return propertiesHandler.saveOrUpdate(data,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@PostMapping(value = "/edit")
	public Mono<SysPropertiesDTO> edit(@RequestParam int id){
		return propertiesHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));	
	}
}