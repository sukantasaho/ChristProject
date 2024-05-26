package com.christ.erp.services.dbobjects.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "erp_gender")
@Getter
@Setter
public class ErpGenderDBO {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_gender_id")
    public Integer erpGenderId;
	
	@Column(name="gender_name")
    public String genderName;

	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
