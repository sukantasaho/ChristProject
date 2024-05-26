package com.christ.erp.services.dbobjects.curriculum.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "erp_programme_approval_level_documents")
@Getter
@Setter
public class ErpProgrammeApprovalLevelDocumentsDBO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_programme_approval_level_documents_id")
	private int id;

	@ManyToOne
	@JoinColumn(name = "erp_programme_approval_level_mapping_id")
	private ErpProgrammeApprovalLevelMappingDBO erpProgrammeApprovalLevelMappingDBO;

	@Column(name = "approval_document_url")
	private String approvalDocumentURL;

	@Column(name = "created_users_id", updatable = false)
	private Integer createdUsersId;

	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;

	@Column(name = "record_status")
	private char recordStatus;
}
