package com.christ.erp.services.dto.employee.recruitment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResearchDetailsDTO {

	public String isResearchExperience;
	public String inflibnetVidwanNo;
	public String scopusId;
	public String hIndex;
	public EmpApplnAddtnlInfoEntriesDTO researchEntries;
	public String isInterviewedBefore;
	public String interviewedBeforeDepartment;
	public String interviewedBeforeYear;
	public String interviewedBeforeApplicationNo;
	public String interviewedBeforeSubject;
	public String vacancyInformationId;
	public String aboutVacancyOthers;
	public String otherInformation;
	public String orcidId;
}
