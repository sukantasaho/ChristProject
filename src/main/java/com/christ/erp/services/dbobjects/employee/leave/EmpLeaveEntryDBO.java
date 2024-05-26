package com.christ.erp.services.dbobjects.employee.leave;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.UrlAccessLinkDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpDBO;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Getter
@Setter
@Table(name = "emp_leave_entry")
public class EmpLeaveEntryDBO implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emp_leave_entry_id")
	public int id;

	@ManyToOne
	@JoinColumn(name = "emp_id")
	public EmpDBO empID;

	@ManyToOne
	@JoinColumn(name = "emp_leave_type_id")
	public EmpLeaveTypeDBO leaveTypecategory;

	@Column(name = "leave_start_date")
	public LocalDate startDate;

	@Column(name = "leave_end_date")
	public LocalDate endDate;

	@Column(name = "leave_start_session")
	public String fromSession;

	@Column(name = "leave_end_session")
	public String toSession;

	@Column(name = "leave_reason")
	public String reason;

	@Column(name = "leave_document_url")
	public String leaveDocumentUrl;

	@Column(name = "is_offline")
	public boolean offline;

	@ManyToOne
	@JoinColumn(name = "erp_applicant_work_flow_process_id")
	public ErpWorkFlowProcessDBO erpApplicantWorkFlowProcessDBO;

	@ManyToOne
	@JoinColumn(name = "erp_application_work_flow_process_id")
	public ErpWorkFlowProcessDBO erpApplicationWorkFlowProcessDBO;

	@Column(name = "number_of_days_leave")
	public BigDecimal totalDays;
	
	@Column(name = "leave_year")
	public Integer leaveYear;
	
	@ManyToOne
	@JoinColumn(name = "emp_leave_approver_id")
	public EmpDBO approverId;
	
	@Column(name = "approver_latest_comment")
	public String approverComments;
	
	@Column(name = "applicant_status_log_time")
	public LocalDate applicantStatusLogTime;
	
	@Column(name = "application_status_log_time")
	public LocalDate applicationStatusLogTime;
	
	@Column(name = "authorizer_latest_comment")
	public String authorizerLatestComment;
	
	@ManyToOne
	@JoinColumn(name = "emp_leave_authorizer_id")
	public EmpDBO empLeaveAuthorizerId;
	
	@ManyToOne
	@JoinColumn(name = "emp_leave_forwarded_1_id")
	public EmpDBO empLeaveForwarded1Id;
	
	@Column(name = "forwarded_1_latest_comment")
	public String forwarded1LatestComment;
	
	@ManyToOne
	@JoinColumn(name = "emp_leave_forwarded_2_id")
	public EmpDBO empLeaveForwarded2Id;
	
	@Column(name = "forwarded_2_latest_comment")
	public String forwarded2LatestComment;
	
	@Column(name = "created_users_id", updatable = false)
	public Integer createdUsersId;

	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;

	@Column(name = "record_status")
	public char recordStatus;
	
	@Column(name = "scheduler_status")
	public String schedulerStatus;
	
	@Column(name = "is_supervisor")
	public boolean isSupervisor;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "leave_document_url_id")
	public UrlAccessLinkDBO leaveDocumentUrlDBO;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "empLeaveEntryId", cascade = CascadeType.ALL)
	private Set<EmpLeaveEntryDetailsDBO> empLeaveEntryDetails;
	
	@Column(name = "is_pending")
	public boolean isPending;
	
}