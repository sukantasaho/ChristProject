package com.christ.erp.services.dbobjects.employee.attendance;

import java.time.LocalDate;
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
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.employee.settings.EmpApproversDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = " emp_work_diary_entries")
@Setter
@Getter

public class EmpWorkDiaryEntriesDBO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name ="emp_work_diary_entries_id")
	private int id;
 
	@ManyToOne
    @JoinColumn(name = "emp_id")
	private EmpDBO empDBO ;
	
	@ManyToOne
	@JoinColumn(name ="erp_academic_year_id")
	private ErpAcademicYearDBO erpAcademicYearId;

	@Column(name ="erp_work_entry_date")
	private LocalDate erpWorkEntryDate;

	@ManyToOne
    @JoinColumn(name = "work_diary_approver_id")
	private EmpDBO workdDiaryApproverId;
	
	@Column(name= "approved_date")
	private LocalDateTime approvedDate;
	
	@Column(name= "clarification_remarks")
	private String clarificationRemarks;
	
	@ManyToOne
	@JoinColumn(name = "erp_applicant_work_flow_process_id")
	public ErpWorkFlowProcessDBO erpApplicantWorkFlowProcessDBO;

    @Column(name= "applicant_status_log_time")
	private LocalDateTime applicantStatusLogTime;
	
	@ManyToOne
	@JoinColumn(name = "erp_application_work_flow_process_id")
	public ErpWorkFlowProcessDBO erpApplicationWorkFlowProcessDBO;
	
	@Column(name = "application_status_log_time")
	private LocalDateTime applicationStatusLogTime;
	
    @Column(name = "created_users_id")
	private Integer createdUsersId;

	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;

	@Column(name = "record_status")
	private char recordStatus;
	
	@OneToMany(mappedBy = "empWorkDiaryEntriesDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<EmpWorkDiaryEntriesDetailsDBO> empWorkDiaryEntriesDetailsDBOSet = new HashSet<>();
}

	

