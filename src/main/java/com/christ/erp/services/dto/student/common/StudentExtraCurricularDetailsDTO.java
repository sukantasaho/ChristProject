package com.christ.erp.services.dto.student.common;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudentExtraCurricularDetailsDTO {

    private int studentExtraCurricularDetailsId;
    private int studentApplnEntriesId;
    private SelectDTO erpExtraCurricular;
    private SelectDTO erpSports;
    private SelectDTO erpSportsLevel;
    
    public StudentExtraCurricularDetailsDTO(int id,Integer studentApplnEntriesId,Integer erpSportsId,String sportsLevelName) {
    	this.studentExtraCurricularDetailsId = id;
    	if(!Utils.isNullOrEmpty(studentApplnEntriesId)) {
    		this.studentApplnEntriesId =studentApplnEntriesId;
    	}
    	if(!Utils.isNullOrEmpty(erpSportsId)) {
        	this.setErpSports(new SelectDTO());
        	this.getErpSports().setValue(String.valueOf(erpSportsId));	
    	}
    	if(!Utils.isNullOrEmpty(sportsLevelName)) {
        	this.setErpSportsLevel(new SelectDTO());
        	this.getErpSportsLevel().setLabel(sportsLevelName);
    	}

    }
}
