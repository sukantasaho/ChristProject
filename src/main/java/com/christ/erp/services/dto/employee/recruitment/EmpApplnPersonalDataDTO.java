package com.christ.erp.services.dto.employee.recruitment;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class EmpApplnPersonalDataDTO {
	
	public int empApplnPersonalDataId;
	public int empApplnEntriesId;
	public String applicantName;
	public String genderId;
	public String fatherName;
	public String motherName;
	public String dateOfBirth;
	public String emailId;
	public String mobileNoCountryCode;
	public String mobileNo;
	public String alternateNo;
	public String aadharNo;
	public String maritalStatusId;
	public String nationalityId;
	public String passportNo;
	public String religionId;
	public String isMinority;
	public String reservationCategoryId;
	public String bloodGroupId;
	public String isDifferentlyAbled;
	public String differentlyAbledId;
	public String differentlyAbledDetails;
	public String currentAddressLine1;
	public String currentAddressLine2;
	public String currentCountryId;
	public String currentStateId;
	public String currentStateOthers;
	public String currentCityId;
	public String currentCityOthers;
	public String currentPincode;
	public String isPermanentEqualsCurrent;
	public String permanentAddressLine1;
	public String permanentAddressLine2;
	public String permanentCountryId;
	public String permanentStateId;
	public String permanentStateOthers;
	public String permanentCityId;
	public String permanentCityOthers;
	public String permanentPincode;
	public String profilePhotoUrl;
	public String isUanNo;
	public String uanNo;
	private SelectDTO highestQualificationLevel;
	
	private String originalFileName;
  	private String uniqueFileName;
  	public Boolean newFile;
  	private String processCode;
  	private String actualPath;
  	private String tempPath;
  	private String orcidNo;
  	private String vidwanNo;
  	private String scopusNo;
  	private Integer hIndexNo;
  	
  	
  	public EmpApplnPersonalDataDTO(Integer empApplnPersonalDataId,Integer empApplnEntriesId, String alternateNo,String bloodGroupName, Boolean isDifferentlyAbled,String differentlyAbledName,
  			String maritalStatusName,String religionName,String reservationCategoryName,String countryName,String aadharNo,String passportNo,String currentAddressLine1,String currentAddressLine2,String currentCountryName,
  			String stateName,String currentStateOthers,String cityName,String currentCityOthers,String currentPincode,String permanentAddressLine1,String permanentAddressLine2,String permanentCountryName,
  			String permanentStateName,String permanentStateOthers,String permanentCityName,String permanentCityOthers,String permanentPincode,String qualificationLevelName,
  			String orcidNo,String vidwanNo,String scopusNo,Integer hIndexNo) {
  		this.empApplnPersonalDataId = empApplnPersonalDataId;
  		this.empApplnEntriesId = empApplnEntriesId;
  		this.alternateNo = alternateNo;
  		this.bloodGroupId =  bloodGroupName;
  		if(!Utils.isNullOrEmpty(isDifferentlyAbled)) {
  			this.differentlyAbledDetails = isDifferentlyAbled? differentlyAbledName :"NO";
  		} else {
  	  		this.differentlyAbledDetails ="NO";
  		}
  		this.maritalStatusId = maritalStatusName;
  		this.religionId = religionName;
  		this.reservationCategoryId = reservationCategoryName;
  		this.nationalityId = countryName;
  		this.aadharNo = aadharNo;
  		this.passportNo = passportNo;
  		this.currentAddressLine1 = currentAddressLine1;
  		this.currentAddressLine2 = currentAddressLine2;
  		this.currentCountryId = currentCountryName;
  		this.currentStateId = !Utils.isNullOrEmpty(stateName)?stateName:currentStateOthers;
  		this.currentCityId = !Utils.isNullOrEmpty(cityName)? cityName:currentCityOthers;
  		this.currentPincode = currentPincode;
  		this.permanentAddressLine1 = permanentAddressLine1;
  		this.permanentAddressLine2 = permanentAddressLine2;
  		this.permanentCountryId = permanentCountryName;
  		this.permanentStateId = !Utils.isNullOrEmpty(permanentStateName)?permanentStateName:permanentStateOthers;
  		this.permanentCityId = !Utils.isNullOrEmpty(permanentCityName)? permanentCityName:permanentCityOthers;
  		this.permanentPincode = permanentPincode;
  		this.highestQualificationLevel = new SelectDTO();
  		this.highestQualificationLevel.setLabel(qualificationLevelName);
  		this.orcidNo = orcidNo;
  		this.vidwanNo = vidwanNo;
  		this.scopusNo = scopusNo;
  		this.hIndexNo = hIndexNo;
  		
  	}
  	
  	
}
