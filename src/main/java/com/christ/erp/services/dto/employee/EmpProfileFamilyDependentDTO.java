package com.christ.erp.services.dto.employee;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpProfileFamilyDependentDTO {
	private Integer empFamilyDetailsAddtnlId;
	private Integer empPersonalDataId;
	private String relationship;
	private String dependentName;
	private LocalDate dependentDob;
	private String dependentQualification;
	private String dependentProfession;
	private String otherDependentRelationship;
	
	public EmpProfileFamilyDependentDTO() {
	}
	public EmpProfileFamilyDependentDTO(Integer empFamilyDetailsAddtnlId, Integer empPersonalDataId, String relationship, String dependentName, 
			LocalDate dependentDob, String dependentQualification, String dependentProfession, String otherDependentRelationship) {
		this.empFamilyDetailsAddtnlId = empFamilyDetailsAddtnlId;
		this.empPersonalDataId = empPersonalDataId;
		this.relationship = relationship;
		this.dependentName = dependentName;
		this.dependentDob =  dependentDob;
		this.dependentQualification = dependentQualification;
		this.dependentProfession = dependentProfession;
		this.otherDependentRelationship = otherDependentRelationship;
		
	}
			
}
