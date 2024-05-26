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
@Table(name="gui_user_view_preference")
@Getter
@Setter
public class GUIUserViewPreferenceDBO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "gui_user_view_preference_id")
	private int id;
	
	@Column(name = "view_mode") // enum('DT','ST','BT')
	private String viewMode;
	
	@Column(name = "menu_theme") // enum('DT','ST','BT')
	private String menuTheme;
	
	@Column(name = "header_theme") //enum('DT','ST','BT')
	private String headerTheme;
	
	@Column(name = "font_size")
	private Integer fontSize;
	
	@Column(name = "created_users_id", updatable=false)
	private Integer createdUsersId;

	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;

	@Column(name = "record_status")
	private char recordStatus;
	
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name= "users_id")
//	private ErpUsersDBO erpUsersDBO;
//	
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name= "gui_background_image_id")
//	private GUIBackgroundImageDBO guiBackgroundImageDBO;
}
