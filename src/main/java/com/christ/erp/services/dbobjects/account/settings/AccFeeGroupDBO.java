package com.christ.erp.services.dbobjects.account.settings;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="acc_fee_group")
public class AccFeeGroupDBO implements Serializable{

	private static final long serialVersionUID = -3798535255608755250L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="acc_fee_group_id")
	public int id;
	
	@Column(name="group_name")
	public String groupName;
	
	@Column(name = "created_users_id")
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name="record_status")
    public char recordStatus;

}
