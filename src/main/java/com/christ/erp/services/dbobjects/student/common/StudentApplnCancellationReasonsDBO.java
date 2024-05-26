package com.christ.erp.services.dbobjects.student.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="student_appln_cancellation_reasons")
public class StudentApplnCancellationReasonsDBO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="student_appln_cancellation_reasons_id")
	public int id;	
	
	@Column(name="reason_name")
	public String  reasonName;
	
	@Column(name = "created_users_id", updatable = false)
	public Integer  createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer  modifiedUsersId;
	
	@Column(name = "record_status")
	public Character recordStatus;
	
}
