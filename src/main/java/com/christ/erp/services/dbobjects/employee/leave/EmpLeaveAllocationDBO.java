package com.christ.erp.services.dbobjects.employee.leave;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name= "emp_leave_allocation")
@Getter
@Setter
public class EmpLeaveAllocationDBO implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_leave_allocation_id")
	public Integer id;
	
	@ManyToOne
	@JoinColumn(name="emp_id")
	public EmpDBO empDBO;
	
	@ManyToOne//(fetch = FetchType.LAZY)
	@JoinColumn(name="emp_leave_type_id")
	public EmpLeaveTypeDBO leaveType;
	
	@Column(name="leaves_allocated")
	public BigDecimal allottedLeaves;
	
	@Column(name="leaves_sanctioned")
	public BigDecimal sanctionedLeaves;
	
	@Column(name="leaves_remaining")
	public BigDecimal leavesRemaining;
	
	@Column(name="leaves_pending")
	public BigDecimal leavesPending;
	
	@Column(name="year")
	public Integer year;
	
	@Column(name="month")
	public String month;
	
	@Column(name="created_users_id", updatable = false)
	public Integer createdUsersId;
	
	@Column(name="modified_users_id")
	public Integer modifiedUsersId;	
	
	@Column(name="record_status")
	public char recordStatus;

}
