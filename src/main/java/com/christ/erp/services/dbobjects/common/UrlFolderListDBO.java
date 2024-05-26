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
@Table(name =" url_folder_list")
@Setter
@Getter

public class UrlFolderListDBO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "url_folder_list_id")
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="sys_menu_id")
	public SysMenuDBO sysMenuDBO;

	@ManyToOne
	@JoinColumn(name="url_aws_config_id")
	public UrlAwsConfigDBO urlAwsConfigDBO;

	@Column(name ="upload_process_code")
	private String uploadProcessCode;
	
	@Column(name = "upload_process_description")
	private String uploadProcessDescription;
	
	@Column(name = "folder_path")
	private String folderPath;
	
	@Column(name = "temp_folder_path")
	private String tempFolderPath;
	
	@Column(name = "file_size_kb")
	private Integer fileSizeKb;
	
	@Column(name = "file_type_extensions")
	private String fileType;
	
	@Column(name = "expiry_upload")
	private Integer expiryUpload;

	@Column(name = "expiry_download")
	private Integer expiryDownload;
	
	@Column(name = "created_users_id", updatable = false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;
	
}
