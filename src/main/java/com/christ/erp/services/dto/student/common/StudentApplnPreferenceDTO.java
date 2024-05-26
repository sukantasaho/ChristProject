package com.christ.erp.services.dto.student.common;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudentApplnPreferenceDTO {
	
	private int id;
	private Integer studentApplnEntriesId;
	private Integer preferenceOrder;
	private SelectDTO programePreference;
	private String locationName;
	private String campusName;
	   
	public StudentApplnPreferenceDTO(int id,Integer studentApplnEntriesId,Integer preferenceOrder,String programmeName,String locationName,String campusName) {
		this.id = id;
		this.setStudentApplnEntriesId(studentApplnEntriesId);
		this.setPreferenceOrder(preferenceOrder);
		if(!Utils.isNullOrEmpty(programmeName)){
			this.setProgramePreference(new SelectDTO());
			this.getProgramePreference().setLabel(programmeName);
		}
		if(!Utils.isNullOrEmpty(locationName)){
			this.locationName = locationName;
		}
		if(!Utils.isNullOrEmpty(campusName)){
			this.campusName = campusName;
		}
	
	}

}
