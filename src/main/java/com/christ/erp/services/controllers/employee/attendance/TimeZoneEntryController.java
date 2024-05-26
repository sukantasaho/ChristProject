package com.christ.erp.services.controllers.employee.attendance;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.employee.attendance.EmpTimeZoneDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.employee.attendance.TimeZoneEntryHandler;
import com.christ.utility.lib.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/Attendance/TimeZoneEntry")
public class TimeZoneEntryController {

	@Autowired
	TimeZoneEntryHandler timeZoneEntryHandler;

	@PostMapping(value = "/getGridData")
	public Flux<EmpTimeZoneDTO> getGridData(){
		return timeZoneEntryHandler.getGridData().switchIfEmpty(Mono.error(new NotFoundException("Data Not Found For Time Zone Entry")));
	}

	@PostMapping(value = "/edit")
	public Mono<EmpTimeZoneDTO> edit(@RequestParam String ID){
		return timeZoneEntryHandler.edit(Integer.parseInt(ID)).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found For Time Zone Entry")));
	}

	@PostMapping(value = "/delete")
	public Mono<ResponseEntity<ApiResult>> delete(@RequestParam String timeZoneId, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return timeZoneEntryHandler.delete(timeZoneId,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value="/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<EmpTimeZoneDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return timeZoneEntryHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
}
