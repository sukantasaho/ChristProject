package com.christ.erp.services.controllers.employee.leave;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.employee.leave.EmpLeaveEntryDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.employee.leave.LeaveApplicationAuthoriserHandler;
import com.christ.utility.lib.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
@RequestMapping(value = "/Secured/Employee/Leave/leaveApplicationAuthoriser")
public class LeaveApplicationAuthoriserController {
    @Autowired
    LeaveApplicationAuthoriserHandler leaveApplicationAuthoriserHandler;
    @PostMapping(value="/getEmployeeDetailsForAuthoriser")
    public Flux<EmpLeaveEntryDTO> getEmployeeDetailsForAuthoriser(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) throws Exception{
        return leaveApplicationAuthoriserHandler.getEmployeeDetailsForAuthoriser(userId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
    @SuppressWarnings("rawtypes")
    @PostMapping(value = "/leaveAuthoriserStatusUpdate")
    public Mono<ResponseEntity<ApiResult>> leaveAuthoriserStatusUpdate(@RequestBody Mono<List<EmpLeaveEntryDTO>> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) throws Exception{
        return leaveApplicationAuthoriserHandler.leaveAuthoriserStatusUpdate(data,Integer.parseInt(userId)).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PostMapping(value="/getEmployeeLeavesByAuthoriserFilterStatus")
    public Flux<EmpLeaveEntryDTO> getEmployeeLeavesByAuthoriserFilterStatus(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId,@RequestParam("value") String value) throws Exception{
        return leaveApplicationAuthoriserHandler.getEmployeeLeavesByAuthoriserFilterStatus(userId,value).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
}
