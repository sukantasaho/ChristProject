package com.christ.erp.services.dbobjects.hostel.leavesandattendance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelHolidayEventsDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "hostel_attendance")
@Getter
@Setter
public class HostelAttendanceDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "hostel_attendance_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "hostel_admissions_id")
    private HostelAdmissionsDBO hostelAdmissionsDBO;
    
    @Column(name = "attendance_date")
    private LocalDate attendanceDate;
    
    @Column(name = "morning_time")
    private LocalTime morningTime;
    
    @Column(name = "evening_time")
    private LocalTime eveningTime;
    
	@ManyToOne
	@JoinColumn(name = "hostel_leave_entry_id")
    private HostelLeaveApplicationsDBO hostelLeaveApplicationsDBO;
	
	@Column(name = "leave_session")
	public String leaveSession;
	
	@ManyToOne
	@JoinColumn(name = "hostel_holiday_events_id")
    private HostelHolidayEventsDBO hostelHolidayEventsDBO;
	
	@Column(name = "holiday_event_session")
	public String holidayEventSession;
	
	@ManyToOne
	@JoinColumn(name = "hostel_punching_exemption_id")
    private HostelPunchingExemptionDBO hostelPunchingExemptionDBO;
	
	@Column(name = "exempted_session")
	public String exemptedSession;
	
	@ManyToOne
	@JoinColumn(name = "student_erp_work_flow_process_id")
    private ErpWorkFlowProcessDBO studentErpWorkFlowProcessDBO;
	
	@ManyToOne
	@JoinColumn(name = "parent_erp_work_flow_process_id")
    private ErpWorkFlowProcessDBO parentErpWorkFlowProcessDBO;
	
    @Column(name = "created_users_id",updatable=false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;

}
