package com.christ.erp.services.dbobjects.common;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name =" url_access_link")
@Setter
@Getter
public class UrlAccessLinkDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "url_access_link_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name="url_folder_list_id")
	public UrlFolderListDBO urlFolderListDBO;

	@Column(name = "file_name_unique")
	private String fileNameUnique;
	
	@Column(name = "temp_file_name_unique")
	private String tempFileNameUnique;
	
	@Column(name = "file_name_original")
	private String fileNameOriginal;
	
	@Column(name = "is_queued")
	private Boolean isQueued;
	
	@Column(name = "is_serviced")
	private Boolean isServiced;
	
	@Column(name = "created_users_id", updatable = false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;
	
	@OneToMany(mappedBy ="urlAccessLinkDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<UrlUploadedFilesDBO> urlUploadedFilesDBO;


	
}
