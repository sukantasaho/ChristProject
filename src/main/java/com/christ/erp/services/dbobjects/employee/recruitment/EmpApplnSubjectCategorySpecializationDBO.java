package com.christ.erp.services.dbobjects.employee.recruitment;

import java.io.Serializable;

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
@Table(name = "emp_appln_subject_category_specialization")
@Setter
@Getter
public class EmpApplnSubjectCategorySpecializationDBO implements Serializable{

	private static final long serialVersionUID = -7664506465350932891L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_appln_subject_category_specialization_id")
    public int empApplnSubjectCategorySpecializationId;
	
	@Column(name="subject_category_specialization_name")
    public String subjectCategorySpecializationName;
	
	@ManyToOne
	@JoinColumn(name="emp_appln_subject_category_id")
	public EmpApplnSubjectCategoryDBO empApplnSubjectCategoryDBO;
	
	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
