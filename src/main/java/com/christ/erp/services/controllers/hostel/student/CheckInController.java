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
import com.christ.erp.services.handlers.hostel.student.CheckInHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Hostel/Student/CheckIn")
public class CheckInController {

	@Autowired
	CheckInHandler checkInHandler;

	@PostMapping(value = "/getGridData")
	public Flux<HostelAdmissionsDTO> getGridData(@RequestParam String academicYearId, @RequestParam String hostelId){
		return checkInHandler.getGridData(academicYearId,hostelId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}

	@PostMapping(value= "/edit")
	public Mono<HostelAdmissionsDTO> edit(@RequestParam int id) {
		return checkInHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/getDataByStudent")
	public Mono<HostelAdmissionsDTO> getDataByStudent(@RequestParam (required = false) String applnNo, @RequestParam (required = false) String regNo, @RequestParam (required = false) String name ){
		return checkInHandler.getDataByStudent(applnNo, regNo, name).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}

	@SuppressWarnings("rawtypes")
	@PostMapping("/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<HostelAdmissionsDTO> dto, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return checkInHandler.saveOrUpdate(dto, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value ="/duplicateCheck")
	public Mono<ResponseEntity<ApiResult>> duplicateCheck(@RequestParam(required = false) String applnNo, @RequestParam(required = false) String regNo, @RequestParam(required = false) String name) {
		return checkInHandler.duplicateCheck(regNo, applnNo, name).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
}
