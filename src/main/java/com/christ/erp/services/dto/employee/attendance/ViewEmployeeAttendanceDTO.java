package com.christ.erp.services.dto.employee.attendance;

import java.util.List;
import java.util.Map;

import com.christ.erp.services.dto.common.AttendanceCumulativeDTO;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ViewEmployeeAttendanceDTO {
	public String date;
	public String dayName;
	public String timeIn;
	public String timeOut;
	public boolean isNextDay;
	public boolean isLateEntry;
	public boolean isEarlyExit;
	public String leaveType;
	public String leaveTypeColor;
	public String leaveSession;
	public String empHolidayEventsType;
	public String holidayEventsDescription;
	public String holidayOrVacationOrExemptionSession;
	public boolean isAbsent;
	public boolean isLeave;
	public boolean isSundayWorking;
	public List<AttendanceCumulativeDTO> cumulativeData;
	public List<AttendanceCumulativeDTO> presentData;
	public List<AttendanceCumulativeDTO> leaveData;
	public List<AttendanceCumulativeDTO> absentData;
	public Map<String,List<ViewEmployeeAttendanceDTO>> cumulativeList;
	public String status;
	public String absenceRemark;
	public boolean isWeeklyOff;
	public boolean isExempted;
	public String departmentName;
	public List<String> campusNames;
	public List<Integer> present;
	public List<Integer> absent;
	public List<Integer> halfDay;
	public List<Integer> earlyExit;
	public List<Integer> lateEntry;
}
