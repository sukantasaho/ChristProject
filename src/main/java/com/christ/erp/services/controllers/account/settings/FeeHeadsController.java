package com.christ.erp.services.controllers.account.settings;

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
import com.christ.erp.services.dto.account.AccFeeHeadsAccountDTO;
import com.christ.erp.services.dto.account.AccFeeHeadsDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.account.settings.FeeHeadsHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Accounts/FeeHeads")
public class FeeHeadsController extends BaseApiController {
	@Autowired
	FeeHeadsHandler feeHeadsHandler;
	
	@PostMapping(value = "/getGridData")
	public Flux<AccFeeHeadsDTO> getGridData(){
		return feeHeadsHandler.getGridData().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/getCampusAccountNumberList")
	public Flux<AccFeeHeadsAccountDTO> getCampusAccountNumberList(){
		return feeHeadsHandler.getCampusAndAccountNoDTO().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/getFeeGroup")
	public Flux<SelectDTO> getFeeGroup(){
		return feeHeadsHandler.getFeeGroup().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<AccFeeHeadsDTO> AccFeeHeadsDTO, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return feeHeadsHandler.saveOrUpdate(AccFeeHeadsDTO, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value = "/edit")
	public Mono<AccFeeHeadsDTO> edit(@RequestParam int id){
		return feeHeadsHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	@PostMapping(value = "/delete")
    public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return feeHeadsHandler.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }
	
	@PostMapping(value = "/getAccAccountsByHostel")
	public Flux<SelectDTO> getAccAccountsByHostel(int hostelId){
		return feeHeadsHandler.getAccAccountsByHostel(hostelId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
}
