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
@Table(name =" url_aws_config")
@Setter
@Getter
public class UrlAwsConfigDBO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "url_aws_config_id")
	private int id;
	
	@Column(name ="bucket_name")
	private String bucketName;
	
	@Column(name = "temp_bucket_name")
	private String tempBucketName;
	
	@Column(name = "region")
	private String region;
	
	@Column(name = "endpoint")
	private String endpoint;
	
	@Column(name = "created_users_id", updatable = false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;
	
}
