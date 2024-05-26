package com.christ.erp.services.dbobjects.employee.salary;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="emp_pay_scale_components")
public class EmpPayScaleComponentsDBO implements Serializable {

	private static final long serialVersionUID = 3722455250633061387L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_pay_scale_components_id")
	public Integer id;
	
	@Column(name="salary_component_name")
	public String salaryComponentName;
	
	@Column(name="salary_component_short_name")
	public String salaryComponentShortName;
	
	@Column(name="pay_scale_type")
	public String payScaleType;
	
	@Column(name="is_component_basic")
	public Boolean isComponentBasic;
	
	@Column(name="salary_component_display_order")
	public Integer salaryComponentDisplayOrder;
	
	@Column(name="is_caculation_type_percentage")
	public Boolean isCalculationTypePercentage;
	
	@Column(name="percentage")
	public BigDecimal percentage;
	
	@Column(name="created_users_id", updatable = false)
	public Integer createdUsersId;
		
	@Column(name="modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name="record_status")
	public Character recordStatus;
}
