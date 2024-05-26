package com.christ.erp.services.controllers.employee.leave;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnAdvertisementImagesDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.employee.leave.BulkLeaveUploadHandler;
import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.erp.services.transactions.employee.leave.BulkLeaveUploadTransaction;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/Leave/BulkLeaveUpload")
public class BulkLeaveUploadController {
	
	@Autowired
	BulkLeaveUploadTransaction bulkLeaveUploadTransaction;
	
	@Autowired
	CommonApiTransaction commonApiTransaction1;
	
	@Autowired
	BulkLeaveUploadHandler bulkLeaveUploadHandler;
	
	
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/validUploadFile")
    public Mono<ApiResult> validUploadFile(@RequestPart("files") Flux<FilePart> files) {
		File directory = new File("D://BulkLeaveUploadExcel//");
		if(!directory.exists()) {
			directory.mkdir();
		}
		return Utils.uploadFiles(files,directory+"//",new String[]{"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"});
	}
	
//	@SuppressWarnings("rawtypes")
//	@PostMapping(value = "/bulkLeaveUpload")
//	public Mono<ResponseEntity<ApiResult>> bulkLeaveUpload(@RequestBody Mono<EmpApplnAdvertisementImagesDTO> data,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId)  {
//	    return bulkLeaveUploadHandler.bulkLeaveUpload(data,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
//	}

	
	@PostMapping(value = "/bulkLeaveUpload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Mono<ResponseEntity<ApiResult>> bulkLeaveUpload(@RequestBody Mono<EmpApplnAdvertisementImagesDTO> data,@RequestParam MultipartFile file,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId)  {
	    return bulkLeaveUploadHandler.bulkLeaveUpload(data,file,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@PostMapping(value = "/bulkLeaveUploadDownloadFormat")
	public  Mono<FileUploadDownloadDTO> bulkLeaveUploadDownloadFormat()  {
	    return bulkLeaveUploadHandler.bulkLeaveUploadDownloadFormat().switchIfEmpty(Mono.error(new NotFoundException(null)));
		
	}

}
