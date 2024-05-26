package com.christ.erp.services.controllers.admission.admissionprocess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.student.common.StudentAdmissionCancellationOfflineDTO;
import com.christ.erp.services.handlers.admission.admissionprocess.AdmissionCancellationOfflineHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Admission/AdmissionProcess/AdmissionCancellationOffline")
@SuppressWarnings("rawtypes")
public class AdmissionCancellationOfflineController {

	@Autowired
	AdmissionCancellationOfflineHandler admissionCancellationOfflineHandler;
	
	@PostMapping(value = "/admissionCancellation")
	public Mono<ResponseEntity<ApiResult>> admissionCancellation(@RequestBody Mono<StudentAdmissionCancellationOfflineDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return admissionCancellationOfflineHandler.admissionCancellation(data,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
}