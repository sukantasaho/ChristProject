package com.christ.erp.services.dbobjects.employee.salary;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeCategoryDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeJobCategoryDBO;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
@Getter
@Setter
@Entity
@Table(name = "emp_daily_wage_slab")
public class EmpDailyWageSlabDBO implements Serializable {

	private static final long serialVersionUID = -6143035683308775660L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emp_daily_wage_slab_id")
	public Integer id;

	@ManyToOne
	@JoinColumn(name = "emp_employee_category_id")
	public EmpEmployeeCategoryDBO empCategory;

	@ManyToOne
	@JoinColumn(name = "emp_employee_job_category_id")
	public EmpEmployeeJobCategoryDBO jobCategory;

	@Column(name = "daily_wage_slab_from")
	public Integer dailyWageSlabFrom;

	@Column(name = "daily_wage_slab_to")
	public Integer dailyWageSlabTo;

	@Column(name = "daily_wage_basic")
	public Integer dailyWageSlabBasic;

	@Column(name = "created_users_id")
	public Integer createdUsersId;

	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;

	@Column(name = "record_Status")
	public char recordStatus;
}
