package com.christ.erp.services.controllers.curriculum.settings;

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
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.curriculum.settings.AcaDurationDetailDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.curriculum.settings.CurriculumDatesHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Curriculum/Settings/CurriculumDates")
public class CurriculumDatesController {
	
	@Autowired
	CurriculumDatesHandler curriculumDatesHandler;
	
	@PostMapping(value = "/getErpCampusFromDuration")
	public Flux<SelectDTO> getErpCampusFromDuration(@RequestParam Integer academicYearId, @RequestParam List<String> sessionIdList ) {
		return curriculumDatesHandler.getErpCampus(academicYearId,sessionIdList).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}	
	
	@PostMapping(value = "/getAcaDurationdetail")
	public Flux<AcaDurationDetailDTO> getAcaDurationdetail(@RequestParam Integer academicYearId,@RequestParam List<String> sessionIdList,@RequestParam List<String> campusIdList) {
		return curriculumDatesHandler.getAcaDurationdetail(academicYearId,sessionIdList,campusIdList).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/update")
	public Mono<ResponseEntity<ApiResult>> updateAcaDurationdetail(@RequestBody Mono<List<AcaDurationDetailDTO>> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return curriculumDatesHandler.updateAcaDurationdetail(data,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
}
