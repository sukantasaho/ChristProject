package com.christ.erp.services.controllers.hostel.student;

import java.io.File;
import java.util.List;

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
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnAdvertisementImagesDTO;
import com.christ.erp.services.dto.hostel.settings.HostelAdmissionsDTO;
import com.christ.erp.services.dto.hostel.settings.HostelDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.hostel.student.RoomAllocationHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/Secured/Hostel/Student/RoomAllocation")
public class RoomAllocationController {
	
	@Autowired
	RoomAllocationHandler roomAllocationHandler;
	
    @PostMapping(value= "/getRoomTypeForStudent")
	public Flux<SelectDTO> getRoomTypeForStudent(@RequestParam String hostelId) {
		return roomAllocationHandler.getRoomTypeForStudent(hostelId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
    
    @PostMapping(value= "/getRoomByUnitAndFloor")
   	public Flux<SelectDTO> getRoomByUnitAndFloor(@RequestParam String unitId) {
   		return roomAllocationHandler.getRoomByUnitAndFloor(unitId).switchIfEmpty(Mono.error(new NotFoundException(null)));
   	}
    
    @PostMapping(value= "/getBedByRoomId")
   	public Flux<SelectDTO> getBedByRoomId(@RequestParam String roomId) {
   		return roomAllocationHandler.getBedByRoomId(roomId).switchIfEmpty(Mono.error(new NotFoundException(null)));
   	}
    
    @PostMapping(value = "/getGridData")
	public Flux<HostelAdmissionsDTO> getGridData(@RequestParam String academicYearId,@RequestParam String hostelId,@RequestParam String roomTypeId){
		return roomAllocationHandler.getGridData(academicYearId,hostelId,roomTypeId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}
    
    @SuppressWarnings("rawtypes")
	@PostMapping("/update")
	public Mono<ResponseEntity<ApiResult>> update(@RequestBody Mono<List<HostelAdmissionsDTO>> data,@RequestParam String hostelId,@RequestParam String admissionYearId, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return roomAllocationHandler.update(data,hostelId,admissionYearId, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
    
    
    @SuppressWarnings("rawtypes")
    @PostMapping("/download")
    public Mono<ResponseEntity<ApiResult>> download(@RequestParam String admissionYearId, @RequestParam String hostelId){
    	return roomAllocationHandler.download(admissionYearId,hostelId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }
    
    @SuppressWarnings("rawtypes")
	@PostMapping("/validateUploadFile")
    public Mono<ApiResult> validateUploadFile(@RequestPart ("files") Flux<FilePart> files){
    	File directory = new File("ExcelUpload");
    	if(!directory.exists()) {
    		directory.mkdir();
    	}
    	return Utils.uploadFiles(files, directory+"//",new String[] {"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"});
    }

    @SuppressWarnings("rawtypes")
    @PostMapping("/roomAllocationUpload")
	public Mono<ResponseEntity<ApiResult>> roomAllocationUpload(@RequestParam String admissionYearId,@RequestBody Mono<EmpApplnAdvertisementImagesDTO> data ,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return roomAllocationHandler.roomAllocationUpload(admissionYearId,data,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build()); 	
    }
      
    @PostMapping(value= "/getRoomByUnitAndFloor1")
   	public Flux<SelectDTO> getRoomByUnitAndFloor1(@RequestParam String unitId,@RequestParam String roomType) {
   		return roomAllocationHandler.getRoomByUnitAndFloor1(unitId,roomType).switchIfEmpty(Mono.error(new NotFoundException(null)));
   	}  
    
    @PostMapping(value = "/gethostelDetails")
    public Flux<HostelDTO> gethostelDetails(@RequestParam String hostelId){
    	return roomAllocationHandler.gethostelDetails(hostelId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
	@SuppressWarnings("rawtypes")
	@DeleteMapping(value = "/delete")
	public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return roomAllocationHandler.delete(id,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
}