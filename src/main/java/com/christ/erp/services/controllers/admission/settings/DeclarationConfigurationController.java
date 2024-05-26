package com.christ.erp.services.controllers.admission.settings;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.admission.settings.AdmProgrammeBatchDTO;
import com.christ.erp.services.dto.admission.settings.StudentApplnDeclarationsDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.admission.settings.DeclarationConfigurationHandler;
import com.christ.utility.lib.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Admission/Settings/DeclarationConfiguration")
public class DeclarationConfigurationController {

    @Autowired
    DeclarationConfigurationHandler declarationConfigurationHandler;

    @PostMapping(value ="/getGridData")
    public Flux<StudentApplnDeclarationsDTO> getGridData(@RequestParam Integer yearId) {
        return declarationConfigurationHandler.getGridData(yearId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value = "/edit")
    public Mono<StudentApplnDeclarationsDTO> edit(@RequestParam Integer id) {
        return declarationConfigurationHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value ="/saveOrUpdate")
    public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<StudentApplnDeclarationsDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return declarationConfigurationHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }
    @DeleteMapping(value ="/delete")
    public Mono<ResponseEntity<ApiResult>> delete(@RequestParam Integer id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return declarationConfigurationHandler.delete(id,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }
}
