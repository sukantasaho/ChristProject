package com.christ.erp.services.dbobjects.hostel.leavesandattendance;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "hostel_punching_exemption")
public class HostelPunchingExemptionDBO {

	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "hostel_punching_exemption_id")
	    public Integer id;

	    @ManyToOne
	    @JoinColumn(name = "hostel_admissions_id")
	    public HostelAdmissionsDBO hostelAdmissionsDBO;

	    @Column(name="exemption_start_date")
	    public LocalDate exemptionStartDate;

	    @Column(name = "exemption_start_session")
	    public String exemptionStartSession;
	    
	    @Column(name = "exemption_end_date")
	    public LocalDate exemptionEndDate;
	    
	    @Column(name = "exemption_end_session")
	    public String exemptionEndSession;

	    @Column(name = "exemption_reason")
	    public String exemptionReason;
	    
	    @Column(name = "created_users_id")
	    public Integer createdUsersId;

	    @Column(name = "created_time")
	    public LocalDateTime createdTime;
	    
	    @Column(name = "modified_time")
	    public LocalDateTime modifiedTime;

	    @Column(name = "modified_users_id")
	    public Integer modifiedUsersId;
	    
	    @Column(name = "record_status")
	    public char recordStatus;

}
