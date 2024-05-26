package com.christ.erp.services.controllers.admission.settings;

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
import com.christ.erp.services.dto.admission.settings.AdmSelectionProcessVenueCityDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.handlers.admission.settings.CityOrVenueMasterHandler;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value="/Secured/Admission/Settings/CityOrVenueMaster")
public class CityOrVenueMasterController  extends BaseApiController {

	CityOrVenueMasterHandler handler=CityOrVenueMasterHandler.getInstance();

    @RequestMapping(value="/getGridData", method=RequestMethod.POST)
    public Mono<ApiResult<List<AdmSelectionProcessVenueCityDTO>>> getGridData()  {
    	ApiResult<List<AdmSelectionProcessVenueCityDTO>> result = new ApiResult<>();
        try {
        	List<AdmSelectionProcessVenueCityDTO> dtoList = handler.getGridData();
            if(!Utils.isNullOrEmpty(dtoList)) {
                result.success = true;
                result.dto = dtoList;
            }else {
                result.success = false;
            }
        }catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }
   
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Mono<ApiResult<AdmSelectionProcessVenueCityDTO>> delete(@RequestParam("id") String id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<AdmSelectionProcessVenueCityDTO> result = new ApiResult<AdmSelectionProcessVenueCityDTO>();
		try {
		 result.success = handler.delete(id,userId);
		}
		catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
	   return Utils.monoFromObject(result);
	}
   
   @RequestMapping(value="/saveOrUpdate", method=RequestMethod.POST)
   public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@RequestBody AdmSelectionProcessVenueCityDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
	   ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
	   try {
		   result.success = handler.saveOrUpdate(data, result, userId);
	   }
	   catch (Exception error) {
		   result.success = false;
		   result.dto = null;
		   result.failureMessage = error.getMessage();
	   }
	   return Utils.monoFromObject(result);
   }
   
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public Mono<ApiResult<AdmSelectionProcessVenueCityDTO>> edit(@RequestParam("id") String id) {
		ApiResult<AdmSelectionProcessVenueCityDTO> result = new ApiResult<AdmSelectionProcessVenueCityDTO>();
		try { 	
	       	result.dto = handler.edit(id);
	       	if(!Utils.isNullOrEmpty(result.dto)) {
	       		result.success = true;
	       	}
	       }
	       catch (Exception error) {
	           result.success = false;
	           result.dto = null;
	           result.failureMessage = error.getMessage();
	       }
		return Utils.monoFromObject(result);
	}
}
