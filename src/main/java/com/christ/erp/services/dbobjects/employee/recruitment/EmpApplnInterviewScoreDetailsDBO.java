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

@SuppressWarnings("serial")
@Entity
@Table(name = "emp_appln_interview_score_details")
@Setter
@Getter
public class EmpApplnInterviewScoreDetailsDBO implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_appln_interview_score_details_id")
	public int interviewScoreDetailsId;
	
	@ManyToOne
	@JoinColumn(name="emp_appln_interview_score_id")
	public EmpApplnInterviewScoreDBO applnInterviewScoreId;
	
	@ManyToOne
	@JoinColumn(name = "emp_appln_interview_template_group_details_id")
	public EmpApplnInterviewTemplateGroupDetailsDBO applnInterviewTemplateGroupDetailId;
	
	@Column(name="score_entered")
	public Integer scoreEntered;
	
	@Column(name = "created_users_id", updatable = false)
	public Integer createdUserId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUserId;
	
	@Column(name="record_status")
	public char recordStatus;
	
}
