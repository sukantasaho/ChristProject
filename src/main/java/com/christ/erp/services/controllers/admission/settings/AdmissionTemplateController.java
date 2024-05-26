package com.christ.erp.services.controllers.admission.settings;

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
import com.christ.erp.services.dto.admission.settings.ErpAdmissionTemplateDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.admission.settings.AdmissionTemplateHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Admission/Settings/AdmissionTemplate")
public class AdmissionTemplateController {

	@Autowired
	AdmissionTemplateHandler admissionTemplateHandler;
	
	@PostMapping(value = "/getGridList")
	public Flux<ErpAdmissionTemplateDTO> getGridList(){
		return admissionTemplateHandler.getGridList().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@RequestMapping(value = "/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveAdmissionTemplate(@RequestBody Mono<ErpAdmissionTemplateDTO> templateDTO, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return admissionTemplateHandler.saveOrUpdate(templateDTO, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	@PostMapping(value = "/edit")
    public Mono<ErpAdmissionTemplateDTO> edit(@RequestParam int id) {
        return admissionTemplateHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	@DeleteMapping(value = "/delete")
    public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return admissionTemplateHandler.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }
	
	
    
}
