package com.christ.erp.services.dto.employee.recruitment;

import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmpPersonalDataDTO {
	
	private int id;
	private SelectDTO erpCountry;
	private SelectDTO erpMaritalStatus;
	private SelectDTO erpReligion;
	private SelectDTO erpReservationCategory;
	private SelectDTO erpBloodGroup;
	private Boolean isDifferentlyAbled;
	private SelectDTO erpDifferentlyAbled;
	private String currentAddressLine1;
	private String currentAddressLine2;
	private SelectDTO currentCountry;
	private SelectDTO currentState;
	private String currentStateOthers;
	private SelectDTO currentCity;
	private String currentCityOthers;
	private String currentPincode;
	private String permanentAddressLine1;
	private String permanentAddressLine2;
	private SelectDTO permanentCountry;
	private SelectDTO permanentState;
	private String permanentStateOthers;
	private SelectDTO permanentCity;
	private String permanentCityOthers;
	private String permanentPincode;
	private SelectDTO erpQualificationLevel;
	private String  highestQualificationAlbum;
	private String orcidNo;
	private String scopusNo;
	
	public EmpPersonalDataDTO(int id ,String countryName,String maritalStatusName,String religionName,String reservationCategoryName,String bloodGroupName,Boolean isDifferentlyAbled
							  ,String differentlyAbledName,String currentAddressLine1, String currentAddressLine2,String currentCountryName,String stateName,String currentStateOthers
							  ,String cityName,String currentCityOthers,String currentPincode,String permanentAddressLine1,String permanentAddressLine2,String permanentCountryName
							  ,String permanentStateName,String permanentStateOthers,String permanentCityName,String permanentCityOthers,String permanentPincode
							  ,String qualificationLevelName,String  highestQualificationAlbum,String orcidNo,String scopusNo) {
		
		this.id = id;
		this.erpCountry = new SelectDTO();
		this.erpCountry.setLabel(countryName);
		this.erpMaritalStatus = new SelectDTO();
		this.erpMaritalStatus.setLabel(maritalStatusName);
		this.erpReligion = new SelectDTO();
		this.erpReligion.setLabel(religionName);
		this.erpReservationCategory = new SelectDTO();
		this.erpReservationCategory.setLabel(reservationCategoryName);
		this.erpBloodGroup = new SelectDTO();
		this.erpBloodGroup.setLabel(bloodGroupName);
		this.isDifferentlyAbled = isDifferentlyAbled;
		this.erpDifferentlyAbled = new SelectDTO();
		this.erpDifferentlyAbled.setLabel(differentlyAbledName);
		this.currentAddressLine1 = currentAddressLine1;
		this.currentAddressLine2 = currentAddressLine2;
		this.currentCountry = new SelectDTO();
		this.currentCountry.setLabel(currentCountryName);
		this.currentState = new SelectDTO();
		this.currentState.setLabel(stateName);
		this.currentStateOthers = currentStateOthers;
		this.currentCity = new SelectDTO();
		this.currentCity.setLabel(cityName);
		this.currentCityOthers = currentCityOthers;
		this.currentPincode = currentPincode;
		this.permanentAddressLine1 =  permanentAddressLine1;
		this.permanentAddressLine2 =  permanentAddressLine2;
		this.permanentCountry = new SelectDTO();
		this.permanentCountry.setLabel(permanentCountryName);
		this.permanentState = new SelectDTO();
		this.permanentState.setLabel(permanentStateOthers);
		this.permanentStateOthers = permanentStateOthers;
		this.permanentCity = new SelectDTO();
		this.permanentCity.setLabel(permanentStateName);
		this.permanentCityOthers = permanentCityOthers;
		this.permanentPincode = permanentPincode;
		this.erpQualificationLevel = new SelectDTO();
		this.erpQualificationLevel.setLabel(qualificationLevelName);
		this.highestQualificationAlbum = highestQualificationAlbum;
		this.orcidNo = orcidNo;
		this.scopusNo = scopusNo;				
		
	}

}
