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
@Table(name = "erp_religion")
@Getter
@Setter
public class ErpReligionDBO{


	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_religion_id")
    public int id;

	@Column(name="religion_name")
    public String religionName;
	
	@Column(name = "is_minority")
	public Boolean isMinority;

	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
