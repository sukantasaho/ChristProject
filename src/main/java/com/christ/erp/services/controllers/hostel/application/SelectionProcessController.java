package com.christ.erp.services.controllers.hostel.application;

import java.io.File;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.employee.common.ErpProgrammeLevelDTO;
import com.christ.erp.services.dto.hostel.settings.HostelApplicationDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.hostel.application.SelectionProcessHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Hostel/Application/SelectionProcess")

public class SelectionProcessController {
	
	@Autowired
	private SelectionProcessHandler selectionProcessHandler;
	
	@PostMapping(value = "/getGridData")
	public Flux<HostelApplicationDTO> getGridData(@RequestParam String yearId, @RequestParam String hostelId) {
		return selectionProcessHandler.getGridData(yearId, hostelId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping("/update")
	public Mono<ResponseEntity<ApiResult>> update(@RequestBody Mono<List<HostelApplicationDTO>> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return selectionProcessHandler.update(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value ="/downloadExcelSheet")
	public Mono<ResponseEntity<ApiResult>> downloadExcelSheet(@RequestParam String yearId, @RequestParam String hostelId) {
		return selectionProcessHandler.downloadExcelSheet(yearId, hostelId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/hostelUploadFile")
    public Mono<ApiResult> validUploadFile(@RequestPart("files") Flux<FilePart> files) {
		File directory = new File("ExcelFileUpload");
		if(!directory.exists()) {
			directory.mkdir();
		}
		return Utils.uploadFiles(files, directory+"//", new String[]{"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"});
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/selectionProcessUpload")
	public Mono<ResponseEntity<ApiResult>> selectionProcessUpload(@RequestParam String yearId, @RequestParam String hostelId, @RequestBody Mono<HostelApplicationDTO> data,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
	    return selectionProcessHandler.selectionProcessUpload(yearId, hostelId, data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping("/updateReviewed")
	public Mono<ResponseEntity<ApiResult>> updateReviewed(@RequestBody Mono<HostelApplicationDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return selectionProcessHandler.updateReviewed(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@PostMapping(value ="/checkIsStudent")
	public Mono<Boolean> checkIsStudent(@RequestParam String applnId) {
		return selectionProcessHandler.checkIsStudent(applnId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}	
	
	@PostMapping(value = "/getProgrammeLevelCount")
	public Flux<ErpProgrammeLevelDTO> getProgrammeLevelCount(@RequestParam String yearId, @RequestParam String hostelId) {
		return selectionProcessHandler.getProgrammeLevelCount(yearId, hostelId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
}
