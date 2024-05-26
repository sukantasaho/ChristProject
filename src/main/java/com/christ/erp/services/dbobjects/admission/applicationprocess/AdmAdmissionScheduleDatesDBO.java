package com.christ.erp.services.dbobjects.admission.applicationprocess;

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
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "adm_admission_schedule_dates")
public class AdmAdmissionScheduleDatesDBO {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_admission_schedule_dates_id")
    private int id;
	
	@ManyToOne
	@JoinColumn(name = "adm_admission_schedule_id")
	private AdmAdmissionScheduleDBO admAdmissionScheduleDBO;
	
	@Column(name = "adm_schedule_date")
	private LocalDate admScheduleDate;
	
	@Column(name = "is_sunday")
	private Boolean isSunday;
	
	@Column(name = "is_holiday")
	private Boolean isHoliday;
	
	@Column(name = "is_date_not_available")
	private Boolean isDateNotAvailable;
	
    @Column(name = "created_users_id")
    private Integer createdUsersId;

    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;
    
    @Column(name = "record_status")
    private char recordStatus;
    
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY, mappedBy ="admAdmissionScheduleDatesDBO")
	private Set<AdmAdmissionScheduleTimeDBO> admAdmissionScheduleTimeDBOSet;
}