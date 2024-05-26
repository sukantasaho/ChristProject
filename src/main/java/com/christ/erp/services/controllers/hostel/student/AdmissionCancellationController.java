package com.christ.erp.services.controllers.hostel.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.hostel.settings.HostelAdmissionsDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.hostel.student.AdmissionCancellationHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Hostel/Student/AdmissionCancellation")
public class AdmissionCancellationController {

	@Autowired
	AdmissionCancellationHandler admissionCancellationHandler;

	@PostMapping(value = "/getStudentHostelData")
	public Mono<HostelAdmissionsDTO> getStudentHostelData(@RequestParam (required = false) String hostelApplnNo, @RequestParam (required = false) String regNo, @RequestParam (required = false) String name, @RequestParam String yearId) {
		return admissionCancellationHandler.getStudentHostelData(hostelApplnNo, regNo, name, yearId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<HostelAdmissionsDTO> dto, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return admissionCancellationHandler.saveOrUpdate(dto, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
}
