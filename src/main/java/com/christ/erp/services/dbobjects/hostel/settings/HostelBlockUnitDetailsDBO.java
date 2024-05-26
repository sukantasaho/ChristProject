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
import com.christ.erp.services.dbobjects.common.ErpUsersDBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
@Entity
@Table(name = "hostel_block_unit_details")
public class HostelBlockUnitDetailsDBO implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="hostel_block_unit_details_id")
	public Integer id;
	
	@Column(name="sequence_no")
	public Integer sequenceNo;
	
	@ManyToOne
	@JoinColumn(name="erp_users_id")
	public ErpUsersDBO erpUsersDBO;
	
	@ManyToOne
	@JoinColumn(name="hostel_block_unit_id")
	public HostelBlockUnitDBO hostelBlockUnitDBO;
	
	@Column(name="hostel_position")
	public String hostelPosition;
	
	@Column(name="position_mobile_no")
	public String positionMobileNo;
	
	@Column(name="position_email")
	public String positionEmail;
	
	@Column(name="position_phone_no")
	public String positionPhoneNo;
	
	@Column(name = "is_sent_sms_email_morning_absence")
	public Boolean isSentSmsEmailMorningAbsence;
	
	@Column(name = "is_sent_sms_email_evening_absence")
	public Boolean isSentSmsEmailEveningAbsence;
	
	@Column(name = "created_users_id")
	public Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer modifiedUsersId;
	
	@Column(name = "record_status")
	public char recordStatus;
}
