package com.christ.erp.services.controllers.hostel.application;

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
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.hostel.settings.HostelApplicationDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.hostel.application.PublishSelectionHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Hostel/Application/PublishSelection")

public class PublishSelectionController {

	@Autowired
	PublishSelectionHandler publishSelectionHandler;
	
	@PostMapping(value="/getGridData")
	public Flux<HostelApplicationDTO> getGridData(@RequestParam String yearId, @RequestParam String hostelId, @RequestParam Boolean isPublished) {
		return publishSelectionHandler.getGridData(yearId, hostelId, isPublished).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/publishSelectionUpdate")
	public Mono<ResponseEntity<ApiResult>> publishSelectionUpdate(@RequestBody Mono<List<HostelApplicationDTO>> data,
			@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return publishSelectionHandler.publishSelectionUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@PostMapping(value = "/getHostelStatus")
	public Flux<SelectDTO> getHostelStatus() {
		return publishSelectionHandler.getHostelStatus().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
}
