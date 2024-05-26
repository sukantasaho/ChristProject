package com.christ.erp.services.dto.common;

public class AttendanceCumulativeDTO {
	public String name;
	public Float value;
	public String leaveTypeName;
	public String colorCode;
	public String sanctionedLeaves;
	public String allocatedLeaves;
	public String balanceLeave;
	public String pendingLeaves;
	public String leaveTypeCode;

	public AttendanceCumulativeDTO() {

	}

	public AttendanceCumulativeDTO(String name, Float value, String leaveTypeName, String colorCode, String sanctionedLeaves, String allocatedLeaves,
			String balanceLeave,String leaveTypeCode,String pendingLeaves) {
		this.name = name;
		this.value = value;
		this.leaveTypeName=leaveTypeName;
		this.colorCode=colorCode;
		this.sanctionedLeaves=sanctionedLeaves;
		this.allocatedLeaves=allocatedLeaves;
		this.balanceLeave=balanceLeave;
		this.leaveTypeCode = leaveTypeCode;
		this.pendingLeaves = pendingLeaves;
	}
}

