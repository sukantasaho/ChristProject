package com.christ.erp.services.dto.employee.recruitment;

import java.time.LocalDate;
import java.util.List;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.employee.recruitment.EmpWorkExperienceDocumentDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class EmpWorkExperienceDTO {
	public int empApplnWorkExperienceId;
	public int empApplnEntriesId;
	public LookupItemDTO workExperienceType;
	public LookupItemDTO functionalArea;
	public String functionalAreaOthers;
	public String employmentType;
	public String designation;
	public String fromDate;
	public String toDate;
	public String years;
	public String months; 
	public String noticePeriod; 
	public String currentSalary; 
	public String institution; 
	public String isRecognised;
	public List<EmpWorkExperienceDocumentDTO> experienceDocumentList;
	private int empId;
	private Boolean isPartTime;
	
	
	public EmpWorkExperienceDTO(int empId, Integer workExperienceYears, Integer workExperienceMonth, Boolean isPartTime, String workExperienceTypeName, String subjectCategory
			                    ,LocalDate workExperienceFromDate, LocalDate workExperienceToDate, String empDesignation, String institution, Boolean isRecognized)  {
		this.empId = empId;
		this.years = String.valueOf(workExperienceYears);
		this.months = String.valueOf(workExperienceMonth);
		this.isPartTime = isPartTime;
		this.workExperienceType = new LookupItemDTO();
		this.workExperienceType.setLabel(workExperienceTypeName);
		this.functionalArea = new LookupItemDTO();
		this.functionalArea.setLabel(subjectCategory);
		this.fromDate = !Utils.isNullOrEmpty(workExperienceFromDate)? Utils.convertLocalDateToStringDate(workExperienceFromDate):"";
		this.toDate = !Utils.isNullOrEmpty(workExperienceToDate)? Utils.convertLocalDateToStringDate(workExperienceToDate):"";
		this.designation = empDesignation;
		this.institution = institution;
		this.isRecognised = !Utils.isNullOrEmpty(isRecognized)? String.valueOf(isRecognized):"";
		
	}
	
}
