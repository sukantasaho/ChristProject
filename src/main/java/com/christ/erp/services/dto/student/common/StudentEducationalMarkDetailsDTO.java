package com.christ.erp.services.dto.student.common;

import java.math.BigDecimal;

import com.christ.erp.services.dto.admission.settings.AdmProgrammeQualificationSubjectEligibilityDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class StudentEducationalMarkDetailsDTO {

	 private int id;
     private String semesterName;
	 private BigDecimal marksObtained;
	 private BigDecimal maximumMarks;
	 private Integer displayOrder;
	 private BigDecimal sgpa;
	 private BigDecimal cgpa;
	 private Integer totalPendingBacklogs;
	 private Boolean isResultDeclared;
	 private AdmProgrammeQualificationSubjectEligibilityDTO admProgrammeQualificationSubjectEligibilityDTO;
}
