package com.christ.erp.services.controllers.curriculum.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.common.ErpCommitteeDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.curriculum.settings.BoardOfStudiesHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value ="/Secured/Curriculum/Settings/BoardOfStudies")
public class BoardOfStudiesController {
	
	@Autowired
    private BoardOfStudiesHandler  boardOfStudiesHandler ;

    @GetMapping(value = "/getGridData")
    public Flux<ErpCommitteeDTO> getGridData(@RequestParam String yearId) {
    	return boardOfStudiesHandler.getGridData(yearId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value = "/edit")
    public Mono<ErpCommitteeDTO> edit(@RequestParam int id,@RequestParam String deptId) {
        return boardOfStudiesHandler.edit(id,deptId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
   
    @SuppressWarnings("rawtypes")
	@DeleteMapping(value = "/delete")
    public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id,String deptId, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return boardOfStudiesHandler.delete(id,deptId, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }
    
    @SuppressWarnings("rawtypes")
	@PostMapping(value = "/saveOrUpdate")
    public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<ErpCommitteeDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return boardOfStudiesHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }
    
    @SuppressWarnings("rawtypes")
	@PostMapping(value = "/saveImport")
    public Mono<ResponseEntity<ApiResult>> saveImport(@RequestBody Mono<ErpCommitteeDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return boardOfStudiesHandler.saveImport(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }
    
}  