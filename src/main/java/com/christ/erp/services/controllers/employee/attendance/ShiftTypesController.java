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
import com.christ.erp.services.dto.employee.attendance.EmpShiftTypesDTO;
import com.christ.erp.services.handlers.employee.attendance.ShiftTypesHandler;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/Attendance/ShiftTypes")
@SuppressWarnings("unchecked")
public class ShiftTypesController extends BaseApiController {

	ShiftTypesHandler shiftTypesHandler = ShiftTypesHandler.getInstance();

    @RequestMapping(value="/getGridData", method=RequestMethod.POST)
    public Mono<ApiResult<List<EmpShiftTypesDTO>>> getGridData()  {
    	ApiResult<List<EmpShiftTypesDTO>> result = new ApiResult<>();
        try {
        	List<EmpShiftTypesDTO> shiftTypesData = shiftTypesHandler.getGridData();
            if(!Utils.isNullOrEmpty(shiftTypesData)) {
                result.success = true;
                result.dto = shiftTypesData;
            }
            else {
                result.success = false;
            }
        }
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }
	    
	@RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveShiftTypes(@RequestBody EmpShiftTypesDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
        try {
            ApiResult<ModelBaseDTO> to = shiftTypesHandler.saveOrUpdate(data, userId);
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
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public Mono<ApiResult<EmpShiftTypesDTO>> selectShiftTypes(@RequestParam("id") String id) {
		ApiResult<EmpShiftTypesDTO> result = new ApiResult<EmpShiftTypesDTO>();
		EmpShiftTypesDTO empShiftTypesDTO = shiftTypesHandler.selectShiftTypes(id);
		if(empShiftTypesDTO.id!=null) {
			result.dto = empShiftTypesDTO;
			result.success = true;
		}
		else {
			result.dto = null;
			result.success = false;
		}
		return Utils.monoFromObject(result);
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Mono<ApiResult> deleteShiftTypes(@RequestBody EmpShiftTypesDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        ApiResult result = new ApiResult();
        try {
            result.success = shiftTypesHandler.deleteShiftTypes(data.id, userId);
        }
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }
}
