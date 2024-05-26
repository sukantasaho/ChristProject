package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpWorkFlowProcessNotificationsDTO {
	private int id;
	private String notificationCode;
	private String notificationHyperlink;
	private SelectDTO erpWorkFlowProcessDBO;
	private String notificationDescription;
	private String notificationContent;
	private Boolean isNotificationActivated;
	private Boolean isSmsActivated;
	private Boolean isEmailActivated;
	private char recordStatus;
	private SelectDTO erpSmsTemplateDBO;
	private SelectDTO erpEmailsTemplateDBO;
}
