package com.christ.erp.services.controllers.curriculum.curriculumDesign;

import java.io.File;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.curriculum.curriculumDesign.ProgrammeOutcomeDefinitionsDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.curriculum.curriculumDesign.ProgrammeOutcomeDefinitionsHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@SuppressWarnings({"rawtypes"})
@RequestMapping(value = "/Secured/Curriculum/Settings/ProgrammeOutcomeDefinitions")
public class ProgrammeOutcomeDefinitionsController {

	@Autowired
	private ProgrammeOutcomeDefinitionsHandler programmeOutcomeDefinitionsHandler;

	@GetMapping(value = "/getGridData")
	public Mono<List<ProgrammeOutcomeDefinitionsDTO>> getGridData(@RequestParam String yearId,@RequestParam String departId) {
		return programmeOutcomeDefinitionsHandler.getGridData(yearId,departId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/edit")
	public Mono<ProgrammeOutcomeDefinitionsDTO> edit(@RequestParam int batchwiseSettingId) {
		return programmeOutcomeDefinitionsHandler.edit(batchwiseSettingId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<ProgrammeOutcomeDefinitionsDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return programmeOutcomeDefinitionsHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping("/validateProgrammeOutcomeDetailsDocuments")
	public Mono<ApiResult> validateProgrammeOutcomeDetailsDocuments(@RequestPart("files") Flux<FilePart> data) {
		File directory = new File("programmeOutcomeDetailsDocuments");
		if(!directory.exists()) {
			directory.mkdir();
		}
		return Utils.uploadFiles(data,directory+"//",new String[]{"jpeg","png"});
	}

	@PostMapping(value = "/getProgrammeListToImport") 
	public Flux<SelectDTO> getProgrammeListToImport(@RequestParam int departmentId,@RequestParam int fromYearId) {
		return programmeOutcomeDefinitionsHandler.getProgrammeListToImport(departmentId,fromYearId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@PostMapping(value = "/saveImportProgrammeDefinitions")
	public Mono<ResponseEntity<ApiResult>> saveImportProgrammeDefinitions(@RequestBody Mono<ProgrammeOutcomeDefinitionsDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return programmeOutcomeDefinitionsHandler.saveImportProgrammeDefinitions(data,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

}
