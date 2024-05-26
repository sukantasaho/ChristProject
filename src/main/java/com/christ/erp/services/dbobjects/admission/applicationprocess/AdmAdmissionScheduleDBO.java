package com.christ.erp.services.dbobjects.admission.applicationprocess;

import java.time.LocalDate;
import java.time.LocalTime;
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
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "adm_admission_schedule")
@Setter
@Getter
public class AdmAdmissionScheduleDBO {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_admission_schedule_id")
    private int id;
	
	@ManyToOne
	@JoinColumn(name = "erp_academic_year_id")
	private ErpAcademicYearDBO erpAcademicYearDBO;
	
	@ManyToOne
	@JoinColumn(name = "erp_campus_id")
	private ErpCampusDBO erpCampusDBO;
	
	@Column(name = "adm_schedule_from_date")
	private LocalDate admScheduleFromDate;
	
	@Column(name = "adm_schedule_to_date")
	private LocalDate admScheduleToDate;
	
	@Column(name = "saturday_end_time_slot")
	private LocalTime saturdayEndTimeSlot;
	
    @Column(name = "created_users_id")
    private Integer createdUsersId;

    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;
    
    @Column(name = "record_status")
    private char recordStatus;
    
    @Column(name = "is_sunday_included")
	private Boolean isSundayInclude;
	
	@Column(name = "is_holiday_included")
	private Boolean isHolidayInclude;
    
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY, mappedBy ="admAdmissionScheduleDBO")
    private Set<AdmAdmissionScheduleDatesDBO> admAdmissionScheduleDatesDBOSet;
}