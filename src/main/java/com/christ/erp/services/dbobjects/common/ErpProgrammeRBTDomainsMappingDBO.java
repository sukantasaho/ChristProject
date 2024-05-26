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
@Table(name = "erp_programme_rbt_domains_mapping")
@Getter
@Setter
public class ErpProgrammeRBTDomainsMappingDBO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_programme_rbt_domains_mapping_id")
	private int id;

	@ManyToOne
	@JoinColumn(name = "erp_programme_id")
	private ErpProgrammeDBO erpProgrammeDBO;

	@ManyToOne
	@JoinColumn(name = "erp_rbt_domains_id")
	private ErpRBTDomainsDBO erpRBTDomainsDBO;

	@Column(name = "created_users_id", updatable = false)
	private Integer createdUsersId;

	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;

	@Column(name = "record_status")
	private char recordStatus;

}
