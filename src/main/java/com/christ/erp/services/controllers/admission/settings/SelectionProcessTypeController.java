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
import com.christ.erp.services.dto.admission.settings.AdmissionSelectionProcessTypeDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.handlers.admission.settings.SelectionProcessTypesHandler;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Admission/Settings/SelectionProcess")
public class SelectionProcessTypeController extends BaseApiController {
	SelectionProcessTypesHandler selectionProcessTypesHandler = SelectionProcessTypesHandler.getInstance();
	
	@RequestMapping(value="/getGridData", method=RequestMethod.POST)
    public Mono<ApiResult<List<AdmissionSelectionProcessTypeDTO>>> getGridData()  {
    	ApiResult<List<AdmissionSelectionProcessTypeDTO>> result = new ApiResult<>();
        try {
        	List<AdmissionSelectionProcessTypeDTO> selectionTypes = selectionProcessTypesHandler.getGridData();
            if(!Utils.isNullOrEmpty(selectionTypes)) {
                result.success = true;
                result.dto = selectionTypes;
            }
            else {
                result.success = false;
            }
        }
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = "Sorry! An exception occurred.";
        }
        return Utils.monoFromObject(result);
    }

//	@RequestMapping(value="/saveOrUpdate", method=RequestMethod.POST)
//    public Mono<ApiResult> saveOrUpdate(@RequestBody AdmissionSelectionProcessTypeDTO data) {
//        ApiResult result = new ApiResult();
//        try {
//            return getUserID().flatMap(userId -> {
//                try {
//                    result.success = selectionProcessTypesHandler.saveOrUpdateSelectionProcessType(data, userId);
//                }
//                catch (Exception error) {
//                    result.success = false;
//                    result.dto = null;
//                    result.failureMessage = error.getMessage();
//                }
//                return Utils.monoFromObject(result);
//            });
//        }
//        catch (Exception error) {
//            result.success = false;
//            result.dto = null;
//            result.failureMessage = error.getMessage();
//        }
//        return Utils.monoFromObject(result);
//    }
	
	@RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@RequestBody AdmissionSelectionProcessTypeDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			ApiResult<ModelBaseDTO> to = selectionProcessTypesHandler.saveOrUpdateSelectionProcessType(data,userId);
			if (to.failureMessage == null || to.failureMessage.isEmpty()) {
				result.success = true;
			} else {
				result.success = false;
				result.failureMessage = to.failureMessage;
			}
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = "Sorry! An exception occurred.";
		}
		return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public Mono<ApiResult<AdmissionSelectionProcessTypeDTO>> edit(@RequestParam("id") String id) {
		ApiResult<AdmissionSelectionProcessTypeDTO> result = new ApiResult<AdmissionSelectionProcessTypeDTO>();
		AdmissionSelectionProcessTypeDTO admissionSelectionProcessTypeDTO = selectionProcessTypesHandler.selectAdmissionSelectionProcessType(id);
		if (admissionSelectionProcessTypeDTO.id != null) {
			result.dto = admissionSelectionProcessTypeDTO;
			result.success = true;
		} else {
			result.dto = null;
			result.success = false;
		}
		return Utils.monoFromObject(result);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Mono<ApiResult> delete(@RequestBody AdmissionSelectionProcessTypeDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult result = new ApiResult();
		try {
			result.success = selectionProcessTypesHandler.deleteAdmissionSelectionProcessType(data.id, userId);
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = "Sorry! An exception occurred.";
		}
		return Utils.monoFromObject(result);
	}
	

}
