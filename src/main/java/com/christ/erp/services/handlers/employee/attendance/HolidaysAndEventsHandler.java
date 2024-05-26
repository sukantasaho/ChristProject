package com.christ.erp.services.handlers.employee.attendance;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpLocationDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpHolidayEventsCddMapDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpHolidayEventsDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpHolidayEventsEmployeewiseDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeCategoryDBO;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.employee.attendance.HolidaysAndEventsEntryDTO;
import com.christ.erp.services.dto.employee.common.EmployeeApplicationDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.employee.attendance.HolidaysAndEventsTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.persistence.Tuple;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class HolidaysAndEventsHandler {

    @Autowired
    HolidaysAndEventsTransaction holidaysAndEventsTransaction;

    public Flux<HolidaysAndEventsEntryDTO> getGridData(Integer academicYearId) {
        List<EmpHolidayEventsDBO> empHolidayEventsDBOList = holidaysAndEventsTransaction.getGridData(academicYearId);
        return convertEmpHolidayEventsDBOToDto(empHolidayEventsDBOList);
    }

    private Flux<HolidaysAndEventsEntryDTO> convertEmpHolidayEventsDBOToDto(List<EmpHolidayEventsDBO> empHolidayEventsDBOList) {
        List<HolidaysAndEventsEntryDTO> holidaysAndEventsEntryDTOList = new ArrayList<>();
        if (!Utils.isNullOrEmpty(empHolidayEventsDBOList)) {
            empHolidayEventsDBOList.forEach(dbo -> {
                HolidaysAndEventsEntryDTO dto = new HolidaysAndEventsEntryDTO();
                if (!Utils.isNullOrEmpty(dbo.getId()))
                    dto.setId(String.valueOf(dbo.getId()));
                dto.setAcademicYear(new ExModelBaseDTO());
                if (!Utils.isNullOrEmpty(dbo.getErpAcademicYearId())) {
                    if (!Utils.isNullOrEmpty(dbo.getErpAcademicYearId().getId()) && !Utils.isNullOrEmpty(dbo.getErpAcademicYearId().getAcademicYearName())) {
                        dto.getAcademicYear().setId(String.valueOf(dbo.getErpAcademicYearId().getId()));
                        dto.getAcademicYear().setText(dbo.getErpAcademicYearId().getAcademicYearName());
                    }
                }
                dto.setLocation(new ExModelBaseDTO());
                if (!Utils.isNullOrEmpty(dbo.getErpLocationId())) {
                    if (!Utils.isNullOrEmpty(dbo.getErpLocationId().getId()) && !Utils.isNullOrEmpty(dbo.getErpLocationId().getLocationName()))
                        dto.getLocation().setId(String.valueOf(dbo.getErpLocationId().getId()));
                    dto.getLocation().setText(dbo.getErpLocationId().getLocationName());
                }
                if (!Utils.isNullOrEmpty(dbo.getHolidayEventsStartDate())) {
                    dto.setStartDate(dbo.getHolidayEventsStartDate());
                }
                if (!Utils.isNullOrEmpty(dbo.getHolidayEventsEndDate())) {
                    dto.setEndDate(dbo.getHolidayEventsEndDate());
                }
                if (!Utils.isNullOrEmpty(dbo.getHolidayEventsDescription())) {
                    dto.setDescription(dbo.getHolidayEventsDescription());
                }
                dto.setTypes(new ExModelBaseDTO());
                if (!Utils.isNullOrEmpty(dbo.getEmpHolidayEventsTypeName())) {
                    if (dbo.getEmpHolidayEventsTypeName().trim().equalsIgnoreCase("Holiday")) {
                        dto.getTypes().setId("1");
                        dto.getTypes().setText("Holiday");
                    } else if (dbo.getEmpHolidayEventsTypeName().trim().equalsIgnoreCase("Restricted holiday")) {
                        dto.getTypes().setId("2");
                        dto.getTypes().setText("Restricted holiday");
                    } else if (dbo.getEmpHolidayEventsTypeName().trim().equalsIgnoreCase("Event")) {
                        dto.getTypes().setId("3");
                        dto.getTypes().setText("Event");
                    } else if (dbo.getEmpHolidayEventsTypeName().trim().equalsIgnoreCase("Vacation")) {
                        dto.getTypes().setId("4");
                        dto.getTypes().setText("Vacation");
                    }
                }
                holidaysAndEventsEntryDTOList.add(dto);
            });
        }
        return Flux.fromIterable(holidaysAndEventsEntryDTOList);
    }

    public Mono<ApiResult> saveOrUpdate(Mono<HolidaysAndEventsEntryDTO> dto, String userId) {
        return dto
                .handle((holidaysAndEventsEntryDTO, synchronousSink) -> {
                    String error = checkCondtion(holidaysAndEventsEntryDTO);
                    if (!Utils.isNullOrEmpty(error)) {
                        synchronousSink.error(new DuplicateException(error));
                    } else {
                        synchronousSink.next(holidaysAndEventsEntryDTO);
                    }
                }).cast(HolidaysAndEventsEntryDTO.class)
                .map(data -> convertDtoToDbo(data, userId))
                .flatMap(s -> {
                    if (!Utils.isNullOrEmpty(s.getId())) {
                        holidaysAndEventsTransaction.update(s);
                    } else {
                        holidaysAndEventsTransaction.save(s);
                    }
                    return Mono.just(Boolean.TRUE);
                }).map(Utils::responseResult);
    }

    public EmpHolidayEventsDBO convertDtoToDbo(HolidaysAndEventsEntryDTO dto, String userId) {
        EmpHolidayEventsDBO empHolidayEventsDBO = !Utils.isNullOrEmpty(dto.getId()) ? holidaysAndEventsTransaction.searchById(dto.getId()) : new EmpHolidayEventsDBO();
        if (!Utils.isNullOrEmpty(dto.getId())) {
            empHolidayEventsDBO.setModifiedUsersId(Integer.parseInt(userId));
        } else {
            empHolidayEventsDBO.setCreatedUsersId(Integer.parseInt(userId));
            empHolidayEventsDBO.setRecordStatus('A');
        }
        if (!Utils.isNullOrEmpty(dto.getTypes())) {
            if (dto.getTypes().id.trim().equalsIgnoreCase("1")) {
                empHolidayEventsDBO.setHolidayEventsSession("FD");
                empHolidayEventsDBO.setEmpHolidayEventsTypeName("Holiday");
            } else if (dto.getTypes().id.trim().equalsIgnoreCase("2")){
                empHolidayEventsDBO.setHolidayEventsSession("FD");
                empHolidayEventsDBO.setEmpHolidayEventsTypeName("Restricted holiday");
            }
            else if (dto.getTypes().id.trim().equalsIgnoreCase("3"))
                empHolidayEventsDBO.setEmpHolidayEventsTypeName("Event");
            else if (dto.getTypes().id.trim().equalsIgnoreCase("4"))
                empHolidayEventsDBO.setEmpHolidayEventsTypeName("Vacation");
            if (!dto.getTypes().id.trim().equalsIgnoreCase("2")) {
                empHolidayEventsDBO.setSchedulerStatus("pending");
            }
        }
        if (!Utils.isNullOrEmpty(dto.getAcademicYear()) && !Utils.isNullOrEmpty(dto.getAcademicYear().id)) {
            empHolidayEventsDBO.setErpAcademicYearId(new ErpAcademicYearDBO());
            empHolidayEventsDBO.getErpAcademicYearId().setId(Integer.parseInt(dto.getAcademicYear().id));
        }
        if (!Utils.isNullOrEmpty(dto.getLocation()) && !Utils.isNullOrEmpty(dto.getLocation().id)) {
            empHolidayEventsDBO.setErpLocationId(new ErpLocationDBO());
            empHolidayEventsDBO.getErpLocationId().setId(Integer.parseInt(dto.getLocation().id));
        }
        if (!Utils.isNullOrEmpty(dto.getDescription()))
            empHolidayEventsDBO.setHolidayEventsDescription(dto.getDescription());
        if (dto.getTypes().id.trim().equalsIgnoreCase("1") || dto.getTypes().id.trim().equalsIgnoreCase("2")) {
            if (!Utils.isNullOrEmpty(dto.getDate())) {
                empHolidayEventsDBO.setHolidayEventsStartDate(dto.getDate());
                empHolidayEventsDBO.setHolidayEventsEndDate(dto.getDate());
            }
        } else {
            if (!Utils.isNullOrEmpty(dto.getStartDate()))
                empHolidayEventsDBO.setHolidayEventsStartDate(dto.getStartDate());
            if (!Utils.isNullOrEmpty(dto.getEndDate()))
                empHolidayEventsDBO.setHolidayEventsEndDate(dto.getEndDate());
            if (!Utils.isNullOrEmpty(dto.getEmpCategory()) && !Utils.isNullOrEmpty(dto.getEmpCategory().id)) {
                empHolidayEventsDBO.setEmpEmployeeCategoryId(new EmpEmployeeCategoryDBO());
                empHolidayEventsDBO.getEmpEmployeeCategoryId().setId(Integer.parseInt(dto.getEmpCategory().id));
            }
            if (!Utils.isNullOrEmpty(dto.getIsOneTimeSignIn())) {
                if (dto.getIsOneTimeSignIn().trim().equalsIgnoreCase("yes"))
                    empHolidayEventsDBO.setIsOneTimeSigning(true);
                else
                    empHolidayEventsDBO.setIsOneTimeSigning(false);
            }
            if (dto.getIsException() != null) {
                empHolidayEventsDBO.setExemption(dto.getIsException());
            }
            if (!Utils.isNullOrEmpty(dto.getIsFullDay())) {
                if (dto.getIsFullDay().trim().equalsIgnoreCase("FD"))
                    empHolidayEventsDBO.setHolidayEventsSession("FD");
                else if (dto.getIsFullDay().trim().equalsIgnoreCase("AN"))
                    empHolidayEventsDBO.setHolidayEventsSession("AN");
                else if (dto.getIsFullDay().trim().equalsIgnoreCase("FN"))
                    empHolidayEventsDBO.setHolidayEventsSession("FN");
            }
            empHolidayEventsDBO = getEmployeeDept(empHolidayEventsDBO, dto, userId);

        }
        return empHolidayEventsDBO;
    }

    public EmpHolidayEventsDBO getEmployeeDept(EmpHolidayEventsDBO dbo, HolidaysAndEventsEntryDTO dto, String userId) {
        Set<EmpHolidayEventsCddMapDBO> empHolidayEventsCddMapDBOSet = new HashSet<>();
        Set<EmpHolidayEventsEmployeewiseDBO> empHolidayEventsEmployeewiseDBOSet = new HashSet<>();
        //employee
        List<EmpHolidayEventsEmployeewiseDBO> empHolidayEventsEmployeewiseDBOList = !Utils.isNullOrEmpty(dto.getId()) ? holidaysAndEventsTransaction.getEmpHolidayEventsEmployeewiseDBO(Integer.parseInt(dto.getId())) : null;
        Map<Integer, EmpHolidayEventsEmployeewiseDBO> empHolidayEventsEmployeewiseDBOMap = !Utils.isNullOrEmpty(empHolidayEventsEmployeewiseDBOList) ?
                empHolidayEventsEmployeewiseDBOList.stream().filter(s -> s.getRecordStatus() == 'A').collect(Collectors.toMap(s -> s.getEmpDBO().getId(), s -> s)) : null;
        List<Integer> empIds = !Utils.isNullOrEmpty(dto.getEmply()) ? dto.getEmply().stream().map(s -> Integer.parseInt(s.id)).toList() : new ArrayList<>();
        //department
        List<EmpHolidayEventsCddMapDBO> empHolidayEventsCddMapList = !Utils.isNullOrEmpty(dto.getId()) ? holidaysAndEventsTransaction.getEmpHolidayEventsCddMap(Integer.parseInt(dto.getId())) : null;
        Map<Integer, EmpHolidayEventsCddMapDBO> empHolidayEventsCddMapDBOMap = !Utils.isNullOrEmpty(empHolidayEventsCddMapList)
                ? empHolidayEventsCddMapList.stream()
                .filter(s -> s.getRecordStatus() == 'A' && !Utils.isNullOrEmpty(s.getErpCampusDeaneryDeptId()))
                .collect(Collectors.toMap(s -> s.getErpCampusDeaneryDeptId().getId(), s -> s))
                : null;
        //get department of a selected employees
        List<Integer> empCampusDepartmentMappingList = !Utils.isNullOrEmpty(empIds) ? holidaysAndEventsTransaction.getEmpCampusDepartmentMappingList(new HashSet<>(empIds)) : null;
        Set<Integer> empCampusDepartmentIds = !Utils.isNullOrEmpty(empCampusDepartmentMappingList) ? new HashSet<>(empCampusDepartmentMappingList) : null;
        //department from front end
        Set<Integer> campusDeanearyDeptIds = new HashSet<>();
        if (!Utils.isNullOrEmpty(dto.getChecked())) {
            for (String ids : dto.getChecked()) {
                if (!Utils.isNullOrEmpty(ids)) {
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int i = 0; i < ids.length(); i++) {
                        char ch = ids.charAt(i);
                        if (ch == '-') {
                            break;
                        }
                        stringBuffer.append(ch);
                    }
                    if (!Utils.isNullOrEmpty(stringBuffer)) {
                        campusDeanearyDeptIds.add(Integer.valueOf(stringBuffer.toString().trim()));
                    }
                }
            }
        }
        //campusDepartment
        if (!Utils.isNullOrEmpty(empHolidayEventsCddMapDBOMap)) {
            if (dto.getIsEmployeeWise()) {
                if (!Utils.isNullOrEmpty(empCampusDepartmentIds)) {
                    empCampusDepartmentIds.forEach(camDeptId -> {
                        EmpHolidayEventsCddMapDBO empHolidayEventsCddMapDBO;
                        if (empHolidayEventsCddMapDBOMap.containsKey(camDeptId)) {
                            empHolidayEventsCddMapDBO = empHolidayEventsCddMapDBOMap.get(camDeptId);
                            empHolidayEventsCddMapDBO.setModifiedUsersId(Integer.parseInt(userId));
                            empHolidayEventsCddMapDBO.setErpCampusDeaneryDeptId(new ErpCampusDepartmentMappingDBO());
                            empHolidayEventsCddMapDBO.getErpCampusDeaneryDeptId().setId(camDeptId);
                            empHolidayEventsCddMapDBO.setEmpHolidayEventsId(dbo);
                            empHolidayEventsCddMapDBOMap.remove(camDeptId);
                            empHolidayEventsCddMapDBOSet.add(empHolidayEventsCddMapDBO);
                        } else {
                            empHolidayEventsCddMapDBO = new EmpHolidayEventsCddMapDBO();
                            empHolidayEventsCddMapDBO.setCreatedUsersId(Integer.parseInt(userId));
                            empHolidayEventsCddMapDBO.setRecordStatus('A');
                            empHolidayEventsCddMapDBO.setErpCampusDeaneryDeptId(new ErpCampusDepartmentMappingDBO());
                            empHolidayEventsCddMapDBO.setEmpHolidayEventsId(dbo);
                            empHolidayEventsCddMapDBO.getErpCampusDeaneryDeptId().setId(camDeptId);
                            empHolidayEventsCddMapDBOSet.add(empHolidayEventsCddMapDBO);
                        }
                    });
                }
            } else {
                if (!Utils.isNullOrEmpty(campusDeanearyDeptIds)) {
                    campusDeanearyDeptIds.forEach(camDeptId -> {
                        EmpHolidayEventsCddMapDBO empHolidayEventsCddMapDBO;
                        if (empHolidayEventsCddMapDBOMap.containsKey(camDeptId)) {
                            empHolidayEventsCddMapDBO = empHolidayEventsCddMapDBOMap.get(camDeptId);
                            empHolidayEventsCddMapDBO.setCreatedUsersId(Integer.parseInt(userId));
                            empHolidayEventsCddMapDBO.setRecordStatus('A');
                            empHolidayEventsCddMapDBO.setEmpHolidayEventsId(dbo);
                            empHolidayEventsCddMapDBO.setErpCampusDeaneryDeptId(new ErpCampusDepartmentMappingDBO());
                            empHolidayEventsCddMapDBO.getErpCampusDeaneryDeptId().setId(camDeptId);
                            empHolidayEventsCddMapDBOSet.add(empHolidayEventsCddMapDBO);
                            empHolidayEventsCddMapDBOMap.remove(camDeptId);
                        } else {
                            empHolidayEventsCddMapDBO = new EmpHolidayEventsCddMapDBO();
                            empHolidayEventsCddMapDBO.setCreatedUsersId(Integer.parseInt(userId));
                            empHolidayEventsCddMapDBO.setRecordStatus('A');
                            empHolidayEventsCddMapDBO.setErpCampusDeaneryDeptId(new ErpCampusDepartmentMappingDBO());
                            empHolidayEventsCddMapDBO.getErpCampusDeaneryDeptId().setId(camDeptId);
                            empHolidayEventsCddMapDBO.setEmpHolidayEventsId(dbo);
                            empHolidayEventsCddMapDBOSet.add(empHolidayEventsCddMapDBO);
                        }
                    });
                }
            }
            empHolidayEventsCddMapDBOMap.forEach((k, v) -> {
                v.setModifiedUsersId(Integer.parseInt(userId));
                v.setRecordStatus('D');
                empHolidayEventsCddMapDBOSet.add(v);
            });
            if (!Utils.isNullOrEmpty(empHolidayEventsCddMapDBOSet))
                dbo.setEmpHolidayEventsCddMapDBOSet(empHolidayEventsCddMapDBOSet);
        } else {
            if (dto.getIsEmployeeWise()) {
                if (!Utils.isNullOrEmpty(empCampusDepartmentIds)) {
                    empCampusDepartmentIds.forEach(camDept -> {
                        EmpHolidayEventsCddMapDBO empHolidayEventsCddMapDBO = new EmpHolidayEventsCddMapDBO();
                        empHolidayEventsCddMapDBO.setErpCampusDeaneryDeptId(new ErpCampusDepartmentMappingDBO());
                        empHolidayEventsCddMapDBO.getErpCampusDeaneryDeptId().setId(camDept);
                        empHolidayEventsCddMapDBO.setEmpHolidayEventsId(dbo);
                        empHolidayEventsCddMapDBO.setCreatedUsersId(Integer.parseInt(userId));
                        empHolidayEventsCddMapDBO.setRecordStatus('A');
                        empHolidayEventsCddMapDBOSet.add(empHolidayEventsCddMapDBO);
                    });
                }
            } else {
                if (!Utils.isNullOrEmpty(campusDeanearyDeptIds)) {
                    campusDeanearyDeptIds.forEach(camDept -> {
                        EmpHolidayEventsCddMapDBO empHolidayEventsCddMapDBO = new EmpHolidayEventsCddMapDBO();
                        empHolidayEventsCddMapDBO.setErpCampusDeaneryDeptId(new ErpCampusDepartmentMappingDBO());
                        empHolidayEventsCddMapDBO.getErpCampusDeaneryDeptId().setId(camDept);
                        empHolidayEventsCddMapDBO.setEmpHolidayEventsId(dbo);
                        empHolidayEventsCddMapDBO.setCreatedUsersId(Integer.parseInt(userId));
                        empHolidayEventsCddMapDBO.setRecordStatus('A');
                        empHolidayEventsCddMapDBOSet.add(empHolidayEventsCddMapDBO);
                    });
                }
            }
            if (!Utils.isNullOrEmpty(empHolidayEventsCddMapDBOSet))
                dbo.setEmpHolidayEventsCddMapDBOSet(empHolidayEventsCddMapDBOSet);
        }

        //employeeWise
        if (!Utils.isNullOrEmpty(dto.getIsEmployeeWise())) {
            if (!Utils.isNullOrEmpty(empHolidayEventsEmployeewiseDBOMap)) {
                if (dto.getIsEmployeeWise()) {
                    dbo.setIsEmployeewiseExemption(true);
                    if (!Utils.isNullOrEmpty(empIds)) {
                        empIds.forEach(emp -> {
                            EmpHolidayEventsEmployeewiseDBO empHolidayEventsEmployeewiseDBO;
                            if (empHolidayEventsEmployeewiseDBOMap.containsKey(emp)) {
                                empHolidayEventsEmployeewiseDBO = empHolidayEventsEmployeewiseDBOMap.get(emp);
                                empHolidayEventsEmployeewiseDBO.setModifiedUsersId(Integer.parseInt(userId));
                                empHolidayEventsEmployeewiseDBOMap.remove(emp);
                                empHolidayEventsEmployeewiseDBO.setEmpHolidayEventsDBO(dbo);
                                empHolidayEventsEmployeewiseDBOSet.add(empHolidayEventsEmployeewiseDBO);
                            } else {
                                empHolidayEventsEmployeewiseDBO = new EmpHolidayEventsEmployeewiseDBO();
                                empHolidayEventsEmployeewiseDBO.setEmpDBO(new EmpDBO());
                                empHolidayEventsEmployeewiseDBO.getEmpDBO().setId(emp);
                                empHolidayEventsEmployeewiseDBO.setEmpHolidayEventsDBO(dbo);
                                empHolidayEventsEmployeewiseDBO.setCreatedUsersId(Integer.parseInt(userId));
                                empHolidayEventsEmployeewiseDBO.setRecordStatus('A');
                                empHolidayEventsEmployeewiseDBOSet.add(empHolidayEventsEmployeewiseDBO);
                            }
                        });
                    }
                } else {
                    dbo.setIsEmployeewiseExemption(false);
                }
                empHolidayEventsEmployeewiseDBOMap.forEach((k, v) -> {
                    v.setRecordStatus('D');
                    v.setModifiedUsersId(Integer.parseInt(userId));
                    empHolidayEventsEmployeewiseDBOSet.add(v);
                });
                if (!Utils.isNullOrEmpty(empHolidayEventsEmployeewiseDBOSet)) {
                    dbo.setEmpHolidayEventsEmployeewiseDBOSet(empHolidayEventsEmployeewiseDBOSet);
                }
            } else if (Utils.isNullOrEmpty(empHolidayEventsEmployeewiseDBOMap)) {
                if (dto.getIsEmployeeWise()) {
                    dbo.setIsEmployeewiseExemption(true);
                    Set<Integer> empList = new HashSet<>();
                    if (!Utils.isNullOrEmpty(dto.getEmply())) {
                        dto.getEmply().forEach(data -> {
                            EmpHolidayEventsEmployeewiseDBO empHolidayEventsEmployeewiseDBO = new EmpHolidayEventsEmployeewiseDBO();
                            empHolidayEventsEmployeewiseDBO.setEmpDBO(new EmpDBO());
                            empHolidayEventsEmployeewiseDBO.getEmpDBO().setId(Integer.parseInt(data.id));
                            empList.add(Integer.parseInt(data.id));
                            empHolidayEventsEmployeewiseDBO.setEmpHolidayEventsDBO(dbo);
                            empHolidayEventsEmployeewiseDBO.setCreatedUsersId(Integer.parseInt(userId));
                            empHolidayEventsEmployeewiseDBO.setRecordStatus('A');
                            empHolidayEventsEmployeewiseDBOSet.add(empHolidayEventsEmployeewiseDBO);
                        });
                        if (!Utils.isNullOrEmpty(empHolidayEventsEmployeewiseDBOSet)) {
                            dbo.setEmpHolidayEventsEmployeewiseDBOSet(empHolidayEventsEmployeewiseDBOSet);
                        }
                    }
                } else {
                    dbo.setIsEmployeewiseExemption(false);
                }
            }
        }
        return dbo;
    }

    public Mono<HolidaysAndEventsEntryDTO> edit(int id) {
        Tuple holidayEventTuple = holidaysAndEventsTransaction.edit(id);
        return convertDBOToDto(holidayEventTuple);
    }

    private Mono<HolidaysAndEventsEntryDTO> convertDBOToDto(Tuple holidayEventTuple) {
        HolidaysAndEventsEntryDTO holidaysAndEventsEntryDTO = new HolidaysAndEventsEntryDTO();
        if (!Utils.isNullOrEmpty(holidayEventTuple)) {
            if (!Utils.isNullOrEmpty(holidayEventTuple.get("id")))
                holidaysAndEventsEntryDTO.setId(String.valueOf(holidayEventTuple.get("id")));
            holidaysAndEventsEntryDTO.setIsEdit(true);
            holidaysAndEventsEntryDTO.setAcademicYear(new ExModelBaseDTO());
            if (!Utils.isNullOrEmpty(holidayEventTuple.get("holidayAcademicYearId")) && !Utils.isNullOrEmpty(holidayEventTuple.get("academicYearName"))) {
                holidaysAndEventsEntryDTO.getAcademicYear().setId(String.valueOf(holidayEventTuple.get("holidayAcademicYearId")));
                holidaysAndEventsEntryDTO.getAcademicYear().setTag(String.valueOf(holidayEventTuple.get("academicYearName")));
            }
            holidaysAndEventsEntryDTO.setLocation(new ExModelBaseDTO());
            if (!Utils.isNullOrEmpty(holidayEventTuple.get("locationId")) && !Utils.isNullOrEmpty(holidayEventTuple.get("locationName"))) {
                holidaysAndEventsEntryDTO.getLocation().setId(String.valueOf(holidayEventTuple.get("locationId")));
                holidaysAndEventsEntryDTO.getLocation().setTag(String.valueOf(holidayEventTuple.get("locationName")));
            }
            if (!Utils.isNullOrEmpty(holidayEventTuple.get("holidayEventDescription")))
                holidaysAndEventsEntryDTO.setDescription(String.valueOf(holidayEventTuple.get("holidayEventDescription")));
            if (!Utils.isNullOrEmpty(holidayEventTuple.get("empHolidayEventTypeName"))) {
                if (String.valueOf(holidayEventTuple.get("empHolidayEventTypeName")).trim().equalsIgnoreCase("Holiday") || String.valueOf(holidayEventTuple.get("empHolidayEventTypeName")).trim().equalsIgnoreCase("Restricted holiday")) {
                    if (String.valueOf(holidayEventTuple.get("empHolidayEventTypeName")).trim().equalsIgnoreCase("Holiday")) {
                        holidaysAndEventsEntryDTO.setTypes(new ExModelBaseDTO());
                        holidaysAndEventsEntryDTO.getTypes().setId("1");
                    }
                    if (String.valueOf(holidayEventTuple.get("empHolidayEventTypeName")).trim().equalsIgnoreCase("Restricted holiday")) {
                        holidaysAndEventsEntryDTO.setTypes(new ExModelBaseDTO());
                        holidaysAndEventsEntryDTO.getTypes().setId("2");
                    }
                    holidaysAndEventsEntryDTO.setIsHolidayOrEvent(false);
                    holidaysAndEventsEntryDTO.setDate(Utils.convertStringDateToLocalDate(String.valueOf(holidayEventTuple.get("holiayEventStartDate"))));
                } else {
                    holidaysAndEventsEntryDTO.setIsHolidayOrEvent(true);
                    holidaysAndEventsEntryDTO.setStartDate(Utils.convertStringDateToLocalDate(String.valueOf(holidayEventTuple.get("holiayEventStartDate"))));
                    holidaysAndEventsEntryDTO.setEndDate(Utils.convertStringDateToLocalDate(String.valueOf(holidayEventTuple.get("holidayEventEndDate"))));
                    holidaysAndEventsEntryDTO.setEmpCategory(new ExModelBaseDTO());
                    if (!Utils.isNullOrEmpty(holidayEventTuple.get("empCategoryId")) && !Utils.isNullOrEmpty(holidayEventTuple.get("employeCategoryName"))) {
                        holidaysAndEventsEntryDTO.getEmpCategory().setId(String.valueOf(holidayEventTuple.get("empCategoryId")));
                        holidaysAndEventsEntryDTO.getEmpCategory().setText(String.valueOf(holidayEventTuple.get("employeCategoryName")));
                    }
                    if (!Utils.isNullOrEmpty(holidayEventTuple.get("empHolidayEventTypeName"))) {
                        if (String.valueOf(holidayEventTuple.get("empHolidayEventTypeName")).trim().equalsIgnoreCase("Event")) {
                            holidaysAndEventsEntryDTO.setTypes(new ExModelBaseDTO());
                            holidaysAndEventsEntryDTO.getTypes().setId("3");
                        } else if (String.valueOf(holidayEventTuple.get("empHolidayEventTypeName")).trim().equalsIgnoreCase("Vacation")) {
                            holidaysAndEventsEntryDTO.setTypes(new ExModelBaseDTO());
                            holidaysAndEventsEntryDTO.getTypes().setId("4");
                        }
                    }
                    holidaysAndEventsEntryDTO.setEmpCategory(new ExModelBaseDTO());
                    if (!Utils.isNullOrEmpty(String.valueOf(holidayEventTuple.get("empCategoryId"))))
                        holidaysAndEventsEntryDTO.getEmpCategory().setId(String.valueOf(holidayEventTuple.get("empCategoryId")));
                    if (!Utils.isNullOrEmpty(holidayEventTuple.get("isEmployeeWiseException"))) {
                        if (String.valueOf(holidayEventTuple.get("isEmployeeWiseException")).trim().equalsIgnoreCase("1"))
                            holidaysAndEventsEntryDTO.setIsEmployeeWise(true);
                        else
                            holidaysAndEventsEntryDTO.setIsEmployeeWise(false);
                    }
                    if (!Utils.isNullOrEmpty(holidayEventTuple.get("isOneTimeSign"))) {
                        if (String.valueOf(holidayEventTuple.get("isOneTimeSign")).trim().equalsIgnoreCase("1")) {
                            holidaysAndEventsEntryDTO.setIsOneTimeSignIn("yes");
                        } else
                            holidaysAndEventsEntryDTO.setIsOneTimeSignIn("no");
                    }
                    if (!Utils.isNullOrEmpty(holidayEventTuple.get("isException"))) {
                        if (String.valueOf(holidayEventTuple.get("isException")).trim().equalsIgnoreCase("1")) {
                            holidaysAndEventsEntryDTO.setIsException(true);
                        } else {
                            holidaysAndEventsEntryDTO.setIsException(false);
                        }
                    }
                    if (!Utils.isNullOrEmpty(holidayEventTuple.get("holidayEventsSession"))) {
                        if (String.valueOf(holidayEventTuple.get("holidayEventsSession")).trim().equalsIgnoreCase("FD"))
                            holidaysAndEventsEntryDTO.setIsFullDay("FD");
                        else if (String.valueOf(holidayEventTuple.get("holidayEventsSession")).trim().equalsIgnoreCase("FN"))
                            holidaysAndEventsEntryDTO.setIsFullDay("FN");
                        else if (String.valueOf(holidayEventTuple.get("holidayEventsSession")).trim().equalsIgnoreCase("AN"))
                            holidaysAndEventsEntryDTO.setIsFullDay("AN");
                    }
                    if (!Utils.isNullOrEmpty(!Utils.isNullOrEmpty(holidayEventTuple.get("isEmployeeWiseException")))) {
                        if (String.valueOf(holidayEventTuple.get("isEmployeeWiseException")).trim().equalsIgnoreCase("1")) {
                            List<Tuple> empHolidayEventsEmployeewiseTuple = holidaysAndEventsTransaction.getEmployeeIdByLoc(Integer.parseInt(String.valueOf(holidayEventTuple.get("id"))), Integer.parseInt(String.valueOf(holidayEventTuple.get("locationId"))));
                            if (!Utils.isNullOrEmpty(empHolidayEventsEmployeewiseTuple)) {
                                holidaysAndEventsEntryDTO.setEmply(new ArrayList<ExModelBaseDTO>());
                                empHolidayEventsEmployeewiseTuple.forEach(empwiseBo -> {
                                    if (!Utils.isNullOrEmpty(empwiseBo.get("empId")) && !Utils.isNullOrEmpty(empwiseBo.get("empName"))) {
                                        ExModelBaseDTO exModelBaseDTO = new ExModelBaseDTO();
                                        exModelBaseDTO.setId(String.valueOf(empwiseBo.get("empId")));
                                        exModelBaseDTO.setTag(String.valueOf(empwiseBo.get("empName")));
                                        holidaysAndEventsEntryDTO.getEmply().add(exModelBaseDTO);
                                    }
                                });
                            }
                        }

                    }
                    ArrayList<String> arrayChecked = new ArrayList<>();
                    List<Tuple> empHolidayEventsCddMapTuple = holidaysAndEventsTransaction.getCddList(Integer.parseInt(String.valueOf(holidayEventTuple.get("id"))));
                    if (!Utils.isNullOrEmpty(empHolidayEventsCddMapTuple)) {
                        empHolidayEventsCddMapTuple.forEach(cddMap -> {
                            String chekedId = "";
                            if (!Utils.isNullOrEmpty(cddMap.get("campusDepartmentId")) && !Utils.isNullOrEmpty(cddMap.get("campusId")) && !Utils.isNullOrEmpty(cddMap.get("deanaryId")) && !Utils.isNullOrEmpty(cddMap.get("departmentId"))) {
                                chekedId = String.valueOf(cddMap.get("campusDepartmentId")) + "-" + String.valueOf(cddMap.get("campusId")) + "-" + String.valueOf(cddMap.get("deanaryId")) + "-" + String.valueOf(cddMap.get("departmentId"));
                                if (!Utils.isNullOrEmpty(chekedId))
                                    arrayChecked.add(chekedId);
                            }
                        });
                        if (!Utils.isNullOrEmpty(arrayChecked)) {
                            String[] checkedArray = Utils.GetStringArray(arrayChecked);
                            holidaysAndEventsEntryDTO.setChecked(checkedArray);
                        }
                    }
                }
            }
        }
        return Mono.just(holidaysAndEventsEntryDTO);
    }

    public Mono<ApiResult> delete(String id, String userId) {
        return holidaysAndEventsTransaction.delete(Integer.parseInt(id), Integer.parseInt(userId)).map(Utils::responseResult);
    }

    public Flux<EmployeeApplicationDTO> getEmployeeByIdsByEmpCategory(HolidaysAndEventsEntryDTO data) {
        List<EmployeeApplicationDTO> employeeApplicationDTOList = new ArrayList<>();
        Set<Integer> campusDeanearyDeptIds = new HashSet<>();
        if (!Utils.isNullOrEmpty(data.getChecked())) {
            for (String ids : data.getChecked()) {
                if (ids != null && !ids.isEmpty()) {
                    StringBuffer id = new StringBuffer();
                    for (int i = 0; i < ids.length(); i++) {
                        char ch = ids.charAt(i);
                        if (ch == '-') {
                            break;
                        }
                        id.append(ch);
                    }
                    if (!Utils.isNullOrEmpty(id)) {
                        campusDeanearyDeptIds.add(Integer.valueOf(id.toString().trim()));
                    }
                }
            }
        }
        List<Tuple> empTupleList = holidaysAndEventsTransaction.getEmpDetails(data, campusDeanearyDeptIds);
        if (!Utils.isNullOrEmpty(empTupleList)) {
            empTupleList.forEach(emp -> {
                if (!Utils.isNullOrEmpty(emp.get("empId")) && !Utils.isNullOrEmpty(emp.get("empName"))) {
                    EmployeeApplicationDTO employeeApplicationDTO = new EmployeeApplicationDTO();
                    employeeApplicationDTO.setValue(String.valueOf(emp.get("empId")));
                    employeeApplicationDTO.setLabel(String.valueOf(emp.get("empName")));
                    employeeApplicationDTOList.add(employeeApplicationDTO);
                }
            });
        }
        return Flux.fromIterable(employeeApplicationDTOList);
    }

    public String checkCondtion(HolidaysAndEventsEntryDTO holidaysAndEventsEntryDTO) {
        boolean isTrue = false;
        String types = null;
        String error = null;
        List<EmpHolidayEventsDBO> list = null;
        if (holidaysAndEventsEntryDTO.getTypes().id.equalsIgnoreCase("1"))
            types = "Holiday";
        else if (holidaysAndEventsEntryDTO.getTypes().id.equalsIgnoreCase("2"))
            types = "Restricted holiday";
        else if (holidaysAndEventsEntryDTO.getTypes().id.equalsIgnoreCase("3"))
            types = "Event";
        else if (holidaysAndEventsEntryDTO.getTypes().id.equalsIgnoreCase("4"))
            types = "Vacation";
        Set<Integer> empList = new HashSet<>();
        Set<Integer> campusDeptIds = new HashSet<>();
        boolean isDuplicate = false;
        if (holidaysAndEventsEntryDTO.getTypes().id.trim().equalsIgnoreCase("1") || holidaysAndEventsEntryDTO.getTypes().id.trim().equalsIgnoreCase("2")) {
            list = holidaysAndEventsTransaction.getDuplicate(holidaysAndEventsEntryDTO);
            isDuplicate = true;
            isTrue = true;
        } else {
            Set<Integer> campusDeanearyDeptIds = new HashSet<>();
            if (!Utils.isNullOrEmpty(holidaysAndEventsEntryDTO.getChecked())) {
                for (String ids : holidaysAndEventsEntryDTO.getChecked()) {
                    if (!Utils.isNullOrEmpty(ids)) {
                        StringBuffer stringBuffer = new StringBuffer();
                        for (int i = 0; i < ids.length(); i++) {
                            char ch = ids.charAt(i);
                            if (ch == '-') {
                                break;
                            }
                            stringBuffer.append(ch);
                        }
                        if (!Utils.isNullOrEmpty(stringBuffer)) {
                            campusDeanearyDeptIds.add(Integer.valueOf(stringBuffer.toString().trim()));
                        }
                    }
                }
            }
            if (!Utils.isNullOrEmpty(holidaysAndEventsEntryDTO.getEmply())) {
                holidaysAndEventsEntryDTO.getEmply().forEach(data -> {
                    if (!Utils.isNullOrEmpty(data.id)) {
                        empList.add(Integer.parseInt(data.id));
                    }
                });
            }
            if(!Utils.isNullOrEmpty(empList))
                 campusDeptIds = new HashSet<>(holidaysAndEventsTransaction.getEmpCampusDepartmentMappingList(empList));
            if (!Utils.isNullOrEmpty(holidaysAndEventsEntryDTO.getIsEmployeeWise())) {
                if (holidaysAndEventsEntryDTO.getIsEmployeeWise() && !Utils.isNullOrEmpty(empList)) {
                      list = holidaysAndEventsTransaction.getDuplicateForEmployee(holidaysAndEventsEntryDTO,campusDeptIds.stream().toList());
                      if(!Utils.isNullOrEmpty(list)){
                          isTrue = true;
                      }else {
                          list = holidaysAndEventsTransaction.getDuplicateforEmployeeWiseYes(holidaysAndEventsEntryDTO, empList.stream().toList());
                      }
                } else {
                    list = holidaysAndEventsTransaction.getDuplicateforEmployeeWiseNo(holidaysAndEventsEntryDTO, campusDeanearyDeptIds);
                }
            }
            isTrue = Utils.isNullOrEmpty(list) ? false : true;
        }
        if (isTrue && isDuplicate) {
            if(!Utils.isNullOrEmpty(list)){
                AtomicReference<String> type = new AtomicReference<>();
                list.forEach(data -> {
                    if(!Utils.isNullOrEmpty(data.getEmpHolidayEventsTypeName()))
                        type.set(data.getEmpHolidayEventsTypeName());
                });
                error = type.get() + " details are Repeated/Duplicated ";
            }
        } else if (isTrue && !Utils.isNullOrEmpty(list) && Utils.isNullOrEmpty(empList)) {
            Set<String> departmentList = list.stream()
                    .filter(s -> !Utils.isNullOrEmpty(s.getEmpHolidayEventsCddMapDBOSet()))
                    .flatMap(s -> s.getEmpHolidayEventsCddMapDBOSet().stream())
                    .filter(p -> (p.getRecordStatus() == 'A') && p.getErpCampusDeaneryDeptId().getErpDepartmentDBO() != null)
                    .map(p -> p.getErpCampusDeaneryDeptId().getErpDepartmentDBO().getDepartmentName())
                    .collect(Collectors.toSet());
            String departmentString = IntStream.range(0, departmentList.stream().toList().size())
                    .mapToObj(i -> (i + 1) + ". " + departmentList.stream().toList().get(i))
                    .collect(Collectors.joining(System.lineSeparator()));
            error = types + " already created for below department " + departmentString;
        } else if (isTrue && !Utils.isNullOrEmpty(list) && !Utils.isNullOrEmpty(empList)) {
            Set<String> departmentList = list.stream()
                    .filter(s -> !Utils.isNullOrEmpty(s.getEmpHolidayEventsEmployeewiseDBOSet()))
                    .flatMap(s -> s.getEmpHolidayEventsEmployeewiseDBOSet().stream())
                    .filter(p -> (p.getRecordStatus() == 'A') && p.getEmpDBO().getErpCampusDepartmentMappingDBO() != null)
                    .map(p -> p.getEmpDBO().getErpCampusDepartmentMappingDBO().getErpDepartmentDBO().getDepartmentName())
                    .collect(Collectors.toSet());
            String departmentString = IntStream.range(0, departmentList.stream().toList().size())
                    .mapToObj(i -> (i + 1) + ". " + departmentList.stream().toList().get(i))
                    .collect(Collectors.joining(System.lineSeparator()));
            error = types + " already created for below department " + departmentString;
        }
        return error;
    }
}
