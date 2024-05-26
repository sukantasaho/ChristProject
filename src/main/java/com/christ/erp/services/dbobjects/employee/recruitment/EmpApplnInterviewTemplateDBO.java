package com.christ.erp.services.dbobjects.employee.recruitment;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Table;

import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeCategoryDBO;

import lombok.Getter;
import lombok.Setter;

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

@Entity
@Table(name = "emp_appln_interview_template")
@Getter
@Setter
public class EmpApplnInterviewTemplateDBO implements Serializable{

	private static final long serialVersionUID = 1623857191898130903L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_appln_interview_template_id")
    private int id;

	@Column(name="interview_name")
    private String interviewName;
	
	@ManyToOne
    @JoinColumn(name="emp_employee_category_id")
    private EmpEmployeeCategoryDBO empEmployeeCategoryDBO;
	
	@Column(name = "is_panelist_comment_required")
	private Boolean isPanelistCommentRequired;

	@Column(name="created_users_id", updatable=false)
    private Integer createdUsersId;

    @Column(name="modified_users_id")
    private Integer modifiedUsersId;

    @Column(name="record_status")
    private char recordStatus;
    
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "empApplnInterviewTemplateDBO",cascade = CascadeType.ALL)
	private Set<EmpApplnInterviewTemplateGroupDBO> empApplnInterviewTemplateGroup = new HashSet<>();
}
