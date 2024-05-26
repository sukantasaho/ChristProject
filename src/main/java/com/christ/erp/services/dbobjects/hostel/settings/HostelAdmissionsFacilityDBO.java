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
@Table(name = "hostel_admissions_facility")
@Getter
@Setter

public class HostelAdmissionsFacilityDBO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="hostel_admissions_facility_id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name ="hostel_admissions_id")
	private HostelAdmissionsDBO hostelAdmissionsDBO;
	
	@ManyToOne
	@JoinColumn(name ="hostel_facility_setting_id")
	private HostelFacilitySettingDBO hostelFacilitySettingsDBO;
	
	@Column(name ="facility_description")
	private String facilityDescription;
	
	@Column(name ="verified_for_checkout")
	private Boolean verifiedForCheckout;
	
	@Column(name = "record_status")
	private char recordStatus;
	
	@Column(name = "created_users_id",updatable=false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
}
