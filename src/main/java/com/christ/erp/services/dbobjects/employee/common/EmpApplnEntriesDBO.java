package com.christ.erp.services.dbobjects.employee.common;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import com.christ.erp.services.dbobjects.common.ErpCampusDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusDepartmentMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpDepartmentDBO;
import com.christ.erp.services.dbobjects.common.ErpGenderDBO;
import com.christ.erp.services.dbobjects.common.ErpLocationDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.common.UrlAccessLinkDBO;
import com.christ.erp.services.dbobjects.employee.recruitment.*;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
@Entity
@Table(name="emp_appln_entries")
public class EmpApplnEntriesDBO implements Serializable{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_appln_entries_id")
    public Integer id;
	
	@Column(name = "application_no")
	public Integer applicationNo;
	
	@Column(name = "applicant_name")
	public String applicantName;
	
	@ManyToOne
	@JoinColumn(name = "erp_gender_id")
	public ErpGenderDBO erpGenderDBO;
	
	@Column(name = "dob")
	public LocalDate dob;
	
	@Column(name = "personal_email_id")
	public String personalEmailId;
	
	@Column(name = "mobile_no_country_code")
	public String mobileNoCountryCode;
	
	@Column(name = "mobile_no")
	public String mobileNo;
	
	@ManyToOne
	@JoinColumn(name="emp_employee_category_id")
	public EmpEmployeeCategoryDBO empEmployeeCategoryDBO;
	
	@Column(name = "highest_qualification_level")
	public Integer highestQualificationLevel;
	
	@Column(name = "is_currently_working")
	public Boolean isCurrentlyWorking;

	@Column(name = "notice_period")
	public String noticePeriod;
	
	@Column(name = "current_monthly_salary")
	public BigDecimal currentMonthlySalary;
	
	@Column(name = "major_achievements")
	public String  majorAchievements;
	
	@Column(name = "expected_salary")
    public  BigDecimal  expectedSalary; 
    
//	@Column(name = "is_inflibnet_vidwan")
//	public Boolean isInflibnetVidwan;
		
//	@Column(name = "inflibnet_vidwan_no")
//	public Integer inflibnetVidwanNo;
//	
//	@Column(name = "scopus_id")
//	public String  scopusId;
//	    
//	@Column(name = "h_index")
//	public Integer  hIndex;
	
	@Column(name = "is_interviewed_before")
	public Boolean  isInterviewedBefore;
	
	@Column(name = "is_research_experience_present")
	public Boolean  isResearchExperiencePresent;
	
	@Column(name = "interviewed_before_department")
	public String  interviewedBeforeDepartment;
	
	@Column(name = "interviewed_before_year")
	public Integer  interviewedBeforeYear;
	
	@Column(name = "interviewed_before_application_no")
	public Integer  interviewedBeforeApplicationNo;
	
	@Column(name = "interviewed_before_subject")
	public String  interviewedBeforeSubject;
	
	@ManyToOne
	@JoinColumn(name = "emp_appln_vacancy_information_id")
	public EmpApplnVacancyInformationDBO empApplnVacancyInformationDBO;
	   
	@Column(name = "about_vacancy_others")
	public String  aboutVacancyOthers;
	
	@Column(name = "other_information")
	public String  otherInformation;
	
	@Column(name = "is_draft_mode")
	public Boolean  isDraftMode;
	
	@Column(name = "is_contacted_by_hod")
	public Boolean  isContactedByHod;
	
	@Column(name = "is_shortlisted_for_interview")
	public Boolean  isShortlistedForInterview;
	
	@Column(name = "is_forwarded_by_hod")
	public Boolean  isForwardedByHod;

	@ManyToOne
	@JoinColumn(name = "erp_campus_id")
	public ErpCampusDBO  erpCampusDBO;
	
//	@Column(name = "personel_officer_comments")
//	public String   personelOfficerComments;
	
//	@Column(name = "cfo_dean_comments")
//	public String  cfoDeanComments;
	
//	@Column(name = "cfo_dean_users_id")
//	public Integer  cfoDeanUsersId;
	
//	@Column(name = "vc_nominee_comments")
//	public String  vcNomineeComments;
	
	@Column(name = "is_documents_verified")
	public Boolean  isDocumentsVerified;
	
//	@Column(name = "is_selected")
//	public Boolean  isSelected;
	
	@ManyToOne
	@JoinColumn(name = "emp_designation_id")
	public EmpDesignationDBO  empDesignationDBO;
	
	@ManyToOne
	@JoinColumn(name = "title_id")
	public ErpEmployeeTitleDBO  titleId;
	
	@Column(name = "total_previous_experience_years")
	public Integer  totalPreviousExperienceYears;
	
	@Column(name = "total_previous_experience_months")
	public Integer  totalPreviousExperienceMonths;
	
	@ManyToOne
	@JoinColumn(name = "emp_appln_registrations_id")
	public EmpApplnRegistrationsDBO empApplnRegistrationsDBO;
	
	@ManyToOne
	@JoinColumn(name = "emp_employee_job_category_id")
	public EmpEmployeeJobCategoryDBO empEmployeeJobCategoryDBO;
	
	@Column(name = "application_status")
	public String  applicationStatus;
	
	@ManyToOne
	@JoinColumn(name = "emp_appln_interview_template_id")
	public EmpApplnInterviewTemplateDBO empApplnInterviewTemplateDBO;
	
