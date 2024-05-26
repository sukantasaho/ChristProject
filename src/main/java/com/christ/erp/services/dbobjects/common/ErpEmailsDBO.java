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
@Table(name = "erp_emails")
@Setter
@Getter
public class ErpEmailsDBO implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_emails_id")
	public int id;
	
	@ManyToOne
	@JoinColumn(name="erp_work_flow_process_notifications_id")
	public ErpWorkFlowProcessNotificationsDBO erpWorkFlowProcessNotificationsDBO;
	
	@Column(name="erp_entries_id")
	public Integer entryId;
	
	@ManyToOne
	@JoinColumn(name="erp_emails_user_entries_id")
	public ErpEmailsUserEntriesDBO erpEmailsUserEntriesDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_users_id")
	public ErpUsersDBO erpUsersDBO;
	
	@Column(name="erp_student_id")
	public Integer studentId;

	@Column(name="sender_name")
	public String senderName;

	@Column(name="recipient_email")
	public String recipientEmail;

	@Column(name="email_subject")
	public String emailSubject;
	
	@Column(name="email_content",columnDefinition="TEXT")
	public String emailContent;
	
	@Column(name="email_is_sent")
	public boolean emailIsSent;
	
	@Column(name="email_sent_time")
	public LocalDateTime emailSentTime;

	@Column(name="priority_level_order")
	public Integer priorityLevelOrder;

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
