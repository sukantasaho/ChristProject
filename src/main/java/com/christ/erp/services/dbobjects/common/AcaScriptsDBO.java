package com.christ.erp.services.dbobjects.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.curriculum.Classes.AcaClassGroupDetailsDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaClassDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = " aca_scripts")
@Getter
@Setter

public class AcaScriptsDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "aca_scripts_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name =" aca_student_sessionwise_id")
	private AcaStudentSessionwiseDBO acaStudentSessionwiseDBO;
	
	@ManyToOne
	@JoinColumn(name = "aca_course_sessionwise_id")
	private AcaCourseSessionwiseDBO acaCourseSessionwiseDBO;
	
	@ManyToOne
	@JoinColumn(name ="aca_class_id")
	private AcaClassDBO acaClassDBO;
	
	@ManyToOne
	@JoinColumn(name = "aca_group_details_id")
	private AcaClassGroupDetailsDBO acaGroupDetailsDBO;
	
	@Column(name = "created_users_id",updatable = false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;

}
