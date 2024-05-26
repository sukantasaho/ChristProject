package com.christ.erp.services.dbobjects.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table (name = "sys_properties_details")
@Setter
@Getter
public class SysPropertiesDetailsDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sys_properties_details_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "sys_properties_id")
	private SysPropertiesDBO sysPropertiesDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_campus_id")
	private ErpCampusDBO erpCampusDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_location_id")
	private ErpLocationDBO erpLocationDBO;

	@Column(name = "property_detail_value")
	private String propertyDetailValue;
	
	@Column(name = "created_users_id")
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;

}
