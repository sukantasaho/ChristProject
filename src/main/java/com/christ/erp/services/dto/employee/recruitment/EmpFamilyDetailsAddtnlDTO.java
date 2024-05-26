package com.christ.erp.services.dto.employee.recruitment;

import java.time.LocalDate;
import com.christ.erp.services.common.Utils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmpFamilyDetailsAddtnlDTO {
    public String empFamilyDetailsAddtnlId;
    public String empApplnPersonalDataId;
    public String empPersonalDataId;
    public String relationship;
    public String dependentName; 
    public String dependentDob;   
    public String dependentQualification; 
    public String dependentProfession;
    public String otherDependentRelationship;
    private LocalDate dependDOB; 
    
    public EmpFamilyDetailsAddtnlDTO(int empPersonalDataId,String relationship,String dependentName,LocalDate dependentDob,String dependentQualification,String dependentProfession) {
    	this.empApplnPersonalDataId = String.valueOf(empPersonalDataId);
    	if(!Utils.isNullOrEmpty(relationship)) {
    		this.relationship = relationship;
    	}
    	this.dependentName = dependentName;
    	this.dependentDob = !Utils.isNullOrEmpty(dependentDob)?Utils.convertLocalDateToStringDate(dependentDob):"";
    	this.dependentQualification = dependentQualification;
    	this.dependentProfession = dependentProfession;
    	
    }
     
}