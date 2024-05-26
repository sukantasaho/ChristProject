package com.christ.erp.services.controllers.employee.settings;
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
import com.christ.erp.services.dto.employee.attendance.LetterTemplatesDTO;
import com.christ.erp.services.handlers.employee.settings.LetterTemplateHandler;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/Settings/LetterTemplate")
public class LetterTemplateController extends BaseApiController {
	LetterTemplateHandler letterTemplateHandler = LetterTemplateHandler.getInstance();
	
	@RequestMapping(value = "/getGridData", method = RequestMethod.POST)
	public Mono<ApiResult<List<LetterTemplatesDTO>>> getGridData() {
        ApiResult<List<LetterTemplatesDTO>> result = new ApiResult<List<LetterTemplatesDTO>>();
        try {
           List<LetterTemplatesDTO> gridData = letterTemplateHandler.getGridData();
           if(!Utils.isNullOrEmpty(gridData)) {
               result.success = true;
               result.dto = gridData;
           }
           else {
               result.success = false;
               result.setFailureMessage("Data not found");
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
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@RequestBody LetterTemplatesDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			if(letterTemplateHandler.saveOrUpdate(data,userId)) {
				result.success = true;
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
	
	 @RequestMapping( value = "/delete", method = RequestMethod.POST)
	 public Mono<ApiResult<ModelBaseDTO>> delete(@RequestParam("headingId") String headingId) {
		 ApiResult result = new ApiResult();
	        try {
	            if(letterTemplateHandler.delete(headingId)) {
	                result.success = true;
	            }
	            else {
	                result.success = false;
	            }
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	            result.success = false;
	            result.failureMessage = e.getMessage();
	        }
	        return Utils.monoFromObject(result);
	}
	 
	 @RequestMapping( value = "/edit", method = RequestMethod.POST)
	 public Mono<ApiResult<LetterTemplatesDTO>> edit(@RequestParam("id") String id) {
		 ApiResult<LetterTemplatesDTO> result = new ApiResult<LetterTemplatesDTO>();
	        try {
	        	LetterTemplatesDTO dto =letterTemplateHandler.edit(id);
	            if(dto!=null) {
	                result.dto = dto;
	                result.success = true;
	            }
	            else {
	                result.success = false;
	            }
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	            result.success = false;
	            result.failureMessage = e.getMessage();
	        }
	        return Utils.monoFromObject(result);
	 } 

}
