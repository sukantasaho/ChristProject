package com.christ.erp.services.dbobjects.employee.attendance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = " emp_work_diary_activity")
@Setter
@Getter
public class EmpWorkDiaryActivityDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emp_work_diary_activity_id")
	private int id;
	
	@Column(name = "activity_name")
	private String activityName;
	
	@Column(name = "is_for_teaching")
	private boolean isForTeaching;
	
	@ManyToOne
	@JoinColumn(name = "emp_work_diary_main_activity_id")
	private EmpWorkDiaryMainActivityDBO empWorkDiaryMainActivityDBO;
	
	@ManyToOne
	@JoinColumn(name = "erp_department_id")
	private ErpDepartmentDBO erpDepartmentDBO;
	
	@Column(name = "created_users_id")
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;
}
