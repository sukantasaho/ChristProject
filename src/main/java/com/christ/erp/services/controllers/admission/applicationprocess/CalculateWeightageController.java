package com.christ.erp.services.controllers.admission.applicationprocess;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.admission.applicationprocess.CalculateWeightageDTOWebFlux;
import com.christ.erp.services.handlers.admission.applicationprocess.CalculateWeightageHandler;
import com.christ.utility.lib.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value ="/Secured/Admission/ApplicationProcess/CalculateWeightage")
public class CalculateWeightageController {

    @Autowired
    private CalculateWeightageHandler handler;

    @PostMapping(value = "/saveOrUpdate")
    public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<CalculateWeightageDTOWebFlux> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return handler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }

}
