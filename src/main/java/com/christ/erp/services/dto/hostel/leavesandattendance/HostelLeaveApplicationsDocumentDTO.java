package com.christ.erp.services.dto.hostel.leavesandattendance;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HostelLeaveApplicationsDocumentDTO {
	
	private int id;
	private HostelLeaveApplicationsDTO hostelLeaveApplicationsDTO;
	private String applicationDocumentsUrl;
	private Integer createdUsersId;
	private Integer modifiedUsersId;
	private char recordStatus;
	private String fileName;
	private String extension;
	public Boolean newFile;


}
