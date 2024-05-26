package com.christ.erp.services.dto.employee.report;

import java.time.LocalDate;
import java.util.List;

import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApplicantListDTO {
	private List<SelectDTO> selectedLocation;
	private List<SelectDTO> selectedCampus;
	private SelectDTO employeeCategory;
	private List<SelectDTO> selectedSubjectOrCategory;
	private List<SelectDTO> selectedSpecialization;
	private List<SelectDTO> department;
	private List<SelectDTO> designation;
	private LocalDate empFromDate;
	private LocalDate empToDate;
	private List<Integer> empIds;
	private String profilePhotoUrl;
	
	
	private Integer empApplnEntriesId;
	private String applicationNo;
	private String applicantName;
	private String gender;
	private String email;
	private String mobile;
	private String status;
	private String alternateNo;
	private String dob;
	private String bloodGroup;
	private String differentlyAbled;
	private String maritalStatus;
	private String religion;
	private String reservationCategory;
	private String nationality;
	private String aadharNo;
	private String passportNo;
	private String sourcingChannel;
	private String employeeCategoryName;
	private String subjectCategoryName;
	private String specialization;
	private String preferredLocation;
	private String currentAddress1;
	private String currentAddress2;
	private String currentAddressCountry;
	private String currentAddressState;
	private String currentAddressCity;
	private String currentAddressPinCode;
	private String permanentAddress1;
	private String permanentAddress2;
	private String permanentAddressCity;
	private String permanentAddressState;
	private String permanentAddressCountry;
	private String permanentAddressPinCode;
	private String highestQualificationLevel;
	private String qualificationDetails;
	private String eligibilityTest;
	private String currentlyWorking;
	private String workExperience;
	private String totalFulltimeExperience;
	private String totalParttimeExperience;
	private String majorAchievements;
	private String expectedSalarymonth;
	private String experienceInChrist;
	private String researchExperience;
	
}
