package com.christ.erp.services.controllers.curriculum.timeTable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.curriculum.timeTable.CourseWiseScheduleDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.curriculum.timeTable.CourseWiseScheduleHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value ="/Secured/Curriculum/TimeTable/CourseWiseSchedule")
@SuppressWarnings("rawtypes")
public class CourseWiseScheduleController {

	@Autowired
	CourseWiseScheduleHandler courseWiseScheduleHandler;
	
	@PostMapping(value = "/getCampusesByUser")
	public Flux<SelectDTO> getCampusesByUser(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
        return courseWiseScheduleHandler.getCampusesByUser(userId).switchIfEmpty(Mono.error(new NotFoundException(null)));	
	}
	
	@PostMapping(value = "/getSession")
	public Flux<SelectDTO> getSession(String yearId){
        return courseWiseScheduleHandler.getSession(yearId).switchIfEmpty(Mono.error(new NotFoundException(null)));	
	}
	
	@PostMapping(value = "/getCourseNameAndCourseCode")
	public Flux<SelectDTO> getCourseNameAndCourseCode(){
        return courseWiseScheduleHandler.getCourseNameAndCourseCode().switchIfEmpty(Mono.error(new NotFoundException(null)));	
	}
	
	@GetMapping(value = "/getGridData")
	public Flux<CourseWiseScheduleDTO> getGridData() {
		return courseWiseScheduleHandler.getGridData().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/edit")
	public Mono<CourseWiseScheduleDTO> edit(@RequestParam int timeTableTemplateId) {
		return courseWiseScheduleHandler.edit(timeTableTemplateId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<CourseWiseScheduleDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return courseWiseScheduleHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@DeleteMapping(value = "/delete")
	public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return courseWiseScheduleHandler.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
}
