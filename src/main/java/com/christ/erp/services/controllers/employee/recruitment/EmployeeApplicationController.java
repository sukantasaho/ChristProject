package com.christ.erp.services.controllers.employee.recruitment;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.christ.erp.services.dto.employee.recruitment.EmployeeApplicantDTO;
import com.christ.erp.services.handlers.employee.recruitment.EmployeeApplicationHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Protected/Employee/Recruitment/EmployeeApplicationControler")
@SuppressWarnings({"rawtypes"})
public class EmployeeApplicationController extends BaseApiController{
	  // @Autowired 
	   //EmployeeApplicationHandler employeeApplicationHandler1 ;
	   
	   @Autowired 
	   EmployeeApplicationHandler employeeApplicationHandler ;
	   
	//EmployeeApplicationHandler employeeApplicationHandler = EmployeeApplicationHandler.getInstance();
    
	@RequestMapping( value = "/submitEmployeeApplication", method = RequestMethod.POST)
    public Mono<ApiResult> submitEmployeeApplication(@RequestBody EmployeeApplicantDTO employeeApplicantDTO, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) throws Exception{
		ApiResult result = new ApiResult();
		if(!Utils.isNullOrEmpty(employeeApplicantDTO)) {
			try {
				String failureMessage = "";
				employeeApplicationHandler.submitEmployeeApplication(employeeApplicantDTO, Integer.parseInt(userId), failureMessage, employeeApplicantDTO.saveMode, result);
				result.dto = employeeApplicantDTO;
			}catch (Exception e) {
				result.success = false;
				result.dto = null;
				result.failureMessage = e.getMessage();
			}
		}
		return Utils.monoFromObject(result);
    }
	
	@RequestMapping( value = "/getEmployeeApplicationStatus", method = RequestMethod.POST)
	public Mono<ApiResult> getEmployeeApplicationStatus(@RequestParam("empApplicationRegistrationId") String empApplicationRegistrationId) throws Exception{
		ApiResult result = new ApiResult();
    	try {
			employeeApplicationHandler.getEmployeeApplicationStatus(empApplicationRegistrationId,result);
			/*if(!Utils.isNullOrEmpty(result.dto)) {
				result.success = true;
			}*/
    	} catch (Exception e) { 
    		result.success = false;
            result.dto = null;
            result.failureMessage = e.getMessage();
    	}
    	return Utils.monoFromObject(result);
    }
	
	@PostMapping("/educationalDetailsFilesUpload")
    public Mono<ApiResult> educationalDetailsFilesUpload( @RequestPart("files") Flux<FilePart> data) throws Exception {
		File directory = new File("educational");
		if(!directory.exists()) {
			directory.mkdir();
		}
		return employeeApplicationHandler.uploadFiles(data, directory+"\\", new String[] {"jpeg","png"},true,"educational");
	}
	
	@PostMapping("/professionalExperienceFilesUpload")
    public Mono<ApiResult> professionalExperienceFilesUpload(@RequestPart("files") Flux<FilePart> data) throws Exception {
		File directory = new File("professional");
		if(!directory.exists()) {
			directory.mkdir();
		}
		return employeeApplicationHandler.uploadFiles(data, directory+"\\", new String[] {"jpeg","png"},true,"professional");
	}
	
	@PostMapping("/employeeApplicantPhotoUpload")	
    public Mono<ApiResult> employeeApplicantPhotoUpload(@RequestPart("files") Flux<FilePart> data) throws Exception {
		File directory = new File("imageUpload");
		if(!directory.exists()) {
			directory.mkdir();
		}
		return employeeApplicationHandler.uploadFiles(data, directory+"\\", new String[] {"jpeg","png"},true,"imageUpload");
	}
	
	@RequestMapping( value = "/getEmployeeApplication", method = RequestMethod.POST)
    public Mono<ApiResult> getEmployeeApplication(@RequestParam("empApplnEntriesId") Integer empApplnEntriesId) throws Exception{
		ApiResult result = new ApiResult();
    	try {
			if(!Utils.isNullOrEmpty(empApplnEntriesId)) {
				EmployeeApplicantDTO employeeApplicantDTO = employeeApplicationHandler.getEmployeeApplication(empApplnEntriesId);
				if(!Utils.isNullOrEmpty(employeeApplicantDTO)) {
					result.success = true;
					result.dto = employeeApplicantDTO;
				}
			}
    	} catch (Exception e) { 
    		result.success = false;
            result.dto = null;
            result.failureMessage = e.getMessage();
    	}
    	return Utils.monoFromObject(result);
    }

	@RequestMapping( value = "/getEmployeeApplicationPreview", method = RequestMethod.POST)
	public Mono<ApiResult> getEmployeeApplicationPreview(@RequestParam("applicationNo") Integer applicationNo) throws Exception{
		ApiResult result = new ApiResult();
		try {
			if(!Utils.isNullOrEmpty(applicationNo)) {
				EmployeeApplicantDTO employeeApplicantDTO = employeeApplicationHandler.getEmployeeApplicationPreview(String.valueOf(applicationNo));
				if(!Utils.isNullOrEmpty(employeeApplicantDTO)) {
					result.success = true;
					result.dto = employeeApplicantDTO;
				}
			}
		} catch (Exception e) {
			result.success = false;
			result.dto = null;
			result.failureMessage = e.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping( value = "/getEmployeeApplicationInterviewDetails", method = RequestMethod.POST)
	public Mono<ApiResult> getEmployeeApplicationInterviewDetails(@RequestParam("empApplnEntriesId") Integer empApplnEntriesId, @RequestParam("interviewRound") String interviewRound) throws Exception{
		ApiResult result = new ApiResult();
		try {
			if(!Utils.isNullOrEmpty(empApplnEntriesId)) {
				EmployeeApplicantDTO employeeApplicantDTO = employeeApplicationHandler.getEmployeeApplicationInterviewDetails(empApplnEntriesId,interviewRound);
				if(!Utils.isNullOrEmpty(employeeApplicantDTO)) {
					result.success = true;
					result.dto = employeeApplicantDTO;
				}
			}
		} catch (Exception e) {
			result.success = false;
			result.dto = null;
			result.failureMessage = e.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping( value = "/submitEmployeeApplicationInterviewDetails", method = RequestMethod.POST)
	public Mono<ApiResult> submitEmployeeApplicationInterviewDetails(@RequestBody EmployeeApplicantDTO employeeApplicantDTO, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) throws Exception{
		ApiResult result = new ApiResult();
		if(!Utils.isNullOrEmpty(employeeApplicantDTO)) {
			try {
				employeeApplicationHandler.submitEmployeeApplicationInterviewDetails(employeeApplicantDTO, Integer.parseInt(userId), result);
				if(!Utils.isNullOrEmpty(result.failureMessage)){
					result.dto = employeeApplicantDTO;
				}
			}catch (Exception e) {
				result.success = false;
				result.dto = null;
				result.failureMessage = e.getMessage();
			}
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping( value = "/getApplicantDetailsFromPrevApplication", method = RequestMethod.POST)
	public Mono<ApiResult> getApplicantDetailsFromPrevApplication(@RequestParam("empApplicationRegistrationId") String empApplicationRegistrationId) throws Exception{
		ApiResult result = new ApiResult();
		try {
			employeeApplicationHandler.getApplicantDetailsFromPrevApplication(empApplicationRegistrationId,result);
		} catch (Exception e) {
			result.success = false;
			result.dto = null;
			result.failureMessage = e.getMessage();
		}
		return Utils.monoFromObject(result);
	}
}