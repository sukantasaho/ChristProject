package com.christ.erp.services.dbobjects.student.common;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.account.fee.AccFeeDemandDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmAdmissionScheduleTimeDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.AdmSelectionProcessPlanDetailDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.ModeOfStudy;
import com.christ.erp.services.dbobjects.admission.settings.AdmProgrammeBatchDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpAdmissionCategoryDBO;
import com.christ.erp.services.dbobjects.common.ErpCampusProgrammeMappingDBO;
import com.christ.erp.services.dbobjects.common.ErpGenderDBO;
import com.christ.erp.services.dbobjects.common.ErpResidentCategoryDBO;
import com.christ.erp.services.dbobjects.common.ErpSpecializationDBO;
import com.christ.erp.services.dbobjects.common.ErpUsersDBO;
import com.christ.erp.services.dbobjects.common.ErpWorkFlowProcessDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaBatchDBO;
import com.christ.erp.services.dbobjects.curriculum.settings.AcaDurationDetailDBO;
import com.christ.erp.services.dbobjects.hostel.settings.HostelAdmissionsDBO;
import com.christ.erp.services.dbobjects.student.recruitment.StudentApplnPreferenceDBO;
import com.christ.erp.services.dbobjects.student.recruitment.StudentApplnPrerequisiteDBO;
import com.christ.erp.services.dbobjects.student.recruitment.StudentApplnRegistrationsDBO;
import com.christ.erp.services.dbobjects.student.recruitment.StudentApplnSelectionProcessDatesDBO;
import com.christ.erp.services.dbobjects.student.recruitment.StudentExtraCurricularDetailsDBO;
import com.christ.erp.services.dbobjects.student.recruitment.StudentWorkExperienceDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "student_appln_entries")
@Setter
@Getter
public class StudentApplnEntriesDBO  {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "student_appln_entries_id")
	public int id;

	@Column(name = "application_no")
	public Integer applicationNo;

	//	@ManyToOne(fetch = FetchType.LAZY)
	@ManyToOne(cascade = CascadeType.ALL ,fetch = FetchType.LAZY)
	@JoinColumn(name="student_personal_data_id")
	public StudentPersonalDataDBO studentPersonalDataDBO;

	/*@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="student_personal_data_id")
	public StudentPersonalDataDBO studentPersonalDataDBO;*/

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "applied_academic_year_id")
	public ErpAcademicYearDBO appliedAcademicYear;

	@Column(name = "submission_date_time")
	public LocalDateTime submissionDateTime;

	@Column(name = "applicant_name")
	public String applicantName;

	@ManyToOne(fetch = FetchType.LAZY)
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
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="student_appln_registrations_id")
	public StudentApplnRegistrationsDBO  studentApplnRegistrationsDBO ;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="adm_programme_batch_id")
	public AdmProgrammeBatchDBO admProgrammeBatchDBO;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="erp_campus_programme_mapping_id")
	public ErpCampusProgrammeMappingDBO erpCampusProgrammeMappingDBO;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="erp_admission_category_id")
	public ErpAdmissionCategoryDBO erpAdmissionCategoryDBO;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="erp_resident_category_id")
	public ErpResidentCategoryDBO erpResidentCategoryDBO;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="erp_specialization_id")
	public ErpSpecializationDBO erpSpecializationDBO;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="aca_batch_id")
	public AcaBatchDBO acaBatchDBO;

	@Column(name = "is_having_work_experience")
	public Boolean isHavingWorkExperience;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "applicant_current_process_status")
	public ErpWorkFlowProcessDBO applicantCurrentProcessStatus;

	@Column(name = "applicant_status_time")
	public LocalDateTime applicantStatusTime;
	
	@Column(name = "fee_payment_final_datetime")
	public LocalDateTime feePaymentFinalDateTime;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_current_process_status")
	public ErpWorkFlowProcessDBO applicationCurrentProcessStatus;

	@Column(name = "application_status_time")
	public LocalDateTime applicationStatusTime;

	@Column(name = "total_weightage")
	public BigDecimal totalWeightage;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "application_verification_status",columnDefinition="ENUM('VE','NV','NE')")
	public ApplicationVerificationStatus applicationVerificationStatus;
	
	@Column(name="application_verified_date")
	public LocalDate applicationVerifiedDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="application_verified_user_id")
	public ErpUsersDBO applicationVerifiedUserId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="student_appln_verification_remarks_id")
	public StudentApplnVerificationRemarksDBO studentApplnVerificationsId;
	
	@Column(name="application_verification_addtl_remarks")
	public String applicationVerificationAddtlRemarks;
	
	@Column(name = "selection_status_remarks")
	public String selectionStatusRemarks;

	@Column(name = "total_part_time_previous_experience_months")
	public Integer  totalPartTimePreviousExperienceMonths;

	@Column(name = "total_part_time_previous_experience_years")
	public Integer  totalPartTimePreviousExperienceYears;

	@Column(name = "total_previous_experience_months")
	public Integer  totalPreviousExperienceMonths;

	@Column(name = "total_previous_experience_years")
	public Integer  totalPreviousExperienceYears;

	@Column(name = "created_users_id", updatable = false)
	public Integer  createdUsersId;

	@Column(name = "modified_users_id")
	public Integer  modifiedUsersId;

	@Column(name = "record_status")
	public Character recordStatus;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "mode_of_study",columnDefinition="ENUM('PART_TIME','FULL_TIME','AIDED','UNAIDED'")
	private ModeOfStudy modeOfStudy;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="selected_duration_detail_id")
	public AcaDurationDetailDBO selectedDurationDetailDBO;

	@OneToMany(mappedBy = "studentApplnEntriesDBO", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<StudentApplnPreferenceDBO> studentApplnPreferenceDBOS = new HashSet<>();

	@OneToMany(mappedBy = "studentApplnEntriesDBO", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<StudentApplnSelectionProcessDatesDBO> studentApplnSelectionProcessDatesDBOS = new HashSet<>();

	@OneToMany(mappedBy = "studentApplnEntriesDBO", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<AdmSelectionProcessDBO> admSelectionProcessDBOS = new HashSet<>();

	@OneToOne(mappedBy = "studentApplnEntriesDBO", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public StudentApplnPrerequisiteDBO studentApplnPrerequisiteDBO;

    @OneToMany(mappedBy = "studentApplnEntriesDBO", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	public Set<StudentEducationalDetailsDBO> studentEducationalDetailsDBOS = new HashSet<>();
    
    @OneToMany(mappedBy = "studentApplnEntriesDBO", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	public Set<StudentApplnSelectionProcessRescheduleDBO> studentApplnSelectionProcessRescheduleDBOs = new HashSet<>();  
    
    @OneToMany(mappedBy = "studentApplnEntriesDBO",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<StudentExtraCurricularDetailsDBO> studentExtraCurricularDetailsDBOS = new HashSet<>();
    
    @OneToMany(mappedBy = "studentApplnEntriesDBO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Set<StudentWorkExperienceDBO> studentWorkExperienceDBOS = new HashSet<>();
    
    @OneToMany(mappedBy = "studentApplnEntriesDBO", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    public Set<StudentDBO> StudentDBOS = new HashSet<>();
     
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "studentApplnEntriesDBO", cascade = CascadeType.ALL)
    public Set<AccFeeDemandDBO> accFeeDemandDBOSet;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "studentApplnEntriesDBO", cascade = CascadeType.ALL)
    public Set<HostelAdmissionsDBO> hostelAdmissionsDBOSet = new HashSet<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="adm_admission_schedule_time_id")
    private AdmAdmissionScheduleTimeDBO admAdmissionScheduleTimeDBO;
    
	@Column(name = "admission_start_datetime")
	public LocalDateTime admissionStartDatetime;
	
	@Column(name = "admission_final_datetime")
	public LocalDateTime admissionFinalDatetime;

}
