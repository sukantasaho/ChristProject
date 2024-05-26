
package com.christ.erp.services.controllers.employee.recruitment;
import java.util.List;
import com.christ.utility.lib.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.onboarding.EmpApplnEntriesDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.employee.recruitment.InterviewProcessHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
//@RequestMapping(value = "/Secured/Employee/Recruitment/InterviewProcess")
public class InterviewProcessController extends BaseApiController{
	
	//InterviewProcessHandler interviewProcessHandler = InterviewProcessHandler.getInstance();

	@Autowired
	InterviewProcessHandler interviewProcessHandler1;

	@RequestMapping(value = "/Protected/Employee/Recruitment/InterviewProcess/getApplicant", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpApplnEntriesDTO>>> getApplicants(@RequestBody EmpApplnEntriesDTO empApplnEntriesDTO,String departmentId,String locationId){
		ApiResult<List<EmpApplnEntriesDTO>> result = new ApiResult<List<EmpApplnEntriesDTO>>();
		try {
			result.dto = interviewProcessHandler1.getApplicants(empApplnEntriesDTO,departmentId,locationId);
			if(!Utils.isNullOrEmpty(result.dto)) {
				result.success = true;
			}
		}catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/Secured/Employee/Recruitment/InterviewProcess/submitApplicationEntries", method = RequestMethod.POST)
	public Mono<ApiResult> submitApplicationEntries(@RequestBody List<EmpApplnEntriesDTO> applicationEntries,@RequestParam("stage") String stage, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId,@RequestParam String departmentId,@RequestParam String locationId){
		ApiResult result = new ApiResult();
		if(!Utils.isNullOrEmpty(applicationEntries)) {
			try {
				boolean isSaved = true;
				switch(stage) {
				case "applicationListing":
					isSaved = interviewProcessHandler1.applicantShortlisted(applicationEntries,userId,departmentId,locationId);
					break;
				case "shortlisted":
					isSaved = interviewProcessHandler1.submitInterviewScheduleDetailsStageOne(applicationEntries,userId);
					break;
				case "scheduleApprovalStageOne":
					isSaved = interviewProcessHandler1.submitInterviewScheduleApproval(applicationEntries,userId);
					break;
				case "scheduleStageTwo":
					isSaved = interviewProcessHandler1.submitInterviewScheduleDetailsStageTwo(applicationEntries,userId);
					break;
				}
				if(isSaved) {
					result.success = true;
				}else {
					result.success = false;
				}
			}catch (Exception e) {
				result.success = false;
				result.dto = null;
				result.failureMessage = e.getMessage();
			}
		}
		return Utils.monoFromObject(result);
	}

//	@RequestMapping(value = "/getInternalInterviewPanelists", method = RequestMethod.POST)
//	public Mono<ApiResult<List<LookupItemDTO>>> getInternalInterviewPanelists(@RequestParam("departmentId") String applicantId,@RequestParam("round") String round, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
//		ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
//		try {
//			interviewProcessHandler.getInternalInterviewPanelists(userId,result,applicantId,round);
//		}catch (Exception e) {
//			result.success = false;
//			result.dto = null;
//			result.failureMessage = e.getMessage();
//		}
//		return Utils.monoFromObject(result);
//	}
//
//	@RequestMapping(value = "/getExternalInterviewPanelists", method = RequestMethod.POST)
//	public Mono<ApiResult<List<LookupItemDTO>>> getExternalInterviewPanelists(String applicantId,String round, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
//		ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
//		try {
//			interviewProcessHandler.getExternalInterviewPanelists(userId,result,applicantId,round);
//		}catch (Exception e) {
//			result.success = false;
//			result.dto = null;
//			result.failureMessage = e.getMessage();
//		}
//		return Utils.monoFromObject(result);
//	}

	//	@RequestMapping(value = "/getHolidays", method = RequestMethod.POST)
	//   	public Mono<ApiResult<List<String>>> getHolidays(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
	//   		ApiResult<List<String>> result = new ApiResult<List<String>>();
	//		try {
	//			result.dto = interviewProcessHandler.getHolidays(userId);
	//			if(!Utils.isNullOrEmpty(result.dto)) {
	//				result.success = true;
	//			}
	//		}catch (Exception e) {
	//			result.success = false;
	//			result.dto = null;
	//			result.failureMessage = e.getMessage();
	//		}
	//		return Utils.monoFromObject(result);
	//   	}

	//	@RequestMapping(value = "/getCampusDepartments", method = RequestMethod.POST)
	//   	public Mono<ApiResult<List<LookupItemDTO>>> getCampusDepartments(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
	//   		ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
	//		try {
	//			interviewProcessHandler.getCampusDepartments(userId,result);
	//		}catch (Exception e) {
	//			result.success = false;
	//			result.dto = null;
	//			result.failureMessage = e.getMessage();
	//		}
	//		return Utils.monoFromObject(result);
	//   	}

	@PostMapping(value = "/Protected/Employee/Recruitment/InterviewProcess/getApplicationReceived")
	public Flux<EmpApplnEntriesDTO> getApplicationReceived(@RequestParam String departmentId,@RequestParam String locationId) throws Exception {
		return interviewProcessHandler1.getApplicationReceived(departmentId,locationId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}

	@PostMapping(value = "/Protected/Employee/Recruitment/InterviewProcess/getScheduleStageOne")
	public Flux<EmpApplnEntriesDTO> getScheduleStageOne(@RequestParam String departmentId,@RequestParam String processCode,@RequestParam String locationId) {
		return interviewProcessHandler1.getScheduleStageOne(departmentId,processCode,locationId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}

	@PostMapping(value = "/Protected/Employee/Recruitment/InterviewProcess/getSheduledForApprovalStageOne")
	public Flux<EmpApplnEntriesDTO> getSheduledForApprovalStageOne(@RequestParam String departmentId,@RequestParam String locationId) {
		return interviewProcessHandler1.getSheduledForApprovalStageOne(departmentId,locationId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}

	@PostMapping(value = "/Protected/Employee/Recruitment/InterviewProcess/getInterviewStatusStageOne")
	public Flux<EmpApplnEntriesDTO> getInterviewStatusStageOne(@RequestParam String departmentId,@RequestParam String processCode,@RequestParam String locationId) {
		return interviewProcessHandler1.getInterviewStatusStageOne(departmentId,processCode,locationId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}

	@PostMapping(value = "/Protected/Employee/Recruitment/InterviewProcess/getScheduleStageTwo")
	public Flux<EmpApplnEntriesDTO> getScheduleStageTwo(@RequestParam String departmentId,@RequestParam String processCode,@RequestParam String locationId) {
		return interviewProcessHandler1.getScheduleStageTwo(departmentId,processCode,locationId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}

	@PostMapping(value = "/Protected/Employee/Recruitment/InterviewProcess/getOfferStatus")
	public Flux<EmpApplnEntriesDTO> getOfferStatus(@RequestParam String departmentId,@RequestParam String processCode,@RequestParam String locationId) {
		return interviewProcessHandler1.getOfferStatus(departmentId,processCode,locationId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}

	@PostMapping(value = "/Protected/Employee/Recruitment/InterviewProcess/getCount")
	public Mono<EmpApplnEntriesDTO> getCount(@RequestParam String departmentId,@RequestParam String locationId) throws Exception {
		return interviewProcessHandler1.getCount(departmentId,locationId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/Secured/Employee/Recruitment/InterviewProcess/saveOrUpdateInterviewStatusStageOne")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdateInterviewStatusStageOne(@RequestBody Mono<EmpApplnEntriesDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId,@RequestParam boolean value) {
		return interviewProcessHandler1.saveOrUpdateInterviewStatusStageOne(data,userId,value).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value = "/Protected/Employee/Recruitment/InterviewProcess/getStageTwoInterviewStatus")
	public Mono<List<EmpApplnEntriesDTO>> getStageTwoInterviewStatus(@RequestParam String departmentId,@RequestParam String locationId,@RequestParam String filterStatus) {
		return interviewProcessHandler1.getStageTwoInterviewStatus(departmentId,locationId,filterStatus).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}

	@PostMapping(value = "/Protected/Employee/Recruitment/InterviewProcess/getStageThreeApplicants")
	public Mono<List<EmpApplnEntriesDTO>> getStageThreeApplicants(@RequestParam String departmentId,@RequestParam String locationId,@RequestParam String filterStatus) {
		return interviewProcessHandler1.getStageThreeApplicants(departmentId,locationId,filterStatus).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/Secured/Employee/Recruitment/InterviewProcess/submitStageThreeSchedule")
	public Mono<ResponseEntity<ApiResult>> submitStageThreeSchedule(@RequestBody Mono<List<EmpApplnEntriesDTO>> dto,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return interviewProcessHandler1.submitStageThreeSchedule(dto,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value = "/Protected/Employee/Recruitment/InterviewProcess/getStageThreeInterviewStatus")
	public Mono<List<EmpApplnEntriesDTO>> getStageThreeInterviewStatus(@RequestParam String departmentId,@RequestParam String locationId,@RequestParam String filterStatus) {
		return interviewProcessHandler1.getStageThreeInterviewStatus(departmentId,locationId,filterStatus).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}

	@PostMapping(value = "/Protected/Employee/Recruitment/InterviewProcess/getApplicantsInterviewScoreDetails")
	public Mono<EmpApplnEntriesDTO> getApplicantsInterviewScoreDetails(@RequestParam String empApplnEntriesId,@RequestParam String applicationNo) {
		return interviewProcessHandler1.getApplicantsInterviewScoreDetails(empApplnEntriesId,applicationNo).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/Secured/Employee/Recruitment/InterviewProcess/departmentEnable")
	public Mono<ResponseEntity<ApiResult>> departmentEnable() {
		return interviewProcessHandler1.departmentEnable().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value = "/Protected/Employee/Recruitment/InterviewProcess/getInternalPanel")
	public Flux<SelectDTO> getInternalPanel(@RequestParam String departmentId,@RequestParam String locationId) throws Exception {
		return interviewProcessHandler1.getInternalPanel(departmentId,locationId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}

	@PostMapping(value = "/Protected/Employee/Recruitment/InterviewProcess/getExternalPanel")
	public Flux<LookupItemDTO> getExternalPanel(@RequestParam String departmentId,@RequestParam String locationId) throws Exception {
		return interviewProcessHandler1.getExternalPanel(departmentId,locationId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/Secured/Employee/Recruitment/InterviewProcess/saveRemoveShortList")
	public Mono<ResponseEntity<ApiResult>> saveRemoveShortList(@RequestBody Mono<EmpApplnEntriesDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return interviewProcessHandler1.saveRemoveShortList(data,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@PostMapping(value = "/Protected/Employee/Recruitment/InterviewProcess/getPanelList")
	public Flux<SelectDTO> getPanelList() {
		return interviewProcessHandler1.getPanelList().switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}

}