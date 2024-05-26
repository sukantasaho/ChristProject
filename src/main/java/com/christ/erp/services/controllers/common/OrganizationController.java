package com.christ.erp.services.controllers.common;

import com.christ.utility.lib.Constants;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.OrganizationsDTO;
import com.christ.erp.services.handlers.common.OrganizationHandler;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Common/Organization")
public class OrganizationController extends BaseApiController {
	
	OrganizationHandler organizationHandler= OrganizationHandler.getInstance();
	
	@RequestMapping(value = "/getData", method = RequestMethod.POST)
	public Mono<ApiResult<OrganizationsDTO>> selectOrganization() {
		ApiResult<OrganizationsDTO> result = new ApiResult<OrganizationsDTO>();
		OrganizationsDTO organizationDTO = organizationHandler.getOrganizationData();
		if (!Utils.isNullOrEmpty(organizationDTO)) {
			result.success = true;
			result.dto = organizationDTO;
		} else {
			result.success = false;
			result.dto = null;
		}
		return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> updateOrganization(@RequestBody OrganizationsDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			ApiResult<ModelBaseDTO> to = organizationHandler.updateOrganization(data, userId);
			if (to.failureMessage == null || to.failureMessage.isEmpty()) {
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
		return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value = "/getOrganizationMissionVision", method = RequestMethod.POST)
	public Mono<ApiResult<OrganizationsDTO>> getOrganizationMissionVision() {
		ApiResult<OrganizationsDTO> result = new ApiResult<OrganizationsDTO>();
		try {
			OrganizationsDTO organizationDTO = organizationHandler.getOrganizationMissionVision();
			if (!Utils.isNullOrEmpty(organizationDTO)) {
				result.success = true;
				result.dto = organizationDTO;
			} else {
				result.success = false;
				result.dto = null;
			}
		} catch (Exception error) {
				result.success = false;
				result.dto = null;
				result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}
}