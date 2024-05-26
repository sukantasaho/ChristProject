package com.christ.erp.services.dbobjects.employee.recruitment;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.LookupItemDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class EmpEligiblityTestDTO {
	public int empEligibilityTestId;
	public int empApplnEntriesId;
	public LookupItemDTO eligibilityTest;
	public String testYear;
	public String eligibilityExamName;
	private int empId;
	
	
	public EmpEligiblityTestDTO(int empId, String eligibilityExamName, Integer testYear) {
		this.empId = empId;
		this.eligibilityExamName = eligibilityExamName;
		this.testYear = !Utils.isNullOrEmpty(testYear)? String.valueOf(testYear):"";
	}
}
