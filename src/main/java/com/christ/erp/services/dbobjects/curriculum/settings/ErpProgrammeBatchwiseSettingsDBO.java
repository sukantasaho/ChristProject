package com.christ.erp.services.dbobjects.curriculum.settings;

import java.io.Serializable;
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
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.common.AcaSessionTypeDBO;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeAddOnCoursesDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeDBO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "erp_programme_batchwise_settings")
@Getter 
@Setter
public class ErpProgrammeBatchwiseSettingsDBO implements Serializable {
	private static final long serialVersionUID = 3722455250633061387L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "erp_programme_batchwise_settings_id")
	private int id;
	
	@Column(name = "is_scond_language")
	private Boolean isSecondLanguage;
	
	@Column(name = "is_specialization")
	private Boolean isSpecialization;
	
	@Column(name = "program_admission_eligibility")
	private String programAdmissionEligibility;
	
	@Column(name = "program_objectives")
	private String programObjectives;
	
	@Column(name = "introduction_to_the_program")
	private String introductionToTheProgram;
	
	@Column(name = "total_no_of_scheme")
	private Integer totalNoOfScheme;
	
	@Column(name = "number_of_batches")
	private Integer numberOfBatches;
	
//	@Column(name = "scheme_duration")
//	private Integer schemeDuration;
	
	@Column(name = "min_no_of_scheme_for_graduation")
	private Integer minNoOfSchemeForGraduation;
	
	@Column(name = "min_no_of_credits_for_graduation")
	private Integer minNoOfCreditsForGraduation;
	
	@Column(name = "total_no_of_years")
	private String totalNoOfYears;
	
	@Column(name = "min_core_courses")
	private Integer minCoreCourses;
	
	@Column(name = "min_elective_courses")
	private Integer minElectiveCourses;
	
	@Column(name = "is_dissertation_required")
	private Boolean isDissertationRequired;
	
	@Column(name = "max_elective_from_same_disciplinary")
	private Integer maxElectiveFromSameDisciplinary;
	
	@Column(name = "max_elective_from_other_disciplinary")
	private Integer maxElectiveFromOtherDisciplinary;
	
	@Column(name = "created_users_id",updatable=false)
	private Integer createdUsersId;
	
	@Column(name = "modified_users_id")
	private Integer modifiedUsersId;
	
	@Column(name = "record_status")
	private char recordStatus;
	
	@ManyToOne
	@JoinColumn(name= "aca_session_type_id")
	private AcaSessionTypeDBO acaSessionTypeDBO;
	
	@ManyToOne
	@JoinColumn(name = "erp_programme_id")
    private ErpProgrammeDBO erpProgrammeDBO;
	
	@ManyToOne
	@JoinColumn(name = "batch_year_id")
	private ErpAcademicYearDBO  erpAcademicYearDBO;
	
	@OneToMany(mappedBy = "erpProgrammeBatchwiseSettingsDBO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ErpProgrammeSecondLanguageSessionDBO> erpProgrammeSecondLanguageSessionDBOSet;
	
	@OneToMany(mappedBy = "erpProgrammeBatchwiseSettingsDBO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ErpProgrammeSpecializationMappingDBO> erpProgrammeSpecializationMappingDBOSet;
	
	@OneToMany(mappedBy = "erpProgrammeBatchwiseSettingsDBO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ErpProgrammeSpecializationSessionMappingDBO> erpProgrammeSpecializationSessionMappingDBOSet;
	
	@OneToMany(mappedBy = "erpProgrammeBatchwiseSettingsDBO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ErpProgrammeAccreditationMappingDBO> erpProgrammeAccreditationMappingDBOSet;
	
	@OneToMany(mappedBy = "erpProgrammeBatchwiseSettingsDBO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ErpCampusProgrammeMappingDetailsDBO> erpCampusProgrammeMappingDetailsDBOSet;
	
	@OneToMany(mappedBy = "erpProgrammeBatchwiseSettingsDBO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ObeProgrammeOutcomeDBO> obeProgrammeOutcomeDBOSet;
	
	@OneToMany(mappedBy = "erpProgrammeBatchwiseSettingsDBO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<AcaBatchDBO> acaBatchDBOSet;	
	
	@OneToMany(mappedBy = "erpProgrammeBatchwiseSettingsDBO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ErpProgrammeAddOnCoursesDBO> erpProgrammeAddOnCoursesDBOSet;	
}
