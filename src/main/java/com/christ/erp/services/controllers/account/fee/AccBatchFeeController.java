package com.christ.erp.services.controllers.account.fee;

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
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.account.fee.AccBatchFeeDTO;
import com.christ.erp.services.dto.account.fee.AccBatchFeeDurationsDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.account.fee.AccBatchFeeHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Accounts/Fee/AccBatchFee")
public class AccBatchFeeController extends BaseApiController {
	
	@Autowired
	AccBatchFeeHandler accBatchFeeHandler;
	/*
	@PostMapping(value = "/getBatchNameByProgramAndYear")
	public Flux<SelectDTO> getBatchNameByProgramAndYear(int programId, int batchYearId){
		return accBatchFeeHandler.getBatchNameByProgramAndYear(programId, batchYearId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/getDetails")
	public Mono<List<AccBatchFeeDurationsDTO>> getDetails(@RequestParam int batchId){
		return accBatchFeeHandler.getDetails(batchId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<AccBatchFeeDTO> accbatchFeeDTO, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return accBatchFeeHandler.saveOrUpdate(accbatchFeeDTO, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@PostMapping(value = "/getGridData")
	public Flux<AccBatchFeeDTO> getGridData(){
		return accBatchFeeHandler.getGridData().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/edit")
	public Mono<AccBatchFeeDTO> edit(@RequestParam int id){
		return accBatchFeeHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/delete")
    public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return accBatchFeeHandler.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }

	@PostMapping(value = "/getDemandAdjustmentCateory")
	public Flux<SelectDTO> getDemandAdjustmentCateory(){
		return accBatchFeeHandler.getDemandAdjustmentCateory().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	 */
}
