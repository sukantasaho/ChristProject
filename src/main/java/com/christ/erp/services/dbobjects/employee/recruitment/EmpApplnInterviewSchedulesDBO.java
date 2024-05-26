package com.christ.erp.services.dbobjects.employee.recruitment;

import java.io.Serializable;
import java.time.LocalDateTime;
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
@Table(name = "emp_appln_interview_schedules")
@Getter
@Setter
public class EmpApplnInterviewSchedulesDBO implements Serializable{
	
	private static final long serialVersionUID = -5871296349284304373L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_appln_interview_schedules_id")
    public int empApplnInterviewSchedulesId;
	
	@ManyToOne
	@JoinColumn(name="emp_appln_entries_id")
	public EmpApplnEntriesDBO empApplnEntriesDBO;
	
	@Column(name="interview_round")
    public Integer interviewRound;
	
	@Column(name="interview_date_time")
	public LocalDateTime interviewDateTime;
	
	@ManyToOne
	@JoinColumn(name="point_of_contact_users_id")
	public ErpUsersDBO erpUsersDBO;
	
	@Column(name="interview_venue")
    public String interviewVenue;
	
	@Column(name = "is_applicant_available")
	public Boolean isApplicantAvailable;

	@ManyToOne
	@JoinColumn(name="emp_appln_non_availability_id")
    public EmpApplnNonAvailabilityDBO empApplnNonAvailabilityDBO;
	
	@Column(name="reason_for_non_availability_others")
    public String reasonForNonAvailabilityOthers;
	
	@Column(name = "is_approved")
	public Boolean isApproved;
	
	@Column(name="approved_by")
    public Integer approvedBy;
	
	@OneToMany(fetch = FetchType.LAZY,mappedBy = "empApplnInterviewSchedulesDBO",cascade=CascadeType.ALL)
    public Set<EmpApplnInterviewPanelDBO> empApplnInterviewPanelDBO;
	
	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "empApplnInterviewSchedulesDBO",cascade=CascadeType.ALL)
	private Set<EmpApplnInterviewScoreDBO> empApplnInterviewScoreDBO;
    
    @JoinColumn(name = "comments")
    private String comments;
}