	@ManyToOne
	@JoinColumn(name = "emp_appln_subject_category_id")
	public EmpApplnSubjectCategoryDBO empApplnSubjectCategoryDBO;
	
	@ManyToOne
	@JoinColumn(name = "emp_appln_subject_category_specialization_id")
	public EmpApplnSubjectCategorySpecializationDBO empApplnSubjectCategorySpecializationDBO;
	
	@Column(name = "total_part_time_previous_experience_years")
	public Integer totalPartTimePreviousExperienceYears;
	
	@Column(name = "total_part_time_previous_experience_months")
	public Integer totalPartTimePreviousExperienceMonths;
	
	@Column(name = "offer_letter_url")
	public String  offerLetterUrl;

	@ManyToOne
	@JoinColumn(name = "applicant_current_process_status")
	public ErpWorkFlowProcessDBO applicantCurrentProcessStatus;
	
	@Column(name = "applicant_status_time")
	public LocalDateTime applicantStatusTime;

	@ManyToOne
	@JoinColumn(name = "application_current_process_status")
	public ErpWorkFlowProcessDBO applicationCurrentProcessStatus;
	
	@Column(name = "application_status_time")
	public LocalDateTime applicationStatusTime;
	
	@Column(name = "submission_date_time")
	public LocalDateTime submissionDate; 
	
	@OneToOne
	@JoinColumn(name="erp_campus_department_mapping_id")
	public ErpCampusDepartmentMappingDBO erpCampusDepartmentMappingDBO;
	
	@Column(name = "offer_letter_generated_date")
	public LocalDateTime offerLetterGeneratedDate;

	@ManyToOne
	@JoinColumn(name="emp_appln_non_availability_id")
	public EmpApplnNonAvailabilityDBO empApplnNonAvailabilityDBO;

	@Column(name = "job_rejection_reason")
	public String  jobRejectionReason;

	@OneToMany(mappedBy = "empApplnEntriesDBO", cascade = CascadeType.ALL)
    public Set<EmpApplnSubjSpecializationPrefDBO> empApplnSubjSpecializationPrefDBOs = new HashSet<>();
	
	@OneToMany(mappedBy = "empApplnEntriesDBO", cascade = CascadeType.ALL)
    public Set<EmpApplnLocationPrefDBO> empApplnLocationPrefDBOs = new HashSet<>();
	
	@OneToOne(mappedBy = "empApplnEntriesDBO", cascade = CascadeType.ALL)
    public EmpApplnPersonalDataDBO empApplnPersonalDataDBO;
	
	@OneToMany(mappedBy = "empApplnEntriesDBO", cascade = CascadeType.ALL)
    public Set<EmpApplnEducationalDetailsDBO> empApplnEducationalDetailsDBOs = new HashSet<>();
	
	@OneToMany(mappedBy = "empApplnEntriesDBO", cascade = CascadeType.ALL)
    public Set<EmpApplnEligibilityTestDBO> empApplnEligibilityTestDBOs = new HashSet<>();
	
	@OneToMany(mappedBy = "empApplnEntriesDBO", cascade = CascadeType.ALL)
    public Set<EmpApplnWorkExperienceDBO> empApplnWorkExperienceDBOs = new HashSet<>();
	
	@OneToMany(mappedBy = "empApplnEntriesDBO", cascade = CascadeType.ALL)
    public Set<EmpApplnAddtnlInfoEntriesDBO> empApplnAddtnlInfoEntriesDBOs = new HashSet<>();
	
	@OneToMany(mappedBy = "empApplnEntriesDBO",cascade=CascadeType.ALL )
	public Set<EmpApplnDignitariesFeedbackDBO> applnDignitariesFeedbackDBOs;
	
	@OneToMany(mappedBy = "empApplnEntriesDBO", cascade = CascadeType.ALL)
    public Set<EmpApplnInterviewSchedulesDBO> empApplnInterviewSchedulesDBOs = new HashSet<>();

	@OneToOne(mappedBy = "empApplnEntriesId", cascade = CascadeType.ALL)
	public EmpJobDetailsDBO empJobDetailsDBO;

	@Column(name = "created_users_id", updatable = false)
	public Integer  createdUsersId;
	
	@Column(name = "modified_users_id")
	public Integer  modifiedUsersId;
	
	@Column(name = "record_status")
	public Character recordStatus;
	
	@ManyToOne
	@JoinColumn(name = "offer_letter_template_id")
	public ErpTemplateDBO erpTemplateForOfferLetterDBO;
	
	@ManyToOne
	@JoinColumn(name = "regret_letter_template_id")
	public ErpTemplateDBO erpTemplateForRegretLetterDBO;
	
	@Column(name = "regret_letter_generated_date")
	public LocalDateTime regretLetterGeneratedDate;
	
	@Column(name = "regret_letter_url")
	public String regretLetterUrl;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shortlisted_employee_id")
	private EmpDBO shortlistedEmployeeId;
	
	@ManyToOne
	@JoinColumn(name = "shortlisted_department_id")
	private ErpDepartmentDBO shortlistedDepartmentId;
	
	@ManyToOne
	@JoinColumn(name = "shortlisted_location_id")
	private ErpLocationDBO shortistedLocationId;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "offer_letter_url_id")
	private UrlAccessLinkDBO offerLetterUrlDbo;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "regret_letter_url_id")
	private UrlAccessLinkDBO regretLetterUrlDbo;
	
	@Column(name = "stage2_onhold_rejected_comments" ,  columnDefinition = "tinytext")
	private String  stage2OnholdRejectedComments;
}