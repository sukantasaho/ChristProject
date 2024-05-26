package com.christ.erp.services.controllers.employee.attendance;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.dto.common.*;
import com.christ.erp.services.dto.employee.attendance.EmpAttendanceDTO;
import com.christ.erp.services.dto.employee.attendance.HigherAuthorityViewDTO;
import com.christ.erp.services.exception.NotFoundException;
import com.christ.erp.services.handlers.common.CommonApiHandler;
import com.christ.erp.services.handlers.employee.attendance.HigherAuthorityViewsHandler;
import com.christ.utility.lib.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/Secured/Employee/Attendance/HigherAuthorityViews")
public class HigherAuthorityViewsController {

    @Autowired
    HigherAuthorityViewsHandler higherAuthorityViewsHandler;

    @Autowired
    CommonApiHandler commonApiHandler;

    @PostMapping(value = "/getEmployeeAttendance")
    public Flux<Map<Integer, List<EmpAttendanceDTO>>> getEmployeeAttendance(@RequestBody HigherAuthorityViewDTO data) {
        return higherAuthorityViewsHandler.getEmployeeAttendance(data).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found For The Particular Range")));
    }

    @PostMapping(value = "/getAllDepartments")
    public Mono<ResponseEntity<ApiResult>> getAllDepartments() {
        return higherAuthorityViewsHandler.getAllDepartments().map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PostMapping(value = "/getCampus")
    public ApiResult<List<LookupItemDTO>> getCampus(@RequestHeader(Constants.QUERY_JWT_CAMPUS_IDS) String campusId) {
        List<Integer> longIds = Arrays.stream(campusId.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        ApiResult<List<LookupItemDTO>> result = commonApiHandler.getCampus(longIds);
        return result;
    }

    @PostMapping(value = "/getAdminViewEmployee")
    public Flux<SelectDTO> getAdminViewEmployee(@RequestHeader(Constants.QUERY_JWT_CAMPUS_IDS) String campusId) {
        return higherAuthorityViewsHandler.getAdminViewEmployee(campusId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found For Employee List")));
    }

    @PostMapping(value = "/getUserSpecificCampusDept")
    public Flux<Map<Integer,NestedSelectDTO>> getUserSpecificCampusDept(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId) {
        return higherAuthorityViewsHandler.getUserSpecificCampusDept(userId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found For The Drop Down")));
    }

    @PostMapping(value = "/getUserSpecificViewEmployee")
    public Flux<SelectDTO> getUserSpecificViewEmployee(@RequestHeader(Constants.HEADER_JWT_USER_ID) String userId){
        return higherAuthorityViewsHandler.getUserSpecificViewEmployee(userId).switchIfEmpty(Mono.error(new NotFoundException("Data Not Found For Employee List")));
    }
}
