package com.christ.erp.services.dto.hostel.leavesandattendance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnAdvertisementDTO;
import com.christ.erp.services.dto.hostel.settings.HostelAdmissionsDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HostelLeaveApplicationsDTO {
	
	private int id;
	private HostelAdmissionsDTO hostelAdmissionsDTO;
	private SelectDTO hostelLeaveType;
	private LocalDate leaveFromDate;
	private String leaveFromDateSession;
	private LocalDate leaveToDate;
	private String leaveToDateSession;
	private String requestType;
	private String leaveReason;
	private boolean isOffline;
	private boolean isCancelled;
	private String cancellationReason;
	private String erpApplicantWorkFlowProcessId;
	private LocalDateTime applicantStatusLogTime;
	private String erpApplicationWorkFlowProcessId;
	private LocalDateTime applicationStatusLogTime;
	private Integer createdUsersId;
	private Integer modifiedUsersId;
	private char recordStatus;
	private String applicationMode;
	private List<HostelLeaveApplicationsDocumentDTO> hostelLeaveApplicationsDocumentDTOList;
	private String approverStatus;
	private String wardenApproval;
	private SelectDTO hostel;
	private SelectDTO academicYear;
	private SelectDTO block;
	private List<EmpApplnAdvertisementDTO> empApplnAdvertisementDTOList;
	private boolean approve;
}