package com.christ.erp.services.controllers.curriculum.settings;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.ErpAcademicYearDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.curriculum.settings.ErpProgrammeBatchwiseSettingsDTO;
import com.christ.erp.services.dto.employee.common.ErpProgrammeDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.curriculum.settings.ProgrammeMasterHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Curriculum/Settings/ProgrammeMaster")
public class ProgrammeMasterController {

	@Autowired
	ProgrammeMasterHandler programmeMasterHandler;

	@PostMapping(value = "/getAdmissionYear")
	public Flux<ErpAcademicYearDTO> getAdmissionYear() {
		return programmeMasterHandler.getAdmissionYear().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/getAddOnCourses")
	public Flux<SelectDTO> getAddOnCourses() {
		return programmeMasterHandler.getAddOnCourses().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/getProgrammeApprovers")
	public Flux<SelectDTO> getProgrammeApprovers(@RequestParam String programmeOrExternal) {
		return programmeMasterHandler.getProgrammeApprovers(programmeOrExternal).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/getGridData")
	public Flux<ErpProgrammeDTO> getGridData(@RequestParam Integer academicYearId) {
		return programmeMasterHandler.getGridData(academicYearId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/editProgramme")
	public Mono<ErpProgrammeDTO> editProgramme(@RequestParam int id, Integer academicYearId) {
		return programmeMasterHandler.editProgramme(id, academicYearId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/saveOrUpdateProgramme")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdateProgramme(@RequestBody Mono<ErpProgrammeDTO> data,@RequestParam Integer academicYearId,@RequestParam(required=false) Boolean isOverWrite, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return programmeMasterHandler.saveOrUpdateProgramme(data, userId, academicYearId, isOverWrite).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}	

	@PostMapping(value = "/editBatch")
	public Mono<ErpProgrammeBatchwiseSettingsDTO> editBatch(@RequestParam int id, Integer academicYearId) {
		return programmeMasterHandler.editBatch(id, academicYearId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/saveOrUpdateBatch")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdateBatch(@RequestBody Mono<ErpProgrammeBatchwiseSettingsDTO> data, @RequestParam Integer programmeId, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return programmeMasterHandler.saveOrUpdateBatch(data, userId, programmeId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/deleteProgramme")
	public Mono<ResponseEntity<ApiResult>> deleteProgramme(@RequestParam int id, Integer academicYearId,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return programmeMasterHandler.deleteProgramme(id, academicYearId, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	} 
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/deleteBatch")
	public Mono<ResponseEntity<ApiResult>> deleteBatch(@RequestParam int id, Integer academicYearId,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return programmeMasterHandler.deleteBatch(id, academicYearId, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}	
		
	@SuppressWarnings("rawtypes")
	@PostMapping("/uploadProgrammeFiles")
    public Mono<ApiResult> uploadProgrammeFiles(@RequestPart("files") Flux<FilePart> data) {
		File directory = new File("D:\\ProgrammeApprovals");
		if(!directory.exists()) {
			directory.mkdir();
		}
		return Utils.uploadFiles(data,directory.getAbsolutePath(),new String[]{"jpg","png","jpeg","pdf","doc"});
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping("/uploadBatchFiles")
    public Mono<ApiResult> uploadBatchFiles(@RequestPart("files") Flux<FilePart> data) {
		File directory = new File("D:\\OutcomeApprovals");
		if(!directory.exists()) {
			directory.mkdir();
		}
		return Utils.uploadFiles(data,directory.getAbsolutePath(),new String[]{"jpg","png","jpeg","pdf","doc"});
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/isAcademicYearCreated")
	public Mono<ResponseEntity<ApiResult>> isAcademicYearCreated(@RequestParam Integer sessionTypeId, Integer noOfSession,Integer academicYear, Boolean isTerm) {
		return programmeMasterHandler.isAcademicYearCreated(sessionTypeId, noOfSession, academicYear, isTerm).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/isBatchCreated")
	public Mono<ResponseEntity<ApiResult>> isBatchCreated(@RequestParam Boolean isDiscontinued,Integer campusProgrammeId,Integer year) {
		return programmeMasterHandler.isBatchCreated(isDiscontinued,campusProgrammeId,year).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
//	@PostMapping(value = "/getBatchwiseList")
//	public Flux<ErpProgrammeBatchwiseSettingsDTO> getBatchwiseList(@RequestParam Integer academicYearId) {
//		return programmeMasterHandler.getBatchwiseList(academicYearId).switchIfEmpty(Mono.error(new NotFoundException(null)));
//	}
}
