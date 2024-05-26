package com.christ.erp.services.dto.employee;

import java.time.LocalDate;

import com.christ.erp.services.common.Utils;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class EmpProfileSidePanelDTO {
	private int id;
	private String empNo;
	private String empName;
	private String personalMailId;
	private String officialMailId;
	private String status;
	private String designation;
	private String department;
	private String profilePhotoUrl;
	private String fileNameOriginal;
	private String uploadProcessCode;
	private String extnNo;
	private LocalDate lastPunchedInDate;
	private String lastPunchInTime;
	private String lastPunchOutTime;
	
	public EmpProfileSidePanelDTO() {
		
	}
	public EmpProfileSidePanelDTO(int id, String empNo, String empName, String personalMailId, String officialMailId, char status, String designation, String department,
			String profilePhotoUrl, String fileNameOriginal, String uploadProcessCode, Integer extnNo ) {
		this.id = id;
		this.empNo = empNo;
		this.empName = empName;
		this.personalMailId = personalMailId;
		this.officialMailId = officialMailId;
		this.status =  String.valueOf(status);
		this.designation = designation;
		this.department = department;
		this.profilePhotoUrl = profilePhotoUrl;
		this.fileNameOriginal = fileNameOriginal;
		this.uploadProcessCode = uploadProcessCode;
		if(!Utils.isNullOrEmpty(extnNo)) {
			this.extnNo = Integer.toString(extnNo);
		}
		else {
			this.extnNo = null;
		}
		
	}
}
