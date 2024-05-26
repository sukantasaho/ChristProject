package com.christ.erp.services.handlers.employee.attendance;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import javax.persistence.Tuple;

import com.christ.erp.services.common.ApiResult;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.AttendanceCumulativeDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.attendance.EmpAttendanceDTO;
import com.christ.erp.services.transactions.employee.attendance.ViewEmployeeAttendanceTransaction;
import com.christ.erp.services.transactions.employee.common.CommonEmployeeTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ViewEmployeeAttendanceHandler {

    @Autowired
    ViewEmployeeAttendanceTransaction viewEmployeeAttendanceTransaction;

    @Autowired
    CommonEmployeeTransaction commonEmployeeTransaction;

    public Flux<EmpAttendanceDTO> getEmployeeAttendance(String startDate, String endDate, String userId) {
        List<Tuple> empAttendanceTuple = viewEmployeeAttendanceTransaction.getEmployeeAttendance(startDate, endDate, userId);
        return convertEmpAttendanceDboToDto(empAttendanceTuple);
    }

    private Flux<EmpAttendanceDTO> convertEmpAttendanceDboToDto(List<Tuple> empAttendanceTuple) {
        List<EmpAttendanceDTO> empAttendanceDTOList = new ArrayList<>();
        if (!Utils.isNullOrEmpty(empAttendanceTuple)) {
            empAttendanceTuple.forEach(tuple -> {
                EmpAttendanceDTO empAttendanceDTO = new EmpAttendanceDTO();
                if (!Utils.isNullOrEmpty(tuple.get("attendance_date")))
                    empAttendanceDTO.setAttendanceDate(Utils.convertStringDateTimeToLocalDateTime(tuple.get("attendance_date").toString()).toLocalDate());
                if (!Utils.isNullOrEmpty(tuple.get("dayName")))
                    empAttendanceDTO.setDayName(tuple.get("dayName").toString().toUpperCase().substring(0, 3));
                if (!Utils.isNullOrEmpty(tuple.get("total_hour")))
                    empAttendanceDTO.setTotalHour(Utils.convertStringTimeToLocalTime(tuple.get("total_hour").toString()));
                if (!Utils.isNullOrEmpty(tuple.get("emp_time_zone_id")) && !Utils.isNullOrEmpty(tuple.get("time_zone_name"))) {
                    SelectDTO selectDTO = new SelectDTO();
                    selectDTO.setValue(tuple.get("emp_time_zone_id").toString());
                    selectDTO.setLabel(tuple.get("time_zone_name").toString());
                    empAttendanceDTO.setEmpTimeZone(selectDTO);
                }
                empAttendanceDTO = covertTupletoDto(empAttendanceDTO,tuple);
                empAttendanceDTOList.add(empAttendanceDTO);
            });
        }
        return Flux.fromIterable(empAttendanceDTOList);
    }

    public EmpAttendanceDTO covertTupletoDto(EmpAttendanceDTO empAttendanceDTO,Tuple tuple){
        //Status Condtion for in time out time other values are null
        if (Utils.isNullOrEmpty(tuple.get("fn_leave_entry_id")) && Utils.isNullOrEmpty(tuple.get("an_leave_entry_id")) && Utils.isNullOrEmpty(tuple.get("emp_holiday_events_id"))
                && ((!Utils.isNullOrEmpty(tuple.get("is_one_time_punch")) && String.valueOf(tuple.get("is_exempted")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_exempted")))
                && ((!Utils.isNullOrEmpty(tuple.get("is_one_time_punch")) && String.valueOf(tuple.get("is_one_time_punch")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_one_time_punch")))) {
            if (((!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday"))) && ((!Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday_working")))) {
                empAttendanceDTO = checkInAndOut(tuple, empAttendanceDTO);
            } else if ((!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("1")) && ((!Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday_working")))) {
                empAttendanceDTO.setIsSunday(true);
                empAttendanceDTO.setStatusName("Sunday");
            } else if (!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("1") && !Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("1")) {
                empAttendanceDTO.setIsSundayWorking(true);
                empAttendanceDTO.setIsSunday(true);
                empAttendanceDTO = checkInAndOut(tuple, empAttendanceDTO);
            }
        }

        //with leave IN THE FORENOON other condition is null
        if (!Utils.isNullOrEmpty(tuple.get("fn_leave_entry_id")) && Utils.isNullOrEmpty(tuple.get("an_leave_entry_id")) && Utils.isNullOrEmpty(tuple.get("emp_holiday_events_id"))
                && ((!Utils.isNullOrEmpty(tuple.get("is_exempted")) && String.valueOf(tuple.get("is_exempted")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_exempted")))
                && ((!Utils.isNullOrEmpty(tuple.get("is_one_time_punch")) && String.valueOf(tuple.get("is_one_time_punch")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_one_time_punch")))) {
            String leaveTypeName = !Utils.isNullOrEmpty(tuple.get("fn_leave_type_name")) ? tuple.get("fn_leave_type_name").toString() : null;
            String leaveSession = !Utils.isNullOrEmpty(tuple.get("fn_leave_session")) ? tuple.get("fn_leave_session").toString() : null;
            empAttendanceDTO.setLeave(true);
            if (((!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday"))) && ((!Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday_working")))) {
                empAttendanceDTO = checkInAndOut(tuple, empAttendanceDTO);
                empAttendanceDTO.setAbsent(false);
                if (!Utils.isNullOrEmpty(leaveSession) && !Utils.isNullOrEmpty(leaveTypeName)) {
                    empAttendanceDTO = checkCondition(tuple, leaveSession, leaveTypeName, empAttendanceDTO);
                }
            } else if ((!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("1")) && ((!Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday_working")))) {
                empAttendanceDTO.setIsSunday(true);
                empAttendanceDTO.setStatusName("Sunday");
            } else if (!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("1") && !Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("1")) {
                empAttendanceDTO.setIsSundayWorking(true);
                empAttendanceDTO.setIsSunday(true);
                empAttendanceDTO = checkInAndOut(tuple, empAttendanceDTO);
                empAttendanceDTO.setAbsent(false);
                if (!Utils.isNullOrEmpty(leaveSession) && !Utils.isNullOrEmpty(leaveTypeName)) {
                    empAttendanceDTO = checkCondition(tuple, leaveSession, leaveTypeName, empAttendanceDTO);
                }
            }
        }

        //with leave IN THE afternoon other condition is null
        else if (Utils.isNullOrEmpty(tuple.get("fn_leave_entry_id")) && !Utils.isNullOrEmpty(tuple.get("an_leave_entry_id")) && Utils.isNullOrEmpty(tuple.get("emp_holiday_events_id"))
                && ((!Utils.isNullOrEmpty(tuple.get("is_exempted")) && String.valueOf(tuple.get("is_exempted")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_exempted")))
                && ((!Utils.isNullOrEmpty(tuple.get("is_one_time_punch")) && String.valueOf(tuple.get("is_one_time_punch")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_one_time_punch")))) {
            String leaveTypeName = !Utils.isNullOrEmpty(tuple.get("an_leave_type_name")) ? tuple.get("an_leave_type_name").toString() : null;
            String leaveSession = !Utils.isNullOrEmpty(tuple.get("an_leave_session")) ? tuple.get("an_leave_session").toString() : null;
            empAttendanceDTO.setLeave(true);
            if (((!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday"))) && ((!Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday_working")))) {
                empAttendanceDTO = checkInAndOut(tuple, empAttendanceDTO);
                empAttendanceDTO.setAbsent(false) ;
                if (!Utils.isNullOrEmpty(leaveSession) && !Utils.isNullOrEmpty(leaveTypeName)) {
                    empAttendanceDTO = checkCondition(tuple, leaveSession, leaveTypeName, empAttendanceDTO);
                }
            } else if ((!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("1")) && ((!Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday_working")))) {
                empAttendanceDTO.setIsSunday(true);
                empAttendanceDTO.setStatusName("Sunday");
            } else if (!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("1") && !Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("1")) {
                empAttendanceDTO.setIsSundayWorking(true);
                empAttendanceDTO.setIsSunday(true);
                empAttendanceDTO = checkInAndOut(tuple, empAttendanceDTO);
                empAttendanceDTO.setAbsent(false);
                if (!Utils.isNullOrEmpty(leaveSession) && !Utils.isNullOrEmpty(leaveTypeName)) {
                    empAttendanceDTO = checkCondition(tuple, leaveSession, leaveTypeName, empAttendanceDTO);
                }
            }
        }

        //with leave IN THE fullday other condition is null
        else if (!Utils.isNullOrEmpty(tuple.get("fn_leave_entry_id")) && !Utils.isNullOrEmpty(tuple.get("an_leave_entry_id")) && Utils.isNullOrEmpty(tuple.get("emp_holiday_events_id"))
                && ((!Utils.isNullOrEmpty(tuple.get("is_exempted")) && String.valueOf(tuple.get("is_exempted")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_exempted")))
                && ((!Utils.isNullOrEmpty(tuple.get("is_one_time_punch")) && String.valueOf(tuple.get("is_one_time_punch")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_one_time_punch")))) {
            String leaveTypeNameForNoon = !Utils.isNullOrEmpty(tuple.get("fn_leave_type_name")) ? tuple.get("fn_leave_type_name").toString() : null;
            String leaveSessionForNoon = !Utils.isNullOrEmpty(tuple.get("fn_leave_session")) ? tuple.get("fn_leave_session").toString() : null;
            String leaveTypeNameAfterNoon = !Utils.isNullOrEmpty(tuple.get("an_leave_type_name")) ? tuple.get("an_leave_type_name").toString() : null;
            String leaveSessionAfterNoon = !Utils.isNullOrEmpty(tuple.get("an_leave_session")) ? tuple.get("an_leave_session").toString() : null;
            empAttendanceDTO.setLeave(true);
            if (((!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday"))) && ((!Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday_working")))) {
                empAttendanceDTO = checkInAndOut(tuple, empAttendanceDTO);
                if (!Utils.isNullOrEmpty(leaveTypeNameForNoon) && !Utils.isNullOrEmpty(leaveSessionForNoon) && !Utils.isNullOrEmpty(leaveTypeNameAfterNoon) && !Utils.isNullOrEmpty(leaveSessionAfterNoon)) {
                    if(Integer.parseInt(String.valueOf(tuple.get("fn_leave_entry_id"))) == Integer.parseInt(String.valueOf(tuple.get("an_leave_entry_id")))){
                        empAttendanceDTO = checkCondition(tuple, leaveSessionForNoon, leaveTypeNameForNoon, empAttendanceDTO);
                    }else {
                        empAttendanceDTO = checkConditionLeaveForDiff(tuple,leaveSessionAfterNoon,leaveTypeNameAfterNoon,leaveSessionForNoon,leaveTypeNameForNoon,empAttendanceDTO);
                    }
                }
            } else if ((!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("1")) && ((!Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday_working")))) {
                empAttendanceDTO.setIsSunday(true);
                empAttendanceDTO.setStatusName("Sunday");
            } else if (!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("1") && !Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("1")) {
                empAttendanceDTO.setIsSundayWorking(true);
                empAttendanceDTO.setIsSunday(true);
                empAttendanceDTO = checkInAndOut(tuple, empAttendanceDTO);
                if (!Utils.isNullOrEmpty(leaveTypeNameForNoon) && !Utils.isNullOrEmpty(leaveSessionForNoon) && !Utils.isNullOrEmpty(leaveTypeNameAfterNoon) && !Utils.isNullOrEmpty(leaveSessionAfterNoon)) {
                    if(Integer.parseInt(String.valueOf(tuple.get("fn_leave_entry_id"))) == Integer.parseInt(String.valueOf(tuple.get("an_leave_entry_id")))){
                        empAttendanceDTO = checkCondition(tuple, leaveSessionForNoon, leaveTypeNameForNoon, empAttendanceDTO);
                    }else {
                        empAttendanceDTO = checkConditionLeaveForDiff(tuple,leaveSessionAfterNoon,leaveTypeNameAfterNoon,leaveSessionForNoon,leaveTypeNameForNoon,empAttendanceDTO);
                    }
                }
            }
        }

        //exemption with other condition null
        if (Utils.isNullOrEmpty(tuple.get("fn_leave_entry_id")) && Utils.isNullOrEmpty(tuple.get("an_leave_entry_id")) && Utils.isNullOrEmpty(tuple.get("emp_holiday_events_id"))
                && (!Utils.isNullOrEmpty(tuple.get("is_exempted")) && String.valueOf(tuple.get("is_exempted")).trim().equalsIgnoreCase("1"))
                && ((!Utils.isNullOrEmpty(tuple.get("is_one_time_punch"))) && String.valueOf(tuple.get("is_one_time_punch")).trim().equalsIgnoreCase("0"))) {
            String exemptedSession = !Utils.isNullOrEmpty(tuple.get("exempted_session")) ? String.valueOf(tuple.get("exempted_session")) : null;
            String exemptedName = "Exempted";
            empAttendanceDTO.setExempted(true);
            if (((!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday"))) && ((!Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday_working")))) {
                empAttendanceDTO = checkInAndOut(tuple, empAttendanceDTO);
                empAttendanceDTO.setAbsent(false);
                if (!Utils.isNullOrEmpty(exemptedSession) && !Utils.isNullOrEmpty(exemptedName)) {
                    empAttendanceDTO = checkCondition(tuple, exemptedSession, exemptedName, empAttendanceDTO);
                }
            } else if ((!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("1")) && ((!Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday_working")))) {
                empAttendanceDTO.setIsSunday(true);
                empAttendanceDTO.setStatusName("Sunday");
            } else if (!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("1") && !Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("1")) {
                empAttendanceDTO.setIsSundayWorking(true);
                empAttendanceDTO.setIsSunday(true);
                empAttendanceDTO = checkInAndOut(tuple, empAttendanceDTO);
                empAttendanceDTO.setAbsent(false);
                if (!Utils.isNullOrEmpty(exemptedSession) && !Utils.isNullOrEmpty(exemptedName)) {
                    empAttendanceDTO = checkCondition(tuple, exemptedSession, exemptedName, empAttendanceDTO);
                }
            }
        }

        //holiday and event other condition null
        if (Utils.isNullOrEmpty(tuple.get("fn_leave_entry_id")) && Utils.isNullOrEmpty(tuple.get("an_leave_entry_id")) && !Utils.isNullOrEmpty(tuple.get("emp_holiday_events_id"))
                && ((Utils.isNullOrEmpty(tuple.get("is_exempted"))) || String.valueOf(tuple.get("is_exempted")).trim().equalsIgnoreCase("0"))
                && ((Utils.isNullOrEmpty(tuple.get("is_one_time_punch"))) || String.valueOf(tuple.get("is_one_time_punch")).trim().equalsIgnoreCase("0"))) {
            String holidayAndEventSession = !Utils.isNullOrEmpty(tuple.get("holiday_events_session")) ? String.valueOf(tuple.get("holiday_events_session")) : null;
            String holidayEventName = !Utils.isNullOrEmpty(tuple.get("emp_holiday_events_type_name")) ? String.valueOf(tuple.get("emp_holiday_events_type_name")) : null;
            if (((!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday"))) && ((!Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday_working")))) {
                empAttendanceDTO = checkInAndOut(tuple, empAttendanceDTO);
                empAttendanceDTO.setHoliday(true);
                empAttendanceDTO.setAbsent(false);
                if (!Utils.isNullOrEmpty(holidayAndEventSession) && !Utils.isNullOrEmpty(holidayEventName)) {
                    empAttendanceDTO = checkCondition(tuple, holidayAndEventSession, holidayEventName, empAttendanceDTO);
                    if(!Utils.isNullOrEmpty(tuple.get("emp_holiday_events_type_name")) && !Utils.isNullOrEmpty(tuple.get("holiday_events_description"))){
                        if(tuple.get("emp_holiday_events_type_name").toString().trim().equalsIgnoreCase("Holiday"))
                            empAttendanceDTO.setStatusName(tuple.get("holiday_events_description").toString());
                    }
                }
            } else if ((!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("1")) && ((!Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday_working")))) {
                empAttendanceDTO.setIsSunday(true);
                empAttendanceDTO.setStatusName("Sunday");
            } else if (!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("1") && !Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("1")) {
                empAttendanceDTO.setIsSundayWorking(true);
                empAttendanceDTO.setIsSunday(true);
                empAttendanceDTO.setHoliday(true);
                empAttendanceDTO = checkInAndOut(tuple, empAttendanceDTO);
                empAttendanceDTO.setAbsent(false);
                if (!Utils.isNullOrEmpty(holidayAndEventSession) && !Utils.isNullOrEmpty(holidayEventName)) {
                    empAttendanceDTO = checkCondition(tuple, holidayAndEventSession, holidayEventName, empAttendanceDTO);
                    if(!Utils.isNullOrEmpty(tuple.get("emp_holiday_events_type_name"))){
                        if(tuple.get("emp_holiday_events_type_name").toString().trim().equalsIgnoreCase("Holiday"))
                            empAttendanceDTO.setStatusName("Holiday");
                    }
                }
            }
        }

        //leave and exemption with afternoon
        if (Utils.isNullOrEmpty(tuple.get("fn_leave_entry_id")) && !Utils.isNullOrEmpty(tuple.get("an_leave_entry_id")) && Utils.isNullOrEmpty(tuple.get("emp_holiday_events_id"))
                && String.valueOf(tuple.get("is_exempted")).trim().equalsIgnoreCase("1")
                && String.valueOf(tuple.get("is_one_time_punch")).trim().equalsIgnoreCase("0")) {
            String exemptedSession = !Utils.isNullOrEmpty(tuple.get("exempted_session")) ? String.valueOf(tuple.get("exempted_session")) : null;
            String exemptedName = "Exempted";
            String leaveTypeNameAfterNoon = !Utils.isNullOrEmpty(tuple.get("an_leave_type_name")) ? tuple.get("an_leave_type_name").toString() : null;
            String leaveSessionAfterNoon = !Utils.isNullOrEmpty(tuple.get("an_leave_session")) ? tuple.get("an_leave_session").toString() : null;
            empAttendanceDTO.setLeave(true);
            empAttendanceDTO.setExempted(true);
            if (((!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday"))) && ((!Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday_working")))) {
                empAttendanceDTO = checkInAndOut(tuple, empAttendanceDTO);
                empAttendanceDTO.setAbsent(false);
                if (!Utils.isNullOrEmpty(exemptedSession) && !Utils.isNullOrEmpty(exemptedName) && !Utils.isNullOrEmpty(leaveTypeNameAfterNoon) && !Utils.isNullOrEmpty(leaveSessionAfterNoon)) {
                    empAttendanceDTO = checkConditionLeaveAndExemption(tuple, leaveSessionAfterNoon, leaveTypeNameAfterNoon, exemptedSession, exemptedName, empAttendanceDTO);
                }
            } else if ((!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("1")) && ((!Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday_working")))) {
                empAttendanceDTO.setIsSunday(true);
                empAttendanceDTO.setStatusName("Sunday");
            } else if (!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("1") && !Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("1")) {
                empAttendanceDTO.setIsSundayWorking(true);
                empAttendanceDTO.setIsSunday(true);
                empAttendanceDTO = checkInAndOut(tuple, empAttendanceDTO);
                empAttendanceDTO.setAbsent(false);
                if (!Utils.isNullOrEmpty(exemptedSession) && !Utils.isNullOrEmpty(exemptedName) && !Utils.isNullOrEmpty(leaveTypeNameAfterNoon) && !Utils.isNullOrEmpty(leaveSessionAfterNoon)) {
                    empAttendanceDTO = checkConditionLeaveAndExemption(tuple, leaveSessionAfterNoon, leaveTypeNameAfterNoon, exemptedSession, exemptedName, empAttendanceDTO);
                }
            }
        }

        //leave and excemption with forenoon
        if (!Utils.isNullOrEmpty(tuple.get("fn_leave_entry_id")) && Utils.isNullOrEmpty(tuple.get("an_leave_entry_id")) && Utils.isNullOrEmpty(tuple.get("emp_holiday_events_id"))
                && String.valueOf(tuple.get("is_exempted")).trim().equalsIgnoreCase("1")
                && String.valueOf(tuple.get("is_one_time_punch")).trim().equalsIgnoreCase("0")) {
            String exemptedSession = !Utils.isNullOrEmpty(tuple.get("exempted_session")) ? String.valueOf(tuple.get("exempted_session")) : null;
            String exemptedName = "Exempted";
            String leaveTypeNameForeNoon = !Utils.isNullOrEmpty(tuple.get("fn_leave_type_name")) ? tuple.get("fn_leave_type_name").toString() : null;
            String leaveSessionForeNoon = !Utils.isNullOrEmpty(tuple.get("fn_leave_session")) ? tuple.get("fn_leave_session").toString() : null;
            empAttendanceDTO.setLeave(true);
            empAttendanceDTO.setExempted(true);
            if (((!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday"))) && ((!Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday_working")))) {
                empAttendanceDTO = checkInAndOut(tuple, empAttendanceDTO);
                empAttendanceDTO.setAbsent(false);
                if (!Utils.isNullOrEmpty(exemptedSession) && !Utils.isNullOrEmpty(exemptedName) && !Utils.isNullOrEmpty(leaveTypeNameForeNoon) && !Utils.isNullOrEmpty(leaveSessionForeNoon)) {
                    empAttendanceDTO = checkConditionLeaveAndExemption(tuple, leaveSessionForeNoon, leaveTypeNameForeNoon, exemptedSession, exemptedName, empAttendanceDTO);
                }
            } else if ((!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("1")) && ((!Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday_working")))) {
                empAttendanceDTO.setIsSunday(true);
                empAttendanceDTO.setStatusName("Sunday");
            } else if (!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("1") && !Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("1")) {
                empAttendanceDTO.setIsSundayWorking(true);
                empAttendanceDTO.setIsSunday(true);
                empAttendanceDTO = checkInAndOut(tuple, empAttendanceDTO);
                empAttendanceDTO.setAbsent(false);
                if (!Utils.isNullOrEmpty(exemptedSession) && !Utils.isNullOrEmpty(exemptedName) && !Utils.isNullOrEmpty(leaveSessionForeNoon) && !Utils.isNullOrEmpty(leaveTypeNameForeNoon)) {
                    empAttendanceDTO = checkConditionLeaveAndExemption(tuple, leaveSessionForeNoon, leaveTypeNameForeNoon, exemptedSession, exemptedName, empAttendanceDTO);
                }
            }
        }

        //leave and excemption with fullday
        if (!Utils.isNullOrEmpty(tuple.get("fn_leave_entry_id")) && !Utils.isNullOrEmpty(tuple.get("an_leave_entry_id")) && Utils.isNullOrEmpty(tuple.get("emp_holiday_events_id"))
                && String.valueOf(tuple.get("is_exempted")).trim().equalsIgnoreCase("1")
                && String.valueOf(tuple.get("is_one_time_punch")).trim().equalsIgnoreCase("0")) {
            String exemptedSession = !Utils.isNullOrEmpty(tuple.get("exempted_session")) ? String.valueOf(tuple.get("exempted_session")) : null;
            String exemptedName = "Exempted";
            String leaveTypeNameAfterNoon = !Utils.isNullOrEmpty(tuple.get("an_leave_type_name")) ? tuple.get("an_leave_type_name").toString() : null;
            String leaveSessionAfterNoon = !Utils.isNullOrEmpty(tuple.get("an_leave_session")) ? tuple.get("an_leave_session").toString() : null;
            String leaveTypeNameForeNoon = !Utils.isNullOrEmpty(tuple.get("fn_leave_type_name")) ? tuple.get("fn_leave_type_name").toString() : null;
            String leaveSessionForeNoon = !Utils.isNullOrEmpty(tuple.get("fn_leave_session")) ? tuple.get("fn_leave_session").toString() : null;
            empAttendanceDTO.setLeave(true);
            empAttendanceDTO.setExempted(true);
            if (((!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday"))) && ((!Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday_working")))) {
                empAttendanceDTO = checkInAndOut(tuple, empAttendanceDTO);
                if (!Utils.isNullOrEmpty(exemptedSession) && !Utils.isNullOrEmpty(exemptedName) && !Utils.isNullOrEmpty(leaveTypeNameForeNoon) && !Utils.isNullOrEmpty(leaveSessionForeNoon) && !Utils.isNullOrEmpty(leaveTypeNameAfterNoon) && !Utils.isNullOrEmpty(leaveSessionAfterNoon)) {
                    if(Integer.parseInt(String.valueOf(tuple.get("fn_leave_entry_id"))) == Integer.parseInt(String.valueOf(tuple.get("an_leave_entry_id")))){
                        empAttendanceDTO = checkConditionLeaveAndExemption(tuple, leaveSessionForeNoon, leaveTypeNameForeNoon, exemptedSession, exemptedName, empAttendanceDTO);
                    }else{
                        empAttendanceDTO = checkConditionLeaveForDiff(tuple, leaveSessionAfterNoon,leaveTypeNameAfterNoon,leaveSessionForeNoon, leaveTypeNameForeNoon, empAttendanceDTO);
                    }
                }
                empAttendanceDTO.setAbsent(false);
            } else if ((!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("1")) && ((!Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday_working")))) {
                empAttendanceDTO.setIsSunday(true);
                empAttendanceDTO.setStatusName("Sunday");
            } else if (!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("1") && !Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("1")) {
                empAttendanceDTO.setIsSundayWorking(true);
                empAttendanceDTO.setIsSunday(true);
                empAttendanceDTO = checkInAndOut(tuple, empAttendanceDTO);
                if (!Utils.isNullOrEmpty(exemptedSession) && !Utils.isNullOrEmpty(exemptedName) && !Utils.isNullOrEmpty(leaveTypeNameForeNoon) && !Utils.isNullOrEmpty(leaveSessionForeNoon) && !Utils.isNullOrEmpty(leaveTypeNameAfterNoon) && !Utils.isNullOrEmpty(leaveSessionAfterNoon)) {
                    if(Integer.parseInt(String.valueOf(tuple.get("fn_leave_entry_id"))) == Integer.parseInt(String.valueOf(tuple.get("an_leave_entry_id")))){
                        empAttendanceDTO = checkConditionLeaveAndExemption(tuple, leaveSessionForeNoon, leaveTypeNameForeNoon, exemptedSession, exemptedName, empAttendanceDTO);
                    }else{
                        empAttendanceDTO = checkConditionLeaveForDiff(tuple, leaveSessionAfterNoon,leaveTypeNameAfterNoon,leaveSessionForeNoon, leaveTypeNameForeNoon, empAttendanceDTO);
                    }
                }
            }
        }

        //leave+holidayAndEvent+forenoon
        if (!Utils.isNullOrEmpty(tuple.get("fn_leave_entry_id")) && Utils.isNullOrEmpty(tuple.get("an_leave_entry_id"))&& !Utils.isNullOrEmpty(tuple.get("emp_holiday_events_id"))
                && ((Utils.isNullOrEmpty(tuple.get("is_exempted"))) || String.valueOf(tuple.get("is_exempted")).trim().equalsIgnoreCase("0"))
                && ((Utils.isNullOrEmpty(tuple.get("is_one_time_punch"))) || String.valueOf(tuple.get("is_one_time_punch")).trim().equalsIgnoreCase("0"))) {
            String holidayAndEventSession = !Utils.isNullOrEmpty(tuple.get("holiday_events_session")) ? String.valueOf(tuple.get("holiday_events_session")) : null;
            String holidayEventName = !Utils.isNullOrEmpty(tuple.get("emp_holiday_events_type_name")) ? String.valueOf(tuple.get("emp_holiday_events_type_name")) : null;
            String leaveTypeName = !Utils.isNullOrEmpty(tuple.get("fn_leave_type_name")) ? tuple.get("fn_leave_type_name").toString() : null;
            String leaveSession = !Utils.isNullOrEmpty(tuple.get("fn_leave_session")) ? tuple.get("fn_leave_session").toString() : null;
            empAttendanceDTO.setLeave(true);
            empAttendanceDTO.setHoliday(true);
            if (((!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday"))) && ((!Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday_working")))) {
                empAttendanceDTO = checkInAndOut(tuple, empAttendanceDTO);
                if (!Utils.isNullOrEmpty(holidayAndEventSession) && !Utils.isNullOrEmpty(holidayEventName) && !Utils.isNullOrEmpty(leaveTypeName) && !Utils.isNullOrEmpty(leaveSession)) {
                    empAttendanceDTO = checkConditionLeaveAndExemption(tuple, leaveSession, leaveTypeName, holidayAndEventSession, holidayEventName, empAttendanceDTO);
                }
            } else if ((!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("1")) && ((!Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday_working")))) {
                empAttendanceDTO.setIsSunday(true);
                empAttendanceDTO.setStatusName("Sunday");
            } else if (!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("1") && !Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("1")) {
                empAttendanceDTO.setIsSundayWorking(true);
                empAttendanceDTO.setIsSunday(true);
                empAttendanceDTO = checkInAndOut(tuple, empAttendanceDTO);
                if (!Utils.isNullOrEmpty(holidayAndEventSession) && !Utils.isNullOrEmpty(holidayEventName) && !Utils.isNullOrEmpty(leaveTypeName) && !Utils.isNullOrEmpty(leaveSession)) {
                    empAttendanceDTO = checkConditionLeaveAndExemption(tuple, leaveSession, leaveTypeName, holidayAndEventSession, holidayEventName, empAttendanceDTO);
                }
            }
        }

        //leave+holidayAndEvent+forenoon
        if (Utils.isNullOrEmpty(tuple.get("fn_leave_entry_id")) && !Utils.isNullOrEmpty(tuple.get("an_leave_entry_id"))&& !Utils.isNullOrEmpty(tuple.get("emp_holiday_events_id"))
                && ((Utils.isNullOrEmpty(tuple.get("is_exempted"))) || String.valueOf(tuple.get("is_exempted")).trim().equalsIgnoreCase("0"))
                && ((Utils.isNullOrEmpty(tuple.get("is_one_time_punch"))) || String.valueOf(tuple.get("is_one_time_punch")).trim().equalsIgnoreCase("0"))) {
            String holidayAndEventSession = !Utils.isNullOrEmpty(tuple.get("holiday_events_session")) ? String.valueOf(tuple.get("holiday_events_session")) : null;
            String holidayEventName = !Utils.isNullOrEmpty(tuple.get("emp_holiday_events_type_name")) ? String.valueOf(tuple.get("emp_holiday_events_type_name")) : null;
            String leaveTypeName = !Utils.isNullOrEmpty(tuple.get("an_leave_type_name")) ? tuple.get("an_leave_type_name").toString() : null;
            String leaveSession = !Utils.isNullOrEmpty(tuple.get("an_leave_session")) ? tuple.get("an_leave_session").toString() : null;
            empAttendanceDTO.setLeave(true);
            empAttendanceDTO.setHoliday(true);
            if (((!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday"))) && ((!Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday_working")))) {
                empAttendanceDTO = checkInAndOut(tuple, empAttendanceDTO);
                if (!Utils.isNullOrEmpty(holidayAndEventSession) && !Utils.isNullOrEmpty(holidayEventName) && !Utils.isNullOrEmpty(leaveTypeName) && !Utils.isNullOrEmpty(leaveSession)) {
                    empAttendanceDTO = checkConditionLeaveAndExemption(tuple, leaveSession, leaveTypeName, holidayAndEventSession, holidayEventName, empAttendanceDTO);
                }
            } else if ((!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("1")) && ((!Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday_working")))) {
                empAttendanceDTO.setIsSunday(true);
                empAttendanceDTO.setStatusName("Sunday");
            } else if (!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("1") && !Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("1")) {
                empAttendanceDTO.setIsSundayWorking(true);
                empAttendanceDTO.setIsSunday(true);
                empAttendanceDTO = checkInAndOut(tuple, empAttendanceDTO);
                if (!Utils.isNullOrEmpty(holidayAndEventSession) && !Utils.isNullOrEmpty(holidayEventName) && !Utils.isNullOrEmpty(leaveTypeName) && !Utils.isNullOrEmpty(leaveSession)) {
                    empAttendanceDTO = checkConditionLeaveAndExemption(tuple, leaveSession, leaveTypeName, holidayAndEventSession, holidayEventName, empAttendanceDTO);
                }
            }
        }

        //leave+holidayAndEvent+FullDAy
        if (!Utils.isNullOrEmpty(tuple.get("fn_leave_entry_id")) && !Utils.isNullOrEmpty(tuple.get("an_leave_entry_id"))&& !Utils.isNullOrEmpty(tuple.get("emp_holiday_events_id"))
                && ((Utils.isNullOrEmpty(tuple.get("is_exempted"))) || String.valueOf(tuple.get("is_exempted")).trim().equalsIgnoreCase("0"))
                && ((Utils.isNullOrEmpty(tuple.get("is_one_time_punch"))) || String.valueOf(tuple.get("is_one_time_punch")).trim().equalsIgnoreCase("0"))) {
            String holidayAndEventSession = !Utils.isNullOrEmpty(tuple.get("holiday_events_session")) ? String.valueOf(tuple.get("holiday_events_session")) : null;
            String holidayEventName = !Utils.isNullOrEmpty(tuple.get("emp_holiday_events_type_name")) ? String.valueOf(tuple.get("emp_holiday_events_type_name")) : null;
            String ANleaveName = !Utils.isNullOrEmpty(tuple.get("an_leave_type_name")) ? tuple.get("an_leave_type_name").toString() : null;
            String ANleaveSession = !Utils.isNullOrEmpty(tuple.get("an_leave_session")) ? tuple.get("an_leave_session").toString() : null;
            String FNleaveName = !Utils.isNullOrEmpty(tuple.get("an_leave_type_name")) ? tuple.get("an_leave_type_name").toString() : null;
            String FNleaveSession = !Utils.isNullOrEmpty(tuple.get("an_leave_session")) ? tuple.get("an_leave_session").toString() : null;
            empAttendanceDTO.setLeave(true);
            empAttendanceDTO.setHoliday(true);
            if (((!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday"))) && ((!Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday_working")))) {
                empAttendanceDTO = checkInAndOut(tuple, empAttendanceDTO);
                if (!Utils.isNullOrEmpty(holidayAndEventSession) && !Utils.isNullOrEmpty(holidayEventName) && !Utils.isNullOrEmpty(ANleaveName) && !Utils.isNullOrEmpty(ANleaveSession) && !Utils.isNullOrEmpty(FNleaveName) && !Utils.isNullOrEmpty(FNleaveSession)) {
                    if(Integer.parseInt(tuple.get("fn_leave_entry_id").toString()) == Integer.parseInt(tuple.get("an_leave_entry_id").toString())){
                        empAttendanceDTO = checkConditionLeaveAndExemption(tuple, ANleaveSession, ANleaveName, holidayAndEventSession, holidayEventName, empAttendanceDTO);
                    }else{
                        empAttendanceDTO = checkConditionLeaveForDiff(tuple,ANleaveSession,ANleaveName,FNleaveSession,FNleaveName,empAttendanceDTO);
                    }
                }
            } else if ((!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("1")) && ((!Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_sunday_working")))) {
                empAttendanceDTO.setIsSunday(true);
                empAttendanceDTO.setStatusName("Sunday");
            } else if (!Utils.isNullOrEmpty(tuple.get("is_sunday")) && String.valueOf(tuple.get("is_sunday")).trim().equalsIgnoreCase("1") && !Utils.isNullOrEmpty(tuple.get("is_sunday_working")) && String.valueOf(tuple.get("is_sunday_working")).trim().equalsIgnoreCase("1")) {
                empAttendanceDTO.setIsSundayWorking(true);
                empAttendanceDTO.setIsSunday(true);
                empAttendanceDTO = checkInAndOut(tuple, empAttendanceDTO);
                if (!Utils.isNullOrEmpty(holidayAndEventSession) && !Utils.isNullOrEmpty(holidayEventName) && !Utils.isNullOrEmpty(ANleaveName) && !Utils.isNullOrEmpty(ANleaveSession) && !Utils.isNullOrEmpty(FNleaveName) && !Utils.isNullOrEmpty(FNleaveSession)) {
                    if(Integer.parseInt(tuple.get("fn_leave_entry_id").toString()) == Integer.parseInt(tuple.get("an_leave_entry_id").toString())){
                        empAttendanceDTO = checkConditionLeaveAndExemption(tuple, ANleaveSession, ANleaveName, holidayAndEventSession, holidayEventName, empAttendanceDTO);
                    }else{
                        empAttendanceDTO = checkConditionLeaveForDiff(tuple,ANleaveSession,ANleaveName,FNleaveSession,FNleaveName,empAttendanceDTO);
                    }
                }
            }
        }
        return empAttendanceDTO;
    }

    public EmpAttendanceDTO checkInAndOut(Tuple tuple, EmpAttendanceDTO empAttendanceDTO) {
        if (((!Utils.isNullOrEmpty(tuple.get("is_late_entry")) && String.valueOf(tuple.get("is_late_entry")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_late_entry"))) && ((!Utils.isNullOrEmpty(tuple.get("is_early_exit")) && String.valueOf(tuple.get("is_early_exit")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_early_exit")))) {
            // Present
            if (!Utils.isNullOrEmpty(tuple.get("in_time")) && !Utils.isNullOrEmpty(tuple.get("out_time"))) {
                empAttendanceDTO.setInTimeStatus((!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())+":00");
                empAttendanceDTO.setOutTimeStatus((!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())+":00");
                empAttendanceDTO.setStatusName("Present");
                empAttendanceDTO.setPresent(true);
//                empAttendanceDTO.setLeaveSession("FD");
            } else if (!Utils.isNullOrEmpty(tuple.get("in_time")) && Utils.isNullOrEmpty(tuple.get("out_time"))) {
                empAttendanceDTO.setInTimeStatus((!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())+":00");
                empAttendanceDTO.setOutTimeStatus("Absent");
                empAttendanceDTO.setAbsent(true);
                empAttendanceDTO.setStatusName("Absent (Afternoon)");
                empAttendanceDTO.setPresent(true);
                empAttendanceDTO.setLeaveSession("AN");
            } else if (Utils.isNullOrEmpty(tuple.get("in_time")) && !Utils.isNullOrEmpty(tuple.get("out_time"))) {
                empAttendanceDTO.setInTimeStatus("Absent");
                empAttendanceDTO.setOutTimeStatus((!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())+":00");
                empAttendanceDTO.setStatusName("Absent (Forenoon)");
                empAttendanceDTO.setAbsent(true);
                empAttendanceDTO.setPresent(true);
                empAttendanceDTO.setLeaveSession("FN");
            } else if (Utils.isNullOrEmpty(tuple.get("in_time")) && Utils.isNullOrEmpty(tuple.get("out_time"))) {
                empAttendanceDTO.setInTimeStatus("Absent");
                empAttendanceDTO.setOutTimeStatus("--");
                empAttendanceDTO.setStatusName("Absent");
                empAttendanceDTO.setAbsent(true);
                empAttendanceDTO.setLeaveSession("FD");
            }
        } else if ((!Utils.isNullOrEmpty(tuple.get("is_late_entry")) && String.valueOf(tuple.get("is_late_entry")).trim().equalsIgnoreCase("1")) && ((!Utils.isNullOrEmpty(tuple.get("is_early_exit")) && String.valueOf(tuple.get("is_early_exit")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_early_exit")))) {
            // Late Entry
            if (!Utils.isNullOrEmpty(tuple.get("in_time")) && !Utils.isNullOrEmpty(tuple.get("late_entry_by")) && !Utils.isNullOrEmpty(tuple.get("out_time"))) {
                empAttendanceDTO.setLateEntryBy(Utils.convertStringTimeToLocalTime(String.valueOf(tuple.get("late_entry_by"))));
                String lateHour = String.valueOf(Utils.convertStringTimeToLocalTime((tuple.get("late_entry_by").toString())).getHour()) + "h";
                String lateMinute = String.valueOf(Utils.convertStringTimeToLocalTime((tuple.get("late_entry_by").toString())).getMinute()) + "m";
                empAttendanceDTO.setInTimeStatus((!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())+":00" + " (Late Entry - " + lateHour + ":" + lateMinute + ")");
                empAttendanceDTO.setOutTimeStatus((!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())+":00");
                empAttendanceDTO.setStatusName("Present");
//                empAttendanceDTO.setLeaveSession("FN");
                empAttendanceDTO.setIsLateEntry(true);
                empAttendanceDTO.setPresent(true);
            } else if (!Utils.isNullOrEmpty(tuple.get("in_time")) && !Utils.isNullOrEmpty(tuple.get("late_entry_by")) && Utils.isNullOrEmpty(tuple.get("out_time"))) {
                empAttendanceDTO.setLateEntryBy(Utils.convertStringTimeToLocalTime(String.valueOf(tuple.get("late_entry_by"))));
                String lateHour = String.valueOf(Utils.convertStringTimeToLocalTime((tuple.get("late_entry_by").toString())).getHour()) + "h";
                String lateMinute = String.valueOf(Utils.convertStringTimeToLocalTime((tuple.get("late_entry_by").toString())).getMinute()) + "m";
                empAttendanceDTO.setInTimeStatus((!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())+":00" + " (Late Entry - " + lateHour + ":" + lateMinute + ")");
                empAttendanceDTO.setOutTimeStatus("Absent");
                empAttendanceDTO.setLeaveSession("AN");
                empAttendanceDTO.setStatusName("Absent (Afternoon)");
                empAttendanceDTO.setIsLateEntry(true);
                empAttendanceDTO.setPresent(true);
                empAttendanceDTO.setAbsent(true);
            }
        } else if (((!Utils.isNullOrEmpty(tuple.get("is_late_entry")) && String.valueOf(tuple.get("is_late_entry")).trim().equalsIgnoreCase("0")) || Utils.isNullOrEmpty(tuple.get("is_late_entry"))) && (!Utils.isNullOrEmpty(tuple.get("is_early_exit")) && String.valueOf(tuple.get("is_early_exit")).trim().equalsIgnoreCase("1"))) {
            // early Exit
            if (!Utils.isNullOrEmpty(tuple.get("out_time")) && !Utils.isNullOrEmpty(tuple.get("early_exit_by")) && !Utils.isNullOrEmpty(tuple.get("in_time"))) {
                String earlyHour = String.valueOf(Utils.convertStringTimeToLocalTime(String.valueOf(tuple.get("early_exit_by"))).getHour()) + "h";
                String earlyMinute = String.valueOf(Utils.convertStringTimeToLocalTime(String.valueOf(tuple.get("early_exit_by"))).getMinute()) + "m";
                empAttendanceDTO.setEarlyExitBy(Utils.convertStringTimeToLocalTime(String.valueOf(tuple.get("early_exit_by"))));
                empAttendanceDTO.setOutTimeStatus((!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())+":00" + " (Early Exit - " + earlyHour + ":" + earlyMinute + ")");
                empAttendanceDTO.setInTimeStatus((!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())+":00");
                empAttendanceDTO.setStatusName("Present");
                empAttendanceDTO.setIsEarlyExit(true);
                empAttendanceDTO.setPresent(true);
//                empAttendanceDTO.setLeaveSession("FD");
            } else if (!Utils.isNullOrEmpty(tuple.get("out_time")) && !Utils.isNullOrEmpty(tuple.get("early_exit_by")) && Utils.isNullOrEmpty(tuple.get("in_time"))) {
                String earlyHour = String.valueOf(Utils.convertStringTimeToLocalTime(String.valueOf(tuple.get("early_exit_by"))).getHour()) + "h";
                String earlyMinute = String.valueOf(Utils.convertStringTimeToLocalTime(String.valueOf(tuple.get("early_exit_by"))).getMinute()) + "m";
                empAttendanceDTO.setEarlyExitBy(Utils.convertStringTimeToLocalTime(String.valueOf(tuple.get("early_exit_by"))));
                empAttendanceDTO.setOutTimeStatus((!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())+":00" + " (Early Exit - " + earlyHour + ":" + earlyMinute + ")");
                empAttendanceDTO.setInTimeStatus("Absent");
                empAttendanceDTO.setStatusName("Absent (Forenoon)");
                empAttendanceDTO.setIsEarlyExit(true);
                empAttendanceDTO.setAbsent(true);
                empAttendanceDTO.setPresent(true);
                empAttendanceDTO.setLeaveSession("FN");
            }
        } else if ((!Utils.isNullOrEmpty(tuple.get("is_late_entry")) && String.valueOf(tuple.get("is_late_entry")).trim().equalsIgnoreCase("1")) && (!Utils.isNullOrEmpty(tuple.get("is_early_exit")) && String.valueOf(tuple.get("is_early_exit")).trim().equalsIgnoreCase("1"))) {
            //Late+Early
            if (!Utils.isNullOrEmpty(tuple.get("in_time")) && !Utils.isNullOrEmpty(tuple.get("late_entry_by")) && !Utils.isNullOrEmpty(tuple.get("out_time")) && !Utils.isNullOrEmpty(tuple.get("early_exit_by"))) {
                String lateHour = String.valueOf(Utils.convertStringTimeToLocalTime((tuple.get("late_entry_by").toString())).getHour()) + "h";
                String lateMinute = String.valueOf(Utils.convertStringTimeToLocalTime((tuple.get("late_entry_by").toString())).getMinute()) + "m";
                String earlyHour = String.valueOf(Utils.convertStringTimeToLocalTime(String.valueOf(tuple.get("early_exit_by"))).getHour()) + "h";
                String earlyMinute = String.valueOf(Utils.convertStringTimeToLocalTime(String.valueOf(tuple.get("early_exit_by"))).getMinute()) + "m";
                empAttendanceDTO.setInTimeStatus((!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())+":00" + " (Late Entry - " + lateHour + ":" + lateMinute + ")");
                empAttendanceDTO.setLateEntryBy(Utils.convertStringTimeToLocalTime(String.valueOf(tuple.get("late_entry_by"))));
                empAttendanceDTO.setOutTimeStatus((!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())+":00" + " (Early Exit - " + earlyHour + ":" + earlyMinute + ")");
                empAttendanceDTO.setEarlyExitBy(Utils.convertStringTimeToLocalTime(String.valueOf(tuple.get("early_exit_by"))));
                empAttendanceDTO.setStatusName("Present");
                empAttendanceDTO.setIsEarlyExit(true);
                empAttendanceDTO.setIsLateEntry(true);
                empAttendanceDTO.setPresent(true);
//                empAttendanceDTO.setLeaveSession("FD");
            }
        }
        return empAttendanceDTO;
    }

    public EmpAttendanceDTO checkCondition(Tuple tuple, String session, String sessionName, EmpAttendanceDTO empAttendanceDTO) {
        if (session.trim().equalsIgnoreCase("FD")) {
            if (Utils.isNullOrEmpty(tuple.get("in_time")) && Utils.isNullOrEmpty(tuple.get("out_time"))) {
                empAttendanceDTO.setInTimeStatus(sessionName);
                empAttendanceDTO.setStatusName(sessionName);
                empAttendanceDTO.setOutTimeStatus("--");
                empAttendanceDTO.setLeaveSession("FD");
                empAttendanceDTO.setAbsent(false);
            } else if (!Utils.isNullOrEmpty(tuple.get("in_time")) && Utils.isNullOrEmpty(tuple.get("out_time"))) {
                String inTime = (!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())+":00";
                empAttendanceDTO.setInTimeStatus(sessionName + " - " + inTime);
                empAttendanceDTO.setStatusName(sessionName);
                empAttendanceDTO.setLeaveSession("FD");
                empAttendanceDTO.setOutTimeStatus(sessionName);
                empAttendanceDTO.setAbsent(false);
            } else if (Utils.isNullOrEmpty(tuple.get("in_time")) && !Utils.isNullOrEmpty(tuple.get("out_time"))) {
                String outTime = (!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())+":00";
                empAttendanceDTO.setOutTimeStatus(sessionName + " - " + outTime);
                empAttendanceDTO.setStatusName(sessionName);
                empAttendanceDTO.setLeaveSession("FD");
                empAttendanceDTO.setAbsent(false);
                empAttendanceDTO.setInTimeStatus(sessionName);
            } else if (!Utils.isNullOrEmpty(tuple.get("in_time")) && !Utils.isNullOrEmpty(tuple.get("out_time"))) {
                String inTime = (!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())+":00";
                String outTime = (!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())+":00";
                empAttendanceDTO.setInTimeStatus(sessionName + " - " + inTime);
                empAttendanceDTO.setOutTimeStatus(sessionName + " - " + outTime);
                empAttendanceDTO.setStatusName(sessionName);
                empAttendanceDTO.setLeaveSession("FD");
                empAttendanceDTO.setAbsent(false);
            }
        }
        //forenoon session
        else if (session.trim().equalsIgnoreCase("FN")) {
            if (Utils.isNullOrEmpty(tuple.get("in_time")) && Utils.isNullOrEmpty(tuple.get("out_time"))) {
                empAttendanceDTO.setInTimeStatus(sessionName);
                empAttendanceDTO.setStatusName(sessionName + " (" + "Forenoon)");
                empAttendanceDTO.setLeaveSession("FN");
                empAttendanceDTO.setOutTimeStatus("Absent");
                empAttendanceDTO.setAbsent(true);
            } else if (!Utils.isNullOrEmpty(tuple.get("in_time")) && Utils.isNullOrEmpty(tuple.get("out_time"))) {
                String inTime = (!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())+":00";
                empAttendanceDTO.setInTimeStatus(sessionName + " - " + inTime);
                empAttendanceDTO.setStatusName(sessionName + " (" + "Forenoon)");
                empAttendanceDTO.setLeaveSession("FN");
                empAttendanceDTO.setOutTimeStatus("Absent");
                empAttendanceDTO.setAbsent(true);
            } else if (Utils.isNullOrEmpty(tuple.get("in_time")) && !Utils.isNullOrEmpty(tuple.get("out_time"))) {
                empAttendanceDTO.setInTimeStatus(sessionName);
                empAttendanceDTO.setStatusName(sessionName + " (" + "Forenoon)");
                empAttendanceDTO.setLeaveSession("FN");
            } else if (!Utils.isNullOrEmpty(tuple.get("in_time")) && !Utils.isNullOrEmpty(tuple.get("out_time"))) {
                String inTime = (!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())+":00";
                empAttendanceDTO.setInTimeStatus(sessionName + " - " + inTime);
                empAttendanceDTO.setStatusName(sessionName + " (" + "Forenoon)");
                empAttendanceDTO.setLeaveSession("FN");
            }
        }
        //afternoon
        else if (session.trim().equalsIgnoreCase("AN")) {
            if (Utils.isNullOrEmpty(tuple.get("in_time")) && Utils.isNullOrEmpty(tuple.get("out_time"))) {
                empAttendanceDTO.setOutTimeStatus(sessionName);
                empAttendanceDTO.setStatusName(sessionName + " (" + "Afternoon)");
                empAttendanceDTO.setLeaveSession("AN");
                empAttendanceDTO.setInTimeStatus("Absent");
                empAttendanceDTO.setAbsent(true);
            } else if (!Utils.isNullOrEmpty(tuple.get("in_time")) && Utils.isNullOrEmpty(tuple.get("out_time"))) {
                empAttendanceDTO.setOutTimeStatus(sessionName);
                empAttendanceDTO.setStatusName(sessionName + " (" + "Afternoon)");
                empAttendanceDTO.setLeaveSession("AN");
            } else if (Utils.isNullOrEmpty(tuple.get("in_time")) && !Utils.isNullOrEmpty(tuple.get("out_time"))) {
                String outTime = (!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())+":00";
                empAttendanceDTO.setOutTimeStatus(sessionName + " - " + outTime);
                empAttendanceDTO.setStatusName(sessionName + " (" + "Afternoon)");
                empAttendanceDTO.setLeaveSession("AN");
                empAttendanceDTO.setInTimeStatus("Absent");
                empAttendanceDTO.setAbsent(true);
            } else if (!Utils.isNullOrEmpty(tuple.get("in_time")) && !Utils.isNullOrEmpty(tuple.get("out_time"))) {
                String outTime = (!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())+":00";
                empAttendanceDTO.setOutTimeStatus(sessionName + " - " + outTime);
                empAttendanceDTO.setStatusName(sessionName + " (" + "Afternoon)");
                empAttendanceDTO.setLeaveSession("AN");
            }
        }
        return empAttendanceDTO;
    }

    public EmpAttendanceDTO checkConditionLeaveForDiff(Tuple tuple, String ANsession, String ANsessionName,String FNsession, String FNsessionName, EmpAttendanceDTO empAttendanceDTO) {
        if (ANsession.trim().equalsIgnoreCase("AN") && FNsession.trim().equalsIgnoreCase("FN")) {
            if (Utils.isNullOrEmpty(tuple.get("in_time")) && Utils.isNullOrEmpty(tuple.get("out_time"))) {
                empAttendanceDTO.setInTimeStatus(FNsessionName);
                empAttendanceDTO.setOutTimeStatus(ANsessionName);
                empAttendanceDTO.setStatusName(FNsessionName + " (" + "Forenoon), " +ANsessionName+ " (" + "Afternoon)");
                empAttendanceDTO.setLeaveSession("FD");
                empAttendanceDTO.setAbsent(false);
            } else if (!Utils.isNullOrEmpty(tuple.get("in_time")) && Utils.isNullOrEmpty(tuple.get("out_time"))) {
                String inTime = (!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())+":00";
                empAttendanceDTO.setInTimeStatus(FNsessionName + " - " + inTime);
                empAttendanceDTO.setOutTimeStatus(ANsessionName);
                empAttendanceDTO.setStatusName(FNsessionName + " (" + "Forenoon), " +ANsessionName+ " (" + "Afternoon)");
                empAttendanceDTO.setLeaveSession("FD");
                empAttendanceDTO.setAbsent(false);
            } else if (Utils.isNullOrEmpty(tuple.get("in_time")) && !Utils.isNullOrEmpty(tuple.get("out_time"))) {
                String outTime = (!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())+":00";
                empAttendanceDTO.setInTimeStatus(FNsessionName);
                empAttendanceDTO.setOutTimeStatus(ANsessionName + " - " + outTime);
                empAttendanceDTO.setStatusName(FNsessionName + " (" + "Forenoon), " +ANsessionName+ " (" + "Afternoon)");
                empAttendanceDTO.setLeaveSession("FD");
                empAttendanceDTO.setAbsent(false);
            } else if (!Utils.isNullOrEmpty(tuple.get("in_time")) && !Utils.isNullOrEmpty(tuple.get("out_time"))) {
                String outTime = (!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())+":00";
                String inTime = (!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())+":00";
                empAttendanceDTO.setInTimeStatus(FNsessionName + " - " + inTime);
                empAttendanceDTO.setOutTimeStatus(ANsessionName + " - " + outTime);
                empAttendanceDTO.setStatusName(FNsessionName + " (" + "Forenoon), " +ANsessionName+ " (" + "Afternoon)");
                empAttendanceDTO.setLeaveSession("FD");
                empAttendanceDTO.setAbsent(false);
            }
        }
        return empAttendanceDTO;
    }

    public EmpAttendanceDTO checkConditionLeaveAndExemption(Tuple tuple, String leaveSession, String leaveSessionName, String holidaySession, String holidaySessionName, EmpAttendanceDTO empAttendanceDTO) {
        if (leaveSession.trim().equalsIgnoreCase("FD") && (holidaySession.trim().equalsIgnoreCase("FD") || holidaySession.trim().equalsIgnoreCase("AN") || holidaySession.trim().equalsIgnoreCase("FN"))) {
            if (Utils.isNullOrEmpty(tuple.get("in_time")) && Utils.isNullOrEmpty(tuple.get("out_time"))) {
                empAttendanceDTO.setInTimeStatus(leaveSessionName);
                empAttendanceDTO.setStatusName(leaveSessionName);
                empAttendanceDTO.setLeaveSession("FD");
            } else if (!Utils.isNullOrEmpty(tuple.get("in_time")) && Utils.isNullOrEmpty(tuple.get("out_time"))) {
                String inTime = (!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())+":00";
                empAttendanceDTO.setInTimeStatus(leaveSessionName + " - " + inTime);
                empAttendanceDTO.setStatusName(leaveSessionName);
                empAttendanceDTO.setLeaveSession("FD");
            } else if (Utils.isNullOrEmpty(tuple.get("in_time")) && !Utils.isNullOrEmpty(tuple.get("out_time"))) {
                String outTime = (!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())+":00";
                empAttendanceDTO.setOutTimeStatus(leaveSessionName + " - " + outTime);
                empAttendanceDTO.setStatusName(leaveSessionName);
                empAttendanceDTO.setLeaveSession("FD");
            } else if (!Utils.isNullOrEmpty(tuple.get("in_time")) && !Utils.isNullOrEmpty(tuple.get("out_time"))) {
                String outTime = (!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())+":00";
                String inTime = (!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())+":00";
                empAttendanceDTO.setInTimeStatus(leaveSessionName + " - " + inTime);
                empAttendanceDTO.setOutTimeStatus(leaveSessionName + " - " + outTime);
                empAttendanceDTO.setStatusName(leaveSessionName);
                empAttendanceDTO.setLeaveSession("FD");
            }
        }
        //forenoon session
        else if (leaveSession.trim().equalsIgnoreCase("FN") && holidaySession.trim().equalsIgnoreCase("AN")) {
            if (Utils.isNullOrEmpty(tuple.get("in_time")) && Utils.isNullOrEmpty(tuple.get("out_time"))) {
                empAttendanceDTO.setInTimeStatus(leaveSessionName);
                empAttendanceDTO.setStatusName(leaveSessionName + " (Forenoon)");
                empAttendanceDTO.setLeaveSession("FN");
                empAttendanceDTO.setHolidayEventsSession("AN");
                empAttendanceDTO.setOutTimeStatus(holidaySessionName + " (Afternoon)");
            } else if (!Utils.isNullOrEmpty(tuple.get("in_time")) && Utils.isNullOrEmpty(tuple.get("out_time"))) {
                String inTime = (!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())+":00";
                empAttendanceDTO.setInTimeStatus(leaveSessionName + " - " + inTime);
                empAttendanceDTO.setStatusName(leaveSessionName + " (" + "Forenoon)");
                empAttendanceDTO.setLeaveSession("FN");
                empAttendanceDTO.setOutTimeStatus(holidaySessionName + " (Afternoon)");
            } else if (Utils.isNullOrEmpty(tuple.get("in_time")) && !Utils.isNullOrEmpty(tuple.get("out_time"))) {
                String outTime = (!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())+":00";
                empAttendanceDTO.setInTimeStatus(leaveSessionName);
                empAttendanceDTO.setStatusName(leaveSessionName + " (" + "Forenoon)");
                empAttendanceDTO.setLeaveSession("FN");
                empAttendanceDTO.setOutTimeStatus(holidaySessionName + " - " + outTime);
            } else if (!Utils.isNullOrEmpty(tuple.get("in_time")) && !Utils.isNullOrEmpty(tuple.get("out_time"))) {
                String outTime = (!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())+":00";
                String inTime = (!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())+":00";
                empAttendanceDTO.setInTimeStatus(leaveSessionName + " - " + inTime);
                empAttendanceDTO.setStatusName(leaveSessionName + " (" + "Forenoon)");
                empAttendanceDTO.setLeaveSession("FN");
                empAttendanceDTO.setOutTimeStatus(holidaySessionName + " - " + outTime);
            }
        }
        //afternoon
        else if (leaveSession.trim().equalsIgnoreCase("AN") && holidaySession.trim().equalsIgnoreCase("FN")) {
            if (Utils.isNullOrEmpty(tuple.get("in_time")) && Utils.isNullOrEmpty(tuple.get("out_time"))) {
                empAttendanceDTO.setOutTimeStatus(leaveSessionName);
                empAttendanceDTO.setStatusName(leaveSessionName + " (" + "Afternoon)");
                empAttendanceDTO.setLeaveSession("AN");
                empAttendanceDTO.setInTimeStatus(holidaySessionName);
            } else if (!Utils.isNullOrEmpty(tuple.get("in_time")) && Utils.isNullOrEmpty(tuple.get("out_time"))) {
                String inTime = (!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())+":00";
                empAttendanceDTO.setOutTimeStatus(leaveSessionName);
                empAttendanceDTO.setStatusName(leaveSessionName + " (" + "Afternoon)");
                empAttendanceDTO.setLeaveSession("AN");
                empAttendanceDTO.setInTimeStatus(holidaySessionName + " - " + inTime);
            } else if (Utils.isNullOrEmpty(tuple.get("in_time")) && !Utils.isNullOrEmpty(tuple.get("out_time"))) {
                String outTime = (!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())+":00";
                empAttendanceDTO.setOutTimeStatus(leaveSessionName + " - " + outTime);
                empAttendanceDTO.setStatusName(leaveSessionName + " (" + "Afternoon)");
                empAttendanceDTO.setLeaveSession("AN");
                empAttendanceDTO.setInTimeStatus(holidaySessionName);
            } else if (!Utils.isNullOrEmpty(tuple.get("in_time")) && !Utils.isNullOrEmpty(tuple.get("out_time"))) {
                String outTime = (!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("out_time").toString())+":00";
                String inTime = (!Utils.isNullOrEmpty(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString()).getSecond()))  ? String.valueOf(Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())) : Utils.convertStringTimeToLocalTime(tuple.get("in_time").toString())+":00";
                empAttendanceDTO.setInTimeStatus(holidaySessionName + " - " + inTime);
                empAttendanceDTO.setStatusName(leaveSessionName + " (" + "Afternoon)");
                empAttendanceDTO.setLeaveSession("AN");
                empAttendanceDTO.setOutTimeStatus(leaveSessionName + " - " + outTime);
            }
        }
        return empAttendanceDTO;
    }

//	private static volatile ViewEmployeeAttendanceHandler viewEmployeeAttendanceHandler = null;
//	ViewEmployeeAttendanceTransaction viewEmployeeAttendanceTransaction=ViewEmployeeAttendanceTransaction.getInstance();
//
//	public static ViewEmployeeAttendanceHandler getInstance() {
//		if (viewEmployeeAttendanceHandler == null) {
//			viewEmployeeAttendanceHandler = new ViewEmployeeAttendanceHandler();
//		}
//		return viewEmployeeAttendanceHandler;
//	}
//
//	public ApiResult<List<EmpAttendanceDTO>> getAttendanceDataForEmployee(Map<String, String> requestParams,
//																		  ApiResult<List<EmpAttendanceDTO>> result) throws Exception {
//		List<Tuple> mappings =viewEmployeeAttendanceTransaction.getAttendanceDetailsForEmployee(requestParams);
//		if (mappings != null && mappings.size() > 0) {
//			result.success = true;
//			result.dto = new ArrayList<>();
//			for (Tuple mapping : mappings) {
//				EmpAttendanceDTO mappingInfo = new EmpAttendanceDTO();
//				if(!Utils.isNullOrEmpty(mapping.get("date"))) {
//		           mappingInfo.date = Utils.convertLocalDateTimeToStringDate(Utils.convertStringDateTimeToLocalDateTime(mapping.get("date").toString()));
//				}
//				if(!Utils.isNullOrEmpty(mapping.get("dayName")))
//					mappingInfo.dayName = mapping.get("dayName").toString();
//				if(!Utils.isNullOrEmpty(mapping.get("timeIn")))
//					mappingInfo.timeIn = Utils.convertLocalDateTimeToStringTime(Utils.convertStringDateTimeToLocalDateTime(mapping.get("timeIn").toString()));
//				if(!Utils.isNullOrEmpty(mapping.get("timeOut")))
//					mappingInfo.timeOut = Utils.convertLocalDateTimeToStringTime(Utils.convertStringDateTimeToLocalDateTime(mapping.get("timeOut").toString()));
////				if(!Utils.isNullOrEmpty(mapping.get("nextDay")))
////					mappingInfo.isNextDay=(boolean) mapping.get("nextDay");
//				if(!Utils.isNullOrEmpty(mapping.get("earlyExit")))
//					mappingInfo.isEarlyExit=(boolean) mapping.get("earlyExit");
//				if(!Utils.isNullOrEmpty(mapping.get("lateEntry")))
//					mappingInfo.isLateEntry=(boolean) mapping.get("lateEntry");
//				if(!Utils.isNullOrEmpty(mapping.get("isSundayWorking")))
//					mappingInfo.isSundayWorking=(boolean) mapping.get("isSundayWorking");
////				if(!Utils.isNullOrEmpty(mapping.get("isWeeklyOff")))
////					mappingInfo.isWeeklyOff=(boolean) mapping.get("isWeeklyOff");
//				if(!Utils.isNullOrEmpty(mapping.get("isExempted")))
//					mappingInfo.isExempted=(boolean) mapping.get("isExempted");
//				if(!Utils.isNullOrEmpty(mapping.get("leaveType")))
//					mappingInfo.leaveType=mapping.get("leaveType").toString();
//				if(!Utils.isNullOrEmpty(mapping.get("leaveTypeColor")))
//					mappingInfo.leaveTypeColor=mapping.get("leaveTypeColor").toString();
//				if(!Utils.isNullOrEmpty(mapping.get("leaveSession")))
//					mappingInfo.leaveSession=mapping.get("leaveSession").toString();
//				if(!Utils.isNullOrEmpty(mapping.get("empHolidayEventsType")))
//					mappingInfo.empHolidayEventsType=mapping.get("empHolidayEventsType").toString();
//				if(!Utils.isNullOrEmpty(mapping.get("holidayEventsDescription")))
//					mappingInfo.holidayEventsDescription=mapping.get("holidayEventsDescription").toString();
//				if(!Utils.isNullOrEmpty(mapping.get("holidayOrVacationOrExemptionSession")))
//					mappingInfo.holidayOrVacationOrExemptionSession=mapping.get("holidayOrVacationOrExemptionSession").toString();
//				if(Utils.isNullOrEmpty(mapping.get("timeIn")) && Utils.isNullOrEmpty(mapping.get("timeOut")) && Utils.isNullOrEmpty(mapping.get("leaveEntryId"))
//						&& Utils.isNullOrEmpty(mapping.get("holidayEventsId"))){
//					mappingInfo.isAbsent=true;
//				}else {
//					mappingInfo.isAbsent=false;
//				}
//				if(!Utils.isNullOrEmpty(mapping.get("leaveEntryId"))){
//					mappingInfo.isLeave=true;
//				}else {
//					mappingInfo.isLeave=false;
//				}
//					result.dto.add(mappingInfo);
//			}
//		}else {
//			result.failureMessage = "No data available for this month";
//		}
//		return result;
//	}
//
//	public ApiResult<List<EmpAttendanceDTO>> getAttendanceDataForEmployeeCumulative(Map<String, String> requestParams,
//																					ApiResult<List<EmpAttendanceDTO>> result) throws Exception {
//		List<Tuple> mappings =viewEmployeeAttendanceTransaction.getAttendanceDetailsForEmployee(requestParams);
//		float present=0;
//		float absent=0;
//		float leave=0;
//		float lateEntry=0;
//		float earlyExit=0;
//		float regular=0;
//		int i=1;
//		float j=(float) 0.5;
//		Map<String,Float> map=new LinkedHashMap<String,Float>();
//		List<EmpAttendanceDTO> presentDetails=new LinkedList<EmpAttendanceDTO>();
//		List<EmpAttendanceDTO> leaveDetails=new LinkedList<EmpAttendanceDTO>();
//		List<EmpAttendanceDTO> absentDetails=new LinkedList<EmpAttendanceDTO>();
//		if (mappings != null && mappings.size() > 0) {
//			result.dto = new ArrayList<>();
//			for (Tuple mapping : mappings) {
//				if((!Utils.isNullOrEmpty(mapping.get("dayName")) && ! mapping.get("dayName").equals("Sunday")) || (!Utils.isNullOrEmpty(mapping.get("dayName"))
//					&& mapping.get("dayName").equals("Sunday") && !Utils.isNullOrEmpty(mapping.get("isSundayWorking")) && mapping.get("isSundayWorking").equals(true))){
//					if(!Utils.isNullOrEmpty(mapping.get("timeIn")) || !Utils.isNullOrEmpty(mapping.get("timeOut"))){
//						EmpAttendanceDTO dto=new EmpAttendanceDTO();
//						dto.date= Utils.convertLocalDateTimeToStringDate(Utils.convertStringDateTimeToLocalDateTime(mapping.get("date").toString()));
//						if(!Utils.isNullOrEmpty(mapping.get("timeIn")))
//							dto.timeIn=Utils.convertLocalDateTimeToStringTime(Utils.convertStringDateTimeToLocalDateTime(mapping.get("timeIn").toString()));
//						if(!Utils.isNullOrEmpty(mapping.get("timeOut")))
//							dto.timeOut=Utils.convertLocalDateTimeToStringTime(Utils.convertStringDateTimeToLocalDateTime(mapping.get("timeOut").toString()));
//						if(!Utils.isNullOrEmpty(mapping.get("leaveSession"))){
//							present= (float) (present+ 0.5);
//							dto.absenceRemark="Half Day Leave (".concat((String) mapping.get("leaveType"))+")";
//						}else {
//							if(((!Utils.isNullOrEmpty(mapping.get("timeIn")) && Utils.isNullOrEmpty(mapping.get("timeOut"))) || (Utils.isNullOrEmpty(mapping.get("timeIn")) && !Utils.isNullOrEmpty(mapping.get("timeOut"))))
//									 && Utils.isNullOrEmpty(mapping.get("leaveEntryId")) && Utils.isNullOrEmpty(mapping.get("leaveSession"))){
//								present= (float) (present+ 0.5);
//								dto.absenceRemark="Half Day Absent";
//							}else {
//								present++;
//							}
//						}
//						if((Utils.isNullOrEmpty(mapping.get("lateEntry")) &&  Utils.isNullOrEmpty(mapping.get("earlyExit"))
//							|| (!Utils.isNullOrEmpty(mapping.get("lateEntry")) && (boolean) mapping.get("lateEntry")==false && !Utils.isNullOrEmpty(mapping.get("earlyExit"))
//							&& (boolean) mapping.get("earlyExit")==false))){
//							if(!Utils.isNullOrEmpty(mapping.get("leaveSession")))
//								regular= (float) (regular+ 0.5);
//							else {
//								if(((!Utils.isNullOrEmpty(mapping.get("timeIn")) && Utils.isNullOrEmpty(mapping.get("timeOut"))) || (Utils.isNullOrEmpty(mapping.get("timeIn")) && !Utils.isNullOrEmpty(mapping.get("timeOut"))))
//										 && Utils.isNullOrEmpty(mapping.get("leaveEntryId")) && Utils.isNullOrEmpty(mapping.get("leaveSession"))){
//									regular= (float) (regular+ 0.5);
//								}else {
//									regular++;
//								}
//							}
//						}
//						if(!Utils.isNullOrEmpty(mapping.get("lateEntry")) && (boolean) mapping.get("lateEntry")==true
//							&& !Utils.isNullOrEmpty(mapping.get("earlyExit")) && (boolean) mapping.get("earlyExit")==true){
//							dto.status="Late Entry/Early Exit";
//							if(!Utils.isNullOrEmpty(mapping.get("leaveSession"))){
//								lateEntry= (float) (lateEntry+ 0.25);
//								earlyExit= (float) (earlyExit+ 0.25);
//							}else {
//								lateEntry= (float) (lateEntry+ 0.5);
//								earlyExit= (float) (earlyExit+ 0.5);
//							}
//						}else {
//							if(!Utils.isNullOrEmpty(mapping.get("lateEntry")) && (boolean) mapping.get("lateEntry")==true){
//								dto.status="Late Entry";
//								if(!Utils.isNullOrEmpty(mapping.get("leaveSession")))
//									lateEntry= (float) (lateEntry+ 0.5);
//								else {
//									if(((!Utils.isNullOrEmpty(mapping.get("timeIn")) && Utils.isNullOrEmpty(mapping.get("timeOut"))) || (Utils.isNullOrEmpty(mapping.get("timeIn")) && !Utils.isNullOrEmpty(mapping.get("timeOut"))))
//											 && Utils.isNullOrEmpty(mapping.get("leaveEntryId")) && Utils.isNullOrEmpty(mapping.get("leaveSession"))){
//										lateEntry= (float) (lateEntry+ 0.5);
//									}else {
//										lateEntry++;
//									}
//								}
//							}
//							if(!Utils.isNullOrEmpty(mapping.get("earlyExit")) && (boolean) mapping.get("earlyExit")==true){
//								dto.status="Early Exit";
//								if(!Utils.isNullOrEmpty(mapping.get("leaveSession")))
//									earlyExit= (float) (earlyExit+ 0.5);
//								else {
//									if(((!Utils.isNullOrEmpty(mapping.get("timeIn")) && Utils.isNullOrEmpty(mapping.get("timeOut"))) || (Utils.isNullOrEmpty(mapping.get("timeIn")) && !Utils.isNullOrEmpty(mapping.get("timeOut"))))
//											 && Utils.isNullOrEmpty(mapping.get("leaveEntryId")) && Utils.isNullOrEmpty(mapping.get("leaveSession"))){
//										earlyExit= (float) (earlyExit+ 0.5);
//									}else {
//										earlyExit++;
//									}
//								}
//							}
//						}
//						presentDetails.add(dto);
//					}
//					if(!Utils.isNullOrEmpty(mapping.get("leaveEntryId"))){
//						EmpAttendanceDTO dto=new EmpAttendanceDTO();
//						dto.date= Utils.convertLocalDateTimeToStringDate(Utils.convertStringDateTimeToLocalDateTime(mapping.get("date").toString()));
//						if(!Utils.isNullOrEmpty(mapping.get("timeIn")))
//							dto.timeIn=Utils.convertLocalDateTimeToStringTime(Utils.convertStringDateTimeToLocalDateTime(mapping.get("timeIn").toString()));
//						if(!Utils.isNullOrEmpty(mapping.get("timeOut")))
//							dto.timeOut= Utils.convertLocalDateTimeToStringTime(Utils.convertStringDateTimeToLocalDateTime(mapping.get("timeOut").toString()));
//						if(!Utils.isNullOrEmpty(mapping.get("leaveType")))
//							dto.leaveType=(String) mapping.get("leaveType");
//						if(!Utils.isNullOrEmpty(mapping.get("leaveSession"))){
//							leave= (float) (leave+ 0.5);
//							dto.status="Half Day";
//						}
//						else{
//							leave++;
//							dto.status="Full Day";
//						}
//						leaveDetails.add(dto);
//						if(!Utils.isNullOrEmpty(mapping.get("leaveType"))) {
//							if(map.containsKey(mapping.get("leaveType"))) {
//								if(!Utils.isNullOrEmpty(mapping.get("leaveSession")))
//									map.put((String) mapping.get("leaveType")+"-"+mapping.get("leaveTypeCode"), (map.get(mapping.get("leaveType")+"-"+mapping.get("leaveTypeCode")) +(float) 0.5));
//								else
//									map.put((String) mapping.get("leaveType")+"-"+mapping.get("leaveTypeCode"), map.get(mapping.get("leaveType")+"-"+mapping.get("leaveTypeCode")) + 1);
//							}else {
//								if(!Utils.isNullOrEmpty(mapping.get("leaveSession")))
//									map.put((String) mapping.get("leaveType")+"-"+mapping.get("leaveTypeCode"),(float) j);
//								else
//									map.put((String) mapping.get("leaveType")+"-"+mapping.get("leaveTypeCode"),(float)i);
//							}
//						}
//					}
//					if(Utils.isNullOrEmpty(mapping.get("timeIn")) && Utils.isNullOrEmpty(mapping.get("timeOut")) && Utils.isNullOrEmpty(mapping.get("leaveEntryId"))
//							&& Utils.isNullOrEmpty(mapping.get("holidayEventsId"))) {
//						EmpAttendanceDTO dto=new EmpAttendanceDTO();
//						absent++;
//						dto.date= Utils.convertLocalDateTimeToStringDate(Utils.convertStringDateTimeToLocalDateTime(mapping.get("date").toString()));
//						if(!Utils.isNullOrEmpty(mapping.get("timeIn")))
//							dto.timeIn= Utils.convertLocalDateTimeToStringTime(Utils.convertStringDateTimeToLocalDateTime(mapping.get("timeIn").toString()));
//						if(!Utils.isNullOrEmpty(mapping.get("timeOut")))
//							dto.timeOut= Utils.convertLocalDateTimeToStringTime(Utils.convertStringDateTimeToLocalDateTime(mapping.get("timeOut").toString()));
//							absentDetails.add(dto);
//					}
//					if(((!Utils.isNullOrEmpty(mapping.get("timeIn")) && Utils.isNullOrEmpty(mapping.get("timeOut"))) || (Utils.isNullOrEmpty(mapping.get("timeIn")) && !Utils.isNullOrEmpty(mapping.get("timeOut"))))
//							&& Utils.isNullOrEmpty(mapping.get("leaveEntryId")) && Utils.isNullOrEmpty(mapping.get("leaveSession"))){
//						EmpAttendanceDTO dto=new EmpAttendanceDTO();
//						absent= (float) (absent+ 0.5);
//						dto.date= Utils.convertLocalDateTimeToStringDate(Utils.convertStringDateTimeToLocalDateTime(mapping.get("date").toString()));
//						if(!Utils.isNullOrEmpty(mapping.get("timeIn")))
//							dto.timeIn= Utils.convertLocalDateTimeToStringTime(Utils.convertStringDateTimeToLocalDateTime(mapping.get("timeIn").toString()));
//						if(!Utils.isNullOrEmpty(mapping.get("timeOut")))
//							dto.timeOut= Utils.convertLocalDateTimeToStringTime(Utils.convertStringDateTimeToLocalDateTime(mapping.get("timeOut").toString()));
//					 	dto.status="Half Day";
//						absentDetails.add(dto);
//					}
//				}
//			}
//			List<AttendanceCumulativeDTO> cumulativeData= new ArrayList<AttendanceCumulativeDTO>();
//			AttendanceCumulativeDTO cumulativePresent= new AttendanceCumulativeDTO();
//			cumulativePresent.name="Present";
//			cumulativePresent.value=(present);
//			cumulativeData.add(cumulativePresent);
//
//			AttendanceCumulativeDTO cumulativeAbsent= new AttendanceCumulativeDTO();
//			cumulativeAbsent.name="Absent";
//			cumulativeAbsent.value=(absent);
//			cumulativeData.add(cumulativeAbsent);
//
//			AttendanceCumulativeDTO cumulativeLeave= new AttendanceCumulativeDTO();
//			cumulativeLeave.name="Leave";
//			cumulativeLeave.value=(leave);
//			cumulativeData.add(cumulativeLeave);
//
//			List<AttendanceCumulativeDTO> presentData= new ArrayList<AttendanceCumulativeDTO>();
//			AttendanceCumulativeDTO cumulativeRegular= new AttendanceCumulativeDTO();
//			cumulativeRegular.name="Regular";
//			cumulativeRegular.value=(regular);
//			presentData.add(cumulativeRegular);
//
//			AttendanceCumulativeDTO cumulativeEarlyExit= new AttendanceCumulativeDTO();
//			cumulativeEarlyExit.name="Late Entry";
//			cumulativeEarlyExit.value=(earlyExit);
//			presentData.add(cumulativeEarlyExit);
//
//			AttendanceCumulativeDTO cumulativeLateExit= new AttendanceCumulativeDTO();
//			cumulativeLateExit.name="Early Exit";
//			cumulativeLateExit.value=(lateEntry);
//			presentData.add(cumulativeLateExit);
//
//			List<AttendanceCumulativeDTO> absentData= new ArrayList<AttendanceCumulativeDTO>();
//			AttendanceCumulativeDTO cumulativeAbsentData= new AttendanceCumulativeDTO();
//			cumulativeAbsentData.name="Absent";
//			cumulativeAbsentData.value=(absent);
//			absentData.add(cumulativeAbsentData);
//
//			List<AttendanceCumulativeDTO> leaveData= new ArrayList<AttendanceCumulativeDTO>();
//			Iterator<String> it = map.keySet().iterator();
//			while(it.hasNext())
//			{
//				String key=it.next();
//				AttendanceCumulativeDTO cumulativeLeave2= new AttendanceCumulativeDTO();
//				String[] key1=key.split("-");
//				cumulativeLeave2.name=key1[1];
//				cumulativeLeave2.leaveTypeName=key1[0];
//				cumulativeLeave2.value=(map.get(key));
//				leaveData.add(cumulativeLeave2);
//			}
//
//			Map<String, List<EmpAttendanceDTO>> cumulativeList = new HashMap<String, List<EmpAttendanceDTO>>();
//			cumulativeList.put("Present", presentDetails);
//			cumulativeList.put("Absent", absentDetails);
//			cumulativeList.put("Leave", leaveDetails);
//
//			EmpAttendanceDTO mappingInfo = new EmpAttendanceDTO();
//			mappingInfo.cumulativeData=cumulativeData;
//			mappingInfo.presentData=presentData;
//			mappingInfo.absentData=absentData;
//			mappingInfo.leaveData=leaveData;
//			mappingInfo.cumulativeList=cumulativeList;
//			result.dto.add(mappingInfo);
//			result.success = true;
//		}else {
//			result.failureMessage = "No data available for selected date range";
//		}
//		return result;
//	}
}
