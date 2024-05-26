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

import com.christ.erp.services.dbobjects.common.ErpUsersDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpApplnEntriesDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "emp_appln_interview_score")
@Setter
@Getter
public class EmpApplnInterviewScoreDBO implements Serializable{

	private static final long serialVersionUID = 5229839416159348099L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_appln_interview_score_id")
    public int empApplnInterviewScoreId;
	
	@ManyToOne
	@JoinColumn(name="emp_appln_entries_id")
	public EmpApplnEntriesDBO empApplnEntriesDBO;
	
	@ManyToOne
	@JoinColumn(name="emp_appln_interview_schedules_id")
	public EmpApplnInterviewSchedulesDBO empApplnInterviewSchedulesDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_users_id")
	public ErpUsersDBO erpUsersDBO;
	
	@ManyToOne
	@JoinColumn(name="emp_interview_university_externals_id")
	public EmpInterviewUniversityExternalsDBO empInterviewUniversityExternalsDBO;
	
	@Column(name="Max_Score")
    public Integer maxScore;
	
	@Column(name="total_score")
    public Integer totalScore;
	
	@Column(name="comments")
    public String comments;
	
	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
    
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "applnInterviewScoreId",cascade=CascadeType.ALL)
	public Set<EmpApplnInterviewScoreDetailsDBO> empApplnInterviewScoreDetailsMap = new HashSet<>();

}
