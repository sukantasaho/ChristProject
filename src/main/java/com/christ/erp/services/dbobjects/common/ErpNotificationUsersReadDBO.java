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

import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnRegistrationsDBO;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Table(name = "erp_notification_users_read")
@Setter
@Getter
public class ErpNotificationUsersReadDBO implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_notification_users_read_id")
	public Integer id;
	
	@ManyToOne
	@JoinColumn(name="erp_notifications_id")
	public ErpNotificationsDBO erpNotificationsDBO;
	
	@ManyToOne
	@JoinColumn(name="emp_appln_registrations_id")
	public EmpApplnRegistrationsDBO empApplnRegistrationsDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_users_id")
	public ErpUsersDBO erpUsersDBO;
	
	@Column(name="student_appln_registration_id")
	public Integer studentApplnRegistrationId;
	
	@Column(name="student_id")
	public Integer studentId;
	
	@Column(name="is_notification_seen")
	public boolean isNotificationSeen;
	
	@Column(name="created_users_id", updatable = false)
	public Integer createdUsersId;
	
	@Column(name="modified_users_id")
	public Integer modifiedUsersId;	
	
	@Column(name="record_status")
	public char recordStatus;
}
