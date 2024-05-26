package com.christ.erp.services.dbobjects.common;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.administraton.academicCalendar.ErpReminderNotificationsDBO;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Table(name = "erp_sms")

@Setter
@Getter
public class ErpSmsDBO implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_sms_id")
	public int id;
	
	@ManyToOne
	@JoinColumn(name="erp_work_flow_process_notifications_id")
	public ErpWorkFlowProcessNotificationsDBO erpWorkFlowProcessNotificationsDBO;
	
	@Column(name="erp_entries_id")
	public Integer entryId;

	@ManyToOne
	@JoinColumn(name="erp_sms_user_entries_id")
	public ErpSmsUserEntriesDBO erpSmsUserEntriesDbo;
	
	@ManyToOne
	@JoinColumn(name="erp_users_id")
	public ErpUsersDBO erpUsersDBO;
	
	@Column(name="student_id")
	public Integer studentId;
	
	@Column(name="recipient_mobile_no")
	public String recipientMobileNo;
	
	@Column(name="sms_content",columnDefinition="TEXT")
	public String smsContent;
	
	@Column(name="sms_is_sent")
	public Boolean smsIsSent;
	
	@Column(name="sms_sent_time")
	public LocalDateTime smsSentTime;
	
	@Column(name="sms_is_delivered")
	public Boolean smsIsDelivered;
	
	@Column(name="sms_delivered_time")
	public LocalDateTime smsDeliveredTime;

	@Column(name="template_id")
	private String templateId;

	@Column(name="gateway_response",columnDefinition="TEXT")
	private String gatewayResponse;

	@Column(name="message_status")
	private String messageStatus;

	@Column(name="sms_transaction_id")
	private String smsTransactionId;
	
	@Column(name="created_users_id", updatable = false)
	public Integer createdUsersId;
	
	@Column(name="modified_users_id")
	public Integer modifiedUsersId;	
	
	@Column(name="record_status")
	public char recordStatus;
	
	@ManyToOne 
	@JoinColumn(name = "erp_reminder_notifications_id")
	private ErpReminderNotificationsDBO erpReminderNotificationsDBO;
}
