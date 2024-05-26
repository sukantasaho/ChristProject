package com.christ.erp.services.controllers.account.fee;

import java.util.List;

import com.christ.utility.lib.Constants;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.account.fee.FeeCashCollectionDTO;
import com.christ.erp.services.dto.account.fee.FeeCashCollectionHeadDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.handlers.account.fee.CashCollectionHandler;

import reactor.core.publisher.Mono;
@RestController
@RequestMapping(value = "/Secured/Accounts/Fee/CashCollection")
public class CashCollectionController extends BaseApiController {
	CashCollectionHandler cashCollectionHandler = CashCollectionHandler.getInstance();
	
	@RequestMapping(value = "/getCurrent", method = RequestMethod.POST)
	public Mono<ApiResult<FeeCashCollectionDTO>> getCurrent() {
		ApiResult<FeeCashCollectionDTO> current = new ApiResult<FeeCashCollectionDTO>();
		try {
			current = cashCollectionHandler.getCurrent();
		} catch (Exception error) {
			current.success = false;
			current.dto = null;
			current.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(current);
	}
	
	@RequestMapping(value = "/getApplicantDetails", method = RequestMethod.POST)
	public Mono<ApiResult<FeeCashCollectionDTO>> getApplicantDetails(@RequestParam String registerNo) {
		ApiResult<FeeCashCollectionDTO> result = new ApiResult<FeeCashCollectionDTO>();
		try {
			result = cashCollectionHandler.getApplicantDetails(registerNo);			
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value = "/getDetailsByReceiptNo", method = RequestMethod.POST)
	public Mono<ApiResult<FeeCashCollectionDTO>> getDetailsByReceiptNo(@RequestParam String receiptNo,@RequestParam String finanacialYearId) {
		ApiResult<FeeCashCollectionDTO> result = new ApiResult<FeeCashCollectionDTO>();
		try {
			result = cashCollectionHandler.getDetailsByReceiptNo(receiptNo,finanacialYearId);			
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value = "/getApplicantDetailsList", method = RequestMethod.POST)
	public Mono<ApiResult<List<FeeCashCollectionDTO>>> getApplicantDetailsList(@RequestParam String registerNo,@RequestParam String finanacialYearId) {
		ApiResult<List<FeeCashCollectionDTO>> result = new ApiResult<List<FeeCashCollectionDTO>>();
		try {
			result.dto = cashCollectionHandler.getApplicantDetailsList(registerNo,finanacialYearId);
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
	
	@RequestMapping(value = "/getFeeHeadByCampusId", method = RequestMethod.POST)
	 	public Mono<ApiResult<List<LookupItemDTO>>> getFeeHeadByCampusId(@RequestParam("finanacialYearId") String finanacialYearId,@RequestParam String campusId) {
	 		ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
	 		try {			
	 			cashCollectionHandler.getFeeHeadByCampusId(result,campusId,finanacialYearId);			
	 		}
	 		catch(Exception error) {
	 			result.dto = null;
	 			result.failureMessage = error.getMessage();
	 			result.success = false;	            
	 		}
	 		return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value = "/getFeeHeadDetails", method = RequestMethod.POST)
 	public Mono<ApiResult<List<FeeCashCollectionHeadDTO>>> getFeeHeadDetails(
			@RequestParam("finanacialYearId") String finanacialYearId, @RequestParam("feeHeadId") String feeHeadId,@RequestParam("index") String index, @RequestBody FeeCashCollectionDTO data ) {
 		ApiResult<List<FeeCashCollectionHeadDTO>> result = new ApiResult<List<FeeCashCollectionHeadDTO>>();
 		try {			
 			List<FeeCashCollectionHeadDTO> result1 = cashCollectionHandler.getFeeHeadDetails(feeHeadId,finanacialYearId,index,data);	
 			if (!Utils.isNullOrEmpty(result1)) {
 				result.dto = result1;
 				result.success = true;
 			}else {
 				result.dto = result1;
 				result.success = false;
 				result.failureMessage = "Select Different head";
 			}
 		}
 		catch(Exception error) {
 			result.dto = null;
 			result.failureMessage = error.getMessage();
 			result.success = false;	            
 		}
 		return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
	public Mono<ApiResult<FeeCashCollectionDTO>> saveOrUpdate(@RequestBody FeeCashCollectionDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<FeeCashCollectionDTO> result = new ApiResult<FeeCashCollectionDTO>();
		try {
			ApiResult<FeeCashCollectionDTO> result1 = new ApiResult<FeeCashCollectionDTO>();
			result1=cashCollectionHandler.saveOrUpdate(data, userId);
			if(!Utils.isNullOrEmpty(result1)) {
				result.success=true;
				result.dto = result1.dto;
			}else {
				result.success=result1.success;
				result.failureMessage=result1.failureMessage;
			}
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}
}
