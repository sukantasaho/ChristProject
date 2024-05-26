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

@Entity
@Table(name = "sys_user_function_override")
@Getter
@Setter
public class SysUserFunctionOverrideDBO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sys_user_function_override_id")
	public int id;

	@ManyToOne
	@JoinColumn(name="erp_users_id")
	public ErpUsersDBO erpUsersDBO;

	@ManyToOne
	@JoinColumn(name="sys_function_id")
	public SysFunctionDBO sysFunctionDBO;

	@ManyToOne
	@JoinColumn(name="erp_campus_id")
	public ErpCampusDBO erpCampusDBO;

	@Column(name = "is_allowed")
	public boolean isAllowed;

	@Column(name = "created_users_id",updatable=false)
	public Integer createdUsersId;

	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;

	@Column(name = "record_status")
	public char recordStatus;

}


