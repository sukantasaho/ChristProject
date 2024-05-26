package com.christ.erp.services.dbobjects.common;

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

import java.io.Serializable;

@Setter
@Getter
@Entity
@Table(name = "erp_department")
public class ErpDepartmentDBO implements Serializable {
	
	private static final long serialVersionUID = 287183123492344059L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_department_id")
	public Integer id;
	
	@Column(name="department_name")
	public String departmentName;
	
	@Column(name="created_users_id")
	public Integer createdUsersId;
		
	@Column(name="modified_users_id")
	public Integer modifiedUsersId;
		
	@Column(name="record_status")
	public char recordStatus;
	
    @ManyToOne
	@JoinColumn(name="erp_department_category_id") 
	public ErpDepartmentCategoryDBO erpDepartmentCategoryDBO;

	@ManyToOne
	@JoinColumn(name="erp_deanery_id")
	public ErpDeaneryDBO erpDeaneryDBO;
}
