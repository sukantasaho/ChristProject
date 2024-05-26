package com.christ.erp.services.controllers.hostel.fineanddisciplinary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.hostel.fineanddisciplinary.HostelDisciplinaryActionsDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.hostel.fineanddisciplinary.DisciplinaryActionEntryHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Hostel/Fineanddisciplinary/DisciplinaryActionEntry")

public class DisciplinaryActionEntryController {

	@Autowired
	DisciplinaryActionEntryHandler disciplinaryActionEntryHandler;

	@PostMapping(value="/getGridData")
	public Flux<HostelDisciplinaryActionsDTO> getGridData(@RequestParam String yearId, @RequestParam String hostelId, @RequestParam (required =false) String blockId, @RequestParam(required =false) String unitId) {
		return disciplinaryActionEntryHandler.getGridData(yearId, hostelId, blockId, unitId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@SuppressWarnings("rawtypes")
	@PostMapping("/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<HostelDisciplinaryActionsDTO> dto, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return disciplinaryActionEntryHandler.saveOrUpdate(dto, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value= "/edit")
	public Mono<HostelDisciplinaryActionsDTO> edit(@RequestParam int id) {
		return disciplinaryActionEntryHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/delete")
	public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return disciplinaryActionEntryHandler.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
}
