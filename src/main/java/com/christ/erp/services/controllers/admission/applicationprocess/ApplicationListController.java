package com.christ.erp.services.controllers.admission.applicationprocess;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.admission.applicationprocess.ApplicationListDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.admission.applicationprocess.ApplicationListHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value ="/Secured/Admission/ApplicationProcess/ApplicationList")
public class ApplicationListController {

	@Autowired
	private ApplicationListHandler applicationListHandler;
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/privilegeEnabled")
	public Mono<ResponseEntity<ApiResult>> privilegeEnabled() {
		return applicationListHandler.privilegeEnabled().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value = "/getGridData")
	public Mono<List<ApplicationListDTO>> getGridData( @RequestBody Mono<ApplicationListDTO> data,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return applicationListHandler.getGridData(data,userId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/getStudentApplicationData")
	public Mono<ResponseEntity<InputStreamResource>>getStudentApplicationData( @RequestBody Mono<ApplicationListDTO> data,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId)  {
		return applicationListHandler.getStudentApplicationData(data,userId).defaultIfEmpty(ResponseEntity.badRequest().build());
	}   

}