package com.christ.erp.services.controllers.admission.settings;
import java.util.List;
import javax.validation.Valid;

import com.christ.erp.services.common.EntityManagerInstance;
import com.christ.erp.services.common.Utils;
import com.christ.utility.lib.Constants;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.admission.settings.AdmScoreCardDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.handlers.admission.settings.SelectionProcessScoreCardHandler;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value="/Secured/Admission/Settings/AdmSelectionProcessScoreCard")
public class SelectionProcessScoreCardController extends BaseApiController {
	
	SelectionProcessScoreCardHandler admSelectionProcessScoreCardHandler=SelectionProcessScoreCardHandler.getInstance();

	@RequestMapping(value="/saveOrUpdate", method=RequestMethod.POST)
    public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@Valid @RequestBody AdmScoreCardDTO admScoreCardDTO, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
    	ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			if(admSelectionProcessScoreCardHandler.duplicateCheck(admScoreCardDTO)) {
				result.failureMessage = "Duplicate record exist with Score Card Template Name: "+ admScoreCardDTO.scoreCardTemplateName;
			} else {
				if(admSelectionProcessScoreCardHandler.saveOrUpdate(admScoreCardDTO,userId)) {
				   result.success = true;
				}
			} 
		}catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
    }
	
	@RequestMapping(value="/getGridData", method=RequestMethod.POST)
	public Mono<ApiResult<List<AdmScoreCardDTO>>> getGridData() throws Exception {
		ApiResult<List<AdmScoreCardDTO>> result=new ApiResult<List<AdmScoreCardDTO>>();
		result.success=true;
		result.dto=admSelectionProcessScoreCardHandler.getGridData();
		return Utils.monoFromObject(result);
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/delete", method=RequestMethod.POST)
	public Mono<ApiResult> delete(@RequestParam String id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) throws Exception {
		ApiResult result=new ApiResult();
		 try {
			result.success	= admSelectionProcessScoreCardHandler.delete(id,userId );
		 } catch (Exception e) {
			e.printStackTrace();
		 }
		 return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Mono<ApiResult<AdmScoreCardDTO>> edit(@RequestParam String id)   {
		ApiResult<AdmScoreCardDTO> result=new ApiResult<AdmScoreCardDTO>();
		try {
			result.dto=admSelectionProcessScoreCardHandler.edit(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		EntityManagerInstance.closeEntityManager();
		return Utils.monoFromObject(result);
	}
}
