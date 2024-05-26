package com.christ.erp.services.controllers.curriculum.Classes;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.curriculum.Classes.AcaClassGroupDTO;
import com.christ.erp.services.dto.student.common.StudentDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.curriculum.Classes.ClassGroupHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Curriculum/Classes/ClassGroup")

public class ClassGroupController {

	@Autowired
	ClassGroupHandler classGroupHandler;

	@PostMapping(value = "/getAcaStudentList")
	public Flux<StudentDTO> getAcaStudentList(@RequestParam(required = false) Integer courseId, @RequestParam(required = false) List<Integer> classIdList, @RequestParam(required = false)Integer activityId, @RequestParam Integer sessionGroupId, @RequestParam Integer academicYearId) {
		return classGroupHandler.getAcaStudentList(courseId, classIdList, activityId, sessionGroupId, academicYearId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/getGridData")
	public Flux<AcaClassGroupDTO>getGridData(@RequestParam String academicYearId, @RequestParam String sessionId, @RequestParam String campusId) {
		return classGroupHandler.getGridData(academicYearId, sessionId, campusId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}

	@SuppressWarnings("rawtypes")
	@PostMapping("/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<AcaClassGroupDTO> dto, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return classGroupHandler.saveOrUpdate(dto, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value= "/edit")
	public Mono<AcaClassGroupDTO> edit(@RequestParam int id) {
		return classGroupHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/delete")
	public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return classGroupHandler.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
}
