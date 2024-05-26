package com.christ.erp.services.dto.employee.recruitment;

import java.time.LocalDateTime;
import java.util.List;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmpApplnInterviewSchedulesDTO extends ExModelBaseDTO {
	public String id;
	public String pointOfContactUsersId;
	public String interviewVenue;
	public boolean isRescheduled;
	public String applicationNo;
	public boolean approved;
	public String approvedBy;
	public List<LookupItemDTO> internalPanelists;
	public List<LookupItemDTO> externalPanelists;
	public String joiningDate;
	public String reportingDate;
	private LocalDateTime interviewDateTime1;
	private Integer interviewRound;
	private String comments;
	private SelectDTO pointofContact;
}
