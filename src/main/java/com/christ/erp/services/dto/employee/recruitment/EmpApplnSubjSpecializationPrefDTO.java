package com.christ.erp.services.dto.employee.recruitment;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class EmpApplnSubjSpecializationPrefDTO {
	
	private int id;
	private int empApplnEntriesId;
	private SelectDTO empApplnSubjectCategory;
	private SelectDTO empApplnSubjectCategorySpecialization;
	
	public EmpApplnSubjSpecializationPrefDTO(Integer empApplnSubjSpecializationPrefId, Integer empApplnEntriesId,Integer subjectCategoryId, String subjectCategory,
			Integer empApplnSubjectCategorySpecializationId, String subjectCategorySpecializationName) {
		this.id = empApplnSubjSpecializationPrefId;
		this.empApplnEntriesId = empApplnEntriesId;
		if(!Utils.isNullOrEmpty(subjectCategoryId)) {
			this.setEmpApplnSubjectCategory(new SelectDTO());
			this.getEmpApplnSubjectCategory().setValue(String.valueOf(subjectCategoryId));
			this.getEmpApplnSubjectCategory().setLabel(subjectCategory);
		}
		if(!Utils.isNullOrEmpty(empApplnSubjectCategorySpecializationId)) {
			this.setEmpApplnSubjectCategorySpecialization(new SelectDTO());
			this.getEmpApplnSubjectCategorySpecialization().setValue(String.valueOf(empApplnSubjectCategorySpecializationId));
			this.getEmpApplnSubjectCategorySpecialization().setLabel(subjectCategorySpecializationName);
		}
	}

}
