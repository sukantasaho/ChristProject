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
@Table(name = "erp_rooms")
@Setter
@Getter
public class ErpRoomsDBO implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_rooms_id")
	public int id;
	
	@Column(name="room_no")
	public Integer roomNo;
	
	@Column(name="room_name")
	public String roomName;
	
	@ManyToOne
	@JoinColumn(name="erp_room_type_id")
	public ErpRoomTypeDBO erpRoomTypeDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_block_id")
	public ErpBlockDBO erpBlockDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_floors_id")
	public ErpFloorsDBO erpFloorsDBO;
	
	@Column(name="created_users_id",updatable = false)
	public Integer createdUsersId;

	@Column(name="modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name="record_status")
	public Character recordStatus;	
}
