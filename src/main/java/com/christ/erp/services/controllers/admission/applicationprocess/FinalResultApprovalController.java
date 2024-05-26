package com.christ.erp.services.controllers.admission.applicationprocess;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.admission.applicationprocess.FinalResultApprovalDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.admission.applicationprocess.FinalResultApprovalHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Admission/AdmissionProcess")
public class FinalResultApprovalController {

	@Autowired
	FinalResultApprovalHandler finalResultApprovalHandler;

	@PostMapping(value = "/getGridData")
	public Flux<FinalResultApprovalDTO> getGridData(@RequestParam String applicantCurrentProcessStatus,@RequestParam String applNo, @RequestParam String campusId, @RequestParam String locationId, @RequestParam String programmeLevelId, @RequestParam String programmeId) {
		return finalResultApprovalHandler.getGridData(applicantCurrentProcessStatus, applNo, campusId, locationId,programmeLevelId,programmeId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/finalResultApprovalStatusUpdate")
	public Mono<ResponseEntity<ApiResult<FinalResultApprovalDTO>>> finalResultApprovalStatusUpdate(@RequestParam String applicantCurrentProcessStatus, @RequestBody Flux<FinalResultApprovalDTO> data1,
			@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		 Mono<List<FinalResultApprovalDTO>> data = data1.collectList();
		return finalResultApprovalHandler.finalResultApprovalStatusUpdate(applicantCurrentProcessStatus, data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/viewAdmissionCard")
	public Mono<ResponseEntity<ApiResult>> viewAdmissionCard(@RequestBody Mono<FinalResultApprovalDTO> data,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return finalResultApprovalHandler.viewAdmissionCard(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/viewFeeDetails")
	public Mono<ResponseEntity<ApiResult>> viewFeeDetails(@RequestBody Mono<FinalResultApprovalDTO> data,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return finalResultApprovalHandler.viewFeeDetails(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value = "/getFinalResultApprovalStatusList")
	public Flux<SelectDTO> getFinalResultApprovalStatusList() {
		return finalResultApprovalHandler.getFinalResultApprovalStatusList().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/getFinalResultApprovalStatusListCount")
	public Mono<ResponseEntity<ApiResult<FinalResultApprovalDTO>>> getFinalResultApprovalStatusListCount() {
		return finalResultApprovalHandler.getFinalResultApprovalStatusListCount().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
}
