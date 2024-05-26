package com.christ.erp.services.dto.hostel.leavesandattendance;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HostelPunchingExemptionGridDTO {

	private Integer id;
	private String registerNo;
	private Integer hostelApplicationNo;
	private String studentName;
	private String programe;
	private LocalDate fromDate;
	private String fromSession;
	private LocalDate toDate;
	private String toSession;
	private String roomNo;
	private String className;
	
}



