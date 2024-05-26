package com.christ.erp.services.dto.student.common;

import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class StudentApplnPrerequisiteDTO {
	  private int id;
	  private SelectDTO studentApplnEntriesDTO;
//	  private AdmPrerequisiteSettingsDetailsPeriodDTOO admPreRequisiteSettingPeriodDTO;
	  private BigDecimal marksObtained;
	  private String examRollNo;
	  
	  private String examName;
	  private Integer examYear;
	  private Integer examMonth;
	  private Integer totalMarks;
	  
	  public StudentApplnPrerequisiteDTO(int id, Integer studentApplnEntriesId, Integer examYear, Integer examMonth, Integer totalMarks, String examName, BigDecimal marksObtained, String examRollNo) {
		  this.id = id;
		  this.setStudentApplnEntriesDTO(new SelectDTO());
		  this.getStudentApplnEntriesDTO().setValue(String.valueOf(studentApplnEntriesId));
		  this.setExamName(examName);
		  this.setExamYear(examYear);
		  this.setExamMonth(examMonth);
		  this.setTotalMarks(totalMarks);
		  this.setMarksObtained(marksObtained);
		  this.setExamRollNo(examRollNo);
	  }
}
