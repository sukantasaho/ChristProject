package com.christ.erp.services.controllers.curriculum.settings;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.common.ErpCommitteeDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.curriculum.settings.DepartmentAdvisoryCommitteeHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value= "/Secured/Curriculum/Settings/DepartmentAdvisoryCommittee")

public class DepartmentAdvisoryCommitteeController {
     
	@Autowired
	private DepartmentAdvisoryCommitteeHandler  departmentAdvisoryCommitteeHandler;

    @PostMapping(value = "/getGridData")
    public Flux<List<ErpCommitteeDTO>> getGridData(@RequestParam String yearId) {
    	return departmentAdvisoryCommitteeHandler.getGridData(yearId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
    @SuppressWarnings("rawtypes")
    @PostMapping(value = "/saveOrUpdate")
    public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<ErpCommitteeDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
    	return departmentAdvisoryCommitteeHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }
	
    @PostMapping(value = "/edit")
    public Mono<ErpCommitteeDTO> edit(@RequestParam int id) {
        return departmentAdvisoryCommitteeHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
   
    @SuppressWarnings("rawtypes")
	@DeleteMapping(value = "/delete")
    public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return departmentAdvisoryCommitteeHandler.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }
    
    @SuppressWarnings("rawtypes")
    @PostMapping(value = "/copyDataFromPrevYear")
    public Mono<ResponseEntity<ApiResult>> copyDataFromPrevYear(@RequestBody Mono<ErpCommitteeDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
    	return departmentAdvisoryCommitteeHandler.copyDataFromPrevYear(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }
}
