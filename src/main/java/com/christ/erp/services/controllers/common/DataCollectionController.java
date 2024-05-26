package com.christ.erp.services.controllers.common;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.common.ErpDataCollectionTemplateDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.common.DataCollectionHandler;
import com.christ.utility.lib.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Common/DataCollection")
public class DataCollectionController {

    @Autowired
    private DataCollectionHandler dataCollectionHandler;

    @PostMapping(value = "/getGridData")
    public Flux<ErpDataCollectionTemplateDTO> getGridData() {
        return dataCollectionHandler.getGridData().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value="/saveOrUpdate")
    public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<ErpDataCollectionTemplateDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return dataCollectionHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PostMapping(value= "/edit")
    public Mono<ErpDataCollectionTemplateDTO> edit(@RequestParam int id) {
        return dataCollectionHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value= "/delete")
    public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID)String userId) {
        return dataCollectionHandler.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }
}
