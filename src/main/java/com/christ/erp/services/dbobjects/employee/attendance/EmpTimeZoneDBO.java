package com.christ.erp.services.dbobjects.employee.attendance;

import javax.persistence.*;

import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeCategoryDBO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "emp_time_zone")
@Setter
@Getter
public class EmpTimeZoneDBO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_time_zone_id")
	private int id;
	
	@Column(name="time_zone_name")
	private String timeZoneName;
	
	@Column(name="is_holiday_time_zone")
	private boolean isHolidayTimeZone;

	@Column(name="is_general_time_zone")
	private boolean isGeneralTimeZone;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "empTimeZoneDBO", cascade=CascadeType.ALL)
	private Set<EmpTimeZoneDetailsDBO>  empTimeZoneDetailsDBOSet;
	
	@Column(name="record_status")
	private char recordStatus;
	
	@Column(name = "created_users_id")
	private Integer createdUsersId;

	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;

	@ManyToOne
	@JoinColumn(name = "emp_employee_category_id")
	private EmpEmployeeCategoryDBO empEmployeeCategoryDBO;

	@Column(name = "is_employeewise")
	private boolean isEmployeewise;
}
