package com.christ.erp.services.handlers.employee.attendance;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.NestedSelectDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.attendance.EmpAttendanceDTO;
import com.christ.erp.services.dto.employee.attendance.HigherAuthorityViewDTO;
import com.christ.erp.services.transactions.employee.attendance.HigherAuthorityViewsTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.persistence.Tuple;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HigherAuthorityViewsHandler {

    @Autowired
    HigherAuthorityViewsTransaction higherAuthorityViewsTransaction;

    @Autowired
    ViewEmployeeAttendanceHandler viewEmployeeAttendanceHandler;

    public Flux<Map<Integer, List<EmpAttendanceDTO>>> getEmployeeAttendance(HigherAuthorityViewDTO data) {
        List<Tuple> empAttendanceTuple = higherAuthorityViewsTransaction.getEmployeeAttendance(data);
        return convertDboToDto(empAttendanceTuple);
    }

    private Flux<Map<Integer, List<EmpAttendanceDTO>>> convertDboToDto(List<Tuple> empAttendanceTuple) {
        if (Utils.isNullOrEmpty(empAttendanceTuple)) {
            return Flux.empty();
        }
        return Flux.fromIterable(empAttendanceTuple)
                .filter(tuple -> !Utils.isNullOrEmpty(tuple.get("empId")))
                .groupBy(tuple -> Integer.parseInt(String.valueOf(tuple.get("empId"))))
                .flatMap(group -> group
                        .collectList()
                        .map(list -> {
                            Integer empId = group.key();
                            List<EmpAttendanceDTO> empAttendanceDTOS = list.stream()
                                    .map(this::getData)
                                    .collect(Collectors.toList());
                            Map<Integer, List<EmpAttendanceDTO>> attendanceMap = new HashMap<>();
                            attendanceMap.put(empId, empAttendanceDTOS);
                            return attendanceMap;
                        }))
                .collectList()
                .flatMapMany(Flux::fromIterable);
    }

    public EmpAttendanceDTO getData(Tuple tuple) {
        EmpAttendanceDTO empAttendanceDTO = new EmpAttendanceDTO();
        if (!Utils.isNullOrEmpty(tuple.get("empId")))
            empAttendanceDTO.setEmpId(Integer.parseInt(String.valueOf(tuple.get("empId")).trim()));
        if (!Utils.isNullOrEmpty(tuple.get("empName")))
            empAttendanceDTO.setEmpName(String.valueOf(tuple.get("empName")).trim());
        if (!Utils.isNullOrEmpty(tuple.get("attendance_date")))
            empAttendanceDTO.setAttendanceDate(Utils.convertStringDateTimeToLocalDateTime(tuple.get("attendance_date").toString()).toLocalDate());
        if (!Utils.isNullOrEmpty(tuple.get("dayName")))
            empAttendanceDTO.setDayName(tuple.get("dayName").toString());
        if (!Utils.isNullOrEmpty(tuple.get("total_hour")))
            empAttendanceDTO.setTotalHour(Utils.convertStringTimeToLocalTime(tuple.get("total_hour").toString()));
        if (!Utils.isNullOrEmpty(tuple.get("emp_time_zone_id")) && !Utils.isNullOrEmpty(tuple.get("time_zone_name"))) {
            SelectDTO selectDTO = new SelectDTO();
            selectDTO.setValue(tuple.get("emp_time_zone_id").toString());
            selectDTO.setLabel(tuple.get("time_zone_name").toString());
            empAttendanceDTO.setEmpTimeZone(selectDTO);
        }
        empAttendanceDTO = viewEmployeeAttendanceHandler.covertTupletoDto(empAttendanceDTO,tuple);
        return empAttendanceDTO;
    }

    @SuppressWarnings("rawtypes")
    public Mono<ApiResult> getAllDepartments() {
        ApiResult result = new ApiResult();
        result.setSuccess(true);
        return Mono.just(result);
    }

    private SelectDTO getEmployeeList(Tuple tuple) {
        SelectDTO selectDTO = new SelectDTO();
        selectDTO.setValue(tuple.get("empNo").toString());
        selectDTO.setLabel(tuple.get("empName").toString());
        return selectDTO;
    }

    public Flux<SelectDTO> getAdminViewEmployee(String campusId) {
        if(Utils.isNullOrEmpty(campusId)){
            return Flux.empty();
        }else {
            List<Integer> campusIdsList = Arrays.stream(campusId.split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            List<Integer> dept = null;
            return higherAuthorityViewsTransaction.getAdminViewEmployee(campusIdsList,dept).flatMapMany(Flux::fromIterable)
                    .filter(tuple -> !Utils.isNullOrEmpty(tuple.get("empName")) && !Utils.isNullOrEmpty(tuple.get("empNo")))
                    .map(this::getEmployeeList);
        }
    }

    public Flux<Map<Integer, NestedSelectDTO>> getUserSpecificCampusDept(String userId) {
        if (Utils.isNullOrEmpty(userId)) {
            return Flux.empty();
        }
        List<Tuple> list = higherAuthorityViewsTransaction.getUserSpecificCampusDept(userId);
        if (!Utils.isNullOrEmpty(list)) {
            return Flux.fromIterable(list)
                    .filter(tuple -> !Utils.isNullOrEmpty(tuple.get("campusID")))
                    .groupBy(tuple -> Integer.parseInt(tuple.get("campusID").toString()))
                    .flatMap(locData -> locData
                            .collectList()
                            .map(this::userSpecificCampusDept)
                    );
        }
        return Flux.empty();
    }

    private Map<Integer, NestedSelectDTO> userSpecificCampusDept(List<Tuple> campusMap) {
        Map<Integer, NestedSelectDTO> map = new HashMap<>();
        Map<Integer, List<Tuple>> existCampusMap = campusMap.stream()
                .filter(tuple -> !Utils.isNullOrEmpty(tuple.get("campusID")))
                .collect(Collectors.groupingBy(
                        tuple -> Integer.parseInt(tuple.get("campusID").toString()),
                        Collectors.toList()
                ));
        Set<Integer> campusSet = !Utils.isNullOrEmpty(campusMap)
                ? campusMap.stream()
                .filter(s -> !Utils.isNullOrEmpty(s) && !Utils.isNullOrEmpty(s.get("campusID")))
                .map(s -> Integer.parseInt(s.get("campusID").toString()))
                .collect(Collectors.toSet())
                : null;
        if(!Utils.isNullOrEmpty(campusSet) && !Utils.isNullOrEmpty(existCampusMap)){
            existCampusMap.forEach((k,v) -> {
                if(!map.containsKey(k)){
                    List<Tuple> department = existCampusMap.get(k);
                    List<SelectDTO> selectDTOS = new ArrayList<>();
                    if(!Utils.isNullOrEmpty(department)){
                        NestedSelectDTO nestedSelectDTO = new NestedSelectDTO();
                        department.forEach(dept -> {
                            nestedSelectDTO.setLabel(dept.get("campus").toString());
                            nestedSelectDTO.setValue(dept.get("campusID").toString());
                            SelectDTO selectDTO = new SelectDTO();
                            selectDTO.setLabel(dept.get("department").toString());
                            selectDTO.setValue(dept.get("departmentID").toString());
                            selectDTOS.add(selectDTO);
                            nestedSelectDTO.setList(selectDTOS);
                            map.put(k,nestedSelectDTO);
                        });
                    }
                }else {
                  NestedSelectDTO nestedSelectDTO  = map.get(k);
                    List<Tuple> department = existCampusMap.get(k);
                    List<SelectDTO> selectDTOS = new ArrayList<>();
                    if(!Utils.isNullOrEmpty(department)){
                        department.forEach(dept -> {
                            SelectDTO selectDTO = new SelectDTO();
                            selectDTO.setLabel(dept.get("department").toString());
                            selectDTO.setValue(dept.get("departmentID").toString());
                            selectDTOS.add(selectDTO);
                            nestedSelectDTO.setList(selectDTOS);
                            map.put(k,nestedSelectDTO);
                        });
                    }
                }
            });
        }
        return map;
    }


    public Flux<SelectDTO> getUserSpecificViewEmployee(String userId) {
        if (Utils.isNullOrEmpty(userId)) {
            return Flux.empty();
        }
        List<Tuple> list = higherAuthorityViewsTransaction.getUserSpecificCampusDept(userId);
        if(!Utils.isNullOrEmpty(list)){
            Set<Integer> campusSet = !Utils.isNullOrEmpty(list)
                    ? list.stream()
                    .filter(s -> !Utils.isNullOrEmpty(s) && !Utils.isNullOrEmpty(s.get("campusID")))
                    .map(s -> Integer.parseInt(s.get("campusID").toString()))
                    .collect(Collectors.toSet())
                    : new HashSet<>();
            Set<Integer> departmentSet = !Utils.isNullOrEmpty(list)
                    ? list.stream()
                    .filter(s -> !Utils.isNullOrEmpty(s) && !Utils.isNullOrEmpty(s.get("departmentID")))
                    .map(s -> Integer.parseInt(s.get("departmentID").toString()))
                    .collect(Collectors.toSet())
                    : new HashSet<>();
            return higherAuthorityViewsTransaction.getAdminViewEmployee(campusSet.stream().toList(),departmentSet.stream().toList()).flatMapMany(Flux::fromIterable)
                    .filter(tuple -> !Utils.isNullOrEmpty(tuple.get("empName")) && !Utils.isNullOrEmpty(tuple.get("empNo")))
                    .map(this::getEmployeeList);

        }
        return Flux.empty();
    }
}
