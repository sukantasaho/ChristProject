package com.christ.erp.services.controllers.admission.applicationprocess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.admission.applicationprocess.GenerateSelectionProcessGrouprCeateDTO;
import com.christ.erp.services.handlers.admission.applicationprocess.GenerateSelectionProcessGroupHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Admission/ApplicationProcess/GenerateSelectionProcessGroup")
@SuppressWarnings("rawtypes")
public class GenerateSelectionProcessGroupController {
	
	@Autowired
	GenerateSelectionProcessGroupHandler GenerateSelectionProcessGrouphandler;
	
	@PostMapping(value = "/GroupCreate")
    public Mono<ResponseEntity<ApiResult>> GroupCreate(@RequestBody Mono<GenerateSelectionProcessGrouprCeateDTO> data , @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return GenerateSelectionProcessGrouphandler.GroupCreate(data, userId).map(ResponseEntity::ok)
        		.defaultIfEmpty(ResponseEntity.badRequest().build());
    }
	

	@PostMapping(value = "/GroupReCreate")
    public Mono<ResponseEntity<ApiResult>> GroupReCreate(@RequestBody Mono<GenerateSelectionProcessGrouprCeateDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return GenerateSelectionProcessGrouphandler.GroupReCreate(data, userId).map(ResponseEntity::ok)
        		.defaultIfEmpty(ResponseEntity.badRequest().build());
    }
}
