package com.christ.erp.services.dbobjects.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "gui_menu_shortcut_link")
@Getter
@Setter
public class GUIMenuShortcutLinkDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "gui_menu_shortcut_link_id")
	private int id;
	
	@Column(name = "quick_link_type")  // enum('Q','R')
	private String quickLinkType;
	
	@Column(name = "link_display_order")
	private Integer linkDisplayOrder;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name= "users_id")
	private ErpUsersDBO erpUsersDBO;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name= "sys_menu_id")
	private SysMenuDBO sysMenuDBO;
	
	@Column(name = "created_users_id", updatable=false)
	private Integer createdUsersId;

	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;

	@Column(name = "record_status")
	private char recordStatus;
	
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name= "gui_menu_shortcut_folder_id")
//	private GUIMenuShortcutFolderIdDBO guiMenuShortcutFolderDBO;
}
