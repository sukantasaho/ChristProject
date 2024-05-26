package com.christ.erp.services.controllers.employee.leave;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveTypeDTO;
import com.christ.erp.services.handlers.employee.leave.LeaveTypeHandler;
import com.christ.utility.lib.Constants;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/Secured/Employee/Leave/LeaveType")
public class LeaveTypeController extends BaseApiController {
	
	LeaveTypeHandler leaveTypeHandler = LeaveTypeHandler.getInstance();
    
    @RequestMapping(value="/saveOrUpdate", method=RequestMethod.POST)
    public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@Valid @RequestBody EmpLeaveTypeDTO leaveTypeMapping, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
    	ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
        try {
            if (leaveTypeMapping != null) {
                if(leaveTypeHandler.duplicateCheckLeaveType(leaveTypeMapping)) {
                    result.failureMessage = "Duplicate record exist with the same Leave type or Leave type code.";
                }
                else {
                    if(leaveTypeHandler.saveOrUpdate(leaveTypeMapping,userId)) {
                        result.success = true;
                    }
                }
            }
        }
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }
    
    @RequestMapping(value="/getGridData", method=RequestMethod.POST)
    public Mono<ApiResult<List<EmpLeaveTypeDTO>>> getGridData()  {
    	ApiResult<List<EmpLeaveTypeDTO>> result = new ApiResult<List<EmpLeaveTypeDTO>>();
        try {
        	result.dto = leaveTypeHandler.getGridData();
            if(!Utils.isNullOrEmpty(result.dto)) {
            	result.success = true;   
            } else {
            	result.success = false;
                result.dto = null;
                result.failureMessage = "Data not found";
            }
        }
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }
    
    @RequestMapping(value="/edit", method=RequestMethod.POST)
    public Mono<ApiResult<EmpLeaveTypeDTO>> selectLeavetype(@RequestParam("ID") int id) {
    	ApiResult<EmpLeaveTypeDTO> result = new ApiResult<EmpLeaveTypeDTO>();
        try {
        	result.dto = leaveTypeHandler.edit(id);
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
    public Mono<ApiResult<ModelBaseDTO>> delete(@RequestBody EmpLeaveTypeDTO leaveTypeMapping, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
    	ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
        try {
            result.success = leaveTypeHandler.delete(leaveTypeMapping,userId );
        }
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }
}

