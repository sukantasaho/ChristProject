package com.christ.erp.services.dbobjects.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name =" url_uploaded_files")
@Setter
@Getter

public class UrlUploadedFilesDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "url_uploaded_files_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name="url_access_link_id")
	public UrlAccessLinkDBO urlAccessLinkDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_users_id")
	public ErpUsersDBO erpUsersDBO;
	
	@Column(name = "uploaded_time")
	private String uploadedTime;
	
	@Column(name = "created_users_id", updatable = false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;
	

}
