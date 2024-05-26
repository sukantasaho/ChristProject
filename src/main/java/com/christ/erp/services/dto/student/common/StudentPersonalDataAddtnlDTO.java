package com.christ.erp.services.dto.student.common;

import java.time.LocalDate;
import java.util.List;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.common.ErpCityDBO;
import com.christ.erp.services.dbobjects.common.ErpCountryDBO;
import com.christ.erp.services.dbobjects.common.ErpStateDBO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Setter
@NoArgsConstructor
public class StudentPersonalDataAddtnlDTO extends ModelBaseDTO {
	
	private int id;
	private String profilePhotoUrl;
	private SelectDTO erpCountry;
	private String passportNo;
	private LocalDate passportDateOfExpiry;
	private SelectDTO erpBloodGroup;
	private SelectDTO erpMotherTounge;
	private SelectDTO erpReligionId;
	private SelectDTO erpReservationCategory;
	private Boolean differentlyAbled;
	private SelectDTO erpDifferentlyAbled;
	private Boolean aadharNoShared;
	private String aadharCardNo;
	private Boolean aadharEnrolled;
	private String aadharEnrolmentNumber;
	private SelectDTO erpInstitutionReference;
	private SelectDTO erpSecondLanguage;
	private String researchTopicDetails;
	private String pioOrOci;
	private String pioOrOciCardNo;
	private String pioOrOciBirthPlace;
	private String passportIssuedDate;
	private String passportStatus;
	private String passportComments;
	private SelectDTO passportIssuedCountry;
    private List<StudentExtraCurricularDetailsDTO> sportsDetailsDTOS;
    private List<StudentExtraCurricularDetailsDTO> extraCurricularDetailsDTOS;
    private Boolean appliedUnderOverseasNri;
	private String placeOfBirth;
	private SelectDTO birthState;
	private String birthStateOthers;
	private SelectDTO birthCity;
	private String birthCityOthers;
	private String birthPincode;
	private SelectDTO birthCountry;
	private String sponsershipName;
	private String sponsershipEmail;
	private String sponsershipPhoneNumber;
	private Integer  sponsershipNoCountryCode;
	private SelectDTO sponsershipCountry;
    
    
    public StudentPersonalDataAddtnlDTO(int id ,String countryName,String bloodGroupName,String religionName,String reservationCategoryName,String motherToungeName,Boolean  isDifferentlyAbled
    		,String differentlyAbledName,String  aadharCardNo,String  aadharEnrolmentNumber,String  pioOrOci,String  pioOrOciCardNo,String  placeOfBirth,String  passportNo,LocalDate  passportIssuedDate
    		,LocalDate  passportDateOfExpiry,String passportIssuedcountryName,String  secondLanguageName,String  researchTopicDetails,String  institutionReferenceName,String stateName,String birthStateOthers,String cityName
			,String	birthCityOthers,String birthPincode,String birthCountryName,String sponsershipName,String sponsershipEmail,String sponsershipPhoneNumber
			,Integer sponsershipNoCountryCode,String sponsershipCountryName) {
    	
    	this.id = id;
		if(!Utils.isNullOrEmpty(countryName)){
			this.setErpCountry(new SelectDTO());
			this.getErpCountry().setLabel(countryName);
		}
		if(!Utils.isNullOrEmpty(bloodGroupName)){
			this.setErpBloodGroup(new SelectDTO());
			this.getErpBloodGroup().setLabel(bloodGroupName);
		}
		if(!Utils.isNullOrEmpty(religionName)){
			this.setErpReligionId(new SelectDTO());
			this.getErpReligionId().setLabel(religionName);
		}
		if(!Utils.isNullOrEmpty(reservationCategoryName)){
			this.setErpReservationCategory(new SelectDTO());
			this.getErpReservationCategory().setLabel(reservationCategoryName);
		}
		if(!Utils.isNullOrEmpty(motherToungeName)){
			this.setErpMotherTounge(new SelectDTO());
			this.getErpMotherTounge().setLabel(motherToungeName);
		}
    	this.differentlyAbled = isDifferentlyAbled;
		if(!Utils.isNullOrEmpty(differentlyAbledName)){
			this.setErpDifferentlyAbled(new SelectDTO());
			this.getErpDifferentlyAbled().setLabel(differentlyAbledName);
		}
    	this.aadharCardNo = aadharCardNo;
    	this.aadharEnrolmentNumber = aadharEnrolmentNumber;
    	this.pioOrOci = pioOrOci;
    	this.pioOrOciCardNo = pioOrOciCardNo;
//    	this.pioOrOciBirthPlace = pioOrOciBirthPlace;
		this.placeOfBirth = placeOfBirth;
    	this.passportNo = passportNo;
    	if(!Utils.isNullOrEmpty(passportIssuedDate)) {
    		this.passportIssuedDate = Utils.convertLocalDateToStringDate(passportIssuedDate);
    	}
    	if(!Utils.isNullOrEmpty(passportDateOfExpiry)) {
    		this.passportDateOfExpiry = passportDateOfExpiry;
    	}
		if(!Utils.isNullOrEmpty(passportIssuedcountryName)){
			this.setPassportIssuedCountry(new SelectDTO());
			this.getPassportIssuedCountry().setLabel(passportIssuedcountryName);
		}
		if(!Utils.isNullOrEmpty(secondLanguageName)){
			this.setErpSecondLanguage(new SelectDTO());
			this.getErpSecondLanguage().setLabel(secondLanguageName);
		}
    	this.researchTopicDetails = researchTopicDetails;
		if(!Utils.isNullOrEmpty(institutionReferenceName)){
			this.setErpInstitutionReference(new SelectDTO());
			this.getErpInstitutionReference().setLabel(institutionReferenceName);
		}
		if(!Utils.isNullOrEmpty(stateName)){
			this.setBirthState(new SelectDTO());
			this.getBirthState().setLabel(stateName);
		}
		this.setBirthStateOthers(birthStateOthers);
		if(!Utils.isNullOrEmpty(cityName)){
			this.setBirthCity(new SelectDTO());
			this.getBirthCity().setLabel(cityName);
		}
		this.setBirthCityOthers(birthCityOthers);
		this.setBirthPincode(birthPincode);
		if(!Utils.isNullOrEmpty(birthCountryName)){
			this.setBirthCountry(new SelectDTO());
			this.getBirthCountry().setLabel(birthCountryName);
		}
		this.setSponsershipName(sponsershipName);
		this.setSponsershipEmail(sponsershipEmail);
		this.setSponsershipPhoneNumber(sponsershipPhoneNumber);
		this.setSponsershipNoCountryCode(sponsershipNoCountryCode);
		if(!Utils.isNullOrEmpty(sponsershipCountryName)){
			this.setSponsershipCountry(new SelectDTO());
			this.getSponsershipCountry().setLabel(sponsershipCountryName);
		}
    }

}
