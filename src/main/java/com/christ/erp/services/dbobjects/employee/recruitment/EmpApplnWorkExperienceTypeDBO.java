package com.christ.erp.services.dbobjects.employee.recruitment;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="emp_appln_work_experience_type")
public class EmpApplnWorkExperienceTypeDBO implements Serializable{

	private static final long serialVersionUID = -9205906128599528613L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_appln_work_experience_type_id")
    public int empApplnWorkExperienceTypeId;
	
	@Column(name = "work_experience_type_name")
	public String workExperienceTypeName;
	
	@Column(name = "is_experience_type_academic")
	public Boolean isExperienceTypeAcademic;
	
	@Column(name="created_users_id", updatable=false)
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name = "record_status")
	public char recordStatus;
}
