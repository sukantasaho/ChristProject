package com.christ.erp.services.controllers.common;

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
import com.christ.erp.services.dto.common.MenuScreenDTO;
import com.christ.erp.services.dto.common.ModuleSubDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.common.MenuScreenHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Common/MenuScreen")
@SuppressWarnings("rawtypes")
public class MenuScreenController {
	
	@Autowired
	private MenuScreenHandler menuScreenHandler;
	
	@PostMapping(value = "/getModule") 
    public Flux<SelectDTO> getModule() {
    	 return menuScreenHandler.getModule().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getSubModule") 
    public Mono<List<ModuleSubDTO>> getSubModule(@RequestParam String moduleId) {
    	 return menuScreenHandler.getSubModule(moduleId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
	@PostMapping(value = "/getMasterScreenReference") 
    public Flux<SelectDTO> getMasterScreenReference() {
    	 return menuScreenHandler.getMasterScreenReference().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
	
    @PostMapping(value = "/getGridData")
    public Mono<List<MenuScreenDTO>> getGridData() {
    	return menuScreenHandler.getGridData().switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
    @PostMapping(value = "/edit")
    public Mono<MenuScreenDTO> edit(@RequestParam int id) {
        return menuScreenHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    
	@DeleteMapping(value = "/delete")
    public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return menuScreenHandler.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }
    
	@PostMapping(value = "/saveOrUpdate")
    public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<MenuScreenDTO> dto, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return menuScreenHandler.saveOrUpdate(dto, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }
	
}
