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
import com.christ.erp.services.dto.account.BillOrReceiptNoDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.handlers.account.settings.BillOrReceiptNoHandler;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Accounts/BillOrReceipt")
public class BillOrReceiptNoController extends  BaseApiController {
	
	BillOrReceiptNoHandler billOrReceiptNumberHandler = BillOrReceiptNoHandler.getInstance();
	
	@RequestMapping(value="/getGridData", method=RequestMethod.POST)
    public Mono<ApiResult<List<BillOrReceiptNoDTO>>> getGridData()  {
    	ApiResult<List<BillOrReceiptNoDTO>> result = new ApiResult<>();
        try {
        	List<BillOrReceiptNoDTO> academicYearData = billOrReceiptNumberHandler.getGridData();
            if(!Utils.isNullOrEmpty(academicYearData)) {
                result.success = true;
                result.dto = academicYearData;
            }else {
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
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@RequestBody BillOrReceiptNoDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			ApiResult<ModelBaseDTO> result1 = new ApiResult<ModelBaseDTO>();
			result1=billOrReceiptNumberHandler.saveOrUpdate(data, userId);
			result.success=result1.success;
			result.failureMessage=result1.failureMessage;
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public Mono<ApiResult<BillOrReceiptNoDTO>> edit(@RequestParam("id") String id) {
		ApiResult<BillOrReceiptNoDTO> result = new ApiResult<BillOrReceiptNoDTO>();
		BillOrReceiptNoDTO menuScreendto;
		try {
			menuScreendto = billOrReceiptNumberHandler.edit(id);
			if(menuScreendto.id!=null) {
				result.dto = menuScreendto;
				result.success = true;
			}else {
				result.dto = null;
				result.success = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Utils.monoFromObject(result);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Mono<ApiResult> delete(@RequestBody BillOrReceiptNoDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        ApiResult result = new ApiResult();
		try {
			result.success = billOrReceiptNumberHandler.delete(data.id, userId);
		}catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
    }
}
