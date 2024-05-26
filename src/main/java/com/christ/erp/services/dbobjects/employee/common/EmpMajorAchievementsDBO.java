package com.christ.erp.services.dbobjects.employee.common;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Table(name = "emp_major_achievements")
@Getter
@Setter
public class EmpMajorAchievementsDBO implements Serializable
{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_major_achievements_id")
	public Integer id;
	
	@Column(name="achievement")
	public String achievements;
	
	@ManyToOne
	@JoinColumn(name = "emp_id")
	public EmpDBO empDBO;
	
	@Column(name="entered_date")
	public LocalDateTime enteredDate;
	
	@Column(name="created_users_id",updatable = false)
	public Integer createdUsersId;

	@Column(name="modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name="record_status")
	public Character recordStatus;	
}
