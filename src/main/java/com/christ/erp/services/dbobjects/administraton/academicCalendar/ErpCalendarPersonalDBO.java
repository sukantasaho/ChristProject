package com.christ.erp.services.dbobjects.administraton.academicCalendar;

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

import com.christ.erp.services.dbobjects.employee.common.EmpDBO;
import com.christ.erp.services.dbobjects.student.common.StudentDBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "erp_calendar_personal")
public class ErpCalendarPersonalDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_calendar_personal_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "erp_calendar_id")
	private ErpCalendarDBO erpCalendarDBO;
	
	@ManyToOne
	@JoinColumn(name = "emp_id")
	private EmpDBO empDBO;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "erp_calendar_to_do_list_id")
	private ErpCalendarToDoListDBO erpCalendarToDoListDBO;
	
	@Column(name = "events_note")
	private String eventsNote;
	
	@Column(name = "important_priority")
	private String importantPriority;
	
	@Column(name = "is_completed")
	private boolean completed;
	
	@Column(name = "is_important")
	private boolean important;
	
	@ManyToOne
	@JoinColumn(name = "student_id")
	private StudentDBO studentDBO;
	
	@Column(name = "created_users_id",updatable=false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;	
		
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY, mappedBy ="erpCalendarPersonalDBO")
	private Set<ErpReminderNotificationsDBO> erpReminderNotificationsDBOSet; 
}