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

@SuppressWarnings("serial")
@Entity
@Table(name = "erp_floors")
@Getter
@Setter
public class ErpFloorsDBO implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_floors_id")
	public Integer id;
	
	@Column(name="floor_no")
	public Integer floorNo;
	
	@Column(name="floor_name")
	public String floorName;
	
	@ManyToOne
	@JoinColumn(name="erp_block_id")
	public ErpBlockDBO erpBlockDBO;
	
	@Column(name="created_users_id",updatable = false)
	public Integer createdUsersId;

	@Column(name="modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name="record_status")
	public Character recordStatus;	
}
