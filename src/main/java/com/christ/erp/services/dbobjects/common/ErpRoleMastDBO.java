package com.christ.erp.services.dbobjects.common;

import java.io.Serializable;

import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Table(name = "erp_role_mast")
public class ErpRoleMastDBO implements Serializable{

	private static final long serialVersionUID = -1666783707452960496L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_role_mast_id")
    public int id;

	@Column(name="role_name")
    public String roleName;

	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
