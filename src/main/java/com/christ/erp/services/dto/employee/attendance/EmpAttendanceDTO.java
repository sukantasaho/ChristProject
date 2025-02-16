package com.christ.erp.services.dto.employee.attendance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import com.christ.erp.services.dto.common.AttendanceCumulativeDTO;

import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.leave.EmpLeaveEntryDTO;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class EmpAttendanceDTO {
//	public String date;
	private String dayName;
//	public String timeIn;
//	public String timeOut;
//	public boolean isNextDay;
//	public boolean isLateEntry;
//	public boolean isEarlyExit;
	private String leaveTypeName;
//	public String leaveTypeColor;
//	public String leaveSession;
//	public String empHolidayEventsType;
//	public String holidayEventsDescription;
//	public String holidayOrVacationOrExemptionSession;
//	public boolean isAbsent;
//	public boolean isLeave;
//	public boolean isSundayWorking;
//	public List<AttendanceCumulativeDTO> cumulativeData;
//	public List<AttendanceCumulativeDTO> presentData;
//	public List<AttendanceCumulativeDTO> leaveData;
//	public List<AttendanceCumulativeDTO> absentData;
//	public Map<String,List<EmpAttendanceDTO>> cumulativeList;
//	public String status;
//	public String absenceRemark;
//	public boolean isWeeklyOff;
//	public boolean isExempted;
//	public String departmentName;
//	public List<String> campusNames;
//	public List<Integer> present;
//	public List<Integer> absent;
//	public List<Integer> halfDay;
//	public List<Integer> earlyExit;
//	public List<Integer> lateEntry;
	private SelectDTO empTimeZone;
	private LocalTime totalHour;
	private String holidayEventsSession;
	private String exemptedSession;
	private Boolean isOneTimePunch;
	private Boolean isSunday;
	private LocalDate attendanceDate;
	private LocalTime inTime;
	private LocalTime outTime;
	private Boolean isLateEntry;
	private LocalTime lateEntryBy;
	private Boolean isEarlyExit;
	private LocalTime earlyExitBy;
	private Integer empId;
	private Integer empLeaveEntryId;
	private boolean isExempted;
	private Boolean isSundayWorking;
	private LocalDate leaveStartDate;
	private LocalDate leaveEndDate;
	private String leaveStartSession;
	private String leaveEndSession;
	private String empHolidayEventsTypeName;
	private LocalDate holidayEventsStartDate;
	private LocalDate holidayEventsEndDate;
	private String empName;
	private String inTimeStatus;
	private String outTimeStatus;
	private String statusName;
	private String leaveSession;
	private boolean isLeave;
	private boolean isHoliday;
	private boolean isAbsent;
	private boolean isPresent;
}
