package com.christ.erp.services.controllers.hostel.application;

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
import com.christ.erp.services.dto.hostel.settings.HostelApplicationDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.hostel.application.OfflineHostelApplicationHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressWarnings("rawtypes")
@RestController
@RequestMapping("/Secured/Hostel/Application/OfflineHostelApplication")

public class OfflineHostelApplicationController {

	@Autowired
	private OfflineHostelApplicationHandler offlineHostelApplicationHandler;

	@PostMapping(value = "/getGridData")
	public Flux<HostelApplicationDTO> getGridData(@RequestParam String yearId) {
		return offlineHostelApplicationHandler.getGridData(yearId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<HostelApplicationDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return offlineHostelApplicationHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value= "/edit")
	public Mono<HostelApplicationDTO> edit(@RequestParam int id) {
		return offlineHostelApplicationHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value= "/delete")
	public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID)String userId) {
		return offlineHostelApplicationHandler.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value = "/getHostelDataByRegNoOrApplnNo")
	public Mono<HostelApplicationDTO> getHostelDataByRegNoOrApplnNo(@RequestParam String yearId, @RequestParam(required = false) String registerNo, @RequestParam(required = false) String applicationNo) {
		return offlineHostelApplicationHandler.getHostelDataByRegNoOrApplnNo(yearId, registerNo, applicationNo).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value ="/checkIsStudent")
	public Mono<ResponseEntity<ApiResult>> checkIsStudent(@RequestParam String registerNo) {
		return offlineHostelApplicationHandler.checkIsStudent(registerNo).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value= "/getStatusByHostel")
	public Mono<HostelApplicationDTO> getStatusByHostel(@RequestParam(required = false) String registerNo, @RequestParam String yearId, @RequestParam String hostelId, @RequestParam(required = false) String applicationNo) {
		return offlineHostelApplicationHandler.getStatusByHostel(registerNo, yearId, hostelId, applicationNo).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/checkPrivilegedUser")
	public Mono<ResponseEntity<ApiResult>> checkPrivilegedUser() {
		return offlineHostelApplicationHandler.checkPrivilegedUser().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value = "/getHostelOfflinePrefix") 
	public Flux<SelectDTO> getHostelOfflinePrefix(@RequestParam String hostelId, @RequestParam String yearId) {
		return offlineHostelApplicationHandler.getHostelOfflinePrefix(hostelId, yearId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value= "/duplicateCheck")
	public Mono<ApiResult> duplicateCheck(@RequestParam String yearId, @RequestParam(required = false) String registerNo,@RequestParam(required = false) String applicationNo ) {
		return offlineHostelApplicationHandler.duplicateCheck(yearId,registerNo,applicationNo).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
}
