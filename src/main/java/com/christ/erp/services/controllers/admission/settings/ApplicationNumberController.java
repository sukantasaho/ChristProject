package com.christ.erp.services.controllers.admission.settings;

import java.util.List;

import com.christ.utility.lib.Constants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.admission.settings.AdmApplnNumbergeneratonDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.handlers.admission.settings.ApplicationNumberHandler;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Admission/Settings/ApplicationNumber")
public class ApplicationNumberController extends BaseApiController{
	
	// ApplicationNumberHandler applicationNumberHandler = ApplicationNumberHandler.getInstance();
	@Autowired
	ApplicationNumberHandler applicationNumberHandler;
	
	@RequestMapping(value="/getGridData", method=RequestMethod.POST)
    public Mono<ApiResult<List<AdmApplnNumbergeneratonDTO>>> getGridData(@RequestParam("yearId") String yearId)  {
    	ApiResult<List<AdmApplnNumbergeneratonDTO>> result = new ApiResult<>();
        try {
        	List<AdmApplnNumbergeneratonDTO> numberGenerationData = applicationNumberHandler.getGridData(yearId);
            if(!Utils.isNullOrEmpty(numberGenerationData)) {
                result.success = true;
                result.dto = numberGenerationData;
            }
            else {
                result.success = false;
                result.failureMessage = "Data not found";
            }
        }
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = "Sorry! An exception occurred.";
        }
        return Utils.monoFromObject(result);
    }
	
	@RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveAcademicYear(@RequestBody AdmApplnNumbergeneratonDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
        try {
            ApiResult<ModelBaseDTO> to = applicationNumberHandler.saveOrUpdate(data, userId);
            if(to.failureMessage==null || to.failureMessage.isEmpty()){
                result.success = true;
            }
            else{
                result.success = false;
                result.failureMessage = to.failureMessage;
            }
        } catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = "Sorry! An exception occurred.";
        }
        return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public Mono<ApiResult<AdmApplnNumbergeneratonDTO>> selectApplicationNumber(@RequestParam("id") String id) {
		ApiResult<AdmApplnNumbergeneratonDTO> result = new ApiResult<AdmApplnNumbergeneratonDTO>();
		AdmApplnNumbergeneratonDTO admApplnNumbergeneratonDTO = applicationNumberHandler.edit(id);
		if(admApplnNumbergeneratonDTO.id!=null) {
			result.dto = admApplnNumbergeneratonDTO;
			result.success = true;
		}
		else {
			result.dto = null;
			result.success = false;
		}
		return Utils.monoFromObject(result);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Mono<ApiResult> delete(@RequestBody AdmApplnNumbergeneratonDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        ApiResult result = new ApiResult();
        try {
            result.success = applicationNumberHandler.delete(data.id, userId);
        }
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = "Sorry! An exception occurred.";
        }
        return Utils.monoFromObject(result);
    }
}
