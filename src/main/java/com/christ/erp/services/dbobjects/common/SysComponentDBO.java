package com.christ.erp.services.dbobjects.common;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name =" sys_component")
@Setter
@Getter

public class SysComponentDBO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sys_component_id")
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="menu_screen_id")
	public SysMenuDBO sysMenuDBO;
	
	@Column(name ="component_name")
	private String componentName;

	@Column(name = "created_users_id", updatable = false)
	private Integer createdUsersId;

	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;

	@Column(name = "record_status")
	private char recordStatus;

}
