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
import com.christ.erp.services.dto.admission.applicationprocess.FinalResultEntryDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.admission.applicationprocess.FinalResultEntryHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value ="/Secured/Admission/ApplicationProcess/FinalResultEntry")
public class FinalResultEntryController {
	
	@Autowired
	private FinalResultEntryHandler finalResultEntryHandler;

	@PostMapping(value = "/getActiveProgrammeByYearValue") 
    public Mono<List<FinalResultEntryDTO>> getActiveProgrammeByYearValue(@RequestParam String yearValue) {
    	 return finalResultEntryHandler.getActiveProgrammeByYearValue(yearValue).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
    @PostMapping(value = "/getGridData")
	public Mono<List<FinalResultEntryDTO>> getGridData(@RequestBody Mono<FinalResultEntryDTO> data) {
    	return finalResultEntryHandler.getGridData(data).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
    
    @SuppressWarnings("rawtypes")
	@PostMapping(value = "/saveApplicantes")
	public Mono<ResponseEntity<ApiResult>> saveApplicantes( @RequestBody Mono<List<FinalResultEntryDTO>> data,@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId)  {
	    return finalResultEntryHandler.saveApplicantes(data,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

}
