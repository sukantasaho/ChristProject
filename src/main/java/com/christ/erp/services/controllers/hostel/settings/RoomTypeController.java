package com.christ.erp.services.controllers.hostel.settings;

import java.io.File;
import java.util.List;

import com.christ.utility.lib.Constants;
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
import com.christ.erp.services.dto.hostel.settings.HostelRoomTypeDTO;
import com.christ.erp.services.handlers.hostel.settings.RoomTypeHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Hostel/Settings/RoomType")
public class RoomTypeController extends BaseApiController{
	
	RoomTypeHandler roomTypeHandler = RoomTypeHandler.getInstance();
	
	@RequestMapping(value="/getGridData", method=RequestMethod.POST)
    public Mono<ApiResult<List<HostelRoomTypeDTO>>> getGridData()  {
    	ApiResult<List<HostelRoomTypeDTO>> result = new ApiResult<>();
        try {
        	List<HostelRoomTypeDTO> hostelRoomTypeDTO =  roomTypeHandler.getGridData();
            if(!Utils.isNullOrEmpty(hostelRoomTypeDTO)) {
                result.success = true;
                result.dto = hostelRoomTypeDTO;
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
	
	@RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@RequestBody HostelRoomTypeDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			ApiResult<ModelBaseDTO> to =  roomTypeHandler.saveOrUpdate(data, userId);
			if(to.failureMessage==null || to.failureMessage.isEmpty()){
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
	
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public Mono<ApiResult<HostelRoomTypeDTO>> selectApplicationNumber(@RequestParam("id") String id) {
		ApiResult<HostelRoomTypeDTO> result = new ApiResult<HostelRoomTypeDTO>();
		HostelRoomTypeDTO hostelRoomTypeDTO;
		try {
			hostelRoomTypeDTO =  roomTypeHandler.edit(id);
			if(hostelRoomTypeDTO.id!=null) {
				result.dto = hostelRoomTypeDTO;
				result.success = true;
			}
			else {
				result.dto = null;
				result.success = false;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Utils.monoFromObject(result);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Mono<ApiResult> delete(@RequestBody HostelRoomTypeDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        ApiResult result = new ApiResult();
		try {
			result.success =  roomTypeHandler.delete(data.id, userId);
		}
		catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
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
