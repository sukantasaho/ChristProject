package com.christ.erp.services.controllers.employee.attendance;

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
import com.christ.erp.services.dto.employee.attendance.EmpDateForVacationPunchingDTO;
import com.christ.erp.services.handlers.employee.attendance.DatesForVacationPunchingHandler;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/attendance/dateForVacationPunching")
public class DatesForVacationPunchingController extends BaseApiController {
	DatesForVacationPunchingHandler datesForVacationPunchingHandler = DatesForVacationPunchingHandler.getInstance();	 
	@RequestMapping(value = "/getGridData", method = RequestMethod.POST)
	public Mono<ApiResult <List<EmpDateForVacationPunchingDTO>>> getGridData(){
		ApiResult< List<EmpDateForVacationPunchingDTO>>  result= new ApiResult< List<EmpDateForVacationPunchingDTO>> ();
        try {
                try {
                	result.dto = datesForVacationPunchingHandler.getGridData();
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
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value="/saveOrUpdate", method=RequestMethod.POST)
    public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@RequestBody EmpDateForVacationPunchingDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
	 ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
    try {
        result.success = datesForVacationPunchingHandler.saveOrUpdate(data, result, userId);
    }
    catch (Exception error) {
        result.success = false;
        result.dto = null;
        result.failureMessage = error.getMessage();
    }
    return Utils.monoFromObject(result);
    }

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public Mono<ApiResult<EmpDateForVacationPunchingDTO>> edit(@RequestParam("id") String id) {
		ApiResult<EmpDateForVacationPunchingDTO> result = new ApiResult<EmpDateForVacationPunchingDTO>();
		try {    
        	result.dto = datesForVacationPunchingHandler.edit(id);
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
             result.success = datesForVacationPunchingHandler.delete(id,userId );
         }
         catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
         }
         return Utils.monoFromObject(result);
	}
}
