package com.christ.erp.services.dbobjects.employee.recruitment;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@Entity
@Table(name = "emp_appln_subject_category")
public class EmpApplnSubjectCategoryDBO implements Serializable {

	private static final long serialVersionUID = 7927288224915025240L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_appln_subject_category_id")
	public Integer id;
	
	@Column(name="subject_category_name")
	public String subjectCategory;
	
	@Column(name="is_academic")
	public Boolean isAcademic;
	
	@Column(name="created_users_id")
	public Integer createdUsersId;	
		
	@Column(name="modified_users_id")
	public Integer modifiedUsersId;
		
	@Column(name="record_status")
	public char recordStatus;
}
