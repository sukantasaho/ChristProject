package com.christ.erp.services.dbobjects.admission.settings;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
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
import javax.persistence.Table;

@Entity
@Table(name = "adm_programme_qualification_settings")
@Setter
@Getter
public class AdmProgrammeQualificationSettingsDBO {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="adm_programme_qualification_settings_id")
    public int id;
    
    @ManyToOne
    @JoinColumn(name="adm_programme_settings_id")
    public AdmProgrammeSettingsDBO admProgrammeSettingsDBO;
    
    @Column(name = "qualification_order")
    public Integer qualificationOrder;
    
    @ManyToOne
    @JoinColumn(name="adm_qualification_list_id")
    public AdmQualificationListDBO admQualificationListDBO;
    
    @Column(name = "is_qualification_level_mandatory")
    public boolean isQualificationLevelMandatory;
    
    @Column(name = "is_upload_required")
    public boolean isUploadRequired;
    
    @Column(name = "is_upload_mandatory")
    public boolean isUploadMandatory;
    
    @Column(name = "university_or_board")
    public String universityOrBoard;
    
    @Column(name = "is_examname_required")
    public boolean isExamnameRequired;

    @Column(name = "is_exam_regi_numb_required")
    public Boolean isExamRegiNumbRequired;

    @Column(name = "is_subject_eligibility_required")
    public boolean isSubjectEligibilityRequired;
    
    @Column(name = "min_subjects_for_eligibility")
    public Integer minSubjectsForEligibility;
    
    @Column(name = "aggregate_subjects_percentage")
    public BigDecimal aggregateSubjectsPercentage;
    
    @Column(name = "marks_entry_type")
    public String marksEntryType;

    @Column(name = "backlog_required")
    public boolean backlogRequired;
    
    @Column(name = "created_users_id",updatable=false)
    public Integer createdUsersId;

    @Column(name = "modified_users_id")
    public Integer modifiedUsersId;

    @Column(name = "record_status")
    public char recordStatus;
    
    @OneToMany(fetch =FetchType.LAZY,mappedBy = "admProgrammeQualificationSettingsDBO", cascade = CascadeType.ALL)
    public Set<AdmProgrammeQualificationSubjectEligibilityDBO> admProgrammeQualificationSubjectEligibilityDBO =  new HashSet<>();
}
