package com.christ.erp.services.dto.employee.recruitment;

import java.util.List;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class EmpApplnEducationalDetailsDTO {

	public int empApplnEducationalDetailsId;
	public int empApplnEntriesId;
	public String qualificationName;
	public String qualificationLevelId;
	public String qualificationOthers;
	public String currentStatus;
	public String course;
	public String specialization;
	public String yearOfCompletion;
	public String gradeOrPercentage;
	public String institute;
	public String boardOrUniversity;
	public List<EmpApplnEducationalDetailsDocumentsDTO> documentList;
	public String qualificationLevelName;
	public String countryId;
	public String stateId;
	public String stateOther;
	private SelectDTO erpInstitute;
	private SelectDTO erpBoardOrUniversity;
	private String qualificationLevelCode;
	
	public EmpApplnEducationalDetailsDTO(int empApplnEntriesId,String course,String specialization,Integer yearOfCompletion,String gradeOrPercentage,String countryName,String stateName,String stateOthers,
			String universityBoardName,String boardOrUniversity,String institutionName,String institute,Integer qualificationLevelId) {
		this.empApplnEntriesId =  empApplnEntriesId;
		this.course = course;
		this.specialization = specialization;
		this.yearOfCompletion = !Utils.isNullOrEmpty(yearOfCompletion)?String.valueOf(yearOfCompletion):"";
		this.gradeOrPercentage = gradeOrPercentage;
		this.countryId = countryName;
		this.stateId = Utils.isNullOrEmpty(stateName)?stateName:stateOthers;
		this.boardOrUniversity = !Utils.isNullOrEmpty(universityBoardName)?universityBoardName:boardOrUniversity;
		this.institute = !Utils.isNullOrEmpty(institutionName)?institutionName:institute;
		this.qualificationLevelId = !Utils.isNullOrEmpty(qualificationLevelId) ? String.valueOf(qualificationLevelId) : null;
	}	
	
}
