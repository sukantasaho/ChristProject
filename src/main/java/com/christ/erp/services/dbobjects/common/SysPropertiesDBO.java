package com.christ.erp.services.dbobjects.common;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sys_properties")
@Getter
@Setter
public class SysPropertiesDBO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sys_properties_id")
	private int id;
	
	@Column(name = "property_name")
	private String propertyName;
	
	@Column(name = "property_value")
	private String propertyValue;
	
	@Column(name = "is_common_property")
	private Boolean commonProperty;
	
	@Column(name = "property_type")
	private String propertyType;
	
	@Column(name = "remarks")
	private String remarks;
	
	@Column(name = "created_users_id")
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;
	
	@OneToMany(mappedBy = "sysPropertiesDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<SysPropertiesDetailsDBO> sysPropertiesDetailsDBOSet;

}
