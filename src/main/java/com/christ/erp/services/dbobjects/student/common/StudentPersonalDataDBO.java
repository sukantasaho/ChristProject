package com.christ.erp.services.dbobjects.student.common;

import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.ErpGenderDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "student_personal_data")
@Setter
@Getter
public class StudentPersonalDataDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="student_personal_data_id")
	public int id;	
	
	@Column(name="student_name")
	public String studentName;
	
	@Column(name="student_dob")
	public LocalDate  studentDob;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="erp_gender_id")
	public ErpGenderDBO erpGenderDBO;
	
	@Column(name="student_university_email_id")
	public String studentUniversityEmailId;
	
	@Column(name="student_personal_email_id")
	public String studentPersonalEmailId;
	
	@Column(name="student_mobile_no_country_code")
	public String studentMobileNoCountryCode;
	
	@Column(name="student_mobile_no")
	public String studentMobileNo;
	
	@ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JoinColumn(name="student_personal_data_addtnl_id")
	public StudentPersonalDataAddtnlDBO studentPersonalDataAddtnlDBO;
	
	@ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JoinColumn(name="student_personal_data_address_id")
	public StudentPersonalDataAddressDBO studentPersonalDataAddressDBO;
	
	@Column(name = "created_users_id", updatable = false)
	public Integer  createdUsersId;

	@Column(name = "modified_users_id")
	public Integer  modifiedUsersId;

	@Column(name = "record_status")
	public Character recordStatus;
}
