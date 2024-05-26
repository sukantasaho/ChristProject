package com.christ.erp.services.dbobjects.common;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "erp_user_group")
public class ErpUserGroupDBO implements Serializable{

	private static final long serialVersionUID = 8391119092610555519L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_user_group_id")
	public Integer id;
	
	@Column(name="user_group_name")
	public String userGroupName;
	
	@Column(name="user_group_code")
	public String useGroupCode;
	
	@Column(name="created_users_id", updatable=false)
	public Integer createdUsersId;
		
	@Column(name="modified_users_id")
	public Integer modifiedUsersId;
		
	@Column(name="record_status")
	public char recordStatus;
}
