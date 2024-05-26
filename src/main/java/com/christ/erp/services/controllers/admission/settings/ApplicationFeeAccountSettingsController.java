/*This screen is removed.This screen is used for update erp_campus_programme_mapping table acc_account_id column but 
erp_campus_programme_mapping table acc_account_id is removed. */
package com.christ.erp.services.controllers.admission.settings;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.common.ErpCampusProgrammeMappingDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.admission.settings.ApplicationFeeAccountSettingsHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//@RestController
@RequestMapping("/Secured/Admission/Settings/ApplicationFeeAccountSettings")
public class ApplicationFeeAccountSettingsController {
	
	@Autowired
	private ApplicationFeeAccountSettingsHandler applicationFeeAccountSettingsHandler;
	
	@PostMapping("/getGridData")
	public Flux<ErpCampusProgrammeMappingDTO> getGridData(){
		return applicationFeeAccountSettingsHandler.getGridData().switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping("/update")
	public Mono<ResponseEntity<ApiResult>> update(@RequestBody Mono<List<ErpCampusProgrammeMappingDTO>> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return applicationFeeAccountSettingsHandler.update(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

}