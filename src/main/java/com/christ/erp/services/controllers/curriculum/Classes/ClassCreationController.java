package com.christ.erp.services.controllers.curriculum.Classes;

import java.util.List;
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
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.curriculum.settings.AcaClassDTO;
import com.christ.erp.services.dto.curriculum.settings.AcaDurationDetailDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.curriculum.Classes.ClassCreationHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Curriculum/Classes/ClassCreation")
public class ClassCreationController {

	@Autowired
	ClassCreationHandler classCreationHandler;

	@PostMapping(value = "/getBatchName")
	public Flux<SelectDTO> getBatchName(@RequestParam String batchYearId, @RequestParam String campusId, @RequestParam String programId) {
		return classCreationHandler.getBatchName(batchYearId, campusId, programId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/getGridData")
	public Flux<AcaClassDTO> getGridData(@RequestParam String academicYearId, @RequestParam String campusId, @RequestParam String sessionType){
		return classCreationHandler.getGridData(academicYearId, campusId, sessionType).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));	
	}

	@PostMapping(value = "/edit")
	public Mono<AcaClassDTO> edit(@RequestParam int id) {
		return classCreationHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/getDurationDetailForSubmission")
	public Flux<AcaDurationDetailDTO> getDurationDetailForSubmission(@RequestParam String yearId, @RequestParam String campusId, @RequestParam String levelId,
			@RequestParam String typeId) {
		return classCreationHandler.getDurationDetailForSubmission(yearId, campusId, levelId, typeId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	} 

	@PostMapping(value = "/getDurationDetailForTerm")
	public Flux<AcaDurationDetailDTO> getDurationDetailForTerm(@RequestParam String batchId) {
		return classCreationHandler.getDurationDetailForTerm(batchId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	} 

	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<List<AcaClassDTO>> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return classCreationHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	} 

	@SuppressWarnings("rawtypes")
	@DeleteMapping(value = "/delete")
	public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return classCreationHandler.delete(id,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/isClassCreated")
	public Mono<ResponseEntity<ApiResult>> isClassCreated(@RequestParam String yearId, @RequestParam String campusCode, @RequestParam String section,
			@RequestParam String programId) {
		return classCreationHandler.isClassCreated(yearId, campusCode, section, programId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
}
