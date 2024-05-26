package com.christ.erp.services.dbobjects.employee.common;

import java.io.Serializable;

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
@Table(name = "emp_employee_group")
@Setter
@Getter
public class EmpEmployeeGroupDBO implements Serializable
{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_employee_group_id")
	public Integer id;
	
	@Column(name="employee_group_name")
	public String employeeGroupName;
	
	@ManyToOne
	@JoinColumn(name = "emp_employee_category_id")
	public EmpEmployeeCategoryDBO empEmployeeCategoryDBO;
	
	@Column(name="created_users_id",updatable = false)
	public Integer createdUsersId;
	
	@Column(name="modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name="record_status")
	public Character recordStatus;	
	
}
	