package com.christ.erp.services.dbobjects.hostel.leavesandattendance;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "hostel_leave_applications")
public class HostelLeaveApplicationsDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "hostel_leave_applications_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "hostel_admissions_id")
	private HostelAdmissionsDBO hostelAdmissionDBO;
	
	@ManyToOne
	@JoinColumn(name = "hostel_leave_type_id")
	private HostelLeaveTypeDBO hostelLeaveTypeDBO;
	
	@Column(name = "leave_from_date")
	private LocalDate leaveFromDate;
	
	@Column(name = "leave_from_date_session")
	private String leaveFromDateSession;
	
	@Column(name = "leave_to_date")
	private LocalDate leaveToDate;
	
	@Column(name = "leave_to_date_session")
	private String leaveToDateSession;
	
	@Column(name = "request_type")
	private String requestType;
	
	@Column(name = "leave_reason")
	private String leaveReason;
	
	@Column(name = "is_offline")
	private boolean isOffline;
	
	@Column(name = "is_cancelled")
	private boolean isCancelled;
	
	@Column(name = "cancellation_reason")
	private String cancellationReason;
	
	@ManyToOne
	@JoinColumn(name = "erp_applicant_work_flow_process_id")
	private ErpWorkFlowProcessDBO erpApplicantWorkFlowProcessId;
	
	@Column(name = "applicant_status_log_time")
	private LocalDateTime applicantStatusLogTime;
	
	@ManyToOne
	@JoinColumn(name = "erp_application_work_flow_process_id")
	private ErpWorkFlowProcessDBO erpApplicationWorkFlowProcessId;
	
	@Column(name = "application_status_log_time")
	private LocalDateTime applicationStatusLogTime;
	
	@Column(name = "created_users_id",updatable = false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;
	
	@OneToMany(mappedBy = "hostelLeaveApplicationsDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<HostelLeaveApplicationsDocumentDBO> hostelLeaveApplicationsDocumentDBOSet;
}