package com.christ.erp.services.controllers.employee.recruitment;

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
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.recruitment.AppointmentApprovalDTO;
import com.christ.erp.services.handlers.employee.recruitment.AppointmentApprovalHandler;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/Recruitment/AppointmentApproval")
public class AppointmentApprovalController extends BaseApiController {
	//AppointmentApprovalHandler appointmentApprovalHandler = AppointmentApprovalHandler.getInstance();
	@Autowired
	AppointmentApprovalHandler appointmentApprovalHandler;
	
	@RequestMapping(value = "/getApplicationEntiesDetails", method = RequestMethod.POST)
	public Mono<ApiResult<AppointmentApprovalDTO>> getApplicationEntiesDetails(@RequestParam("applicationNumber") String applicationNumber,
			@RequestParam("applicantName") String applicantName){
		ApiResult<AppointmentApprovalDTO> result = new ApiResult<AppointmentApprovalDTO>();
        try {
        	result.dto = appointmentApprovalHandler.getApplicationEntriesDetails(applicationNumber, applicantName);
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
	
    @RequestMapping(value="/saveOrUpdate", method=RequestMethod.POST)
    public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@RequestBody AppointmentApprovalDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) throws Exception {
    	ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
        try {
            result.success = appointmentApprovalHandler.saveOrUpdate(data, userId, result);
        }catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }
}
