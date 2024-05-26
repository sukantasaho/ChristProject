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
@Table(name = "emp_appln_interview_template_group_details")
@Setter
@Getter
public class EmpApplnInterviewTemplateGroupDetailsDBO implements Serializable{
	
	private static final long serialVersionUID = -9187236920248578339L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emp_appln_interview_template_group_details_id")
	private int id;
	
	@Column(name = "parameter_name")
	private String parameterName;
	
	@Column(name = "parameter_order_no")
	private Integer parameterOrderNo;
	
	@Column(name = "parameter_max_score")
	private Integer parameterMaxScore;
	
	@Column(name = "is_auto_calculate")
	private Boolean autoCalculate;
	
	@Column(name = "created_users_id")
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;
	
	@ManyToOne
	@JoinColumn(name = "emp_appln_interview_template_group_id")
	private EmpApplnInterviewTemplateGroupDBO empApplnInterviewTemplateGroupDBO;
}
