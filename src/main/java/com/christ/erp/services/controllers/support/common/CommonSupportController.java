package com.christ.erp.services.controllers.support.common;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.support.common.CommonSupportHandler;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/CommonSupport")
public class CommonSupportController extends BaseApiController {
	
	@Autowired
	CommonSupportHandler commonSupportHandler;

	@PostMapping(value = "/getSupportArea")
	public Flux<SelectDTO> getSupportArea(){
		return commonSupportHandler.getSupportArea().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/getCategoryGroupByArea")
	public Flux<SelectDTO> getSupportCategoryGroupByArea(@RequestParam int supportAreaId){
		return commonSupportHandler.getSupportCategoryGroupByArea(supportAreaId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/getErpUserGroup")
	public Flux<SelectDTO> getErpUserGroup(){
		return commonSupportHandler.getErpUserGroup().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	@PostMapping(value = "getSupportRole")
    public Flux<SelectDTO> getSupportRole() {
		return commonSupportHandler.getSupportRole().switchIfEmpty(Mono.error(new NotFoundException(null)));
   }

}
