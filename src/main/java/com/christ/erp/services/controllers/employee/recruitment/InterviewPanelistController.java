package com.christ.erp.services.controllers.employee.recruitment;

import java.io.File;
import java.util.List;

import com.christ.utility.lib.Constants;

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
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpInterviewExternalPanelistDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpInterviewPanelistDTO;
import com.christ.erp.services.handlers.employee.recruitment.InterviewPanelistHandler;
import com.christ.erp.services.transactions.employee.recruitment.InterviewPanelistTransaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/Recruitment/InterviewPanelist")
public class InterviewPanelistController extends BaseApiController {
	
	@Autowired
	InterviewPanelistTransaction interviewPanellistTransaction;
	
	//InterviewPanelistTransaction interviewPanellistTransaction = InterviewPanelistTransaction.getInstance();
	@Autowired
	InterviewPanelistHandler interviewPanellistHandler;

	@RequestMapping(value = "/getUniversityExternalPanelList", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getUniversityExternalPanelist() {
		ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
		try {			
			interviewPanellistHandler.getUniversityExternalPanelist(result);			
		}
		catch(Exception error) {
			result.dto = null;
			result.failureMessage = error.getMessage();
			result.success = false;	            
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/getInternalPanelList", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getDepartmentEmployee(@RequestParam("departmentId") String departmentId,
			@RequestParam("locationId") String locationId) {
		ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
		try {
			if((departmentId.isEmpty())&& (locationId.isEmpty())) {
				result.failureMessage = "Please select department or location";
			}else {
				String employeeType = "Internal";
				List<LookupItemDTO> data =  interviewPanellistHandler.getEmployee(departmentId,locationId,employeeType,result);
				if(Utils.isNullOrEmpty(data)) {
					result.failureMessage = "Data Not Found";
				}
			}
		}
		catch(Exception error) {
			result.dto = null;
			result.failureMessage = error.getMessage();
			result.success = false;	            
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/getExternalPanelList", method = RequestMethod.POST)
	public Mono<ApiResult<List<LookupItemDTO>>> getNonDepartmentEmployee(
			@RequestParam("departmentId") String departmentId, @RequestParam("locationId") String locationId) {
		ApiResult<List<LookupItemDTO>> result = new ApiResult<List<LookupItemDTO>>();
		try {
			if((departmentId.isEmpty())&& (locationId.isEmpty())) {
				result.failureMessage = "Please select department or location";
			}else {
				String employeeType = "External";
				List<LookupItemDTO> data = interviewPanellistHandler.getEmployee(departmentId,locationId,employeeType,result);
				if(Utils.isNullOrEmpty(data)) {
					result.failureMessage = "Data Not Found";
				}
			}
		}
		catch(Exception error) {
			result.dto = null;
			result.failureMessage = error.getMessage();
			result.success = false;	            
		}
		return Utils.monoFromObject(result);
	}

	@RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@RequestBody EmpInterviewPanelistDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			ApiResult<ModelBaseDTO> to = interviewPanellistHandler.saveOrUpdate(data, userId);
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

	@RequestMapping(value = "/saveOrUpdateUniversityExternalPanel", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdateUniversityExternalPanel(@RequestBody EmpInterviewExternalPanelistDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			ApiResult<ModelBaseDTO> to = interviewPanellistHandler.saveOrUpdateUniversityExternalPanel(data, userId);
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
	public Mono<ApiResult<List<EmpInterviewPanelistDTO>>> getGridData() {	
		ApiResult<List<EmpInterviewPanelistDTO>> result = new ApiResult<List<EmpInterviewPanelistDTO>>();
		try {
			List<EmpInterviewPanelistDTO> gridData = interviewPanellistHandler.getGridData();
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

	@RequestMapping(value ="/getGridDataUniversityExternalPanel", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpInterviewExternalPanelistDTO>>> getGridDataExternalPanel() {	
		ApiResult<List<EmpInterviewExternalPanelistDTO>> result = new ApiResult<List<EmpInterviewExternalPanelistDTO>>();
		try {
			List<EmpInterviewExternalPanelistDTO> gridData = interviewPanellistHandler.getGridDataUniversityExternalPanel();
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


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/deleteUniversityExternalPanel", method = RequestMethod.POST)
	public Mono<ApiResult> deleteUniversityExternalPanel(@RequestBody EmpInterviewExternalPanelistDTO data) {		
		ApiResult result = new ApiResult();
		try {
			if(interviewPanellistHandler.deleteUniversityExternalPanel(data)) {
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Mono<ApiResult> delete(@RequestBody EmpInterviewPanelistDTO data) {		
		ApiResult result = new ApiResult();
		try {
			if(interviewPanellistHandler.delete(data)) {
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public Mono<ApiResult> edit(@RequestBody EmpInterviewPanelistDTO data) {		
		ApiResult result = new ApiResult();
		try {
			EmpInterviewPanelistDTO dto = interviewPanellistHandler.edit(data);
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
	@RequestMapping(value = "/editUniversityExternalPanel", method = RequestMethod.POST)
	public Mono<ApiResult> editUniversityExternalPanel(@RequestParam String  id) {	
		ApiResult result = new ApiResult();
		try {
			EmpInterviewExternalPanelistDTO dto = interviewPanellistHandler.editUniversityExternalPanel(id);
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

	@SuppressWarnings("rawtypes")
	@PostMapping("/uploadFiles")
	public Mono<ApiResult> uploadFiles(@RequestPart("files") Flux<FilePart> data) {
		File directory = new File("ImageUpload");
		if(!directory.exists()) {
			directory.mkdir(); 
		}
		return Utils.uploadFiles(data,directory+"\\",new String[]{"application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/msword","application/pdf"});
	}
}
