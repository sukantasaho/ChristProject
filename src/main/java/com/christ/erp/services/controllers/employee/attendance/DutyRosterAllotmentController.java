package com.christ.erp.services.controllers.employee.attendance;

import java.util.Map;

import com.christ.utility.lib.Constants;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.employee.attendance.DutyRosterAllotmentDTO;
import com.christ.erp.services.handlers.employee.attendance.DutyRosterAllotmentHandler;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value="/Secured/Employee/Attendance/DutyRosterAllotment")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class DutyRosterAllotmentController extends BaseApiController {
	
	DutyRosterAllotmentHandler dutyRosterAllotmentHandler = DutyRosterAllotmentHandler.getInstance();

	@RequestMapping(value="/getDutyRosterData", method=RequestMethod.POST)
    public Mono<ApiResult<DutyRosterAllotmentDTO>> getDutyRosterData(@RequestBody Map<String,String> data) {
        ApiResult<DutyRosterAllotmentDTO> result = new ApiResult<>();
        try {
        	DutyRosterAllotmentDTO rosterData = dutyRosterAllotmentHandler.getDutyRosterData(data);
            if(!Utils.isNullOrEmpty(rosterData)) {
                result.success = true;
                result.dto = rosterData;
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
	
	@RequestMapping(value="/saveOrUpdate", method=RequestMethod.POST)
    public Mono<ApiResult> saveOrUpdate(@RequestBody DutyRosterAllotmentDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        ApiResult result = new ApiResult();
        try {
            result.success = dutyRosterAllotmentHandler.saveOrUpdate(data, userId);
        }
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }
}
