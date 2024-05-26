package com.christ.erp.services.dbobjects.administraton.academicCalendar;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.ErpEmailsDBO;
import com.christ.erp.services.dbobjects.common.ErpNotificationsDBO;
import com.christ.erp.services.dbobjects.common.ErpSmsDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "erp_reminder_notifications")
@Setter
@Getter
public class ErpReminderNotificationsDBO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_reminder_notifications_id")
	private int id;
	
	@Column(name = "reminder_date_time")
	private LocalDateTime reminderDateTime;
		
	@ManyToOne
	@JoinColumn(name = "erp_calendar_personal_id")
	private ErpCalendarPersonalDBO erpCalendarPersonalDBO;
	
	@Column(name = "is_notification_activated")
	private boolean notificationActivated;
	
	@Column(name = "is_sms_activated")
	private boolean smsActivated;
	
	@Column(name = "is_email_activated")
	private boolean emailActivated;
	
	@ManyToOne
	@JoinColumn(name ="sms_template_id")
	private ErpTemplateDBO erpTemplateDBOForSms;
	
	@ManyToOne
	@JoinColumn(name = "email_template_id")
	private ErpTemplateDBO erpTemplateDBOForEmail;
	
	@Column(name = "created_users_id",updatable=false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;
	
	@Column(name = "reminder_comments")
	private String reminderComments;
	
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY, mappedBy ="erpReminderNotificationsDBO")
	private Set<ErpNotificationsDBO> erpNotificationsDBOSet;
	
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY, mappedBy ="erpReminderNotificationsDBO")
	private Set<ErpSmsDBO> erpSmsDBOSet;
	
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY, mappedBy ="erpReminderNotificationsDBO")
	private Set<ErpEmailsDBO> erpEmailsDBOSet;
}
