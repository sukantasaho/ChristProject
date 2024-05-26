package com.christ.erp.services.dto.employee.attendance;

import java.time.LocalDateTime;
import java.util.List;
import com.christ.erp.services.dto.employee.common.EmpDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpWorkDiaryEntriesDTO {
    private int id;
	private String date;
	private EmpDTO empDTO ;
    private String totalHour;
    private LocalDateTime approvedDate;
    private String clarificationRemarks;
    private Integer applicantWorkFlowProcessId;
    private Integer applicationWorkFlowProcessId;
    private String status;
    private String workFlowStatus;
	private String totalHours;
	private String activityName;
	private String otherActivityTotal;
	private String otherActivityTotalHour;
	private List<EmpWorkDiaryEntriesDetailsDTO> empWorkDiaryEntriesDetails;
    private List<EmpAttendanceDTO> viewEmployeeAttendanceDto;
   }
