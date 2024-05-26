package com.christ.erp.services.dbobjects.employee.recruitment;

import com.christ.erp.services.dbobjects.account.fee.AdjustmentType;
import com.christ.erp.services.dbobjects.common.SysComponentDBO;
import com.christ.erp.services.dbobjects.common.SysComponentGroup;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeJobCategoryDBO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name =" emp_profile_component_mapping")
@Setter
@Getter

public class EmpProfileComponentMappingDBO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emp_profile_component_mapping_id")
	private int id;

	@ManyToOne
	@JoinColumn(name="sys_component_group_id")
	public SysComponentGroup sysComponentGroup;

	@ManyToOne
	@JoinColumn(name="sys_component_id")
	public SysComponentDBO sysComponentDBO;

	@ManyToOne
	@JoinColumn(name = "emp_employee_job_category_id")
	public EmpEmployeeJobCategoryDBO empEmployeeJobCategoryDBO;

	@Enumerated(EnumType.STRING)
	@Column(name = "display_status",columnDefinition="ENUM('NA','O','M')")
	private DisplayStatus displayStatus;

	@Column(name = "record_status")
	private char recordStatus;

	@Column(name = "created_users_id", updatable = false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	

}
