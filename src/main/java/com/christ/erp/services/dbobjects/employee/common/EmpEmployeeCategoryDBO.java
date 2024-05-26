package com.christ.erp.services.dbobjects.employee.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "emp_employee_category")
@Setter
@Getter
public class EmpEmployeeCategoryDBO  {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emp_employee_category_id")
	public Integer id;

	@Column(name = "employee_category_name")
	public String employeeCategoryName;

	@Column(name = "is_employee_category_academic")
	public Boolean isEmployeeCategoryAcademic;

	@Column(name = "is_show_in_appln")
	public Boolean isShowInAppln;

	@Column(name = "created_users_id",updatable=false)
	public Integer createdUsersId;

	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;

	@Column(name = "record_status")
	public char recordStatus;
}
