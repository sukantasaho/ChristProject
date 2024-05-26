package com.christ.erp.services.controllers.admission.applicationprocess;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.admission.applicationprocess.AdmSelectionProcessScoreDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmSelectionProcessTypeDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.admission.applicationprocess.ScoreEntryHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.christ.utility.lib.Constants;

@RestController
@RequestMapping("/Secured/Admission/ApplicationProcess/ScoreEntry")
public class ScoreEntryController {
	@Autowired
	private ScoreEntryHandler scoreEntryHandler;

	@PostMapping(value = "/getSelectionProcessList")
	public Mono<AdmSelectionProcessScoreDTO> getSelectionProcessList(@RequestParam int applicationNumber,@RequestParam String mode) {
		return scoreEntryHandler.getSelectionProcessList(applicationNumber, mode).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/getSubProcess")
	public Flux<AdmSelectionProcessTypeDTO> getSubProcess(@RequestParam int processId,@RequestParam int admSelectionProcessTypeId , @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId)  {
		return scoreEntryHandler.getSubProcess(processId,admSelectionProcessTypeId,userId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}

	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/saveScore")
	public Mono<ResponseEntity<ApiResult>> saveScore(@RequestParam int processId,@RequestBody Mono<AdmSelectionProcessTypeDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return scoreEntryHandler.saveScore(processId,data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
	//GroupSocre Entry Api's
	
	@PostMapping(value = "/getGroupSelectionProcessList")
	public Mono<List<SelectDTO>> getGroupSelectionProcessList(@RequestParam int timeId) {
		return scoreEntryHandler.getGroupSelectionProcessList(timeId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/getGroupSubProcessList")
	public Mono<List<SelectDTO>> getGroupSubProcessList(@RequestParam int typeId)  {
		return scoreEntryHandler.getGroupSubProcessList(typeId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@PostMapping(value = "/getGroupSubProcessData")
	public Mono<List<AdmSelectionProcessScoreDTO>> getGroupSubProcessData(@RequestParam int groupId ,@RequestParam int subProcessId , @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId)  {
		return scoreEntryHandler.getGroupSubProcessData(groupId,subProcessId, userId).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/saveGroupScore")
	public Mono<ResponseEntity<ApiResult>> saveGroupScore(@RequestBody Mono<List<AdmSelectionProcessScoreDTO>> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return scoreEntryHandler.saveGroupScore(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	
}