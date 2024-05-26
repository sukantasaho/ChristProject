package com.christ.erp.services.dbobjects.curriculum.settings;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.curriculum.common.ErpAccreditationAffiliationTypeDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "erp_programme_accreditation_mapping")
@Getter
@Setter
public class ErpProgrammeAccreditationMappingDBO implements Serializable {
	private static final long serialVersionUID = 3722455250633061387L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_programme_accreditation_mapping_id")
	private int id; 
	
	@ManyToOne
	@JoinColumn(name= "erp_accreditation_affiliation_type_id")
	private ErpAccreditationAffiliationTypeDBO erpAccreditationAffiliationTypeDBO;
	
	@Column(name = "created_users_id",updatable=false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;
	
	@ManyToOne
	@JoinColumn(name= "erp_programme_batchwise_settings_id")
	private ErpProgrammeBatchwiseSettingsDBO erpProgrammeBatchwiseSettingsDBO;	
}
