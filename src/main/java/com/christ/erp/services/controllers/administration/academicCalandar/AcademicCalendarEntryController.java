package com.christ.erp.services.controllers.administration.academicCalandar;
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
import com.christ.erp.services.dto.administraton.academicCalendar.ErpCalendarDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnAdvertisementImagesDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.administraton.academicCalendar.AcademicCalendarEntryHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Administraton/AcademicCalandar/AcademicCalendarEntry")
public class AcademicCalendarEntryController {
	
	@Autowired
	AcademicCalendarEntryHandler academicCalendarEntryHandler;
	
	@PostMapping(value = "getUserType")
	public Flux<SelectDTO> getUserType(){
		return academicCalendarEntryHandler.getUserType().switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));	
	}
	
	@PostMapping(value = "getAcademicCalenderApplicableFor")
	public Flux<SelectDTO> getAcademicCalenderApplicableFor(){
		return academicCalendarEntryHandler.getErpAcademicCalendarCategory().switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}
	
	@PostMapping(value = "/edit")
	public Flux<ErpCalendarDTO> edit(@RequestParam String academicYearId,@RequestParam String locId) {
		return academicCalendarEntryHandler.edit(academicYearId,locId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
    @SuppressWarnings("rawtypes")
	@PostMapping("/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<List<ErpCalendarDTO>> data,@RequestParam String academicYearId,@RequestParam String locId, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return academicCalendarEntryHandler.saveOrUpdate(data,academicYearId,locId, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
    
    @SuppressWarnings("rawtypes")
	@PostMapping(value = "/importFromPreviousYear")
	public Mono<ResponseEntity<ApiResult>> importFromPreYear(@RequestBody Mono<ErpCalendarDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return academicCalendarEntryHandler.importFromPreviousYear(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
    
    @SuppressWarnings("rawtypes")
	@PostMapping("/validateUploadFile")
    public Mono<ApiResult> validateUploadFile(@RequestPart ("files") Flux<FilePart> files){
    	File directory = new File("ExcelUpload1");
    	if(!directory.exists()) {
    		directory.mkdir();
    	}
    	return Utils.uploadFiles(files, directory+"//",new String[] {"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"});
//    	resources/template_with_macro.xlsm                       
    }
    
    @SuppressWarnings("rawtypes")
    @PostMapping("/academiccalenderUpload")
	public Mono<ResponseEntity<ApiResult>> academiccalenderUpload(@RequestParam String academicId,@RequestBody Mono<EmpApplnAdvertisementImagesDTO> data ,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return academicCalendarEntryHandler.academiccalenderUpload(academicId,data,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build()); 	
    }
}