package com.christ.erp.services.dto.common;

public class DashBoardCampusDepartmentAttendanceDTO {
	public int present;
	public int absent;
	public int halfDay;
	public int lateEntry;
	public int earlyExit;

	public DashBoardCampusDepartmentAttendanceDTO() {

	}

	public DashBoardCampusDepartmentAttendanceDTO(int present,int absent,int halfDay,int lateEntry,int earlyExit) {
		this.present=present;
		this.absent=absent;
		this.halfDay=halfDay;
		this.lateEntry=lateEntry;
		this.earlyExit=earlyExit;
	}
}
