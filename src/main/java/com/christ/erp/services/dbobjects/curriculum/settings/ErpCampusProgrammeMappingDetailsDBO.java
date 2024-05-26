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

import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "erp_campus_programme_mapping_details")
@Getter
@Setter
public class ErpCampusProgrammeMappingDetailsDBO implements Serializable {
	private static final long serialVersionUID = 3722455250633061387L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_campus_programme_mapping_details_id")
	private int id; 
	
	@Column(name = "approved_intake")
	private Integer approvedIntake;
	
	@Column(name = "revised_intake")
	private Integer revisedIntake;
	
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
	@JoinColumn(name= "erp_campus_programme_mapping_id")
	private ErpCampusProgrammeMappingDBO erpCampusProgrammeMappingDBO;
}
