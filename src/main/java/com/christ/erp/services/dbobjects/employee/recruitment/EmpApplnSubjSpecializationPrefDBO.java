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

import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;

@Entity
@Table(name = "emp_appln_subj_specialization_pref")
public class EmpApplnSubjSpecializationPrefDBO implements Serializable{

	private static final long serialVersionUID = 7890659735821103985L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_appln_subj_specialization_pref_id")
    public int empApplnSubjSpecializationPrefId;
	
	@ManyToOne
	@JoinColumn(name="emp_appln_entries_id")
	public EmpApplnEntriesDBO empApplnEntriesDBO;
	
	@ManyToOne
	@JoinColumn(name="emp_appln_subject_category_id")
	public EmpApplnSubjectCategoryDBO empApplnSubjectCategoryDBO;
	
	@ManyToOne
	@JoinColumn(name="emp_appln_subject_category_specialization_id")
	public EmpApplnSubjectCategorySpecializationDBO empApplnSubjectCategorySpecializationDBO;
	
	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
