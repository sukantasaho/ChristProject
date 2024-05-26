package com.christ.erp.services.dto.student.common;

import java.math.BigDecimal;
import java.util.List;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.admission.applicationprocess.AdmQualificationListDTO;
import com.christ.erp.services.dto.admission.settings.AdmQualificationDegreeListDTO;
import com.christ.erp.services.dto.common.ErpInstitutionDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.curriculum.common.AcaSessionTypeDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudentEducationalDetailsDTO {

	  private int id;
	  private StudentApplnEntriesDTO studentApplnEntriesDTO;
	  private AdmQualificationListDTO admQualificationListDTO;
	  private AdmQualificationDegreeListDTO admQualificationDegreeListDTO;
	  private SelectDTO studentDTO;
	  private ErpInstitutionDTO erpInstitutionDTO;
	  private String institutionOthers;
	  private SelectDTO universityBoard;
	  private SelectDTO country;
	  private String institutionOthersState;
	  private SelectDTO state;
	  private Integer noOfPendingBacklogs;
	  private Integer yearOfPassing;
	  private Integer monthOfPassing;
	  private String examRegisterNo;
	  private Boolean isLevelCompleted;
	  private AcaSessionTypeDTO erpSessionType;
	  private Integer totalSemesters;
	  private BigDecimal consolidatedMarksObtained;
	  private BigDecimal consolidatedMaximumMarks;
	  private String percentage;
      private Boolean isResultDeclared;
	  private List<StudentEducationalMarkDetailsDTO> StudentEducationalMarkDetailS;
	  private List<StudentEducationalDetailsDocumentsDTO> studentEducationalDetailsDocuments;
	  
	  public StudentEducationalDetailsDTO(int id,Integer studentApplnEntriesId,Integer qualificationOrder,String degreeName,String universityBoardName,String countryName,String stateName,String institutionOthersState
			  ,String institutionName,String institutionOthers,Integer yearOfPassing,Integer monthOfPassing,BigDecimal consolidatedMaximumMarks,BigDecimal consolidatedMarksObtained,BigDecimal percentage) {
		  
		  this.id = id;
		  this.setStudentApplnEntriesDTO(new StudentApplnEntriesDTO());
		  this.getStudentApplnEntriesDTO().setId(studentApplnEntriesId);
		  this.setAdmQualificationListDTO(new AdmQualificationListDTO());
		  this.getAdmQualificationListDTO().setQualificationOrder(qualificationOrder);
		  this.setAdmQualificationDegreeListDTO(new AdmQualificationDegreeListDTO());
		  this.getAdmQualificationDegreeListDTO().setDegreeName(degreeName);
		  this.setUniversityBoard(new SelectDTO());
		  this.getUniversityBoard().setLabel(universityBoardName);
		  this.setCountry(new SelectDTO());
		  this.getCountry().setLabel(countryName);
		  if(!Utils.isNullOrEmpty(stateName)){
			  this.setState(new SelectDTO());
			  this.getState().setLabel(stateName);
		  } else {
			  this.setInstitutionOthersState(institutionOthersState);
		  }
		  if(!Utils.isNullOrEmpty(institutionName)){
			  this.setErpInstitutionDTO(new ErpInstitutionDTO());
			  this.getErpInstitutionDTO().setInstitutionName(institutionName);
		  } else {
			  this.setInstitutionOthers(institutionOthers);
		  }
		  this.setYearOfPassing(yearOfPassing);
		  this.setMonthOfPassing(monthOfPassing);
		  this.setConsolidatedMaximumMarks(consolidatedMaximumMarks);
		  this.setConsolidatedMarksObtained(consolidatedMarksObtained);
		  if(!Utils.isNullOrEmpty(percentage)) {
			  this.setPercentage(String.valueOf(percentage));
		  }
	  }
}

