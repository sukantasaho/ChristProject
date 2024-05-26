package com.christ.erp.services.dbobjects.admission.settings;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.Table;

import com.christ.erp.services.dbobjects.account.settings.AccFeeHeadsDBO;
import com.christ.erp.services.dbobjects.admission.applicationprocess.ModeOfStudy;
import com.christ.erp.services.dbobjects.admission.applicationprocess.ProgrammeMode;
import com.christ.erp.services.dbobjects.common.ErpAcademicYearDBO;
import com.christ.erp.services.dbobjects.common.ErpCurrencyDBO;
import com.christ.erp.services.dbobjects.common.ErpProgrammeDBO;
import com.christ.erp.services.dbobjects.employee.settings.ErpTemplateDBO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@SuppressWarnings("serial")
@Entity
@Table(name = "adm_programme_settings")
public class AdmProgrammeSettingsDBO implements Serializable{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_programme_settings_id")
    public int id;

	@ManyToOne
    @JoinColumn(name="erp_programme_id")
    public ErpProgrammeDBO erpProgrammeDBO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="adm_admission_type_id")
    public AdmAdmissionTypeDBO admAdmissionTypeDBO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="adm_batch_year_id")
    public ErpAcademicYearDBO admBatchYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="erp_academic_year_id")
    public ErpAcademicYearDBO erpAcademicYearDBO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="adm_intake_batch_id")
    public AdmIntakeBatchDBO admIntakeBatchDBO;
    
//	@ManyToOne
//  @JoinColumn(name="erp_currency_id")
//  public ErpCurrencyDBO erpCurrencyDBO;
	
//	@Column(name = "fee_for_indian_applicants", precision=10, scale=2)
//	public BigDecimal feeForIndianApplicants;
	
//	@Column(name = "fee_for_international_applicants", precision=10, scale=2)
//	public BigDecimal feeForInternationalApplicants;
	
	@Column(name = "is_research_topic_required")
	public boolean isResearchTopicRequired;
	
	@Column(name = "is_second_language")
	public boolean isSecondLanguage;
	    
	@Column(name = "is_work_experience_required")
	public boolean isWorExperienceRequired;
	
	@Column(name = "is_work_experience_mandatory")
	public boolean isWorkExperienceMandatory;

    @Column(name = "is_having_other_programme_preferences")
    public Boolean isHavingOtherProgrammePreferences;
	    
	@Column(name = "min_no_of_months_experience")
	public Integer minNoOfMonthsExperience;
	
	@Column(name = "no_of_preference_required")
	public Integer noOfPreferenceRequired;	

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_of_application",columnDefinition="ENUM('ONLINE','OFFLINE','BOTH')")
    public ApplicationMode applicationMode;
    
    @ManyToOne
    @JoinColumn(name = "erp_terms_template_id")
	public ErpTemplateDBO erpTemplateDBO;
	
	@Column(name = "preference_option")
	public char preferenceOption;

    @ManyToOne
    @JoinColumn(name = "erp_appln_print_template_id")
    public ErpTemplateDBO erpApplnPrintTemplate;
    
    @ManyToOne
    @JoinColumn(name = "acc_fee_heads_id")
    public AccFeeHeadsDBO accFeeHeadsDBO;
    
//    @Column(name = "online_payment_modes")
//	public String onlinePaymentModes;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_of_study",columnDefinition="ENUM('PART_TIME','FULL_TIME','BOTH'")
    private ProgrammeMode modeOfStudy;

    @Column(name = "is_programme_mode_displayed")
    public Boolean isProgrammeModeDisplayed;

    @Column(name = "created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public char recordStatus;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "admProgrammeSettingsDBO")
	public Set<AdmProgrammePreferenceSettingsDBO> admProgrammePreferenceSettingsSetDbos = new HashSet<>();
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "admProgrammeSettingsDBO")
    public Set<AdmProgrammeQualificationSettingsDBO> admProgrammeQualificationSettingsSetDbos = new HashSet<>();
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "admProgrammeSettingsDBO")
    public Set<AdmProgrammeDocumentSettingsDBO> admProgrammeDocumentSettingSetDbos = new HashSet<>();
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "admProgrammeSettingsDBO")
    public Set<AdmProgrammeFeePaymentModeDBO> admProgrammeFeePaymentModeDBOSet = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "admProgrammeSettingsDBO")
    public Set<AdmProgrammeBatchDBO> admProgrammeBatchDBOSet = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "admProgrammeSettingsDBO")
    public Set<AdmProgrammeBatchPreferencesDBO> admProgrammeBatchPreferencesDBOSet = new HashSet<>();
}


