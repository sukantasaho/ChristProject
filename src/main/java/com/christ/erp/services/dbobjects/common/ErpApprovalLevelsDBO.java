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
@Table(name = "erp_approval_levels")
@Getter
@Setter
public class ErpApprovalLevelsDBO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_approval_levels_id")
	private int id;

	@Column(name = "approver")
	private String approver;

	@Column(name = "is_external")
	private Boolean isExternal;

	@Column(name = "is_for_programme")
	private Boolean isForProgramme;

	@Column(name = "created_users_id", updatable = false)
	private Integer createdUsersId;

	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;

	@Column(name = "record_status")
	private char recordStatus;
	
	@Column(name = "is_mandatory")
	private Boolean isMandatory;
}
