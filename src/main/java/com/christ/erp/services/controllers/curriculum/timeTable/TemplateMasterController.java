package com.christ.erp.services.controllers.curriculum.timeTable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.curriculum.timeTable.TemplateMasterDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.curriculum.timeTable.TemplateMasterHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressWarnings({"rawtypes"})
@RestController
@RequestMapping(value = "/Secured/Curriculum/TimeTable/TemplateMaster")
public class TemplateMasterController {

	@Autowired
	private TemplateMasterHandler templateMasterHandler;

	@GetMapping(value = "/getGridData")
	public Flux<TemplateMasterDTO> getGridData() {
		return templateMasterHandler.getGridData().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/edit")
	public Mono<TemplateMasterDTO> edit(@RequestParam int timeTableTemplateId) {
		return templateMasterHandler.edit(timeTableTemplateId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<TemplateMasterDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return templateMasterHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@DeleteMapping(value = "/delete")
	public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return templateMasterHandler.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

}
