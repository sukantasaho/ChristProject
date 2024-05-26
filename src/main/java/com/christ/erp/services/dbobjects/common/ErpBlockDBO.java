package com.christ.erp.services.dbobjects.common;

import java.io.Serializable;
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

@Setter
@Getter
@SuppressWarnings("serial")
@Entity
@Table(name = "erp_block")
public class ErpBlockDBO implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_block_id")
	public Integer id;
	
	@Column(name="block_name")
	public String blockName;
	
	@ManyToOne
	@JoinColumn(name="erp_campus_id")
	public ErpCampusDBO erpCampusDBO;
	
	@Column(name="block_location_longitutde")
	public String blockLocationLongitude;
	
	@Column(name="block_location_lattitude")
	public String blockLocationLatitude;
	
	@Column(name="total_rooms_available")
	public Integer totalRoomsAvailable;
	
	@Column(name="total_rooms_active")
	public Integer totalRoomsactive;
	
	@Column(name="created_users_id",updatable = false)
	public Integer createdUsersId;

	@Column(name="modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name="record_status")
	public Character recordStatus;	
}
