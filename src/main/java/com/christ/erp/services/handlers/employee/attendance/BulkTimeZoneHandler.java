package com.christ.erp.services.handlers.employee.attendance;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.attendance.EmpDatewiseTimeZoneDBO;
import com.christ.erp.services.dbobjects.employee.attendance.EmpTimeZoneDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpJobDetailsDBO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.attendance.EmpDatewiseTimeZoneDTO;
import com.christ.erp.services.dto.employee.attendance.EmpTimeZoneDTO;
import com.christ.erp.services.dto.employee.attendance.HolidaysAndEventsEntryDTO;
import com.christ.erp.services.dto.employee.common.EmployeeApplicationDTO;
import com.christ.erp.services.exception.DuplicateException;
import com.christ.erp.services.transactions.employee.attendance.BulkTimeZoneTransation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.persistence.Tuple;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class BulkTimeZoneHandler {

    @Autowired
    BulkTimeZoneTransation bulkTimeZoneTransation;

    public Flux<EmpDatewiseTimeZoneDTO> getData() {
        List<EmpDatewiseTimeZoneDTO> empDatewiseTimeZoneDTOS = bulkTimeZoneTransation.getData();
        if (!Utils.isNullOrEmpty(empDatewiseTimeZoneDTOS)) {
            return Flux.fromIterable(empDatewiseTimeZoneDTOS);
        } else {
            return Flux.empty();
        }
    }

    public Mono<EmpDatewiseTimeZoneDTO> edit(int id) {
        EmpDatewiseTimeZoneDTO empDatewiseTimeZoneDTO = bulkTimeZoneTransation.edit(id);
        if (!Utils.isNullOrEmpty(empDatewiseTimeZoneDTO)) {
            ArrayList<String> arrayChecked = new ArrayList<>();
            if (!Utils.isNullOrEmpty(empDatewiseTimeZoneDTO.getLocationSelect().getValue()) && !Utils.isNullOrEmpty(empDatewiseTimeZoneDTO.getCampusDeptId()) &&
                    !Utils.isNullOrEmpty(empDatewiseTimeZoneDTO.getCampusId()) && !Utils.isNullOrEmpty(empDatewiseTimeZoneDTO.getDeptId()) && !Utils.isNullOrEmpty(empDatewiseTimeZoneDTO.getDenearyId())) {
                String chekedId = "";
                chekedId = String.valueOf(empDatewiseTimeZoneDTO.getCampusDeptId()) + "-" + String.valueOf(empDatewiseTimeZoneDTO.getCampusId()) + "-" + String.valueOf(empDatewiseTimeZoneDTO.getDenearyId()) + "-" + String.valueOf(empDatewiseTimeZoneDTO.getDeptId());
                if (!Utils.isNullOrEmpty(chekedId))
                    arrayChecked.add(chekedId);
            }
            if (!Utils.isNullOrEmpty(arrayChecked)) {
                String[] checkedArray = Utils.GetStringArray(arrayChecked);
                empDatewiseTimeZoneDTO.setChecked(checkedArray);
            }
            return Mono.just(empDatewiseTimeZoneDTO);
        } else {
            return Mono.empty();
        }
    }

    public Mono<ApiResult> delete(int id, String userId) {
        return bulkTimeZoneTransation.delete(id, Integer.parseInt(userId)).map(Utils::responseResult);
    }

    public Mono<ApiResult> saveOrUpdate(Mono<EmpDatewiseTimeZoneDTO> dto, String userId) {
        return dto
                .handle((empDatewiseTimeZoneDTO, synchronousSink) -> {
                    Set<Integer> empIds = new HashSet<>();
                    Set<Integer> campusDeanearyDeptIds = new HashSet<>();
                    if (!Utils.isNullOrEmpty(empDatewiseTimeZoneDTO.getEmpSelect())) {
                        Set<Integer> finalEmpIds = empIds;
                        empDatewiseTimeZoneDTO.getEmpSelect().forEach(emp -> {
                            if (!Utils.isNullOrEmpty(emp.getValue()))
                                finalEmpIds.add(Integer.parseInt(emp.getValue()));
                        });
                        empIds = finalEmpIds;
                    } else {
                        if (!Utils.isNullOrEmpty(empDatewiseTimeZoneDTO.getChecked())) {
                            for (String ids : empDatewiseTimeZoneDTO.getChecked()) {
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
                    }
                    Set<Integer> empCampusDept = null;
                    if (!Utils.isNullOrEmpty(empIds)) {
                        empCampusDept = new HashSet<>(bulkTimeZoneTransation.getEmpCampusDepartmentMappingList(empIds));
                    } else {
                        empCampusDept = campusDeanearyDeptIds;
                        List<Integer> empIds1 = bulkTimeZoneTransation.getEmployee(empCampusDept.stream().toList());
                        empIds = !Utils.isNullOrEmpty(empIds1) ? empIds1.stream().collect(Collectors.toSet()) : null;
                        if (!Utils.isNullOrEmpty(empIds))
                            empDatewiseTimeZoneDTO.setEmpIds(empIds);
                    }
                    List<EmpDatewiseTimeZoneDBO> list = null;
                    if (!Utils.isNullOrEmpty(empDatewiseTimeZoneDTO.getTimeZoneStartDate()) && !Utils.isNullOrEmpty(empDatewiseTimeZoneDTO.getTimeZoneEndDate()) && !Utils.isNullOrEmpty(empIds)) {
                        list = bulkTimeZoneTransation.duplicateCheck1(empDatewiseTimeZoneDTO, empIds.stream().toList());
                    }
                    if (!Utils.isNullOrEmpty(list)) {
                        Set<String> departmentList = list.stream()
                                .filter(s -> !Utils.isNullOrEmpty(s.getEmpDBO()) && !Utils.isNullOrEmpty(s.getEmpDBO().getErpCampusDepartmentMappingDBO()) && s.getRecordStatus() == 'A')
                                .map(p -> p.getEmpDBO().getErpCampusDepartmentMappingDBO().getErpDepartmentDBO().getDepartmentName())
                                .collect(Collectors.toSet());
                        String departmentString = IntStream.range(0, departmentList.size())
                                .mapToObj(i -> (i + 1) + ". " + departmentList.stream().toList().get(i))
                                .collect(Collectors.joining(System.lineSeparator()));
                        String error = "Duplicate entry " + " already created for below department " + departmentString;
                        synchronousSink.error(new DuplicateException(error));
                    } else {
                        synchronousSink.next(empDatewiseTimeZoneDTO);
                    }
                }).cast(EmpDatewiseTimeZoneDTO.class)
                .map(data -> convertDtoToDbo(data, userId))
                .flatMap(s -> Mono.just(Boolean.TRUE)).map(Utils::responseResult);
    }

    private boolean convertDtoToDbo(EmpDatewiseTimeZoneDTO dto, String userId) {
        boolean isTrue = false;
        List<EmpDatewiseTimeZoneDBO> empDatewiseTimeZoneDBOS = new ArrayList<>();
        List<EmpDBO> empDBOS = new ArrayList<>();
        if (!Utils.isNullOrEmpty(dto.getEmpSelect())) {
            Set<Integer> empIds = !Utils.isNullOrEmpty(dto.getEmpSelect()) ? dto.getEmpSelect().stream().filter(s -> !Utils.isNullOrEmpty(s.getValue())).map(selectDTO -> Integer.parseInt(selectDTO.getValue())).collect(Collectors.toSet()) : null;
            List<EmpDBO> empDBOList = !Utils.isNullOrEmpty(empIds) ? bulkTimeZoneTransation.getEmp(empIds.stream().toList()) : null;
            Map<Integer, EmpDBO> empMap = !Utils.isNullOrEmpty(empDBOList) ? empDBOList.stream().collect(Collectors.toMap(s -> s.getId(), s -> s)) : new HashMap<>();
            dto.getEmpSelect().forEach(emp -> {
                if (!Utils.isNullOrEmpty(dto.getTimeZoneStartDate()) && !Utils.isNullOrEmpty(dto.getTimeZoneEndDate())) {
                    EmpDatewiseTimeZoneDBO dbo = null;
                    if (Utils.isNullOrEmpty(dto.getId())) {
                        dbo = new EmpDatewiseTimeZoneDBO();
                        dbo.setCreatedUsersId(Integer.parseInt(userId));
                    } else {
                        dbo = bulkTimeZoneTransation.existData(dto.getId());
                        dbo.setModifiedUsersId(Integer.parseInt(userId));
                    }
                    dbo.setCreatedUsersId(Integer.parseInt(userId));
                    dbo.setRecordStatus('A');
                    if (!Utils.isNullOrEmpty(dto.getDescription()))
                        dbo.setDescription(dto.getDescription());
                    if (!Utils.isNullOrEmpty(dto.getEmpTimeZoneSelect())) {
                        if (!Utils.isNullOrEmpty(dto.getEmpTimeZoneSelect().getValue())) {
                            dbo.setEmpTimeZoneDBO(new EmpTimeZoneDBO());
                            dbo.getEmpTimeZoneDBO().setId(Integer.parseInt(dto.getEmpTimeZoneSelect().getValue()));
                        }
                    }
                    if (!Utils.isNullOrEmpty(dto.getTimeZoneStartDate()) && !Utils.isNullOrEmpty(dto.getTimeZoneEndDate())) {
                        dbo.setTimeZoneStartDate(dto.getTimeZoneStartDate());
                        dbo.setTimeZoneEndDate(dto.getTimeZoneEndDate());
                        long daysBetween = ChronoUnit.DAYS.between(dto.getTimeZoneStartDate(), dto.getTimeZoneEndDate()) + 1;
                        dbo.setNumberOfDays((int) daysBetween);
                    }
                    if (!Utils.isNullOrEmpty(emp.getValue())) {
                        dbo.setEmpDBO(new EmpDBO());
                        dbo.getEmpDBO().setId(Integer.parseInt(emp.getValue()));
                    }
                    empDatewiseTimeZoneDBOS.add(dbo);
                } else if (Utils.isNullOrEmpty(dto.getTimeZoneStartDate()) && Utils.isNullOrEmpty(dto.getTimeZoneEndDate())) {
                    if (!Utils.isNullOrEmpty(empMap) && empMap.containsKey(Integer.parseInt(emp.getValue()))) {
                        EmpDBO empDBO = empMap.get(Integer.parseInt(emp.getValue()));
                        if (dto.isGenaral()) {
                            empDBO.setEmpTimeZoneDBO(new EmpTimeZoneDBO());
                            empDBO.getEmpTimeZoneDBO().setId(Integer.parseInt(dto.getEmpTimeZoneSelect().getValue()));
                        }
                        if (dto.isHoliday()) {
                            if (!Utils.isNullOrEmpty(empDBO.getEmpJobDetailsDBO()) && !Utils.isNullOrEmpty(empDBO.getEmpJobDetailsDBO().getHolidayTimeZoneDBO())) {
                                empDBO.getEmpJobDetailsDBO().getHolidayTimeZoneDBO().setId(Integer.parseInt(dto.getEmpTimeZoneSelect().getValue()));
                            } else {
                                empDBO.getEmpJobDetailsDBO().setHolidayTimeZoneDBO(new EmpTimeZoneDBO());
                                empDBO.getEmpJobDetailsDBO().getHolidayTimeZoneDBO().setId(Integer.parseInt(dto.getEmpTimeZoneSelect().getValue()));
                            }
                        }
                        empDBOS.add(empDBO);
                    }
                }
            });
            if (!Utils.isNullOrEmpty(empDatewiseTimeZoneDBOS)) {
                if (!empDatewiseTimeZoneDBOS.isEmpty()) {
                    isTrue = bulkTimeZoneTransation.update(empDatewiseTimeZoneDBOS);
                }
            }
            if (!Utils.isNullOrEmpty(empDBOS)) {
                if (!empDBOS.isEmpty()) {
                    isTrue = bulkTimeZoneTransation.updateEmp(empDBOS);
                }
            }
        } else if (!Utils.isNullOrEmpty(dto.getEmpIds()) && Utils.isNullOrEmpty(dto.getId())) {
            List<EmpDBO> empDBOList = !Utils.isNullOrEmpty(dto.getEmpIds()) ? bulkTimeZoneTransation.getEmp(dto.getEmpIds().stream().toList()) : null;
            Map<Integer, EmpDBO> empMap = !Utils.isNullOrEmpty(empDBOList) ? empDBOList.stream().collect(Collectors.toMap(s -> s.getId(), s -> s)) : new HashMap<>();
            dto.getEmpIds().forEach(emp -> {
                if (!Utils.isNullOrEmpty(dto.getTimeZoneStartDate()) && !Utils.isNullOrEmpty(dto.getTimeZoneEndDate())) {
                    EmpDatewiseTimeZoneDBO dbo = new EmpDatewiseTimeZoneDBO();
                    dbo.setCreatedUsersId(Integer.parseInt(userId));
                    dbo.setRecordStatus('A');
                    if (!Utils.isNullOrEmpty(dto.getDescription()))
                        dbo.setDescription(dto.getDescription());
                    if (!Utils.isNullOrEmpty(dto.getTimeZoneStartDate()))
                        dbo.setTimeZoneStartDate(dto.getTimeZoneStartDate());
                    if (!Utils.isNullOrEmpty(dto.getTimeZoneEndDate()))
                        dbo.setTimeZoneEndDate(dto.getTimeZoneEndDate());
                    if (!Utils.isNullOrEmpty(dto.getEmpTimeZoneSelect())) {
                        if (!Utils.isNullOrEmpty(dto.getEmpTimeZoneSelect().getValue())) {
                            dbo.setEmpTimeZoneDBO(new EmpTimeZoneDBO());
                            dbo.getEmpTimeZoneDBO().setId(Integer.parseInt(dto.getEmpTimeZoneSelect().getValue()));
                        }
                    }
                    if (!Utils.isNullOrEmpty(dto.getTimeZoneStartDate()) && !Utils.isNullOrEmpty(dto.getTimeZoneEndDate())) {
                        long daysBetween = ChronoUnit.DAYS.between(dto.getTimeZoneStartDate(), dto.getTimeZoneEndDate()) + 1;
                        dbo.setNumberOfDays((int) daysBetween);
                    }
                    if (!Utils.isNullOrEmpty(emp)) {
                        dbo.setEmpDBO(new EmpDBO());
                        dbo.getEmpDBO().setId(emp);
                    }
                    empDatewiseTimeZoneDBOS.add(dbo);
                } else if (Utils.isNullOrEmpty(dto.getTimeZoneStartDate()) && Utils.isNullOrEmpty(dto.getTimeZoneEndDate())) {
                    if (!Utils.isNullOrEmpty(empMap) && empMap.containsKey(emp)) {
                        EmpDBO empDBO = empMap.get(emp);
                        if (dto.isGenaral()) {
                            empDBO.setEmpTimeZoneDBO(new EmpTimeZoneDBO());
                            empDBO.getEmpTimeZoneDBO().setId(Integer.parseInt(dto.getEmpTimeZoneSelect().getValue()));
                        }
                        if (dto.isHoliday()) {
                            if (!Utils.isNullOrEmpty(empDBO.getEmpJobDetailsDBO()) && !Utils.isNullOrEmpty(empDBO.getEmpJobDetailsDBO().getHolidayTimeZoneDBO())) {
                                empDBO.getEmpJobDetailsDBO().getHolidayTimeZoneDBO().setId(Integer.parseInt(dto.getEmpTimeZoneSelect().getValue()));
                            } else {
                                empDBO.getEmpJobDetailsDBO().setHolidayTimeZoneDBO(new EmpTimeZoneDBO());
                                empDBO.getEmpJobDetailsDBO().getHolidayTimeZoneDBO().setId(Integer.parseInt(dto.getEmpTimeZoneSelect().getValue()));
                            }
                        }
                        empDBOS.add(empDBO);
                    }
                }
            });
            if (!Utils.isNullOrEmpty(empDatewiseTimeZoneDBOS)) {
                if (!empDatewiseTimeZoneDBOS.isEmpty()) {
                    isTrue = bulkTimeZoneTransation.update(empDatewiseTimeZoneDBOS);
                }
            }
            if (!Utils.isNullOrEmpty(empDBOS)) {
                if (!empDBOS.isEmpty()) {
                    isTrue = bulkTimeZoneTransation.updateEmp(empDBOS);
                }
            }
        }
        return isTrue;
    }

    public Flux<EmployeeApplicationDTO> getEmployeeByIdsByEmpCategory(EmpDatewiseTimeZoneDTO data) {
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
        List<Tuple> empTupleList = bulkTimeZoneTransation.getEmpDetails(data, campusDeanearyDeptIds);
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
}