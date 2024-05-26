package com.christ.erp.services.dbobjects.admission.settings;


import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "adm_qualitative_parameter")
@Setter
@Getter
public class AdmQualitativeParamterDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="adm_qualitative_parameter_id")
	public Integer id;
	
	@Column(name="qualitative_parameter_label")
	public String qualitativeParameterLabel;
	
	@Column(name="field_type")
	public String fieldType;
	
	@Column(name = "record_status")
	public char recordStatus;
	
	@Column(name = "created_users_id")
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@OneToMany(mappedBy ="admQualitativeParameter",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<AdmQualitativeParamterOptionDBO> admQualitativeParameterOptionSet;
	
	
}