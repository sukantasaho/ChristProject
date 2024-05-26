package com.christ.erp.services.controllers.hostel.settings;

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
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.hostel.settings.HostelApplicationDTO;
import com.christ.erp.services.handlers.hostel.settings.AdmissionFormHandler;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value="/Secured/Hostel/Settings/AdmissionForm")
public class AdmissionFormController extends BaseApiController {

	AdmissionFormHandler admissionFormHandler = AdmissionFormHandler.getInstance();
	
	@RequestMapping(value="/editHostelAdmissionOrApplicationData", method=RequestMethod.POST)
    public Mono<ApiResult<HostelApplicationDTO>> getHostelApplicationData(@RequestParam("acadamicYearId") String acadamicYearId, 
    		@RequestParam("hostelApplicationNum") String hostelApplicationNum, @RequestParam("registerNo") String registerNo,  @RequestParam("applicationNo") String applicationNo)  {
    	ApiResult<HostelApplicationDTO> result = new ApiResult<>();
        try {
        	HostelApplicationDTO hostelApplicationDTO = admissionFormHandler.getHostelApplicationData(acadamicYearId, hostelApplicationNum, registerNo, applicationNo,result);
            if(!Utils.isNullOrEmpty(hostelApplicationDTO)) {
                result.success = true;
                result.dto = hostelApplicationDTO;
            }else {
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
	public Mono<ApiResult<HostelApplicationDTO>> saveOrUpdate(@RequestBody HostelApplicationDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		ApiResult<HostelApplicationDTO> result = new ApiResult<HostelApplicationDTO>();
		try {
			HostelApplicationDTO dto = admissionFormHandler.saveOrUpdate(data, userId, result);
			if(!Utils.isNullOrEmpty(dto)) {
				result.dto = dto;
				result.success = true;
			}
		} catch (Exception e) {
			result.success = false;
			result.dto = null;
			result.failureMessage = e.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/cancelAdmissionForm", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> cancelAdmissionForm(@RequestBody HostelApplicationDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			admissionFormHandler.cancelAdmissionForm(data, userId, result);
		} catch (Exception e) {
			result.success = false;
			result.dto = null;
			result.failureMessage = e.getMessage();
		}
		return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value = "/printAdmissionForm", method = RequestMethod.POST)
	public Mono<ApiResult<HostelApplicationDTO>> printAdmissionForm(@RequestBody HostelApplicationDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		ApiResult<HostelApplicationDTO> result = new ApiResult<HostelApplicationDTO>();
		try {
			HostelApplicationDTO dto = admissionFormHandler.printAdmissionForm(data, userId, result);
			if(!Utils.isNullOrEmpty(dto)) {
				result.dto = dto;
				result.success = true;
			}
		} catch (Exception e) {
			result.success = false;
			result.dto = null;
			result.failureMessage = e.getMessage();
		}
		return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value = "/getHostelSeatsAvailableByRoomTypeId", method = RequestMethod.POST)
   	public Mono<ApiResult<LookupItemDTO>> getHostelSeatsAvailableDetatils(@RequestParam("academicYearId") String academicYearId,
   			@RequestParam("hostelId") String hostelId, @RequestParam("roomTypeId") String roomTypeId) {
   		ApiResult<LookupItemDTO> result = new ApiResult<>();
   		try {
   			LookupItemDTO lookupItemDTO = admissionFormHandler.getHostelSeatsAvailableDetatils(academicYearId,hostelId, roomTypeId);
   			if(!Utils.isNullOrEmpty(lookupItemDTO)) {
   				result.dto = lookupItemDTO;
   				result.success = true;
   			}
        }catch(Exception error) {
        	result.dto = null;
        	result.failureMessage = error.getMessage();
        	result.success = false;
        }
   		return Utils.monoFromObject(result);
   	}
}
