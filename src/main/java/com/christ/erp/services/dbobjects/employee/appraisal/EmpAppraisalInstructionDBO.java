package com.christ.erp.services.dbobjects.employee.appraisal;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "emp_appraisal_instruction")
public class EmpAppraisalInstructionDBO implements Serializable {	
	
	private static final long serialVersionUID = 5362767098691410989L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_appraisal_instruction_id")
	public Integer id;	
	
	@Column(name="instruction_name")
	public String instructionName;
	
	@Column(name="appraisal_type")
	public String appraisalType;
	
	@Column(name ="instruction_content", columnDefinition = "mediumtext")
	public String instructionContent;
    
	@Column(name="created_users_id",updatable=false)
	public Integer createdUsersId;
	
	@Column(name="modified_users_id")
	public Integer modifiedUsersId;	
	
	@Column(name="record_status")
	public char recordStatus; 
}
