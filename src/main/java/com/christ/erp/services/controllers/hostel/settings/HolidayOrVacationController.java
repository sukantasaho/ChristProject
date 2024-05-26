package com.christ.erp.services.controllers.hostel.settings;

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
import com.christ.erp.services.dto.hostel.settings.HostelHolidayEventsDTO;
import com.christ.erp.services.handlers.hostel.settings.HolidayOrVacationHandler;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value="/Secured/Hostel/Settings/HolidayOrVacation")
public class HolidayOrVacationController extends BaseApiController {
	
	HolidayOrVacationHandler holidayOrVacationHandler = HolidayOrVacationHandler.getInstance();
	
	@RequestMapping(value = "/getGridData", method = RequestMethod.POST)
    public Mono<ApiResult<List<HostelHolidayEventsDTO>>> getGridData(){
        ApiResult<List<HostelHolidayEventsDTO>> result = new ApiResult<>();
        try{
            List<HostelHolidayEventsDTO> hostelMasterData = holidayOrVacationHandler.getGridData();
            if(!Utils.isNullOrEmpty(hostelMasterData)){
                result.success = true;
                result.dto = hostelMasterData;
            }else{
                result.success = false;
            }
        }catch (Exception error){
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }
	
	@RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@RequestBody HostelHolidayEventsDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			result.success = holidayOrVacationHandler.saveOrUpdate(data, userId, result);
		} catch (Exception e) {
			result.success = false;
			result.dto = null;
			result.failureMessage = e.getMessage();
		}
		return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public Mono<ApiResult<HostelHolidayEventsDTO>> edit(@RequestParam("id") String id) {
		ApiResult<HostelHolidayEventsDTO> result = new ApiResult<HostelHolidayEventsDTO>();
    	 try { 
        	result.dto = holidayOrVacationHandler.edit(id);
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
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> delete(@RequestParam("id") String id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
		 result.success = holidayOrVacationHandler.delete(id,userId);
		}
		catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
	   return Utils.monoFromObject(result);
	}
}
