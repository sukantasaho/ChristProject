package com.christ.erp.services.controllers.admission.settings;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.admission.settings.AdmPrerequisiteSettingsDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.admission.settings.ApplicationPrerequisitesHandler;
import com.christ.utility.lib.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Admission/applicationPrerequisites")
public class ApplicationPrerequisitesController extends BaseApiController {
//	ApplicationPrerequisitesHandler applicationPrerequisitesHandler=ApplicationPrerequisitesHandler.getInstance();

    @Autowired
    ApplicationPrerequisitesHandler applicationPrerequisitesHandler1;

//	@RequestMapping(value="/saveOrUpdate", method=RequestMethod.POST)
//    public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@RequestBody AdmPrerequisiteSettingsDTO PrerequisiteSettingsDTO, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
//    	ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
//		try {
//			ApiResult<ModelBaseDTO> to = applicationPrerequisitesHandler.saveOrUpdate(PrerequisiteSettingsDTO,userId);
//			if (to.failureMessage != null) {
//				result.success = false;
//				result.failureMessage = to.failureMessage;
//			} else {
//				result.success = true;
//			}
//		} catch (Exception error) {
//			result.success = false;
//			result.dto = null;
//			result.failureMessage = error.getMessage();
//		}
//		return Utils.monoFromObject(result);
//    }

//	@RequestMapping(value ="/getGridData", method = RequestMethod.POST)
//	public Mono<ApiResult<List<AdmPrerequisiteSettingsDTO>>> getGridData() {
//		ApiResult<List<AdmPrerequisiteSettingsDTO>> result = new ApiResult<List<AdmPrerequisiteSettingsDTO>>();
//		try {
//			List<AdmPrerequisiteSettingsDTO> gridData = applicationPrerequisitesHandler.getGridData();
//			if(!Utils.isNullOrEmpty(gridData)) {
//				result.success = true;
//				result.dto = gridData;
//			}
//			else {
//				result.success = false;
//			}
//		}
//		catch (Exception error) {
//			result.success = false;
//			result.dto = null;
//			result.failureMessage = error.getMessage();
//		}
//		return Utils.monoFromObject(result);
//	}

//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	@RequestMapping(value = "/delete", method = RequestMethod.POST)
//	public Mono<ApiResult> delete(@RequestBody AdmPrerequisiteSettingsDTO data) {
//		ApiResult result = new ApiResult();
//		try {
//			if(applicationPrerequisitesHandler.delete(data)) {
//				result.success = true;
//				result.dto = null;
//			}
//			else
//				result.success = false;
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//			result.success = false;
//			result.dto = null;
//			result.failureMessage = e.getMessage();
//		}
//		return Utils.monoFromObject(result);
//	}

    @SuppressWarnings({"rawtypes"})
//	@RequestMapping(value = "/edit", method = RequestMethod.POST)
//	public Mono<ApiResult> edit(@RequestParam("academicYearId")String academicYearId, @RequestParam("programmeId")String programmeId) {
//		ApiResult result = new ApiResult();
//		try {
//			AdmPrerequisiteSettingsDTO dto = applicationPrerequisitesHandler.edit(academicYearId,programmeId);
//			if(!Utils.isNullOrEmpty(dto)) {
//				result.success = true;
//				result.dto = dto;
//			}
//			else {
//				result.success = false;
//				result.dto = null;
//				result.failureMessage ="Error operation";
//			}
//		}catch (Exception e) {
//			e.printStackTrace();
//			result.success = false;
//			result.dto = null;
//			result.failureMessage = e.getMessage();
//		}
//		return Utils.monoFromObject(result);
//	}

//   @RequestMapping(value = "/getYearList", method = RequestMethod.POST)
//	public Mono<ApiResult<List<LookupItemDTO>>> getGeneratedYear() {
//		ApiResult<List<LookupItemDTO>> generatedYear = new ApiResult<>();
//		try {
//			generatedYear = applicationPrerequisitesHandler.getYearList();
//			generatedYear.success = true;
//		} catch (Exception error) {
//			generatedYear.success = false;
//			generatedYear.dto = null;
//			generatedYear.failureMessage = error.getMessage();
//		}
//		return Utils.monoFromObject(generatedYear);
//	}

    @DeleteMapping(value = "/delete")
    public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return applicationPrerequisitesHandler1.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PostMapping(value = "/getGridData")
    public Flux<AdmPrerequisiteSettingsDTO> getGridData(@RequestParam Integer yearId, @RequestParam(required = false) String programmeId, @RequestParam(required = false) String typeId) {
        return applicationPrerequisitesHandler1.getGridData(yearId, programmeId, typeId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value = "/edit")
    public Mono<AdmPrerequisiteSettingsDTO> edit(@RequestParam Integer id) {
        return applicationPrerequisitesHandler1.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value = "/saveOrUpdate")
    public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<AdmPrerequisiteSettingsDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return applicationPrerequisitesHandler1.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }
}
