package com.christ.erp.services.dbobjects.administraton.academicCalendar;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "erp_academic_calendar_details")
public class ErpAcademicCalendarDetailsDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_academic_calendar_details_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "erp_academic_calendar_id")
	private ErpCalendarDBO erpCalendarDBO;
	
	@Column(name = "date")
	private LocalDate date;
	
	@Column(name = "created_users_id")
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;	
}