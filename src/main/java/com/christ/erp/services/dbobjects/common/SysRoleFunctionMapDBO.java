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

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Table(name = "sys_role_function_map")
@Setter
@Getter
public class SysRoleFunctionMapDBO implements Serializable {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="sys_role_function_map_id")
    public int id;
	
	@ManyToOne
	@JoinColumn(name="sys_role_id")
    public SysRoleDBO sysRoleDBO;
	
	@ManyToOne
	@JoinColumn(name="sys_function_id")
    public SysFunctionDBO sysFunctionDBO;
	
	@Column(name="is_authorised")
    public Boolean authorised;
	
	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
	
}
