package com.christ.erp.services.controllers.employee.attendance;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.employee.attendance.EmpDatewiseTimeZoneDTO;
import com.christ.erp.services.dto.employee.common.EmployeeApplicationDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.employee.attendance.BulkTimeZoneHandler;
import com.christ.erp.services.transactions.employee.attendance.BulkTimeZoneTransation;
import com.christ.utility.lib.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping(value = "/Secured/Employee/Attendance/BulkTimeZone")
public class BulkTimeZoneController {

    @Autowired
    BulkTimeZoneHandler bulkTimeZoneHandler;

    @Autowired
    BulkTimeZoneTransation bulkTimeZoneTransation;

    @PostMapping(value = "/getGridData")
    public Flux<EmpDatewiseTimeZoneDTO> getGridData(){
        return bulkTimeZoneHandler.getData().switchIfEmpty(Mono.error(new NotFoundException("Data Not Found")));
    }

    @PostMapping(value = "/edit")
    public Mono<EmpDatewiseTimeZoneDTO> edit(@RequestParam int id){
        return bulkTimeZoneHandler.edit(id).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found ")));
    }

    @PostMapping(value = "/delete")
    public Mono<ResponseEntity<ApiResult>> delete(@RequestParam int id, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
        return bulkTimeZoneHandler.delete(id, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PostMapping(value="/saveOrUpdate")
    public Mono<ResponseEntity<ApiResult>> saveOrUpdate(@RequestBody Mono<EmpDatewiseTimeZoneDTO> data, @RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return bulkTimeZoneHandler.saveOrUpdate(data, userId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PostMapping(value = "/getEmployeeByIdsByEmpCategory")
    public Flux<EmployeeApplicationDTO> getEmployeeByIdsByEmpCategory(@RequestBody EmpDatewiseTimeZoneDTO data) {
        return bulkTimeZoneHandler.getEmployeeByIdsByEmpCategory(data).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found EmployeeList")));
    }

}