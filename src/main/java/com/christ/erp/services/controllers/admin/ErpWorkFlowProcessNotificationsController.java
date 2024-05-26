package com.christ.erp.services.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.common.ErpWorkFlowProcessNotificationsDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.admin.ErpWorkFlowProcessNotificationsHandler;
import com.christ.utility.lib.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Secured/Admin/Settings/AddErpWorkFlowProcessNotifications")
public class ErpWorkFlowProcessNotificationsController {
	
	@Autowired
	ErpWorkFlowProcessNotificationsHandler erpWorkFlowProcessNotificationsHandler;

	@PostMapping(value="/getGridData")
	public Flux<ErpWorkFlowProcessNotificationsDTO> getGridData(){
		return erpWorkFlowProcessNotificationsHandler.getGridData().switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value="/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<ErpWorkFlowProcessNotificationsDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return erpWorkFlowProcessNotificationsHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}

	@PostMapping(value= "/edit")
	public Mono<ErpWorkFlowProcessNotificationsDTO> edit(@RequestParam int ID){
		return erpWorkFlowProcessNotificationsHandler.edit(ID).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
}
