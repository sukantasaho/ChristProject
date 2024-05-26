package com.christ.erp.services.dto.common;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErpCommitteeDTO {
	
	private int id;
	private Boolean universityBOS;
	private SelectDTO erpAcademicYear;
	private SelectDTO importToYear;
	private List<SelectDTO> departmentList;
	private List<SelectDTO> internalMembers;
	private SelectDTO erpDepartment;
	private SelectDTO erpCommitteeType;
	private LocalDate programmeCourseStructureEntryLastDate;
	private List<ErpCommitteeMembersDTO> erpCommitteeMembersDTOList;
	private List<ErpCommitteeProgrammeDTO> erpCommitteeProgrammeDTOList;
	private List<ErpCommitteeProgrammeCourseReviewDTO> erpCommitteeProgrammeCourseReviewDTOList;
	private SelectDTO erpCampus;
	private Integer membersId;
	private SelectDTO erpProgram;
	private List<SelectDTO> erpProgramList;
	private List<ErpCommitteeCampusDTO> erpCommitteeCampusDTOList;
	private List<SelectDTO> erpCommitteeCampusList;
	
}
