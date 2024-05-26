package com.christ.erp.services.handlers.employee.attendance;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.attendance.EmpTimeZoneDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpTimeZoneDetailsDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeCategoryDBO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.attendance.EmpTimeZoneDTO;
import com.christ.erp.services.dto.employee.attendance.EmpTimeZoneDetailsDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.employee.attendance.TimeZoneEntryTransaction;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.persistence.Tuple;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TimeZoneEntryHandler {

    @Autowired
    TimeZoneEntryTransaction timeZoneEntryTransaction;

    public Flux<EmpTimeZoneDTO> getGridData() {
        List<Tuple> empTimeZoneList = timeZoneEntryTransaction.getGridData();
        return convertEmpTimeZoneDboToDto(empTimeZoneList);
    }

    private Flux<EmpTimeZoneDTO> convertEmpTimeZoneDboToDto(List<Tuple> empTimeZoneList) {
        List<EmpTimeZoneDTO> empTimeZoneDTOList = new ArrayList<>();
        if (!Utils.isNullOrEmpty(empTimeZoneList)) {
            empTimeZoneList.forEach(tuple -> {
                EmpTimeZoneDTO empTimeZoneDTO = new EmpTimeZoneDTO();
                if (!Utils.isNullOrEmpty(tuple.get("ID")))
                    empTimeZoneDTO.setId(Integer.parseInt(String.valueOf(tuple.get("ID"))));
                if (!Utils.isNullOrEmpty(tuple.get("TimeZoneName")))
                    empTimeZoneDTO.setTimeZoneName(String.valueOf(tuple.get("TimeZoneName")));
                if (!Utils.isNullOrEmpty(tuple.get("isHolidayTimeZone"))) {
                    if (tuple.get("isHolidayTimeZone").toString().trim().equalsIgnoreCase("1")) {
                        empTimeZoneDTO.setHolidayTimeZone(true);
                    } else {
                        empTimeZoneDTO.setHolidayTimeZone(false);
                    }
                }
                if (!Utils.isNullOrEmpty(tuple.get("isgeneraltimezone"))) {
                    if (tuple.get("isgeneraltimezone").toString().trim().equalsIgnoreCase("1")) {
                        empTimeZoneDTO.setGeneralTimeZone(true);
                    } else {
                        empTimeZoneDTO.setGeneralTimeZone(false);
                    }
                }
                if(!Utils.isNullOrEmpty(tuple.get("empEmployeeCategoryId")) && !Utils.isNullOrEmpty(tuple.get("employeeCategoryName"))){
                    SelectDTO selectDTO = new SelectDTO();
                    selectDTO.setValue(String.valueOf(tuple.get("empEmployeeCategoryId")));
                    selectDTO.setLabel(String.valueOf(tuple.get("employeeCategoryName")));
                    empTimeZoneDTO.setEmployeeCategory(selectDTO);
                }
                empTimeZoneDTOList.add(empTimeZoneDTO);
            });
        }
        return Flux.fromIterable(empTimeZoneDTOList);
    }

    public Mono<EmpTimeZoneDTO> edit(int id) {
        EmpTimeZoneDBO empTimeZoneDBO = timeZoneEntryTransaction.edit(id);
        return convertDboToDto(empTimeZoneDBO);
    }

    private Mono<EmpTimeZoneDTO> convertDboToDto(EmpTimeZoneDBO empTimeZoneDBO) {
        EmpTimeZoneDTO empTimeZoneDTO = new EmpTimeZoneDTO();
        List<EmpTimeZoneDetailsDTO> empTimeZoneDetailsDTOList = new ArrayList<>();
        if (!Utils.isNullOrEmpty(empTimeZoneDBO)) {
            BeanUtils.copyProperties(empTimeZoneDBO, empTimeZoneDTO);
            if (empTimeZoneDBO.isGeneralTimeZone())
                empTimeZoneDTO.setSelected("general");
            if (empTimeZoneDBO.isHolidayTimeZone())
                empTimeZoneDTO.setSelected("holiday");
            if (!Utils.isNullOrEmpty(empTimeZoneDBO.getEmpEmployeeCategoryDBO())) {
                if (!Utils.isNullOrEmpty(empTimeZoneDBO.getEmpEmployeeCategoryDBO().getId()) && !Utils.isNullOrEmpty(empTimeZoneDBO.getEmpEmployeeCategoryDBO().getEmployeeCategoryName())) {
                    empTimeZoneDTO.setEmployeeCategory(new SelectDTO());
                    empTimeZoneDTO.getEmployeeCategory().setValue(String.valueOf(empTimeZoneDBO.getEmpEmployeeCategoryDBO().getId()));
                    empTimeZoneDTO.getEmployeeCategory().setLabel(String.valueOf(empTimeZoneDBO.getEmpEmployeeCategoryDBO().getEmployeeCategoryName()));
                }
            }
            if (!Utils.isNullOrEmpty(empTimeZoneDBO.getEmpTimeZoneDetailsDBOSet())) {
                empTimeZoneDetailsDTOList = empTimeZoneDBO.getEmpTimeZoneDetailsDBOSet()
                        .stream()
                        .sorted(Comparator.comparingInt(details -> getDayOrder(details.getDayName())))
                        .map(dboDetails -> {
                            EmpTimeZoneDetailsDTO empTimeZoneDetailsDTO = new EmpTimeZoneDetailsDTO();
                            BeanUtils.copyProperties(dboDetails, empTimeZoneDetailsDTO);
                            return empTimeZoneDetailsDTO;
                        })
                        .collect(Collectors.toList());
            }
            empTimeZoneDTO.setItems(empTimeZoneDetailsDTOList);
        }
        return Mono.just(empTimeZoneDTO);
    }

    // assign an order to day names
    private int getDayOrder(String dayName) {
        Map<String, Integer> dayOrderMap = Map.of(
                "MONDAY", 1,
                "TUESDAY", 2,
                "WEDNESDAY", 3,
                "THURSDAY", 4,
                "FRIDAY", 5,
                "SATURDAY", 6,
                "SUNDAY", 7
        );
        return dayOrderMap.getOrDefault(dayName, Integer.MAX_VALUE);
    }

    public Mono<ApiResult> delete(String id, String userId) {
        return timeZoneEntryTransaction.delete(Integer.parseInt(id), Integer.parseInt(userId)).map(Utils::responseResult);
    }

    public Mono<ApiResult> saveOrUpdate(Mono<EmpTimeZoneDTO> dto, String userId) {
        return dto
                .handle((empTimeZoneDTO, synchronousSink) -> {
                    boolean isTrue = timeZoneEntryTransaction.duplicateCheck(empTimeZoneDTO);
                    if (isTrue) {
                        synchronousSink.error(new DuplicateException("Duplicate entry found for timezone " + empTimeZoneDTO.getTimeZoneName() + " and employee category " + empTimeZoneDTO.getEmployeeCategory().getLabel() + "."));
                    } else {
                        synchronousSink.next(empTimeZoneDTO);
                    }
                }).cast(EmpTimeZoneDTO.class)
                .map(data -> convertDtoToDbo(data, userId))
                .flatMap(s -> {
                    if (!Utils.isNullOrEmpty(s.getId())) {
                        timeZoneEntryTransaction.update(s);
                    } else {
                        timeZoneEntryTransaction.save(s);
                    }
                    return Mono.just(Boolean.TRUE);
                }).map(Utils::responseResult);
    }

    public EmpTimeZoneDBO convertDtoToDbo(EmpTimeZoneDTO dto, String userId) {
        EmpTimeZoneDBO empTimeZoneDBO = !Utils.isNullOrEmpty(dto.getId()) ? timeZoneEntryTransaction.edit(dto.getId()) : new EmpTimeZoneDBO();
        if (!Utils.isNullOrEmpty(dto.getId())) {
            empTimeZoneDBO.setModifiedUsersId(Integer.parseInt(userId));
        } else {
            empTimeZoneDBO.setCreatedUsersId(Integer.parseInt(userId));
            empTimeZoneDBO.setRecordStatus('A');
        }
        if (!Utils.isNullOrEmpty(dto.getSelected())) {
            if (dto.getSelected().trim().equalsIgnoreCase("general")) {
                empTimeZoneDBO.setGeneralTimeZone(true);
            } else {
                empTimeZoneDBO.setGeneralTimeZone(false);
            }
            if (dto.getSelected().trim().equalsIgnoreCase("holiday")) {
                empTimeZoneDBO.setHolidayTimeZone(true);
            } else {
                empTimeZoneDBO.setHolidayTimeZone(false);
            }
        }
        if (!Utils.isNullOrEmpty(dto.getTimeZoneName())) {
            empTimeZoneDBO.setTimeZoneName(dto.getTimeZoneName());
        }
        if (!Utils.isNullOrEmpty(dto.getEmployeeCategory())) {
            if (!Utils.isNullOrEmpty(dto.getEmployeeCategory().getValue())) {
                empTimeZoneDBO.setEmpEmployeeCategoryDBO(new EmpEmployeeCategoryDBO());
                empTimeZoneDBO.getEmpEmployeeCategoryDBO().setId(Integer.parseInt(dto.getEmployeeCategory().getValue()));
            }
        }
        Set<EmpTimeZoneDetailsDBO> empTimeZoneDetailsDBOSetExist = !Utils.isNullOrEmpty(empTimeZoneDBO.getEmpTimeZoneDetailsDBOSet()) ? empTimeZoneDBO.getEmpTimeZoneDetailsDBOSet() : null;
        Map<Integer, EmpTimeZoneDetailsDBO> existEmpTimeZoneDetailsMap = !Utils.isNullOrEmpty(empTimeZoneDetailsDBOSetExist) ? empTimeZoneDetailsDBOSetExist.stream().filter(s -> s.getRecordStatus() == 'A').collect(Collectors.toMap(s -> s.getId(), s -> s)) : new HashMap<>();
        Set<EmpTimeZoneDetailsDBO> empTimeZoneDetailsDBOSet = new HashSet<>();
        if (!Utils.isNullOrEmpty(dto.getItems())) {
            dto.getItems().forEach(dtoDetails -> {
                EmpTimeZoneDetailsDBO empTimeZoneDetailsDBO = null;
                if (!Utils.isNullOrEmpty(dtoDetails.getId()) && existEmpTimeZoneDetailsMap.containsKey(dtoDetails.getId())) {
                    empTimeZoneDetailsDBO = existEmpTimeZoneDetailsMap.get(dtoDetails.getId());
                    empTimeZoneDetailsDBO.setModifiedUsersId(Integer.parseInt(userId));
                    existEmpTimeZoneDetailsMap.remove(dtoDetails.getId());
                } else {
                    empTimeZoneDetailsDBO = new EmpTimeZoneDetailsDBO();
                    empTimeZoneDetailsDBO.setCreatedUsersId(Integer.parseInt(userId));
                    empTimeZoneDetailsDBO.setRecordStatus('A');
                }
                empTimeZoneDetailsDBO.setEmpTimeZoneDBO(empTimeZoneDBO);
                empTimeZoneDetailsDBO.setExempted(dtoDetails.isExempted());
                empTimeZoneDetailsDBO.setOneTimePunch(dtoDetails.isOneTimePunch());
                empTimeZoneDetailsDBO.setDayName(dtoDetails.getDayName());
                empTimeZoneDetailsDBO.setEmpInTime(dtoDetails.getEmpInTime());
                empTimeZoneDetailsDBO.setTimeInStartTime(dtoDetails.getTimeInStartTime());
                empTimeZoneDetailsDBO.setInTimeEnds(dtoDetails.getInTimeEnds());
                empTimeZoneDetailsDBO.setEmpOutTime(dtoDetails.getEmpOutTime());
                empTimeZoneDetailsDBO.setTimeOutEndTime(dtoDetails.getTimeOutEndTime());
                empTimeZoneDetailsDBO.setOutTimeStart(dtoDetails.getOutTimeStart());
                empTimeZoneDetailsDBO.setHalfDayStartTime(dtoDetails.getHalfDayStartTime());
                empTimeZoneDetailsDBO.setHalfDayEndTime(dtoDetails.getHalfDayEndTime());
                empTimeZoneDetailsDBOSet.add(empTimeZoneDetailsDBO);
            });
            if (!Utils.isNullOrEmpty(existEmpTimeZoneDetailsMap)) {
                existEmpTimeZoneDetailsMap.forEach((entry, value) -> {
                    value.setModifiedUsersId(Integer.parseInt(userId));
                    value.setRecordStatus('D');
                    empTimeZoneDetailsDBOSet.add(value);
                });
            }
            empTimeZoneDBO.setEmpTimeZoneDetailsDBOSet(empTimeZoneDetailsDBOSet);
        }
        return empTimeZoneDBO;
    }
}
