package com.christ.erp.services.controllers.employee.recruitment;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.christ.erp.services.dto.employee.profile.*;
import com.christ.erp.services.dto.employee.recruitment.EmpProfileComponentMapDTO;
import com.christ.erp.services.handlers.common.CommonApiHandler;
import com.christ.utility.lib.caching.CacheUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.EmpEmploymentTabDTO;
import com.christ.erp.services.dto.employee.EmpPersonalDataTabDTO;
import com.christ.erp.services.dto.employee.EmpProfileGridDTO;
import com.christ.erp.services.dto.employee.EmpProfileSidePanelDTO;
import com.christ.erp.services.dto.employee.common.EmpDTO;
import com.christ.erp.services.dto.employee.recruitment.EmployeeProfileDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.employee.recruitment.EmployeeProfileHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/Recruitment/EmployeeProfile")
public class EmployeeProfileController {
	//EmployeeProfileHandler employeeProfileHandler = EmployeeProfileHandler.getInstance();
	@Autowired
	EmployeeProfileHandler employeeProfileHandler;

	@Autowired
	private CommonApiHandler commonApiHandler;
	
	@RequestMapping(value = "/searchEmployees", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpDTO>>> getApplicants(@RequestBody EmpDTO empDTO){
		ApiResult<List<EmpDTO>> result = new ApiResult<List<EmpDTO>>();
        try {
        	result.dto = employeeProfileHandler.searchEmployees(empDTO);
            if(!Utils.isNullOrEmpty(result.dto)) {
            	result.success = true;
            }
        }catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value = "/getEmployeeProfileDetails", method = RequestMethod.POST)
	public Mono<ApiResult<EmployeeProfileDTO>> getEmployeeProfileDetails(@RequestParam("empId") String  empId){
		ApiResult<EmployeeProfileDTO> result = new ApiResult<EmployeeProfileDTO>();
        try {
        	result.dto = employeeProfileHandler.getEmployeeProfileDetails(empId);
            if(!Utils.isNullOrEmpty(result.dto)) {
            	result.success = true;
            }
        }catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/submitEmployeeProfile", method = RequestMethod.POST)
	public Mono<ApiResult> submitEmployeeProfile(@RequestBody EmployeeProfileDTO employeeProfile,@RequestParam("tab") String tab,@RequestParam("boxName") String box, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		ApiResult result = new ApiResult();
		if(!Utils.isNullOrEmpty(employeeProfile)) {
			try {
				boolean saved = false;
				switch(tab) {
				case "personalDetails":
					saved = employeeProfileHandler.savePersonalDetails(employeeProfile,userId);
					break;
				case "jobDetails":
					saved = employeeProfileHandler.saveJobDetails(employeeProfile,userId);
					break;
				case "salaryAndLeaveDetails":
					saved = employeeProfileHandler.saveSalaryAndLeaveDetails(employeeProfile,userId);
					break;
				case "educationAndExperienceDetails":
					saved = employeeProfileHandler.saveEducationAndExperienceDetails(employeeProfile,userId);
					break;
				}
				result.success = saved;
			}catch (Exception e) {
				result.success = false;
				result.dto = null;
				result.failureMessage = e.getMessage();
			}
		}
		return Utils.monoFromObject(result);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/createEmployee", method = RequestMethod.POST)
	public Mono<ApiResult> createEmployee(@RequestBody EmpDTO emp, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		ApiResult result = new ApiResult();
		if(!Utils.isNullOrEmpty(emp)) {
			try {
				result.dto = employeeProfileHandler.createEmployee(emp,userId);
				result.success = true;
			}catch (Exception e) {
				result.success = false;
				result.dto = null;
				result.failureMessage = e.getMessage();
			}
		}
		return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value = "/getApplicantDetails", method = RequestMethod.POST)
	public Mono<ApiResult<EmpDTO>> getApplicantDetails(
			@RequestParam("applicationNo") String applicationNo) {
		ApiResult<EmpDTO> result = new ApiResult<EmpDTO>();
		try {
			EmpDTO dto = employeeProfileHandler.getApplicantDetails(applicationNo);
			if (!Utils.isNullOrEmpty(dto)) {
				result.success = true;
				result.dto = dto;
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
	
	@SuppressWarnings("rawtypes")
	@PostMapping("/filesUpload")
    public Mono<ApiResult> educationalDetailsFilesUpload( @RequestPart("filesUpload") Flux<FilePart> data) throws Exception {
		File directory = new File("Employee-Profile");
		if(!directory.exists()) {
			directory.mkdir();
		}
		return employeeProfileHandler.uploadFiles(data, directory+"\\", new String[] {"jpeg","png"},true,"educational");
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/duplicateCheckEmpNo")
	public Mono<ResponseEntity<ApiResult>> duplicateCheckEmpNo(@RequestParam String  empNO){
		return employeeProfileHandler.duplicateCheckEmpNo(empNO).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping("/uploadFiles")
    public Mono<ApiResult> uploadFiles(@RequestPart("files") Flux<FilePart> data) {
		File directory = new File("ImageUpload");
		if(!directory.exists()) {
			directory.mkdir();
		}
		return Utils.uploadFiles(data,directory+"//",new String[]{"jpeg","png"});
	}
	
	@PostMapping("/getEmployeeList")
	public Flux<SelectDTO> getEmployeeList(@RequestParam String campusId ){
		return  employeeProfileHandler.getEmployeeList(campusId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping("/getEmpProfileGridData")
	public Flux<EmpProfileGridDTO> getEmpProfileGridData(){
		return  employeeProfileHandler.getEmpProfileGridData().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	@PostMapping("/getPersonalDataTabDetails")
	public Mono<EmpPersonalDataTabDTO> getPersonalDataTabDetails(@RequestParam Integer empId, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId, @RequestHeader(Constants.QUERY_JWT_CAMPUS_IDS) String userCampusIds){
		Mono<EmpPersonalDataTabDTO> dataMono = employeeProfileHandler.getEmplProfilePersonalDataTabDTO(empId, userId, userCampusIds);
		if (dataMono != null) {
			return dataMono.flatMap(data -> {
			    if (!Utils.isNullOrEmpty(data)) {
			        return Mono.just(data);
			    } else {
			        return Mono.error(new NotFoundException(null));
			    }
			}).switchIfEmpty(Mono.error(new NotFoundException(null)));
		}else {
			 return Mono.error(new NotFoundException(null));
		}
	}
	
	@PostMapping(value = "/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdatePersonalDataTab(@RequestBody Mono<EmpPersonalDataTabDTO> empPersonalDataTabDTO, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return employeeProfileHandler.saveOrUpdatePersonalDataTab(empPersonalDataTabDTO, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	@PostMapping("/getSidePanelDetails")
	public Mono<EmpProfileSidePanelDTO> getSidePanelDetails(@RequestParam int empId ){
		Mono<EmpProfileSidePanelDTO> dataMono = employeeProfileHandler.getEmpProfileSidePanel(empId);
		if (dataMono != null) {
			return dataMono.flatMap(data -> {
			    if (!Utils.isNullOrEmpty(data)) {
			        return Mono.just(data);
			    } else {
			        return Mono.error(new NotFoundException(null));
			    }
			}).switchIfEmpty(Mono.error(new NotFoundException(null)));
		}else {
			 return Mono.error(new NotFoundException(null));
		}
	}
	@PostMapping("/getEmploymentDetails")
	public Mono<EmpEmploymentTabDTO> getEmploymentDetails(@RequestParam Integer empId, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId, @RequestHeader(Constants.QUERY_JWT_CAMPUS_IDS) String userCampusIds){
		Mono<EmpEmploymentTabDTO> dataMono = employeeProfileHandler.getEmploymentDetails(empId, userId, userCampusIds);
		if (dataMono != null) {
			return dataMono.flatMap(data -> {
			    if (!Utils.isNullOrEmpty(data)) {
			        return Mono.just(data);
			    } else {
			        return Mono.error(new NotFoundException(null));
			    }
			}).switchIfEmpty(Mono.error(new NotFoundException(null)));
		}else {
			 return Mono.error(new NotFoundException(null));
		}
	}
	@PostMapping(value = "/saveOrUpdateEmployment")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdateEmployment(@RequestBody Mono<EmpEmploymentTabDTO> empEmploymentTabDTO, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId, @RequestHeader(Constants.QUERY_JWT_CAMPUS_IDS) String userCampusIds) {
		return employeeProfileHandler.saveOrUpdateEmployment(empEmploymentTabDTO, userId, userCampusIds).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	@PostMapping(value = "/empProfileEditEnable")
	public Mono<ResponseEntity<ApiResult>> empProfileEditEnable() {
		return commonApiHandler.privilegeEnable().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	@PostMapping(value = "/empProfileAadharAndPanEnable")
	public Mono<ResponseEntity<ApiResult>> empProfileAadharEnable() {
		return commonApiHandler.privilegeEnable().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	@PostMapping(value = "/empProfileAddressEnable")
	public Mono<ResponseEntity<ApiResult>> empProfileAddressEnable() {
		return commonApiHandler.privilegeEnable().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	@PostMapping(value = "/empProfilePassportAndVisaEnable")
	public Mono<ResponseEntity<ApiResult>> empProfileVisaEnable() {
		return commonApiHandler.privilegeEnable().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	@PostMapping(value = "/empProfileFamilyAndBackgroundEnable")
	public Mono<ResponseEntity<ApiResult>> empProfileFamilyBackgroundEnable() {
		return commonApiHandler.privilegeEnable().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	@PostMapping(value = "/empProfileOfficeRemarksEnable")
	public Mono<ResponseEntity<ApiResult>> empProfileOfficeRemarksEnable() {
		return commonApiHandler.privilegeEnable().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	@PostMapping(value = "/getQualificationDetails")
	public Mono<EmpQualificationTabDTO> getQualificationDetails(@RequestParam Integer empId){
		Mono<EmpQualificationTabDTO> dataMono = employeeProfileHandler.getEducationDetails(empId);
		if (dataMono != null) {
			return dataMono.flatMap(data -> {
				if (!Utils.isNullOrEmpty(data)) {
					return Mono.just(data);
				} else {
					return Mono.error(new NotFoundException(null));
				}
			}).switchIfEmpty(Mono.error(new NotFoundException(null)));
		}else {
			return Mono.error(new NotFoundException(null));
		}
	}
	@PostMapping(value = "/saveOrUpdateQualificationDetails")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdateQualificationDetails(@RequestBody Mono<EmpQualificationTabDTO> empQualificationTabDTO, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return employeeProfileHandler.saveOrUpdateQualificationDetails(empQualificationTabDTO, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	@PostMapping(value = "/getSalaryDetails")
	public Mono<EmpSalaryTabDTO> getSalaryDetails(@RequestParam Integer empId){
		Mono<EmpSalaryTabDTO> dataMono = employeeProfileHandler.getSalaryDetails(empId);
		if (dataMono != null) {
			return dataMono.flatMap(data -> {
				if (!Utils.isNullOrEmpty(data)) {
					return Mono.just(data);
				} else {
					return Mono.error(new NotFoundException(null));
				}
			}).switchIfEmpty(Mono.error(new NotFoundException(null)));
		}else {
			return Mono.error(new NotFoundException(null));
		}
	}
	@PostMapping(value = "/getPayScaleDetails")
	public Mono<List<PayScaleDetailsDTO>> getPayScaleDetails(@RequestParam Integer cellId){
		Mono<List<PayScaleDetailsDTO>> dataMono = employeeProfileHandler.getPayScaleDetails(cellId);
		if (dataMono != null) {
			return dataMono.flatMap(data -> {
				if (!Utils.isNullOrEmpty(data)) {
					return Mono.just(data);
				} else {
					return Mono.error(new NotFoundException(null));
				}
			}).switchIfEmpty(Mono.error(new NotFoundException(null)));
		}else {
			return Mono.error(new NotFoundException(null));
		}
	}
	@PostMapping(value = "/saveOrUpdateSalaryDetails")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdateSalaryDetails(@RequestBody Mono<EmpSalaryTabDTO> salaryTabDTO, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId, @RequestHeader(Constants.QUERY_JWT_CAMPUS_IDS) String userCampusIds) {
		return employeeProfileHandler.saveOrUpdateSalaryDetails(salaryTabDTO, userId, userCampusIds).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	@PostMapping(value = "/myProfileEditEnable")
	public Mono<ResponseEntity<ApiResult>> myProfileEditEnable() {
		return commonApiHandler.privilegeEnable().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	@PostMapping(value = "/currentJobDetailsEnable")
	public Mono<ResponseEntity<ApiResult>> currentJobDetailsEnable() {
		return commonApiHandler.privilegeEnable().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	@PostMapping(value = "/workspaceDirectoryEnable")
	public Mono<ResponseEntity<ApiResult>> workspaceDirectoryEnable() {
		return commonApiHandler.privilegeEnable().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	@PostMapping(value = "/letterDetailsEnable")
	public Mono<ResponseEntity<ApiResult>> appointmentDetailsEnable() {
		return commonApiHandler.privilegeEnable().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	@PostMapping(value = "/workTimeDetailsEnable")
	public Mono<ResponseEntity<ApiResult>> workTimeDetailsEnable() {
		return commonApiHandler.privilegeEnable().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	@PostMapping(value = "/approverDetailsEnable")
	public Mono<ResponseEntity<ApiResult>> approverDetailsEnable() {
		return commonApiHandler.privilegeEnable().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	@PostMapping(value = "/resignationDetailsEnable")
	public Mono<ResponseEntity<ApiResult>> resignationDetailsEnable() {
		return commonApiHandler.privilegeEnable().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	@PostMapping(value = "/remarksEnable")
	public Mono<ResponseEntity<ApiResult>> remarksEnable() {
		return commonApiHandler.privilegeEnable().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	@PostMapping(value = "/guestContractDetailsEnable")
	public Mono<ResponseEntity<ApiResult>> guestContractDetailsEnable() {
		return commonApiHandler.privilegeEnable().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	@PostMapping(value = "/employmentHistoryEnable")
	public Mono<ResponseEntity<ApiResult>> employmentHistoryEnable() {
		return commonApiHandler.privilegeEnable().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	@PostMapping(value = "/getExperienceDetails")
	public Mono<EmpWorkExperienceTabDTO> getExperienceDetails(@RequestParam Integer empId){
		Mono<EmpWorkExperienceTabDTO> dataMono = employeeProfileHandler.getExperienceDetails(empId);
		if (dataMono != null) {
			return dataMono.flatMap(data -> {
				if (!Utils.isNullOrEmpty(data)) {
					return Mono.just(data);
				} else {
					return Mono.error(new NotFoundException(null));
				}
			}).switchIfEmpty(Mono.error(new NotFoundException(null)));
		}else {
			return Mono.error(new NotFoundException(null));
		}
	}
	@PostMapping(value = "/saveOrUpdateExperienceDetails")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdateExperienceDetails(@RequestBody Mono<EmpWorkExperienceTabDTO> experienceTabDTO, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId, @RequestHeader(Constants.QUERY_JWT_CAMPUS_IDS) String userCampusIds) {
		return employeeProfileHandler.saveOrUpdateExperience(experienceTabDTO, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	@PostMapping(value = "/getEmpApplicantDetails")
	public Mono<EmpProfileCreateEmployeeDTO> getApplicantDetails(@RequestParam Integer applicationNo) {
		Mono<EmpProfileCreateEmployeeDTO> dataMono = employeeProfileHandler.getApplicantDetails(applicationNo);
		if (dataMono != null) {
			return dataMono.flatMap(data -> {
				if (!Utils.isNullOrEmpty(data)) {
					return Mono.just(data);
				} else {
					return Mono.error(new NotFoundException(null));
				}
			}).switchIfEmpty(Mono.error(new NotFoundException(null)));
		} else {
			return Mono.error(new NotFoundException(null));

		}
	}
	@PostMapping(value = "/saveOrUpdateNewEmployee")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdateNewEmployee(@RequestBody Mono<EmpProfileCreateEmployeeDTO> empProfileCreateEmployeeDTO, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return employeeProfileHandler.saveOrUpdateNewEmployee(empProfileCreateEmployeeDTO, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	@PostMapping(value = "/getLeaveAllotmentDetails")
	public Mono<List<EmpProfileLeaveAllotmentDTO>> getLeaveAllotmentDetails(@RequestParam Integer leaveCategoryId, String doj) {
		LocalDate date = LocalDate.parse(doj, DateTimeFormatter.ISO_DATE);
		Mono<List<EmpProfileLeaveAllotmentDTO>> dataMono = employeeProfileHandler.getLeaveAllotmentDetails(leaveCategoryId, date );
		if (dataMono != null) {
			return dataMono.flatMap(data -> {
				if (!Utils.isNullOrEmpty(data)) {
					return Mono.just(data);
				} else {
					return Mono.error(new NotFoundException(null));
				}
			}).switchIfEmpty(Mono.error(new NotFoundException(null)));
		} else {
			return Mono.error(new NotFoundException(null));
		}
	}
	@PostMapping(value = "/getLeaveSummary")
	public Mono<List<EmpProfileLeaveAllotmentDTO>> getLeaveSummary(@RequestParam Integer empId, Integer year) {
		Mono<List<EmpProfileLeaveAllotmentDTO>> dataMono = employeeProfileHandler.getLeaveSummary(empId, year );
		if (dataMono != null) {
			return dataMono.flatMap(data -> {
				if (!Utils.isNullOrEmpty(data)) {
					return Mono.just(data);
				} else {
					return Mono.error(new NotFoundException(null));
				}
			}).switchIfEmpty(Mono.error(new NotFoundException(null)));
		} else {
			return Mono.error(new NotFoundException(null));
		}
	}
}