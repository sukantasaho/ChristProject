package com.christ.erp.services.controllers.employee.salary;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.employee.salary.EmpPayScaleGradeMappingDTO;
import com.christ.erp.services.handlers.employee.salary.PayScaleMatrixHandler;
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
@RequestMapping(value="/Secured/Employee/Salary/PayScaleMatrix")
public class PayScaleMatrixController extends BaseApiController {

    PayScaleMatrixHandler payScaleMatrixHandler = PayScaleMatrixHandler.getInstance();

    @RequestMapping(value="/getMatrixData", method=RequestMethod.POST)
    public Mono<ApiResult<EmpPayScaleGradeMappingDTO>> getPayScaleLevelDetails(@RequestBody Map<String,String> data) {
        ApiResult<EmpPayScaleGradeMappingDTO> result = new ApiResult<>();
        try {
            EmpPayScaleGradeMappingDTO MatrixData = payScaleMatrixHandler.getMatrixData(data);
            if(!Utils.isNullOrEmpty(MatrixData)) {
                result.success = true;
                result.dto = MatrixData;
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

    @RequestMapping(value="/saveOrUpdate", method=RequestMethod.POST)
    public Mono<ApiResult> saveOrUpdate(@RequestBody EmpPayScaleGradeMappingDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        ApiResult result = new ApiResult();
        try {
            result.success = payScaleMatrixHandler.saveOrUpdate(data, userId);
        }
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);

    }

    @RequestMapping(value="/getGridData", method=RequestMethod.POST)
    public Mono<ApiResult<List<EmpPayScaleGradeMappingDTO>>> getGridData()  {
        ApiResult<List<EmpPayScaleGradeMappingDTO>> result = new ApiResult<>();
        try {
           List<EmpPayScaleGradeMappingDTO> gridData = payScaleMatrixHandler.getGridData();
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

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Mono<ApiResult> delete(@RequestBody Map<String,String> data) {
        ApiResult result = new ApiResult();
        try {
            result.success = payScaleMatrixHandler.delete(data);
        }
        catch (Exception e) {
            e.printStackTrace();
            result.success = false;
            result.failureMessage = e.getMessage();
        }
        return Utils.monoFromObject(result);
    }
}