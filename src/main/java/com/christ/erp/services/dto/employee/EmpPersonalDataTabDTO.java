package com.christ.erp.services.dto.employee;

import java.time.LocalDate;
import java.util.List;

import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.aws.FileUploadDownloadDTO;
import com.christ.erp.services.dto.common.CommonDTO;
import com.christ.erp.services.dto.common.CommonUploadDownloadDTO;
import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class EmpPersonalDataTabDTO {
	private int empId;
	private Integer personalDataId;
	private Integer jobDetailsId;
	private String employeeNo;
	private String empName;
	private SelectDTO gender;
	private LocalDate dob;
	private LocalDate doj;
	private String mobCountryCode;
	private String mobileNo;
	private String personalEmailId;
	private String alternateContactNo;
	//----differently Abled Details
	private Boolean isDifferentlyAbled;
	private SelectDTO erpDifferentlyAbled;
	private String differentlyAbledDetails;
	//----
	private SelectDTO bloodGroup;
	private SelectDTO nationality;
	private SelectDTO maritalStatus;
	private SelectDTO highestQualification;
	private SelectDTO religion;
	private String minority;
	private SelectDTO reservationCategory;
	private Boolean isAadharAvailable; 
	private String aadharNo;
	private CommonUploadDownloadDTO adharUploadDTO;
	private Boolean isAadharEnrolled;
	private String aadharEnrolledNo;
	private String panNo;
	private CommonUploadDownloadDTO panUploadDTO;
	private String fourWheelerNo;
    private CommonUploadDownloadDTO fourWheelerUploadDTO;
	private String twoWheelerNo;
    private CommonUploadDownloadDTO twoWheelerUploadDTO;
	//------current address
	private String currentAddressLine1;
	private String currentAddressLine2;
	private SelectDTO currentAddressCity;
	private String currentAddressCityOthers;
	private CommonDTO currentAddressCountry;
	private String currentAddressPinCode;
	private SelectDTO currentAddressState;
	private String currentAddressStateOthers;
	private Boolean isPermanentEqualsCurrent;
	//-------
	
	//-----------permanent address
	private String permanentAddressLine1;
	private String permanentAddressLine2;
	private SelectDTO permanentAddressCity;
	private String permanentAddressCityOthers;
	private CommonDTO permanentAddressCountry;
	private String permanentAddressPinCode;
	private SelectDTO permanentAddressState;
	private String permanentAddressStateOthers;
	//---------
	
	//-----Family/Dependency Information
	private List<EmpProfileFamilyDependentDTO> empFamilyDependentDTOList;
	private List<EmpProfilePFandGratuityDTO> empProfilePFandGratuityDTOList;
	//----------
	private String familyBackgroundBrief;
	//---emergency contact
	private String emergencyContactName;
	private String emergencyContactRelatonship;
	private String emergencyContactHome;
	private String emergencyContactWork;
	private String emergencyMobileNo;
	private String emergencyContactAddress;
	//-------passport details
	private String passportNo;
	private LocalDate passportIssuedDate;
	private String passportIssuedPlace;
	private String passportStatus;
	private LocalDate passportDateOfExpiry;
	private String passportComments;
	private String profilePhotoUrl;
	private String fileNameOriginal;
	private String uploadProcessCode;
	private CommonUploadDownloadDTO passportUploadDTO;
	//----------visa details
	private String visaNo;
	private LocalDate visaIssuedDate;
	private String visaStatus;
	private LocalDate visaDateOfExpiry;
	private String visaComments;
    private CommonUploadDownloadDTO visaUploadDTO;
    private String frroNo;
    private LocalDate frroIssuedDate;
    private String frroStatus;
    private LocalDate frroDateOfExpiry;
	private CommonUploadDownloadDTO frroUploadDTO;
	private String frroComments;
	private List<FileUploadDownloadDTO> uniqueFileNameList;
	private CommonUploadDownloadDTO signatureUploadDTO;

	
	public EmpPersonalDataTabDTO() {
		
	}
	
	public EmpPersonalDataTabDTO(int empId, Integer personalDataId, Integer jobDetailsId, String employeeNo, String empName, Integer genderId, String gender,	
			LocalDate dob, LocalDate doj , String mobCountryCode, String mobileNo, String personalEmailId, String alternateContactNo, Boolean isDifferentlyAbled,
			Integer erpDifferentlyAbledId, String erpDifferentlyAbledName, String differentlyAbledDetails, Integer bloodGroupId, String bloodGroup, 
			Integer nationalityId, String nationality, Integer maritalStatusId, String maritalStatus, Integer highestQualificationId, String highestQualificationName,
			Integer religionId, String religionName, Integer reservationCategoryId, String reservationCategoryName, String panNo, String fourWheelerNo, String currentAddressLine1,
			String currentAddressLine2, Integer currentAddressCityId, String currentAddressCityName, String currentAddressCityOthers, 
			Integer currentAddressCountryId, String currentAddressCountryName, String currentAddressPinCode, Integer currentAddressStateId, String currentAddressState, 
			String currentAddressStateOthers, Boolean isPermanentEqualsCurrent, String permanentAddressLine1, String permanentAddressLine2, Integer permanentAddressCityId,
			String permanentAddressCity, String permanentAddressCityOthers, Integer permanentAddressCountryId, String permanentAddressCountry, String permanentAddressPinCode,
			Integer permanentAddressStateId, String permanentAddressState, String permanentAddressStateOthers, String familyBackgroundBrief, String emergencyContactName,
			String emergencyContactRelatonship, String emergencyContactHome, String emergencyContactWork, String emergencyContactAddress, String passportNo, LocalDate passportIssuedDate,
			String passportIssuedPlace, String passportStatus, LocalDate passportDateOfExpiry, String passportComments, String profilePhotoUrl,  String fileNameOriginal, 
			String uploadProcessCode, Boolean minority, String aadharNo, String twoWheelerNo, Boolean isAadharAvailable, Boolean isAadharEnrolled,
			String aadharEnrolledNo, String emergencyMobileNo,  String visaNo, LocalDate visaIssuedDate, String visaStatus, LocalDate visaDateOfExpiry, 
			String visaComments, String frroNo, LocalDate frroIssuedDate, String frroStatus, LocalDate frroDateOfExpiry, String frroComments, 
			String adharFileNameUnique, String adharFileNameOrg, String adharProcessCode,
			String panFileNameUnique, String panFileNameOrg, String panProcessCode,
			String twoWheelerFileNameUnique, String twoWheelerFileNameOrg, String twoWheelerProcessCode,
			String fourWheelerFileNameUnique, String fourWheelerFileNameOrg, String fourWheelerProcessCode,
			String passportFileNameUnique, String passportFileNameOrg, String passportProcessCode,
			String visaFileNameUnique, String visaFileNameOrg, String visaProcessCode, 
			String frrFileNameUnique, String frrFileNameOrg, String frrProcessCode,
			String signatureFileNameUnique, String signatureFileNameOrg, String signatureProcessCode, String permanentCountryNationality, String currentAddressCountryNationality ) {
		this.empId = empId;
		this.personalDataId = personalDataId;
		this.jobDetailsId = jobDetailsId;
		this.employeeNo = employeeNo;
		this.empName = empName;
		if(!Utils.isNullOrEmpty(genderId)) {
			this.gender = new SelectDTO();
			this.gender.setLabel(gender);
			this.gender.setValue(Integer.toString(genderId));
		}
		if(!Utils.isNullOrEmpty(minority) && minority) {
			this.minority = "Yes";
		}
		else {
			this.minority = "No";
		}
		this.dob = dob;
		this.doj = doj;
		this.mobCountryCode = mobCountryCode;
		this.mobileNo = mobileNo;
		this.personalEmailId = personalEmailId;
		this.alternateContactNo = alternateContactNo;
		this.isDifferentlyAbled = isDifferentlyAbled;
		this.isAadharAvailable = isAadharAvailable;
		this.isAadharEnrolled = isAadharEnrolled;
		
		if(!Utils.isNullOrEmpty(erpDifferentlyAbledId)) { 
			this.erpDifferentlyAbled = new SelectDTO();
			this.erpDifferentlyAbled.setLabel(erpDifferentlyAbledName);
			this.erpDifferentlyAbled.setValue(Integer.toString(erpDifferentlyAbledId));
			this.differentlyAbledDetails = differentlyAbledDetails;
		}
		if(!Utils.isNullOrEmpty(bloodGroupId)) {
			this.bloodGroup = new SelectDTO();
			this.bloodGroup.setValue(Integer.toString(bloodGroupId));
			this.bloodGroup.setLabel(bloodGroup);
		}
		if(!Utils.isNullOrEmpty(nationalityId)) {
			this.nationality = new SelectDTO();
			this.nationality.value = Integer.toString(nationalityId); 
			this.nationality.label = nationality;
		}
		if (!Utils.isNullOrEmpty(maritalStatusId)){
			this.maritalStatus = new SelectDTO();
			this.maritalStatus.value = Integer.toString(maritalStatusId);
			this.maritalStatus.label = maritalStatus;
		}
		if(!Utils.isNullOrEmpty(highestQualificationId)){
			this.highestQualification = new SelectDTO();
			this.highestQualification.value = Integer.toString(highestQualificationId);
			this.highestQualification.label = highestQualificationName;
		}
		if(!Utils.isNullOrEmpty(religionId)){
			this.religion = new SelectDTO();
			this.religion.value = Integer.toString(religionId);
			this.religion.label = religionName;
		}
		if(!Utils.isNullOrEmpty(reservationCategoryId)){
			this.reservationCategory = new SelectDTO();
			this.reservationCategory.value = Integer.toString(reservationCategoryId);
			this.reservationCategory.label = reservationCategoryName;
		}
		this.aadharNo = aadharNo;
		this.aadharEnrolledNo = aadharEnrolledNo;
		this.panNo = panNo;
		this.fourWheelerNo = fourWheelerNo;
		this.twoWheelerNo = twoWheelerNo;
		this.currentAddressLine1 = currentAddressLine1;
		this.currentAddressLine2 = currentAddressLine2;
		
		if(!Utils.isNullOrEmpty(currentAddressCityId)){
			this.currentAddressCity = new SelectDTO();
			this.currentAddressCity.value = Integer.toString(currentAddressCityId);
			this.currentAddressCity.label = currentAddressCityName;
		}
		this.currentAddressCityOthers = currentAddressCityOthers;
		this.currentAddressPinCode = currentAddressPinCode;
		
		if(!Utils.isNullOrEmpty(currentAddressCountryId)){
			this.currentAddressCountry = new CommonDTO();
			this.currentAddressCountry.value = Integer.toString(currentAddressCountryId);
			this.currentAddressCountry.label = currentAddressCountryName;
			this.currentAddressCountry.nationality = currentAddressCountryNationality;
		}
		
		if(!Utils.isNullOrEmpty(currentAddressStateId)){
			this.currentAddressState = new SelectDTO();
			this.currentAddressState.value = Integer.toString(currentAddressStateId);
			this.currentAddressState.label = currentAddressState;
		}
		this.currentAddressStateOthers = currentAddressStateOthers;
		this.isPermanentEqualsCurrent = isPermanentEqualsCurrent;
		this.permanentAddressLine1 = permanentAddressLine1;
		this.permanentAddressLine2 = permanentAddressLine2;
		
		if(!Utils.isNullOrEmpty(permanentAddressCityId)){
			this.permanentAddressCity = new SelectDTO();
			this.permanentAddressCity.value = Integer.toString(permanentAddressCityId);
			this.permanentAddressCity.label = permanentAddressCity;
		}
		this.permanentAddressCityOthers = permanentAddressCityOthers;
		
		if(!Utils.isNullOrEmpty(permanentAddressCountryId)){
			this.permanentAddressCountry = new CommonDTO();
			this.permanentAddressCountry.value = Integer.toString(permanentAddressCountryId);
			this.permanentAddressCountry.label = permanentAddressCountry;
			this.permanentAddressCountry.nationality = permanentCountryNationality;
		}
		this.permanentAddressPinCode = permanentAddressPinCode;
		if(!Utils.isNullOrEmpty(permanentAddressStateId)){
			this.permanentAddressState = new SelectDTO();
			this.permanentAddressState.value = Integer.toString(permanentAddressStateId);
			this.permanentAddressState.label = permanentAddressCountry;
		}
		this.permanentAddressStateOthers = permanentAddressStateOthers;
		this.familyBackgroundBrief = familyBackgroundBrief;
		this.emergencyContactName = emergencyContactName;
		this.emergencyContactRelatonship = emergencyContactRelatonship;
		this.emergencyContactHome = emergencyContactHome;
		this.emergencyContactWork = emergencyContactWork;
		this.emergencyMobileNo = emergencyMobileNo;
		this.emergencyContactAddress = emergencyContactAddress;
		//passport details
		this.passportNo = passportNo;
		this.passportIssuedDate = passportIssuedDate;
		this.passportIssuedPlace = passportIssuedPlace;
		this.passportStatus = passportStatus;
		this.passportDateOfExpiry = passportDateOfExpiry;
		this.passportComments = passportComments;
		//visa details
		this.visaNo = visaNo;
		this.visaIssuedDate = visaIssuedDate;
		this.visaStatus = visaStatus;
		this.visaDateOfExpiry = visaDateOfExpiry;
		this.visaComments = visaComments;
		this.frroNo = frroNo;
		this.frroIssuedDate = frroIssuedDate;
		this.frroStatus = frroStatus;
		this.frroDateOfExpiry = frroDateOfExpiry;
		this.frroComments = frroComments;
		
		this.profilePhotoUrl = profilePhotoUrl;
		this.fileNameOriginal = fileNameOriginal;
		this.uploadProcessCode = uploadProcessCode;

		CommonUploadDownloadDTO adharUploadDTO = null;
		if(!Utils.isNullOrEmpty(adharFileNameUnique)) {
			adharUploadDTO = new CommonUploadDownloadDTO();
			adharUploadDTO.setActualPath(adharFileNameUnique);
			adharUploadDTO.setOriginalFileName(adharFileNameOrg);
			adharUploadDTO.setProcessCode(adharProcessCode);
			adharUploadDTO.setNewFile(false);
		}
		this.adharUploadDTO = adharUploadDTO;

		CommonUploadDownloadDTO panUploadDTO = null;
		if(!Utils.isNullOrEmpty(panFileNameUnique)) {
			panUploadDTO = new CommonUploadDownloadDTO();
			panUploadDTO.setActualPath(panFileNameUnique);
			panUploadDTO.setOriginalFileName(panFileNameOrg);
			panUploadDTO.setProcessCode(panProcessCode);
			panUploadDTO.setNewFile(false);
		}
		this.panUploadDTO = panUploadDTO;
		CommonUploadDownloadDTO twoWheelerUploadDTO = null;
		if(!Utils.isNullOrEmpty(twoWheelerFileNameUnique)) {
			twoWheelerUploadDTO = new CommonUploadDownloadDTO();
			twoWheelerUploadDTO.setActualPath(twoWheelerFileNameUnique);
			twoWheelerUploadDTO.setOriginalFileName(twoWheelerFileNameOrg);
			twoWheelerUploadDTO.setProcessCode(twoWheelerProcessCode);
			twoWheelerUploadDTO.setNewFile(false);
		}
		this.twoWheelerUploadDTO = twoWheelerUploadDTO;
		CommonUploadDownloadDTO fourWheelerUploadDTO = null;
		if(!Utils.isNullOrEmpty(fourWheelerFileNameUnique)) {
			fourWheelerUploadDTO = new CommonUploadDownloadDTO();
			fourWheelerUploadDTO.setActualPath(fourWheelerFileNameUnique);
			fourWheelerUploadDTO.setOriginalFileName(fourWheelerFileNameOrg);
			fourWheelerUploadDTO.setProcessCode(fourWheelerProcessCode);
			fourWheelerUploadDTO.setNewFile(false);
		}
		this.fourWheelerUploadDTO = fourWheelerUploadDTO;

		CommonUploadDownloadDTO passportUploadDTO = null;
		if(!Utils.isNullOrEmpty(passportFileNameUnique)) {
			passportUploadDTO = new CommonUploadDownloadDTO();
			passportUploadDTO.setActualPath(passportFileNameUnique);
			passportUploadDTO.setOriginalFileName(passportFileNameOrg);
			passportUploadDTO.setProcessCode(passportProcessCode);
			passportUploadDTO.setNewFile(false);
		}
		this.passportUploadDTO = passportUploadDTO;

		CommonUploadDownloadDTO visaUploadDTO = null;
		if(!Utils.isNullOrEmpty(visaFileNameUnique)) {
			visaUploadDTO = new CommonUploadDownloadDTO();
			visaUploadDTO.setActualPath(visaFileNameUnique);
			visaUploadDTO.setOriginalFileName(visaFileNameOrg);
			visaUploadDTO.setProcessCode(visaProcessCode);
			visaUploadDTO.setNewFile(false);
		}
		this.visaUploadDTO = visaUploadDTO;
		CommonUploadDownloadDTO frroUploadDTO = null;
		if(!Utils.isNullOrEmpty(frrFileNameUnique)) {
			frroUploadDTO = new CommonUploadDownloadDTO();
			frroUploadDTO.setActualPath(frrFileNameUnique);
			frroUploadDTO.setOriginalFileName(frrFileNameOrg);
			frroUploadDTO.setProcessCode(frrProcessCode);
			frroUploadDTO.setNewFile(false);
		}
		this.frroUploadDTO = frroUploadDTO;
		CommonUploadDownloadDTO signatureUploadDTO = null;
		if(!Utils.isNullOrEmpty(signatureFileNameUnique)) {
			signatureUploadDTO = new CommonUploadDownloadDTO();
			signatureUploadDTO.setActualPath(signatureFileNameUnique);
			signatureUploadDTO.setOriginalFileName(signatureFileNameOrg);
			signatureUploadDTO.setProcessCode(signatureProcessCode);
			signatureUploadDTO.setNewFile(false);
		}
		this.signatureUploadDTO = signatureUploadDTO;
		
	}

}
