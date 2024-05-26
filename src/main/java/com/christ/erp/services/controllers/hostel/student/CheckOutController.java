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
import com.christ.erp.services.handlers.hostel.student.CheckOutHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Hostel/Student/CheckOut")

public class CheckOutController {

	@Autowired
	CheckOutHandler checkOutHandler;

	@PostMapping(value = "/getGridData")
	public Flux<HostelAdmissionsDTO> getGridData(@RequestParam String academicYearId, @RequestParam String hostelId){
		return checkOutHandler.getGridData(academicYearId, hostelId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}

	@PostMapping(value = "/getDataByStudent")
	public Mono<HostelAdmissionsDTO> getDataByStudent(@RequestParam (required = false) String hostelApplicationNo, @RequestParam (required = false) String regNo){
		return checkOutHandler.getDataByStudent(hostelApplicationNo, regNo).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}

	@PostMapping(value= "/edit")
	public Mono<HostelAdmissionsDTO> edit(@RequestParam int id) {
		return checkOutHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@SuppressWarnings("rawtypes")
	@PostMapping("/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<HostelAdmissionsDTO> dto, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return checkOutHandler.saveOrUpdate(dto, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value ="/duplicateCheck")
	public Mono<ResponseEntity<ApiResult>> duplicateCheck(@RequestParam(required = false) String hostelApplnNo, @RequestParam(required = false) String regNo) {
		return checkOutHandler.duplicateCheck(regNo, hostelApplnNo).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
}
