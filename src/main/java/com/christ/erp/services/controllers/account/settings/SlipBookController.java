package com.christ.erp.services.controllers.account.settings;

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
import com.christ.erp.services.dto.account.settings.AccSlipBookDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.handlers.account.settings.SlipBookHandler;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Account/Settings/SlipBook")
public class SlipBookController extends BaseApiController {

	SlipBookHandler slipBookHandler = SlipBookHandler.getInstance();

	@RequestMapping(value = "/getGridData", method = RequestMethod.POST)
	public Mono<ApiResult<List<AccSlipBookDTO>>> getGridData() {
		ApiResult<List<AccSlipBookDTO>> result = new ApiResult<>();
		try {
			List<AccSlipBookDTO> gridData = slipBookHandler.getGridData();
			if(!Utils.isNullOrEmpty(gridData)){
				result.success = true;
				result.dto = gridData;
			} else {
				result.success = false;
			}
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveAssgnApprvrs(@RequestBody AccSlipBookDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			if(data != null) {
				if(slipBookHandler.duplicateAccSlipBook(data)){
					result.failureMessage = "Duplicate record exist for Slip Book No.: "+ data.slipBookNo;
				}else {
				if(slipBookHandler.saveOrUpdate(data, userId)){
					result.success = true;
				}
			}
			}
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public Mono<ApiResult<AccSlipBookDTO>> getslipBookDetails(@RequestParam("ID") String id) {
		ApiResult<AccSlipBookDTO> result = new ApiResult<>();
		try {
			AccSlipBookDTO accSlipbookData = slipBookHandler.getaccSlipBookData(id);
			if(!Utils.isNullOrEmpty(accSlipbookData)) {
				result.success = true;
				result.dto = accSlipbookData;
			} else {
				result.success = false;
			}
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> delete(@RequestBody AccSlipBookDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			result.success = slipBookHandler.delete(data, userId);
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}
}
