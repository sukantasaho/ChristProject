package com.christ.erp.services.dto.student.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.admission.applicationprocess.AdmSelectionProcessPlanCenterBasedDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmSelectionProcessPlanDetailAllotmentDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmSelectionProcessPlanDetailDTO;
import com.christ.erp.services.dto.admission.settings.AdmSelectionProcessCenterDetailsDTO;
import com.christ.erp.services.dto.admission.settings.AdmSelectionProcessVenueCityDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class StudentApplnSelectionProcessDatesDTO {
	
    private int studentApplnSelectionProcessDatesId;
    private LocalDateTime selectionProcessDate;
    private StudentApplnEntriesDTO studentApplnEntries;
    private AdmSelectionProcessPlanDetailDTO admSelectionProcessPlanDetail;
    private AdmSelectionProcessPlanCenterBasedDTO admSelectionProcessPlanCenterBased;
    private AdmSelectionProcessCenterDetailsDTO admSelectionProcessCenterDetails;
    private AdmSelectionProcessPlanDetailAllotmentDTO admSelectionProcessPlanDetailAllotment;
    private AdmSelectionProcessVenueCityDTO admSelectionProcessVenueCity;
    private SelectDTO state;
    private Boolean conductedInIndia;
    private Boolean conductedOutsideIndia;
    private Integer processOrder;
    
    private String selectionStageName;
    private LocalDate selectionProcessDate1;
    private LocalTime selectionProcessTime;
    private String venueName;
    private Integer studentApplnEntriesId;
    
    
    public StudentApplnSelectionProcessDatesDTO(int id,Integer studentApplnEntriesId,String selectionStageName,LocalDate selectionProcessDate,LocalTime selectionProcessTime,String venueName,Integer processOrder
            ,String selectionStageName1,LocalDate selectionProcessDate1,LocalTime selectionProcessTime1,String venueName1,Integer processOrder1
    ) {
    	this.studentApplnSelectionProcessDatesId = id;
    	this.studentApplnEntriesId = studentApplnEntriesId;
        if(!Utils.isNullOrEmpty(selectionStageName)){
            this.selectionStageName = selectionStageName;
        } else {
            this.selectionStageName = selectionStageName1;
        }
    	if(!Utils.isNullOrEmpty(selectionProcessDate)) {
    		this.selectionProcessDate1 = selectionProcessDate;
    	} else {
            this.selectionProcessDate1 = selectionProcessDate1;
        }
    	if(!Utils.isNullOrEmpty(selectionProcessTime)) {
    		this.selectionProcessTime = selectionProcessTime;
    	} else {
            this.selectionProcessTime = selectionProcessTime1;
        }
        if(!Utils.isNullOrEmpty(venueName)){
            this.venueName = venueName;
        } else {
            this.venueName = venueName1;
        }
    	if(!Utils.isNullOrEmpty(processOrder)){
            this.processOrder = processOrder;
        } else {
            this.processOrder = processOrder1;
        }

    }

}
