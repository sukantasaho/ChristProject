package com.christ.erp.services.controllers.hostel.fineanddisciplinary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.hostel.fineanddisciplinary.HostelFineEntryDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.hostel.fineanddisciplinary.FineEntryHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Hostel/FineAndDisciplinary/FineEntry")
public class FineEntryController {

	@Autowired
	FineEntryHandler fineEntryHandler;

	@PostMapping(value = "/getGridData")
	public Flux<HostelFineEntryDTO> getAdmissionYear(@RequestParam Integer academicYearId,Integer hostelId,@RequestParam(required=false)Integer blockId,@RequestParam(required=false)Integer unitId, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return fineEntryHandler.getGridData(academicYearId, hostelId, blockId, unitId, userId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/edit")
	public Mono<HostelFineEntryDTO> edit(@RequestParam int id) {
		return fineEntryHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<HostelFineEntryDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID)String userId) {
		return fineEntryHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/delete")
	public Mono<ResponseEntity<ApiResult>> deleteBatch(@RequestParam int id,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return fineEntryHandler.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
}
