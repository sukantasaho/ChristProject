package com.christ.erp.services.controllers.employee.settings;

import java.util.List;

import javax.persistence.Tuple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.settings.EmpApproversDTO;
import com.christ.erp.services.dto.employee.settings.EmpApproversDetailsDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.employee.settings.AssignApproversHandler;
import com.christ.utility.lib.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/Secured/Employee/Settings/AssignApprovers")
public class AssignApproversController {
	
	@Autowired
	AssignApproversHandler assignApproversHandler;

	@PostMapping("/getGridData")
	public Flux<EmpApproversDTO> getGridData(){
		return assignApproversHandler.getGridData().switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
	}
	
	@PostMapping("/edit")
	public Mono<EmpApproversDTO> edit(@RequestBody EmpApproversDTO data) {
		return  assignApproversHandler.edit(data).switchIfEmpty(Mono.error(new NotFoundException(null)));
	} 
	
	@RequestMapping(value = "/getEmpDetails", method = RequestMethod.POST)
	public Mono<ApiResult<List<EmpApproversDetailsDTO>>> selectMappingEmpDetails(@RequestBody EmpApproversDTO data) {
		ApiResult<List<EmpApproversDetailsDTO>> result = new ApiResult<List<EmpApproversDetailsDTO>>();
		try {
			EmpApproversDTO dto = new EmpApproversDTO();
			List<Tuple> emp = assignApproversHandler.dupcheck(data);
			if(!Utils.isNullOrEmpty(emp)) {
				result.success = false;
				result.failureMessage = "Data Exists Already";
			}else {
				assignApproversHandler.getempDetails(data, emp, dto);
				if(!Utils.isNullOrEmpty(dto.getItems())) {
					result.success = true;
					result.dto = dto.getItems();
				} else {
					result.success = false;
					result.failureMessage = dto.getErrorMsg();
				}
			}
		} catch (Exception error) {
			result.success = false;
			result.dto = null;
			result.failureMessage = error.getMessage();
		}
		return Utils.monoFromObject(result);
	}

	@PostMapping("/getEmployeeList")
	public Flux<SelectDTO> getEmployeeList(@RequestBody EmpApproversDTO data){
		return  assignApproversHandler.getEmployeeList(data).switchIfEmpty(Mono.error(new NotFoundException(null)));
	}
	 
	@SuppressWarnings("rawtypes")
	@PostMapping(value="/saveOrUpdate")
	public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<EmpApproversDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
		return assignApproversHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
	 
	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/delete")
	public Mono<ResponseEntity<ApiResult>> delete(@RequestBody EmpApproversDTO data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
		return assignApproversHandler.delete(data,userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
	}
}