package com.christ.erp.services.dbobjects.common;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "erp_notification_user_entries")
public class ErpNotificationUserEntriesDBO implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_notification_user_entries_id")
	public Integer id;
	
	@Column(name="notification_code")
	public String notificationCode;
	
	@Column(name="notification_content")
	public String notificationContent;

	@Column(name="notification_component_path")
	public String notificationComponentPath;
	
	@Column(name="created_users_id", updatable = false)
	public Integer createdUsersId;
	
	@Column(name="modified_users_id")
	public Integer modifiedUsersId;	
	
	@Column(name="record_status")
	public char recordStatus;
}
