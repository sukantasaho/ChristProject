package com.christ.erp.services.controllers.employee.attendance;

import com.christ.erp.services.dto.employee.common.EmployeeApplicationDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.utility.lib.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.employee.attendance.HolidaysAndEventsEntryDTO;
import com.christ.erp.services.handlers.employee.attendance.HolidaysAndEventsHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/Attendance/HolidaysAnsEvents")
public class HolidaysAndEventsController {
	@Autowired
	HolidaysAndEventsHandler holidaysAndEventsHandler;

	@PostMapping(value = "/getGridData")
	public Flux<HolidaysAndEventsEntryDTO> getGridData(@RequestParam Integer academicYearId) {
		return holidaysAndEventsHandler.getGridData(academicYearId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found For Holiday And Event Entry")));
	}

	@PostMapping(value = "/edit")
	public Mono<HolidaysAndEventsEntryDTO> edit(@RequestParam String id){
		return holidaysAndEventsHandler.edit(Integer.parseInt(id)).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found For Holiday And Event Entry")));
	}

	@PostMapping(value = "/delete")
	public Mono<ResponseEntity<ApiResult>> delete(@RequestParam String headingId, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return holidaysAndEventsHandler.delete(headingId,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value="/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<HolidaysAndEventsEntryDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return holidaysAndEventsHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value = "/getEmployeeByIdsByEmpCategory")
	public Flux<EmployeeApplicationDTO> getEmployeeByIdsByEmpCategory(@RequestBody HolidaysAndEventsEntryDTO data) {
        return holidaysAndEventsHandler.getEmployeeByIdsByEmpCategory(data).switchIfEmpty(Mono.error(new NotFoundException("No Data Found For The Employee List.")));
    }

}