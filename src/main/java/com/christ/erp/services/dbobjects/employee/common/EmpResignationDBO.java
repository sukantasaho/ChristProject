package com.christ.erp.services.dbobjects.employee.common;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Table(name="emp_resignation")
@Setter
@Getter
public class EmpResignationDBO implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="emp_resignation_id")
    public int id;
    
    @ManyToOne
	@JoinColumn(name="emp_id")
    public EmpDBO empDBO;
    
    @ManyToOne
	@JoinColumn(name="emp_resignation_reason_id")
    public EmpResignationReasonDBO empResignationReasonDBO;
    
    @Column(name="submission_date")
    public LocalDate submissionDate;
    
    @Column(name="vc_accepted_date")
    public LocalDate vcAcceptedDate;
    
    @Column(name="hod_recommended_relieving_date")
    public LocalDate hodRecomendedRelievingDate;
    
    @Column(name="is_serving_notice_period")
    public boolean isServingNoticePeriod;
    
    @Column(name="reference_no")
    public Integer referenceNo;

    @Column(name="relieving_date")
    public LocalDate relievingDate;
    
    @Column(name="reason_other")
    public String reasonOther;

    @Column(name="date_of_leaving")
    public LocalDate dateOfLeaving;
    
    @Column(name="po_remarks")
    public String poRemarks;
    
    @Column(name="created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;

    @Column(name="notice_period_served_days")
    public Integer noticePeriodServedDays;

    @Column(name="is_exit_interview_completed")
    public Boolean isExitInterviewCompleted;

}