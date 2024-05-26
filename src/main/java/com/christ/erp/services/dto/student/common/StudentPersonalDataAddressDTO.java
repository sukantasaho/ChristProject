package com.christ.erp.services.dto.student.common;

import java.math.BigDecimal;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudentPersonalDataAddressDTO extends ModelBaseDTO {
	
    private int studentPersonalDataAddressId;
    private String currentAddressLine1;
    private String currentAddressLine2;
    private SelectDTO currentCountry;
    private SelectDTO currentState;
    private String currentStateOthers;
    private SelectDTO currentCity;
    private String currentCityOthers;
    private String currentPincode;
    private Boolean isPermanentEqualsCurrent;
    private String permanentAddressLine1;
    private String permanentAddressLine2;
    private SelectDTO permanentCountry;
    private SelectDTO permanentState;
    private String permanentStateOthers;
    private SelectDTO permanentCity;
    private String permanentCityOthers;
    private String permanentPincode;
    
    private SelectDTO fatherSalutation;
    private String fatherName;
    private SelectDTO fatherErpQualificationLevel;
    private SelectDTO fatherOccupation;
//    private String fatherYearlyIncomeRange;
    public BigDecimal fatherYearlyIncomeRangeFrom;
    public BigDecimal fatherYearlyIncomeRangeTo;
    private SelectDTO fatherIncomeCurrency;
    private String fatherEmail;
    private String fatherMobileNoCountryCode;
    private String fatherMobileNo;
    private SelectDTO motherSalutation;
    private String motherName;
    private SelectDTO motherErpQualificationLevel;
    private SelectDTO motherOccupation;
//    private String motherYearlyIncomeRange;
    private BigDecimal motherYearlyIncomeRangeFrom;
    private BigDecimal motherYearlyIncomeRangeTo;
    public SelectDTO motherIncomeCurrency;
    public String motherEmail;
    public String motherMobileNoCountryCode;
    public String motherMobileNo;
    public SelectDTO guardianSalutation;
    public String guardianName;
    public String guardianEmail;
    public String guardianMobileNoCountryCode;
    public String guardianMobileNo;
    public String guardianAddressLine1;
    public String guardianAddressLine2;
    private SelectDTO guardianCountry;
    private SelectDTO guardianState;
    private String guardianStateOthers;
    private SelectDTO guardianCity;
    private String guardianCityOthers;
    private String guardianPincode;
    private SelectDTO birthCountry;
    public String birthPincode;
    private SelectDTO birthState;
    private String birthStateOthers;
    private SelectDTO birthCity;
    private String birthCityOthers;
	private BigDecimal familyAnnualIncome;
	private SelectDTO familyAnnualIncomeCurrency;
    
    public StudentPersonalDataAddressDTO(Integer id,String  currentAddressLine1,String currentAddressLine2,String cityName, String currentCityOthers,String stateName,String currentStateOthers,String countryName
    		,String currentPincode, String  permanentAddressLine1,String permanentAddressLine2,String permanentCityName,String permanentCityOthers,String permanentStateName,String permanentStateOthers
    		,String permanentCountryName,String permanentPincode,String  fatherErpSalutationName,String fatherName,String fatherEmail,String fatherMobileNoCountryCode,String fatherMobileNo,String fatherQualificationLevelName
    		,String fatherOccupationName,BigDecimal fatherYearlyIncomeRangeFrom
    		,BigDecimal fatherYearlyIncomeRangeTo,String fatherCurrencyName,String  motherErpSalutationName,String motherName,String motherEmail,String motherMobileNoCountryCode,String motherMobileNo
    		,String motherQualificationLevelName,String motherOccupationName,BigDecimal motherYearlyIncomeRangeFrom,BigDecimal motherYearlyIncomeRangeTo,String motherCurrencyName
    		,String  guardianErpSalutationName,String  guardianName,String guardianEmail,String guardianMobileNoCountryCode,String guardianMobileNo, BigDecimal familyAnnualIncome,String familyAnnualIncomeCurrency
    		) {
    	
    	this.studentPersonalDataAddressId = id;
    	this.currentAddressLine1 = currentAddressLine1;
    	this.currentAddressLine2 = currentAddressLine2;
		if(!Utils.isNullOrEmpty(cityName)){
			this.setCurrentCity(new SelectDTO());
			this.getCurrentCity().setLabel(cityName);
		}
    	this.currentCityOthers = currentCityOthers;
		if(!Utils.isNullOrEmpty(stateName)){
			this.setCurrentState(new SelectDTO());
			this.getCurrentState().setLabel(stateName);
		}
    	this.currentStateOthers = currentStateOthers;
		if(!Utils.isNullOrEmpty(countryName)){
			this.setCurrentCountry(new SelectDTO());
			this.getCurrentCountry().setLabel(countryName);
		}
    	this.currentPincode = currentPincode;
    	this.permanentAddressLine1 = permanentAddressLine1;
    	this.permanentAddressLine2 = permanentAddressLine2;
		if(!Utils.isNullOrEmpty(permanentCityName)){
			this.setPermanentCity(new SelectDTO());
			this.getPermanentCity().setLabel(permanentCityName);
		}
    	this.setPermanentCityOthers(permanentCityOthers);
		if(!Utils.isNullOrEmpty(permanentStateName)){
			this.setPermanentState(new SelectDTO());
			this.getPermanentState().setLabel(permanentStateName);
		}
    	this.setPermanentStateOthers(permanentStateOthers);
		if(!Utils.isNullOrEmpty(permanentCountryName)){
			this.setPermanentCountry(new SelectDTO());
			this.getPermanentCountry().setLabel(permanentCountryName);
		}
    	this.setPermanentPincode(permanentPincode);
		if(!Utils.isNullOrEmpty(fatherErpSalutationName)){
			this.setFatherSalutation(new SelectDTO());
			this.getFatherSalutation().setLabel(fatherErpSalutationName);
		}
    	this.fatherName = fatherName;
    	this.fatherEmail = fatherEmail;
    	this.fatherMobileNoCountryCode = fatherMobileNoCountryCode;
    	this.fatherMobileNo = fatherMobileNo;
		if(!Utils.isNullOrEmpty(fatherQualificationLevelName)){
			this.setFatherErpQualificationLevel(new SelectDTO());
			this.getFatherErpQualificationLevel().setLabel(fatherQualificationLevelName);
		}
		if(!Utils.isNullOrEmpty(fatherOccupationName)){
			this.setFatherOccupation(new SelectDTO());
			this.getFatherOccupation().setLabel(fatherOccupationName);
		}
    	this.setFatherYearlyIncomeRangeFrom(fatherYearlyIncomeRangeFrom);
    	this.setFatherYearlyIncomeRangeTo(fatherYearlyIncomeRangeTo);
		if(!Utils.isNullOrEmpty(fatherCurrencyName)){
			this.setFatherIncomeCurrency(new SelectDTO());
			this.getFatherIncomeCurrency().setLabel(fatherCurrencyName);
		}
		if(!Utils.isNullOrEmpty(motherErpSalutationName)){
			this.setMotherSalutation(new SelectDTO());
			this.getMotherSalutation().setLabel(motherErpSalutationName);
		}
    	this.setMotherName(motherName);
    	this.setMotherEmail(motherEmail);
    	this.setFatherMobileNoCountryCode(motherMobileNoCountryCode);
    	this.setMotherMobileNo(motherMobileNo);
		if(!Utils.isNullOrEmpty(motherQualificationLevelName)){
			this.setMotherErpQualificationLevel(new SelectDTO());
			this.getMotherErpQualificationLevel().setLabel(motherQualificationLevelName);
		}
    	if(!Utils.isNullOrEmpty(motherOccupationName)){
			this.setMotherOccupation(new SelectDTO());
			this.getMotherOccupation().setLabel(motherOccupationName);
		}
    	this.setMotherYearlyIncomeRangeFrom(motherYearlyIncomeRangeFrom);
    	this.setMotherYearlyIncomeRangeTo(motherYearlyIncomeRangeTo);
		if(!Utils.isNullOrEmpty(motherCurrencyName)){
			this.setMotherIncomeCurrency(new SelectDTO());
			this.getMotherIncomeCurrency().setLabel(motherCurrencyName);
		}
		if(!Utils.isNullOrEmpty(guardianErpSalutationName)){
			this.setGuardianSalutation(new SelectDTO());
			this.getGuardianSalutation().setLabel(guardianErpSalutationName);
		}
    	this.setGuardianName(guardianName);
    	this.setGuardianEmail(guardianEmail);
    	this.setGuardianMobileNoCountryCode(guardianMobileNoCountryCode);
    	this.setGuardianMobileNo(guardianMobileNo);
		this.setFamilyAnnualIncome(familyAnnualIncome);
		if(!Utils.isNullOrEmpty(familyAnnualIncomeCurrency)){
			this.setFamilyAnnualIncomeCurrency(new SelectDTO());
			this.getFamilyAnnualIncomeCurrency().setLabel(familyAnnualIncomeCurrency);
		}

    }

}
