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
@Table(name = "erp_screen_config_mast")
@Setter
@Getter
public class ErpScreenConfigMastDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_screen_config_mast_id")
	private int id;
	
	@Column(name = "mapped_table_name")
	private String mappedTableName;
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "created_users_id", updatable = false)
	private Integer  createdUsersId;

	@Column(name = "modified_users_id")
	private Integer  modifiedUsersId;

	@Column(name = "record_status")
	private Character recordStatus;
	
}
