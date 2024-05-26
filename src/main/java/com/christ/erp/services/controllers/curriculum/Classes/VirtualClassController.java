package com.christ.erp.services.controllers.curriculum.Classes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.curriculum.settings.AcaClassDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.curriculum.Classes.VirtualClassHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Curriculum/Classes/VirtualClass")
public class VirtualClassController {

	@Autowired
	VirtualClassHandler virtualClassHandler;

	@PostMapping(value = "/getProgrammeClassList")
	public Flux<AcaClassDTO> getProgrammeClassList(@RequestParam String academicYearId,@RequestParam String campusId,@RequestParam String levelId,@RequestParam String sessionGroupID){
		return virtualClassHandler.getProgrammeClassList(academicYearId,campusId,levelId,sessionGroupID).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<AcaClassDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return virtualClassHandler.saveOrUpdate(data,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	} 

	@SuppressWarnings("rawtypes")
	@DeleteMapping(value = "/delete")
	public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return virtualClassHandler.delete(id,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value = "/edit")
	public Mono<AcaClassDTO> edit(@RequestParam int id) {
		return virtualClassHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/getGridData")
	public Flux<AcaClassDTO> getGridData(@RequestParam String academicYearId,@RequestParam String campusId){
		return virtualClassHandler.getGridData(academicYearId,campusId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));	
	}
}