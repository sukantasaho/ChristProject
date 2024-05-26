package com.christ.erp.services.controllers.employee.onboarding;
import javax.validation.Valid;

import com.christ.erp.services.common.Utils;
import com.christ.utility.lib.Constants;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.onboarding.EmpDocumentVerificationDTO;
import com.christ.erp.services.handlers.employee.onboarding.DocumentVerificationHandler;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/OnBoarding/DocumentVerification")
public class DocumentVerificationController extends BaseApiController {

	DocumentVerificationHandler documentVerificationHandler = DocumentVerificationHandler.getInstance();

	@RequestMapping(value="/saveOrUpdate", method=RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@Valid @RequestBody EmpDocumentVerificationDTO empDocumentVerificationDTO, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			if (empDocumentVerificationDTO != null) {
				result.success=documentVerificationHandler.saveOrUpdate(empDocumentVerificationDTO,userId);
			}
		}
		catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value="/editDocVerifiedbyEmployeeId", method=RequestMethod.POST)
	public Mono<ApiResult<EmpDocumentVerificationDTO>> editDocumentverified(@RequestParam("id") String id) {
		ApiResult<EmpDocumentVerificationDTO> result = new ApiResult<EmpDocumentVerificationDTO>();
		try {
			result.dto = documentVerificationHandler.editDocumentverified(id);
			result.success=true;
		}
		catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}


	@RequestMapping(value="/editDocumentVerifiedbyEmployeeId", method=RequestMethod.POST)
	public Mono<ApiResult<EmpDocumentVerificationDTO>> editDocumentVerifiedbyEmployeeId(@RequestParam("id") String id) {
		ApiResult<EmpDocumentVerificationDTO> result = new ApiResult<EmpDocumentVerificationDTO>();
		try {
			result.dto= documentVerificationHandler.editDocumentVerifiedbyEmployeeId(id);
			result.success=true;
		}
		catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value="/editDocVerifiedbyApplicationNumber", method=RequestMethod.POST)
	public Mono<ApiResult<EmpDocumentVerificationDTO>> editDocumentverifiedbyApplicationnumber(@RequestParam("id") String id)  {
		ApiResult<EmpDocumentVerificationDTO> result = new ApiResult<EmpDocumentVerificationDTO>();
		try {
			result.dto= documentVerificationHandler.editDocumentverifiedbyApplicationnumber(id);
			if(!Utils.isNullOrEmpty(result.dto)) {
				result.success=true;
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

