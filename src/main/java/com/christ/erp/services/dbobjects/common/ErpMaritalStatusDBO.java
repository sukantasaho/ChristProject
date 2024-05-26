package com.christ.erp.services.dbobjects.common;

import java.io.Serializable;

import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Setter
@Getter
@Entity
@Table(name = "erp_marital_status")
public class ErpMaritalStatusDBO implements Serializable{

	private static final long serialVersionUID = -1039235036754689988L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_marital_status_id")
    public int id;

	@Column(name="marital_status_name")
    public String maritalStatusName;

	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
