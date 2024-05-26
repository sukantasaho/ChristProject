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
import com.christ.erp.services.dto.admission.settings.AdmQualitativeParamterDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.handlers.admission.settings.SelectionProcessScoreCareQualitativeParamtersHandler;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Admission/SelectionProcessScoreCareQualitativeParamters")
@SuppressWarnings("unchecked")
public class SelectionProcessScoreCareQualitativeParamtersController extends BaseApiController {

	SelectionProcessScoreCareQualitativeParamtersHandler selectionProcessScoreCareQualitativeParamtersHandler = SelectionProcessScoreCareQualitativeParamtersHandler.getInstance();

    @RequestMapping(value="/getGridData", method=RequestMethod.POST)
    public Mono<ApiResult<List<AdmQualitativeParamterDTO>>> getGridData()  {
    	ApiResult<List<AdmQualitativeParamterDTO>> result = new ApiResult<>();
        try {
        	List<AdmQualitativeParamterDTO> admQualitativeParamterData = selectionProcessScoreCareQualitativeParamtersHandler.getGridData();
            if(!Utils.isNullOrEmpty(admQualitativeParamterData)) {
                result.success = true;
                result.dto = admQualitativeParamterData;
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
	public Mono<ApiResult<ModelBaseDTO>> saveAcademicYear(@RequestBody AdmQualitativeParamterDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
        try {
            ApiResult<ModelBaseDTO> to = selectionProcessScoreCareQualitativeParamtersHandler.saveOrUpdate(data, userId);
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
	public Mono<ApiResult<AdmQualitativeParamterDTO>> selectAdmQualitativeParamter(@RequestParam("id") String id) {
		ApiResult<AdmQualitativeParamterDTO> result = new ApiResult<AdmQualitativeParamterDTO>();
		AdmQualitativeParamterDTO admQualitativeParamterDTO = selectionProcessScoreCareQualitativeParamtersHandler.selectAdmQualitativeParamter(id);
		if(admQualitativeParamterDTO.id!=null) {
			result.dto = admQualitativeParamterDTO;
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
	public Mono<ApiResult> deleteAcademicYear(@RequestBody AdmQualitativeParamterDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        ApiResult result = new ApiResult();
        try {
            result.success = selectionProcessScoreCareQualitativeParamtersHandler.deleteAdmQualitativeParamter(data.id, userId);
        }
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }
}
