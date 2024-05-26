package com.christ.erp.services.controllers.employee.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.employee.settings.EmpApplnNumberGenerationDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.employee.settings.ApplicationNumbersHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Employee/Settings/ApplicationNumbers")
public class ApplicationNumbersController {
 
    @Autowired
    ApplicationNumbersHandler applicationNumbersHandler;
 
	@PostMapping(value = "/getGridData")
    public Flux<EmpApplnNumberGenerationDTO> getGridData() {
		return applicationNumbersHandler.getGridData().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value= "/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<EmpApplnNumberGenerationDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID)String userId){
		return applicationNumbersHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	} 
	
	@PostMapping(value= "/edit")
    public Mono<EmpApplnNumberGenerationDTO> edit(@RequestParam int id){
		return applicationNumbersHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value= "/delete")
	public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID)String userId){
		return  applicationNumbersHandler.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
}