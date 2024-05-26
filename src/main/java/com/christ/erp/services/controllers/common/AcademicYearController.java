package com.christ.erp.services.controllers.common;

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
import com.christ.erp.services.dto.common.AcademicYearDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.handlers.common.AcademicYearHandler;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Common/AcademicYear")
@SuppressWarnings("unchecked")
public class AcademicYearController extends BaseApiController {

	AcademicYearHandler academicYearHandler = AcademicYearHandler.getInstance();

    @RequestMapping(value="/getGridData", method=RequestMethod.POST)
    public Mono<ApiResult<List<AcademicYearDTO>>> getGridData()  {
    	ApiResult<List<AcademicYearDTO>> result = new ApiResult<>();
        try {
        	List<AcademicYearDTO> academicYearData = academicYearHandler.getGridData();
            if(!Utils.isNullOrEmpty(academicYearData)) {
                result.success = true;
                result.dto = academicYearData;
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
	public Mono<ApiResult<ModelBaseDTO>> saveAcademicYear(@RequestBody AcademicYearDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
        try {
            ApiResult<ModelBaseDTO> to = academicYearHandler.saveOrUpdate(data, userId);
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
	public Mono<ApiResult<AcademicYearDTO>> selectAcademicYear(@RequestParam("id") String id) {
		ApiResult<AcademicYearDTO> result = new ApiResult<AcademicYearDTO>();
		AcademicYearDTO academicYearDTO = academicYearHandler.selectAcademicYear(id);
		if(academicYearDTO.id!=null) {
			result.dto = academicYearDTO;
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
	public Mono<ApiResult> deleteAcademicYear(@RequestBody AcademicYearDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        ApiResult result = new ApiResult();
        try {
            result.success = academicYearHandler.deleteAcadmeicYear(data.id, userId);
        }
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }
}