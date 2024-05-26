package com.christ.erp.services.dbobjects.administraton.academicCalendar;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
@Table(name ="erp_calendar_to_do_list")
public class ErpCalendarToDoListDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_calendar_to_do_list_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "emp_id")
	private EmpDBO empDBO;
	
	@ManyToOne
	@JoinColumn(name = "student_id")
	private StudentDBO studentDBO;
	
	@Column(name = "to_do_note")
	private String toDoNote;
	
	@Column(name = "to_do_date")
	private LocalDate toDoDate;
	
	@Column(name = "is_completed")
	private boolean completed;
	
	@Column(name = "created_users_id",updatable=false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;	
	
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY, mappedBy ="erpCalendarToDoListDBO")
	private Set<ErpCalendarPersonalDBO> erpCalendarPersonalDBOSet;
	
	@Column(name = "created_time")
	private LocalDateTime createdTime;
}