package com.christ.erp.services.controllers.employee.salary;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.employee.salary.*;
import com.christ.erp.services.handlers.employee.salary.AnnualIncrementHandler;
import com.christ.utility.lib.Constants;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
@RequestMapping(value="/Secured/Employee/Salary/AnnualIncrement")
public class AnnualIncrementController extends BaseApiController {

    AnnualIncrementHandler annualIncrementHandler = AnnualIncrementHandler.getInstance();

    @RequestMapping(value="/getAllGradeLevelCell", method= RequestMethod.POST)
    public Mono<ApiResult<List<PayScaleMappingDTO>>> getAllGradeLevelCell()  {
        ApiResult<List<PayScaleMappingDTO>> result = new ApiResult<>();
        try {
            List<PayScaleMappingDTO> gridData = annualIncrementHandler.getAllGradeLevelCell();
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

    @RequestMapping(value="/getAllEmployee", method= RequestMethod.POST)
    public Mono<ApiResult<List<AnnualIncrementDTO>>> getAllEmployee(@RequestBody AnnualIncrementSearchDTO data,@RequestParam String isForReview)  {
        ApiResult<List<AnnualIncrementDTO>> result = new ApiResult<>();
        try {
            List<AnnualIncrementDTO> gridData = annualIncrementHandler.getAllEmployee(data,isForReview);
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
        return Mono.justOrEmpty(result);
    }

    @RequestMapping(value="/getReviewOrApprovalRequests", method= RequestMethod.POST)
    public Mono<ApiResult<AnnualIncrementReviewOrAppraisalListDTO>> getReviewOrApprovalRequests(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId)  {
        ApiResult<AnnualIncrementReviewOrAppraisalListDTO> result = new ApiResult<>();
        try {
                try {
                    result.dto = annualIncrementHandler.getReviewOrApprovalRequests(String.valueOf(userId));
                    result.success = true;
                } catch (Exception e) {
                    result.success = false;
                    result.failureMessage=e.getMessage();
                }
                return Mono.justOrEmpty(result);
        }
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Mono.justOrEmpty(result);
    }

    @RequestMapping(value="/getIncrementStatus", method= RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getIncrementStatus()  {
        ApiResult<List<LookupItemDTO>> result = new ApiResult<>();
        try {
            List<LookupItemDTO> gridData = annualIncrementHandler.getIncrementStatus();
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


    @RequestMapping(value="/getReviewersAndApproversList", method= RequestMethod.POST)
    public Mono<ApiResult<List<LookupItemDTO>>> getReviewersAndApproversList()  {
        ApiResult<List<LookupItemDTO>> result = new ApiResult<>();
        try {
            List<LookupItemDTO> gridData = annualIncrementHandler.getReviewersAndApproversList();
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

    @RequestMapping(value = "/saveOrUpdateComments", method = RequestMethod.POST)
    public Mono<ApiResult> saveOrUpdateComments(@RequestParam String comments, @RequestParam String payscaleId,@RequestParam boolean isReviewer,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
        ApiResult result = new ApiResult();
        if(!Utils.isNullOrEmpty(comments)) {
            try {
                result.dto = annualIncrementHandler.saveOrUpdateComments(comments,userId,payscaleId,isReviewer);
                result.success = true;
            }catch (Exception e) {
                result.success = false;
                result.dto = null;
                result.failureMessage = e.getMessage();
            }
        }
        return Utils.monoFromObject(result);
    }

    @RequestMapping(value="/saveOrUpdate", method=RequestMethod.POST)
    public Mono<ApiResult> saveOrUpdate(@RequestBody AnnualIncrementSubmitDTO data,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
        ApiResult result = new ApiResult();
        try {
            result.success = annualIncrementHandler.saveOrUpdate(data.data, userId,data.reviewerOrApproversArray);
        }
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }

    @RequestMapping(value = "/getEmpPayScaleComponentsForIncrement", method = RequestMethod.POST)
    public Mono<ApiResult<List<SalaryComponentDTO>>> getEmpPayScaleComponentsForIncrement(@RequestParam String payScaleType) {
        ApiResult<List<SalaryComponentDTO>> result = new ApiResult<>();
        try {
            List<SalaryComponentDTO>  salaryComponentDTOs = annualIncrementHandler.getEmpPayScaleComponentsForIncrement(payScaleType);
            if(!Utils.isNullOrEmpty(salaryComponentDTOs)) {
                result.success = true;
                result.dto = salaryComponentDTOs;
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
}