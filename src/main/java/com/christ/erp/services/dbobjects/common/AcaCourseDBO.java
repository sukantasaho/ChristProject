package com.christ.erp.services.dbobjects.common;

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
@Table(name =" aca_course")
@Setter
@Getter

public class AcaCourseDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "aca_course_id")
	private int id;
	
	@Column(name ="course_name")
	private String courseName;
	
	@Column(name = "course_code")
	private String courseCode;
	
	@ManyToOne
	@JoinColumn(name ="aca_course_type_id")
	private AcaCourseTypeDBO acaCourseTypeDBO;
	
	@ManyToOne
	@JoinColumn(name ="offering_department_id")
	private ErpDepartmentDBO erpDepartmentDBO;
	
	@Column(name =" initiated_year")
	private Integer initiatedYear;
	
	@Column(name = "created_users_id", updatable = false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;
	
}
