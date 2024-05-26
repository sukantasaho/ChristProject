package com.christ.erp.services.controllers.admission.applicationprocess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.student.common.StudentApplnEntriesDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.admission.applicationprocess.MarksVerificationHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Admission/ApplicationProcess/MarksVerification")

public class MarksVerificationController {

	@Autowired
	private MarksVerificationHandler marksVerificationHandler;

	@PostMapping(value = "/getStudentDetails")
	public Mono<StudentApplnEntriesDTO> getStudentDetails(@RequestParam int applicationNumber) {
		return marksVerificationHandler.getStudentDetails(applicationNumber).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value= "/getEducationalDetails")
	public Mono<StudentApplnEntriesDTO> getEducationalDetails(@RequestParam int applicationNumber) {
		return marksVerificationHandler.getEducationalDetails(applicationNumber).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value= "/getRemarkDetails")
	public Flux<SelectDTO> getRemarkDetails() {
		return marksVerificationHandler.getRemarkDetails().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/marksVerificationUpdate")
	public Mono<ResponseEntity<ApiResult>> marksVerificationUpdate(@RequestBody Mono<StudentApplnEntriesDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) int userId){
		return marksVerificationHandler.marksVerificationUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
}


