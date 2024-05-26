package com.christ.erp.services.dbobjects.employee.attendance;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;

@SuppressWarnings("serial")
@Entity
@Table(name = "emp_vacation_punching_dates_cd_map")
public class EmpVacationPunchingDatesCDMapDBO implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_vacation_punching_dates_cd_map_id")
	public  Integer id;	
	
	@ManyToOne
	@JoinColumn(name="erp_campus_department_mapping_id")
	public ErpCampusDepartmentMappingDBO erpCampusDepartmentMappingDBO;
	
	@ManyToOne
	@JoinColumn(name="emp_vacation_punching_dates_id")
	public EmpDateForVacationPunchingDBO  empDateForVacationPunchingDBO;
	
	@Column(name="created_users_id")
	public Integer createdUsersId;
	
	@Column(name="modified_users_id")
	public Integer modifiedUsersId;;
	
	@Column(name="record_status")
	public Character recordStatus;	
}
