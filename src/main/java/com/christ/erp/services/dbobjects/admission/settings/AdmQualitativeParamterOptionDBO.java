package com.christ.erp.services.dbobjects.admission.settings;

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
@Table(name = "adm_qualitative_parameter_option")
@Setter
@Getter
public class AdmQualitativeParamterOptionDBO  {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "adm_qualitative_parameter_option_id")
	public Integer id;
	
	@ManyToOne
	@JoinColumn(name="adm_qualitative_parameter_id")
	public AdmQualitativeParamterDBO admQualitativeParameter;
	
	@Column(name="option_name")
	public String optionName;
	
	@Column(name = "record_status")
	public char recordStatus;
	
	@Column(name = "created_users_id",updatable = false)
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
}