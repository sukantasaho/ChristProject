package com.christ.erp.services.dbobjects.employee.recruitment;

import java.io.Serializable;
import java.time.LocalDate;
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
import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="emp_work_experience")
@Setter
@Getter
public class EmpWorkExperienceDBO implements Serializable{

	private static final long serialVersionUID = -7047490703432339933L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_work_experience_id")
    public int empWorkExperienceId;
	
	@ManyToOne
	@JoinColumn(name="emp_appln_entries_id")
	public EmpApplnEntriesDBO empApplnEntriesDBO;
	
	@ManyToOne
	@JoinColumn(name="emp_id")
	public EmpDBO empDBO;
	
	@ManyToOne
	@JoinColumn(name = "emp_appln_work_experience_type_id")
	public EmpApplnWorkExperienceTypeDBO empApplnWorkExperienceTypeDBO;
	
	@ManyToOne
	@JoinColumn(name="emp_appln_subject_category_id")
	public EmpApplnSubjectCategoryDBO empApplnSubjectCategoryDBO;
	
	@Column(name="work_experience_from_date")
	public LocalDate workExperienceFromDate;
	
	@Column(name="work_experience_to_date")
	public LocalDate workExperienceToDate;
	
	@Column(name = "work_experience_years")
	public Integer workExperienceYears;
	
	@Column(name = "work_experience_month")
	public Integer workExperienceMonth;
	
	@Column(name = "emp_designation")
	public String empDesignation;
	
	@Column(name = "institution")
	public String institution;
	
	@Column(name = "is_part_time")
	public Boolean isPartTime;
	
	@Column(name = "is_current_experience")
	public Boolean isCurrentExperience;
	
	@Column(name = "is_recognized")
	public Boolean isRecognized;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "empWorkExperienceDBO", cascade = CascadeType.ALL)
	public Set<EmpWorkExperienceDocumentDBO> workExperienceDocumentsDBOSet = new HashSet<>();
	
//	@ManyToOne
//	@JoinColumn(name = "emp_appln_subject_category_specialization_id")
//	public EmpApplnSubjectCategorySpecializationDBO empApplnSubjectCategorySpecializationDBO;
	
	@Column(name="created_users_id", updatable=false)
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name = "record_status")
	public char recordStatus;
}
