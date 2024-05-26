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

@Entity
@Table(name = "hostel_application_room_type_preference")
@SuppressWarnings("serial")
@Getter
@Setter

public class HostelApplicationRoomTypePreferenceDBO implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "hostel_application_room_type_preference_id")
	private int id;	
	
	@ManyToOne
	@JoinColumn(name ="hostel_application_id")
	private HostelApplicationDBO hostelApplicationDBO;
	
	@ManyToOne
	@JoinColumn(name ="hostel_room_type_id")
	private HostelRoomTypeDBO hostelRoomTypesDBO;
	
	@Column(name ="preference_order")
	private Integer preferenceOrder;
	
	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
