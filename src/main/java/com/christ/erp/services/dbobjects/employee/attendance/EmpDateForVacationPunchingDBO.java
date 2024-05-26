package com.christ.erp.services.dbobjects.employee.attendance;

import java.io.Serializable;
import java.time.LocalDate;
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

import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeCategoryDBO;

@Entity
@Table(name = "emp_vacation_punching_dates")
public class EmpDateForVacationPunchingDBO implements Serializable {
	
	private static final long serialVersionUID = -7644167167522438144L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_vacation_punching_dates_id")
	public Integer id;
	
	@ManyToOne
	@JoinColumn(name="emp_employee_category_id")
	public EmpEmployeeCategoryDBO empEmployeeCategoryDBO;
	
	@Column(name="vacation_punching_start_date")
	public LocalDate vacationPunchingStartDate;
	
	@Column(name="vacation_punching_end_date")
	public LocalDate vacationPunchingEndDate;
	
	@Column(name = "punching_dates_description")
	public String punchingDatesDescription ;
	
	@Column(name="created_users_id")
	public Integer createdUsersId;
	
//	@Column(name="created_time")
//	public Date createdTime;
//	
	@Column(name="modified_users_id")
	public Integer modifiedUsersId;
	
//	@Column(name="modified_time")
//	public Date modifiedTime;
	
	@Column(name="record_status")
	public Character recordStatus;	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "empDateForVacationPunchingDBO", cascade = CascadeType.ALL)
	public Set<EmpVacationPunchingDatesCDMapDBO> cdMapDBOs = new HashSet<>();
}
