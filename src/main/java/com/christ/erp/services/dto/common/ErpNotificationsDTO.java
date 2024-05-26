package com.christ.erp.services.dto.common;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErpNotificationsDTO {
	
	private int id;
	private Integer count;
	private String message;
	private Integer erpNotificationUserEntriesId;
	private String notificationType;
	private String notificationContent;
	private boolean notificationSeen;
	private String notificationComponentPath;
	private String notificationDescription;
	private List<ErpNotificationsDTO> erpNotificationsDTOList;
}
