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

@SuppressWarnings("serial")
@Entity
@Table(name = "erp_notification_user_preference")
public class ErpNotificationUserPrefernceDBO implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_notification_user_preference_id")
	public int id;

	@ManyToOne
	@JoinColumn(name="erp_work_flow_process_notifications_id")
	public ErpWorkFlowProcessNotificationsDBO erpWorkFlowProcessNotificationsDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_users_id")
	public ErpUsersDBO erpUsersDBO;
	
	@Column(name="is_notification_deactivated")
	public Boolean isNotificationDeactivated;

	@Column(name="is_email_deactivated")
	public Boolean isEmailDeactivated;

	@Column(name="is_sms_deactivated")
	public Boolean isSmsDeactivated;
	
	@Column(name="created_users_id", updatable = false)
	public Integer createdUsersId;
	
	@Column(name="modified_users_id")
	public Integer modifiedUsersId;	
	
	@Column(name="record_status")
	public char recordStatus;
}
