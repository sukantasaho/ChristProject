package com.christ.erp.services.controllers.account.fee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.account.fee.DemandDTO;
import com.christ.erp.services.dto.account.fee.DemandProgramDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.account.fee.DemandSlipGenerationHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@RestController

@RequestMapping("/Secured/Accounts/Fee/DemandGeneration")
public class DemandSlipGenerationController {
	
	@Autowired DemandSlipGenerationHandler demandSlipGenerationHandler;

	@PostMapping(value = "/getAcademicYearNo")
	public Flux<SelectDTO> getAcademicYearNo(int academicYearId, int campusId){
		return demandSlipGenerationHandler.getAcademicYearNo(academicYearId,campusId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/getAllBatches")
	public Flux<SelectDTO> getAllBatches(int academicYearId, int campusId){
		return demandSlipGenerationHandler.getAllBatches(academicYearId,campusId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/getAllProgramsByYearNo")
	public Flux<DemandProgramDTO> getAllProgramsByYearNo(int academicYearId, int campusId, int yearNo){
		return demandSlipGenerationHandler.getAllProgramsByYearNo(academicYearId,campusId, yearNo).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/generateDemand")
	public Mono<ResponseEntity<ApiResult<DemandDTO>>> generateDemand(@RequestBody Mono<DemandDTO> data,  @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return demandSlipGenerationHandler.generateDemand(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
}
