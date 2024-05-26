package com.christ.erp.services.controllers.admission.applicationprocess;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.admission.applicationprocess.OfflineApplicationIssueDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.admission.applicationprocess.OfflineApplicationIssueHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Mono;

@RestController

public class OfflineApplicationIssueController {
	
	private static final String secure = "/Secured/Admission/applicationprocess/OfflineApplicationIssue";
	private static final String protect = "/Protected/Admission/applicationprocess/OfflineApplicationIssue";
	
	@Autowired
	OfflineApplicationIssueHandler handler;
	
	@RequestMapping(value = protect + "/getCurrent", method = RequestMethod.POST)
	public Mono<ApiResult<OfflineApplicationIssueDTO>> getCurrent() {
		ApiResult<OfflineApplicationIssueDTO> current = new ApiResult<OfflineApplicationIssueDTO>();
		try {
			current = handler.getCurrent();
			if(!Utils.isNullOrEmpty(current)) {
				current.success = true;
			}
		} catch (Exception error) {
			current.success = false;
			current.dto = null;
			current.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(current);
	}
	
	@RequestMapping(value= secure + "/saveOrUpdate", method=RequestMethod.POST)
    public Mono<ApiResult<OfflineApplicationIssueDTO>> saveOrUpdate(@Valid @RequestBody OfflineApplicationIssueDTO offlineApplicationIssueDTO, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
    	ApiResult<OfflineApplicationIssueDTO> result = new ApiResult<OfflineApplicationIssueDTO>();
		try {
			result.success = handler.saveOrUpdate(offlineApplicationIssueDTO,result,userId);
			 if(result.success) {
				 result.success = true;
				 result.dto= offlineApplicationIssueDTO;
			 }else {
				 result.success = false;
				 result.dto = null;
			 }
		}catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
    }
	
	@RequestMapping(value = protect + "/getApplicantDetails", method = RequestMethod.POST)
	public Mono<ApiResult<OfflineApplicationIssueDTO>> getApplicantDetails(@RequestParam String applicationNumber,@RequestParam String academicYearID) {
		ApiResult<OfflineApplicationIssueDTO> result = new ApiResult<OfflineApplicationIssueDTO>();
		try {
			result = handler.getApplicantDetails(applicationNumber,academicYearID);			
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value = protect +"/getApplicantDetailsList", method = RequestMethod.POST)
	public Mono<ApiResult<List<OfflineApplicationIssueDTO>>> getApplicantDetailsList(@RequestParam String applicationNumber,@RequestParam String academicYearId) {
		ApiResult<List<OfflineApplicationIssueDTO>> result = new ApiResult<List<OfflineApplicationIssueDTO>>();
		try {
			result.dto = handler.getApplicantDetailsList(applicationNumber,academicYearId);
			if(result.dto.size()>0) {
				result.success = true;
			}
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value = protect +"/getDetailsByReceiptNo", method = RequestMethod.POST)
	public Mono<ApiResult<OfflineApplicationIssueDTO>> getDetailsByReceiptNo(@RequestParam String receiptNumber,@RequestParam String receiptDate,@RequestParam String financialYearId) {
		ApiResult<OfflineApplicationIssueDTO> result = new ApiResult<OfflineApplicationIssueDTO>();
		try {
			result = handler.getDetailsByReceiptNo(receiptNumber,receiptDate,financialYearId);			
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}
	
	 @RequestMapping(value = protect + "/getReceiptDatesByReceiptNumber",method = RequestMethod.POST)
	    public Mono<ApiResult<List<LookupItemDTO>>> getReceiptDatesByReceiptNumber(@RequestParam("receiptNumber") String receiptNumber) {
	        ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
	        try {
	        	handler.getReceiptDatesByReceiptNumber(result,receiptNumber);
	        }
	        catch(Exception error) {
	            result.dto = null;
	            result.failureMessage = error.getMessage();
	            result.success = false;
	        }
	        return Utils.monoFromObject(result);
	    }
	@PostMapping(value = protect + "/getAmountForOfflineApplication")
	public Mono<OfflineApplicationIssueDTO> getAmountForOfflineApplication(@RequestParam String offlineApplnNoPrefix, @RequestParam String offlineApplnNo, 
															@RequestParam String academicYear){
		return handler.getAmountForOfflineApplication(offlineApplnNoPrefix, offlineApplnNo, academicYear).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
}
