package com.christ.erp.services.dbobjects.common;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.employee.common.EmpDBO;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Table(name = "erp_room_emp_mapping")
@Getter
@Setter
public class ErpRoomEmpMappingDBO implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_room_emp_mapping_id")
	public int id;
	
	@ManyToOne
	@JoinColumn(name="erp_rooms_id")
	public ErpRoomsDBO erpRoomsDBO;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="emp_id")
	public EmpDBO empDBO;
	
	@Column(name="cabin_no")
	public Integer cabinNo;
	
	@Column(name="telephone_number")
	public String telephoneNumber;
	
	@Column(name="telephone_extension")
	public Integer telephoneExtension;
	
	@Column(name="last_active_time_in_office")
	public LocalDateTime lastActiveTimeInOffice;
	
	@Column(name = "created_users_id", updatable = false)
	public Integer createdUsersId;

	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;

	@Column(name = "record_Status")
	public char recordStatus;
}
