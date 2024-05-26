package com.christ.erp.services.dto.curriculum.settings;


import java.time.LocalDate;

import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.student.common.StudentDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExternalsDTO {

	private int id;
	private String externalName;
	private String contactNo;
	private String emailId;
	private LocalDate dob;
	private StudentDTO student;
	private Integer admittedYear;
	private SelectDTO department;
	private SelectDTO externalsCategory; 
	private ExternalsAdditionalDetailsDTO externalsAdditionalDetails;
}
