package com.christ.erp.services.dbobjects.hostel.settings;

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

@Getter
@Setter
@Entity
@Table(name = "hostel_seat_availability_details")
@SuppressWarnings("serial")
public class HostelSeatAvailabilityDetailsDBO implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "hostel_seat_availability_details_id")
	public int id;
	
	@ManyToOne
	@JoinColumn(name = "hostel_seat_availability_id")
	public HostelSeatAvailabilityDBO hostelSeatAvailabilityDBO;
	
	@ManyToOne
	@JoinColumn(name = "hostel_room_type_id")
	public HostelRoomTypeDBO hostelRoomTypeDBO;
	
	@Column(name = "total_seats")
	public Integer totalSeats;
	
	@Column(name = "available_seats")
	public Integer availableSeats;

	@Column(name="created_users_id", updatable=false)
	public Integer createdUsersId;

	@Column(name="modified_users_id")
	public Integer modifiedUsersId;

	@Column(name="record_status")
	public char recordStatus;
		
}
