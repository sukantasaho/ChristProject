package com.christ.erp.services.dto.employee.onboarding;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.ExModelBaseDTO;
import com.christ.erp.services.dto.common.ModelBaseDTO;
import com.christ.erp.services.dto.common.SelectDTO;
import com.christ.erp.services.dto.employee.common.WorkFlowStatusApplicantTimeLineDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnInterviewSchedulesDTO;
import com.christ.erp.services.dto.employee.recruitment.EmpApplnInterviewScoreDTO;
import com.christ.erp.services.dto.employee.recruitment.InterviewScoreEntryGroupHeadDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class EmpApplnEntriesDTO extends ModelBaseDTO
{
	public String applicantId;
	public String applicantNumber;
	public String applicantName;
	public String applicantCampus;
	public String applicantAppliedFor;
	public String applicantCountry;
	public ExModelBaseDTO category;
	public String subjectCategoryName;
	public String subjectCategorySpecializationName;
	public LocalDate applicationDate;
	public String highestQualificationLevel;
	public boolean contactedByHod;
	public boolean shortlisted;
	public String employeeCategoryName;
	public String locationPreference;
	public String orcidNo;
	public LocalDateTime fromDate;
	public LocalDateTime toDate;
	public String subjectCategoryId;
	public String applicationStatus;
	public String campusDepartmentId;
	public String status;
	public EmpApplnInterviewSchedulesDTO empApplnInterviewSchedulesDTO ;
	public float totalScore;
	public boolean isModified = false;
	public String reasonForRejection;
	public String offerStatus;
	public String interviewDateTime;
	public HashSet<String> panelistNames = new HashSet<String>(); 
	public Map<String,String> interviewCommentsMap = new HashMap<>();
	public Map<String,Map<String,List<Map<String,String>>>> interviewScoreMap = new HashMap<>();
	public Map<String, InterviewScoreEntryGroupHeadDTO> interviewScoreEntryGroupHeadDTO = new HashMap<>();
	public String offerLetterGeneratedDate;
	public String regretLetterGeneratedDate;
	private String totalFullTimeExperience;
	private String profilePhotoUrl; // may need to remove
	private String applicantEmail;
	private String applicantMobile;
	private int countApplicationReceived;
	private int countScheduleStageOne;
	private int countInterviewStatusOne;
	private int countScheduleStageOneApprovel;	
	private int countScheduleStageTwo;
	private int countInterviewStatusTwo;
	private int countIcheduleStageThree;
	private int countInterviewStatusThree;
	private int countOfferStatus;
	private String selectionStatus;
	private String employeeName;
	private String employeeEmail;
	private String employeeMobileNo;
	private String scoreEntry;
	private List<EmpApplnInterviewScoreDTO> applicantInterviewScore;
	private String eligibilityId;
	private List<SelectDTO> research;
	private SelectDTO shortlistedDepartment;
	private Boolean isCategoryAcademic;
	private List<WorkFlowStatusApplicantTimeLineDTO> workFlowTimeLine;
	private SelectDTO gender;
	private String dob;
	private String vacancyInformation;
	private String majorAchievements;
	private BigDecimal  expectedSalary;
	private Boolean  isInterviewedBefore;
	private String interviewedBeforeDepartment;
	private Integer  interviewedBeforeYear;
	private Integer interviewedBeforeApplicationNo;
	private String interviewedBeforeSubject;
	private Boolean isResearchExperiencePresent;
	private LocalDateTime joiningDateTime;
	private Boolean isAcademic;
	private LocalDateTime preferedJoiningDateTime;
    private String joiningDateRejectReason;
    private Boolean isJoiningDateConfirmed;
    private String stage2OnholdRejectedComments;
    private String stage3Comments;
	
	public EmpApplnEntriesDTO(int id,Integer applicationNo ) {
		this.applicantId = String.valueOf(id);
		this.applicantNumber = String.valueOf(applicationNo);
	}
	
	public EmpApplnEntriesDTO(int id,Integer applicationNo,String applicantName,String personalEmailId,String gender,String mobileNoCountryCode,String mobileNo,LocalDate dob,
			 String vacancyInformationName,String aboutVacancyOthers,String employeeJobName,String subjectCategory,String subjectCategorySpecializationName,String majorAchievements,BigDecimal  expectedSalary,
			 Boolean  isInterviewedBefore,String interviewedBeforeDepartment,Integer  interviewedBeforeYear,Integer interviewedBeforeApplicationNo,String interviewedBeforeSubject,
			 Boolean isResearchExperiencePresent) {
		this.applicantId = String.valueOf(id);
		this.applicantNumber = String.valueOf(applicationNo);
		this.applicantName = applicantName;
		this.applicantEmail = personalEmailId;
		this.gender = new SelectDTO();
		this.gender.setLabel(gender);
		String mobile = "";
		if(!Utils.isNullOrEmpty(mobileNoCountryCode)) {
			mobile = mobileNoCountryCode+"-";
		}
		if(!Utils.isNullOrEmpty(mobileNo)) {
			mobile +=mobileNo;
		}
		this.applicantMobile = mobile;
		this.dob = !Utils.isNullOrEmpty(dob)?Utils.convertLocalDateToStringDate(dob):"";
		this.vacancyInformation = !Utils.isNullOrEmpty(vacancyInformationName)?vacancyInformationName:aboutVacancyOthers;
		this.category = new ExModelBaseDTO();
		this.category.setText(employeeJobName);
		this.subjectCategoryName = subjectCategory;
		this.subjectCategorySpecializationName = subjectCategorySpecializationName;
		this.majorAchievements = majorAchievements;
		this.expectedSalary = expectedSalary;
		this.isInterviewedBefore = isInterviewedBefore;
		this.interviewedBeforeDepartment =interviewedBeforeDepartment;
		this.interviewedBeforeYear = interviewedBeforeYear;
		this.interviewedBeforeApplicationNo = interviewedBeforeApplicationNo;
		this.interviewedBeforeSubject = interviewedBeforeSubject;
		
	}
	
}
