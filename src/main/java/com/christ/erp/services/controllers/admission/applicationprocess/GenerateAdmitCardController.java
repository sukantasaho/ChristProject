package com.christ.erp.services.controllers.admission.applicationprocess;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.admission.applicationprocess.GenerateAdmitCardDTO;
import com.christ.erp.services.dto.admission.settings.AdmProgrammeSettingsDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.admission.applicationprocess.GenerateAdmitCardHandler;
import com.christ.utility.lib.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping(value ="/Secured/Admission/ApplicationProcess/GenerateAdmitCard")
public class GenerateAdmitCardController {

    @Autowired
    private GenerateAdmitCardHandler generateAdmitCardHandler;

    /*@PostMapping(value = "/getSelectionDate")
    public Flux<LookupItemDTO> getSelectionDate(@RequestParam int year) {
        return generateAdmitCardHandler.getSelectionDate(year).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }*/

    @PostMapping(value = "/saveOrUpdate")
    public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<GenerateAdmitCardDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return generateAdmitCardHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PostMapping("/uploadFiles")
    public Mono<ApiResult> uploadFiles(@RequestPart("files") Flux<FilePart> data) throws IOException {
        //File directory = new File("E:\\Intellij\\Admin Services\\SelectioProcessExcelUpload");
        File directory = new File("SelectioProcessExcelUpload");
        if(!directory.exists()) {
            directory.mkdir();
        }
        return Utils.uploadFiles(data,directory+"//",new String[]{"application/vnd.ms-excel",".csv",".xlsx",".xls","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"});
        //return generateAdmitCardHandler.uploadFiles(data,directory+"//",new String[]{"application/vnd.ms-excel",".csv",".xlsx",".xls","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"});
    }

    @GetMapping(value = "/getGridData")
    public Flux<GenerateAdmitCardDTO> getGridData(@RequestParam Integer admissionYearId) {
            return generateAdmitCardHandler.getGridData(admissionYearId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @GetMapping(value = "/getAdmitCardDetailsBySession")
    public Flux<GenerateAdmitCardDTO> getAdmitCardDetailsBySession(@RequestParam Integer sessionPlanId) {
        return generateAdmitCardHandler.getAdmitCardDetailsBySession(sessionPlanId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

//    @PostMapping(value= "/generateRegeneratePublishAdmitCard")
//    public Mono<ResponseEntity<ApiResult>> generateRegeneratePublishAdmitCard(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
//        return generateAdmitCardHandler.generateRegeneratePublishAdmitCard(userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
//    }
}
