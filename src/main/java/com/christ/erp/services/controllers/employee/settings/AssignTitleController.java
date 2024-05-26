package com.christ.erp.services.controllers.employee.settings;

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
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.settings.ErpAssignTitleDTO;
import com.christ.erp.services.handlers.employee.settings.AssignTitleHandler;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/Settings/AssignTitleController")
public class AssignTitleController extends BaseApiController{

	@Autowired
	AssignTitleHandler assignTitleHandler;
	
	AssignTitleHandler handler =AssignTitleHandler.getInstance();
	@RequestMapping(value ="/getGridData", method = RequestMethod.POST)
	public Mono<ApiResult<List<ErpAssignTitleDTO>>> getGridData(){
		ApiResult< List<ErpAssignTitleDTO>>  result= new ApiResult< List<ErpAssignTitleDTO>> ();	
        try {
        	result.dto = assignTitleHandler.getGridData();
            if(!Utils.isNullOrEmpty(result.dto)) 
            	result.success = true;  
         } catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value="/saveOrUpdate", method=RequestMethod.POST)
    public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@RequestBody ErpAssignTitleDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
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
	public Mono<ApiResult<ErpAssignTitleDTO>> edit(@RequestParam("id") String userBOId) {
		ApiResult<ErpAssignTitleDTO> result = new ApiResult<ErpAssignTitleDTO>();
    	 try { 
        	result.dto = handler.edit(userBOId);
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
	public Mono<ApiResult<ModelBaseDTO>> delete(@RequestParam("id") String userBoId, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
	     ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
         try {
             result.success = handler.delete(userBoId,userId );
         }
         catch (Exception error) {
             result.success = false;
             result.dto = null;
             result.failureMessage = error.getMessage();
         }
         return Utils.monoFromObject(result);
	}
}
