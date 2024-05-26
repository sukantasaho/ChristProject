package com.christ.erp.services.dbobjects.employee.leave;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.employee.common.EmpDBO;

import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "emp_leave_entry_details")
@Getter
@Setter
public class EmpLeaveEntryDetailsDBO implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_leave_entry_details_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "emp_leave_entry_id")
	private EmpLeaveEntryDBO empLeaveEntryId;

	@ManyToOne
	@JoinColumn(name = "emp_id")
	private EmpDBO empId;
	
	@ManyToOne
	@JoinColumn(name = "emp_leave_type_id")
	private EmpLeaveTypeDBO leaveTypeId;
	
	@Column(name = "leave_date")
	private LocalDate date;

	@Column(name = "leave_session")
	private String session;

	@Column(name = "number_of_days_leave")
	private BigDecimal totalDays;
	
	@Column(name = "emp_holiday_events_id")
	public Integer holidayEventId;
	
	@Column(name = "is_sunday")
	public boolean isSunday;
	
	@Column(name = "created_users_id", updatable = false)
	private Integer createdUsersId;

	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;

	@Column(name = "record_status")
	private char recordStatus;
	
	
}
