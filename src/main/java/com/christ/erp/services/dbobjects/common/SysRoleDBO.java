package com.christ.erp.services.dbobjects.common;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Table(name = "sys_role")
@Setter
@Getter
public class SysRoleDBO implements Serializable{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="sys_role_id")
    public int id;

	@Column(name="role_name")
    public String roleName;
	
	@ManyToOne
	@JoinColumn(name="sys_role_group_id")
    public SysRoleGroupDBO sysRoleGroup;
	
	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
    
    @OneToMany(fetch = FetchType.LAZY,  mappedBy = "sysRoleDBO", cascade = CascadeType.ALL)
   	public Set<SysRoleFunctionMapDBO> sysRoleFunctionMapDBOs = new HashSet<>();
    
    @OneToMany(fetch = FetchType.LAZY,  mappedBy = "sysRoleDBO", cascade = CascadeType.ALL)
   	public Set<SysUserRoleMapDBO> SysUserRoleMapDBOSet = new HashSet<>();
}
