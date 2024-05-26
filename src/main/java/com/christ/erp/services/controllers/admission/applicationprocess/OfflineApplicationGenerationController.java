package com.christ.erp.services.controllers.admission.applicationprocess;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.admission.applicationprocess.OfflineApplicationGenerationDTO;
import com.christ.erp.services.dto.admission.applicationprocess.OfflineApplicationIssueDTO;
import com.christ.erp.services.dto.admission.settings.AdmProgrammeSettingsDTO;
import com.christ.erp.services.dto.common.ErpCommitteeDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.salary.EmpPayScaleComponentsDTO;
import com.christ.erp.services.dto.student.common.StudentApplicationEditDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.admission.applicationprocess.MarksVerificationHandler;
import com.christ.erp.services.handlers.admission.applicationprocess.OfflineApplicationGenerationHandler;
import com.christ.erp.services.handlers.employee.salary.SalaryComponentsHandler;
import com.christ.utility.lib.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/Secured/Admission/ApplicationProcess/OfflineApplicationGeneration")
public class OfflineApplicationGenerationController {

    @Autowired
    OfflineApplicationGenerationHandler offlineApplicationGenerationHandler;

    @GetMapping(value = "/getGridData")
    public Flux<OfflineApplicationGenerationDTO> getGridData(@RequestParam Integer yearId) {
        return offlineApplicationGenerationHandler.getGridData(yearId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @SuppressWarnings("rawtypes")
    @PostMapping(value = "/saveOrUpdate")
    public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<OfflineApplicationGenerationDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return offlineApplicationGenerationHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PostMapping(value = "/edit")
    public Mono<OfflineApplicationGenerationDTO> edit(@RequestParam int id) {
        return offlineApplicationGenerationHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @SuppressWarnings("rawtypes")
    @PostMapping(value = "/delete")
    public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return offlineApplicationGenerationHandler.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PostMapping(value = "/getProgrammeByDate")
    public Flux<SelectDTO> getProgrammeByDate(@RequestParam String date) {
        return offlineApplicationGenerationHandler.getProgrammeByDate(date).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value = "/getIntakeBatchByProgramme")
    public Flux<SelectDTO> getIntakeBatchByProgramme(@RequestParam Integer programmeId) {
        return offlineApplicationGenerationHandler.getIntakeBatchByProgramme(programmeId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value = "/getAdmissionTypeByBatch")
    public Flux<SelectDTO> getAdmissionTypeByBatch(@RequestParam Integer programmeId, Integer intakeBatchId) {
        return offlineApplicationGenerationHandler.getAdmissionTypeByBatch(programmeId, intakeBatchId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value = "/getCampusOrLocation")
    public Flux<SelectDTO> getCampusOrLocation(@RequestParam Integer programmeId, Integer intakeBatchId, Integer admissionTypeId) {
        return offlineApplicationGenerationHandler.getCampusOrLocation(programmeId, intakeBatchId, admissionTypeId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

}
