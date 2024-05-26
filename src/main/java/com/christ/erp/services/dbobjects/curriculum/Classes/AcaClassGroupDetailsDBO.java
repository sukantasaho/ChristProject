package com.christ.erp.services.dbobjects.curriculum.Classes;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.AcaCourseSessionwiseDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaClassDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name ="aca_class_group_details")
@Getter
@Setter

public class AcaClassGroupDetailsDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "aca_class_group_details_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name ="aca_class_group_id")
	private AcaClassGroupDBO acaClassGroupDBO;
	
	@ManyToOne
	@JoinColumn(name= "aca_class_id")
	private AcaClassDBO acaClassDBO;
	
	@ManyToOne
	@JoinColumn(name ="aca_course_sessionwise_id")
	private AcaCourseSessionwiseDBO acaCourseSessionwiseDBO;

	@Column(name = "created_users_id",updatable = false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;
	
	@OneToMany(mappedBy = "acaClassGroupDetailsDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<AcaClassGroupStudentsDBO> acaClassGroupStudentDBOSet = new HashSet<>();

}
