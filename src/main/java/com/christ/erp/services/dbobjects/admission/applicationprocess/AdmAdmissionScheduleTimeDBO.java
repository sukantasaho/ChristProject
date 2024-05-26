package com.christ.erp.services.dbobjects.admission.applicationprocess;

import java.time.LocalTime;
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

import com.christ.erp.services.dbobjects.student.common.StudentApplnEntriesDBO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "adm_admission_schedule_time")
public class AdmAdmissionScheduleTimeDBO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="adm_admission_schedule_time_id")
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "adm_admission_schedule_dates_id")
	private AdmAdmissionScheduleDatesDBO admAdmissionScheduleDatesDBO;

	@Column(name = "adm_schedule_time_slot")
	private LocalTime admScheduleTimeSlot;

	@Column(name = "max_no_of_seat_in_slot")
	private Integer maxNoOfSeatInSlot;

	@Column(name = "created_users_id")
	private Integer createdUsersId;

	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;

	@Column(name = "record_status")
	private char recordStatus;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "admAdmissionScheduleTimeDBO", cascade = CascadeType.ALL)
	public Set<StudentApplnEntriesDBO> studentApplnEntriesDBOs;
}