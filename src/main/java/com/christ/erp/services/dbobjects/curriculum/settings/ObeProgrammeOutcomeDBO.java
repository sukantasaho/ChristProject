package com.christ.erp.services.dbobjects.curriculum.settings;

import java.io.Serializable;
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
import com.christ.erp.services.dbobjects.common.ObeProgrammeOutcomeTypesDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "obe_programme_outcome")
@Getter
@Setter
public class ObeProgrammeOutcomeDBO implements Serializable {
	private static final long serialVersionUID = 3722455250633061387L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="obe_programme_outcome_id")
    private int id;

    @Column(name = "comments")
	private String comments;
	
	@Column(name = "created_users_id",updatable=false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;

	@ManyToOne
	@JoinColumn(name= "erp_programme_batchwise_settings_id")
	private ErpProgrammeBatchwiseSettingsDBO erpProgrammeBatchwiseSettingsDBO;
	
	@ManyToOne
	@JoinColumn(name= "obe_programme_outcome_types_id")
	private ObeProgrammeOutcomeTypesDBO obeProgrammeOutcomeTypesDBO;
	
	@ManyToOne
	@JoinColumn(name= "erp_approval_levels_id")
	private ErpApprovalLevelsDBO erpApprovalLevelsDBO;
	
	@OneToMany(mappedBy = "obeProgrammeOutcomeDBO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ObeProgrammeOutcomeDetailsDBO> obeProgrammeOutcomeDetailsDBOSet;
	
	@OneToMany(mappedBy = "obeProgrammeOutcomeDBO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ObeProgrammeOutcomeUploadDetailsDBO> obeProgrammeOutcomeUploadDetailsDBOSet;
}
