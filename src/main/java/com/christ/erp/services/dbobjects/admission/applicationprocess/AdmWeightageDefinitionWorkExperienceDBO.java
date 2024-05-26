package com.christ.erp.services.dbobjects.admission.applicationprocess;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "adm_weightage_definition_work_experience")
@Getter
@Setter
public class AdmWeightageDefinitionWorkExperienceDBO {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_weightage_definition_work_experience_id")
    private int id;
	
	@Column(name = "work_experience_name")
	private String workExperienceName;
	
	@Column(name = "work_experience_year")
	private Integer workExperienceYear;

	@Column(name="created_users_id", updatable = false)
    private Integer createdUsersId;

    @Column(name="modified_users_id")
    private Integer modifiedUsersId;

    @Column(name="record_status")
    private Character recordStatus;
}
