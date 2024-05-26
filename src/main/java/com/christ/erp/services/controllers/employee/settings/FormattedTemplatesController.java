package com.christ.erp.services.controllers.employee.settings;
import com.christ.erp.services.common.ApiResult;
import java.util.List;

import com.christ.utility.lib.Constants;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.settings.ErpTemplateDTO;
import com.christ.erp.services.handlers.employee.settings.FormattedTemplatesHandler;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/Settings/FormattedTemplatesController")
public class FormattedTemplatesController extends BaseApiController {
	
	FormattedTemplatesHandler formattedTemplatesHandler = FormattedTemplatesHandler.getInstance();

    @RequestMapping(value="/getGridData", method=RequestMethod.POST)
    public Mono<ApiResult<List<ErpTemplateDTO>>> getGridData()  {
    	ApiResult<List<ErpTemplateDTO>> result = new ApiResult<>();
        try {
        	List<ErpTemplateDTO> academicYearData = formattedTemplatesHandler.getGridData();
            if(!Utils.isNullOrEmpty(academicYearData)) {
                result.success = true;
                result.dto = academicYearData;
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
    
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveFormattedTemplate(@RequestBody ErpTemplateDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
        try {
            ApiResult<ModelBaseDTO> to = formattedTemplatesHandler.saveOrUpdate(data, userId);
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
	public Mono<ApiResult<ErpTemplateDTO>> selectFormattedTemplates(@RequestParam("id") String id) {
		ApiResult<ErpTemplateDTO> result = new ApiResult<ErpTemplateDTO>();
		ErpTemplateDTO erpTemplateDTO = formattedTemplatesHandler.selectFormattedTemplates(id);
		if(erpTemplateDTO.id!=null) {
			result.dto = erpTemplateDTO;
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
	public Mono<ApiResult> deleteerpTemplate(@RequestBody ErpTemplateDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        ApiResult result = new ApiResult();
        try {
            result.success = formattedTemplatesHandler.deleteErpTemplate(data.id, userId);
        }
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }
		
}
