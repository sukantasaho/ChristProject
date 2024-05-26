package com.christ.erp.services.dbobjects.employee.salary;

import java.io.Serializable;
import java.math.BigDecimal;

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

@Entity
@Table(name="emp_pay_scale_details_components")
@Setter
@Getter
public class EmpPayScaleDetailsComponentsDBO implements Serializable{

	private static final long serialVersionUID = 3621043883904529206L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_pay_scale_details_components_id")
	public Integer id;
	
	@ManyToOne
	@JoinColumn(name = "emp_pay_scale_details_id")
	public EmpPayScaleDetailsDBO empPayScaleDetailsDBO ;
	
	@ManyToOne
	@JoinColumn(name = "emp_pay_scale_components_id")
	public EmpPayScaleComponentsDBO empPayScaleComponentsDBO;
	
	@Column(name="emp_salary_component_value")
	public BigDecimal empSalaryComponentValue;
	
	@Column(name="created_users_id", updatable = false)
	public Integer createdUsersId;
		
	@Column(name="modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name="record_status")
	public Character recordStatus;
}
