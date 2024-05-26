package com.christ.erp.services.dbobjects.common;

import java.io.Serializable;
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

import com.christ.erp.services.dbobjects.administraton.academicCalendar.ErpReminderNotificationsDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpApplnRegistrationsDBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@SuppressWarnings("serial")
@Entity
@Table(name = "erp_notifications")
public class ErpNotificationsDBO implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="erp_notifications_id")
	public Integer id;
	
	@Column(name = "notification_log_time")
	public LocalDateTime notificationLogTime;
	
	@Column(name = "notification_from_date_time")
	public LocalDateTime notificationFromDateTime;
	
	@Column(name = "notification_to_date_time")
	public LocalDateTime notificationToDateTime;
	
	@ManyToOne
	@JoinColumn(name="erp_work_flow_process_notifications_id")
	public ErpWorkFlowProcessNotificationsDBO erpWorkFlowProcessNotificationsDBO;
	
	@Column(name="erp_entries_id")
	public Integer entryId;
	
	@ManyToOne
	@JoinColumn(name="erp_notification_user_entries_id")
	public ErpNotificationUserEntriesDBO erpNotificationUserEntriesDBO;
	
	@ManyToOne
	@JoinColumn(name="emp_appln_registrations_id")
	public EmpApplnRegistrationsDBO empApplnRegistrationsDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_users_id")
	public ErpUsersDBO erpUsersDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_emp_campus_id")
	public ErpCampusDBO erpCampusDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_emp_department_id")
	public ErpDepartmentDBO erpDepartmentDBO;
	
	@Column(name="student_appln_registration_id")
	public Integer studentApplnRegistrationId;
	
	@Column(name="student_id")
	public Integer studentId;
	
	@Column(name="class_id")
	public Integer classId;
	
	@ManyToOne
	@JoinColumn(name="erp_student_campus_id")
	public ErpCampusDBO erpStudentCampusDBO;
	
	@ManyToOne
	@JoinColumn(name="erp_student_department_id")
	public ErpDepartmentDBO erpStudentDepartmentDBO;
	
	@Column(name="created_users_id", updatable = false)
	public Integer createdUsersId;
	
	@Column(name="modified_users_id")
	public Integer modifiedUsersId;	
	
	@Column(name="record_status")
	public char recordStatus;
	
	@ManyToOne
	@JoinColumn(name = "erp_reminder_notifications_id")
	private ErpReminderNotificationsDBO erpReminderNotificationsDBO;
	
	@OneToMany(cascade = CascadeType.ALL,mappedBy = "erpNotificationsDBO",fetch = FetchType.LAZY)
	private Set<ErpNotificationUsersReadDBO> erpNotificationUsersReadDBOSet;

}
