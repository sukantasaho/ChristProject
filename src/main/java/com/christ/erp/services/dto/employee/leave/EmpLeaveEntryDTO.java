package com.christ.erp.services.dto.employee.leave;

import java.time.LocalDate;
import java.util.List;

import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.dto.common.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpLeaveEntryDTO extends ModelBaseDTO {
	public int id;
	public String name;
	public String department;
	public String designation;
	public String campus;
	public String email;
	public String leaveApplication;
	public String isExempted;
	public String employeeId;
	public LocalDate startDate;
	public LocalDate endDate;
	public String reason;
	public String leaveTypeName;
	public boolean sundayWorking;
	public boolean holidayWorking;
	public ExModelBaseDTO leaveTypecategory;
	public ExModelBaseDTO fromSession;
	public ExModelBaseDTO toSession;
	public boolean isLeaveTypeDocumentRequired;
	public String leavePolicy;
	public LocalDate tempStartDate;
	public LocalDate tempEndDate;
	public ExModelBaseDTO tempFormSession;
	public ExModelBaseDTO tempToSession;
	public String leaveDocumentUrl;
	public String mode;
	public String locationId;
	public String initilizeMonth;
	public Integer applicantWorkFlowProcessId;
	public Integer applicationWorkFlowProcessId;
	public String status;
	public String processCode;
	public String comment;
	public String totalDays;
	public String approverId;
	public SelectColorDTO approverStatus;
	public SelectDTO approverForwardedEmployee;
	public String approverComments;
	public String applicantStatusLogTime;
	public String applicationStatusLogTime;
	public SelectColorDTO authoriserStatus;
	public String authoriserComments;
	public String leaveApproverStatusCheckbox;
	public String empNo;
	public SelectDTO leaveInitializationYear;
	public String empId;
	public boolean sundayWorkingDay;
	public boolean holidayWorkingDay;
	public SelectDTO approverForwarded2Employee;
	private String approverForwardedEmployeeComments;
	private String approverForwarded2EmployeeComments;
	private String schedulerStatus;
	private String employeePhotoUrl;
	private CommonUploadDownloadDTO documentUrl;
	private String departmentId;
	private String showAlert;
	private String alertMsg;
	private String approvedName;
	public boolean isApprover;
	public boolean isHod;
	public boolean isDean;
	public boolean isAdmin;
	private FileUploadDownloadDTO fileUploadDownloadDTO;
	private String forwarderId;
	private String forwarderForwardedId;

}
