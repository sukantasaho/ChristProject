package com.christ.erp.services.controllers.employee.attendance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.employee.attendance.EmpWorkDiaryActivityDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.employee.attendance.WorkDiaryActivityHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Employee/Attendance/WorkDiaryActivity")
public class WorkDiaryActivityController {
	
	@Autowired
	WorkDiaryActivityHandler workDiaryActivityHandler;
	
	//@GetMapping(value = "/getGridData", produces =  MediaType.TEXT_EVENT_STREAM_VALUE)
	@PostMapping(value = "/getGridData")
	public Flux<EmpWorkDiaryActivityDTO> getGridData(){
		return workDiaryActivityHandler.getGridData().switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<EmpWorkDiaryActivityDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return workDiaryActivityHandler.saveOrUpdate(data,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@PostMapping(value = "/edit")
	public Mono<EmpWorkDiaryActivityDTO> edit(@RequestParam int id){
		return workDiaryActivityHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));	
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/delete")
	public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return workDiaryActivityHandler.delete(id,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
}
