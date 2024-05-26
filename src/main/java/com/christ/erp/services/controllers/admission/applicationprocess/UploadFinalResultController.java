package com.christ.erp.services.controllers.admission.applicationprocess;

import java.io.File;
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
import com.christ.erp.services.dto.employee.recruitment.EmpApplnAdvertisementImagesDTO;
import com.christ.erp.services.handlers.admission.applicationprocess.UploadFinalResultHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value ="/Secured/Admission/ApplicationProcess/UploadFinalResult")
public class UploadFinalResultController {
	
	@Autowired
	private UploadFinalResultHandler uploadFinalResultHandler;
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/validUploadFile")
    public Mono<ApiResult> validUploadFile(@RequestPart("files") Flux<FilePart> files) {
		File directory = new File("ExcelUpload");
		if(!directory.exists()) {
			directory.mkdir();
		}
		return Utils.uploadFiles(files,directory+"//",new String[]{"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"});
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/finalResultUpload")
	public Mono<ResponseEntity<ApiResult>> finalResultUpload(@RequestParam String yearId, @RequestBody Mono<EmpApplnAdvertisementImagesDTO> data,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId)  {
	    return uploadFinalResultHandler.finalResultUpload(yearId,data,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

}