package com.christ.erp.services.dto.employee.recruitment;

import java.time.LocalDate;
import com.christ.erp.services.common.Utils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class EmpAddtnlPersonalDataDTO {

    public String empAddtnlPersonalDataId;
    public String empApplnPersonalDataId;
    public String empPersonalDataId;
    public String panNo;
    public String fourWheelerNo;
    public String twoWheelerNo;
    public String isAadharAvailable;
    public String aadharNo;
    public String isAadharEnrolled;
    public String aadharEnrolledNo;
    public String emergencyContactName;
    public String emergencyContactAddress;
    public String emergencyContactRelatonship;
    public String emergencyMobileNo;
    public String emergencyContactHome;
    public String emergencyContactWork;
    public String passportNo;
    public String passportIssuedDate;
    public String passportStatus;
    public String passportDateOfExpiry;
    public String passportComments;
    public String passportIssuedPlace;
    public String visaNo;
    public String visaIssuedDate;
    public String visaStatus;
    public String visaDateOfExpiry;
    public String visaComments;
    public String frroNo;
    public String frroIssuedDate;
    public String frroStatus;
    public String frroDateOfExpiry;
    public String frroComments;
    public String familyBackgroundBrief;
    public String visaUploadUrl;
    public String passportUploadUrl;
    public String frroUploadUrl;
    
    public EmpAddtnlPersonalDataDTO(int empPersonalDataDBOId, String panNo,String aadharNo,String emergencyContactName,String emergencyContactAddress,String emergencyContactRelationship,String emergencyMobileNo
						    		,String emergencyContactHome,String emergencyContactWork,String passportNo,LocalDate passportIssuedDate,String passportStatus,LocalDate passportDateOfExpiry,String passportIssuedPlace,String passportComments
						    		,String visaNo,LocalDate visaIssuedDate, String visaStatus,LocalDate visaDateOfExpiry,String visaComments,String frroNo,LocalDate frroIssuedDate,String frroStatus,LocalDate frroDateOfExpiry
						    		,String frroComments,String fourWheelerNo,String twoWheelerNo,String familyBackgroundBrief) {
		this.empPersonalDataId =  String.valueOf(empPersonalDataDBOId);
    	this.panNo = panNo;
    	this.aadharNo = aadharNo;
    	this.emergencyContactName = emergencyContactName;
    	this.emergencyContactAddress = emergencyContactAddress;
    	this.emergencyContactRelatonship = emergencyContactRelationship;
    	this.emergencyMobileNo = emergencyMobileNo;
    	this.emergencyContactHome = emergencyContactHome;
    	this.emergencyContactWork = emergencyContactWork;
    	this.passportNo = passportNo;
    	this.passportIssuedDate = !Utils.isNullOrEmpty(passportIssuedDate)?Utils.convertLocalDateToStringDate(passportIssuedDate):"";
    	this.passportStatus = passportStatus;
    	this.passportDateOfExpiry = !Utils.isNullOrEmpty(passportDateOfExpiry)?Utils.convertLocalDateToStringDate(passportDateOfExpiry):"";
    	this.passportIssuedPlace = passportIssuedPlace;
    	this.passportComments = passportComments;
    	this.visaNo = visaNo;
    	this.visaIssuedDate = !Utils.isNullOrEmpty(visaIssuedDate)?Utils.convertLocalDateToStringDate(visaIssuedDate):"";
    	this.visaStatus = visaStatus;
    	this.visaDateOfExpiry = !Utils.isNullOrEmpty(visaDateOfExpiry)?Utils.convertLocalDateToStringDate(visaDateOfExpiry):"";
    	this.visaComments = visaComments;
    	this.frroNo = frroNo;
    	this.frroIssuedDate =  !Utils.isNullOrEmpty(frroIssuedDate)?Utils.convertLocalDateToStringDate(frroIssuedDate):"";
    	this.frroStatus = frroStatus;
    	this.frroDateOfExpiry = !Utils.isNullOrEmpty(frroDateOfExpiry)?Utils.convertLocalDateToStringDate(frroDateOfExpiry):"";
    	this.frroComments = frroComments;
    	this.fourWheelerNo = fourWheelerNo;
    	this.twoWheelerNo = twoWheelerNo;
    	this.familyBackgroundBrief = familyBackgroundBrief;

    }
}