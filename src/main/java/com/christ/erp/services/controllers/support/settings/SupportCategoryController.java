package com.christ.erp.services.controllers.support.settings;


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
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.support.settings.SupportCategoryDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.support.settings.SupportCategoryHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Support/Settings/SupportCategories")
public class SupportCategoryController extends BaseApiController {

	@Autowired
	SupportCategoryHandler supportCategoriesHandler;
	
	@PostMapping(value = "/getGridData")
	public Flux<SupportCategoryDTO> getGriddata(){
		return supportCategoriesHandler.getGridData().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdateSupportCategory(@RequestBody Mono<SupportCategoryDTO> supportCategoryDTOMono, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return supportCategoriesHandler.saveOrUpdateSupportCategory(supportCategoryDTOMono, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value = "/edit")
	public Mono<SupportCategoryDTO> editSupportCategory(@RequestParam int id){
		return supportCategoriesHandler.editSupportCategory(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/delete")
    public Mono<ResponseEntity<ApiResult>> deleteSupportCategory(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return supportCategoriesHandler.deleteSupportCategory(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }
	
	@PostMapping(value = "/getCampus")
	public Flux<SelectDTO> getCampus(){
		return supportCategoriesHandler.getCampus().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
}
