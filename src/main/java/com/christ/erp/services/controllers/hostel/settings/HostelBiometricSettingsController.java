package com.christ.erp.services.controllers.hostel.settings;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.hostel.settings.HostelBiometricSettingsDTO;
import com.christ.erp.services.handlers.hostel.settings.HostelBiometricSettingsHandler;
import com.christ.utility.lib.Constants;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(value = "/Secured/Hostel/Settings/BiometricSetting")
public class HostelBiometricSettingsController extends BaseApiController {

    HostelBiometricSettingsHandler hostelBiometricSettingsHandler = HostelBiometricSettingsHandler.getInstance();

    @RequestMapping(value = "/getGridData",method = RequestMethod.POST)
    public Mono<ApiResult<List<HostelBiometricSettingsDTO>>> getGridData()throws Exception {
        ApiResult<List<HostelBiometricSettingsDTO>> result = new ApiResult<>();
        try{
            List<HostelBiometricSettingsDTO> hostelBiometricSettingsDTOS = hostelBiometricSettingsHandler.getGridData();
            if(!Utils.isNullOrEmpty(hostelBiometricSettingsDTOS)){
                result.success = true;
                result.dto = hostelBiometricSettingsDTOS;
            }else{
                result.success = false;
            }
        }catch (Exception error){
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }

    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    public Mono<ApiResult<HostelBiometricSettingsDTO>> edit(@RequestParam("id")String id) throws Exception{
    ApiResult<HostelBiometricSettingsDTO> result = new ApiResult<>();
    HostelBiometricSettingsDTO dto = hostelBiometricSettingsHandler.edit(id);
        if(!Utils.isNullOrEmpty(dto)) {
            result.dto = dto;
            result.success = true;
        }
        else {
            result.dto = null;
            result.success = false;
        }
        return Utils.monoFromObject(result);
    }

    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public Mono<ApiResult<ModelBaseDTO>> delete(@RequestParam("id") String id) throws Exception {
        ApiResult<ModelBaseDTO> result = new ApiResult<>();
        try {
            if (hostelBiometricSettingsHandler.delete(id)) {
                result.success = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.success = false;
            result.failureMessage = e.getMessage();
        }
        return Utils.monoFromObject(result);
    }

    @RequestMapping(value = "/saveOrUpdate",method = RequestMethod.POST)
    public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@RequestBody HostelBiometricSettingsDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) throws Exception{
        ApiResult<ModelBaseDTO> result = new ApiResult<>();
        try {
            hostelBiometricSettingsHandler.saveOrUpdate(data,userId,result);
        }
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }
}
