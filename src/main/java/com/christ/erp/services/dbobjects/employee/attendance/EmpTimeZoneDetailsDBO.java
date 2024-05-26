package com.christ.erp.services.dbobjects.employee.attendance;

import java.time.LocalTime;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "emp_time_zone_details")
@Setter
@Getter
public class EmpTimeZoneDetailsDBO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emp_time_zone_details_id")
	private int id;

	@ManyToOne
	@JoinColumn(name = "emp_time_zone_id")
	private EmpTimeZoneDBO empTimeZoneDBO;

	@Column(name = "is_exempted")
	private boolean isExempted;

	@Column(name = "day_name")
	private String dayName;

	@Column(name = "emp_in_time")
	private LocalTime empInTime;

	@Column(name = "in_time_ends")
	private LocalTime inTimeEnds;

	@Column(name = "emp_out_time")
	private LocalTime empOutTime;

	@Column(name = "half_day_start_time")
	private LocalTime halfDayStartTime;

	@Column(name = "half_day_end_time")
	private LocalTime halfDayEndTime;

	@Column(name = "record_status")
	private char recordStatus;
	
	@Column(name = "created_users_id")
	private Integer createdUsersId;

	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "time_in_start_time")
	private LocalTime timeInStartTime;
	
	@Column(name = "time_out_end_time")
	private LocalTime timeOutEndTime;

	@Column(name = "is_one_time_punch")
	private  boolean isOneTimePunch;

	@Column(name = "out_time_start")
	private LocalTime outTimeStart;
}
