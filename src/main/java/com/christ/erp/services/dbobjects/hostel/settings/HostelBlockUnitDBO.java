package com.christ.erp.services.dbobjects.hostel.settings;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.common.ErpBlockDBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "hostel_block_unit")
public class HostelBlockUnitDBO implements Serializable {

	private static final long serialVersionUID = 6599918663023667736L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="hostel_block_unit_id")
	public Integer id;
	
	@ManyToOne
	@JoinColumn(name="hostel_block_id")
	public HostelBlockDBO hostelBlockDBO;

	@Column(name="hostel_unit")
	public String hostelUnit;
	
	@Column(name="total_floors")
	public Integer totalFloors;
	
	@Column(name="is_leave_submission_online")
	public Boolean isLeaveSubmissionOnline; 
	
	@Column(name = "leave_submission_next_day_by")
	public LocalTime leaveSubmissionNextDayBy;
	
	@Column(name = "leave_submission_saturday_by")
	public LocalTime leaveSubmissionSaturdayBy;
	
	@Column(name="is_sms_morning_absence")
	public Boolean isSmsMorningAbsence; 
	
	@Column(name="is_sms_evening_absence")
	public Boolean isSmsEveningAbsence; 
	
	@Column(name="is_email_morning_absence")
	public Boolean isEmailMorningAbsence; 
	
	@Column(name="is_email_evening_absence")
	public Boolean isEmailEveningAbsence; 
	
	@Column(name="is_punching_exemption_sunday_morning")
	public Boolean isPunchingExemptionSundayMorning; 
	
	@Column(name="is_punching_exemption_holiday_morning")
	public Boolean isPunchingExemptionHolidayMorning; 
	
	@Column(name="is_punching_exemption_sunday_evening")
	public Boolean isPunchingExemptionSundayEvening; 
	
	@Column(name="is_punching_exemption_holiday_evening")
	public Boolean isPunchingExemptionHolidayEvening; 
	
	@Column(name = "record_status")
	public char recordStatus;
	
	@Column(name = "created_users_id")
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@OneToMany(mappedBy ="hostelBlockUnitDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<HostelBlockUnitDetailsDBO> hostelBlockUnitDetailsDBOSet ;
	
	@OneToMany(mappedBy ="hostelBlockUnitDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<HostelFloorDBO> hostelFloorDBOSet = new HashSet<>();
}
