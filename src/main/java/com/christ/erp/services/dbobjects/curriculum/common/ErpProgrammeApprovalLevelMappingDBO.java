package com.christ.erp.services.dbobjects.curriculum.common;

import java.time.LocalDate;
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

import com.christ.erp.services.dbobjects.common.ErpApprovalLevelsDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "erp_programme_approval_level_mapping")
@Getter
@Setter
public class ErpProgrammeApprovalLevelMappingDBO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_programme_approval_level_mapping_id")
	private int id;

	@ManyToOne
	@JoinColumn(name = "erp_programme_id")
	private ErpProgrammeDBO erpProgrammeDBO;
	
	@ManyToOne
	@JoinColumn(name = "erp_approval_levels_id")
	private ErpApprovalLevelsDBO erpApprovalLevelsDBO;

	@Column(name = "approval_comments")
	private String approvalComments;

	@Column(name = "approval_date")
	private LocalDate approvalDate;

	@Column(name = "created_users_id", updatable = false)
	private Integer createdUsersId;

	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;

	@Column(name = "record_status")
	private char recordStatus;
	
	@OneToMany(mappedBy = "erpProgrammeApprovalLevelMappingDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL )
	private Set<ErpProgrammeApprovalLevelDocumentsDBO> erpProgrammeApprovalLevelDocumentsDBOSet;

}
