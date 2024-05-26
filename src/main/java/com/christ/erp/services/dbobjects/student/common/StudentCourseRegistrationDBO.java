package com.christ.erp.services.dbobjects.student.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.common.AcaCourseSessionwiseDBO;
import com.christ.erp.services.dbobjects.common.AcaStudentSessionwiseDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name =" student_course_registration")
@Getter
@Setter

public class StudentCourseRegistrationDBO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "student_course_registration_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name ="aca_student_sessionwise_id")
	private AcaStudentSessionwiseDBO acaStudentSessionwiseDBO;
	
	@ManyToOne
	@JoinColumn(name = "aca_course_sessionwise_id")
	private AcaCourseSessionwiseDBO acaCourseSessionwiseDBO;
	
	@Column(name = "created_users_id", updatable = false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;
	
}
