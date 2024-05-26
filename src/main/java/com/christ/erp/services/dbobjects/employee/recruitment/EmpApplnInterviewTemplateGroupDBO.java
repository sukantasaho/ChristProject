package com.christ.erp.services.dbobjects.employee.recruitment;
import java.io.Serializable;
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

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "emp_appln_interview_template_group")
@Getter
@Setter
public class EmpApplnInterviewTemplateGroupDBO implements Serializable{
	
	private static final long serialVersionUID = 4737349832675315151L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emp_appln_interview_template_group_id")
	private int id;
	
	@Column(name = "template_group_heading")
	private String templateGroupHeading;
	
	@Column(name = "heading_order_no")
	private Integer headingOrderNo;
	
	@Column(name = "created_users_id")
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "emp_appln_interview_template_id")
	private EmpApplnInterviewTemplateDBO empApplnInterviewTemplateDBO;
	
	@OneToMany(fetch = FetchType.LAZY,mappedBy = "empApplnInterviewTemplateGroupDBO",cascade = CascadeType.ALL)
	private Set<EmpApplnInterviewTemplateGroupDetailsDBO> empApplnInterviewTemplateGroupDetails = new HashSet<>();

}
