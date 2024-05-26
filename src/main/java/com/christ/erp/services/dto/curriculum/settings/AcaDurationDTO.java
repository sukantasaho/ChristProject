package com.christ.erp.services.dto.curriculum.settings;

import java.time.LocalDate;

import com.christ.erp.services.dto.common.ErpAcademicYearDTO;
import com.christ.erp.services.dto.curriculum.common.AcaSessionGroupDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcaDurationDTO {

	private int id;	
	private ErpAcademicYearDTO erpAcademicYearDTO;
	private AcaSessionGroupDTO acaSessionGroupDTO;
	private String durationName;
	private Boolean isCurrentSession;
	private LocalDate academicDurationStartDate;
	private LocalDate academicDurationEndDate;
	private LocalDate openElectiveStartDate;
	private LocalDate openElectiveEndDate;
	private LocalDate openElectiveRegistrationStartDate;
	private LocalDate openElectiveRegistrationEndDate;
	private LocalDate coursePlanStartDate;
	private LocalDate coursePlanEndDate;
}
