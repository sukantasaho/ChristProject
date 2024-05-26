package com.christ.erp.services.controllers.hostel.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.hostel.settings.HostelFineCategoryDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.hostel.settings.FineCategoryHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Hostel/Settings/FineCategory")

public class FineCategoryController {
	
	@Autowired
	FineCategoryHandler fineCategoryHandler;
	
	@PostMapping(value = "/getGridData")
	public Flux<HostelFineCategoryDTO> getGridData(){
		return fineCategoryHandler.getGridData().switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}
	
	@PostMapping(value = "/edit")
	public Mono<HostelFineCategoryDTO> edit(@RequestParam int id) {
		return fineCategoryHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping("/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<HostelFineCategoryDTO> dto, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return fineCategoryHandler.saveOrUpdate(dto, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/delete")
	public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return fineCategoryHandler.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
}
