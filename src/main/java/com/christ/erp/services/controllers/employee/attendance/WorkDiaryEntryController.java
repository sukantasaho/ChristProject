package com.christ.erp.services.controllers.employee.attendance;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.employee.attendance.EmpWorkDiaryEntriesDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.employee.attendance.WorkDiaryEntryHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Employee/attendance/WorkDiaryEntry")
public class WorkDiaryEntryController {

	@Autowired
	WorkDiaryEntryHandler workDiaryEntryHandler;

	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<EmpWorkDiaryEntriesDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return workDiaryEntryHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value = "/getWorkDiaryEntryData")
	public Flux<EmpWorkDiaryEntriesDTO> getWorkDiaryEntryData(@RequestParam Map<String,String> requestParams, @RequestParam (required = false) String empId, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return workDiaryEntryHandler.getWorkDiaryEntryData(requestParams, empId, userId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));	
	}
	
	@SuppressWarnings("rawtypes")
	@DeleteMapping(value = "/delete")
	public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return workDiaryEntryHandler.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}	
} 
