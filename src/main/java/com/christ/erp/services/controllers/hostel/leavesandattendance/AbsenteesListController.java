package com.christ.erp.services.controllers.hostel.leavesandattendance;

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
import com.christ.erp.services.dto.hostel.leavesandattendance.AbsenteesListDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.hostel.leavesandattendance.AbsenteesListHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value ="/Secured/hostel/leavesandattendance/AbsenteesList")
@SuppressWarnings("rawtypes")
public class AbsenteesListController {

	@Autowired
	private AbsenteesListHandler absenteesListHandler;

	@PostMapping(value = "/getGridData")
	public Mono<List<AbsenteesListDTO>> getGridData(@RequestBody Mono<AbsenteesListDTO> dto) {
		return absenteesListHandler.getGridData(dto).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/save")
	public Mono<ResponseEntity<ApiResult>> save(@RequestBody Mono<List<AbsenteesListDTO>> dto, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return absenteesListHandler.save(dto, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value = "/getAbsenteeListofStudent")
	public Flux<AbsenteesListDTO> getAbsenteeListofStudent(@RequestParam int yearId, @RequestParam int admissionId ,@RequestParam int month) {
		return absenteesListHandler.getAbsenteeListofStudent(yearId,admissionId,month).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

}