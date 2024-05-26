package com.christ.erp.services.dto.employee.common;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkFlowStatusApplicantTimeLineDTO {
	private Integer workFlowId;
	private LocalDateTime statusLogCreatedTime;
	private  LocalDateTime statusLogLastmodifiedTime;
	private Boolean isCurrentStatus;
	private String applicationDisplayText;
	
	public WorkFlowStatusApplicantTimeLineDTO(Integer workFlowId,String applicationDisplayText, LocalDateTime statusLogCreatedTime,LocalDateTime statusLogLastmodifiedTime, Boolean isCurrentStatus) {
		this.setWorkFlowId(workFlowId);
		this.setApplicationDisplayText (applicationDisplayText);
		this.setIsCurrentStatus(isCurrentStatus);
		this.setStatusLogCreatedTime(statusLogCreatedTime);
		this.setStatusLogLastmodifiedTime(statusLogLastmodifiedTime);
	}
}
