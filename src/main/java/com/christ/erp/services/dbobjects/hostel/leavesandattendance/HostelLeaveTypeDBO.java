package com.christ.erp.services.dbobjects.hostel.leavesandattendance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

@Entity
@Table(name = "hostel_leave_type")
public class HostelLeaveTypeDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "hostel_leave_type_id")
	private int id;
	
	@Column(name = "leave_type_name")
	private String leaveTypeName;
	
	@Column(name = "record_status")
	private char recordStatus;
	
	@Column(name = "created_users_id",updatable = false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;

}
