package com.christ.erp.services.dbobjects.common;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name =" sys_component_group")
@Setter
@Getter

public class SysComponentGroup {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sys_component_group_id")
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="menu_screen_id")
	public SysMenuDBO sysMenuDBO;
	
	@Column(name ="component_group_name")
	private String componentGroupName;
	
	@Column(name = "created_users_id", updatable = false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;
	
}
