package com.christ.erp.services.dbobjects.employee.attendance;

import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.*;

import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.leave.EmpLeaveEntryDBO;
import com.fasterxml.jackson.annotation.JacksonInject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "emp_attendance")
public class EmpAttendanceDBO {
  
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_attendance_id")
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "emp_id")
	private EmpDBO empId;
	
	@Column(name="attendance_date")
	private LocalDateTime attendanceDate;
	
	@Column(name="in_time")
	private LocalTime inTime;
	
	@Column(name="out_time")
	private  LocalTime outTime;

	@Column(name="is_late_entry")
	private Boolean isLateEntry;
	
	@Column(name="late_entry_by")
	private String lateEntryBy;
	
	@Column(name="is_early_exit")
	private Boolean isEarlyExit;
	
	@Column(name="early_exit_by")
	private Boolean earlyExitBy;

	@ManyToOne
	@JoinColumn(name = "emp_holiday_events_id")
	private EmpHolidayEventsDBO empHolidayEventsId;

	@Column(name="is_exempted")
	private Boolean isExempted;
	
	@Column(name="is_sunday_working")
	private Boolean isSundayWorking;
	
	@Column(name = "created_users_id")
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;
	
	@Column(name = "record_status")
	public char recordStatus;

	@ManyToOne
	@JoinColumn(name = "emp_time_zone_id")
	private EmpTimeZoneDBO empTimeZoneDBO;

	@Column(name = "total_hour")
	private LocalTime totalHour;

	@Column(name = "holiday_events_session")
	private String holidayEventsSession;

	@Column(name = "exempted_session")
	private String exemptedSession;

	@Column(name = "is_one_time_punch")
	private Boolean isOneTimePunch;

	@Column(name = "is_sunday")
	private Boolean isSunday;

	@ManyToOne
	@JoinColumn(name = "fn_emp_leave_entry_id")
	private EmpLeaveEntryDBO fnEmpLeaveEntryDBO;

	@ManyToOne
	@JoinColumn(name = "an_emp_leave_entry_id")
	private EmpLeaveEntryDBO anEmpLeaveEntryDBO;

}

