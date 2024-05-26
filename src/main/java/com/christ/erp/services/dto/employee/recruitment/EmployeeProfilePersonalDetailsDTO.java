package com.christ.erp.services.dto.employee.recruitment;

import java.util.HashMap;
import java.util.Map;

public class EmployeeProfilePersonalDetailsDTO {
	public BasicInformationDTO basicInformation;
	public EmployeeProfileAddressDTO currentAddress;
	public EmployeeProfileAddressDTO permanentAddress;
	public GovernmentDocumentDetailsDTO passportDetails;
	public Map<String,GovernmentDocumentDetailsDTO> visaAndFrroDetails;
	public Map<String,String> emergencyContact = new HashMap<>();
	public EmployeeProfileFamilyOrDependencyInformationDTO familyOrDependencyInformation;
}
