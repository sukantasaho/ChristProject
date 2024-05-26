package com.christ.erp.services.dbobjects.admission.settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "adm_prerequisite_exam")
@Getter
@Setter
public class AdmPrerequisiteExamDBO  {	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="adm_prerequisite_exam_id")
	public int id;
	
	@Column(name="exam_name")
	public String examName;
	
	@Column(name="exam_code")
	public String examCode;
	
	@Column(name = "record_status")
	public char recordStatus;
	
	@Column(name = "created_users_id", updatable = false)
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
}
