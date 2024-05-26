package com.christ.erp.services.dto.admission.applicationprocess;

import java.time.LocalDateTime;
import java.util.List;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FinalResultEntryDTO {
	
	private SelectDTO academicYear;
	private SelectDTO location;
	private SelectDTO campus;
	private SelectDTO level;
	private SelectDTO programme;
	private List<SelectDTO> campusList;
	private Integer applicationNo;
	private String applicantName;
	private String gender;
	private String totalWeightage;
	private String residentCategory;
	private String status;
	private String processCode;
	private String appliedProgrammeCampus;
	private SelectDTO selectedProgramme;
	private SelectDTO selectedCampus;
	private SelectDTO admissionCategory;
	private LocalDateTime lastDateAndTimeFeepayment;

}
