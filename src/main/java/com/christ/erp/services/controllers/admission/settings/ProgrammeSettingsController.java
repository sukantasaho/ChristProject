package com.christ.erp.services.controllers.admission.settings;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.controllers.common.BaseApiController;
import com.christ.erp.services.dto.admission.settings.AdmProgrammeBatchDTO;
import com.christ.erp.services.dto.admission.settings.AdmProgrammeBatchPreferencesDTO;
import com.christ.erp.services.dto.admission.settings.AdmProgrammeSettingsDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.admission.settings.ProgrammeSettingsHandler;
import com.christ.utility.lib.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(value="/Secured/Admission/Settings/ProgrammeSettings")
public class ProgrammeSettingsController extends BaseApiController {
	
	ProgrammeSettingsHandler handler = ProgrammeSettingsHandler.getInstance();
	@Autowired
	ProgrammeSettingsHandler programmeSettingsHandler;

	@RequestMapping(value="/saveOrUpdate", method=RequestMethod.POST)
	public Mono<ApiResult<ModelBaseDTO>> saveOrUpdate(@RequestBody AdmProgrammeSettingsDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		ApiResult<ModelBaseDTO> result = new ApiResult<ModelBaseDTO>();
		try {
			   result.success = programmeSettingsHandler.saveOrUpdate(data, result, userId);
		   }catch (Exception error) {
			   result.success = false;
			   result.dto = null;
			   result.failureMessage = "Sorry! An exception occurred.";
		   }
		return Utils.monoFromObject(result);
	}
	
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public Mono<ApiResult<AdmProgrammeSettingsDTO>> edit(@RequestParam("id") String id) {
		ApiResult<AdmProgrammeSettingsDTO> result = new ApiResult<AdmProgrammeSettingsDTO>();
		try {
	       	result.dto = handler.edit(id);
	       	if(!Utils.isNullOrEmpty(result.dto)) {
	       		result.success = true;
	       	}
	       }catch (Exception error) {
	           result.success = false;
	           result.dto = null;
	           result.failureMessage = "Sorry! An exception occurred.";
	       }
		return Utils.monoFromObject(result);
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/duplicateCheck")
	public Mono<ResponseEntity<ApiResult>> duplicateCheck(@RequestBody Mono<AdmProgrammeSettingsDTO> dto, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return programmeSettingsHandler.duplicateCheck(dto, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

    @SuppressWarnings("rawtypes")
	@DeleteMapping(value = "/delete")
    public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return programmeSettingsHandler.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }
	@PostMapping(value ="/getGridData")
	public Flux<AdmProgrammeSettingsDTO> getGridData(@RequestParam Integer yearId , @RequestParam(required=false) String programmeId, @RequestParam(required=false) String intakeBatchId) {
		return programmeSettingsHandler.getGridData(yearId,programmeId,intakeBatchId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	@PostMapping(value ="/getOtherPreference")
	public Mono<List<AdmProgrammeBatchDTO>> getOtherPreference(@RequestParam String settingsId) {
		return programmeSettingsHandler.getOtherPreference(settingsId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	@PostMapping(value ="/getOtherPreferenceProgrammeList")
	public Flux<SelectDTO> getOtherPreferenceProgrammeList(@RequestBody AdmProgrammeSettingsDTO dto) {
		return programmeSettingsHandler.getOtherPreferenceProgrammeList(dto).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	@PostMapping(value ="/getProgrammeBatchBySettingId")
	public Flux<SelectDTO> getProgrammeBatchBySettingId(@RequestParam String settingId) {
		return programmeSettingsHandler.getProgrammeBatchBySettingId(settingId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	@PostMapping(value ="/saveProgrammePreferences")
	public Mono<ResponseEntity<ApiResult>> saveProgrammePreferences(@RequestBody Mono<AdmProgrammeBatchDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return programmeSettingsHandler.saveProgrammePreferences(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value ="/getCheckProg")
	public Mono<ResponseEntity<ApiResult>> getCheckProg(@RequestParam Integer progSettingId,@RequestParam List<Integer> campOrlocIds) {
		return programmeSettingsHandler.getCheckProg(progSettingId,campOrlocIds).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

}
