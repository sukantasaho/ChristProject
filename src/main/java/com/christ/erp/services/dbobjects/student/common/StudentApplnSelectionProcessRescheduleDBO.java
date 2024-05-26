package com.christ.erp.services.dbobjects.student.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanCenterBasedDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDetailDBO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "student_appln_selection_process_reschedule")
@Setter
@Getter
public class StudentApplnSelectionProcessRescheduleDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "student_appln_selection_process_reschedule_id")
	public Integer id;
	
	@Column(name = "request_received_date_time")
	public LocalDateTime requestReceivedDateTime;
	
	@Column(name = "reschedule_requested_date")
	public LocalDate rescheduleRequestedDate;
	
	@Column(name = "reschedule_requested_time")
	public LocalTime rescheduleRequestedTime;
	
	@Column(name = "is_request_authorized")
	public Boolean  isRequestAuthorized;
	
	@Column(name = "is_request_rejected")
	public Boolean  isRequestRejected;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_appln_entries_id")
    private StudentApplnEntriesDBO studentApplnEntriesDBO;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adm_selection_process_plan_detail_id")
    private AdmSelectionProcessPlanDetailDBO admSelectionProcessPlanDetailDBO;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adm_selection_process_plan_center_based_id")
    private AdmSelectionProcessPlanCenterBasedDBO admSelectionProcessPlanCenterBasedDBO;
    
	@Column(name = "created_users_id", updatable = false)
	public Integer  createdUsersId;

	@Column(name = "modified_users_id")
	public Integer  modifiedUsersId;

	@Column(name = "record_status")
	public Character recordStatus;

}
