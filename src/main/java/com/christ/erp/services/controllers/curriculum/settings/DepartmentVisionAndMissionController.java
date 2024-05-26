package com.christ.erp.services.controllers.curriculum.settings;

import java.util.List;
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
import com.christ.erp.services.dto.curriculum.settings.DepartmentMissionVisionDTO;
import com.christ.erp.services.handlers.curriculum.settings.DepartmentVisionAndMissionHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Curriculum/Settings/DepartmentVisionAndMission")
public class DepartmentVisionAndMissionController extends BaseApiController {
	
	DepartmentVisionAndMissionHandler departmentVisionAndMissionHandler = DepartmentVisionAndMissionHandler.getInstance();

	@RequestMapping(value ="/getGridData", method = RequestMethod.POST)
	public Mono<ApiResult<List<DepartmentMissionVisionDTO>>> selectDepartmentMissionVision() {
		ApiResult<List<DepartmentMissionVisionDTO>>result = new ApiResult<List<DepartmentMissionVisionDTO>>();
			try {
				List<DepartmentMissionVisionDTO> list = departmentVisionAndMissionHandler.getGridData();
				if (!Utils.isNullOrEmpty(list)) {
					result.success = true;
					result.dto = list;
				} else {
					result.success = false;
				}
			} catch (Exception e) {
				result.success = false;
				result.dto = null;
				result.failureMessage = e.getMessage();
			}
		return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public Mono<ApiResult<DepartmentMissionVisionDTO>> edit(@RequestParam("id") String id, @RequestParam(required = false) Boolean isDepart) {
		ApiResult<DepartmentMissionVisionDTO> result = new ApiResult<>();
        try {
        	DepartmentMissionVisionDTO departmentMissionVisionDTO = departmentVisionAndMissionHandler.edit(id, isDepart);
        	if (!Utils.isNullOrEmpty(departmentMissionVisionDTO)) {
        		result.success = true;
				result.dto = departmentMissionVisionDTO;
			} else {
				result.success = false;
				result.dto = null;
			}
        } catch (Exception e) {
        	result.success = false;
			result.dto = null;
			result.failureMessage = e.getMessage();
		}
		return  Utils.monoFromObject(result);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Mono<ApiResult> delete(@RequestParam("id") String id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult result = new ApiResult();
		try {
			result.success = departmentVisionAndMissionHandler.delete(id,userId);
		} catch (Exception error) {
				result.success = false;
				result.dto = null;
				result.failureMessage = error.getMessage();
		}
		return  Utils.monoFromObject(result);
	}
	
	@RequestMapping(value="/saveOrUpdate", method=RequestMethod.POST)
    public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@RequestBody DepartmentMissionVisionDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId ) {
    	ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
        try {
        	ApiResult<ModelBaseDTO> to = departmentVisionAndMissionHandler.saveOrUpdate(data,userId);					
			if (to.failureMessage==null || to.failureMessage.isEmpty()) {
				result.success = true;
			} else {
				result.success = false;
		        result.failureMessage = to.failureMessage;
			}
		 } catch (Exception error) {
				result.success = false;
				result.dto = null;
				result.failureMessage = error.getMessage();
		 }
         return  Utils.monoFromObject(result);
    }

}
