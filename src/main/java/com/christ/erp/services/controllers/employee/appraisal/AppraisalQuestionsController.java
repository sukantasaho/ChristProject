package com.christ.erp.services.controllers.employee.appraisal;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.employee.appraisal.EmpAppraisalTemplateDTO;
import com.christ.erp.services.handlers.employee.appraisal.AppraisalQuestionsHandler;
import com.christ.utility.lib.Constants;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/Secured/Employee/Appraisal/AppraisalQuestionsController")
public class AppraisalQuestionsController extends BaseApiController {

    AppraisalQuestionsHandler appraisalQuestionsHandler = AppraisalQuestionsHandler.getInstance();

    @RequestMapping(value="/getGridData", method=RequestMethod.POST)
    public Mono<ApiResult<List<EmpAppraisalTemplateDTO>>> getGridData()  {
        ApiResult<List<EmpAppraisalTemplateDTO>> result = new ApiResult<>();
        try {
            List<EmpAppraisalTemplateDTO> templateDTOList = appraisalQuestionsHandler.getGridData();
            if(!Utils.isNullOrEmpty(templateDTOList)) {
                result.success = true;
                result.dto = templateDTOList;
            }
        }
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }

    @RequestMapping(value="/edit", method=RequestMethod.POST)
    public Mono<ApiResult<EmpAppraisalTemplateDTO>> edit(@RequestBody Map<String,String> data)  {
        ApiResult<EmpAppraisalTemplateDTO> result = new ApiResult<>();
        try {
            EmpAppraisalTemplateDTO empAppraisalTemplateDTO = appraisalQuestionsHandler.edit(data);
            if(!Utils.isNullOrEmpty(empAppraisalTemplateDTO)) {
                result.success = true;
                result.dto = empAppraisalTemplateDTO;
            }
        }
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }

    @RequestMapping(value="/saveOrUpdate", method=RequestMethod.POST)
    public Mono<ApiResult> saveOrUpdate(@RequestBody EmpAppraisalTemplateDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        ApiResult result = new ApiResult();
        try {
            result.success = appraisalQuestionsHandler.saveOrUpdate(data,Integer.parseInt(userId),result);
        }
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Mono<ApiResult> delete(@RequestBody Map<String,String> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        ApiResult result = new ApiResult();
        try {
            result.success = appraisalQuestionsHandler.delete(data, Integer.parseInt(userId));
        }
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }
}
