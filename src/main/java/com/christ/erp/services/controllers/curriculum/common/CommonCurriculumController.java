package com.christ.erp.services.controllers.curriculum.common;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.dto.common.AcaCourseDTO;
import com.christ.erp.services.dto.common.ErpAcademicYearDTO;
import com.christ.erp.services.dto.common.ErpCommitteeDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.curriculum.common.AcaSessionGroupDTO;
import com.christ.erp.services.dto.curriculum.common.AcaSessionTypeDTO;
import com.christ.erp.services.dto.student.common.StudentDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.curriculum.common.CommonCurriculumHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/Protected/Curriculum/Common/CommonCurriculum")
public class CommonCurriculumController {
	
	@Autowired
	CommonCurriculumHandler commonCurriculumHandler;
	
	//@GetMapping(value ="/getExternalCatergory", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@PostMapping(value="/getExternalsCatergory")
	public Flux<SelectDTO> getExternalsCategory(){
		return commonCurriculumHandler.getExternalsCategory().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value="/getStudentDetails")
	public Flux<StudentDTO> getStudentDetails(@RequestParam Integer admittedYear){
		return commonCurriculumHandler.getStudentDetails(admittedYear).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/getEmpsByDepartment") 
    public Flux<SelectDTO> getEmpsByDepartment(@RequestParam String departmentId) {
    	 return commonCurriculumHandler.getEmpsByDepartment(departmentId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getExternalMembers")
    public Flux<SelectDTO> getExternalMembersByCategory(@RequestParam int categoryId) {
    	 return commonCurriculumHandler.getExternalMembersByCategory(categoryId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getCommitteeRoles")
    public Flux<SelectDTO> getCommitteeRoles() {
    	 return commonCurriculumHandler.getCommitteeRoles().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getProgramme")
    public Flux<SelectDTO> getProgrammeByDepartment(@RequestParam int departId) {
    	 return commonCurriculumHandler.getProgrammeByDepartment(departId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getProgrammes")
    public Flux<SelectDTO> getProgrammeByDepartmentFromDeptMapping(@RequestParam int departId) {
    	 return commonCurriculumHandler.getProgrammeByDepartmentFromDeptMapping(departId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value="/getRBTDomains")
	public Flux<SelectDTO> getRBTDomains(){
		return commonCurriculumHandler.getRBTDomains().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
    @PostMapping(value = "/getImportYearList") 
    public Mono<List<ErpCommitteeDTO>> getImportYearList(@RequestParam String committeeType) {
    	 return commonCurriculumHandler.getImportYearList(committeeType).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
    @PostMapping(value="/getCampusByDepartment")
	public Flux<SelectDTO> getCampusByDepartment(@RequestParam int departmentId){
		return commonCurriculumHandler.getCampusByDepartment(departmentId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
    
	@PostMapping(value = "/getAcaCourseType") 
    public Flux<SelectDTO> getAcaCourseType() {
    	 return commonCurriculumHandler.getAcaCourseType().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getSpecialization")
	public Flux<SelectDTO> getSpecialization() {
		return commonCurriculumHandler.getSpecialization().switchIfEmpty(Mono.error(new  NotFoundException(null)));
	}
    
	@PostMapping(value = "/getAcaSessionType")
	public Flux<AcaSessionTypeDTO> getAcaSessionType() {
		return commonCurriculumHandler.getAcaSessionType().switchIfEmpty(Mono.error(new  NotFoundException(null)));
	}
	
	@PostMapping(value = "/getAcaSession")
	public Flux<SelectDTO> getAcaSession(@RequestParam String sessionTypeId,@RequestParam(required = false) Integer sessionNumber, @RequestParam(required = false) Boolean isTerm) {
		return commonCurriculumHandler.getAcaSession(sessionTypeId,sessionNumber,isTerm).switchIfEmpty(Mono.error(new  NotFoundException(null)));
	}	
	
	@PostMapping(value = "/getObeProgrammeOutcomeTypes")
	public Flux<SelectDTO> getObeProgrammeOutcomeTypes() {
		return commonCurriculumHandler.getObeProgrammeOutcomeTypes().switchIfEmpty(Mono.error(new  NotFoundException(null)));
	}
	
	@PostMapping(value = "/getAcaSessionGroup")
	public Flux<AcaSessionGroupDTO> getAcaSessionGroup(@RequestParam(required = false) String sessionTypeId) {
		return commonCurriculumHandler.getAcaSessionGroup(sessionTypeId).switchIfEmpty(Mono.error(new  NotFoundException(null)));
	}

	@PostMapping(value = "/getAccredationType")
	public Flux<SelectDTO> getAccreditationType() {
		return commonCurriculumHandler.getAccreditationType().switchIfEmpty(Mono.error(new  NotFoundException(null)));
	}
	
	@PostMapping(value = "/getProgrammeLevel")
	public Flux<SelectDTO> getProgrammeLevel(){
		return commonCurriculumHandler.getProgrammeLevel().switchIfEmpty(Mono.error(new  NotFoundException(null)));	
	}
	
	@PostMapping(value = "/getAcaSessionByGroup")
	public Flux<SelectDTO> getAcaSessionByGroup(@RequestParam String sessionGroupId) {
		return commonCurriculumHandler.getAcaSessionByGroup(sessionGroupId).switchIfEmpty(Mono.error(new  NotFoundException(null)));
	}
	
	@PostMapping(value = "/getDepartmentByCampusId")
	public Flux<SelectDTO> getDepartmentByCampusId(@RequestParam String campusId) {
		return commonCurriculumHandler.getDepartmentByCampusId(campusId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value ="/getAttActivity")
	public Flux<SelectDTO> getAttActivity() {
		return commonCurriculumHandler.getAttActivity().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/getAcademicYearFromDuration")
	public Flux<ErpAcademicYearDTO> getAcademicYearFromDuration() {
		return commonCurriculumHandler.getAcademicYearFromDuration().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value ="/getAcaCourse")
	public Flux<AcaCourseDTO> getAcaCourse(@RequestParam String academicYearId, @RequestParam String departmentId, @RequestParam (required = false) String levelId, @RequestParam String sessionGroupId, @RequestParam String campusId) {
		return commonCurriculumHandler.getAcaCourse(academicYearId, departmentId, levelId, sessionGroupId, campusId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value ="/getAcaClass")
	public Flux<SelectDTO> getAcaClass(@RequestParam String academicYearId, @RequestParam(required = false) String courseId, @RequestParam (required = false) String levelId, @RequestParam String sessionGroupId, @RequestParam String campusId) {
		return commonCurriculumHandler.getAcaClass(academicYearId, levelId, sessionGroupId, campusId, courseId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/getProgrammeByCampus")
    public Flux<SelectDTO> getProgrammeByCampus(@RequestParam int campusId, @RequestParam int yearId) {
    	 return commonCurriculumHandler.getProgrammeByCampus(campusId,yearId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getBatchYear")
    public Flux<SelectDTO> getBatchYear() {
    	 return commonCurriculumHandler.getBatchYear().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getAcademicYearFromDurationAndSession")
	public Flux<ErpAcademicYearDTO> getAcademicYearFromDurationAndSession(@RequestParam String typeId) {
		return commonCurriculumHandler.getAcademicYearFromDurationAndSession(typeId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/getProgrammeLevelByCampus")
	public Flux<SelectDTO> getProgrammeLevelByCampus(@RequestParam String yearId, @RequestParam String campusId, @RequestParam String typeId){
		return commonCurriculumHandler.getProgrammeLevelByCampus(yearId,campusId,typeId).switchIfEmpty(Mono.error(new NotFoundException(null)));	
	}
}