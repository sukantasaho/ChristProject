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
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Getter 
@Setter
@Table(name = "erp_work_flow_process_notifications")
public class ErpWorkFlowProcessNotificationsDBO implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_work_flow_process_notifications_id")
	public int id;
	
	@Column(name="notification_code")
	public String notificationCode;
	
	@Column(name="notification_hyperlink")
	public String notificationHyperlink;
	
	@ManyToOne
	@JoinColumn(name="erp_work_flow_process_id")
	public ErpWorkFlowProcessDBO erpWorkFlowProcessDBO;
	
	@Column(name="notification_description")
	public String notificationDescription;
	
	@Column(name="notification_content")
	public String notificationContent;
	
	@Column(name="is_notification_activated")
	public Boolean isNotificationActivated;

	@Column(name="is_sms_activated")
	public Boolean isSmsActivated;

	@Column(name="is_email_activated")
	public Boolean isEmailActivated;

	@Column(name="created_users_id", updatable = false)
	public Integer createdUsersId;
	
	@Column(name="modified_users_id")
	public Integer modifiedUsersId;	
	
	@Column(name="record_status")
	public char recordStatus;
	
	@ManyToOne
	@JoinColumn(name="sms_template_id")
	public ErpTemplateDBO erpSmsTemplateDBO;
	
	@ManyToOne
	@JoinColumn(name="email_template_id")
	public ErpTemplateDBO erpEmailsTemplateDBO;
}
