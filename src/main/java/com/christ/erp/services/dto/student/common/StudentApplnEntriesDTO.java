package com.christ.erp.services.dto.student.common;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dbobjects.student.common.ApplicationVerificationStatus;
import com.christ.erp.services.dto.admission.applicationprocess.ErpResidentCategoryDTO;
import com.christ.erp.services.dto.common.ErpCampusProgrammeMappingDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudentApplnEntriesDTO extends ModelBaseDTO {
     
	private Integer id;
	private String studentApplnEntriesId;
	private StudentPersonalDataDTO studentPersonalDataDTO;
	private String applicantName;
	private String applicationNumber;
	private SelectDTO program;
	private String programme;
	private String campusOrLocation;
	private String photoUrl;
	private String mobileNo;
	private String personalEmailId;
	private String applicationVerificationStatus;
    private SelectDTO studentApplnVerificationRemarksId;
    private String applicationVerificationAddtlRemarks;
    private LocalDate applicationVerifiedDate;
	private String applicationVerifiedUserId;
    private StudentApplnPrerequisiteDTO studentApplnPrerequisite;
	private List<StudentEducationalDetailsDTO> studentEducationalDetails;
	private Integer appliedAcademicYear;
	private StudentDTO student;
	private Integer erpGender;
	private ErpCampusProgrammeMappingDTO erpCampusProgrammeMappingId;
	private String erpStatus;
	private SelectDTO erpResidentCategoryDTO;
	private ErpResidentCategoryDTO ErpResidentCategory;
	private List<StudentDTO> studentDTO;
	
	private LocalDate dob;
	private SelectDTO gender;
	private LocalDateTime submissionDateTime;
	private SelectDTO specialization;
	private SelectDTO appliedBatch;
	private BigDecimal totalWeightage;
	private LocalDateTime feePaymentFinalDateTime;
	private String applicantionStatusDisplayText;
	private String selectionStatusRemarks;
	private LocalDateTime applicationStatusTime;
	private String applicantStatusDisplayText1;
	private LocalDateTime applicantStatusTime;
	private Integer studentPersonalDataAddtnlId;
	private Integer studentPersonalDataAddressId;
	private String  registerNo;
	private Integer admissionYear;
	private SelectDTO admissionType;
	private SelectDTO intakeBatch;
	private Integer  totalPartTimePreviousExperienceMonths;
	private Integer  totalPartTimePreviousExperienceYears;
	private Integer  totalPreviousExperienceMonths;
	private Integer  totalPreviousExperienceYears;

	public  StudentApplnEntriesDTO(int id,Integer studentPersonalDataAddtnlId,Integer studentPersonalDataAddressId, Integer applicationNo,String applicantName,LocalDate dob,String genderName,String mobileNoCountryCode,String mobileNo,String personalEmailId,LocalDateTime submissionDateTime,
			String programmeName,String campusName,String locationName,String specializationName,String residentCategoryName,Integer academicYear,String admissionType,String admIntakeBatchName,ApplicationVerificationStatus applicationVerificationStatusName,LocalDate applicationVerifiedDate,
			String  userName,String verificationRemarksName,String applicationVerificationAddtlRemarks,BigDecimal totalWeightage,LocalDateTime feePaymentFinalDateTime,String applicantionStatusDisplayText,String selectionStatusRemarks,
			LocalDateTime applicationStatusTime,String applicantStatusDisplayText1,LocalDateTime applicantStatusTime,String  registerNo,Integer  totalPartTimePreviousExperienceMonths,Integer  totalPartTimePreviousExperienceYears,
								   Integer  totalPreviousExperienceMonths,Integer  totalPreviousExperienceYears,String campusLocationDisplayName) {
		this.studentApplnEntriesId = String.valueOf(id);
		if(!Utils.isNullOrEmpty(studentPersonalDataAddtnlId)) {
			this.studentPersonalDataAddtnlId = studentPersonalDataAddtnlId;
		}
		if(!Utils.isNullOrEmpty(studentPersonalDataAddressId)) {
			this.studentPersonalDataAddressId = studentPersonalDataAddressId;
		}
		this.applicationNumber = String.valueOf(applicationNo);
		this.applicantName =  String.valueOf(applicantName);
		if(!Utils.isNullOrEmpty(dob)) {
			this.dob = dob;
		}
	    this.setGender(new SelectDTO());
	    this.getGender().setLabel(genderName);
		String mobile = "";
		if(!Utils.isNullOrEmpty(mobileNoCountryCode)) {
			mobile = mobileNoCountryCode+"-";
		}
		if(!Utils.isNullOrEmpty(mobileNo)) {
			mobile +=mobileNo;
		}
		this.mobileNo = mobile;
		this.personalEmailId = personalEmailId;
		this.submissionDateTime = submissionDateTime;
		this.programme = programmeName;
		if(!Utils.isNullOrEmpty(campusLocationDisplayName)){
			this.campusOrLocation = campusLocationDisplayName;
		} else {
			this.campusOrLocation = !Utils.isNullOrEmpty(campusName)?campusName:locationName;
		}
		if(!Utils.isNullOrEmpty(specializationName)){
			this.setSpecialization(new SelectDTO());
			this.getSpecialization().setLabel(specializationName);
		}
		if(!Utils.isNullOrEmpty(residentCategoryName)){
			this.setErpResidentCategoryDTO(new SelectDTO());
			this.getErpResidentCategoryDTO().setLabel(residentCategoryName);
		}
//		this.setAppliedBatch(new SelectDTO());
//		this.getAppliedBatch().setLabel(batchName);
		this.admissionYear = academicYear;
		if(!Utils.isNullOrEmpty(admissionType)){
			this.setAdmissionType(new SelectDTO());
			this.getAdmissionType().setLabel(admissionType);
		}
		if(!Utils.isNullOrEmpty(admIntakeBatchName)){
			this.setIntakeBatch(new SelectDTO());
			this.getIntakeBatch().setLabel(admIntakeBatchName);
		}
		this.applicationVerificationStatus = applicationVerificationStatusName.name();
		if(!Utils.isNullOrEmpty(applicationVerifiedDate)) {
			this.applicationVerifiedDate = applicationVerifiedDate;
		}
		this.applicationVerifiedUserId = userName;
		if(!Utils.isNullOrEmpty(verificationRemarksName)){
			this.setStudentApplnVerificationRemarksId(new SelectDTO());
			this.getStudentApplnVerificationRemarksId().setLabel(verificationRemarksName);
		}
		this.applicationVerificationAddtlRemarks = applicationVerificationAddtlRemarks;
		this.totalWeightage = totalWeightage;
		if(!Utils.isNullOrEmpty(feePaymentFinalDateTime)) {
			this.feePaymentFinalDateTime = feePaymentFinalDateTime;
		}
		this.applicantionStatusDisplayText = applicantionStatusDisplayText;
		this.selectionStatusRemarks = selectionStatusRemarks;
		if(!Utils.isNullOrEmpty(applicationStatusTime)) {
			this.applicationStatusTime =applicationStatusTime;
		}
		this.applicantStatusDisplayText1 = applicantStatusDisplayText1;
		this.applicantStatusTime = applicantStatusTime;
		this.registerNo = registerNo;
		this.setTotalPartTimePreviousExperienceMonths(totalPartTimePreviousExperienceMonths);
		this.setTotalPartTimePreviousExperienceYears(totalPartTimePreviousExperienceYears);
		this.setTotalPreviousExperienceMonths(totalPreviousExperienceMonths);
		this.setTotalPreviousExperienceYears(totalPreviousExperienceYears);
	}
	
	
  }
