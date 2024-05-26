package com.christ.erp.services.controllers.account.settings;

import java.io.File;
import java.util.List;

import com.christ.utility.lib.Constants;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.account.settings.AccAccountsDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.handlers.account.settings.AccountEntryHandler;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Accounts/AccountEntry")
public class AccountEntryController extends BaseApiController {
	
	AccountEntryHandler accountEntryHandler = AccountEntryHandler.getInstance();
	
	@RequestMapping(value="/getGridData", method=RequestMethod.POST)
    public Mono<ApiResult<List<AccAccountsDTO>>> getGridData()  {
    	ApiResult<List<AccAccountsDTO>> result = new ApiResult<>();
        try {
        	List<AccAccountsDTO> AccAccountsDTOs  = accountEntryHandler.getGridData();
            if(!Utils.isNullOrEmpty(AccAccountsDTOs)) {
                result.success = true;
                result.dto = AccAccountsDTOs;
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
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@RequestBody AccAccountsDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			ApiResult<ModelBaseDTO> to = accountEntryHandler.saveOrUpdate(data, userId);
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
	
	@SuppressWarnings("rawtypes")
	@PostMapping("/uploadFile")
    public Mono<ApiResult> uploadFiles(@RequestPart("files") Flux<FilePart> data) {
		File directory = new File("ImageUpload");
		if(!directory.exists()) {
			directory.mkdir();
		}
		return Utils.uploadFiles(data,directory+"//",new String[]{"jpg","png","jpeg","pdf"});
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> delete(@RequestParam("id") String id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
		 result.success = accountEntryHandler.delete(id,userId);
		}
		catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
	   return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public Mono<ApiResult<AccAccountsDTO>> edit(@RequestParam("id") String id) {
		ApiResult<AccAccountsDTO> result = new ApiResult<AccAccountsDTO>();
    	 try { 
        	result.dto = accountEntryHandler.edit(id);
        	if(!Utils.isNullOrEmpty(result.dto)) {
        		result.success = true;
        	}
        }
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result); 
	}
}
