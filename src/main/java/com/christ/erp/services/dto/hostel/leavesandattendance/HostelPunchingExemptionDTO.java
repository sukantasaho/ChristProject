package com.christ.erp.services.dto.hostel.leavesandattendance;

import java.time.LocalDate;
import java.util.List;

import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HostelPunchingExemptionDTO {

	private Integer id;
	private SelectDTO selectedYear;
	private SelectDTO selectedHostel;
	private SelectDTO selectedBlock;
	private SelectDTO selectedUnit;
	private LocalDate fromDate;
	private String fromSession;
	private LocalDate toDate;
	private String toSession;
	private String reason;
	private List<HostelPunchingExemptionGridDTO> exemptedStudentList;
	private List<HostelPunchingExemptionGridDTO> studentList;
	
	
}
