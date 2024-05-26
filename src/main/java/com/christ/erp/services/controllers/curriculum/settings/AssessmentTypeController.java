package com.christ.erp.services.controllers.curriculum.settings;

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
import com.christ.erp.services.dto.curriculum.settings.ExamAssessmentTemplateDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.curriculum.settings.AssessmentTypeHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value ="/Secured/Curriculum/Settings/AssessmentType")
public class AssessmentTypeController {
	
	@Autowired
	private AssessmentTypeHandler assessmentTypeHandler;
	
	@PostMapping(value = "/getRatio") 
    public Flux<SelectDTO> getRatio() {
    	 return assessmentTypeHandler.getRatio().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getAssessmentCategory") 
    public Flux<SelectDTO> getAssessmentCategory(@RequestParam String examType) {
    	 return assessmentTypeHandler.getAssessmentCategory(examType.trim()).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getModeOfExam") 
    public Flux<SelectDTO> getModeOfExam() {
    	 return assessmentTypeHandler.getModeOfExam().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getAttendanceType") 
    public Flux<SelectDTO> getAttendanceType() {
    	 return assessmentTypeHandler.getAttendanceType().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
    @GetMapping(value = "/getGridData")
    public Flux<ExamAssessmentTemplateDTO> getGridData() {
    	return assessmentTypeHandler.getGridData().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
    @SuppressWarnings("rawtypes")
	@DeleteMapping(value = "/delete")
    public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return assessmentTypeHandler.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }
    
    @PostMapping(value = "/edit")
    public Mono<ExamAssessmentTemplateDTO> edit(@RequestParam int id) {
        return assessmentTypeHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @SuppressWarnings("rawtypes")
	@PostMapping(value = "/saveOrUpdate")
    public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<ExamAssessmentTemplateDTO> dto, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return assessmentTypeHandler.saveOrUpdate(dto, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }
}