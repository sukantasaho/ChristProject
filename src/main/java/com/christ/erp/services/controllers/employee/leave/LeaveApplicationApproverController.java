package com.christ.erp.services.controllers.employee.leave;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.employee.leave.EmpLeaveEntryDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.employee.leave.LeaveApplicationApproverHandler;
import com.christ.utility.lib.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
@RequestMapping(value = "/Secured/Employee/Leave/leaveApplicationApprover")
public class LeaveApplicationApproverController {
    @Autowired
    LeaveApplicationApproverHandler leaveApplicationApproverHandler;

    @PostMapping(value="/getEmployeeDetailsForApprover")
    public Flux<EmpLeaveEntryDTO> getEmployeeDetailsForApprover(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) throws Exception{
        return leaveApplicationApproverHandler.getEmployeeDetailsForApprover(userId).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value = "/leaveApproverStatusUpdate")
    public Mono<ResponseEntity<ApiResult>> leaveApproverStatusUpdate(@RequestBody Mono<List<EmpLeaveEntryDTO>> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
        return leaveApplicationApproverHandler.leaveApproverStatusUpdate(data,Integer.parseInt(userId)).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PostMapping(value="/getEmployeeLeavesByApproverFilterStatus")
    public Flux<EmpLeaveEntryDTO> getEmployeeLeavesByApproverFilterStatus(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId,@RequestParam("value") String value) throws Exception{
        return leaveApplicationApproverHandler.getEmployeeLeavesByApproverFilterStatus(userId,value).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }

    @PostMapping(value="/getEmployeeLeavesOnSameDay")
    public Flux<EmpLeaveEntryDTO> getEmployeeLeavesOnSameDay(@RequestParam("employeeId") String employeeId,@RequestParam("startDate") String startDate,@RequestParam("endDate") String endDate) throws Exception{
        return leaveApplicationApproverHandler.getEmployeeLeavesOnSameDay(employeeId,startDate,endDate).switchIfEmpty(Mono.error(new NotFoundException(null)));
    }
}
