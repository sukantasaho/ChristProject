package com.christ.erp.services.controllers.hostel.settings;

import java.util.List;

import com.christ.utility.lib.Constants;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.hostel.settings.HostelBlockUnitDTO;
import com.christ.erp.services.handlers.hostel.settings.BlockAndUnitHandler;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Hostel/Settings/BlockAndUnit")
public class BlockAndUnitController extends BaseApiController{
	
	BlockAndUnitHandler blockAndUnitHandler = BlockAndUnitHandler.getInstance();
	
	@RequestMapping(value="/getGridData", method=RequestMethod.POST)
    public Mono<ApiResult<List<HostelBlockUnitDTO>>> getGridData()  {
    	ApiResult<List<HostelBlockUnitDTO>> result = new ApiResult<>();
        try {
        	List<HostelBlockUnitDTO> hostelBlockUnitDTO = blockAndUnitHandler.getGridData();
            if(!Utils.isNullOrEmpty(hostelBlockUnitDTO)) {
                result.success = true;
                result.dto = hostelBlockUnitDTO;
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
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@RequestBody HostelBlockUnitDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			ApiResult<ModelBaseDTO> to = blockAndUnitHandler.saveOrUpdate(data, userId);
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
	public Mono<ApiResult<HostelBlockUnitDTO>> selectApplicationNumber(@RequestParam("id") String id) {
		ApiResult<HostelBlockUnitDTO> result = new ApiResult<HostelBlockUnitDTO>();
		HostelBlockUnitDTO hostelBlockUnitDTO;
		try {
			hostelBlockUnitDTO = blockAndUnitHandler.edit(id);
			if(hostelBlockUnitDTO.id!=null) {
				result.dto = hostelBlockUnitDTO;
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
	public Mono<ApiResult> delete(@RequestBody HostelBlockUnitDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        ApiResult result = new ApiResult();
		try {
			result.success = blockAndUnitHandler.delete(data.id, userId);
		}
		catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
    }
}
