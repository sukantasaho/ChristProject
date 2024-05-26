package com.christ.erp.services.dto.student.common;

import java.time.LocalDate;

import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentBasicProfileDTO {
	
	private String candidateName;
	private LocalDate dob;
	private SelectDTO gender;
	private String mobileNoCountryCode;
	private String mobileNo;
	private String emailId;
	private SelectDTO programmePreference1;
	private SelectDTO location1;
	private String specialization1;
	private SelectDTO programmePreference2;
	private SelectDTO location2;
	private String specialization2;
	private SelectDTO programmePreference3;
	private SelectDTO location3;
	private String specialization3;
	
}
