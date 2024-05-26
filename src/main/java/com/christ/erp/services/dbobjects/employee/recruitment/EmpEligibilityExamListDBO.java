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
@Table(name = "emp_eligibility_exam_list")
public class EmpEligibilityExamListDBO implements Serializable{
	
	private static final long serialVersionUID = -5414596806199933613L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_eligibility_exam_list_id")
    public int empEligibilityExamListId;
	
	@Column(name="eligibility_exam_name")
    public String eligibilityExamName;
	
	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
