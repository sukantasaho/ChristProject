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

@Entity
@Table(name = "sys_user_role_map")
@Setter
@Getter
public class SysUserRoleMapDBO implements Serializable, Comparable<SysUserRoleMapDBO>{

    private static final long serialVersionUID = -8932837362773067546L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="sys_user_role_map_id")
    public Integer id;

    @ManyToOne
    @JoinColumn(name="sys_role_id")
    public SysRoleDBO sysRoleDBO;

    @ManyToOne
    @JoinColumn(name="erp_users_id")
    public ErpUsersDBO erpUsersDBO;

    @ManyToOne
    @JoinColumn(name="erp_campus_id")
    public ErpCampusDBO erpCampusDBO;

    @Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;

	@Override
	public int compareTo(SysUserRoleMapDBO obj) {
		return this.sysRoleDBO.id - obj.sysRoleDBO.id;
	}
	

}
