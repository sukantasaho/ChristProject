package com.christ.erp.services.dbobjects.curriculum.settings;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.ErpQualificationLevelDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = " externals_additional_details")
@Getter
@Setter
public class ExternalsAdditionalDetailsDBO implements Serializable {
	private static final long serialVersionUID = 3722455250633061387L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "externals_additional_details_id")
	private int id;

	@Column(name = "address")
	private String address;

	@Column(name = "experience_details")
	private String experienceDetails;

	@Column(name = "organization_details")
	private String organizationDetails;

	@Column(name = "qualification_description")
	private String qualificationDescription;

	@Column(name = "created_users_id", updatable=false)
	private Integer createdUsersId;

	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;

	@Column(name = "record_status")
	private char recordStatus;

	@ManyToOne
	@JoinColumn(name = "erp_qualification_level_id")
	private ErpQualificationLevelDBO erpQualificationLevelDBO;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="externals_id")
	public ExternalsDBO externalsDBO;
}