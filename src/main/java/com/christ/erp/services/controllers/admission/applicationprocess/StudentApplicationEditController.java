package com.christ.erp.services.controllers.admission.applicationprocess;

import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.student.common.StudentApplicationEditDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.admission.applicationprocess.StudentApplicationEditHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value ="/Secured/Admission/Applicationprocess/StudentApplicationEdit")
@SuppressWarnings("rawtypes")
public class StudentApplicationEditController {

	@Autowired
	private StudentApplicationEditHandler  studentApplicationEditHandler ;

	@GetMapping(value = "/getApplicantListCard")
	public Flux<StudentApplicationEditDTO> getApplicantListCard(@RequestParam String yearId, @RequestParam String applicationNoOrName, @RequestParam String programmeId) {
		return studentApplicationEditHandler.getApplicantListCard(yearId,applicationNoOrName,programmeId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@GetMapping(value = "/getStudentDetails")
	public Mono<StudentApplicationEditDTO> getStudentDetails(@RequestParam Integer studentId) {
		return studentApplicationEditHandler.getStudentDetails(studentId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<StudentApplicationEditDTO> data,@RequestParam String tabName,  @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return studentApplicationEditHandler.saveOrUpdate(data,tabName,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping("/uploadFiles")
	public Mono<ApiResult> uploadFiles(@RequestPart("files") Flux<FilePart> data) {
		File directory = new File("StudentEducationalDocumentFiles");
		if(!directory.exists()) {
			directory.mkdir();
		}
		return Utils.uploadFiles(data,directory+"//",new String[]{"jpeg","png"});
	}

	@PostMapping("/studentWorkExpuploadFiles")
	public Mono<ApiResult> studentWorkExpuploadFiles(@RequestPart("files") Flux<FilePart> data) {
		File directory = new File("StudentWorkExperienceDocumentFiles");
		if(!directory.exists()) {
			directory.mkdir();
		}
		return Utils.uploadFiles(data,directory+"//",new String[]{"jpeg","png"});
	}

}
