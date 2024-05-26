package com.christ.erp.services.dbobjects.curriculum.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "erp_accreditation_affiliation_type")
@Getter
@Setter
public class ErpAccreditationAffiliationTypeDBO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_accreditation_affiliation_type_id")
	private int id;

	@Column(name = "accreditation_affiliation_type")
	private String accreditationAffiliationType;
	
	@Column(name = "accreditation_code")
	private String accreditationCode;
	
	@Column(name = "created_users_id", updatable = false)
	private Integer createdUsersId;

	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;

	@Column(name = "record_status")
	private char recordStatus;	
}
