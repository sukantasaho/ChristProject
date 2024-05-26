package com.christ.erp.services.controllers.employee.appraisal;


import java.util.List;

import com.christ.utility.lib.Constants;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.appraisal.EmpAppraisalSessionDTO;
import com.christ.erp.services.handlers.employee.appraisal.AnnualAppraisalSessionHandler;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/Appraisal/AnnualAppraisalSession")
public class AnnualAppraisalSessionController extends BaseApiController {
	AnnualAppraisalSessionHandler annualAppraisalSessionHandler = AnnualAppraisalSessionHandler.getInstance();

	@RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@RequestBody EmpAppraisalSessionDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			ApiResult<ModelBaseDTO> to = annualAppraisalSessionHandler.saveOrUpdate(data,userId);
			if (to.failureMessage != null) {
				result.success = false;
				result.failureMessage = to.failureMessage;
			} else {
				result.success = true;
			}
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value ="/getGridData", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpAppraisalSessionDTO>>> getGridData() {	
		ApiResult<List<EmpAppraisalSessionDTO>> result = new ApiResult<List<EmpAppraisalSessionDTO>>();
		try {
			List<EmpAppraisalSessionDTO> gridData = annualAppraisalSessionHandler.getGridData();
			if(!Utils.isNullOrEmpty(gridData)) {
				result.success = true;
				result.dto = gridData;
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Mono<ApiResult> delete(@RequestBody EmpAppraisalSessionDTO data) {		
		ApiResult result = new ApiResult();
		try {
			if(annualAppraisalSessionHandler.delete(data)) {
				result.success = true;
				result.dto = null;
			}
			else 
				result.success = false;
		}
		catch (Exception e) {
			e.printStackTrace();
			result.success = false;
			result.dto = null;
			result.failureMessage = e.getMessage();
		}
		return Utils.monoFromObject(result);
	}
}
