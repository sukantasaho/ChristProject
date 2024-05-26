package com.christ.erp.services.dto.administraton.academicCalendar;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErpReminderNotificationsDTO {
	
	private int id;
	private ErpCalendarPersonalDTO erpCalendarPersonalDTO;
	private boolean notificationActivated;
	private boolean smsActivated;
	private boolean emailActivated;
	private LocalDateTime reminderDateTime;
	private char recordStatus;
	private String reminderComments;
	private Integer erpNotificationId;
	private Integer erpSmsId;
	private Integer erpEmailId;
}
