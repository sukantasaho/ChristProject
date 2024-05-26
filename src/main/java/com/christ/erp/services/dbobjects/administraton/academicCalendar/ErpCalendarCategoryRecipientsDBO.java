package com.christ.erp.services.dbobjects.administraton.academicCalendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.employee.common.EmpDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "erp_calendar_category_recipients")
@Setter
@Getter
public class ErpCalendarCategoryRecipientsDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_calendar_category_recipients_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "erp_calendar_category_id")
	private ErpCalendarCategoryDBO erpCalendarCategoryDBO;
	
	@ManyToOne
	@JoinColumn(name = "emp_id")
	private EmpDBO empDBO;
	
	@Column(name = "created_users_id",updatable=false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;	
}