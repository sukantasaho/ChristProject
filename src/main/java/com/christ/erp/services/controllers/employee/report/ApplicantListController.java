package com.christ.erp.services.controllers.employee.report;

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
import com.christ.erp.services.dto.employee.report.ApplicantListDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.employee.report.ApplicantListHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/Report/ApplicantList")
public class ApplicantListController {
	
	@Autowired
	private ApplicantListHandler applicantListHandler;
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/locationEnable")
	public Mono<ResponseEntity<ApiResult>> locationEnable() {
		return applicantListHandler.locationEnable().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@PostMapping(value = "/getGridData")
	public Mono<List<ApplicantListDTO>> getGridData( @RequestBody Mono<ApplicantListDTO> data,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return applicantListHandler.getGridData(data,userId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/getEmpApplicantsDetails")
	public Mono<ResponseEntity<InputStreamResource>>getEmpApplicantsDetails( @RequestBody Mono<ApplicantListDTO> data,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId)  {
		return applicantListHandler.getEmpApplicantsDetails(data,userId).defaultIfEmpty(ResponseEntity.badRequest().build());
	} 
}
