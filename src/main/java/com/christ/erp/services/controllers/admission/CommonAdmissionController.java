package com.christ.erp.services.controllers.admission;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.admission.applicationprocess.AdmPrerequisiteExamDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmQualificationListDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmSelectionProcessPlanDetailDTO;
import com.christ.erp.services.dto.admission.settings.AdmPrerequisiteSettingsDetailsPeriodDTO;
import com.christ.erp.services.dto.admission.settings.ProgramPreferenceDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.admission.CommonAdmissionHandler;
import com.christ.utility.lib.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(value = "/Protected/Admission/CommonAdmissionController")
public class CommonAdmissionController extends BaseApiController{
	
//	CommonAdmissionHandler commonAdmissionHandler = CommonAdmissionHandler.getInstance();
	
	@Autowired
	private CommonAdmissionHandler commonAdmissionHandler;
	
	/* // apis are not using
//	@RequestMapping(value = "/getAccountHead",method = RequestMethod.POST) 
    public Mono<ApiResult<List<LookupItemDTO>>> getAccountHead(@RequestParam("offlineApplnNoPrefix") String offlineApplnNoPrefix,@RequestParam("offlineApplnNo") String offlineApplnNo,@RequestParam("academicYear") String academicYear) {
        ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
        try {
        	commonAdmissionHandler.getAccountHead(result,offlineApplnNoPrefix,offlineApplnNo,academicYear);
        }
        catch(Exception error) {
            result.dto = null;
            result.failureMessage = error.getMessage();
            result.success = false;
        }
        return Utils.monoFromObject(result);
    }
    
 //   @RequestMapping(value = "/getAmountByAccountHead",method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getAmountByAccountHead(@RequestParam("accountHeadId") String accountHeadId) {
        ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
        try {
        	commonAdmissionHandler.getAmountByAccountHead(result,accountHeadId);
        }
        catch(Exception error) {
            result.dto = null;
            result.failureMessage = error.getMessage();
            result.success = false;
        }
        return Utils.monoFromObject(result);
    }
      */
    @RequestMapping(value = "/getAccountName",method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getAccountName(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
        try {
            commonAdmissionHandler.getAccountName(result,userId);
        }
        catch(Exception error) {
            result.dto = null;
            result.failureMessage = error.getMessage();
            result.success = false;
        }
        return Utils.monoFromObject(result);
    }
    
    @RequestMapping(value = "/getQualitativeParameterLabel",method = RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getQualitativeParameterLabelList(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
        try {
            commonAdmissionHandler.getQualitativeParameterLabelList(result);
        }
        catch(Exception error) {
            result.dto = null;
            result.failureMessage = error.getMessage();
            result.success = false;
        }
        return Utils.monoFromObject(result);
    }
    
    @RequestMapping(value = "/getPrerequisiteExamMonthsYearByExamId", method = RequestMethod.POST)
	public Mono<ApiResult<List<AdmPrerequisiteSettingsDetailsPeriodDTO>>> getPrerequisiteExamMonthsYearByExamId(@RequestParam("erpCampusProgrammeMappingId") String erpCampusProgrammeMappingId, @RequestParam("examId") String examId
			, @RequestParam("erpAcademicYearId") String erpAcademicYearId) {
		ApiResult<List<AdmPrerequisiteSettingsDetailsPeriodDTO>> result = new ApiResult<>();
		try {
			if(!Utils.isNullOrEmpty(erpCampusProgrammeMappingId) && !Utils.isNullOrEmpty(examId) && !Utils.isNullOrEmpty(erpAcademicYearId)) {
				commonAdmissionHandler.getPrerequisiteExamMonthsYearByExamId(result, erpCampusProgrammeMappingId, examId, erpAcademicYearId);
			}
		}
		catch(Exception error) {
			result.dto = null;
			result.failureMessage = error.getMessage();
			result.success = false;
		}
		return Mono.justOrEmpty(result);
	}
    
    @PostMapping(value = "/getAdmPrerequisiteExam")
    //, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AdmPrerequisiteExamDTO> getAdmPrerequisiteExam(@RequestParam int acadadmicYearId,@RequestParam int programId) {
        return commonAdmissionHandler.getAdmPrerequisiteExam(acadadmicYearId, programId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
    @PostMapping(value = "/getAdmQualificationList")
    //, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AdmQualificationListDTO> getAdmQualificationList(@RequestParam int programId) {
        return commonAdmissionHandler.getAdmQualificationList(programId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
    @PostMapping(value = "/getAdmSelectionProcessPlanDetail")
    //, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AdmSelectionProcessPlanDetailDTO> getAdmSelectionProcessPlanDetail(@RequestParam int acadadmicYearId, @RequestParam List<Integer> campusProgramMapingId) {
        return commonAdmissionHandler.getAdmSelectionProcessPlanDetail(acadadmicYearId, campusProgramMapingId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
    @PostMapping(value = "/getAdmWeightageDefinitionWorkExperience")
    public Flux<LookupItemDTO> getAdmWeightageDefinitionWorkExperience() {
        return commonAdmissionHandler.getAdmWeightageDefinitionWorkExperience().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
    @PostMapping(value = "/getAdmQualificationDegreeList")
    public Flux<LookupItemDTO> getAdmQualificationDegreeList(@RequestParam(required = false) String admQualificationListId) {
        return commonAdmissionHandler.getAdmQualificationDegreeList(admQualificationListId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
    @PostMapping(value = "/getSelectionProcessDate")
    public Flux<LookupItemDTO> getSelectionProcessDate(@RequestParam int year,@RequestParam int degreeId, @RequestParam String erpCampusProgramId ) {
    	 return commonAdmissionHandler.getSelectionProcessDate(year,degreeId,erpCampusProgramId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
    
    @PostMapping(value = "/getSelectionProcessCenter")
    public Flux<LookupItemDTO> getSelectionProcessCenter(@RequestParam("date") String date) {
        return commonAdmissionHandler.getSelectionProcessCenter(date).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
    @PostMapping(value = "/getCampusProgrammeMapping")
    public Flux<LookupItemDTO> getCampusProgrammeMapping(@RequestParam int year,@RequestParam int degreeId) {
    	 return commonAdmissionHandler.getCampusProgrammeMapping(year,degreeId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value = "/getSelectionProcessTime")
    public Flux<LookupItemDTO> getSelectionProcessTime(@RequestParam String selectionProcessDate, @RequestParam int selectionProcessVenueId) {
    	 return commonAdmissionHandler.getSelectionProcessTime(selectionProcessDate,selectionProcessVenueId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
    @PostMapping(value = "/getQualification")
    public Flux<SelectDTO> getQualification() {
        return commonAdmissionHandler.getQualification().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value = "/getProgrammeBySelectionProcessSession")
    public Flux<ProgramPreferenceDTO> getProgrammeBySelectionProcessSession(@RequestParam String selectionProcessSession) {
        return commonAdmissionHandler.getProgrammeBySelectionProcessSession(selectionProcessSession).switchIfEmpty(Mono.error(new NotFoundException("Programme not found")));
    }
    
    @PostMapping(value = "/getUniversityOrBoard")
    public Flux<SelectDTO> getUniversityOrBoard() {
        return commonAdmissionHandler.getUniversityOrBoard().switchIfEmpty(Mono.error(new NotFoundException("Data not found")));
    }
    
	@PostMapping(value = "/getAdmissionCategory") 
    public Flux<SelectDTO> getAdmissionCategory() {
    	 return commonAdmissionHandler.getAdmissionCategory().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value = "/getSelectionProcessSession")
    public Flux<SelectDTO> getSelectionProcessSession(@RequestParam String academicYearId) {
        return commonAdmissionHandler.getSelectionProcessSession(academicYearId).switchIfEmpty(Mono.error(new NotFoundException("Session not found")));
    }

    @PostMapping(value = "/getSelectionProcessTypeBySession")
    public Flux<LookupItemDTO> getSelectionProcessTypeBySession(@RequestParam String selectionProcessSession) {
        return commonAdmissionHandler.getSelectionProcessTypeBySession(selectionProcessSession).switchIfEmpty(Mono.error(new NotFoundException("Selection Process Types not found")));
    }
    
	@PostMapping(value = "/getAccHeadsForApplicationFees") 
    public Flux<SelectDTO> getAccHeadsForApplicationFees() {
    	 return commonAdmissionHandler.getAccHeadsForApplicationFees().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    @PostMapping(value = "/getSessionForProgramme")
    public Flux<SelectDTO> getSessionForProgramme(@RequestParam Integer programmeId  ,@RequestParam String yearId) {
        return commonAdmissionHandler.getSessionForProgramme(programmeId,yearId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value = "/getProgrammeByYear")
    public Flux<SelectDTO> getProgrammeByYear(@RequestParam Integer yearId,@RequestParam Integer yearValue) {
        return commonAdmissionHandler.getProgrammeByYear(yearId,yearValue).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value = "/getLocationOrCampusByProgrammeAndYear")
    public Flux<SelectDTO> getLocationOrCampusByProgrammeAndYear(@RequestParam String yearValue, @RequestParam String programId, @RequestParam(required =false) Boolean isLocation) {
        return commonAdmissionHandler.getLocationOrCampusByProgrammeAndYear(yearValue, programId, isLocation).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value = "/getAdmissionType")
    public Flux<SelectDTO> getAdmissionType() {
        return commonAdmissionHandler.getAdmissionType().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value = "/getProgrammeMode")
    public Mono<List<SelectDTO>> getProgrammeMode(@RequestParam String yearValue, @RequestParam String programId) {
        return commonAdmissionHandler.getProgrammeMode(yearValue,programId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value = "/getProgrammeByYearAndIntakeAndType")
    public Flux<SelectDTO> getProgrammeByYearAndIntakeAndType(@RequestParam String yearId, @RequestParam List<Integer> intakeId, @RequestParam String admissionType) {
        return commonAdmissionHandler.getProgrammeByYearAndIntakeAndType(yearId,intakeId,admissionType).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value = "/getIntakeBatch")
    public Flux<SelectDTO> getIntakeBatch() {
        return commonAdmissionHandler.getIntakeBatch().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value = "/getProgrammeByPlan")
    public Flux<SelectDTO> getProgrammeByPlan(@RequestParam Integer planId) {
        return commonAdmissionHandler.getProgrammeByPlan(planId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value = "/getBoardType")
    public Mono<List<SelectDTO>> getBoardType() {
        return commonAdmissionHandler.getBoardType().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
}