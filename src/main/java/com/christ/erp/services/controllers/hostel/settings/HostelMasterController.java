package com.christ.erp.services.controllers.hostel.settings;
import java.io.File;
import java.util.List;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.hostel.settings.HostelDTO;
import com.christ.erp.services.handlers.hostel.settings.HostelMasterHandler;
import com.christ.utility.lib.Constants;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Hostel/Settings/HostelMaster")
public class HostelMasterController extends BaseApiController{

    HostelMasterHandler hostelMasterHandler = HostelMasterHandler.getInstance();

    @RequestMapping(value = "/getGridData", method = RequestMethod.POST)
    public Mono<ApiResult<List<HostelDTO>>> getGridData(){
        ApiResult<List<HostelDTO>> result = new ApiResult<>();
        try{
            List<HostelDTO> hostelMasterData = hostelMasterHandler.getGridData();
            if(!Utils.isNullOrEmpty(hostelMasterData)){
                result.success = true;
                result.dto = hostelMasterData;
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

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Mono<ApiResult<HostelDTO>> edit(@RequestParam("id") String id) throws Exception{
        ApiResult<HostelDTO> result = new ApiResult();
        HostelDTO hostelDTO = hostelMasterHandler.edit(String.valueOf(id));
        if(!Utils.isNullOrEmpty(hostelDTO)) {
            result.dto = hostelDTO;
            result.success = true;
        }
        else {
            result.dto = null;
            result.success = false;
        }
        return Utils.monoFromObject(result);
    }

    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate (@RequestBody HostelDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
        ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
        try {
            ApiResult<ModelBaseDTO> to = hostelMasterHandler.saveOrUpdate(data,userId);
            if(to.failureMessage==null || to.failureMessage.isEmpty()){
                result.success = true;
                result.dto = to.dto;
            }
            else{
                result.success = false;
                result.failureMessage = to.failureMessage;
            }
        }
        catch (Exception error) {
            result.success = false;
            result.dto = null;
            result.failureMessage = error.getMessage();
        }
        return Utils.monoFromObject(result);
    }

    @RequestMapping (value = "/delete", method = RequestMethod.POST)
    public Mono<ApiResult<ModelBaseDTO>> delete(@RequestParam("hostelId") String hostelId) {
        ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
        try {
            if (hostelMasterHandler.delete(hostelId)) {
                result.success = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.success = false;
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
		return Utils.uploadFiles(data ,directory+"//",new String[]{"jpg","png","jpeg","pdf"});
	}
}
