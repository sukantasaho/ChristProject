package com.christ.erp.services.dto.employee.recruitment;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.christ.erp.services.common.Utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmpJobDetailsDTO {

    public String empJobDetailsId;
    public String empApplnEntriesId;
    public String empId;
    public String applicationNo;
    public String isWithPf;
    public String isWithGratuity;
    public String isEsiApplicable;
    public String isUanNoAvailable;
    public String pfAccountNo;
    public LocalDate pfDate;
    public String gratuityNo;
    public LocalDate gratuityDate;
    public String uanNo;
    public String isSibAccountAvailable;
    public String sibAccountBank;
    public String ifscCode;
    public String empPayScaleDetailsId;
//    public String vcComments;
    public String reportingDate;
 //   public String joiningDate;
    public String isOfferAccepted;
    public String isWaitForDocuments;
    public String documentsRemarks;
    public String documentsSubmissionDueDate;
    public String isDisplayWebsite;
//  public String memoDate;
//  public String memoDetails;
//  public String memoUploadUrl;
//  public String memoRefNo;
    public String isBiometricConfigurationCompleted;
    public String isVacationApplicable;
    public String isHolidayWorking;
    public String isDutyRosterApplicable;
    public String isHolidayTimeZoneApplicable;
    public String holidayTimeZoneId;
    public String isVacationTimeZoneApplicable;
    public String vacationTimeZoneId;
    public String smartCardNo;
    public String empLeaveCategoryAllotmentId;
    public String retirementDate;
    public String appointmentLetterDate;
    public String appointmentLetterRfNo;
    public String appointmentLetterUrl;
    public String appointmentLetterExtendedDate;
    public String appointmentLetterExtendedRfNo;
    public String appointmentLetterExtendedUrl;
    public String regularAppointmentLetterDate;
    public String regularAppointmentLetterRfNo;
    public String regularAppointmentLetterUrl;
    public String confirmationLetterDate;
    public String confirmationLetterRfNo;
    public String confirmationLetterUrl;
    public String contractLetterDate;
    public String contractLetterRfNo;
    public String contractLetterUrl;
    public boolean isEmployeeActive;
	private LocalDate contractStartDate;
	private LocalDate contractEndDate;
	private String contractRemarks;
	private String timeZoneName;
	private String holidayTimeZoneName;
	private String empLeaveCategoryAllotmentName;
	private Integer recognisedExpYears;
	private Integer recognisedExpMonths;
	private LocalDateTime preferedJoiningDateTime;
    private String joiningDateRejectReason;
    private Boolean isJoiningDateConfirmed;
	
    public EmpJobDetailsDTO(int id,LocalDate retirementDate, LocalDate contractStartDate, LocalDate contractEndDate, String contractRemarks
		    ,String pfAccountNo, LocalDate pfDate, String uanNo,String gratuityNo, LocalDate gratuityDate
		    ,String sibAccountBank, String branchIfscCode, String smartCardNo, Boolean isDisplayWebsite, Boolean isVacationApplicable
		    ,Boolean isVacationTimeZoneApplicable, String timeZoneName, Boolean isHolidayTimeZoneApplicable, String holidayTimeZoneName, Boolean isDutyRosterApplicable
		    ,String empLeaveCategoryAllotmentName, Integer recognisedExpYears, Integer recognisedExpMonths) {
	this.empId = String.valueOf(id);
	this.retirementDate = !Utils.isNullOrEmpty(retirementDate)?Utils.convertLocalDateToStringDate(retirementDate):"";
	this.contractStartDate = contractStartDate;
	this.contractEndDate = contractEndDate;
	this.contractRemarks = contractRemarks;
	this.pfAccountNo = pfAccountNo;
	this.pfDate = !Utils.isNullOrEmpty(pfDate)? pfDate : null;
	this.uanNo = uanNo;
	this.gratuityNo = gratuityNo;
	this.gratuityDate = !Utils.isNullOrEmpty(gratuityDate)?gratuityDate:null;
	this.sibAccountBank = sibAccountBank;
	this.ifscCode = branchIfscCode;
	this.smartCardNo = smartCardNo;
	this.isDisplayWebsite = !Utils.isNullOrEmpty(isDisplayWebsite)? String.valueOf(isDisplayWebsite):"";
	this.isVacationApplicable = !Utils.isNullOrEmpty(isVacationApplicable)?String.valueOf(isVacationApplicable):"";
	this.isVacationTimeZoneApplicable =  !Utils.isNullOrEmpty(isVacationTimeZoneApplicable)? String.valueOf(isVacationTimeZoneApplicable):"";
	this.timeZoneName = timeZoneName;
	this.isHolidayTimeZoneApplicable = !Utils.isNullOrEmpty(isHolidayTimeZoneApplicable)? String.valueOf(isHolidayTimeZoneApplicable):"";
	this.holidayTimeZoneName = holidayTimeZoneName;
	this.isDutyRosterApplicable = String.valueOf(isDutyRosterApplicable);
	this.empLeaveCategoryAllotmentName =  empLeaveCategoryAllotmentName;
	this.recognisedExpYears = recognisedExpYears;
	this.recognisedExpMonths =recognisedExpMonths;	
   }
}
