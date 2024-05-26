package com.christ.erp.services.dto.employee.recruitment;

import java.util.List;

import com.christ.erp.services.dto.employee.common.EmpMajorAchievementsDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProfessionalExperienceDTO {

	public String isCurrentlyWorking;
	public EmpApplnWorkExperienceDTO currentExperience;
	public List<EmpApplnWorkExperienceDTO> professionalExperienceList;
	public String totalPreviousExperienceYears;
	public String totalPreviousExperienceMonths; 
	public String totalPartTimePreviousExperienceYears;
	public String totalPartTimePreviousExperienceMonths; 
    public String recognisedExpYears;
    public String recognisedExpMonths;
    public String fullTimeYears;
    public String fullTimeMonths;
    public String partTimeYears;
    public String partTimeMonths;
	public String majorAchievements;
	public String expectedSalary; 
	public List<EmpWorkExperienceDTO> experienceInformation;
	public List<EmpMajorAchievementsDTO> majorAchievementsList;

}
