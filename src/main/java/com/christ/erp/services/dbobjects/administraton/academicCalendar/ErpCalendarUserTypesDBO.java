package com.christ.erp.services.dbobjects.administraton.academicCalendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeCategoryDBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "erp_calendar_user_types")
public class ErpCalendarUserTypesDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_calendar_user_types_id")
	private int id;
	
	@Column(name = "user_type")
	private String userType;
	
	@Column(name = "is_student")
	private boolean isStudent;
	
	@ManyToOne
	@JoinColumn(name = "emp_employee_category_id")
	private EmpEmployeeCategoryDBO empEmployeeCategoryDBO;
	
	@Column(name = "created_users_id")
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;	
}