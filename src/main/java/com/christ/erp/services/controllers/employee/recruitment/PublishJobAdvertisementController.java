package com.christ.erp.services.controllers.employee.recruitment;

import java.io.File;
import java.util.List;

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
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnAdvertisementDTO;
import com.christ.erp.services.handlers.employee.recruitment.PublishJobAdvertisementHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/Recruitment/PublishJobAdvertisement")
public class PublishJobAdvertisementController extends BaseApiController {
	//PublishJobAdvertisementHandler publishJobAdvertisementHandler = PublishJobAdvertisementHandler.getInstance();
	@Autowired
	PublishJobAdvertisementHandler publishJobAdvertisementHandler;
	
	@RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveEmpapplnJobAdvertisement(@RequestBody EmpApplnAdvertisementDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			ApiResult<ModelBaseDTO> to = publishJobAdvertisementHandler.saveOrUpdate(data, userId);
			if(to.failureMessage == null || to.failureMessage.isEmpty()){
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
	
	@RequestMapping(value="/getGridData", method=RequestMethod.POST)
    public Mono<ApiResult<List<EmpApplnAdvertisementDTO>>> getGridData()  {
 	ApiResult<List<EmpApplnAdvertisementDTO>> result = new ApiResult<>();
        try {
        	List<EmpApplnAdvertisementDTO> jobAdvertisementData = publishJobAdvertisementHandler.getGridData();
            if(!Utils.isNullOrEmpty(jobAdvertisementData)) {
                result.success = true;
                result.dto = jobAdvertisementData;
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
	 
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Mono<ApiResult> deleteJobAdvertisement(@RequestBody EmpApplnAdvertisementDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        ApiResult result = new ApiResult();
		try {
			result.success = publishJobAdvertisementHandler.deleteJobAdvertisement(data.id, userId);
		}
		catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
    }
	 
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public Mono<ApiResult<EmpApplnAdvertisementDTO>> selectJobAdvertisement(@RequestParam("id") String id) throws Exception {
		ApiResult<EmpApplnAdvertisementDTO> result = new ApiResult<EmpApplnAdvertisementDTO>();
		EmpApplnAdvertisementDTO empApplnAdvertisementDTO = publishJobAdvertisementHandler.selectEmpApplnAdvertisement(id);
		if(empApplnAdvertisementDTO.id!=null) {
			result.dto = empApplnAdvertisementDTO;
			result.success = true;
		}
		else {
			result.dto = null;
			result.success = false;
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
		return Utils.uploadFiles(data,directory+"//",new String[]{"jpg","png","jpeg","pdf"});
	}
}
