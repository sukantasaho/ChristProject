package com.christ.erp.services.controllers.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.common.GUIMenuShortcutLinkDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.common.GUIHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Protected/Common/GUI")
public class GUIController {

	@Autowired
	GUIHandler guiHandler;
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/saveOrDeleteFavouriteAndRecent")
	public Mono<ResponseEntity<ApiResult>>  saveOrDeleteFavouriteAndRecent(@RequestBody Mono<GUIMenuShortcutLinkDTO> dto ,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return guiHandler.saveOrDeleteFavouriteAndRecent(dto,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@PostMapping(value = "/getFavouriteAndRecentList")
	public Mono<GUIMenuShortcutLinkDTO> getFavouriteAndRecentList(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return guiHandler.getFavouriteAndRecentList(userId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
//	@PostMapping(value = "/getUserMeuLog")
//	public Mono<GUIUserViewPreferenceDTO> getUserMeuLog(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
//		return guiHandler.getUserMeuLog(userId).switchIfEmpty(Mono.error(new NotFoundException(null)));
//	}	
}
