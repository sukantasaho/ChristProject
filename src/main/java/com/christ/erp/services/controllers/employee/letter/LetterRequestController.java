package com.christ.erp.services.controllers.employee.letter;
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
import com.christ.erp.services.dto.employee.letter.LetterRequestDTO;
import com.christ.erp.services.helpers.employee.letter.LetterRequestHandler;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/Letter/LetterRequest")
public class LetterRequestController extends BaseApiController {
	LetterRequestHandler letterRequestHandler = LetterRequestHandler.getInstance();

	@RequestMapping(value = "/getGridData", method = RequestMethod.POST)
	public Mono<ApiResult<List<LetterRequestDTO>>> getGridData() {
		ApiResult<List<LetterRequestDTO>> result = new ApiResult<List<LetterRequestDTO>>();
		try {
			List<LetterRequestDTO> gridData = letterRequestHandler.getGridData();
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

	@RequestMapping(value="/saveOrUpdate", method=RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@RequestBody LetterRequestDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			ApiResult<ModelBaseDTO> to = letterRequestHandler.saveOrUpdate(data,userId,result);
			if(to.failureMessage==null || to.failureMessage.isEmpty()){
				result.success = true;
			}
			else{
				result.success = false;
				result.failureMessage = to.failureMessage;
			}
		}
		catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping( value = "/edit", method = RequestMethod.POST)
	public Mono<ApiResult<LetterRequestDTO>> edit(@RequestParam("id") String id) {
		ApiResult<LetterRequestDTO> result = new ApiResult<LetterRequestDTO>();
		try {
			LetterRequestDTO dto =letterRequestHandler.edit(id);
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

	@RequestMapping( value = "/delete", method = RequestMethod.POST)
	public Mono<ApiResult<LetterRequestDTO>> delete(@RequestParam("headingId") String headingId) {
		ApiResult<LetterRequestDTO> result = new ApiResult<LetterRequestDTO>();
		try {
			if(letterRequestHandler.delete(headingId)) {
				result.success = true;
			}			
		}
		catch (Exception e) {
			e.printStackTrace();
			result.success = false;
			result.failureMessage = e.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping( value = "/getLetterRequestHelpText", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> getLetterHelpText(@RequestBody LetterRequestDTO data) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			LetterRequestDTO dto =letterRequestHandler.getLetterRequestHelpText(data);
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