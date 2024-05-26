package com.christ.erp.services.dbobjects.common;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="erp_department_category")
public class ErpDepartmentCategoryDBO implements Serializable {

	private static final long serialVersionUID = 8012776287006892637L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_department_category_id")
	public int id;
	
	@Column(name="department_category_name")
	public String departmentCategoryName;

	@Column(name="is_category_academic")
	public Boolean isCategoryAcademic;
	
	@Column(name="created_users_id",updatable=false)
	public Integer createdUsersId;

	@Column(name="modified_users_id")
	public Integer modifiedUsersId;
		
	@Column(name="record_status")
	public Character recordStatus;
}
