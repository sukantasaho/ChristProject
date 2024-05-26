package com.christ.erp.services.controllers.admission.applicationprocess;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.admission.applicationprocess.AdmWeightageDefinitionDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.admission.applicationprocess.WeightageDefinitionSettingsHandler;
import com.christ.utility.lib.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Admission/ApplicationProcess/WeightageDefinitionSettings")
@SuppressWarnings("rawtypes")
public class WeightageDefinitionSettingsController {

	@Autowired
	private WeightageDefinitionSettingsHandler weightageDefinitionSettingsHandler;

    @PostMapping(value = "/getGridData")
    //, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AdmWeightageDefinitionDTO> getGridData() {
        return weightageDefinitionSettingsHandler.getGridData().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
    @PostMapping(value = "/edit")
    public Mono<AdmWeightageDefinitionDTO> edit(@RequestParam int id) {
        return weightageDefinitionSettingsHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

	@PostMapping(value = "/saveOrUpdate")
    public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<AdmWeightageDefinitionDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return weightageDefinitionSettingsHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }

	@PostMapping(value = "/delete")
    public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return weightageDefinitionSettingsHandler.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }
}

