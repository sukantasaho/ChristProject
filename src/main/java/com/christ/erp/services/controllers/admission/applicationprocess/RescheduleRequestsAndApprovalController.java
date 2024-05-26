package com.christ.erp.services.controllers.admission.applicationprocess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.admission.admissionprocess.SelectionProcessRescheduleRequestDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.admission.applicationprocess.RescheduleRequestsAndApprovalHandler;
import com.christ.utility.lib.Constants;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value ="/Secured/Admission/ApplicationProcess/RescheduleRequestsAndApproval")
public class RescheduleRequestsAndApprovalController {
	
	@Autowired
	private RescheduleRequestsAndApprovalHandler rescheduleRequestsAndApprovalHandler;
	
	@PostMapping(value = "/getApplicantNoList")
    public Flux<SelectDTO> getApplicantNoList(@RequestParam int applicationNo) {
    	 return rescheduleRequestsAndApprovalHandler.getApplicantNoList(applicationNo).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getApplicantNameList")
    public Flux<SelectDTO> getApplicantNameList(@RequestParam String applicantName) {
    	 return rescheduleRequestsAndApprovalHandler.getApplicantNameList(applicantName).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getApplicantDetails")
    public Mono<SelectionProcessRescheduleRequestDTO> getApplicantDetails(@RequestParam String studentApplnEntriesId) {
    	 return rescheduleRequestsAndApprovalHandler.getApplicantDetails(studentApplnEntriesId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getRescheduleDetails")
    public Mono<List<SelectionProcessRescheduleRequestDTO>> getRescheduleDetails(@RequestParam String studentApplnEntriesId) {
    	 return rescheduleRequestsAndApprovalHandler.getRescheduleDetails(studentApplnEntriesId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getSelectionProcessDates")
    public Mono<List<SelectionProcessRescheduleRequestDTO>> getSelectionProcessDates(@RequestParam String erpCampusProgrammeMappingId,@RequestParam String selectionProcessType) {
    	 return rescheduleRequestsAndApprovalHandler.getSelectionProcessDates(erpCampusProgrammeMappingId,selectionProcessType).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getSelectionProcessDatesBySelectionProcessPlanDetailsId")
    public Mono<List<SelectionProcessRescheduleRequestDTO>> getSelectionProcessDatesBySelectionProcessPlanDetailsId(@RequestParam String selectionProcessPlanDetailsId,@RequestParam String selectedVenueId) {
    	 return rescheduleRequestsAndApprovalHandler.getSelectionProcessDatesBySelectionProcessPlanDetailsId(selectionProcessPlanDetailsId,selectedVenueId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
    @SuppressWarnings("rawtypes")
	@PostMapping(value = "/saveRescheduleData")
	public Mono<ResponseEntity<ApiResult>> saveRescheduleData( @RequestBody Mono<SelectionProcessRescheduleRequestDTO> data,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId)  {
	    return rescheduleRequestsAndApprovalHandler.saveRescheduleData(data,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
    
	@PostMapping(value = "/approvalRescheduleDetails")
    public Mono<List<SelectionProcessRescheduleRequestDTO>> approvalRescheduleDetails() {
    	 return rescheduleRequestsAndApprovalHandler.approvalRescheduleDetails().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@SuppressWarnings("rawtypes")
    @PostMapping(value = "/saveApprovalDetails")
	public Mono<ResponseEntity<ApiResult>> saveApprovalDetails( @RequestBody Mono<List<SelectionProcessRescheduleRequestDTO>> data,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId)  {
		return rescheduleRequestsAndApprovalHandler.saveApprovalDetails(data,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

}
