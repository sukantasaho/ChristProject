package com.christ.erp.services.controllers.admission.applicationprocess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.admission.applicationprocess.SelectionProcessGroupEditDTO;
import com.christ.erp.services.dto.admission.applicationprocess.SelectionProcessGroupEditDetailsDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.admission.applicationprocess.SelectionProcessGroupEditHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.christ.utility.lib.Constants;

@RestController
@RequestMapping(value = "/Secured/Admission/ApplicationProcess/SelectionProcessGroupEdit")
public class SelectionProcessGroupEditController {

    @Autowired
    private SelectionProcessGroupEditHandler selectionProcessGroupEditHandler;

    @PostMapping(value = "/getSelectionDate")
    public Flux<LookupItemDTO> getSelectionDate(@RequestParam int year) {
        return selectionProcessGroupEditHandler.getSelectionDate(year).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value = "/getSelectionGroup")
    public Flux<LookupItemDTO> getSelectionGroup(@RequestParam String date, @RequestParam String timeId) {
        return selectionProcessGroupEditHandler.getSelectionGroup(date, timeId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value = "/getGroupApplicantsData")
    public Flux<SelectionProcessGroupEditDetailsDTO> getGroupApplicantsData(@RequestParam String groupId) {
        return selectionProcessGroupEditHandler.getGroupApplicantsData(groupId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value = "/getApplicantData")
    public Flux<SelectionProcessGroupEditDetailsDTO> getApplicantData(@RequestParam String applicationNo, @RequestParam String id, String venueCityId) {
        return selectionProcessGroupEditHandler.getApplicantData(applicationNo, id, venueCityId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @SuppressWarnings("rawtypes")
    @PostMapping(value = "/saveOrUpdate")
    public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<SelectionProcessGroupEditDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return selectionProcessGroupEditHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PostMapping(value = "/move")
    public Mono<ResponseEntity<ApiResult>> move(@RequestBody Mono<SelectionProcessGroupEditDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return selectionProcessGroupEditHandler.move(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }

}
