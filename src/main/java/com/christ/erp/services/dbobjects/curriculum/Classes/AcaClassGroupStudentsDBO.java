package com.christ.erp.services.dbobjects.curriculum.Classes;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name =" aca_class_group_students")
@Getter
@Setter

public class AcaClassGroupStudentsDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "aca_class_group_students_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name ="student_id")
	private StudentDBO studentDBO;
	
	@ManyToOne
	@JoinColumn(name ="aca_class_group_details_id")
	private AcaClassGroupDetailsDBO acaClassGroupDetailsDBO;
	
	@Column(name = "created_users_id",updatable = false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;
	
}
