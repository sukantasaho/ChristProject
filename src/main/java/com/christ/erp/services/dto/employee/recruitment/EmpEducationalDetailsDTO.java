package com.christ.erp.services.dto.employee.recruitment;

import java.util.List;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class EmpEducationalDetailsDTO {
	public String id;
	public int empId;
	public int empApplnEntriesId;
	public String qualificationName;
	public String qualificationLevelId;
	public String qualificationOthers;
	public String currentStatus;
	public String course;
	public String specialization;
	public LookupItemDTO yearOfCompletion;
	public String gradeOrPercentage;
	public String institute;
	public String boardOrUniversity;
	public String qualificationLevelName;
	public String countryId;
	public String stateId;
	public boolean isAddMore;
	public LookupItemDTO state;
	public LookupItemDTO country;
	public LookupItemDTO qualificationLevel;
	public String stateOthers;
	public List<EmpEducationalDetailsDocumentsDTO> documentList;
	public String qualificationLevelOrder;
	private SelectDTO erpInstitute;
	private SelectDTO erpBoardOrUniversity;
	
	public EmpEducationalDetailsDTO(int empId, int qualificationLevelId, String qualificationLevelName , String course, String specialization, Integer yearOfCompletion, String gradeOrPercentage
			                        ,String erpInstitutionDBOName ,String institute, String boardOrUniversity, String erpUniversityBoardDBOName
			                        ,String erpCountry, String stateOthers, String erpStateDBOName, String currentStatus) {
		this.empId = empId;
		this.qualificationLevelName = qualificationLevelName;
		this.qualificationLevelId = !Utils.isNullOrEmpty(qualificationLevelId) ? String.valueOf(qualificationLevelId): null;
		this.course = course;
		this.specialization = specialization;
		this.yearOfCompletion = new LookupItemDTO();
		this.yearOfCompletion.setLabel(String.valueOf(yearOfCompletion));
		this.gradeOrPercentage = gradeOrPercentage;
		this.institute = !Utils.isNullOrEmpty(erpInstitutionDBOName)?erpInstitutionDBOName:institute;
		this.boardOrUniversity = !Utils.isNullOrEmpty(erpUniversityBoardDBOName)?erpUniversityBoardDBOName:boardOrUniversity;
		this.country = new LookupItemDTO();
		this.country.setLabel(erpCountry);
		this.state = new LookupItemDTO();
		this.state.setLabel(!Utils.isNullOrEmpty(erpStateDBOName)?erpStateDBOName:stateOthers);
		this.currentStatus = currentStatus;
	}
	
}
