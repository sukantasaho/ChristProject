package com.christ.erp.services.dto.employee.recruitment;

import com.christ.erp.services.dto.common.CommonDTO;
import com.christ.erp.services.dto.common.LookupItemDTO;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class BasicInformationDTO  {
	public String employeeId;
	public String empNo;
	public String personalDataId;
	public String addtnlPersonalDataId;
	public String employeeName;
	public String adharNo;
	public CommonDTO nationality;
	public LookupItemDTO gender;
	public LookupItemDTO maritalStatus;
	public String dateOfBirth;
	public LookupItemDTO bloodGroup;
	public CommonDTO religion;
	public String panNo;
	public String universityEmailId;
	public String personalEmailId;
	public LookupItemDTO reservationCategory;
	public String isDifferentiallyAbled;
	public boolean isMinority;
	public LookupItemDTO disabilityType;
	public String mobileNo;
	public CommonDTO countryCode;
	public String fourWheelerNo;
	public String tworWheelerNo;
	public String fourWheelerDocument;
	public String fourWheelerDocumentUrl;
	public String tworWheelerDocument;
	public String tworWheelerDocumentUrl;
	public String smartCardNo;
	public String bankAccountNo;
	public String ifscCode;
	public String employeePhoto;
	private String adharUploadUrl;
	private String panUploadUrl;
	public String orcidNo;
	private String vidwnNo;
	private String scopusNo;
	
}
