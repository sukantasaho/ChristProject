package com.christ.erp.services.dbobjects.administraton.academicCalendar;

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

import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpLocationDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "erp_calendar")
@Setter
@Getter
public class ErpCalendarDBO { 
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_calendar_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "erp_academic_year_id")
	private ErpAcademicYearDBO erpAcademicYearDBO;
	
	@ManyToOne
	@JoinColumn(name = "erp_location_id")
	private ErpLocationDBO erpLocationDBO;
	
	@Column(name = "activities_events")
	private String activitiesEvents;
	
	@ManyToOne
	@JoinColumn(name = "erp_calendar_category_id")
	private ErpCalendarCategoryDBO erpCalendarCategoryDBO;
	
	@Column(name = "from_date")
	private LocalDate fromDate;
	
	@Column(name = "to_date")
	private LocalDate toDate;
	
	@Column(name = "is_published")
	private boolean published;
	
	@Column(name = "created_users_id",updatable=false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;	
	
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY, mappedBy ="erpCalendarDBO")
	private Set<ErpCalendarCampusDBO> erpCalendarCampusDBOSet;
	
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY, mappedBy ="erpCalendarDBO")
	private Set<ErpCalendarUserTypesDetailsDBO> erpCalendarUserTypesDetailsDBOSet;
	
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY, mappedBy ="erpCalendarDBO")
	private Set<ErpCalendarDatesDBO> erpCalendarDatesDBOSet;
	
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY, mappedBy ="erpCalendarDBO")
	private Set<ErpCalendarPersonalDBO> erpCalendarPersonalDBOSet;
}