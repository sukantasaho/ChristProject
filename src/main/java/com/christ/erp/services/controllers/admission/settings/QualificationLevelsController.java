package com.christ.erp.services.controllers.admission.settings;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.admission.applicationprocess.AdmQualificationListDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.admission.settings.QualificationLevelsHandler;
import com.christ.utility.lib.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/admission/Setting/QualificationLevels")
public class QualificationLevelsController {

    @Autowired
    QualificationLevelsHandler admQualificationHandler ;


    @PostMapping(value = "/delete")
    public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
        return admQualificationHandler.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @SuppressWarnings("rawtypes")
    @PostMapping(value = "/saveOrUpdate")
    public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<AdmQualificationListDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return admQualificationHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PostMapping(value = "/edit")
    public Mono<AdmQualificationListDTO> edit(@RequestParam int id) {
        return admQualificationHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value = "/getGridData")
    public Flux<AdmQualificationListDTO> getGridData() {
        return admQualificationHandler.getGridData().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }


}
