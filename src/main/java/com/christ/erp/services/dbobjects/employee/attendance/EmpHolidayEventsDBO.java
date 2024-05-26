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
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpLocationDBO;
import com.christ.erp.services.dbobjects.employee.common.EmpEmployeeCategoryDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "emp_holiday_events")
@Setter
@Getter
public class EmpHolidayEventsDBO  {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_holiday_events_id")
	public int id;

	@Column(name="emp_holiday_events_type_name")
	public String empHolidayEventsTypeName;

	@ManyToOne
	@JoinColumn(name = "erp_academic_year_id")
	public ErpAcademicYearDBO erpAcademicYearId;

	@ManyToOne
	@JoinColumn(name = "erp_location_id")
	public ErpLocationDBO erpLocationId;

	@ManyToOne
	@JoinColumn(name = "emp_employee_category_id")
	public EmpEmployeeCategoryDBO empEmployeeCategoryId;

	@Column(name="is_employeewise_exemption")
	public Boolean isEmployeewiseExemption;

	@Column(name="holiday_events_start_date")
	public LocalDate holidayEventsStartDate;

	@Column(name="holiday_events_end_date")
	public LocalDate holidayEventsEndDate;

	@Column(name="holiday_events_session")
	public String holidayEventsSession;

	@Column(name="is_one_time_signing")
	public Boolean isOneTimeSigning;

	@Column(name="holiday_events_description")
	public String holidayEventsDescription;

	@Column(name = "created_users_id")
	public Integer createdUsersId;

	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;

	@Column(name = "record_status")
	public char recordStatus;

	@OneToMany(fetch = FetchType.LAZY,mappedBy = "empHolidayEventsId",cascade=CascadeType.ALL)
	public Set<EmpHolidayEventsCddMapDBO> empHolidayEventsCddMapDBOSet = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY,mappedBy = "empHolidayEventsDBO",cascade=CascadeType.ALL)
	public Set<EmpHolidayEventsEmployeewiseDBO> empHolidayEventsEmployeewiseDBOSet = new HashSet<>();

	@Column(name = "is_exemption")
	private boolean isExemption;

	@Column(name = "scheduler_status")
	private String schedulerStatus;
}