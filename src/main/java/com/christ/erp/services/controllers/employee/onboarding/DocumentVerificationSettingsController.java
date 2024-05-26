package com.christ.erp.services.controllers.employee.onboarding;

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
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.onboarding.EmpDocumentChecklistMainDTO;
import com.christ.erp.services.handlers.employee.onboarding.DocumentVerificationSettingsHandler;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/OnBoarding/DocumentVerificationSettings")
@SuppressWarnings({"unchecked","rawtypes"})
public class DocumentVerificationSettingsController extends BaseApiController{
	DocumentVerificationSettingsHandler documentVerificationSettingsHandler = DocumentVerificationSettingsHandler.getInstance();

	@RequestMapping(value = "/getGridData", method = RequestMethod.POST)
    public Mono<ApiResult<List<EmpDocumentChecklistMainDTO>>> getGridData(){
        ApiResult<List<EmpDocumentChecklistMainDTO>> result = new ApiResult<List<EmpDocumentChecklistMainDTO>>();
        try {
        	List<EmpDocumentChecklistMainDTO> headingsData = documentVerificationSettingsHandler.getGridData();
        	if(!Utils.isNullOrEmpty(headingsData)) {
                result.success = true;
                result.dto = headingsData;
            }
        }catch (Exception e) {
        	result.success = false;
            result.dto = null;
            result.failureMessage = e.getMessage();
		}
        return Utils.monoFromObject(result);
    }
	
	@RequestMapping( value = "/saveOrUpdate", method = RequestMethod.POST)
    public Mono<ApiResult> saveOrUpdate(@RequestBody EmpDocumentChecklistMainDTO headingData, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) throws Exception{
    	ApiResult result = new ApiResult();
		if(!Utils.isNullOrEmpty(headingData)) {
			try {
				String failureMessage = documentVerificationSettingsHandler.saveOrUpdate(headingData, userId);
				if(Utils.isNullOrEmpty(failureMessage)) {
					result.success = true;
				}else {
					result.failureMessage = failureMessage;
				}
			}catch (Exception e) {
				result.success = false;
				result.dto = null;
				result.failureMessage = e.getMessage();
			}
		}
		return Utils.monoFromObject(result);
    }
	
	@RequestMapping( value = "/edit", method = RequestMethod.POST )
    public Mono<ApiResult<EmpDocumentChecklistMainDTO>> edit(@RequestParam("headingId") String headingId){
        ApiResult<EmpDocumentChecklistMainDTO> result = new ApiResult<>();
        try{
        	EmpDocumentChecklistMainDTO checklistData = documentVerificationSettingsHandler.edit(headingId);
        	if(!Utils.isNullOrEmpty(checklistData)) {
        		result.success = true;
        		result.dto = checklistData;
        	}
        }catch (Exception e) {
        	result.success = false;
            result.dto = null;
            result.failureMessage = e.getMessage();
		}
        return Utils.monoFromObject(result);
    }
	
	@RequestMapping( value = "/delete", method = RequestMethod.POST)
    public Mono<ApiResult<ModelBaseDTO>> deleteHeadingData(@RequestParam("headingId") String headingId, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
    	ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		if(!Utils.isNullOrEmpty(headingId) && !Utils.isNullOrWhitespace(headingId)) {
			try {
				result.success = documentVerificationSettingsHandler.delete(headingId,userId);
			}catch (Exception e) {
				result.success = false;
				result.dto = null;
				result.failureMessage = e.getMessage();
			}
		}
		return Utils.monoFromObject(result);
    }
}
