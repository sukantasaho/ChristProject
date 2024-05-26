package com.christ.erp.services.dbobjects.administraton.academicCalendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.ErpCampusDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "erp_calendar_campus")
@Setter
@Getter
public class ErpCalendarCampusDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_calendar_campus_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "erp_calendar_id")
	private ErpCalendarDBO erpCalendarDBO;
	
	@ManyToOne
	@JoinColumn(name = "erp_campus_id")
	private ErpCampusDBO erpCampusDBO;
	
	@Column(name = "created_users_id",updatable=false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;	
}