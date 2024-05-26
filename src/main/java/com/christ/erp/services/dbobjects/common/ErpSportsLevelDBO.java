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
@Table(name="erp_sports_level")
@Getter
@Setter
public class ErpSportsLevelDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_sports_level_id")
	private int id;

	@Column(name="sports_level_name")
	private String sportsLevelName;

	@Column(name="created_users_id",updatable=false)
	private Integer createdUsersId;

	@Column(name="modified_users_id")
	private Integer modifiedUsersId;

	@Column(name="record_status")
	private char recordStatus;

}
