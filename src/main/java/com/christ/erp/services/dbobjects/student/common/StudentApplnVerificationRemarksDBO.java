package com.christ.erp.services.dbobjects.student.common;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "student_appln_verification_remarks")
@Setter
@Getter

public class StudentApplnVerificationRemarksDBO {
	 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "student_appln_verification_remarks_id")
	private Integer id;
	
	@Column(name = "verification_remarks_name")
	private String verificationRemarksName;
	
	@Column(name = "created_users_id", updatable = false)
	private Integer  createdUsersId;

	@Column(name = "modified_users_id")
	private Integer  modifiedUsersId;

	@Column(name = "record_status")
	private Character recordStatus;
	
	
}
