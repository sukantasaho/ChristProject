package com.christ.erp.services.controllers.hostel.leavesandattendance;

import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.hostel.leavesandattendance.HostelLeaveApplicationsDTO;
import com.christ.erp.services.dto.hostel.settings.HostelAdmissionsDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.hostel.leavesandattendance.HostelOfflineLeaveApplicationHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Hostel/LeavesAndAttendance/HostelOfflineLeaveApplication")
public class HostelOfflineLeaveApplicationController {
	
	@Autowired
	HostelOfflineLeaveApplicationHandler hostelOfflineLeaveApplicationHandler;
	
	@PostMapping(value = "/getStudentDetails")
	public Mono<HostelAdmissionsDTO> getStudentDetails(@RequestParam String registerNo,@RequestParam String yearId){
		return hostelOfflineLeaveApplicationHandler.getStudentDetails(registerNo,yearId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}
	
	@PostMapping(value = "/getGridData")
	public Flux<HostelLeaveApplicationsDTO> getGridData(@RequestParam String academicYear,@RequestParam String hostel,@RequestParam String blockId,@RequestParam String unitId){
		return hostelOfflineLeaveApplicationHandler.getGridData(academicYear,hostel,blockId,unitId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));	
	}
	
	@PostMapping(value = "/edit")
	public Mono<HostelLeaveApplicationsDTO> edit(@RequestParam int id){
		return hostelOfflineLeaveApplicationHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@SuppressWarnings("rawtypes")
	@DeleteMapping(value = "/delete")
	public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return hostelOfflineLeaveApplicationHandler.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<HostelLeaveApplicationsDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return hostelOfflineLeaveApplicationHandler.saveOrUpdate(data,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping("/uploadFile")
    public Mono<ApiResult> uploadFile(@RequestPart("files") Flux<FilePart> data) {
		File directory = new File("ImageUpload");
		if(!directory.exists()) {
			directory.mkdir();
		}
		return Utils.uploadFiles(data,directory.getAbsolutePath(),new String[]{"jpg","png","jpeg","pdf","doc"});
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/wardenApprove")
	public Mono<ResponseEntity<ApiResult>> wardenApprove(@RequestBody Mono<HostelLeaveApplicationsDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return hostelOfflineLeaveApplicationHandler.wardenApprove(data,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}	
}