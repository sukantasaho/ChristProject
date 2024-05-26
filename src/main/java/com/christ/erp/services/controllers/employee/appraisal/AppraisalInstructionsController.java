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
import com.christ.erp.services.dto.employee.appraisal.EmpAppraisalInstructionDTO;
import com.christ.erp.services.handlers.employee.appraisal.AppraisalInstructionsHandler;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/Appraisal/AppraisalInstructions")
public class AppraisalInstructionsController extends BaseApiController {	
	AppraisalInstructionsHandler appraisalInstructionsHandler = AppraisalInstructionsHandler.getInstance();
	
	@RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@RequestBody EmpAppraisalInstructionDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			ApiResult<ModelBaseDTO> to = appraisalInstructionsHandler.saveOrUpdate(data,userId);
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
	public Mono<ApiResult<List<EmpAppraisalInstructionDTO>>> getGridData() {	
		ApiResult<List<EmpAppraisalInstructionDTO>> result = new ApiResult<List<EmpAppraisalInstructionDTO>>();
		try {
			List<EmpAppraisalInstructionDTO> gridData = appraisalInstructionsHandler.getGridData();
			if(!Utils.isNullOrEmpty(gridData)) {
				result.success = true;
				result.dto = gridData;
			}
			else {
				result.success = false;
				result.failureMessage = "Data not Found";
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
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public Mono<ApiResult> edit(@RequestBody EmpAppraisalInstructionDTO data) {		
		ApiResult result = new ApiResult();
		try {
			EmpAppraisalInstructionDTO dto = appraisalInstructionsHandler.edit(data);
			if(!Utils.isNullOrEmpty(dto)) {
				result.success = true;
				result.dto = dto;
			}
			else {
				result.success = false;
				result.dto = null;
				result.failureMessage ="Error operation";
			}
		}catch (Exception e) {
			e.printStackTrace();
			result.success = false;
			result.dto = null;
			result.failureMessage = e.getMessage(); 
		}
		return Utils.monoFromObject(result);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Mono<ApiResult> delete(@RequestBody EmpAppraisalInstructionDTO data) {		
		ApiResult result = new ApiResult();
		try {
			if(appraisalInstructionsHandler.delete(data)) {
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
