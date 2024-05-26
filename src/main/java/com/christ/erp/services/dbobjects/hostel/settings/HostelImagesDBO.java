package com.christ.erp.services.dbobjects.hostel.settings;

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
@Table(name= "hostel_images")

@Getter
@Setter

public class HostelImagesDBO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name ="hostel_images_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name ="hostel_id")
	private HostelDBO hostelDBO;
	
	@Column(name ="hostel_image_url")
	private String hostelImageUrl;
	
	@Column(name="created_users_id", updatable=false)
    public Integer createdUsersId;

    @Column(name="modified_users_id")
    public Integer modifiedUsersId;

    @Column(name="record_status")
    public char recordStatus;
}
