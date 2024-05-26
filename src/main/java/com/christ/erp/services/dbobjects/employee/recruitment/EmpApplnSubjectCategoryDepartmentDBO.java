package com.christ.erp.services.dbobjects.employee.recruitment;

import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "emp_appln_subject_category_department")
public class EmpApplnSubjectCategoryDepartmentDBO implements Serializable  {
	
	private static final long serialVersionUID = -190136184295866767L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_appln_subject_category_department_id")
	public Integer id;
	
	@ManyToOne
	@JoinColumn(name="emp_appln_subject_category_id")
	public EmpApplnSubjectCategoryDBO subject;
	
	@ManyToOne
	@JoinColumn(name="erp_department_id")
	public ErpDepartmentDBO department ;
	 	
	@Column(name="created_users_id")
	public Integer createdUsersId;

	@Column(name="modified_users_id")
	public Integer modifiedUsersId;
		
	@Column(name="record_status")
	public char recordStatus;
}
