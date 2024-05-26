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

import com.christ.erp.services.dbobjects.curriculum.common.ErpProgrammeApprovalLevelMappingDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.ErpProgrammeBatchwiseSettingsDBO;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "erp_programme")
@Getter
@Setter
public class ErpProgrammeDBO{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_programme_id")
	public int id;
	
	@Column(name="programme_name")
	public String programmeName;

	@ManyToOne
	@JoinColumn(name = "erp_programme_degree_id")
	public ErpProgrammeDegreeDBO erpProgrammeDegreeDBO;

	@ManyToOne
	@JoinColumn(name = "erp_programme_level_id")
	public ErpProgrammeLevelDBO erpProgrammeLevelDBO;
	
	@ManyToOne
	@JoinColumn(name = "coordinating_department_id")
	private ErpDepartmentDBO coordinatingDepartment;

	@Column(name = "record_status")
	public char recordStatus;
	
	@Column(name = "created_users_id",updatable=false)
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@OneToMany(mappedBy = "erpProgrammeDBO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ErpProgrammeDepartmentMappingDBO> erpProgrammeDepartmentMappingDBOSet;

	@OneToMany(mappedBy = "erpProgrammeDBO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ErpCampusProgrammeMappingDBO> erpCampusProgrammeMappingDBOSet;

	@OneToMany(mappedBy = "erpProgrammeDBO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ErpProgrammeAddtnlDetailsDBO> erpProgrammeAddtnlDetailsDBOSet;
	
	@ManyToOne
	@JoinColumn(name = "erp_deanery_id")
	private ErpDeaneryDBO erpDeaneryDBO;
	
	@Column(name="programme_code")
	private String programmeCode;
	
	@OneToMany(mappedBy = "erpProgrammeDBO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ErpProgrammeRBTDomainsMappingDBO> erpProgrammeRBTDomainsMappingDBOSet;

	@OneToMany(mappedBy = "erpProgrammeDBO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ErpProgrammeApprovalLevelMappingDBO> erpProgrammeApprovalLevelMappingDBOSet;
	
	@OneToMany(mappedBy = "erpProgrammeDBO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ErpProgrammeBatchwiseSettingsDBO> erpProgrammeBatchwiseSettingsDBOSet;

	@Column(name = "programme_name_for_application")
	public String programmeNameForApplication;
}
