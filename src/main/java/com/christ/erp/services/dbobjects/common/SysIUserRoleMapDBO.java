package com.christ.erp.services.dbobjects.common;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "sys_user_role_map")
public class SysIUserRoleMapDBO implements Serializable {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="sys_user_role_map_id")
    public int id;

	@ManyToOne
	@JoinColumn(name="sys_role_id")
    public SysRoleDBO roleName;
	
	@ManyToOne
	@JoinColumn(name="erp_users_id")
    public ErpUsersDBO erpUsersDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_campus_id")
    public ErpCampusDBO campusDBO;
	
	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
