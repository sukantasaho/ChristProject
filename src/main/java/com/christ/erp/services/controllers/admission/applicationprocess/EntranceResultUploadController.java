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
import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnAdvertisementImagesDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.admission.applicationprocess.EntranceResultUploadHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value ="/Secured/Admission/ApplicationProcess/EntranceResultUpload")
public class EntranceResultUploadController {
	
	@Autowired
	private EntranceResultUploadHandler entranceResultUploadHandler;
	
	
	@PostMapping(value = "/getEntranceTest")
    public Flux<SelectDTO> getEntranceTest() {
        return entranceResultUploadHandler.getEntranceTest().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/entranceResultUpload")
	public Mono<ResponseEntity<ApiResult>> entranceResultUpload(@RequestParam String yearId,@RequestParam String selectionTypeId, @RequestBody Mono<EmpApplnAdvertisementImagesDTO> data,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId)  {
	    return entranceResultUploadHandler.entranceResultUpload(yearId,selectionTypeId,data,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@PostMapping(value = "/entranceResultUploadDownloadFormat")
	public  Mono<FileUploadDownloadDTO> entranceResultUploadDownloadFormat()  {
	    return entranceResultUploadHandler.entranceResultUploadDownloadFormat().switchIfEmpty(Mono.error(new NotFoundException(null)));
		
	}
}
