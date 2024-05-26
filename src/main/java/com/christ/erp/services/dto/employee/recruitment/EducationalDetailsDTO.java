package com.christ.erp.services.dto.employee.recruitment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.christ.erp.services.dbobjects.employee.recruitment.EmpEligiblityTestDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import com.christ.erp.services.dto.student.common.StudentEducationalDetailsDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EducationalDetailsDTO {

	public String highestQualificationLevelId;
	public String highestQualificationAlbum;
	public LookupItemDTO highestQualification;
	public List<EmpApplnEducationalDetailsDTO> qualificationLevelsList;
	public List<EmpApplnEligibilityTestDTO> eligibilityTestList; 
	public List<EmpApplnEducationalDetailsDTO> otherQualificationLevelsList;
	public Map<String,List<EmpEducationalDetailsDTO>> empEducationalDetailsMap=new HashMap<>();
	public List<EmpEligiblityTestDTO> eligibilityTestDetails;

	public List<EmpEducationalDetailsDTO> empEducationalDetailsDTOS;
	private List<StudentEducationalDetailsDTO> studentEducationalDetailsDTOList;
}
