package com.christ.erp.services.dto.employee.recruitment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.LookupItemDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmpApplnWorkExperienceDTO {

	public int empApplnWorkExperienceId;
	public int empApplnEntriesId;
	public String workExperienceTypeId;
	public String functionalAreaId;
	public String functionalAreaOthers;
	public String employmentType;
	public String designation;
//	public String fromDate;
//	public String toDate;
	public String years;
	public String months; 
	public String noticePeriod; 
	public String currentSalary; 
	public String institution; 
	public List<EmpApplnWorkExperienceDocumentDTO> experienceDocumentList;
	public LookupItemDTO functionalArea;
	private LocalDate fromDate;
	private LocalDate toDate;
	private Boolean isPartTime;
	private Boolean isCurrentExperience;
	
	public EmpApplnWorkExperienceDTO(int empApplnEntriesId,Integer workExperienceYears,Integer workExperienceMonth,Boolean isPartTime,String workExperienceTypeName,String subjectCategory,
			LocalDate workExperienceFromDate,LocalDate workExperienceToDate,String empDesignation,String institution,String noticePeriod,BigDecimal currentMonthlySalary,String functionalAreaOthers,
			Boolean isCurrentExperience) {
		this.empApplnEntriesId =empApplnEntriesId;
		this.years = String.valueOf(workExperienceYears);
		this.months =String.valueOf(workExperienceMonth);
		this.isPartTime = isPartTime;
		this.workExperienceTypeId = workExperienceTypeName;
		this.functionalAreaId = !Utils.isNullOrEmpty(subjectCategory)? subjectCategory:functionalAreaOthers;
		this.fromDate = workExperienceFromDate;
		this.toDate = workExperienceToDate;
		this.designation = empDesignation;
		this.institution = institution;
		this.noticePeriod = noticePeriod;
		this.currentSalary = String.valueOf(currentMonthlySalary);
		this.isCurrentExperience = isCurrentExperience;
		
	}
}
