package com.christ.erp.services.dbobjects.employee.attendance;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "emp_holiday_events_cd_map")
@Setter
@Getter
public class EmpHolidayEventsCddMapDBO  {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_holiday_events_cd_map_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name="emp_holiday_events_id")
	private EmpHolidayEventsDBO empHolidayEventsId;
	
	@ManyToOne
	@JoinColumn(name="erp_campus_department_mapping_id")
	private ErpCampusDepartmentMappingDBO erpCampusDeaneryDeptId;
	
	@Column(name = "created_users_id")
	private Integer createdUsersId;

	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;
}
